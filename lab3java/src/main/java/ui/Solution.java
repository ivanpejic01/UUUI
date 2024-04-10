package ui;

import java.io.File;
import java.io.FileNotFoundException;

//glavna klasa cija metoda main sluzi za pozivanje metoda fit i predict iz klase ID3
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
		if (args.length == 3) {
			//https://www.freecodecamp.org/news/java-string-to-int-how-to-convert-a-string-to-an-integer/
			parametarDubina = Integer.parseInt(args[2]);
		}
		else {
			parametarDubina = -1;
		}
		
		datotekaUcenje = new File(putanjaDatotekaUcenje);
		datotekaProvjera = new File(putanjaDatotekaProvjera);
		
		//u mojoj implementaciji obje metode su void
		model.fit(datotekaUcenje, parametarDubina);
		model.predict(datotekaProvjera);
			
	}

	
}
