package Compress;

import java.io.FileInputStream;
import java.io.IOException;

public class Arithmetic_Compressor implements Compressor
{

	@Override
	public void Compress(String[] input_names, String[] output_names)
	{
		//initializing variables
		FileInputStream fis = null;
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

	@Override
	public void Decompress(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub

	}

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
