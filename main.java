package Compress;

public class main {

	public static void main(String[] args) 
	{
		Arithmetic_Compressor ac = new Arithmetic_Compressor();
		String [] input = new String [1];
		input[0] = "C:\\Users\\sirpa\\Documents\\eclipse-projects\\Arithmetic\\src\\Compress\\Arithmetic-compress\\input.txt";
		String [] output = new String [1];
		output[0] = "C:\\Users\\sirpa\\Documents\\eclipse-projects\\Arithmetic\\src\\Compress\\Arithmetic-compress\\output.txt";
		ac.Compress(input, output);
	}

}
