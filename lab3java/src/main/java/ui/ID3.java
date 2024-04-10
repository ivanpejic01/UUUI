package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.lang.Math;

public class ID3 {
	
	public static Map<String, HashSet<String>> mapaZnacajkaVrijednosti = new HashMap<>();
	public static Map<String, ArrayList<String>> mapaZnacajkaSveVrijednosti = new HashMap<>();
	public static Map<Integer, Map<String, String>> mapaGrane = new HashMap<>();
	public static List<Cvor> listaCvorova = new ArrayList<>();
	public static List<Cvor> stablo = new ArrayList<>();
	public static String ciljnaVarijabla = new String();
	public static Set<String> vrijednostiCiljneVarijable = new TreeSet<>();
	public static ArrayList<String> sveVrijednostiCiljneVarijable = new ArrayList<>();
	public static Map<String, HashSet<String>> mapaZnacajkaVrijednostiTest = new HashMap<>();
	public static Map<String, ArrayList<String>> mapaZnacajkaSveVrijednostiTest = new HashMap<>();
	
	

	//prazan konstruktor klase ID3
	public ID3() {
		
	}
	
	public void fit(File datotekaUcenje, int parametarDubina) throws FileNotFoundException {
		
		Scanner scanUcenje = new Scanner(datotekaUcenje, "UTF-8");
		
		String[] znacajke = scanUcenje.nextLine().split(",");
		for (int i = 0; i < znacajke.length; i++) {
		}
		
		while(scanUcenje.hasNextLine()) {
			String[] linija = scanUcenje.nextLine().split(",");
			for (int i = 0; i < linija.length; i++) {
				HashSet<String> vrijednostiZnacajke = mapaZnacajkaVrijednosti.get(znacajke[i]);
				ArrayList<String> sveVrijednostiZnacajke = mapaZnacajkaSveVrijednosti.get(znacajke[i]);
				if (vrijednostiZnacajke == null) {
					vrijednostiZnacajke = new HashSet<>();
				}
				if(sveVrijednostiZnacajke == null) {
					sveVrijednostiZnacajke = new ArrayList<>();
				}
				vrijednostiZnacajke.add(linija[i]);
				sveVrijednostiZnacajke.add(linija[i]);
				//dvije strukture koje spremaju zasebne vrijednosti svake znacajke ili sve vrijednosti po redovima
				mapaZnacajkaVrijednosti.put(znacajke[i], vrijednostiZnacajke);
				mapaZnacajkaSveVrijednosti.put(znacajke[i], sveVrijednostiZnacajke);
			}
		
		}

		ciljnaVarijabla = znacajke[znacajke.length - 1];
		sveVrijednostiCiljneVarijable = mapaZnacajkaSveVrijednosti.get(ciljnaVarijabla);
	
		
			List<Cvor> stablo = new ArrayList<>();
			List<Cvor> pomStablo = new ArrayList<>();
			Deque<Cvor> stabloProsirivanje = new LinkedList<>();
			Map<String, Map<String, Map<String, Integer>>> znacajkaZaVrijednost = new HashMap<>();
			Map<String, Double> znacajkaEntropija = new HashMap<>();
			Map<String, Double> znacajkaInformacijskaDobit = new TreeMap<>();
			Map<String, List<Cvor>> znacajkaGrane = new HashMap<>();
			Map<String, Double> granaEntropija = new HashMap<>();
			Map<String, Map<String, Double>> znacajkaGranaEntropija = new HashMap<>();
			Map<String, Map<String, String>> znacajkaGranaDijete = new HashMap<>();
			Map<String, String> granaDijete = new HashMap<>();
			Map<String, Map<String, Double>> dijeteGranaEntropija = new HashMap<>();
			
			//prvi korak je naci korijen stabla
			for (int i = 0; i < znacajke.length - 1; i++) {
				//za svaku znacajku pogledaj broj vrijednosti ciljne varijabli koje se pojavljuju ovisno o vrijednosti
				//znacajke
				//struktura mape znacajkaZaVrijednost -> Map<znacajka, Map<vrijednostZnacajke, Map<vrijednostCiljneVarijable, brojPojavaCiljneVarijable>>>
				Map<String, Integer> brojZaVrijednost = new HashMap<>();
				Cvor grana = new Cvor(null);
				
				for (int j = 0; j < sveVrijednostiCiljneVarijable.size(); j++) {
					
					Integer broj = brojZaVrijednost.get(sveVrijednostiCiljneVarijable.get(j));
					Map<String, Map<String, Integer>> pomMapa = znacajkaZaVrijednost.get(znacajke[i]);
					
					if (pomMapa == null) {
						pomMapa = new HashMap<>();
					}
					
					Map<String, Integer> pomMapa2 = pomMapa.get(mapaZnacajkaSveVrijednosti.get(znacajke[i]).get(j));
					if (pomMapa2 == null) {
						pomMapa2 = new HashMap<>();
					}
					Integer pomBroj = pomMapa2.get(sveVrijednostiCiljneVarijable.get(j));
					if (pomBroj == null) {
						pomBroj = 0;
					}
					pomBroj++;
					pomMapa2.put(sveVrijednostiCiljneVarijable.get(j), pomBroj);
					pomMapa.put(mapaZnacajkaSveVrijednosti.get(znacajke[i]).get(j), new HashMap<>(pomMapa2));
					znacajkaZaVrijednost.put(znacajke[i], new HashMap<>(pomMapa));
					if (broj == null) {
						broj = 0;
					}
					broj++;
					brojZaVrijednost.put(sveVrijednostiCiljneVarijable.get(j), broj);
					
				}
				
				Double entropija = 0.0;

				//pomaze za entropiju jer fiksira znacajku i pojave pojedine vrijednost ciljne varijable
				for (Map.Entry<String, Integer> entry: brojZaVrijednost.entrySet()) {
					Double razlomak = (Double.valueOf(entry.getValue()) / sveVrijednostiCiljneVarijable.size());
					entropija -= (razlomak * (Math.log(razlomak) / Math.log(2)));	
				}
				
				znacajkaEntropija.put(znacajke[i], entropija);
				
				//izracunaj informacijsku dobit za trenutnu znacajku
				Double informacijskaDobit = entropija;
				for (Map.Entry<String, Map<String, Integer>> entry: znacajkaZaVrijednost.get(znacajke[i]).entrySet()) {
					Integer brojnik = 1;
					Integer nazivnik = 0;
					Double malaEntropija = 0.0;
					Double razlomak = 0.0;
					for (Map.Entry<String, Integer> entry2: entry.getValue().entrySet()) {
						Set<String> dostupniKljucevi =  znacajkaZaVrijednost.get(znacajke[i]).get(entry.getKey()).keySet();
						Set<String> potrebniKljucevi = mapaZnacajkaVrijednosti.get(ciljnaVarijabla);
						Set<String> razlika = new HashSet<>(potrebniKljucevi);
						razlika.removeAll(dostupniKljucevi);
						//ako postoji neka vrijednost koja se ne pojavljuje za fiksiranu znacajku i njezinu vrijednost
						if (!razlika.isEmpty()) {
							brojnik = 0;
							nazivnik += entry2.getValue();
							//break;
						}
						else {
						nazivnik += entry2.getValue();
						}
					}
					
					//iteriranje po svim vrijednostima ciljne varijable i njenim pojavama za fiksiranu vrijednost znacajke
					for (Map.Entry<String, Integer> entry2: entry.getValue().entrySet()) {
						if (brojnik == 0) {
							razlomak = 0.0;
							malaEntropija -= 0.0;
						}
						else {
							brojnik = entry2.getValue();
							razlomak = Double.valueOf(brojnik) / nazivnik;
							malaEntropija -= (razlomak * (Math.log(razlomak) / Math.log(2)));
						}
					}
					
					grana.setZnacajka(null);
					grana.setEntropija(malaEntropija);
					if (malaEntropija == 0.0) {
						grana.setList(true);
					}
					else {
						grana.setList(false);
					}
					granaEntropija.put(entry.getKey(), malaEntropija);

					informacijskaDobit -= (Double.valueOf(nazivnik) / sveVrijednostiCiljneVarijable.size()) * malaEntropija;
					
				}

				dijeteGranaEntropija.put(znacajke[i], granaEntropija);
				//spremi entropije grana za pojedinu znacajku
				znacajkaGranaEntropija.put(znacajke[i], new HashMap<>(granaEntropija));
				znacajkaInformacijskaDobit.put(znacajke[i], informacijskaDobit);
				List<Cvor> pomLista = znacajkaGrane.get(znacajke[i]);
				if (pomLista == null) {
					pomLista = new ArrayList<>();
				}

				pomLista.add(grana);
				znacajkaGrane.put(znacajke[i], pomLista);
				
		
			}
			
			Double maksimum = -10000000.0;
			String maxZnacajka = null;
			String ispis = new String();
			Double infoDobit = 0.0;
			//iz svih informacijskih dobiti nadi najvecu
			//ako su dvije informacijske dobiti jednake, bit ce uzeta ona koja je abecedno manja jer je 
			//znacajkaInformacijskaDobit TreeMap
			for (Map.Entry<String, Double> entry: znacajkaInformacijskaDobit.entrySet()) {
				infoDobit = entry.getValue();

				if (infoDobit > maksimum) {
					maksimum = infoDobit;
					maxZnacajka = entry.getKey();
				}
				
				ispis += "IG(" + entry.getKey() + ")=" + infoDobit + " ";
			}

			Cvor korijen = new Cvor(maxZnacajka);
			korijen.setEntropija(znacajkaEntropija.get(maxZnacajka));
			for (Map.Entry<String, Map<String, Integer>> entry: znacajkaZaVrijednost.get(maxZnacajka).entrySet()) {
				korijen.dodajGranu(entry.getKey());
			}
			korijen.setGranaEntropija(znacajkaGranaEntropija.get(maxZnacajka));
			korijen.setGranaRoditelj(null);
			korijen.setCvorRoditelj(null);
			korijen.setList(false);
			korijen.setRoditelj(null);
			//stablo sluzi za ispis na kraju, a pomStablo simulira red
			stablo.add(korijen);
			pomStablo.add(korijen);
			
			boolean imaSirenja = false;
			//glavni dio programa, prosirujem cvorove dok god mogu i gradim stablo, zapravo BFS algoritam
			while(!imaSirenja) {
				
				Map<String, Double> dijeteID = new TreeMap<>();
				//ako nema vise cvorova za sirenje
				if (pomStablo.size() == 0) {
					break;
				}
				//cvor za sirenje je cvor s vrha simuliranog reda
				Cvor cvorZaSirenje = pomStablo.get(0);
				pomStablo.remove(0);
				//samo listove mogu prosiriti
				if (!cvorZaSirenje.getList()) {
				granaDijete.clear();
				//za svaku granu
				for (String s: cvorZaSirenje.getGraneDjeca()) {
					Double entropijaGrane = cvorZaSirenje.getGranaEntropija().get(s);
					//u mojoj implementaciji listovi stabla su cvorovi kojima je znacajka postavljena na null, a 
					//znacajka list na true
					Cvor cvorZaDodavanje = new Cvor(null);
					if (entropijaGrane == 0.0) {
						Cvor noviCvor = new Cvor("");
						noviCvor.setList(true);
						noviCvor.setEntropija(0.0); 
						noviCvor.setGranaRoditelj(s);
						noviCvor.setCvorRoditelj(cvorZaSirenje.getZnacajka());
						noviCvor.setRoditelj(cvorZaSirenje);
						noviCvor.setDubina(cvorZaSirenje.getDubina() + 1);
						//dodavanje lista u stablo
						stablo.add(noviCvor);
						}
					
					else {
							
							//ovako idem po stablu od trenutnog cvora do vrha da vidim koje znacajke smijem ispitati kao djecu
							//npr ako stablo ide A->B->C i ja prosirujem C, nema smisla da ispitujem B i A
							String znacajkaRoditeljFilter = cvorZaSirenje.getZnacajka();
							//stabloFilter je zapravo lista cvorova koje preskacem 
							List<Cvor> stabloFilter = new ArrayList<>();
							stabloFilter.add(cvorZaSirenje);
							boolean imaRoditeljaFilter = true;
							
							Cvor stariCvorFilter = cvorZaSirenje;
							while (imaRoditeljaFilter) {
								 
								Cvor roditeljFilter = stariCvorFilter.getRoditelj();
								if (roditeljFilter == null) {
									imaRoditeljaFilter = false;
								}
								else {
									znacajkaRoditeljFilter = roditeljFilter.getCvorRoditelj();
									stabloFilter.add(roditeljFilter);
									stariCvorFilter = roditeljFilter;
								}
							}
							
							for (int i = 0; i < znacajke.length - 1; i++) {
								String znacajka = znacajke[i];

								if (stabloFilter.stream().filter(cvor -> cvor.getZnacajka().equals(znacajka)).toList().size() == 0) {
									Integer broj = 0;
									Integer nazivnik = 0;
									List<Integer> brojnici = new ArrayList<>();
									for (String cilj: mapaZnacajkaVrijednosti.get(ciljnaVarijabla)) {
										broj = znacajkaZaVrijednost.get(cvorZaSirenje.getZnacajka()).get(s).get(cilj);
										nazivnik += broj;
										brojnici.add(broj);
									}

									Double malaEntropija = 0.0;
									Double razlomak = 0.0;
									for (Integer brojnik: brojnici) {
										razlomak = Double.valueOf(brojnik) / nazivnik;
										malaEntropija -= razlomak * (Math.log(razlomak) / Math.log(2));
									}
									
								Double infoDobitMala = entropijaGrane;
								Integer velikiNazivnik = 0;
								List<Double> umnosci = new ArrayList<>();
								for(Map.Entry<String, Map<String, Integer>> entry: znacajkaZaVrijednost.get(znacajka).entrySet()) {

									Integer maliNazivnik = 0;
									List<Integer> malaLista = new ArrayList<>();
									for (String cilj: mapaZnacajkaVrijednosti.get(ciljnaVarijabla)) {

											Integer brojnikID = 0;
											
											Map<String, String> pomMapaZaEntropiju = new HashMap<>();
											pomMapaZaEntropiju.put(znacajka, entry.getKey());
											String znacajkaRoditelj = cvorZaSirenje.getZnacajka();
											boolean imaRoditelja = true;

											String staraZnacajka = znacajka;
											String staraGrana = s;
											//sad idem po stablu prema gore da konstruiram put, odnosno grane po kojima djeca mogu doci do roditelja
											while (imaRoditelja) {
												 
												String pomZnacajka = znacajkaRoditelj;
												
												Cvor roditelj = stablo.stream().
												filter(cvorStablo -> cvorStablo.getZnacajka().equals(pomZnacajka)).
												findFirst().orElse(null);
												if (roditelj == null) {
													imaRoditelja = false;
												}
												else {
													pomMapaZaEntropiju.put(roditelj.getZnacajka(), staraGrana);
													znacajkaRoditelj = roditelj.getCvorRoditelj();
													staraZnacajka = roditelj.getZnacajka();
													staraGrana = roditelj.getGranaRoditelj();
												}
											}
											
											
											
											//racunanje dijela za informacijsku dobit
											for (int k = 0; k < mapaZnacajkaSveVrijednosti.get(ciljnaVarijabla).size(); k++) {
												
											
											boolean podudaranje = true;
											//ako postoji red takav da je ciljna varijabla jednaka trenutnoj vrijednosti cilj te ako 
											//je u tom redu vrijdednost roditelja i djece jednaka pohranjenim vrijednostima, to je dobro i 
											//brojnikID povecavam za 1
											for(Map.Entry<String, String> pomoc: pomMapaZaEntropiju.entrySet()) {
												if (podudaranje) {
												if ((mapaZnacajkaSveVrijednosti.get(pomoc.getKey()).get(k).equals(pomoc.getValue())) 
														&& mapaZnacajkaSveVrijednosti.get(ciljnaVarijabla).get(k).equals(cilj)) {
													podudaranje = true;
												}
												else {
													podudaranje = false;
												}
												}
											}
											if (podudaranje) {
												brojnikID += 1;
											}
											
											}
											
											maliNazivnik += brojnikID;
											malaLista.add(brojnikID);
										}
										
										//sad racunam entropiju svake grane cvora
										velikiNazivnik += maliNazivnik;
										Double maliRazlomak = 0.0;
										Double novaEntropija = 0.0;
										if (maliNazivnik == 0) {
											novaEntropija -= 0;
										}
										else {
										for (Integer noviBroj: malaLista) {
											if (noviBroj == 0) {
												novaEntropija -= 0;
											}
											else {
											maliRazlomak = Double.valueOf(noviBroj) / maliNazivnik;
											novaEntropija -= maliRazlomak * (Math.log(maliRazlomak) / Math.log(2));
											}
										}
										}
										//dodajem entropiju grane u mapu i to cu kasnije koristiti za informacijsku dobit
										granaEntropija.put(entry.getKey(), novaEntropija);
										umnosci.add(Double.valueOf(maliNazivnik) * novaEntropija);
										
									}
								//idi po svim umnoscima entropije i brojnika te podijeli s velikim nazivnikom
								for (Double umnozak: umnosci) {
									infoDobitMala -= Double.valueOf(umnozak) / velikiNazivnik;
								}
								dijeteID.put(znacajka, Math.round(infoDobitMala * Math.pow(10, 5)) / Math.pow(10, 5));
								znacajkaGranaEntropija.put(znacajka, new HashMap<>(granaEntropija));
								}
							}
							//slicnan ispis i trazenje djeteta s najvecom informacijskom dobiti kao i kod korijena
							String podIspis = new String();
							Double maxInfoDobit = -10000000.0;
							String maxZnacajkaMala = new String();
							for(Map.Entry<String, Double> entry: dijeteID.entrySet()) {
								podIspis += "IG(" + entry.getKey() + ")=" + entry.getValue() + " ";
								
								if (entry.getValue() > maxInfoDobit) {
									maxInfoDobit = entry.getValue();
									maxZnacajkaMala = entry.getKey();
								}
							}
							
							granaDijete.put(s, maxZnacajkaMala);
							
							if (maxZnacajkaMala.length() < 1) {
								cvorZaDodavanje.setZnacajka(null);
							}
							else {
							cvorZaDodavanje.setZnacajka(maxZnacajkaMala);
							}
							cvorZaDodavanje.setEntropija(22.5);
							cvorZaDodavanje.setGranaRoditelj(s);
							cvorZaDodavanje.setCvorRoditelj(cvorZaSirenje.getZnacajka());
							cvorZaDodavanje.setGranaEntropija(znacajkaGranaEntropija.get(maxZnacajkaMala));
							cvorZaDodavanje.setRoditelj(cvorZaSirenje);
							cvorZaDodavanje.setDubina(cvorZaSirenje.getDubina() + 1);
							if (maxZnacajkaMala.length() > 0) {
							for (String grana: mapaZnacajkaVrijednosti.get(maxZnacajkaMala)) {
								cvorZaDodavanje.dodajGranu(grana);
								}
							}
							//ako za cvor dijete nema vise reprezentativnih primjera, on je list
							if (maxZnacajkaMala.length() < 1) {
								cvorZaDodavanje.setList(true);
							}
							else {
								cvorZaDodavanje.setList(false);
							}
							stablo.add(cvorZaDodavanje);
							pomStablo.add(cvorZaDodavanje);
							}

					
					}
				//za trenutnu znacajku stavi njegove grane i djecu
				znacajkaGranaDijete.put(cvorZaSirenje.getZnacajka(), new HashMap<>(granaDijete));
				if (pomStablo.size() == 0) {
					imaSirenja = true;
				}
				
				}
				
				
			}
			int brojacGrana = 0;
			System.out.println("[BRANCHES]:");
			
			//ovisi zelim li rezati ili ne
			//ako je dubina zadana na 0, ispisat cu samo ciljnu znacajku koja se najvise pojavljuje
			if (parametarDubina == 0) {
				Map<String, Integer> ciljnaPojave = new HashMap<>();
				for (String ciljna: mapaZnacajkaVrijednosti.get(ciljnaVarijabla)) {
					for (int i = 0; i < sveVrijednostiCiljneVarijable.size(); i++) {
						if ((sveVrijednostiCiljneVarijable.get(i).equals(ciljna))) {
							Integer pomPojave = ciljnaPojave.get(ciljna);
							if (pomPojave == null) {
								pomPojave = 0;
							}
							pomPojave++;
							ciljnaPojave.put(ciljna, pomPojave);
						}
							
					}
				}
				String ciljnaMax = new String();
				int pojaveMax = 0;
				for (Map.Entry<String, Integer> entry: ciljnaPojave.entrySet()) {
					if (entry.getValue() > pojaveMax) {
						pojaveMax = entry.getValue();
						ciljnaMax = entry.getKey();
					}
				}
			}
			
			//ako rezem na nekoj dubini vecoj od 0
			else if (parametarDubina > 0) {
				for (Cvor cvor: stablo) {
					Map<String, String> pomMapaKraj = new HashMap<>();
					List<Cvor> put = new ArrayList<>();
					if ((cvor.getDubina() == parametarDubina) || 
							(cvor.getList() && cvor.getDubina() <= parametarDubina)) {
						//ako je cvor na dubini na kojoj zelim rezati ili je slucajno list
						put = new ArrayList<>();
						boolean imaRoditelja = true;
						String znacajkaRoditelj = cvor.getCvorRoditelj();
						Map<String, String> stabloGrana = new LinkedHashMap<>();
						Cvor stariCvor = cvor;
						//opet idem po stablu do vrha da vidim koji su cvorovi relevantni za koji red ispisa
						while (imaRoditelja) {

							Cvor roditelj = stariCvor.getRoditelj();
							if (roditelj == null) {
								imaRoditelja = false;
							}
							else {
								put.add(roditelj);
								stabloGrana.put(roditelj.getZnacajka(), stariCvor.getGranaRoditelj());
								znacajkaRoditelj = roditelj.getCvorRoditelj();
								stariCvor = roditelj;
							}
							}
						
						String ispisPuta = new String();
						Integer brojac = 1;
						List<Integer> indeksi = new ArrayList<>();
						//https://www.benchresources.net/how-to-iterate-through-linkedhashmap-in-reverse-order-in-java/
						List<String> obrnutiKljucevi = new ArrayList<>(stabloGrana.keySet());
						Collections.reverse(obrnutiKljucevi);
						//ispisujem put od roditelja do ciljnog cvora na danoj dubini ili do lista
						for (String kljuc: obrnutiKljucevi) {
							ispisPuta += brojac + ":" + kljuc + "=" + stabloGrana.get(kljuc) + " ";
							brojac++;
							pomMapaKraj.put(kljuc, stabloGrana.get(kljuc));
							for (int i = 0; i < mapaZnacajkaSveVrijednosti.get(kljuc).size(); i++) {
								if (mapaZnacajkaSveVrijednosti.get(kljuc).get(i).equals(stabloGrana.get(kljuc))) {
									//indeksi mi trebaju kasnije jer je presjek indeksa zapravo onaj red u kojem se pojavljuju
									//sve vrijednosti od vrha do trazenog cvora
									indeksi.add(i);
								}

							}

						}
						
						String maxVrijednost = new String();
						//ako sam podrezao samo tako da mi ostane korijen, vrijednost izlaza racunam po njegovoj vrijednosti
						if (stabloGrana.size() == 1) {
							Map<String, Integer> vrijednostBrojac = new HashMap<>();
							for (Integer indeks: indeksi) {
								String vrijednost = sveVrijednostiCiljneVarijable.get(indeks);
								Integer brojacVrijednosti = vrijednostBrojac.get(vrijednost);
								if (brojacVrijednosti == null) {
									brojacVrijednosti = 0;
								}
								brojacVrijednosti++;
								vrijednostBrojac.put(vrijednost, brojacVrijednosti);
							}
							
							int maxPojava = 0;
							maxVrijednost = new String();
							for (Map.Entry<String, Integer> entry: vrijednostBrojac.entrySet()) {
								if (entry.getValue() > maxPojava) {
									maxPojava = entry.getValue();
									maxVrijednost = entry.getKey();
								}
								if (entry.getValue() == maxPojava) {
									//ako se dvije ciljne vrijednosti pojave isti broj puta, ispisujem onu leksikografski prvu
									String prva = maxVrijednost;
									String druga = entry.getKey();
									if (prva.compareTo(druga) > 0) {
										maxVrijednost = entry.getKey();
									}
								}
							}
	
						}
						
						else {
						//ako je ostalo vise cvorova nego samo korijen
						int maxPojave = 0;
						//trazim presjek indeksa jer su to oni na kojima se podudaraju sve vrijednosti od dna prema vrhu
						for (Integer indeks: indeksi) {
							int pojave = Collections.frequency(indeksi, indeks);
							if (pojave > maxPojave) {
								maxPojave = pojave;
								}

							}
						
						Map<String, Integer> vrijednostBrojac = new HashMap<>();
						for (Integer indeks: indeksi) {
							int pojave = Collections.frequency(indeksi, indeks);
							if (pojave == maxPojave) {
								String vrijednost = sveVrijednostiCiljneVarijable.get(indeks);
								Integer brojacVrijednosti = vrijednostBrojac.get(vrijednost);
								if (brojacVrijednosti == null) {
									brojacVrijednosti = 0;
								}
								brojacVrijednosti++;
								vrijednostBrojac.put(vrijednost, brojacVrijednosti);
							}
						}
						//koja se vrijednost najvise pojavljuje
						int maxPojava = 0;
						maxVrijednost = new String();
						for (Map.Entry<String, Integer> entry: vrijednostBrojac.entrySet()) {
							if (entry.getValue() > maxPojava) {
								maxPojava = entry.getValue();
								maxVrijednost = entry.getKey();
							}
							
						}
						//ako se dvije vrijednosti pojave isti broj puta, ispisujem onu leksikografski prvu po redu
						for (Map.Entry<String, Integer> entry: vrijednostBrojac.entrySet()) {
							if (entry.getValue() == maxPojava) {
								String prva = maxVrijednost;
								String druga = entry.getKey();
								if (prva.compareTo(druga) > 0) {
									maxVrijednost = entry.getKey();
								}
							}
						}
						
	
						}
						pomMapaKraj.put(null, maxVrijednost);
						ispisPuta += maxVrijednost;
						System.out.println(ispisPuta);
						mapaGrane.put(brojacGrana, new HashMap<>(pomMapaKraj));
						brojacGrana++;
						
						
					}
				}
				
				
			}
			//ako nema podrezivanja
			else {
			for (Cvor cvor: stablo) {
				Map<String, String> pomMapaKraj = new HashMap<>();
				List<Cvor> put = new ArrayList<>();
				if (cvor.getList()) {
				put = new ArrayList<>();
				boolean imaRoditelja = true;
				String znacajkaRoditelj = cvor.getCvorRoditelj();
				Map<String, String> stabloGrana = new LinkedHashMap<>();
				Cvor stariCvor = cvor;
				//opet uzimam relevantne cvorove i radim slican postupak kao u prethodnom koraku
				while (imaRoditelja) {

					Cvor roditelj = stariCvor.getRoditelj();
					if (roditelj == null) {
						imaRoditelja = false;
					}
					else {
						put.add(roditelj);
						stabloGrana.put(roditelj.getZnacajka(), stariCvor.getGranaRoditelj());
						znacajkaRoditelj = roditelj.getCvorRoditelj();
						stariCvor = roditelj;
					}
					}
				String ispisPuta = new String();
				Integer brojac = 1;
				List<Integer> indeksi = new ArrayList<>();
				//https://www.benchresources.net/how-to-iterate-through-linkedhashmap-in-reverse-order-in-java/
				List<String> obrnutiKljucevi = new ArrayList<>(stabloGrana.keySet());
				Collections.reverse(obrnutiKljucevi);
				
				for (String kljuc: obrnutiKljucevi) {
					ispisPuta += brojac + ":" + kljuc + "=" + stabloGrana.get(kljuc) + " ";
					brojac++;
					pomMapaKraj.put(kljuc, stabloGrana.get(kljuc));
					for (int i = 0; i < mapaZnacajkaSveVrijednosti.get(kljuc).size(); i++) {
						if (mapaZnacajkaSveVrijednosti.get(kljuc).get(i).equals(stabloGrana.get(kljuc))) {
							indeksi.add(i);
						}

					}

				}
	
				//https://stackoverflow.com/questions/44367203/how-to-count-duplicate-elements-in-arraylist
				int indeksCiljne = 0;
				if (stabloGrana.size() == 1) {
					indeksCiljne = indeksi.get(0);
				}

				else {
				int maxPojave = 0;
				for (Integer indeks: indeksi) {
					int pojave = Collections.frequency(indeksi, indeks);
					if (pojave > maxPojave) {
						indeksCiljne = indeksi.get(indeksi.indexOf(indeks));
						maxPojave = pojave;

						}
					}
				}
				pomMapaKraj.put(null, sveVrijednostiCiljneVarijable.get(indeksCiljne));
				ispisPuta += sveVrijednostiCiljneVarijable.get(indeksCiljne);
				System.out.println(ispisPuta);
				mapaGrane.put(brojacGrana, new HashMap<>(pomMapaKraj));
				brojacGrana++;
					}
				}
			}
		
		scanUcenje.close();
		
	}
	
