package Compress;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Set;

public class Arithmetic_Compressor implements Compressor
{
	ArrayList<String> keys = new ArrayList <String>();
	ArrayList<Integer> sizes = new ArrayList <Integer>();
	
	@Override
	public void Compress(String[] input_names, String[] output_names)
	{
		//initializing variables
		FileInputStream fis;
		FileOutputStream fos;
		int [] freq = new int [10]; 
		float[] probability = new float [10];

		try 
		{
			//reading from file to byte array
			fis = new FileInputStream(input_names[0]);
			byte [] arr = fis.readAllBytes();
			//counting the values in the byte array o(n)
			for (int i = 0; i < arr.length; i++)
			{
				freq [arr[i]- 48]++;
			}
			calculateProbability (freq,arr.length,probability);
			//so far
			float cum_freq [] = new float [10];
			for (int i = 1; i < 10; i ++)
				cum_freq[i] = cum_freq[i-1] + probability[i-1];
			
			encode (cum_freq,probability,arr);
			
			//write to file
			fos = new FileOutputStream(output_names[0]);
			String bina = "";
			fos.write(sizes.size());
			for (int i = 0; i < sizes.size(); i++)
			{
				fos.write(sizes.get(i));
				for(int j=0;j<keys.get(i).length();j=j+8)//TODO
				{
					bina=keys.get(i).substring(i, i+8);
					int bin=Integer.parseInt(bina,2);

					byte[] barr= {(byte)bin};
					fos.write(barr);
				}
			}
			System.out.println("Stop!!!!!!!!");

		}
		catch (IOException e) {  e.printStackTrace();}

	}

	//calculate the probabilities for each possible value
	private void calculateProbability(int [] arr, int total, float [] probability)
	{
		for (int i = 0; i < 10; i++)
			probability[i] = (float)((float)arr[i]/(float)total);
	}
	
	private static String floatToBinaryString( double n ) {
	    String val = "";    // Setting up string for result
	    while ( n > 0 ) {     // While the fraction is greater than zero (not equal or less than zero)
	        double r = n * 2;   // Multiply current fraction (n) by 2
	        if( r >= 1 ) {      // If the ones-place digit >= 1
	            val += "1";       // Concat a "1" to the end of the result string (val)
	            n = r - 1;        // Remove the 1 from the current fraction (n)
	        }else{              // If the ones-place digit == 0
	            val += "0";       // Concat a "0" to the end of the result string (val)
	            n = r;            // Set the current fraction (n) to the new fraction
	        }
	    }
	    return val;          // return the string result with all appended binary values
	}
	
	//encoding the file with arithmetic code. each block is 8 bytes
	public void encode(float [] C, float [] P, byte [] arr)
	{
		//Initializing
		Float key = (float) 0;
		float w = 1;
		float high = 1;
		float low = 0;
		int size = 0;
		int i = 0;

		do 
		{
			//end of every block, write down values and reset it
			if (size %8 == 0 && i != 0)
			{
				keys.add(floatToBinaryString(key));
				sizes.add(size);
				size = 0;
				System.out.println(key.toString());
				key = (float) 0;
				high = 1;
				low = 0;
				System.out.println(key.toString());
			}
			//calculation itself
				w = high - low;	
				low += (float)(w*C[arr[i]-48]);
				high = low +(float)w*P[arr[i]-48];
				key = (float)((high+low)/2);
				size ++;
				i++;
		} while ( i < arr.length);
		//the end of the file
		if (size != 0)
		{
			keys.add(floatToBinaryString(key));
			sizes.add(size);
			System.out.println(key);
		}

	}
	@Override
	public void Decompress(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub

	}
	//public void decode()
	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

}
