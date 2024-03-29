package Elementi;


import java.util.ArrayList;
import java.util.HashMap;

import AI.Writer;
import GUI.AskBox;
import GUI.TavolaDaGioco;
import Gioco.CreatoreCaselle;
import javafx.util.Pair;

public class Board {

	Writer writer = new Writer();
	//AIClass ai = new AIClass();
	
	
	ArrayList<Giocatore> giocatori;
	HashMap<Integer, String> mappa;
	private HashMap<String, Casella> caselle;
	
	MazzoCarte mazzo;
	
	Banca banca;
	private Dadi dadi;
	
	//info relative allo stato del giocatore (turno corrente)
//	int numDoppi;
	int giocatoreCorrenteIndex;
	Giocatore giocatoreCorrente;
	private boolean giocoFinito = false;
	
	
	//simple constructor
	public Board(ArrayList<Giocatore> giocatori)  {
		this.giocatori = giocatori;
		this.setDadi(new Dadi());
		this.mazzo = new MazzoCarte();
		
		//status
		this.giocatoreCorrenteIndex = 0;
		this.giocatoreCorrente = giocatori.get(0);
//		this.numDoppi = 0;
		
		this.creaMappa();
	}	
	private void creaMappa()  {
		CreatoreCaselle creatore = new CreatoreCaselle();
		
		mappa = creatore.caricaMappa();
		setCaselle(creatore.getCaselle());
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
	public void rollaDadi() {
		
		controllaGiocoFinito();
		if (giocoFinito) {
			return;
		}
		numPlaces = getDadi().tiraDadi();
		TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha rollato " + getDadi().toString());
		boolean again = gestisciNumeroDadi();
		TavolaDaGioco.update(giocatoreCorrente);
		controllaGiocoFinito();
		if (giocoFinito) {
			return;
		}
		if (!again && !giocoFinito)  {
			finisciTurno();
			//gestione AI
			do  {
				controllaGiocoFinito();
				if (giocoFinito) {
					return;
				}
				int decisione = giocatoreCorrente.decidiCosaFare(this.giocatori);
				GiocatoreAI player = null;
				if(decisione != -1 && !giocoFinito) {
					player = (GiocatoreAI) giocatoreCorrente;
					player.setBoard(this);
					player.proposteDaFare(giocatori.get(getGiocatoreAvversarioIndex()));
					if(player.inPrigione) {
						String modo = player.voglioUscireDiPrigione();
						esciDiPrigione(modo);
					}
					controllaGiocoFinito();
					if (giocoFinito) {
						return;
					}
					
				}
				while (decisione != 0 && !giocoFinito)  {
					//IPOTECA
					if (decisione == 1)  {
						//pu� ritornare il colore (=> vendere una casa per casella del set
						//o il nome di una casella
						Pair<Boolean,String> daIpotecare = player.decidiCosaIpotecare(getGiocatoreBot());
						
						/*if (daIpotecare.getKey())  {
							this.vendiCase(daIpotecare.getValue());
						} else  {*/
							this.ipoteca(daIpotecare.getValue());
						//}
						break;
					}
					
					//COSTRUISCO
					else if (decisione == 2)  {
						this.costruisci(player.decidiCosaCostruire(this));
						break;
					}
					
					else if (decisione == 3) {
						this.disipoteca(player.decidiCosaDisipotecare(getGiocatoreBot()));
						break;
					}
					
					decisione = giocatoreCorrente.decidiCosaFare(this.giocatori);
					controllaGiocoFinito();
					if (giocoFinito) {
						return;
					}
					if (decisione > 3 || decisione < 0) {
						controllaGiocoFinito();
						if (giocoFinito) {
							return;
						}
						break;
					}
				}
				controllaGiocoFinito();
				if (giocoFinito) {
					return;
				}
				numPlaces = getDadi().tiraDadi();
				TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha rollato " + getDadi().toString());
				again = gestisciNumeroDadi();
				TavolaDaGioco.update(giocatoreCorrente);
				controllaGiocoFinito();
				if (giocoFinito) {
					return;
				}
			} while (again && !giocoFinito);
			controllaGiocoFinito();
			if (giocoFinito) {
				return;
			}
			finisciTurno();
		}
	}
	public boolean gestisciNumeroDadi()  {		
		if(getDadi().isDoppioNumero() && getGiocatoreCorrente().getNumeriDoppi() == 2)  {
			TavolaDaGioco.aggiungiACronologia("Terzo numero doppio consecutivo");
			TavolaDaGioco.aggiungiACronologia(giocatoreCorrente.getNome() + " finisce in prigione");
			giocatoreCorrente.setInPrigione(true);
			giocatoreCorrente.setPosizioneInTabella(10);
			giocatoreCorrente.incrTurniPrigione();
			getGiocatoreCorrente().azzeraNumeroDoppi();
			return false;
		} else if(getDadi().isDoppioNumero() && giocatoreCorrente.isInPrigione())  {
			TavolaDaGioco.aggiungiACronologia("Numero doppio, Giocatore " + giocatoreCorrente.getNome() + " esce dalla prigione");
			giocatoreCorrente.resetTurniPrigione();
			giocatoreCorrente.setInPrigione(false);
			getGiocatoreCorrente().azzeraNumeroDoppi();
		} else if(giocatoreCorrente.isInPrigione() && giocatoreCorrente.getTurniPrigione() > 2)  {
			giocatoreCorrente.resetTurniPrigione();
			giocatoreCorrente.setInPrigione(false);
			getGiocatoreCorrente().azzeraNumeroDoppi();
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " esce di prigione dopo 3 turni");
		} else if(giocatoreCorrente.isInPrigione())  {
			giocatoreCorrente.incrTurniPrigione();
			getGiocatoreCorrente().azzeraNumeroDoppi();
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " non � uscito di prigione");
			
			return false;
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
			giocatoreCorrente.aumentaNumeroDoppi();
			return true;
		}
		return false;
	}
	/*public void scambia(int soldiToBot, int soldiToYou, String[] caselleToBot, String[] caselleToYou)  {
		getGiocatoreBot().aumentaSoldi(soldiToBot);
		getGiocatoreBot().diminuisciSoldi(soldiToYou);
		getGiocatoreVero().aumentaSoldi(soldiToYou);
		getGiocatoreVero().diminuisciSoldi(soldiToBot);
		
		for (String s : caselleToBot)  {
			caselle.get(s).setProprietario(getGiocatoreBot());
		}
		for (String s : caselleToBot)  {
			caselle.get(s).setProprietario(getGiocatoreVero());
		}
	}*/
	public void compraCasellaAvversaria(Casella casella, Giocatore giocatoreCheFaPorposta, Giocatore giocatoreCheAccetta, int prezzo)  {
		giocatoreCheAccetta.getCasellePossedute().remove(casella);
//		giocatoreCheFaPorposta.getCasellePossedute().add(casella);
		giocatoreCheFaPorposta.aggiungiCasella(casella);
		casella.setProprietario(giocatoreCheFaPorposta);
		giocatoreCheFaPorposta.diminuisciSoldi(prezzo);
		TavolaDaGioco.aggiungiACronologia(giocatoreCheFaPorposta.getNome() + " ha acquistato " + casella.getNome());
	}
	public boolean costruisci(ArrayList<String> sets)  {
		for(String colore : sets)  {
			ArrayList<CasellaResidenziale> caselle = this.checkPossedimentoColore(colore, giocatoreCorrente);
			
			if(caselle != null)  {
				
				if(caselle.get(0).getNumeroCaseCostruite() + 1 > 5)  {
//					TavolaDaGioco.aggiungiACronologia("C'� gi� un albergo, impossibile costruire ancora qui".toUpperCase());
					AskBox.numeroMassimoCostruzioniSuperato();
					return false;
				} else if(giocatoreCorrente.getSoldi() < caselle.get(0).getPrezzoCostruzioneCasa() * caselle.size())  {
//					TavolaDaGioco.aggiungiACronologia("Soldi non sufficienti".toUpperCase());
					AskBox.soldiInsufficienti();
					return false;
				} else  {
					//TavolaDaGioco.aggiungiACronologia("Casa costruita con successo".toUpperCase());
					
					for (CasellaResidenziale c : caselle)  {
						c.aggiungiCasa();
						giocatoreCorrente.diminuisciSoldi(c.getPrezzoCostruzioneCasa());
					}
					return true;
				}
				
			}
		}
		return false;
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
			if(giocatoreCorrente.isInPrigione())  {
				giocatoreCorrente.diminuisciSoldi(500);
				giocatoreCorrente.setInPrigione(false);
				TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha pagato per uscire di prigione");
			} else  {
				System.out.println("Non sei in prigione");
			}
		}
	}
	public void ipoteca(String nomeCasella)  {
		Casella cas = getCaselle().get(nomeCasella);
		
		if(!cas.equals(null) && !cas.isIpotecata())  {
			giocatoreCorrente.aumentaSoldi(cas.getPrezzoIpoteca());
			cas.setIpotecata(true);
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha ipotecato " + cas.getNome() + " per " + cas.getPrezzoIpoteca());
		}
	}
	public void disipoteca(String nomeCasella)  {
		
		System.out.println(nomeCasella);
		if(nomeCasella.equals("")) {
			return;
		}
		
		Casella cas = getCaselle().get(nomeCasella);
		
		if(cas == null)  {
			System.out.println("Non � stato selezionato niente");
		} else if(!cas.isIpotecata())  {
			System.out.println("La casella � gi� disipotecata");
		} else  {
			giocatoreCorrente.diminuisciSoldi(cas.getPrezzoIpoteca());
			cas.setIpotecata(false);
			TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha tolto l'ipoteca a " + cas.getNome() + " per " + cas.getPrezzoIpoteca());
		}
	}
	/*public void vendiCase(String colore)  {
		for(CasellaResidenziale c : getGiocatoreCorrente().getCaselleResidenzialiOggetto())  {
			if (c.getColore().equals(colore))  {
				c.rimuoviCasa();
				getGiocatoreCorrente().aumentaSoldi(c.getPrezzoCostruzioneCasa()/2);
			}
		}
	}*/
	public void finisciTurno()  {
		giocatoreCorrenteIndex++;
		
		if(giocatoreCorrenteIndex >= giocatori.size())  {
			giocatoreCorrenteIndex = 0;
		}
		
		giocatoreCorrente.azzeraNumeroDoppi();
		giocatoreCorrente = giocatori.get( giocatoreCorrenteIndex );
	}
	public void gestisciPosizione(String position)  {
		
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
			Casella casella = getCaselle().get(position);
			
			if(!casella.haProprietario())  {
				//casella libera

				boolean vuoleComprare = false;
				
				if (giocatoreCorrente.getNome().equals("BOT1")) {
					vuoleComprare = getGiocatoreBot().decidiSeComprareCasella(casella, getGiocatoreBot());
				}
				else {
				//Se sei il giocatore normale gestisci la proposta
					vuoleComprare = giocatoreCorrente.getSoldi() >= casella.getPrezzoVendita() && TavolaDaGioco.chiediSeVuoleComprare(casella);
				}
	
				if(vuoleComprare)  {
					banca.vendiCasella(giocatoreCorrente, casella);
					TavolaDaGioco.aggiungiACronologia("Giocatore " + giocatoreCorrente.getNome() + " ha acquistato " + casella.getNome());
				} else  {
					banca.iniziaAsta(casella, giocatori, isAITurn());

					for (Giocatore giocatore : giocatori) {
						if (giocatore.getSoldi() <= 0) {
							giocoFinito = true;
						}
					}
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
				giocatoreCorrente.setPosizioneInTabella(10);
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
	public int getGiocatoreAvversarioIndex()  {
		if(giocatoreCorrenteIndex == 0) {
			return 1;
		}
		return 0;
	}

	public boolean isAITurn()  {
		if(giocatoreCorrente instanceof GiocatoreAI) {
			return true;
		}
		return false;
	}
	public Giocatore getGiocatoreVero()  {
		return this.giocatori.get(0);
	}
	public GiocatoreAI getGiocatoreBot()  {
		return (GiocatoreAI) this.giocatori.get(1);
	}
	public ArrayList<CasellaResidenziale> checkPossedimentoColore(String colore, Giocatore giocatore)  {
		ArrayList<CasellaResidenziale> caselle = new ArrayList<CasellaResidenziale>();
		for(Casella c : giocatore.getCasellePossedute())  {
			if(!(c instanceof CasellaResidenziale))  {
				continue;
			}
			
			CasellaResidenziale cr = (CasellaResidenziale) c;
			String col2 = cr.getColore();
			if(colore.equals(col2))  {
				caselle.add(cr);
			}
		}
		if(caselle.size() == 3)  {
			return caselle;
		} else if(caselle.size() == 2 && (colore.equals("brown") || colore.equals("blue")))  {
			return caselle;
		}
		
		return null;
	}
	
	
	public HashMap<String, Casella> getCaselle() {
		return caselle;
	}
	public void setCaselle(HashMap<String, Casella> caselle) {
		this.caselle = caselle;
	}
	public boolean isColore(String col)  {
		return col.equals("brown") || col.equals("lightblue") || col.equals("pink") || 
				col.equals("orange") || col.equals("red") || col.equals("yellow") || 
				col.equals("green") || col.equals("blue");
	}
	
	public Casella getCasellaDaNome(String nome) {
		nome = nome.toUpperCase();
		return caselle.get(nome);
	}
	
	public void scambia(Casella casellaDaPrendere, Giocatore giocatoreCheFaPorposta, Giocatore giocatoreCheAccetta, Casella casellaDaLasciare)  {
		giocatoreCheAccetta.getCasellePossedute().remove(casellaDaPrendere);
//		giocatoreCheFaPorposta.getCasellePossedute().add(casellaDaPrendere);
		giocatoreCheFaPorposta.aggiungiCasella(casellaDaPrendere);
		casellaDaPrendere.setProprietario(giocatoreCheFaPorposta);
		giocatoreCheFaPorposta.getCasellePossedute().remove(casellaDaLasciare);
//		giocatoreCheAccetta.getCasellePossedute().add(casellaDaLasciare);
		giocatoreCheAccetta.aggiungiCasella(casellaDaLasciare);
		casellaDaLasciare.setProprietario(giocatoreCheAccetta);
		TavolaDaGioco.aggiungiACronologia(giocatoreCheFaPorposta.getNome() + " ha scambiato " + 
				casellaDaLasciare.getNome() + " con " + casellaDaPrendere.getNome());
	}
	public boolean isGiocoFinito() {
		return giocoFinito;
	}
	public void setGiocoFinito(boolean giocoFinito) {
		this.giocoFinito = giocoFinito;
	}
	
	public void controllaGiocoFinito() {
		for (Giocatore giocatore : giocatori) {
			if (giocatore.getSoldi() <= 0) {
				giocoFinito = true;
			}
		}
	}
}