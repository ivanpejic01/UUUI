package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	public static List<Cvor> listaCvorova = new ArrayList<>();
	public static List<Cvor> stablo = new ArrayList<>();
	public static String ciljnaVarijabla = new String();
	public static HashSet<String> vrijednostiCiljneVarijable = new HashSet<>();
	public static ArrayList<String> sveVrijednostiCiljneVarijable = new ArrayList<>();
	

	
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
			Map<String, Map<String, Map<String, Integer>>> znacajkaZaVrijednost = new HashMap<>();
			Map<String, Double> znacajkaEntropija = new HashMap<>();
			Map<String, Double> znacajkaInformacijskaDobit = new HashMap<>();
			Map<String, List<Cvor>> znacajkaGrane = new HashMap<>();
			Map<String, Double> granaEntropija = new HashMap<>();
			List<Cvor> cvorovi = new ArrayList<>();
			Map<String, List<String>> znacajkaNazivGrane = new HashMap<>();
			
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
			/*Cvor cvor = new Cvor(minZnacajka);
			cvor.setList(false);
			cvor.setEntropija(znacajkaEntropija.get(minZnacajka));
			cvor.setInformacijskaDobit(znacajkaInformacijskaDobit.get(minZnacajka));
			cvorovi.add(cvor);*/
			//System.out.println(ispis.substring(0, ispis.length() - 1));
			//System.out.println("Max znacajka = " + maxZnacajka);
			Cvor korijen = new Cvor(maxZnacajka);
			korijen.setEntropija(znacajkaEntropija.get(maxZnacajka));
			korijen.setInformacijskaDobit(infoDobit);
			for (Map.Entry<String, Map<String, Integer>> entry: znacajkaZaVrijednost.get(maxZnacajka).entrySet()) {
				korijen.dodajGranu(entry.getKey());
			}
			korijen.setGranaRoditelj(null);
			korijen.setCvorRoditelj(null);
			korijen.setList(false);
			//System.out.println(korijen.toString());
			stablo.add(korijen);
			pomStablo.add(korijen);
			
			//System.out.println("Korijen stabla = " + korijen.toString());
			boolean imaSirenja = false;
			
			while(!imaSirenja) {
				
				Map<String, Double> dijeteID = new HashMap<>();
				Cvor cvorZaSirenje = pomStablo.get(0);
				pomStablo.remove(0);
				
				List<String> listaZnacajki = new ArrayList<>();
				if (!cvorZaSirenje.getList()) {

				//System.out.println("Sirim cvor " + cvorZaSirenje.toString());
				//za svaku granu
				for (String s: cvorZaSirenje.getGraneDjeca()) {
					Double entropijaGrane = granaEntropija.get(s);
					//System.out.println("Grana = " + s);
					//System.out.println("Entropija grane = " + entropijaGrane);
					Cvor cvorZaDodavanje = new Cvor(null);
					if (entropijaGrane == 0.0) {
						Cvor noviCvor = new Cvor("");
						noviCvor.setList(true);
						noviCvor.setEntropija(0.0);
						noviCvor.setGranaRoditelj(s);
						noviCvor.setCvorRoditelj(cvorZaSirenje.getZnacajka());
						/*RACUNANJE VRIJEDNOSTI LISTA*/
						
						
						stablo.add(noviCvor);
						}
					
					else {
						

							for (int i = 0; i < znacajke.length - 1; i++) {
								String znacajka = znacajke[i];
								
								if (stablo.stream().filter(cvor -> cvor.getZnacajka().equals(znacajka)).toList().size() == 0) {
								//	System.out.println("Znacajka " + znacajka);

									Integer broj = 0;
									Integer nazivnik = 0;
									List<Integer> brojnici = new ArrayList<>();
									for (String cilj: mapaZnacajkaVrijednosti.get(ciljnaVarijabla)) {
										//System.out.println(cilj);
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
									
								Double infoDobitMala = malaEntropija;
								String dijete = new String();
								//System.out.println("infor dobit mala prije " + infoDobitMala);
								for(Map.Entry<String, Map<String, Integer>> entry: znacajkaZaVrijednost.get(znacajka).entrySet()) {
									
									//System.out.println("Key " + entry.getKey());
									dijete = entry.getKey();
									Integer maliNazivnik = 0;
									List<Integer> malaLista = new ArrayList<>();
									for (String cilj: mapaZnacajkaVrijednosti.get(ciljnaVarijabla)) {
											List vrijednostiKorijena = mapaZnacajkaSveVrijednosti.get(cvorZaSirenje.getZnacajka());
											List vrijednostiDjeteta = mapaZnacajkaSveVrijednosti.get(znacajka);
											List vrijednostiCilja = mapaZnacajkaSveVrijednosti.get(ciljnaVarijabla);
											Integer brojnikID = 0;
											
											
											for (int k = 0; k < vrijednostiKorijena.size(); k++) {
												
											if (vrijednostiKorijena.get(k).equals(s) && 
													vrijednostiDjeteta.get(k).equals(entry.getKey()) &&
													vrijednostiCilja.get(k).equals(cilj)) {
												brojnikID += 1;
											}
											
											}
											
											//System.out.println("BrojnikID = " + brojnikID);
											maliNazivnik += brojnikID;
											malaLista.add(brojnikID);
										}
										//System.out.println("mali nazivnik " + maliNazivnik);
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
										
										//System.out.println("NOVA ENTROPIJA " + novaEntropija);
										granaEntropija.put(entry.getKey(), novaEntropija);
										infoDobitMala -= (Double.valueOf(maliNazivnik) / nazivnik) * novaEntropija;

									}
								//System.out.println("Mala infor dobit " + infoDobitMala);
								dijeteID.put(znacajka, infoDobitMala);
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
							if (podIspis.length() > 0) {
							//System.out.println(podIspis.substring(0, podIspis.length() - 1));
								}
							
							cvorZaDodavanje.setZnacajka(maxZnacajkaMala);
							cvorZaDodavanje.setInformacijskaDobit(maxInfoDobit);
							cvorZaDodavanje.setEntropija(22.5);
							cvorZaDodavanje.setGranaRoditelj(s);
							cvorZaDodavanje.setCvorRoditelj(cvorZaSirenje.getZnacajka());
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
							//System.out.println("Dodajem cvor " + cvorZaDodavanje.toString());
							}

					
					}
					
				//System.out.println("Velicina pom stabla = " + pomStablo.size());
				if (pomStablo.size() == 0) {
					imaSirenja = true;
				}
				}
				
				
				
			}
			System.out.println("[BRANCHES]:");
			for (Cvor cvor: stablo) {
				List<Cvor> put = new ArrayList<>();
				if (cvor.getList()) {
				put = new ArrayList<>();
				boolean imaRoditelja = true;
				
				String znacajkaRoditelj = cvor.getCvorRoditelj();
				Map<String, String> stabloGrana = new HashMap<>();
				Cvor stariCvor = cvor;
				while (imaRoditelja) {
					String pomZnacajka = znacajkaRoditelj;
					
					Cvor roditelj = stablo.stream().
					filter(cvorStablo -> cvorStablo.getZnacajka().equals(pomZnacajka)).
					findFirst().orElse(null);
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
				for (Map.Entry<String, String> entry: stabloGrana.entrySet()) {
					ispisPuta += brojac + ":" + entry.getKey() + "=" + entry.getValue() + " ";
					brojac++;
					for (int i = 0; i < mapaZnacajkaSveVrijednosti.get(entry.getKey()).size(); i++) {
						if (mapaZnacajkaSveVrijednosti.get(entry.getKey()).get(i).equals(entry.getValue())) {
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
				for (Integer indeks: indeksi) {
					int pojave = Collections.frequency(indeksi, indeks);
					if (pojave > 1) {
						indeksCiljne = indeksi.get(indeksi.indexOf(indeks));
						break;
						}
					}
				}
				
				ispisPuta += sveVrijednostiCiljneVarijable.get(indeksCiljne);
				System.out.println(ispisPuta);

				}
			}
		
		scanUcenje.close();
		
	}
	
	public static  void predict() {
		
	}
	
	public static Double izracunajEntropiju() {
		
		
		return 0.0;
	}

}
