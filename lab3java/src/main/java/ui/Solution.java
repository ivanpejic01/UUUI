package ui;

import java.io.File;
import java.io.FileNotFoundException;


public class Solution {

	public static String putanjaDatotekaUcenje = new String();
	public static String putanjaDatotekaProvjera = new String();
	public static Integer parametarDubina;
	public static File datotekaUcenje;
	public static File datotekaProvjera;

	public static ID3 model = new ID3();
	
	public static void main(String[] args) throws FileNotFoundException {
	
		putanjaDatotekaUcenje = args[0];
		putanjaDatotekaProvjera = args[1];
		//https://www.freecodecamp.org/news/java-string-to-int-how-to-convert-a-string-to-an-integer/
		if (args.length == 3) {
			parametarDubina = Integer.parseInt(args[2]);
		}
		else {
			parametarDubina = -1;
		}
		System.out.println("Ucenje " + putanjaDatotekaUcenje + " provjera: " + putanjaDatotekaProvjera);
		
		datotekaUcenje = new File(putanjaDatotekaUcenje);
		datotekaProvjera = new File(putanjaDatotekaProvjera);
		
		model.fit(datotekaUcenje, parametarDubina);
		model.predict(datotekaProvjera);
		
		
		
		
		
	}

	
}
