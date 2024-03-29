package AI;

import java.util.ArrayList;

//import com.sun.tools.doclets.formats.html.FrameOutputWriter;

import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import javafx.util.Pair;

public class Parser {

	public Parser() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean parsePropostaAcquisto(AnswerSets answers) {
		// TODO Auto-generated method stub
		boolean esito = false;
		int n = 0;
		for(AnswerSet a: answers.getAnswersets()){
			 System.out.println("AS n.: " + ++n + ": " + a);
			 String accetto = "accetto";
			 String as = a.toString();
			 if(as.contains(accetto)) {
				 esito = true;
			 }
		}
		return esito;

	}
	
	public boolean parseDecisionePuntata(AnswerSets answers) {
		// TODO Auto-generated method stub
		boolean esito = true;
		int n = 0;
		for(AnswerSet a: answers.getAnswersets()){
			 System.out.println("AS n.: " + ++n + ": " + a);
			 String nonPunta = "nonPunta";
			 String as = a.toString();
			 if(as.contains(nonPunta)) {
				 esito = false;
			 }
		}
		return esito;

	}
	
	public int parseDecisioneIniziale(AnswerSets answers)  {
		int esito = 0;
		int n = 0;
		for(AnswerSet a: answers.getAnswersets()){
			 System.out.println("AS n.: " + ++n + ": " + a);
			 String as = a.toString();
			 if(as.contains("dadi")) {
				 esito = 0;
				 System.out.println("DADI");
			 }
			 else if (as.contains("ipoteca"))  {
				 esito = 1;
				 System.out.println("IPOTECA");
			 }
			 else if (as.contains("costruire"))  {
				 esito = 2;
				 System.out.println("COSTRUISCO");
			 }
			 else if (as.contains("togliIpoteca"))  {
				 esito = 3;
				 System.out.println("TOLGO IPOTECA");
			 }
		}
		
		return esito;
	}
	
	public String parseUscitaPrigione(AnswerSets answers) {
		// TODO Auto-generated method stub
		String esito = "";
		int n = 0;
		for(AnswerSet a: answers.getAnswersets()){
			 System.out.println("AS n.: " + ++n + ": " + a);
			 String as = a.toString();
			 if(as.contains("token")) {
				 esito = "token";
				 System.out.println("TOKEN");
			 }
			 else if(as.contains("dadi")) {
				 esito = "dadi";
				 System.out.println("DADI");
			 }
			 else if (as.contains("paga")) {
				 esito = "paga";
				 System.out.println("PAGA");
			}
		}
		return esito;
 
	}
	
	public ArrayList<String> parseProposte(AnswerSets answers) {
		// TODO Auto-generated method stub
		//Ritorno un array list: A[0] indica il modo (Scambio o Acquisto o Niente), A[1] indica la casella da prendere,
		//A[2] indica la casella eventuale da scambiare
		ArrayList<String> esito = new ArrayList<String>();
		int n = 0;
		for(AnswerSet a: answers.getAnswersets()){
			 System.out.println("AS n.: " + ++n + ": " + a);
			 String as = a.toString();
			 if(as.contains("chiediAcquisto")) {
				 System.out.println("ACQUISTO COMPA");
				 esito.add("Acquisto");
				 ArrayList<Atomo> atomi = parseAtomi(as, "chiediAcquisto");
				 stampaAtomiconsiderati(atomi);
				 esito.add(atomi.get(0).get(0));
			 }
			 else if(as.contains("propostaScambio")) {
				 System.out.println("SCAMBIO COMPA");
				 esito.add("Scambio");
				 ArrayList<Atomo> atomi = parseAtomi(as, "propostaScambio");
				 stampaAtomiconsiderati(atomi);
				 esito.add(atomi.get(0).get(0));
				 esito.add(atomi.get(0).get(1));
			 }
			 else{
				 System.out.println("NIENTE COMPA");
				 esito.add("Niente");
			 }
		}
		return esito;

	}
	
	
	
	public boolean parseDecisioneScambioAcquisto(AnswerSets answers) {

		boolean esito = false;
		int n = 0;
		for(AnswerSet a: answers.getAnswersets()){
			 System.out.println("AS n.: " + ++n + ": " + a);
			 String as = a.toString();
			 if(as.contains("accetto")) {
				 System.out.println("ACCETTO SCAMBIO/ACQUISTO");
				 esito = true;
			 }
			 else if(as.contains("rifiuto")) {
				 System.out.println("RIFIUTO SCAMBIO/ACQUISTO");
			 }
		}
		return esito;
	}
	
	
	
	private void stampaAtomiconsiderati(ArrayList<Atomo> atomi) {
		// TODO Auto-generated method stub
		for (Atomo atomo : atomi) {
			System.out.println(atomo);
		}

	}
	
	private ArrayList<Atomo> parseAtomi(String as, String atomoDaIsolare) {

		ArrayList<Atomo> atomiDaIsolare = new ArrayList<Atomo>();
		String[] atomi = as.split("\\)");
		for (String atomo : atomi) {
			atomo = atomo.replaceAll("\\s", "");
			if(atomo.contains(atomoDaIsolare)) {
				atomo = atomo.substring(1);
				atomo = atomo.replace(atomoDaIsolare, "");
				atomo = atomo.replace("(", "");
				atomo = atomo.replace(")", "");
				atomo = atomo.replace("[", "");
				atomo = atomo.replace("]", "");
				String[] valori = atomo.split("\\,");
				atomiDaIsolare.add(new Atomo(atomoDaIsolare, valori));
			}
		}
		return atomiDaIsolare;
	}

	public String parseFaiPropostaScambio(AnswerSets answers) {
		return "";
	}

	public ArrayList<String> parseDecidiCosaCostruire(AnswerSets answers) {
		ArrayList<String> doveCostruire = new ArrayList<String>();
		for(AnswerSet a: answers.getAnswersets()){
			 String as = a.toString();
			 for(Atomo att : parseAtomi(as, "costruire"))  {
				 doveCostruire.add(att.toString().substring(10).replace(")",""));
			 }
		}
		return doveCostruire;
	}

	public Pair<Boolean,String> parseDecidiCosaIpotecare(AnswerSets answers) {
		boolean vendi = true;
		String casellaOSet = "";
		for(AnswerSet a: answers.getAnswersets()){
			 String as = a.toString();
			 for(Atomo att : parseAtomi(as, "vendi"))  {
				 casellaOSet = att.toString().substring(6).replace(")","").toUpperCase();
			 }
			 for(Atomo att : parseAtomi(as, "ipoteca"))  {
				 casellaOSet = att.toString().substring(8).replace(")","").toUpperCase();
				 vendi = false;
			 }
		}
		return new Pair<Boolean,String>(vendi,casellaOSet);
	}
	
	public String parseDecidiCosaDisipotecare(AnswerSets answers) {
		String esito = "";
		int n = 0;
		for(AnswerSet a: answers.getAnswersets()){
			 System.out.println("AS n.: " + ++n + ": " + a);
			 String as = a.toString();
			 if(as.contains("togli")) {
				 ArrayList<Atomo> atomi = parseAtomi(as, "togliIpoteca");
				 esito = atomi.get(0).get(0);
				 System.out.println(esito);
			 }
			
		}
		return esito;
	}

}
