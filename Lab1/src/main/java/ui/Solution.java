package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Comparator;

public class Solution {

	public static String algoritam = new String();
	public static String putanjaProstorStanja = new String();
	public static String putanjaHeuristika = new String();
	public static String zastavica = new String();
	public static List<String> argumenti = new ArrayList<>();
	public static File prostorStanja;
	public static String pocetnoStanje = new String();
	public static List<String> konacnaStanja = new ArrayList<>();
	public static Map<String, Map<String, Double>> susjedniCvorovi = new TreeMap<>();
	public static File heuristika;
	public static Map<String, Double> mapaHeuristika = new TreeMap<>();
	

	public static void main(String[] args) throws FileNotFoundException {
		
		argumenti = Arrays.asList(args);

		/*procitaj algoritam s ulaza*/
		if (argumenti.indexOf("--alg") > -1) {
		algoritam = argumenti.get(argumenti.indexOf("--alg") + 1);
		}

		/*procitaj putanju do prostora stanja s ulaza i grupiraj stanja sa susjedima*/
		if (argumenti.indexOf("--ss") > -1) {
		putanjaProstorStanja = argumenti.get(argumenti.indexOf("--ss") + 1);
		//System.out.println("Putanja prostor stanja " + putanjaProstorStanja);
		prostorStanja = new File(putanjaProstorStanja);
		Scanner scanProstorStanja = new Scanner(prostorStanja, "UTF-8");
		int brojac = 0;
		while(scanProstorStanja.hasNextLine()) {
			String linija = scanProstorStanja.nextLine();
			/*preskacem linije s komentarima*/
			if (!linija.equals("#")) {
				brojac++;
				if (brojac == 1) {
					pocetnoStanje = linija;
					
				}
				else if (brojac == 2) {
					konacnaStanja = Arrays.asList(linija.split(" "));
				}
				else if (brojac > 2) {
					String kljuc = linija.split(": ")[0];
					String susjediSCijenama = linija.split(": ")[1];
					String[] susjediOdvojeni = susjediSCijenama.split(" ");
					Map<String, Double> pom = new TreeMap<>();
					for (int i = 0; i < susjediOdvojeni.length; i++) {
						pom.put(susjediOdvojeni[i].split(",")[0], Double.parseDouble(susjediOdvojeni[i].split(",")[1]));
					}
					susjedniCvorovi.put(kljuc, pom);
				}
				
				
			}
			
			
		}
		scanProstorStanja.close();
		}

		/*procitaj putanju do heuristike s ulaza*/

		if (argumenti.indexOf("--h") > -1) {
		//System.out.println("Nadena heuristika na indeksu " + argumenti.indexOf("--h"));
		putanjaHeuristika = argumenti.get(argumenti.indexOf("--h") + 1);
		//System.out.println("Putanja heuristika " + putanjaHeuristika);
		heuristika = new File(putanjaHeuristika);
		Scanner scanHeuristika = new Scanner(heuristika, "UTF-8");
		while(scanHeuristika.hasNextLine()) {
			String linija = new String();
			linija = scanHeuristika.nextLine();
			String kljuc = linija.split(": ")[0];
			String vrijednost = linija.split(": ")[1];
			mapaHeuristika.put(kljuc, Double.parseDouble(vrijednost));
			}
		scanHeuristika.close();
		}

				
		/*provjeri zastavicu*/
		if (argumenti.indexOf("--check-optimistic") > -1) {
			zastavica = "--check-optimictic";
		} 
		else if (argumenti.indexOf("--check-consistent") > -1) {
			zastavica = "--check-consistent";
		}
		else {
			zastavica = "";
		}
		if (!zastavica.equals("")) {
			System.out.println("Zastavica " + zastavica);
		}
		
		if (algoritam.equals("bfs")) {
			bfsAlgoritam();
		}
		
		if (algoritam.equals("ucs")) {
			ucsAlgoritam();
		}
		
		if (algoritam.equals("astar")) {
			aStarAlgoritam();
		}
		
	}

