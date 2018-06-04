package Compress;

import java.math.BigDecimal;
import java.math.MathContext;

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
		calculateCumFreq(cum_freq, probability);
		String key = encode (cum_freq,probability,freq,input_names[0]);
		output_names[0] = key;
	}
	

	@Override
	public void Decompress(String[] input_names, String[] output_names) {
		//read the values
		int freq [] = new int [10];
		for (int i = 0; i < 10; i ++)
		{
			freq[i] = Integer.parseInt(input_names[0].substring(32*i, 32*(i+1)),2);
		}
		int total = 0;
		for (int i = 0; i < 10; i++)
			total += freq[i];
		String str_scale = input_names[0].substring(320, 352);
		String str = input_names[0].substring(352);
		int scale = Integer.parseInt(str_scale, 2);
		
		MathContext mc = new MathContext(scale);
		BigDecimal key = reverseString(str, mc);
		System.out.println(key.toString());
		
		BigDecimal probability [] = new BigDecimal[10];
		BigDecimal cum_freq [] = new BigDecimal [10];
		calculateProbability(freq, total, probability);
		calculateCumFreq(cum_freq, probability);
		System.out.println(decode(key,probability,cum_freq,scale));
	}
	private String decode (BigDecimal key, BigDecimal [] P, BigDecimal [] C,int length)
	{
		StringBuilder ans = new StringBuilder();
		BigDecimal high = BigDecimal.ONE;
		BigDecimal low = BigDecimal.ZERO;
		BigDecimal w, lowerBound, higherBound;
		int j;
		
		for (int i = 0; i < length; i++)
		{
			w = high.subtract(low);
			for (j = 0; j < 10; j ++)
			{
				lowerBound = w.multiply(C[j]);
				lowerBound = lowerBound.add(low);
				//lowerBound = l + w*c[j]
				
				higherBound = C[j].add(P[j]);
				higherBound = higherBound.multiply(w);
				higherBound = higherBound.add(low);
				//higherBound = l + w*(c[j]+p[j])
				
				if (key.compareTo(lowerBound) >= 0 && key.compareTo(higherBound) < 0)
				{
					ans.append(j);
					//ans.insert(0,j);
					low = lowerBound;
					//l = l + w*c[j]
					high = low.add(w.multiply(P[j]));
					//high = low + w*p[j]
					break;
				}
			}
		}
		
		
		
		return ans.toString();
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
	private void calculateCumFreq(BigDecimal[] cum_freq, BigDecimal[] probability)
	{
		cum_freq[0] = BigDecimal.valueOf(0);
		//calculate cum_freq
		for (int i = 1; i < 10; i ++)
		{
			BigDecimal temp = new BigDecimal(cum_freq[i-1].toString());
			temp =temp.add(probability[i-1]);
			cum_freq[i] = temp;
			//cum_freq[i] = BigDecimal.cum_freq[i-1] + probability[i-1];
		}
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
	public String encode(BigDecimal [] C, BigDecimal [] P, int[] freq,String input)
	{
		//Initializing
		BigDecimal key = BigDecimal.ZERO;
		BigDecimal w = BigDecimal.ONE;
		BigDecimal high = w;
		BigDecimal low = BigDecimal.valueOf(0);
		BigDecimal temp;
		BigDecimal mult;
		int i;
		for (i = 0;i <input.length(); i++)
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
			//R = L + w*P(x)
		} 
		MathContext mc = new MathContext(i);
		temp = high.add(low);
		key = temp;
		key = key.divide(BigDecimal.valueOf(2));
		key = key.setScale(mc.getPrecision(), BigDecimal.ROUND_HALF_UP);
		System.out.println(key.toString());
		
		StringBuilder ans = new StringBuilder();
		ans = codeApeerances(ans,freq);
		
		StringBuilder str_scale = new StringBuilder(Integer.toBinaryString(key.scale()));
		while (str_scale.length() != 32)//ans should be 4 bytes exactly
			str_scale.insert(0, "0");
		ans.append(str_scale);
		
		//add the binary key to the end of the string
		ans.append(keyToBinaryString(key));
		return ans.toString();
	}
	
	//Converts the key to binary string
	private String keyToBinaryString(BigDecimal key)
	{
	//Initializing variables
		int limit = (int) Math.pow(2, key.scale()+1);
		StringBuilder val = new StringBuilder();
		BigDecimal zero = new BigDecimal("0");
		BigDecimal one = new BigDecimal("1");
		BigDecimal two = new BigDecimal("2.0");
		while (key.compareTo(zero) > 0)
		{

			if (val.length() >= 20000)//this should be our accuracy limit
				return val.toString();

			BigDecimal r = key.multiply(two);
			if (r.compareTo(one) >= 0)   // If the ones-place digit >= 1
			{
				val.append("1");  // Concat a "1" to the end of the result string (val)
				key = r.subtract(one); // Remove the 1 from the current fraction (n)
			}
			else
			{
				val.append("0");  // If the ones-place digit == 0 concat a "0" to the end of the result string (val)
				key = r;  // Set the current fraction (n) to the new fraction
			}
		}
		return val.toString();
	}
	//code the freq to the string
	private StringBuilder codeApeerances(StringBuilder ans, int[] freq) 
	{
		for (int i = 0; i < 10; i++)
		{
			String temp = Integer.toBinaryString(freq[i]);
			while (temp.length() < 32)
				temp = "0" + temp;
			ans.append(temp);
		}
		return ans;
	}

	//reverse binary string to BigDecimal
	private BigDecimal reverseString(String str,MathContext scale)
	{
		BigDecimal answer = new BigDecimal(0);
		for (int i = 0; i < str.length(); i++)
		{
			BigDecimal temp = new BigDecimal(Double.toString((double)(str.charAt(i)-48)*Math.pow(0.5,i+1)));

			//temp = temp.setScale(mc.getPrecision(), BigDecimal.ROUND_HALF_UP);
			answer = answer.add(temp);			
		}
		answer = answer.setScale(scale.getPrecision(), BigDecimal.ROUND_HALF_UP);

		return answer;

	}


}
