package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import ui.*;

public class Solution {

	public static Set<List<String>> klauzalniOblik = new LinkedHashSet<>();
	public static Set<List<String>> skupPotpore = new LinkedHashSet<>();
	public static String zadatak = new String();
	public static String putanjaPopisKlauzula = new String();
	public static String putanjaKorisnickeNaredbe = new String();
	public static File popisKlauzula;
	public static List<String> klauzulaZaDokaz = new ArrayList<>();	
	public static List<String> negiranaKlauzulaZaDokaz = new ArrayList<>();
	public static int brojKlauzula = 0;
	public static boolean rezolucijaOpovrgavanjem = false;
	public static List<Integer> zamjenaSkupPotpore = new ArrayList<>();
	public static List<Integer> zamjenaKlauzalniOblik = new ArrayList<>();
	public static String klauzulaZaDokazIspis = new String();
	public static Map<Integer, List<List<String>>> mapaParova = new LinkedHashMap<>();
	public static List<String> ulazneLinije = new ArrayList<String>();
	public static File korisnickeNaredbe;
	public static List<String> linije = new ArrayList<>();
	public static boolean imaDuplikata = false;
	public static List<String> novaKlauzulaZaDokaz = new ArrayList<>();	
	
	public static void main(String[] args) throws IOException {
		
		//prva dva argumenta imam u svakom slucaju
		
		zadatak = args[0];
		System.out.println(zadatak); //vrijednost moze biti resolution ili cooking
		
		putanjaPopisKlauzula = args[1];
		System.out.println(putanjaPopisKlauzula);
		
		popisKlauzula = new File(putanjaPopisKlauzula);

		//provjera koji cu podzatak raditi, ako postoji i treci argument, radim podzadatak Kuharski asistent
		if (args.length == 3) {
			putanjaKorisnickeNaredbe = args[2];
			System.out.println(putanjaKorisnickeNaredbe);
			korisnickeNaredbe = new File(putanjaKorisnickeNaredbe);
			Scanner scanKorisnickeNaredbe = new Scanner(korisnickeNaredbe, "UTF-8");
			while(scanKorisnickeNaredbe.hasNextLine()) {
				ulazneLinije.add(scanKorisnickeNaredbe.nextLine().toLowerCase());
			}
			String klauzulaZaDodavanje = new String();
			String klauzulaZaBrisanje = new String();
			String klauzulaZaIspitivanje = new String();

			String odvojeniLiterali[];


			for(String ulaznaLinija: ulazneLinije) {
				String naredba = ulaznaLinija.substring(ulaznaLinija.length() - 1, ulaznaLinija.length());
				//https://www.java67.com/2015/07/how-to-append-text-to-existing-file-in-java-example.html
				//https://www.javatpoint.com/java-filewriter-class
				//dodavanje klauzule na kraj datoteke
				if (naredba.equals("+")) {
					klauzulaZaDodavanje = ulaznaLinija.substring(0, ulaznaLinija.length() - 2);
					//append mod FileWriter-a je ukljucen ako u konstruktoru za drugi argument proslijedim true
					FileWriter upisKlauzule = new FileWriter(popisKlauzula, true);
					upisKlauzule.write(klauzulaZaDodavanje + "\n");
					System.out.println(klauzulaZaDodavanje + " dodana.");
					upisKlauzule.close();
					citanjeKlauzalniOblik();
					
				}
				//brisanje klauzule iz datoteke
				else if (naredba.equals("-")) {
					String azuriraneKlauzule = new String();
					klauzulaZaBrisanje = ulaznaLinija.substring(0, ulaznaLinija.length() - 2);
					//nadi klauzulu ako postoji
					if (linije.contains(klauzulaZaBrisanje)) {
						linije.remove(linije.indexOf(klauzulaZaBrisanje));
						System.out.println(klauzulaZaBrisanje + " obrisana.");
						for(String linija: linije) {
							azuriraneKlauzule += linija + "\n";
						}
						//prebrisi sve i upisi ponovno
						FileWriter azuriranjeKlauzula = new FileWriter(popisKlauzula);
						azuriranjeKlauzula.write(azuriraneKlauzule);
						azuriranjeKlauzula.close();
						citanjeKlauzalniOblik();
					}
					

				}
				//ovaj dio pozivat ce se kad god radim podzadatak Kuharski asistent te se ispituje klauzula
				else if (naredba.equals("?")) {
					
					skupPotpore.clear();
					klauzulaZaDokazIspis = "";
					novaKlauzulaZaDokaz.clear();
					klauzulaZaIspitivanje = ulaznaLinija.substring(0, ulaznaLinija.length() - 2);
					System.out.println("Ispitujem " + klauzulaZaIspitivanje);
					odvojeniLiterali = klauzulaZaIspitivanje.split(" v ");
					citanjeKlauzalniOblik();
					for (int i = 0; i < odvojeniLiterali.length; i++) {
						novaKlauzulaZaDokaz.add(odvojeniLiterali[i]);
					}
					//ciscenje klauzalnog oblika - objasnjeno u metodi
					klauzalniOblik = new LinkedHashSet<List<String>>(strategijaBrisanja(klauzalniOblik));
					System.out.println("Pocetne klauzule - pocetak:");
					//ispis pocetnih klauzula i negirane klauzule za dokaz
					ispisKlauzalniOblik(new ArrayList<>(klauzalniOblik));
					negiranaKlauzulaZaDokaz = negiranjeKlauzuleZaDokaz(novaKlauzulaZaDokaz);
					List<String> pom = new ArrayList<>();
					for (String s: negiranaKlauzulaZaDokaz) {
						pom.clear();
						pom.add(s);
						skupPotpore.add(new ArrayList<>(pom));
					}
					System.out.println("Skup potpore");
					for(List<String> lista: skupPotpore) {
						System.out.println(lista);
					}
					brojKlauzula = klauzalniOblik.size();
					for (String s: negiranaKlauzulaZaDokaz) {
						brojKlauzula++;
						System.out.println(brojKlauzula + ". " + s);
					}
					System.out.println("==========================");
					//poziv algoritma rezolucije opovrgavanjem
					rezolucijaOpovrgavanjem = algoritam(new ArrayList<>(klauzalniOblik), new ArrayList<>(skupPotpore));
					//priprema klauzule koju sam trebao dokazati za "ljepsi" ispis
					for (String s: novaKlauzulaZaDokaz) {
						klauzulaZaDokazIspis += s;
						if (novaKlauzulaZaDokaz.indexOf(s) != (novaKlauzulaZaDokaz.size() - 1)) {
							klauzulaZaDokazIspis += " v ";
						}
						
					}
					if (rezolucijaOpovrgavanjem) {
						System.out.println("[CONCLUSION]: " + klauzulaZaDokazIspis + " is true");
					}
					else {
						System.out.println("[CONCLUSION]: " + klauzulaZaDokazIspis + " is unknown");
					}
				}
			}

			scanKorisnickeNaredbe.close();
		}
		
		//ako imam dva argumenta radim prvi podzadatak vjezbe - Rezoluciju opovrgavanjem
		else if (args.length == 2) {
		citanjeKlauzalniOblik();
		List<String> zadnja = new ArrayList<>();
		//ako u pocetnim klauzulama nema duplikata sve je u redu, medutim ako su se pojavili ukloni ih
		if (!imaDuplikata) {
		for (List<String> lista: klauzalniOblik) {
			zadnja = lista;
			}
		klauzalniOblik.remove(zadnja);
		}
		//dio isti kao i u Kuharskom asistentu kad se ispituje klauzula - detaljnije o metodama, kod njihovog poziva
		klauzalniOblik = new LinkedHashSet<List<String>>(strategijaBrisanja(klauzalniOblik));
		negiranaKlauzulaZaDokaz = negiranjeKlauzuleZaDokaz(klauzulaZaDokaz);
		List<String> pom = new ArrayList<>();
		System.out.println("Polazne klauzule - pocetak:");
		ispisKlauzalniOblik(new ArrayList<>(klauzalniOblik));
		for (String s: negiranaKlauzulaZaDokaz) {
			pom.clear();
			pom.add(s);
			skupPotpore.add(new ArrayList<>(pom));
		}

		brojKlauzula = klauzalniOblik.size();
		for (String s: negiranaKlauzulaZaDokaz) {
			brojKlauzula++;
			System.out.println(brojKlauzula + ". " + s);
		}
		
		System.out.println("==========================");
		
		rezolucijaOpovrgavanjem = algoritam(new ArrayList<>(klauzalniOblik), new ArrayList<>(skupPotpore));
		for (String s: klauzulaZaDokaz) {
			klauzulaZaDokazIspis += s;
			if (klauzulaZaDokaz.indexOf(s) != (klauzulaZaDokaz.size() - 1)) {
				klauzulaZaDokazIspis += " v ";
			}
			
		}
		if (rezolucijaOpovrgavanjem) {
			System.out.println("[CONCLUSION]: " + klauzulaZaDokazIspis + " is true");
		}
		else {
			System.out.println("[CONCLUSION]: " + klauzulaZaDokazIspis + " is unknown");
		}
		}

	}
	
