package Compress;

public class main {

	public static void main(String[] args) 
	{
		Arithmetic_Compressor ac = new Arithmetic_Compressor();
		String [] input = new String [1];
		input[0] = "1010276584974512345612345";
		String [] output = new String [1];
		String [] again = new String [1];
		ac.Compress(input, output);
		ac.Decompress(output, again);
	}

}