	public void predict(File datotekaProvjera) throws FileNotFoundException {
		
		Scanner scanProvjera = new Scanner(datotekaProvjera, "UTF-8");
		List<String> predvidanja = new ArrayList<>();
		String ispisPredvidanja = new String();
		
		String[] znacajke = scanProvjera.nextLine().split(",");
		for (int i = 0; i < znacajke.length; i++) {
		}
		while(scanProvjera.hasNextLine()) {
			String[] linija = scanProvjera.nextLine().split(",");
			for (int i = 0; i < linija.length; i++) {
				HashSet<String> vrijednostiZnacajke = mapaZnacajkaVrijednostiTest.get(znacajke[i]);
				ArrayList<String> sveVrijednostiZnacajke = mapaZnacajkaSveVrijednostiTest.get(znacajke[i]);
				if (vrijednostiZnacajke == null) {
					vrijednostiZnacajke = new HashSet<>();
				}
				if(sveVrijednostiZnacajke == null) {
					sveVrijednostiZnacajke = new ArrayList<>();
				}
				vrijednostiZnacajke.add(linija[i]);
				sveVrijednostiZnacajke.add(linija[i]);
				mapaZnacajkaVrijednostiTest.put(znacajke[i], vrijednostiZnacajke);
				mapaZnacajkaSveVrijednostiTest.put(znacajke[i], sveVrijednostiZnacajke);
			}
		
		}
		
		
		for (int i = 0; i < mapaZnacajkaSveVrijednostiTest.get(ciljnaVarijabla).size(); i++) {
			Map<String, String> testZnacajkaVrijednost = new HashMap<>();
			//spremi redak u mapu
			for (int j = 0; j < znacajke.length - 1; j++) {
				testZnacajkaVrijednost.put(znacajke[j], mapaZnacajkaSveVrijednostiTest.get(znacajke[j]).get(i));
			}
			
			List<String> kljuceviUlazTest = new ArrayList<>(testZnacajkaVrijednost.keySet());
			boolean imaPodudaranja = false;
			//mapaGrane su zapravo redovi ispisa
			for (int j = 0; j < mapaGrane.size(); j++) {
				Map<String, String> granaZnacajkaVrijednost = new HashMap<>(mapaGrane.get(j));
				List<String> pomListaGrane = new ArrayList<>(mapaGrane.get(j).keySet());
				List<String> razlika = new ArrayList<>();
				boolean svaPodudaranja = true;
				//uzmi samo one znacajke koje su ispisane u retku
				for (String s: pomListaGrane) {
					if (kljuceviUlazTest.contains(s)) {
						razlika.add(s);
					}
				}
	
				for (String s: razlika) {
					//konstruiraj put po grani od kraja prema pocetku, ako postoji redak u testu koji se podudara
					//to je dobro, inace nema podudaranja
					if (!testZnacajkaVrijednost.get(s).equals(granaZnacajkaVrijednost.get(s))) {
						svaPodudaranja = false;
					}
				}
				
				if (svaPodudaranja) {
					//ako se sve vrijednosti podudaraju, procitaj predvidanje
					predvidanja.add(mapaGrane.get(j).get(null));
					imaPodudaranja = true;
				}
					
			}
			//slucaj kad trazim onu vrijednost koja se najvise puta pojavljuje
			if (!imaPodudaranja) {
			Map<String, Integer> pojaveCiljneVarijable = new HashMap<>();
			List<String> sortiraneVrijednosti = new ArrayList<>(mapaZnacajkaSveVrijednosti.get(ciljnaVarijabla));
			Collections.sort(sortiraneVrijednosti);
		
			for (String s: sortiraneVrijednosti) {
				Integer broj = pojaveCiljneVarijable.get(s);
				if (broj == null) {
					broj = 1;
				} else {
					broj++;
				}
				pojaveCiljneVarijable.put(s, broj);
			}
			int maxPojave = 0;
			String maxVrijednost = new String();
			for (String s: sortiraneVrijednosti) {
				int pojave = pojaveCiljneVarijable.get(s);
				if (pojave > maxPojave) {
					maxPojave = pojave;
					maxVrijednost = s;
				}
			}
			predvidanja.add(maxVrijednost);
		}
			

				
		}
		ispisPredvidanja += "[PREDICTIONS]: ";
		for (String s: predvidanja) {
			ispisPredvidanja += (s + " ");
		}
		
		System.out.println(ispisPredvidanja.substring(0, ispisPredvidanja.length() - 1));
		
		int pogodak = 0;
		for (int i = 0; i < predvidanja.size(); i++) {
			if (predvidanja.get(i).equals(mapaZnacajkaSveVrijednostiTest.get(ciljnaVarijabla).get(i))) {
				pogodak++;
			}
		}

		Double ucinkovitost = Double.valueOf(pogodak) / (mapaZnacajkaSveVrijednostiTest.get(ciljnaVarijabla).size());
		System.out.printf("[ACCURACY]: %.5f\n", Math.round(ucinkovitost * Math.pow(10, 5)) / Math.pow(10, 5));
		
		//gradim matricu
		Set<String> jedinstveneVrijednostiStvarna = new TreeSet<>(mapaZnacajkaSveVrijednostiTest.get(ciljnaVarijabla));
		Set<String> jedinstveneVrijednostiPredvidena = new TreeSet<>(predvidanja);
		List<String> sortiranoZaMatricuStvarna = new ArrayList<>(jedinstveneVrijednostiStvarna);
		List<String> sortiranoZaMatricuPredvidena = new ArrayList<>(jedinstveneVrijednostiPredvidena);

		Map<Integer, Integer> indeksPojave = new TreeMap<>();
		for (int i = 0; i < (sortiranoZaMatricuStvarna.size() * sortiranoZaMatricuStvarna.size()); i++) {
			indeksPojave.put(i, 0);
		}
		
		for (int i = 0; i < sortiranoZaMatricuStvarna.size(); i++) {
			
			for (int j = 0; j < sortiranoZaMatricuPredvidena.size(); j++) {
				
				for (int k = 0; k < mapaZnacajkaSveVrijednostiTest.get(ciljnaVarijabla).size(); k++) {
				if ((mapaZnacajkaSveVrijednostiTest.get(ciljnaVarijabla).get(k).equals(sortiranoZaMatricuStvarna.get(i))) &&
					(predvidanja.get(k).equals(sortiranoZaMatricuPredvidena.get(j)))) {
					int pojave = indeksPojave.get((i * sortiranoZaMatricuStvarna.size()) + j);
					pojave++;
					indeksPojave.put((i * sortiranoZaMatricuStvarna.size()) + j, pojave);
					}	
				}
			}
		}
		System.out.println("[CONFUSION_MATRIX]:");
		for (int i = 0; i < sortiranoZaMatricuStvarna.size(); i++) {
			String ispisReda = new String();
			for (int j = 0; j < sortiranoZaMatricuStvarna.size(); j++) {
				
				ispisReda += (indeksPojave.get((i * sortiranoZaMatricuStvarna.size()) + j));
				ispisReda += " ";
				}
			System.out.println(ispisReda);
		}
		
		
		scanProvjera.close();
	}
	

}