	//metoda za ispis klauzula koje su u klauzalnom obliku - u ovoj implementaciji lista lista
	public static void ispisKlauzalniOblik(List<List<String>> klauzalniOblik) {
		
		for (int i = 0; i < klauzalniOblik.size(); i++) {
			List<String> pomLista = new ArrayList<>(klauzalniOblik.get(i));
			
			String tekstIspis = new String();
			tekstIspis += ((i + 1) + ". ");
			for (String s: pomLista) {
				tekstIspis += s;
				if (!(pomLista.indexOf(s) == (pomLista.size() - 1))) {
					tekstIspis += " v ";
				}
			}
			System.out.println(tekstIspis);
		}
	}
	
	//metoda za negiranje klauzule za dokaz, ako postoji vise literala u klauzuli, oni ce nakon negiranja postati zasebne klauzule
	public static List<String> negiranjeKlauzuleZaDokaz(List<String> klauzulaZaDokaz) {
		
		List<String> povratnaLista = new ArrayList<>();
		String literal = new String();
		if (klauzulaZaDokaz.size() == 1) {
			literal = klauzulaZaDokaz.get(0);
			if (literal.contains("~")) {
				povratnaLista.add(literal.substring(1, literal.length()));
			}
			else {
				povratnaLista.add("~" + literal);
			}
		}
		else {
			for (int i = 0; i < klauzulaZaDokaz.size(); i++) {
				literal = klauzulaZaDokaz.get(i);
				if (literal.contains("~")) {
					povratnaLista.add(literal.substring(1, literal.length()));
				}
				else {
					povratnaLista.add("~" + literal);
				}
			}
		}
		return povratnaLista;
	}
	
