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
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.util.Set;
import java.lang.Math;

public class ID3 {
	
	public static Map<String, HashSet<String>> mapaZnacajkaVrijednosti = new HashMap<>();
	public static Map<String, ArrayList<String>> mapaZnacajkaSveVrijednosti = new HashMap<>();
	public static Map<Integer, Map<String, String>> mapaGrane = new HashMap<>();
	public static List<Cvor> listaCvorova = new ArrayList<>();
	public static List<Cvor> stablo = new ArrayList<>();
	public static String ciljnaVarijabla = new String();
	public static HashSet<String> vrijednostiCiljneVarijable = new HashSet<>();
	public static ArrayList<String> sveVrijednostiCiljneVarijable = new ArrayList<>();
	public static Map<String, HashSet<String>> mapaZnacajkaVrijednostiTest = new HashMap<>();
	public static Map<String, ArrayList<String>> mapaZnacajkaSveVrijednostiTest = new HashMap<>();
	
	

	
	public ID3() {
		
	}
	
	public void fit(File datotekaUcenje) throws FileNotFoundException {
		
		Scanner scanUcenje = new Scanner(datotekaUcenje, "UTF-8");
		
		String[] znacajke = scanUcenje.nextLine().split(",");
		for (int i = 0; i < znacajke.length; i++) {
			//System.out.println("Znacajka " + znacajke[i]);
		}
		String linijaString = new String();
		while(scanUcenje.hasNextLine()) {
			String[] linija = scanUcenje.nextLine().split(",");
			linijaString = "";
			for (int i = 0; i < linija.length; i++) {
				linijaString += linija[i] + ",";
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
				mapaZnacajkaVrijednosti.put(znacajke[i], vrijednostiZnacajke);
				mapaZnacajkaSveVrijednosti.put(znacajke[i], sveVrijednostiZnacajke);
			}
		
		}
		
		//System.out.println("Razlicite vrijednosti");
		for(Map.Entry<String, HashSet<String>> entry: mapaZnacajkaVrijednosti.entrySet()) {
			//System.out.println("kljuc = " + entry.getKey());
			for(String vrijednost: entry.getValue()) {
				//System.out.println("vrijednost = " + vrijednost);
			}
		}
		
		//System.out.println("Sve vrijednosti");
		for(Map.Entry<String, ArrayList<String>> entry: mapaZnacajkaSveVrijednosti.entrySet()) {
			//System.out.println("kljuc = " + entry.getKey());
			for(String vrijednost: entry.getValue()) {
				//System.out.println("vrijednost = " + vrijednost);
			}
		}
		ciljnaVarijabla = znacajke[znacajke.length - 1];
		sveVrijednostiCiljneVarijable = mapaZnacajkaSveVrijednosti.get(ciljnaVarijabla);
	
		
			List<Cvor> stablo = new ArrayList<>();
			List<Cvor> pomStablo = new ArrayList<>();
			Deque<Cvor> stabloProsirivanje = new LinkedList<>();
			Map<String, Map<String, Map<String, Integer>>> znacajkaZaVrijednost = new HashMap<>();
			Map<String, Double> znacajkaEntropija = new HashMap<>();
			Map<String, Double> znacajkaInformacijskaDobit = new HashMap<>();
			Map<String, List<Cvor>> znacajkaGrane = new HashMap<>();
			Map<String, Double> granaEntropija = new HashMap<>();
			Map<String, Map<String, Double>> znacajkaGranaEntropija = new HashMap<>();
			Map<String, Map<String, String>> znacajkaGranaDijete = new HashMap<>();
			Map<String, String> granaDijete = new HashMap<>();
			Map<String, Map<String, Double>> dijeteGranaEntropija = new HashMap<>();
			for (int i = 0; i < znacajke.length - 1; i++) {
				
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
					pomMapa.put(mapaZnacajkaSveVrijednosti.get(znacajke[i]).get(j), pomMapa2);
					znacajkaZaVrijednost.put(znacajke[i], pomMapa);
					if (broj == null) {
						broj = 0;
					}
					broj++;
					brojZaVrijednost.put(sveVrijednostiCiljneVarijable.get(j), broj);
				}
				
				Double entropija = 0.0;

				for (Map.Entry<String, Integer> entry: brojZaVrijednost.entrySet()) {
					Double razlomak = (Double.valueOf(entry.getValue()) / sveVrijednostiCiljneVarijable.size());
					entropija -= (razlomak * (Math.log(razlomak) / Math.log(2)));	
				}
				//System.out.println("Znacajka = " + znacajke[i] + " entropija " + entropija);
				znacajkaEntropija.put(znacajke[i], entropija);
				
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
						/*PREGLEDAJ!!!!!*/
						//System.out.println("Za malu entropiju " + entry2.getKey());
						if (!razlika.isEmpty()) {
							brojnik = 0;
							nazivnik += entry2.getValue();
							break;
						}
						else {
						nazivnik += entry2.getValue();
						}
					}
					
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

