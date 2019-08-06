package Elementi;


import java.util.ArrayList;
import java.util.HashMap;

import AI.AIClass;
import AI.Writer;
import GUI.TavolaDaGioco;
import Gioco.CreatoreCaselle;

//singleton board
public class Board {

	Writer writer = new Writer();
	AIClass ai = new AIClass();
	
	
	ArrayList<Giocatore> giocatori;
	HashMap<Integer, String> mappa;
	HashMap<String, Casella> caselle;
	
	MazzoCarte mazzo;
	
	Banca banca;
	private Dadi dadi;
	
	//info relative allo stato del giocatore (turno corrente)
	int numDoppi;
	int giocatoreCorrenteIndex;
	Giocatore giocatoreCorrente;
	
	
	//simple constructor
	public Board(ArrayList<Giocatore> giocatori)  {
		this.giocatori = giocatori;
		this.setDadi(new Dadi());
		this.mazzo = new MazzoCarte();
		
		//status
		this.giocatoreCorrenteIndex = 0;
		this.giocatoreCorrente = giocatori.get(0);
		this.numDoppi = 0;
		
		this.creaMappa();
	}
	
	private void creaMappa()  {
		CreatoreCaselle creatore = new CreatoreCaselle();
		
		mappa = creatore.caricaMappa();
		caselle = creatore.getCaselle();
		banca = new Banca();
	}
	
	public Giocatore getGiocatoreCorrente()  {
		return giocatoreCorrente;
	}
	
	public ArrayList<Giocatore> getGiocatori()  {
		return giocatori;
	}
	
	//AZIONI GIOCATORE CORRENTE
	
	private int numPlaces;
	public int rollaDadi() {
		numPlaces = getDadi().tiraDadi();
		TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha rollato " + getDadi().toString());
		return numPlaces;
	}
	
