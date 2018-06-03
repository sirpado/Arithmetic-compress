package Compress;

public class main {

	public static void main(String[] args) 
	{
		Arithmetic_Compressor ac = new Arithmetic_Compressor();
		String [] input = new String [1];
		input[0] = "01012";
		String [] output = new String [1];
		ac.Compress(input, output);
	}

}
