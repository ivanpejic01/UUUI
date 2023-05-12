package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Solution {

	public static String putanjaDatotekaUcenje = new String();
	public static String putanjaDatotekaProvjera = new String();
	public static File datotekaUcenje;
	public static File datotekaProvjera;

	public static ID3 model = new ID3();
	
	public static void main(String[] args) throws FileNotFoundException {
	
		putanjaDatotekaUcenje = args[0];
		putanjaDatotekaProvjera = args[1];
		System.out.println("Ucenje " + putanjaDatotekaUcenje + " provjera: " + putanjaDatotekaProvjera);
		
		datotekaUcenje = new File(putanjaDatotekaUcenje);
		datotekaProvjera = new File(putanjaDatotekaProvjera);
		
		model.fit(datotekaUcenje);
		
		
		
		
	}

	
}