	public void tiraDadi()  {

		
		if(getDadi().isDoppioNumero() && numDoppi == 2)  {
			TavolaDaGioco.aggiungiACronologia("Terzo numero doppio consecutivo,\nGiocatore " + giocatoreCorrente.getNome() + " finisce in prigione");
			
			giocatoreCorrente.setInPrigione(true);
			giocatoreCorrente.setPosizioneInTabella(10);
//			finisciTurno();
			return;
		} else if(getDadi().isDoppioNumero() && giocatoreCorrente.isInPrigione())  {
			TavolaDaGioco.aggiungiACronologia("Numero doppio, Giocatore " + giocatoreCorrente.getNome() + " esce dalla prigione");
			
			giocatoreCorrente.resetTurniPrigione();
			giocatoreCorrente.setInPrigione(false);
		} else if(giocatoreCorrente.isInPrigione() && giocatoreCorrente.getTurniPrigione() > 2)  {
			giocatoreCorrente.resetTurniPrigione();
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " esce di prigione dopo 3 turni");
			
//			finisciTurno();
			return;
		} else if(giocatoreCorrente.isInPrigione())  {
			
			giocatoreCorrente.incrTurniPrigione();
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " non � uscito di prigione");
			
//			finisciTurno();
			return;
		}
		
		int position = giocatoreCorrente.getPosizioneInTabella();
		
		//gestione "passa dal via"
		giocatoreCorrente.setPosizioneInTabella(position + numPlaces);
		if(giocatoreCorrente.getPosizioneInTabella() < position)  {
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " passa dal via");
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " riceve 2000 euro");
			
			banca.pagaPassaggioStart(giocatoreCorrente);
		}
		
		TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " arriva su: " + mappa.get(giocatoreCorrente.getPosizioneInTabella()));
		
		gestisciPosizione(mappa.get(giocatoreCorrente.getPosizioneInTabella()));
		
		//controlla se fine turno
		if(getDadi().isDoppioNumero())  {
			numDoppi++;
		}/* else  {
			finisciTurno();
			
		}*/
	}
	
	public void scambia(Casella casellaDaPrendere, Giocatore giocatoreCheFaPorposta, Giocatore giocatoreCheAccetta, Casella casellaDaLasciare)  {
		giocatoreCheAccetta.getCasellePossedute().remove(casellaDaPrendere);
		giocatoreCheFaPorposta.getCasellePossedute().add(casellaDaPrendere);
		casellaDaPrendere.setProprietario(giocatoreCheFaPorposta);
		giocatoreCheFaPorposta.getCasellePossedute().remove(casellaDaLasciare);
		giocatoreCheAccetta.getCasellePossedute().add(casellaDaLasciare);
		casellaDaLasciare.setProprietario(giocatoreCheAccetta);
		TavolaDaGioco.aggiungiACronologia(giocatoreCheFaPorposta.getNome() + " ha scambiato " + 
				casellaDaLasciare.getNome() + " con " + casellaDaPrendere.getNome());
	}
	
	public void compraCasellaAvversaria(Casella casella, Giocatore giocatoreCheFaPorposta, Giocatore giocatoreCheAccetta)  {
		giocatoreCheAccetta.getCasellePossedute().remove(casella);
		giocatoreCheFaPorposta.getCasellePossedute().add(casella);
		casella.setProprietario(giocatoreCheFaPorposta);
		giocatoreCheFaPorposta.diminuisciSoldi(casella.getPrezzoVendita());
		TavolaDaGioco.aggiungiACronologia(giocatoreCheFaPorposta.getNome() + " ha acquistato " + casella.getNome());
	}
	
	public void costruisci(String nome)  {
		Casella cas = caselle.get(nome);
		
		if(cas == null)  {
			TavolaDaGioco.aggiungiACronologia("Non � stato selezionato niente".toUpperCase());
//			System.out.println("Non � stato selezionato niente");
			return;
		}
		
		boolean permesso = banca.checkPossedimentoColore(cas, giocatoreCorrente);

		if(permesso)  {
			
			CasellaResidenziale casella = (CasellaResidenziale) cas;					
			
			if(casella.getNumeroCaseCostruite() + 1 > 5)  {
				TavolaDaGioco.aggiungiACronologia("C'� gi� un albergo, impossibile costruire ancora qui".toUpperCase());
				//System.out.println("C'� gi� un albergo, impossibile costruire ancora qui");
			} else if(giocatoreCorrente.getSoldi() < casella.getPrezzoCostruzioneCasa())  {
				TavolaDaGioco.aggiungiACronologia("Soldi non sufficienti".toUpperCase());
//				System.out.println("Soldi non sufficienti");
			} else  if(!banca.checkDifferenzaCaseColore(cas, giocatoreCorrente))  {
				TavolaDaGioco.aggiungiACronologia("Soldi non sufficienti".toUpperCase());
//				System.out.println("Devi costruire prima sulle altre caselle");
			} else  {
				TavolaDaGioco.aggiungiACronologia("Casa costruita con successo".toUpperCase());
//				System.out.println("Casa costruita con successo");
				
				casella.aggiungiCasa();
				giocatoreCorrente.diminuisciSoldi(casella.getPrezzoCostruzioneCasa());
			}
			
		} else  {
			TavolaDaGioco.aggiungiACronologia("Non hai tutto il set del colore della casella scelta".toUpperCase());
//			System.out.println("Non hai tutto il set del colore della casella scelta");
		}
	}
	
	public void esciDiPrigione(String modo)  {
		
		
		if(modo.equals("token"))  {
			if(giocatoreCorrente.hasTokenPrigione())  {
				giocatoreCorrente.usaTokenPrigione();
				TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha usato token prigione ed � ora libero");
				
			} else  {
				System.out.println("Giocatore " + giocatoreCorrente.getNome() + " non ha token prigione");
			}
			
		}
		else if (modo.equals("paga")) {
			giocatoreCorrente.diminuisciSoldi(50);
			giocatoreCorrente.setInPrigione(false);
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha pagato per uscire di prigione");
		}
	}
	
	public void ipoteca(String nomeCasella)  {
		Casella cas = caselle.get(nomeCasella);
		
		if(cas == null)  {
			System.out.println("Non � stato selezionato niente");
		} else if(cas.isIpotecata())  {
			System.out.println("La casella � gi� ipotecata");
		} else  {
			giocatoreCorrente.aumentaSoldi(cas.getPrezzoIpoteca());
			cas.setIpotecata(true);
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha ipotecato " + cas.getNome() + " per " + cas.getPrezzoIpoteca());
		}
	}
	
	public void finisciTurno()  {
		giocatoreCorrenteIndex++;
		
		if(giocatoreCorrenteIndex >= giocatori.size())  {
			giocatoreCorrenteIndex = 0;
		}
		
		numDoppi = 0;
		giocatoreCorrente = giocatori.get( giocatoreCorrenteIndex );
	}
	
	public void gestisciPosizione(String position)  {
//mia aggiunta		
		if(position.equals("Jail")) {
			giocatoreCorrente.setInPrigione(true);
			System.out.println("Ho arrestato");
		}
		
		if(position.equals("Start") || position.equals("FreeParking") || position.equals("Jail"))  {
			return;
		}
		
		if(position.equals("GoJail"))  {
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " finisce in prigione");
			giocatoreCorrente.setPosizioneInTabella(10);
			giocatoreCorrente.setInPrigione(true);
			
		} else if(position.equals("IncomeTax"))  {
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " paga " + CostantiGioco.INCOME_TAX + " di tassa");
			giocatoreCorrente.diminuisciSoldi(CostantiGioco.INCOME_TAX);
			
		} else if(position.equals("SuperTax"))  {
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " paga " + CostantiGioco.SUPER_TAX + " di super tassa");
			giocatoreCorrente.diminuisciSoldi(CostantiGioco.SUPER_TAX);
			
		} else if(position.equals("CommunityChest"))  {
			gestisciCartaPescata(mazzo.pescaChest());
			
		} else if(position.equals("Chance"))  {
			gestisciCartaPescata(mazzo.pescaChest());
			
		} else  {
			//capitato su una casella, se libera pu� acquistarla, altrimenti deve pagare la rendita
			Casella casella = caselle.get(position);
			
			if(!casella.haProprietario())  {
				//casella libera

				boolean vuoleComprare = false;
//PRIMA AI
				writer.writePropostaAcquisto(casella, giocatoreCorrente);
				vuoleComprare = ai.propostaAcquisto();
				System.out.println("Scelta Fatta: " + vuoleComprare);
	
//				DA SCOMMENTARE SE SI TOGLIE L'AIz
//				vuoleComprare = giocatoreCorrente.getSoldi() >= casella.getPrezzoVendita() && TavolaDaGioco.chiediSeVuoleComprare(casella);
				
				if(vuoleComprare)  {
					banca.vendiCasella(giocatoreCorrente, casella);
					TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha acquistato " + casella.getNome());
				} else  {
					banca.iniziaAsta(casella, giocatori);
				}
				
			} else if(giocatoreCorrente != casella.getProprietario()) {
				//casella occupata
				
				int importo = casella.getPrezzoTransito();
				giocatoreCorrente.diminuisciSoldi(importo);
				casella.getProprietario().aumentaSoldi(importo);
				
				TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " paga a " + casella.getProprietario().getNome() + " " + importo + "�");
			}
		}
	}
	
	public void gestisciCartaPescata(String[] carta)  {
		
		TavolaDaGioco.aggiungiACronologia(carta[0]);
		
		String effetto = carta[1];
		String valore  = carta[2];
		int val = Integer.parseInt(valore);
		
		if(effetto.equals("RiceviSoldi"))  {
			giocatoreCorrente.aumentaSoldi(val);
		
		} else if(effetto.equals("PagaSoldi"))  {
			giocatoreCorrente.diminuisciSoldi(val);
			
		} else if(effetto.equals("MuoviSuCasella"))  {
			int prevPos = giocatoreCorrente.getPosizioneInTabella();
			giocatoreCorrente.setPosizioneInTabella(val);
			if(val == 10)  {
				giocatoreCorrente.setInPrigione(true);
				return;
			}
			
			//gestione passa dal via
			if(prevPos > val)  {
				banca.pagaPassaggioStart(giocatoreCorrente);
			}
			gestisciPosizione(mappa.get(val));
			
		} else if(effetto.equals("MuoviCaselleIndietro"))  {
			giocatoreCorrente.avanza(-val);
			gestisciPosizione(mappa.get(giocatoreCorrente.getPosizioneInTabella()));
		} else if(effetto.equals("TokenPrigione"))  {
			giocatoreCorrente.addTokenPrigione();
		}
	}

	public Dadi getDadi() {
		return dadi;
	}

	public void setDadi(Dadi dadi) {
		this.dadi = dadi;
	}
	
	public int getGiocatoreCorrenteIndex()  {
		return giocatoreCorrenteIndex;
	}
	
	
	public void iniziaTurnoGiocatoreSuccessivo() {
		
		if(giocatoreCorrente.isInPrigione()) {
//SECONDA AI
			writer.writeUscitaPrigione(giocatoreCorrente);
			String modoUscita = ai.uscitaPrigione();
			System.out.println("Scelta Fatta: " + modoUscita);
			esciDiPrigione(modoUscita);
		}
		
	}
	
}