	//strategija brisanja koja provjerava je li klauzula podskup neke druge te tautologije
	public static List<List<String>> strategijaBrisanja(Set<List<String>> klauzalniOblik) {
		
		//uklanjanje podskupova klauzula

		List<List<String>> klauzalniOblikLista = new ArrayList<>(klauzalniOblik);
		List<String> trazenaPodlista = new ArrayList<>();
		List<String> glavnaPodlista = new ArrayList<>();
		List<String> literali = new ArrayList<>();
		List<List<String>> listeZaZamjenu = new ArrayList<>();
		int trazeniIndeks;
		
		//ideja za provjeru podliste: https://stackoverflow.com/questions/32864977/how-to-check-if-a-list-contains-a-sublist-in-a-given-order-in-java
		for (int i = 0; i < klauzalniOblikLista.size(); i++) {
			glavnaPodlista = klauzalniOblikLista.get(i);
			
			for (int j = 0; j < klauzalniOblikLista.size(); j++) {
				trazenaPodlista = klauzalniOblikLista.get(j);
				if (i != j) {
					//ako lista ima podlistu treba izbaciti onu vecu jer je sadrzana u svojoj podlisti
					trazeniIndeks = Collections.indexOfSubList(glavnaPodlista, trazenaPodlista);
					if (trazeniIndeks != -1) {
						listeZaZamjenu.add(glavnaPodlista);
					}
				}
			}
			
		}
		//ukloni sve liste koje su oznacene
		for(List<String> lista: listeZaZamjenu) {
			trazeniIndeks = klauzalniOblikLista.indexOf(lista);
			if (trazeniIndeks != -1) {
			klauzalniOblikLista.remove(trazeniIndeks);
			}
			
		}
		listeZaZamjenu.clear();
		//oznacavanje tautologija
		for (int i = 0; i < klauzalniOblikLista.size(); i++) {
			literali = klauzalniOblikLista.get(i);
			for (String s: literali) {
				
				if (literali.contains(s) && literali.contains("~" + s)) {
					listeZaZamjenu.add(literali);
				}
			}
		}
		//izbaci oznacene tautologije
		for(List<String> lista: listeZaZamjenu) {
			trazeniIndeks = klauzalniOblikLista.indexOf(lista);
			if (trazeniIndeks != -1) {
			klauzalniOblikLista.remove(trazeniIndeks);
			}
		}
		
		return klauzalniOblikLista;
	}
	