					//System.out.println("Vrijednost = " + entry.getKey() +  " mala entropija = " + malaEntropija);
					informacijskaDobit -= (Double.valueOf(nazivnik) / sveVrijednostiCiljneVarijable.size()) * malaEntropija;
					
				}
				System.out.println("Umecem za znacajku " + znacajke[i] + " sljedece vrijednosti");
				dijeteGranaEntropija.put(znacajke[i], granaEntropija);
				for(Map.Entry<String, Double> ulaz: granaEntropija.entrySet()) {
					System.out.println("grana = " + ulaz.getKey() + " entropija = " + ulaz.getValue());
				}
				znacajkaGranaEntropija.put(znacajke[i], new HashMap<>(granaEntropija));
				grana.setInformacijskaDobit(informacijskaDobit);
				//System.out.println("Info dobit = " + informacijskaDobit);
				znacajkaInformacijskaDobit.put(znacajke[i], informacijskaDobit);
				List<Cvor> pomLista = znacajkaGrane.get(znacajke[i]);
				if (pomLista == null) {
					pomLista = new ArrayList<>();
				}

				pomLista.add(grana);
				znacajkaGrane.put(znacajke[i], pomLista);
				
		
			}
			
			Double maksimum = -1.0;
			String maxZnacajka = null;
			String ispis = new String();
			Double infoDobit = 0.0;
			for (Map.Entry<String, Double> entry: znacajkaInformacijskaDobit.entrySet()) {
				infoDobit = entry.getValue();

				if (infoDobit > maksimum) {
					maksimum = infoDobit;
					maxZnacajka = entry.getKey();
				}
				
				ispis += "IG(" + entry.getKey() + ")=" + infoDobit + " ";
			}

			System.out.println(ispis.substring(0, ispis.length() - 1));
			//System.out.println("Max znacajka = " + maxZnacajka);
			Cvor korijen = new Cvor(maxZnacajka);
			korijen.setEntropija(znacajkaEntropija.get(maxZnacajka));
			korijen.setInformacijskaDobit(infoDobit);
			for (Map.Entry<String, Map<String, Integer>> entry: znacajkaZaVrijednost.get(maxZnacajka).entrySet()) {
				korijen.dodajGranu(entry.getKey());
			}
			korijen.setGranaEntropija(znacajkaGranaEntropija.get(maxZnacajka));
			korijen.setGranaRoditelj(null);
			korijen.setCvorRoditelj(null);
			korijen.setList(false);
			korijen.setRoditelj(null);
			//System.out.println(korijen.toString());
			stablo.add(korijen);
			pomStablo.add(korijen);
			
			System.out.println("Korijen stabla = " + korijen.toString());
			boolean imaSirenja = false;
			
			while(!imaSirenja) {
				
				Map<String, Double> dijeteID = new HashMap<>();
				Cvor cvorZaSirenje = pomStablo.get(0);
				pomStablo.remove(0);
				
				List<String> listaZnacajki = new ArrayList<>();
				if (!cvorZaSirenje.getList()) {
				granaDijete.clear();
				System.out.println("Sirim cvor " + cvorZaSirenje.toString());
				//za svaku granu
				for (String s: cvorZaSirenje.getGraneDjeca()) {
					//granaEntropija = new HashMap<>();
					Double entropijaGrane = cvorZaSirenje.getGranaEntropija().get(s);
					System.out.println("Znacajka = " + cvorZaSirenje.getZnacajka() + " grana = " + s + 
							" entropija grane = " + entropijaGrane);

					Cvor cvorZaDodavanje = new Cvor(null);
					if (entropijaGrane == 0.0) {
						Cvor noviCvor = new Cvor("");
						noviCvor.setList(true);
						noviCvor.setEntropija(0.0);
						noviCvor.setGranaRoditelj(s);
						noviCvor.setCvorRoditelj(cvorZaSirenje.getZnacajka());
						noviCvor.setRoditelj(cvorZaSirenje);
						/*RACUNANJE VRIJEDNOSTI LISTA*/
						stablo.add(noviCvor);
						System.out.println("Dodao sam list " + noviCvor.toString());
						}
					
					else {
						
							String znacajkaRoditeljFilter = cvorZaSirenje.getZnacajka();
							List<Cvor> stabloFilter = new ArrayList<>();
							stabloFilter.add(cvorZaSirenje);
							boolean imaRoditeljaFilter = true;
							
							Cvor stariCvorFilter = cvorZaSirenje;
							String staraZnacajkaFilter = cvorZaSirenje.getZnacajka();
							String staraGranaFilter = s;
							while (imaRoditeljaFilter) {
								 
								String pomZnacajkaFilter = znacajkaRoditeljFilter;
								
								Cvor roditeljFilter = stablo.stream().
								filter(cvorStablo -> cvorStablo.getZnacajka().equals(pomZnacajkaFilter)).
								findFirst().orElse(null);
								if (roditeljFilter == null) {
									imaRoditeljaFilter = false;
								}
								else {
									
									znacajkaRoditeljFilter = roditeljFilter.getCvorRoditelj();
									stabloFilter.add(roditeljFilter);
								}
							}
							
							for (int i = 0; i < znacajke.length - 1; i++) {
								String znacajka = znacajke[i];
								
								//if (stabloProsirivanje.stream().filter(cvor -> cvor.getZnacajka().equals(znacajka)).toList().size() == 0) {
								//System.out.println("Znacajka " + znacajka);
								if (stabloFilter.stream().filter(cvor -> cvor.getZnacajka().equals(znacajka)).toList().size() == 0) {
									Integer broj = 0;
									Integer nazivnik = 0;
									List<Integer> brojnici = new ArrayList<>();
									for (String cilj: mapaZnacajkaVrijednosti.get(ciljnaVarijabla)) {
										//System.out.println("Cilj = " + cilj);
										broj = znacajkaZaVrijednost.get(cvorZaSirenje.getZnacajka()).get(s).get(cilj);
										if (broj == null) {
											broj = 0;
										}
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
								System.out.println("Info dobit mala prije = " + entropijaGrane);
								String dijete = new String();
								//System.out.println("infor dobit mala prije " + infoDobitMala);
								Integer velikiNazivnik = 0;
								List<Double> umnosci = new ArrayList<>();
								for(Map.Entry<String, Map<String, Integer>> entry: znacajkaZaVrijednost.get(znacajka).entrySet()) {
									//granaEntropija.clear();
									System.out.println("Key " + entry.getKey());
									dijete = entry.getKey();
									Integer maliNazivnik = 0;
									//velikiNazivnik = 0;
									List<Integer> malaLista = new ArrayList<>();
									for (String cilj: mapaZnacajkaVrijednosti.get(ciljnaVarijabla)) {

											Integer brojnikID = 0;
											
											Map<String, String> pomMapaZaEntropiju = new HashMap<>();
											pomMapaZaEntropiju.put(znacajka, entry.getKey());
											String znacajkaRoditelj = cvorZaSirenje.getZnacajka();
											boolean imaRoditelja = true;

											String staraZnacajka = znacajka;
											String staraGrana = s;
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
											
											
											
											
											for (int k = 0; k < mapaZnacajkaSveVrijednosti.get(ciljnaVarijabla).size(); k++) {
												
											
											boolean podudaranje = true;
											
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
											
											System.out.println("BrojnikID = " + brojnikID);
											maliNazivnik += brojnikID;
											
											malaLista.add(brojnikID);
										}
										
										
										System.out.println("mali nazivnik " + maliNazivnik);
										velikiNazivnik += maliNazivnik;
										Double maliRazlomak = 0.0;
										Double novaEntropija = 0.0;
										if (malaLista.contains(0)) {
											malaEntropija = 0.0;
										}
										else {
										for (Integer noviBroj: malaLista) {
											maliRazlomak = Double.valueOf(noviBroj) / maliNazivnik;
											novaEntropija -= maliRazlomak * (Math.log(maliRazlomak) / Math.log(2));
										}
										}
										
										System.out.println("NOVA ENTROPIJA " + novaEntropija);
										System.out.println("Dodajem entropiju " + novaEntropija + " za " + entry.getKey() + 
												" i znacajku " + znacajka);
										granaEntropija.put(entry.getKey(), novaEntropija);
										umnosci.add(Double.valueOf(maliNazivnik) * novaEntropija);
										
									}
								//znacajkaGranaEntropija.put(znacajka, new HashMap<>(granaEntropija));
								System.out.println("Veliki nazivnik = " + velikiNazivnik);
								for (Double umnozak: umnosci) {
									infoDobitMala -= Double.valueOf(umnozak) / velikiNazivnik;
								}
								System.out.println("Mala infor dobit " + Math.round(infoDobitMala * Math.pow(10, 5)) / Math.pow(10, 5));
								dijeteID.put(znacajka, infoDobitMala);
								znacajkaGranaEntropija.put(znacajka, new HashMap<>(granaEntropija));
								}
							}
							
							String podIspis = new String();
							Double maxInfoDobit = 0.0;
							String maxZnacajkaMala = new String();
							for(Map.Entry<String, Double> entry: dijeteID.entrySet()) {
								podIspis += "IG(" + entry.getKey() + ")=" + entry.getValue() + " ";
								
								if (entry.getValue() > maxInfoDobit) {
									maxInfoDobit = entry.getValue();
									maxZnacajkaMala = entry.getKey();
								}
							}
							
							for(Map.Entry<String, Map<String, Double>> kraj: znacajkaGranaEntropija.entrySet()) {
								System.out.println("DIjete " + kraj.getKey());
								for(Map.Entry<String, Double> krajGrana: kraj.getValue().entrySet()) {
									System.out.println("Grana " + krajGrana.getKey() + " entropija " + krajGrana.getValue());
								}
							}
							if (podIspis.length() > 0) {
								}
							granaDijete.put(s, maxZnacajkaMala);
							System.out.println("Max znacajka mala " + maxZnacajkaMala);
							cvorZaDodavanje.setZnacajka(maxZnacajkaMala);
							cvorZaDodavanje.setInformacijskaDobit(maxInfoDobit);
							cvorZaDodavanje.setEntropija(22.5);
							cvorZaDodavanje.setGranaRoditelj(s);
							cvorZaDodavanje.setCvorRoditelj(cvorZaSirenje.getZnacajka());
							cvorZaDodavanje.setGranaEntropija(znacajkaGranaEntropija.get(maxZnacajkaMala));
							cvorZaDodavanje.setRoditelj(cvorZaSirenje);
							for (String grana: mapaZnacajkaVrijednosti.get(maxZnacajkaMala)) {
								cvorZaDodavanje.dodajGranu(grana);
							}

							if (maxZnacajkaMala.length() < 1) {
								cvorZaDodavanje.setList(true);
							}
							else {
								cvorZaDodavanje.setList(false);
							}
							stablo.add(cvorZaDodavanje);
							pomStablo.add(cvorZaDodavanje);
							System.out.println("Dodajem cvor " + cvorZaDodavanje.toString());
							}

					
					}
				znacajkaGranaDijete.put(cvorZaSirenje.getZnacajka(), new HashMap<>(granaDijete));
				if (pomStablo.size() == 0) {
					imaSirenja = true;
				}
				stabloProsirivanje.addLast(cvorZaSirenje);

				
				}
				
				
			}
			int brojacGrana = 0;
			System.out.println("[BRANCHES]:");
			for (Cvor cvor: stablo) {
				Map<String, String> pomMapaKraj = new HashMap<>();
				List<Cvor> put = new ArrayList<>();
				if (cvor.getList()) {
				put = new ArrayList<>();
				boolean imaRoditelja = true;
				String znacajkaRoditelj = cvor.getCvorRoditelj();
				Map<String, String> stabloGrana = new LinkedHashMap<>();
				Cvor stariCvor = cvor;
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
				
				//System.out.println("PRvi ispis puta " + ispisPuta);
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
		
		scanUcenje.close();
		
	}
	
	public static  void predict(File datotekaProvjera) throws FileNotFoundException {
		
Scanner scanProvjera = new Scanner(datotekaProvjera, "UTF-8");
		List<String> predvidanja = new ArrayList<>();
		String ispisPredvidanja = new String();
		
		String[] znacajke = scanProvjera.nextLine().split(",");
		for (int i = 0; i < znacajke.length; i++) {
			//System.out.println("Znacajka " + znacajke[i]);
		}
		String linijaString = new String();
		while(scanProvjera.hasNextLine()) {
			String[] linija = scanProvjera.nextLine().split(",");
			linijaString = "";
			for (int i = 0; i < linija.length; i++) {
				linijaString += linija[i] + ",";
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
		
		for (int i = 0; i < mapaGrane.size(); i++) {
			//System.out.println("Indeks " + i);
			for(Map.Entry<String, String> entry: mapaGrane.get(i).entrySet()) {
				//System.out.println("Kljuc " + entry.getKey() + " vrijednost " + entry.getValue());
			}
		}
		
		for (int i = 0; i < mapaZnacajkaSveVrijednostiTest.get(ciljnaVarijabla).size(); i++) {
			Map<String, String> testZnacajkaVrijednost = new HashMap<>();
			for (int j = 0; j < znacajke.length - 1; j++) {
				testZnacajkaVrijednost.put(znacajke[j], mapaZnacajkaSveVrijednostiTest.get(znacajke[j]).get(i));
			}
			
			List<String> kljuceviUlazTest = new ArrayList<>(testZnacajkaVrijednost.keySet());
			boolean imaPodudaranja = false;
			for (int j = 0; j < mapaGrane.size(); j++) {
				Map<String, String> granaZnacajkaVrijednost = new HashMap<>(mapaGrane.get(j));
				List<String> pomListaGrane = new ArrayList<>(mapaGrane.get(j).keySet());
				List<String> razlika = new ArrayList<>();
				boolean svaPodudaranja = true;
				for (String s: pomListaGrane) {
					if (kljuceviUlazTest.contains(s)) {
						razlika.add(s);
					}
				}
				
				for (String s: razlika) {
					//System.out.println("Razlika " + s);
				}


					
				for (String s: razlika) {
					//System.out.println("S = " + s);
					
					if (!testZnacajkaVrijednost.get(s).equals(granaZnacajkaVrijednost.get(s))) {
						//System.out.println("Laz");
						svaPodudaranja = false;
					}
				}
				
				if (svaPodudaranja) {
					//System.out.println("Konacna vrijednost za red = " + i + " " + mapaGrane.get(j).get(null));
					predvidanja.add(mapaGrane.get(j).get(null));
					imaPodudaranja = true;
				}
				
				

				
				
				
			}
			if (!imaPodudaranja) {
			Map<String, Integer> pojaveCiljneVarijable = new HashMap<>();
			List<String> sortiraneVrijednosti = new ArrayList<>(mapaZnacajkaSveVrijednosti.get(ciljnaVarijabla));
			Collections.sort(sortiraneVrijednosti);
		
			for (String s: sortiraneVrijednosti) {
				//System.out.println("Sortirana " + s);
				Integer broj = pojaveCiljneVarijable.get(s);
				if (broj == null) {
					broj = 1;
				} else {
					broj++;
				}
				pojaveCiljneVarijable.put(s, broj);
			}
			//System.out.println("Pojave ciljne");
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
		System.out.printf("[ACCURACY]: %.5f", Math.round(ucinkovitost * Math.pow(10, 5)) / Math.pow(10, 5));
		
		scanProvjera.close();
	}
	

}
