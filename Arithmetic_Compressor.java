package Compress;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class Arithmetic_Compressor implements Compressor
{

	@Override
	public void Compress(String[] input_names, String[] output_names)
	{

		for (int i = 0; i < input_names.length; i++)
		{
			//initializing variables
			int [] freq = new int [10]; 
			BigDecimal[] probability = new BigDecimal[10];
			BigDecimal cum_freq [] = new BigDecimal [10];

			for (int j = 0; j < input_names[i].length(); j++)
			{
				freq [input_names[i].charAt(j)- 48]++;
			}
			calculateProbability (freq,input_names[i].length(),probability);
			calculateCumFreq(cum_freq, probability);

			StringBuilder key = new StringBuilder();
			key = codeApeerances(key, freq);
			key.append(Integer.toBinaryString(input_names[i].length()));
			while (key.length()%32 != 0)
				key.insert(320, 0);

			key.append(Encode(cum_freq,probability,freq,input_names[i]));
			output_names[i] = key.toString();
		}
	}

	@Override
	public void Decompress(String[] input_names, String[] output_names) {
		for (int i = 0; i < input_names.length; i++)
		{
			//read the values
			int freq [] = new int [10];
			for (int j = 0; j < 10; j ++)
			{
				freq[j] = Integer.parseInt(input_names[i].substring(32*j, 32*(j+1)),2);
			}
			int total = 0;
			for (int j = 0; j < 10; j++)
				total += freq[j];
			String str_scale = input_names[i].substring(320, 352);
			String str = input_names[i].substring(352);
			int size = Integer.parseInt(str_scale, 2);

			BigDecimal probability [] = new BigDecimal[10];
			BigDecimal cum_freq [] = new BigDecimal [10];

			calculateProbability(freq, total, probability);
			calculateCumFreq(cum_freq, probability);

			System.out.println(Decode(str, probability, cum_freq, size));
			output_names[i] = Decode(str, probability, cum_freq, size);
		}
	}
	private String Decode (String key, BigDecimal [] P, BigDecimal [] C,int length)
	{
		StringBuilder ans = new StringBuilder();
		BigDecimal high = BigDecimal.ONE;
		BigDecimal low = BigDecimal.ZERO;

		BigDecimal w, lowerBound, higherBound;
		int j;

		for (int i = 0; i < length; i++)
		{
			BigDecimal bin = reverseString(key);
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

				if (bin.compareTo(lowerBound) >= 0 && bin.compareTo(higherBound) <0)
				{
					ans.append(j);
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
	public byte[] CompressWithArray(String[] input_names, String[] output_names) 
	{
		String half_answer[] = {""};
		byte [] arr = new byte [half_answer.length/8];
		Compress(input_names, half_answer); // get the binary string 
		for (int i = 0; i < half_answer[0].length(); i = i+8)
		{
			arr [i/8] = (byte) Integer.parseInt(half_answer[0].substring(i, i+8),2);
		}
		return arr;
		
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

			probability[i] = new BigDecimal(arr[i]);
			BigDecimal temp = new BigDecimal(total);
			probability[i] = probability[i].divide(temp,2,RoundingMode.HALF_EVEN);
		}
	}

	private String Encode(BigDecimal [] C, BigDecimal [] P, int[] freq, String input)
	{
		//Initializing
		BigDecimal quarter = new BigDecimal("0.25");
		BigDecimal half = new BigDecimal("0.5");
		BigDecimal threequarters = new BigDecimal("0.75");
		BigDecimal w = BigDecimal.ONE;
		BigDecimal high = w;
		BigDecimal low = BigDecimal.valueOf(0);
		BigDecimal temp;
		BigDecimal mult;
		StringBuilder scale = new StringBuilder();
		int i,counter=0;
		for (i = 0;i <input.length(); i++)
		{
			//calculation itself 
			w = high.subtract(low);
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
		boolean flag = true;

		//scaling
		while (flag)
		{
			if ( low.compareTo(BigDecimal.ZERO) >= 0 && high.compareTo(half) < 0) // lower half
			{
				low = low.multiply(BigDecimal.valueOf(2));
				high = high.multiply(BigDecimal.valueOf(2));
				scale.append(0);
			}
			else if (low.compareTo(half) >= 0 && high.compareTo(BigDecimal.ONE) <0) // upper half
			{
				scale.append(1);
				low = low.subtract(half);
				low = low.add(low); //(l = 2 *(l-0.5))
				high = high.subtract(half);
				high = high.add(high);// h = (h-0.5)
			}
			else if (low.compareTo(quarter) >= 0 && high.compareTo(threequarters) < 0 && low.compareTo(half) <= 0 && high.compareTo(half) >= 0) // middle half
			{
				counter++;
				low = low.subtract(quarter);
				low = low.add(low);// l = 2(l-0.25)
				high = high.subtract(quarter);
				high = high.add(high);//h = 2(h-0.25)
			}
			else if (low.compareTo(half) <= 0 && high.compareTo(threequarters) >= 0)
			{
				scale.append(1);
				counter++;
				for ( i = 0; i < counter; i++)
					scale.append(0);
				flag = false;
			}
			else if (low.compareTo(quarter) <= 0 && high.compareTo(half) >= 0)
			{
				scale.append(0);
				counter++;
				for ( i = 0; i < counter; i++)
					scale.append(1);
				flag = false;
			}
		} 
		//key = high.add(low);
		//key = key.divide(BigDecimal.valueOf(2));
		//System.out.println(key.toString());

		//int limit = (int)(Math.log(1/(high.subtract(low).doubleValue()))/Math.log(2));
		//scale.append(keyToBinaryString(key,limit));
		return scale.toString();
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
	private BigDecimal reverseString(String str)
	{
		BigDecimal answer = new BigDecimal(0);
		for (int i = 0; i < str.length(); i++)
		{
			BigDecimal temp = new BigDecimal(Double.toString((double)(str.charAt(i)-48)*Math.pow(0.5,i+1)));

			answer = answer.add(temp);			
		}
		return answer;

	}


}