	//faktorizacija - izbacivanje duplih literala iz klauzule, preko strukture set
	public static List<String> faktorizacija (List<String> listaZaFaktorizaciju) {
		Set<String> prijelazniSet = new HashSet<>(listaZaFaktorizaciju);
		
		return new ArrayList<>(prijelazniSet);
	}
	
	//algoritam za rezoluciju opovrgavanjem
	public static boolean algoritam(List<List<String>> klauzalniOblik, List<List<String>> skupPotpore) {
		boolean imaRjesenja = false; //odnosi se na pronalazak NIL-a
		List<String> klauzulaSkupPotpore = new ArrayList<>();
		List<String> klauzulaKlauzulniOblik = new ArrayList<>();
		List<String> klauzulaSkupPotporeDva = new ArrayList<>();
		int indeks1 = 0;
		int indeks2 = 0;
		boolean podudaranje1 = false;
		boolean podudaranje2 = false;
		String literal1= new String();
		String literal2 = new String();
		List<String> dobivenaKlauzula = new ArrayList<>();
		List<String> pomLista1;
		List<String> pomLista2;
		int podudaranje1Dio = 0;
		int podudaranje2Dio = 0;
		int zadnjiIndeks = 0;
		List<List<String>> pom1 = new ArrayList<>();
		List<List<String>> pom2 = new ArrayList<>();
		mapaParova.clear();
		while (!imaRjesenja) {
			//podudaranje u ovoj implementaciji = jedan literal je negacija drugog
			podudaranje1= false; //podudaranje klauzula iz skupa potpore i pocetnog skupa
			podudaranje2 = false; //podudaranje klauzula iz skupa potpore
			
			//prvo provjeravam podudara li se neka klauzula iz pocetnog skupa s nekom iz skupa potpore 
			//dvije petlje i gledanje svih mogucih kombinacija
			for (int i = 0; i < skupPotpore.size(); i++) {
				if (podudaranje1) {
					break;
				}
				klauzulaSkupPotpore = new ArrayList<>(skupPotpore.get(i));
				indeks1 = i;

				for (int j = 0; j < klauzalniOblik.size(); j++) {
					
					if (podudaranje1) {

						break;
					}
					klauzulaKlauzulniOblik = new ArrayList<>(klauzalniOblik.get(j));
					indeks2 = j;
					//provjera jesam li ove dvije klauzule vec usporedio
					if (!podudaranje(mapaParova, klauzulaKlauzulniOblik, klauzulaSkupPotpore)) {
					for (String literalSkupPotpore: klauzulaSkupPotpore) {
						if (podudaranje1) {
							break;
						}
						for (String literalKlauzalniOblik: klauzulaKlauzulniOblik) {
							
							if (podudaranje1) {
								break;
							}
							//gledanje je li jedan literal negacija drugog i oznacavanje gdje je negirani, a gdje obicni literal
							if (literalSkupPotpore.equals("~" + literalKlauzalniOblik)) {
								podudaranje1 = true;
								literal1 = literalSkupPotpore;
								literal2 = literalKlauzalniOblik;
								podudaranje1Dio = 1;

							}
							else if (literalKlauzalniOblik.equals("~" + literalSkupPotpore)) {
								podudaranje1 = true;
								literal1 = literalKlauzalniOblik;
								literal2 = literalSkupPotpore;
								podudaranje1Dio = 2;
							} 
							else {
								podudaranje1 = false;
							}
						}
					}
				}
				}
			}
			//provjera podudaranja unutar skupa potpore
			//dvije petlje kroz skup potpore i gledanje svih mogucih kombinacija
			if (!podudaranje1) {
			for (int i = 0; i < skupPotpore.size(); i++) {
				if (podudaranje2) {
					break;
				}
				klauzulaSkupPotpore = new ArrayList<>(skupPotpore.get(i));
				indeks1 = i;

				for (int j = 0; j < skupPotpore.size(); j++) {
					if (podudaranje2) {
						break;
					}
					klauzulaSkupPotporeDva = new ArrayList<>(skupPotpore.get(j));
					indeks2 = j;
					if (!podudaranje(mapaParova, klauzulaSkupPotpore, klauzulaSkupPotporeDva)) {
					if (i != j) {
					for (String literalSkupPotpore: klauzulaSkupPotpore) {
						if (podudaranje2) {
							break;
						}
						for (String literalSkupPotporeDva: klauzulaSkupPotporeDva) {
							if (podudaranje2) {
								break;
							}
							if (literalSkupPotpore.equals("~" + literalSkupPotporeDva)) {
								podudaranje2 = true;
								literal1 = literalSkupPotpore;
								literal2 = literalSkupPotporeDva;
								podudaranje2Dio = 1;

							}
							else if (literalSkupPotporeDva.equals("~" + literalSkupPotpore) && !podudaranje2) {
								podudaranje2 = true;
								literal1 = literalSkupPotporeDva;
								literal2 = literalSkupPotpore;
								podudaranje2Dio = 2;
							}
							else {
								podudaranje2 = false;
							}
							
						}
					}
				}
				}
				}
			
			}
			
		}
			//ako nema podudaranja, tj nema vise mogucih kombinacija, onda nema ni rjesenja, tj nismo dokazali trazenu klauzulu
			if (!podudaranje1 && !podudaranje2) {
				return false;
			}
			//slucaj kad se podudaraju jedna klauzula iz pocetnog skupa i jedna iz skupa potpore
			else if (podudaranje1) {
				dobivenaKlauzula.clear();

				//nadi u skupovima klauzule u kojima se dogodilo podudaranje
				if (podudaranje1Dio == 1) {
					for (int i = 0; i < skupPotpore.size(); i++) {
						if (skupPotpore.get(i).equals(klauzulaSkupPotpore)) {
							indeks1 = i;
							break;
						}
					}
					
					for (int i = 0; i < klauzalniOblik.size(); i++) {
							if (klauzalniOblik.get(i).equals(klauzulaKlauzulniOblik)) {
								indeks2 = i;
								break;
							}
					}
					
					
				}

				pomLista1 = new ArrayList<>(skupPotpore.get(indeks1));
				pomLista2 = new ArrayList<>(klauzalniOblik.get(indeks2));
				pom1.add(new ArrayList<>(pomLista1));
				pom1.add(new ArrayList<>(pomLista2));
				//stavi u mapu vec iskoristenih kombinacija ovu
				if (mapaParova.isEmpty()) {
					zadnjiIndeks = 0;
					mapaParova.put(zadnjiIndeks, new ArrayList<>(pom1));
				}
				else {
				zadnjiIndeks = zadnjiKljuc(mapaParova.keySet());
				mapaParova.put(zadnjiIndeks + 1, new ArrayList<>(pom1));
				}
				
				pom1.clear();
				//ovisno o tome u kojem je dijelu bilo podudaranje, znam koja je klauzula iz kojeg skupa
				if (podudaranje1Dio == 1) {
					pomLista1.remove(literal1);
					pomLista2.remove(literal2);
				}
				else if (podudaranje1Dio == 2) {
					pomLista1.remove(literal2);
					pomLista2.remove(literal1);
				}
				
				if (pomLista1.isEmpty() && pomLista2.isEmpty()) {
						System.out.println(klauzulaSkupPotpore + " + " + klauzulaKlauzulniOblik + " => NIL");
						dobivenaKlauzula.add("NIL");
						imaRjesenja = true;	
						return true;
					}
				else {
				dobivenaKlauzula.addAll(pomLista1);
				dobivenaKlauzula.addAll(pomLista2);
				}
				
				dobivenaKlauzula = faktorizacija(new ArrayList<>(dobivenaKlauzula));
				
				System.out.println(klauzulaSkupPotpore + " + " + klauzulaKlauzulniOblik + " => " + dobivenaKlauzula);
				skupPotpore.add(new ArrayList<>(dobivenaKlauzula)); //dodaj novu klauzulu u skup potpore

			}
			
			//podudaranje skup potpore - skup potpore
			else if (podudaranje2) {
				dobivenaKlauzula.clear();

				//rekonstruiraj iz koje je klauzule dosao negirani, a iz koje obicni literal
				if (podudaranje2Dio == 1) {
					for (int i = 0; i < skupPotpore.size(); i++) {

							if (skupPotpore.get(i).equals(klauzulaSkupPotpore)) {
							indeks1 = i;
							break;
							}	
						
					}
					
					for (int i = 0; i < skupPotpore.size(); i++) {
						if (i != indeks1) {
							if (skupPotpore.get(i).equals(klauzulaSkupPotporeDva)) {
							indeks2 = i;
							break;
							}
						}
						
					}
					
					
				}
				else if (podudaranje2Dio == 2) {

					for (int i = 0; i < skupPotpore.size(); i++) {
							if (skupPotpore.get(i).equals(klauzulaSkupPotporeDva)) {
							indeks2 = i;
							break;
							}
						
					}
					
					for (int i = 0; i < skupPotpore.size(); i++) {
						if (i != indeks2) {
							if (skupPotpore.get(i).equals(klauzulaSkupPotpore)) {
							indeks1 = i;
							break;
							}
						}
						
					}
				}
				
				
				pomLista1 = new ArrayList<>(skupPotpore.get(indeks1));
				pomLista2 = new ArrayList<>(skupPotpore.get(indeks2));
				
				pom2.add(new ArrayList<>(pomLista1));
				pom2.add(new ArrayList<>(pomLista2));
				if (mapaParova.isEmpty()) {
					zadnjiIndeks = 0;
					mapaParova.put(zadnjiIndeks, new ArrayList<>(pom2));
				}
				else {
				zadnjiIndeks = zadnjiKljuc(mapaParova.keySet());
				mapaParova.put(zadnjiIndeks + 1, new ArrayList<>(pom2));
				}
				pom2.clear();

				if (pomLista1.contains(literal1)) {
					pomLista1.remove(pomLista1.indexOf(literal1));
					pomLista2.remove(pomLista2.indexOf(literal2));
				}
				
				else if (pomLista2.contains(literal1)) {
					pomLista1.remove(pomLista1.indexOf(literal2));
					pomLista2.remove(pomLista2.indexOf(literal1));
				}

				if (pomLista1.isEmpty() && pomLista2.isEmpty()) {
					System.out.println(klauzulaSkupPotpore + " + " + klauzulaSkupPotporeDva + " => NIL");
						dobivenaKlauzula.add("NIL");
						imaRjesenja = true;	
						return true;
				}
				else {
				dobivenaKlauzula.addAll(pomLista1);
				dobivenaKlauzula.addAll(pomLista2);
				}
				System.out.println(klauzulaSkupPotpore + " + " + klauzulaSkupPotporeDva + " => " + dobivenaKlauzula);
				dobivenaKlauzula = faktorizacija(dobivenaKlauzula);
				skupPotpore.add(new ArrayList<>(dobivenaKlauzula));

			}

			System.out.println("Polazne klauzule:");
			ispisKlauzalniOblik(new ArrayList<>(klauzalniOblik));
			skupPotpore = strategijaBrisanja(new LinkedHashSet<List<String>>(skupPotpore));
			System.out.println("Skup potpore:");
			ispisKlauzalniOblik(new ArrayList<>(skupPotpore));

		}
	
		return false;	
	}
	
