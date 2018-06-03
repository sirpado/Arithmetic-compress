package Compress;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Arithmetic_Compressor implements Compressor
{


	@Override
	public void Compress(String[] input_names, String[] output_names)
	{
		//initializing variables

		int [] freq = new int [10]; 
		BigDecimal[] probability = new BigDecimal[10];
		BigDecimal cum_freq [] = new BigDecimal [10];

		for (int i = 0; i < input_names[0].length(); i++)
		{
			freq [input_names[0].charAt(i)- 48]++;
		}
		calculateProbability (freq,input_names[0].length(),probability);
		cum_freq[0] = BigDecimal.valueOf(0);
		for (int i = 1; i < 10; i ++)
		{
			BigDecimal temp = new BigDecimal(cum_freq[i-1].toString());
			temp =temp.add(probability[i-1]);
			cum_freq[i] = temp;
			//cum_freq[i] = BigDecimal.cum_freq[i-1] + probability[i-1];
		}

		String key = encode (cum_freq,probability,input_names[0]);
		output_names[0] = key;
	}
	//calculate the probabilities for each possible value
	private void calculateProbability(int [] arr, int total, BigDecimal[] probability)
	{
		for (int i = 0; i < 10; i++)
		{
			double temp = (double)arr[i]/(double)total;
			probability[i] = new BigDecimal(Double.toString(temp));
			//probability[i] =arr[i]/total;
		}
	}

	private String keyToBinaryString(BigDecimal key)
	{
		//System.out.println("scale:" + key.scale());
		int limit = (int) Math.pow(2, key.scale()+1);
		StringBuilder val = new StringBuilder();
		BigDecimal zero = new BigDecimal("0");
		BigDecimal one = new BigDecimal("1");
		BigDecimal two = new BigDecimal("2.0");
		while (key.compareTo(zero) != 0)
		{
			
			if (val.length() >= limit)
				return val.toString();
			
			BigDecimal r = key.multiply(two);
			if (r.compareTo(one) >= 0)
			{
				val.append("1");
				key = r.subtract(one);
			}
			else
			{
				val.append("0");
				key = r;
			}
		}
		return val.toString();
	}
	/*
	private static String floatToBinaryString( double n ) {
		String val = "";    // Setting up string for result
		while ( n > 0 )
		{     // While the fraction is greater than zero (not equal or less than zero)
			double r = n * 2;   // Multiply current fraction (n) by 2
			if( r >= 1 )
			{      // If the ones-place digit >= 1
				val += "1";       // Concat a "1" to the end of the result string (val)
				n = r - 1;        // Remove the 1 from the current fraction (n)
			}
			else
			{              // If the ones-place digit == 0
				val += "0";       // Concat a "0" to the end of the result string (val)
				n = r;            // Set the current fraction (n) to the new fraction
			}
		}
		System.out.println("val length: "+val.length());
		return val;          // return the string result with all appended binary values
	}
*/
	
	public String encode(BigDecimal [] C, BigDecimal [] P, String input)
	{
		//Initializing
		BigDecimal key = BigDecimal.ZERO;
		BigDecimal w = BigDecimal.ONE;
		BigDecimal high = w;
		BigDecimal low = BigDecimal.valueOf(0);
		BigDecimal temp;
		BigDecimal mult;

		for (int i = 0;i <input.length(); i++)
		{
			//calculation itself
			w = high;
			w = w.subtract(low);
			//w = high - low

			mult = w;
			mult = mult.multiply(C[input.charAt(i)-48]);
			low = low.add(mult);
			//L = L + w*C(x)

			mult = w;
			temp = low;
			mult = mult.multiply(P[input.charAt(i)-48]);
			high = temp.add(mult);
			//high = temp;
			//R = L + w*P(x)

		} 
		temp = high;
		temp.add(low);
		key = temp;
		key = key.divide(BigDecimal.valueOf(2));
		
		StringBuilder ans = new StringBuilder(Integer.toBinaryString(key.scale()));
		while (ans.length() != 32)//ans should be 4 bytes exactly
			ans.insert(0, "0");
		ans.append(keyToBinaryString(key));
		return ans.toString();

	}
	//reverse binary string to float
	private BigDecimal reverseString(String str)
	{
		BigDecimal answer = BigDecimal.ZERO;
		for (int i = 0; i < str.length(); i++)
		{
			BigDecimal temp = new BigDecimal( Float.toString((float) ((str.charAt(i)-48) * Math.pow(0.5,i+1))));
			answer = answer.add(temp);
		}
		return answer;

	}
	public void decode(float [] P ,float [] C, float key, StringBuilder sb,int size)
	{
		float high = 0;
		float low = 1;
		//float range;
		int symbol = 9;
		float [] range= new float [10];

		//find the first letter
		for (int i = 0; i < 9; i ++)
			if (key >= P[i] &&  key < P[i+1] )
			{
				symbol = i;
				break;
			}
		sb.append((char)symbol+48);
		while (size > 0)
		{
			for (int i = 0; i < 10; i++)
				range[i] = range[i-1] + P[i-1];
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