	public static void bfsAlgoritam() {
		boolean imaRjesenja = false;
		Cvor cvorSVrha = new Cvor(pocetnoStanje, null, 0.0, 0.0);
		Deque<Cvor> open = new LinkedList<>();
		List<Cvor> closed = new LinkedList<>();
		Map<String, Double> pomMapa = new TreeMap<>();
		open.add(cvorSVrha);
		while(!open.isEmpty()) {
			cvorSVrha = open.removeFirst();
			//System.out.println("Trenutno stanje " + cvorSVrha.stanje + " i cijena " + cvorSVrha.cijena);
			if (konacnaStanja.contains(cvorSVrha.stanje)) {
				//System.err.println("Kraj");
				imaRjesenja = true;
				closed.add(cvorSVrha);
				break;
			}
			closed.add(cvorSVrha);
			pomMapa = susjedniCvorovi.get(cvorSVrha.stanje);
			for (Map.Entry<String, Double> entry: pomMapa.entrySet()) {
				if (closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null) == null) {
				open.addLast(new Cvor(entry.getKey(), cvorSVrha, entry.getValue() + cvorSVrha.cijena, 0.0));
				}
			}

			
			
		}
		
		
		ispisBfsUcsAstar(algoritam, imaRjesenja, cvorSVrha, closed);
		
	}
	
	public static void ucsAlgoritam() {
		boolean imaRjesenja = false;
		Cvor cvorSVrha = new Cvor(pocetnoStanje, null, 0.0, 0.0);
		List<Cvor> open = new LinkedList<>();
		List<Cvor> closed = new LinkedList<>();
		Map<String, Double> pomMapa = new TreeMap<>();
		open.add(cvorSVrha);
		while(!open.isEmpty()) {
			cvorSVrha = open.get(0);
			open.remove(0);
			if (konacnaStanja.contains(cvorSVrha.stanje)) {
				imaRjesenja = true;
				System.out.println("Kraj");
				closed.add(cvorSVrha);
				break;
			}
			
			pomMapa = susjedniCvorovi.get(cvorSVrha.stanje);
			closed.add(cvorSVrha);
			for (Map.Entry<String, Double> entry: pomMapa.entrySet()) {
				if (closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null) == null) {
				open.add(new Cvor(entry.getKey(), cvorSVrha, entry.getValue() + cvorSVrha.cijena, 0.0));
				}
			}
			open.sort(Comparator.comparing(Cvor::getCijena)
                    .thenComparing(Comparator.comparing(Cvor::getStanje)));
			
		}
		
		ispisBfsUcsAstar(algoritam, imaRjesenja, cvorSVrha, closed);
		
		
	}
	
	public static void aStarAlgoritam() {
		boolean imaRjesenja = false;
		Cvor cvorSVrha = new Cvor(pocetnoStanje, null, 0.0, 0.0);
		List<Cvor> open = new LinkedList<>();
		List<Cvor> closed = new LinkedList<>();
		Map<String, Double> pomMapa = new TreeMap<>();
		open.add(cvorSVrha);
		
		while(!open.isEmpty()) {
			cvorSVrha = open.get(0);
			open.remove(0);
			if (konacnaStanja.contains(cvorSVrha.stanje)) {
				imaRjesenja = true;
				System.out.println("Kraj");
				closed.add(cvorSVrha);
				break;
			}
			
			pomMapa = susjedniCvorovi.get(cvorSVrha.stanje);
			closed.add(cvorSVrha);
			for (Map.Entry<String, Double> entry: pomMapa.entrySet()) {
				
				if ((open.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null) != null) && 
						cvorSVrha.stanje.equals(entry.getKey())) {
					
					if (cvorSVrha.cijena > entry.getValue()) {
						int index = open.indexOf(cvorSVrha);
						open.remove(index);
					}
				}
				
				if ((closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null) != null) && 
						cvorSVrha.stanje.equals(entry.getKey())) {
					if (cvorSVrha.cijena > entry.getValue()) {
						int index = closed.indexOf(cvorSVrha);
						closed.remove(index);
					}
				}
				
				
				if (closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null) == null) {
					open.add(new Cvor(entry.getKey(), cvorSVrha, entry.getValue() + cvorSVrha.cijena, entry.getValue() + cvorSVrha.cijena + mapaHeuristika.get(entry.getKey())));
					}
				
				
			}
			open.sort(Comparator.comparing(Cvor::getFunkcijaF)
                    .thenComparing(Comparator.comparing(Cvor::getStanje)));
		}
		
		ispisBfsUcsAstar(algoritam, imaRjesenja, cvorSVrha, closed);
	}
	
	public static void ispisBfsUcsAstar(String algoritam, boolean imaRjesenja, Cvor cvorSVrha, List<Cvor> closed) {
		List<String> linijeZaIspis = new ArrayList<>();
		List<String> put = new ArrayList<>();
		
		if (algoritam.equals("bfs") || algoritam.equals("ucs")) {
			linijeZaIspis.add("# " + algoritam.toUpperCase());
		}
		else if (algoritam.equals("astar")) {
			linijeZaIspis.add("# A-STAR " + putanjaHeuristika);
		}
		
		if (imaRjesenja) {
			linijeZaIspis.add("[FOUND_SOLUTION]: yes");
		}
		else {
			linijeZaIspis.add("[FOUND_SOLUTION]: no");
		}
		
		if (linijeZaIspis.contains("[FOUND_SOLUTION]: yes")) {
			
			
			
			Cvor pomCvor = cvorSVrha;
			put.add(pomCvor.stanje);
			while(pomCvor.roditelj != null) {
				int index = closed.indexOf(pomCvor.roditelj);
				put.add(closed.get(index).stanje);
				pomCvor = closed.get(index);
			}
			
			String linijaPut = new String();
			linijaPut += "[PATH]: ";
			Collections.reverse(put);
			for (String s: put) {
				if (put.indexOf(s) != (put.size() - 1)) {
					linijaPut += s + " => ";
				} 
				else {
					linijaPut += s;
				}
			}
			linijeZaIspis.add("[STATES_VISITED]: " + closed.size());
			linijeZaIspis.add("[PATH_LENGTH]: " + put.size());
			linijeZaIspis.add("[TOTAL_COST]: " + cvorSVrha.cijena);
			linijeZaIspis.add(linijaPut);
			
		}
		
		for (String linija: linijeZaIspis) {
			System.out.println(linija);
		}
		
		for (Cvor cvor: closed) {
			System.out.println(cvor.stanje);
		}
	}
	
	
	public static class Cvor {
		private String stanje;
		private Cvor roditelj;
		private Double cijena;
		private Double funkcijaF;
		
		public Cvor(String stanje, Cvor roditelj, Double cijena, Double funkcijaF) {
			this.stanje = stanje;
			this.roditelj = roditelj;
			this.cijena = cijena;
			this.funkcijaF = funkcijaF;
		}
		
		public String getStanje() {
			return this.stanje;
		}
		
		public Double getCijena() {
			return this.cijena;
		}
		
		public Double getFunkcijaF() {
			return this.funkcijaF;
		}
	}

}