	//
	//ideja za iterator: https://www.geeksforgeeks.org/how-to-get-the-last-element-from-linkedhashset-in-java/
	//nadi zadnji indeks da znas gdje umetnuti sljedeci par
	public static Integer zadnjiKljuc(Set<Integer> setKljuceva) {
		Iterator<Integer> set = setKljuceva.iterator();
		Integer zadnjiKljuc = 0;
		while(set.hasNext()) {
			zadnjiKljuc = set.next(); 
		}
		
		return zadnjiKljuc;
	}
	
	//metoda za trazenje jesam li zadani par klauzula vec koristio
	//parovi je mapa u kojoj su pohranjeni vec koristeni parovi klauzula
	//klauzula1 i klauzula2 su klauzule u obliku lista koje usporedujem
	public static boolean podudaranje(Map<Integer, List<List<String>>> parovi, List<String> klauzula1, List<String> klauzula2) {

		if (parovi.size() == 0) {
			return false;
		}

		for (Map.Entry<Integer, List<List<String>>> entry: parovi.entrySet()) {

			//https://howtodoinjava.com/java/collections/arraylist/compare-two-arraylists/
			if ((entry.getValue().get(0).equals(klauzula1) && entry.getValue().get(1).equals(klauzula2)) ||
					(entry.getValue().get(0).equals(klauzula2) && entry.getValue().get(1).equals(klauzula1))) {

				return true;
			}
		}
		return false;
	}

