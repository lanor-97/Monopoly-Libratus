package AI;

import java.util.ArrayList;
import java.util.Arrays;

public class Atomo extends ArrayList<String>{

	private String nomeAtomo;
	public Atomo(String nomeAtomo) {
		// TODO Auto-generated constructor stub
		super();
		this.setNomeAtomo(nomeAtomo);
	}
	public Atomo(String nomeAtomo, ArrayList<String> valori) {
		// TODO Auto-generated constructor stub
		super();
		this.setNomeAtomo(nomeAtomo);
		this.addAll(valori);
	}
	public Atomo(String nomeAtomo, String[] valori) {
		// TODO Auto-generated constructor stub
		super();
		this.setNomeAtomo(nomeAtomo);
		this.addAll(Arrays.asList(valori));
	}
	public String getNomeAtomo() {
		return nomeAtomo;
	}
	public void setNomeAtomo(String nomeAtomo) {
		this.nomeAtomo = nomeAtomo;
	}
}
