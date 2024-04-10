package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
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
	public static Map<String, Map<String, Double>> susjedniCvorovi = new TreeMap<>(); //strukturu TreeMap koristio sam svugdje gdje sam koristio i Map zbog algoritma sortiranja koji ona ima ugraden
	public static File heuristika;
	public static Map<String, Double> mapaHeuristika = new TreeMap<>();
	

	public static void main(String[] args) throws FileNotFoundException {
		
		//argumente koji su polje pretvaram u listu jer mi je jednostavnije raditi s listama
		argumenti = Arrays.asList(args);

		//citanje algoritma koji trebam koristiti
		if (argumenti.indexOf("--alg") > -1) {
		algoritam = argumenti.get(argumenti.indexOf("--alg") + 1);
		}

		//citanje putanje do prostora stanja
		if (argumenti.indexOf("--ss") > -1) {
		putanjaProstorStanja = argumenti.get(argumenti.indexOf("--ss") + 1);
		prostorStanja = new File(putanjaProstorStanja);
		Scanner scanProstorStanja = new Scanner(prostorStanja, "UTF-8"); //citanje iz datoteke gdje je smjesten opis prostora stanja
		int brojac = 0;
		while(scanProstorStanja.hasNextLine()) {
			String linija = scanProstorStanja.nextLine();
			//preskacem pocetne linije s komentarima
			if (!linija.equals("#")) {
				brojac++;
				//brojac je zapravo oznacava koja je linija po redu iza onih s komentarima, prva linija nakon komentara je pocetno stanje
				if (brojac == 1) {
					pocetnoStanje = linija;
					
				}
				//druga linija nakon komentara je skup konacnih stanja
				else if (brojac == 2) {
					konacnaStanja = Arrays.asList(linija.split(" "));
				}
				//nakon linije s konacnim stanjima slijede linije u kojima su definirani cvorovi i njihovi susjedi s cijenama
				else if (brojac > 2) {
					
					if (linija.split(": ").length == 2) {
					String kljuc = linija.split(": ")[0]; //kljuc je stanje za koje gledamo susjede
					String susjediSCijenama = linija.split(": ")[1];
					String[] susjediOdvojeni = susjediSCijenama.split(" ");
					Map<String, Double> pom = new TreeMap<>(); //mapa u koju se pohranjuju susjedi s njihovim cijenama
					for (int i = 0; i < susjediOdvojeni.length; i++) {
						pom.put(susjediOdvojeni[i].split(",")[0], Double.parseDouble(susjediOdvojeni[i].split(",")[1]));
					}
					susjedniCvorovi.put(kljuc, pom); //mapa ciji je kljuc stanje, a vrijednost nova mapa parova susjed-cijena
					}
					else if (linija.split(":").length == 1) {
						susjedniCvorovi.put(linija.split(":")[0], null);
					}
					}
				
				
			}
			
			
		}
		scanProstorStanja.close();
		}

		//citanje putanje do heuristike s ulaza, ostatak slicno kao i kod prostora stanja
		if (argumenti.indexOf("--h") > -1) {
		putanjaHeuristika = argumenti.get(argumenti.indexOf("--h") + 1);
		heuristika = new File(putanjaHeuristika);
		Scanner scanHeuristika = new Scanner(heuristika, "UTF-8");
		while(scanHeuristika.hasNextLine()) {
			String linija = new String();
			linija = scanHeuristika.nextLine();
			String kljuc = linija.split(": ")[0];
			String vrijednost = linija.split(": ")[1];
			mapaHeuristika.put(kljuc, Double.parseDouble(vrijednost)); //mapa ciji je kljuc stanje, a vrijednost njegova heuristika
				

			}
		scanHeuristika.close();
		}

				
		//provjeravanje zastavice
		if (argumenti.indexOf("--check-optimistic") > -1) {
			zastavica = "--check-optimistic";
		} 
		else if (argumenti.indexOf("--check-consistent") > -1) {
			zastavica = "--check-consistent";
		}
		
		else {
			zastavica = "";
		}
		//ovisno o procitanom algoritmu, poziva se metoda za obradu tog algoritma
		if (algoritam.equals("bfs")) {
			bfsAlgoritam();
		}
		
		if (algoritam.equals("ucs")) {
			ucsAlgoritam();
		}
		
		if (algoritam.equals("astar")) {
			aStarAlgoritam();
		}
		
		//osim algoritma provjerava se i zastavica
		if (zastavica.equals("--check-optimistic")) {
			provjeriOptimisticnost();
		}
		
		else if (zastavica.equals("--check-consistent")) {
			provjeriKonzistentnost();
		}
		
	}

	//metoda za obradu BFS algoritma
	public static void bfsAlgoritam() {
		boolean imaRjesenja = false;
		Cvor cvorSVrha = new Cvor(pocetnoStanje, null, 0.0, 0.0);
		//koristenje struktura Deque i LinkedList preuzeo sam iz skripte profesora Marka Cupica
		Deque<Cvor> open = new LinkedList<>(); //Deque zbog metoda removeFirst() i addLast()
		List<Cvor> closed = new ArrayList<>(); //ArrayList jer je slozenost pretrage O(1), iako je svejedno jer imam i dodavanje i pretragu
		Map<String, Double> pomMapa = new TreeMap<>();
		open.add(cvorSVrha);

		/*osnovni dio algoritma - koncept algoritma preuzet s prezentacija iz predmeta Uvod u umjetnu inteligenciju
		 * Pretrazivanje prostora stanja, slajd 19
		 * Koncept open, closed listi Pretrazivanje prostora stanja, slajd 45*/
		while(!open.isEmpty()) {
			cvorSVrha = open.removeFirst();
			if (konacnaStanja.contains(cvorSVrha.stanje)) {
				imaRjesenja = true;
				closed.add(cvorSVrha);
				break;
			}
			//ako closed ne sadrzi trenutni cvor dodaj ga
			if (!closed.contains(cvorSVrha)) {
				closed.add(cvorSVrha);
			}
			pomMapa = susjedniCvorovi.get(cvorSVrha.stanje); //
			for (Map.Entry<String, Double> entry: pomMapa.entrySet()) {
				//ako se cvor ne nalazi u listi closed, dodaj ga u open pa kasnije prosiri
				//ideja: https://www.baeldung.com/find-list-element-java
				if (closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null) == null) {
				open.addLast(new Cvor(entry.getKey(), cvorSVrha.stanje, entry.getValue() + cvorSVrha.cijena, 0.0)); //ako susjedni cvor jos nije posjecen, dodaje se u listu za obilazak, roditelj mu je trenutni cvor
				}
				
			}
				
		}
		
		ispisBfsUcsAstar(algoritam, imaRjesenja, cvorSVrha, closed); //metoda za ispis rezultata
		
	}
	
	//metoda za obradu UCS algoritma
	public static void ucsAlgoritam() {
		boolean imaRjesenja = false;
		Cvor cvorSVrha = new Cvor(pocetnoStanje, null, 0.0, 0.0);
		List<Cvor> open = new LinkedList<>(); //ovdje ne koristim vise Deque jer necu dodavati elemente nuzno na kraj
		List<Cvor> closed = new ArrayList<>();
		Map<String, Double> pomMapa = new TreeMap<>();
		open.add(cvorSVrha);
		/*kostur algoritma preuzet s prezentacija iz predmeta Uvod u umjetnu inteligenciju
		 * Pretrazivanje prostora stanja, slajd 25*/
		while(!open.isEmpty()) {
			cvorSVrha = open.get(0);
			open.remove(0);
			if (konacnaStanja.contains(cvorSVrha.stanje)) {
				imaRjesenja = true;
				closed.add(cvorSVrha);
				break;
			}
			
			pomMapa = susjedniCvorovi.get(cvorSVrha.stanje);
			closed.add(cvorSVrha);
			for (Map.Entry<String, Double> entry: pomMapa.entrySet()) {
				if (closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null) == null) {
				open.add(new Cvor(entry.getKey(), cvorSVrha.stanje, entry.getValue() + cvorSVrha.cijena, 0.0));
				}
			}
			//ne vrsim dodavanje cvorova za obradu na kraj vec prvo dodam sve susjede trenutnog cvora koji prosirujem pa na kraju sortiram cijelu listu prvo prema cijeni pa prema nazivu stanja
			//ideja: https://www.techiedelight.com/sort-list-of-objects-using-comparator-java/
			open.sort(Comparator.comparing(Cvor::getCijena)
                    .thenComparing(Comparator.comparing(Cvor::getStanje)));
			
		}
		
		ispisBfsUcsAstar(algoritam, imaRjesenja, cvorSVrha, closed); //metoda za ispis rezultata
		
		
	}
	
	//metoda za obradu A* algoritma
	public static void aStarAlgoritam() {
		boolean imaRjesenja = false;
		Cvor cvorSVrha = new Cvor(pocetnoStanje, null, 0.0, 0.0);
		List<Cvor> open = new LinkedList<>();
		List<Cvor> closed = new ArrayList<>();
		Map<String, Double> pomMapa = new TreeMap<>();
		open.add(cvorSVrha);
		
		/*kostur algoritma preuzet s prezentacija iz predmeta Uvod u umjetnu inteligenciju
		 * Heuristicko pretrazivanje, slajd 17*/
		while(!open.isEmpty()) {
			cvorSVrha = open.get(0);
			open.remove(0);
			if (konacnaStanja.contains(cvorSVrha.stanje)) {
				imaRjesenja = true;
				closed.add(cvorSVrha);
				break;
			}
			
			pomMapa = susjedniCvorovi.get(cvorSVrha.stanje);
			closed.add(cvorSVrha);
			
			for (Map.Entry<String, Double> entry: pomMapa.entrySet()) {
				
				
				//provjeri nalazi li se neki susjed vec u listi open pa usporedi cijene
				if ((open.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().isPresent())) {
					int index = open.indexOf(open.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null));
					Cvor stariCvor = open.get(index);
					if (stariCvor.cijena > entry.getValue() + cvorSVrha.cijena) {
						open.remove(index);
					}
				}
				//provjeri nalazi li se neki susjed u listi closed pa usporedi cijene
				if ((closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().isPresent())) {
					Cvor findFirst = closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null);
					int index = closed.indexOf(findFirst);
					Cvor stariCvor = closed.get(index);
					if (stariCvor.cijena > entry.getValue() + cvorSVrha.cijena) {
						closed.remove(index);
					}
				}
				
				if (!closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().isPresent()) {
					open.add(new Cvor(entry.getKey(), cvorSVrha.stanje, entry.getValue() + cvorSVrha.cijena, entry.getValue() + cvorSVrha.cijena + mapaHeuristika.get(entry.getKey())));
					}
				
				
			}
			//sada prvo sortiram po funkciji f
			open.sort(Comparator.comparing(Cvor::getFunkcijaF)
                    .thenComparing(Comparator.comparing(Cvor::getStanje)));
		}
		
		ispisBfsUcsAstar(algoritam, imaRjesenja, cvorSVrha, closed);
	}
	
	//metoda koju koristim za izracun prave cijene kod provjere optimisticnosti heuristike, zapravo UCS algoritam koji vraca cijenu
	//UCS algoritam odabrao sam jer njegovu implementaciju vec imam u ovoj vjezbi, a koristi cijene za razliku od BFS algoritma
	public static Double cijena(String trenutnoStanje) {
		//cijena konacnog stanja je 0
		if (konacnaStanja.contains(trenutnoStanje)) {
			return 0.0;
		}
		Cvor cvorSVrha = new Cvor(trenutnoStanje, null, 0.0, 0.0);
		boolean imaRjesenja = false;
		List<Cvor> open = new LinkedList<>();
		List<Cvor> closed = new LinkedList<>();
		Map<String, Double> pomMapa = new TreeMap<>();
		
		
		open.add(cvorSVrha);
		while(!open.isEmpty()) {
			cvorSVrha = open.get(0);
			open.remove(0);
			if (konacnaStanja.contains(cvorSVrha.stanje)) {
				imaRjesenja = true;
				closed.add(cvorSVrha);
				break;
			}
			closed.add(cvorSVrha);
			
			pomMapa = susjedniCvorovi.get(cvorSVrha.stanje);
			
			for (Map.Entry<String, Double> entry: pomMapa.entrySet()) {
				if (closed.stream().filter(cvor -> cvor.stanje.equals(entry.getKey())).findFirst().orElse(null) == null) {
				open.add(new Cvor(entry.getKey(), cvorSVrha.stanje, entry.getValue() + cvorSVrha.cijena, 0.0));
				}
			}
		
			open.sort(Comparator.comparing(Cvor::getCijena)
                    .thenComparing(Comparator.comparing(Cvor::getStanje)));
			
		}
	
		return cvorSVrha.cijena;
		
	}
	
	/*kostur algoritama za optimisticnost i konzistentnost takoder je preuzet s prezentacija iz predmeta Uvod u umjetnu inteligenciju
	 * Heuristicko pretrazivanje, slajd 19
	 * Heuristicko pretrazivanje, slajd 25*/
	public static void provjeriOptimisticnost() {
		List<String> linijeZaIspis = new LinkedList<>();
		linijeZaIspis.add("# HEURISTIC-OPTIMISTIC " + putanjaHeuristika);
		Double pravaCijena;
		Double heuristika;
		boolean postojiGreska = false;
		
		for (String trenutnoStanje: susjedniCvorovi.keySet()) {
			pravaCijena = cijena(trenutnoStanje);
			heuristika = mapaHeuristika.get(trenutnoStanje);
			if (heuristika <= pravaCijena) {
				linijeZaIspis.add("[CONDITION]: [OK] h(" + trenutnoStanje + ") <= h*: " + heuristika + " <= " + pravaCijena);
			}
			else {
				postojiGreska = true;
				linijeZaIspis.add("[CONDITION]: [ERR] h(" + trenutnoStanje + ") <= h*: " + heuristika + " <= " + pravaCijena);
			}
		}
		if (postojiGreska) {
			linijeZaIspis.add("[CONCLUSION]: Heuristic is not optimistic.");
		}
		else {
			linijeZaIspis.add("[CONCLUSION]: Heuristic is optimistic.");
		}
		
		for (String linija: linijeZaIspis) {
			System.out.println(linija);
		}
	}
	
	public static void provjeriKonzistentnost() {
		
		List<String> linijeZaIspis = new LinkedList<>();
		Map<String, Double> pomMapa = new TreeMap<>();
		Double trenutnaHeuristika;
		Double susjednaHeuristika;
		Double susjednaCijena;
		boolean postojiGreska = false;
		
		linijeZaIspis.add("# HEURISTIC-CONSISTENT " + putanjaHeuristika);
		for (String trenutnoStanje: susjedniCvorovi.keySet()) {
			pomMapa = susjedniCvorovi.get(trenutnoStanje);
			trenutnaHeuristika = mapaHeuristika.get(trenutnoStanje);
			if (pomMapa != null) {
			for (Map.Entry<String, Double> entry: pomMapa.entrySet()) {
				susjednaHeuristika = mapaHeuristika.get(entry.getKey());
				susjednaCijena = entry.getValue();
				if (trenutnaHeuristika <= susjednaHeuristika + susjednaCijena) {
					linijeZaIspis.add("[CONDITION]: [OK] h(" + trenutnoStanje + ") <= h(" + entry.getKey() + ") + c: " 
							+ trenutnaHeuristika + " <= " + susjednaHeuristika + " + " + susjednaCijena);
				} 
				else {
					linijeZaIspis.add("[CONDITION]: [ERR] h(" + trenutnoStanje + ") <= h(" + entry.getKey() + ") + c: " 
							+ trenutnaHeuristika + " <= " + susjednaHeuristika + " + " + susjednaCijena);
					postojiGreska = true;
				}
			}
		}
			
		}
		if (postojiGreska) {
			linijeZaIspis.add("[CONCLUSION]: Heuristic is not consistent.");
		}
		else {
			linijeZaIspis.add("[CONCLUSION]: Heuristic is consistent.");
		}
		for (String linija: linijeZaIspis) {
			System.out.println(linija);
		}
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
			
			
			//rekonstruiranje puta
			Cvor pomCvor = cvorSVrha;
			put.add(pomCvor.stanje);
			while(pomCvor.roditelj != null) {
				String roditelj = pomCvor.roditelj;
				Cvor trazeni = closed.stream().filter(cvor -> cvor.stanje.equals(roditelj)).findFirst().orElse(null);
				int index = closed.indexOf(trazeni);
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
	
	//klasa cvor koju koristim kao pomoc
	public static class Cvor {
		private String stanje;
		private String roditelj;
		private Double cijena;
		private Double funkcijaF;
		
		public Cvor(String stanje, String roditelj, Double cijena, Double funkcijaF) {
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