package ui;

import java.util.ArrayList;
import java.util.List;

public class Cvor {
	
	private String znacajka;
	private Double informacijskaDobit;
	private Double entropija;
	private boolean list;
	private String granaRoditelj;
	private String cvorRoditelj;
	private List<String> graneDjeca;
	private String vrijednostLista;
	
	public Cvor(String znacajka) {
		this.znacajka = znacajka;
		this.informacijskaDobit = 0.0;
		this.entropija = 0.0;
		this.list = false;
		this.granaRoditelj = new String();
		this.cvorRoditelj = new String();
		this.graneDjeca = new ArrayList<>();
		this.vrijednostLista = new String();
	}
	 
	
	public void setZnacajka (String znacajka) {
		this.znacajka = znacajka;
	}
	public void setEntropija (Double entropija) {
		this.entropija = entropija;
	}
	
	public void setInformacijskaDobit (Double informacijskaDobit) {
		this.informacijskaDobit = informacijskaDobit;
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
	
	public void setVrijednostLista(String vrijednostLista) {
		this.vrijednostLista = vrijednostLista;
	}
	
	@Override
	public String toString() {
		return "Znacajka = " + this.znacajka + " entropija = " + this.entropija + " grana roditelj = " + this.granaRoditelj +
				" list = " + this.list + " cvor roditelj " + this.cvorRoditelj;
	}
}
