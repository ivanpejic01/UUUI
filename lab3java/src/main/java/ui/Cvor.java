package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cvor {
	
	private String znacajka; //znacajka cvora
	private Double entropija;
	private boolean list; //je li list
	private String granaRoditelj; //grana po kojoj dolazi do roditelja
	private String cvorRoditelj; //znacajka roditelja
	private List<String> graneDjeca; //lista grana po kojima se cvor siri
	private Map<String, Double> granaEntropija; //mapa grana i njihovih entropija
	private Map<String, String> granaDijete; //mapa grana i djece koja su na njima, tj, njihovih znacajki
	private Cvor roditelj; //cvor roditelj
	private int dubina; //dubina u stablu, bitna kasnije kod podrezivanja
	
	public Cvor(String znacajka) {
		this.znacajka = znacajka;
		this.entropija = 0.0;
		this.list = false;
		this.granaRoditelj = new String();
		this.cvorRoditelj = new String();
		this.graneDjeca = new ArrayList<>();
		this.granaEntropija = new HashMap<>();
		this.granaDijete = new HashMap<>();
		this.roditelj = null;
		dubina = 0;
	}
	 
	
	public void setZnacajka (String znacajka) {
		this.znacajka = znacajka;
	}
	public void setEntropija (Double entropija) {
		this.entropija = entropija;
	}

	public void setList(boolean list) {
		this.list = list;
	}
	
	public Double getEntropija() {
		return this.entropija;
	}
	
	public void dodajGranu (String grana) {
		this.graneDjeca.add(grana);
	}
	
	public String getGranaRoditelj() {
		return this.granaRoditelj;
	}
	
	public void setGranaRoditelj(String granaRoditelj) {
		this.granaRoditelj = granaRoditelj;
	}
	
	public List<String> getGraneDjeca() {
		return this.graneDjeca;
	}
	
	public boolean getList() {
		return this.list;
	}
	
	public String getZnacajka() {
		return this.znacajka;
	}
	
	public void setCvorRoditelj(String cvorRoditelj) {
		this.cvorRoditelj = cvorRoditelj;
	}
	
	public String getCvorRoditelj() {
		return this.cvorRoditelj;
	}

	public void setGranaEntropija(Map<String, Double> granaEntropija) {
		this.granaEntropija = granaEntropija;
	}
	
	public Map<String, Double> getGranaEntropija() {
		return this.granaEntropija;
	}
	
	public void setGranaDijete(Map<String, String> granaDijete) {
		this.granaDijete = granaDijete;
	}
	
	public void setRoditelj(Cvor roditelj) {
		this.roditelj = roditelj;
	}
	
	public Cvor getRoditelj() {
		return this.roditelj;
	}
	
	public Map<String, String> getGranaDijete() {
		return this.granaDijete;
	}
	
	public int getDubina() {
		return this.dubina;
	}
	
	public void setDubina(int dubina) {
		this.dubina = dubina;
	}
	@Override
	public String toString() {
		String djeca = new String();
		for (Map.Entry<String, String> entry: granaDijete.entrySet()) {
			djeca += entry.getKey() + " " + entry.getValue() + " ";
		}
		return "Znacajka = " + this.znacajka + " entropija = " + this.entropija + " grana roditelj = " + this.granaRoditelj +
				" list = " + this.list + " cvor roditelj " + this.cvorRoditelj;
	}
}