	//metoda za citanje neceg u pocetnog skupa 
	public static void citanjeKlauzalniOblik() throws FileNotFoundException {
		klauzalniOblik.clear();
		klauzulaZaDokaz.clear();
		linije.clear();
		imaDuplikata = false;
		Scanner scanPopisKlauzula = new Scanner(popisKlauzula, "UTF-8");
		while(scanPopisKlauzula.hasNextLine()) {
			String linija = scanPopisKlauzula.nextLine().toLowerCase();
			linije.add(linija);
			if (linija.charAt(0) != '#') {
				
				String[] literaliOdvojeni = linija.split(" v ");
				List<String> pomLista = new ArrayList<>();
				for (int i = 0; i < literaliOdvojeni.length; i++) {
					pomLista.add(literaliOdvojeni[i]);
				}
				if (scanPopisKlauzula.hasNextLine()) {
				klauzalniOblik.add(pomLista);
				} 
				else if (!scanPopisKlauzula.hasNextLine()) {
					//System.out.println("Na kraju sam");
					//slucaj gdje imam 2 jednake klauzule od kojih zelim napraviti skup klauzula
					if (klauzalniOblik.add(pomLista)) {
						imaDuplikata = false;
					}
					else {
						imaDuplikata = true;
					}
					klauzulaZaDokaz = pomLista;
				}
			}
		}
		
		scanPopisKlauzula.close();
	}
}
