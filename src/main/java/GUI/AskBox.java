package GUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Elementi.Board;
import Elementi.Casella;
import Elementi.CasellaResidenziale;
import Elementi.Giocatore;
import Gioco.CreatoreCaselle;

public class AskBox extends JFrame {

	private static final long serialVersionUID = 1L;
	Board board;
	TextField spesa;

	JPanel panel = new JPanel();

	String territorioString;
	String nome;
	String metodo;
	Giocatore gioc;
	Casella casellaDaPrendere;

	HashSet<String> nomiCaselleEsistenti;

	public AskBox(final Board board) {
		super("Monopoly");
		this.setResizable(false);
		this.setPreferredSize(new Dimension(400, 400));
		this.setLayout(new BorderLayout());

		nomiCaselleEsistenti = new HashSet<String>();
		CreatoreCaselle.caricaNomiCaselle(nomiCaselleEsistenti);

		panel.setBackground(new Color(100, 100, 100));
		spesa = new TextField();

		this.setContentPane(panel);
		this.pack();
		this.board = board;
		this.setVisible(false);
	}

	public void chiediInfoCostruzione() {
		panel.removeAll();
		if (board.getGiocatoreVero().getNumSetsPosseduti() > 0) {
			for (String coloreEPrezzo : board.getGiocatoreVero().getSetsEPrezzi()) {
				String colore = coloreEPrezzo.split(",")[0];
				String prezzo = coloreEPrezzo.split(",")[1];
				final ArrayList<String> temp = new ArrayList<String>();
				temp.add(colore);

				Button btn = new Button(
						"Compra case su set " + traduciColore(colore) + " al prezzo di " + prezzo + "�");
				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						board.costruisci(temp);
					}
				});
				this.add(btn);
			}
			this.setVisible(true);
		} else {
			JLabel label = new JLabel("Non hai set di colori completi per costruire");
			this.add(label);
			this.setVisible(true);
		}
	}

	public String traduciColore(String col) {
		if (col.equals("brown"))
			return "A";
		else if (col.equals("lightblue"))
			return "B";
		else if (col.equals("pink"))
			return "C";
		else if (col.equals("orange"))
			return "D";
		else if (col.equals("red"))
			return "E";
		else if (col.equals("yellow"))
			return "F";
		else if (col.equals("green"))
			return "G";
		else
			return "H";
	}

	public int calcolaPrezzoVenditaColore(String col) {
		if (col.equals("brown"))
			return 500;
		else if (col.equals("lightblue"))
			return 750;
		else if (col.equals("pink"))
			return 1500;
		else if (col.equals("orange"))
			return 1500;
		else if (col.equals("red"))
			return 2250;
		else if (col.equals("yellow"))
			return 2250;
		else if (col.equals("green"))
			return 3000;
		else
			return 2000;
	}

	public void chiediInfoIpoteca() {
		panel.removeAll();

		final JComboBox<String> box = new JComboBox<String>();

		Button confermButtonIpoteca;
		confermButtonIpoteca = new Button("Conferma");
		this.add(box);
		this.add(confermButtonIpoteca);

		ArrayList<Casella> caselle = board.getGiocatoreCorrente().getCaselleNonIpotecate();
		Set<String> sets = new HashSet<String>();
		for (Casella c : caselle) {
			if (c instanceof CasellaResidenziale) {
				CasellaResidenziale cas = (CasellaResidenziale) c;
				if (cas.getNumeroCaseCostruite() > 0) {
					sets.add(cas.getColore());
					continue;
				}
			}
			box.addItem(c.getNome());
		}

		for (final String colore : sets) {
			Button btn = new Button("Vendi 1 casa (per casella) del set " + traduciColore(colore) + " al prezzo di "
					+ calcolaPrezzoVenditaColore(colore) + "�");

			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					board.vendiCase(colore);
					TavolaDaGioco.update(board.getGiocatoreVero());
				}
			});
			this.add(btn);
		}

		confermButtonIpoteca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.ipoteca((String) box.getSelectedItem());
				TavolaDaGioco.update(board.getGiocatoreVero());
			}
		});

		this.setVisible(true);
	}

	public void chiediComeUscirePrigione() {
		panel.removeAll();

		Button token = new Button("Utilizza token");
		Button paga = new Button("Paga la multa (500�)");

		token.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.esciDiPrigione("token");
			}
		});

		paga.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.esciDiPrigione("paga");
			}
		});

		this.add(token);
		this.add(paga);
		this.setVisible(true);
	}


	public static boolean chiediSeAccettaScambio(String casellaDaPrendere, String casellaDaScambiare,
			String proprietarioCasellaDaPrendere) {
		
		int answer = JOptionPane.showConfirmDialog(new JFrame(), proprietarioCasellaDaPrendere + ", vuoi scambiare "
				+ casellaDaPrendere + " con " + casellaDaScambiare + " ?");
		if (answer == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}

	public static boolean chiediSeAccettaOfferta(String casellaDaPrendere, int prezzo,
			String proprietarioCasellaDaPrendere) {
		int answer = JOptionPane.showConfirmDialog(new JFrame(),
				proprietarioCasellaDaPrendere + ", vuoi vendere " + casellaDaPrendere + " per " + prezzo + " ?");
		if (answer == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}

	public static void avversarioAccetta() {
//		JOptionPane.showConfirmDialog(new JFrame(), "L'avversario accetta!");
		JOptionPane.showMessageDialog(new JFrame(), "L'avversario accetta!");
	}

	public static void avversarioRifiuta() {
//		JOptionPane.showConfirmDialog(new JFrame(), "L'avversario rifiuta!");
		JOptionPane.showMessageDialog(new JFrame(), "L'avversario rifiuta!");
	}

	public static void offertaNonValida() {
//		JOptionPane.showConfirmDialog(new JFrame(), "L'avversario rifiuta!");
		JOptionPane.showMessageDialog(new JFrame(), "Offerta Non Valida!");
	}

	public void giocatorePerdente(String giocatorePerdente) {
		JOptionPane.showMessageDialog(new JFrame(), "Il giocatore " + giocatorePerdente + "ha perso!");
	}
	
	
	
	ArrayList<String> casellePresentiGiocatore = new ArrayList<String>();
	ArrayList<String> casellePresentiBot = new ArrayList<String>();
	AskBox temp;
	
	public void chiediScambio() {
		panel.removeAll();

		String text = "<html><pre>Caselle tue:<br/>";
		Button confermButtonProponiScambio;
		confermButtonProponiScambio = new Button("Conferma");

		casellePresentiGiocatore.clear();
		casellePresentiBot.clear();

		int count = 0;
		for (String c : board.getGiocatoreVero().getCaselleResidenziali()) {
			if (++count > 4) {
				text += "<br/>";
				count = 0;
			}
			text += c + ",";
			casellePresentiGiocatore.add(c);
		}
		text = text.substring(0, text.length() - 1);
		text += "<br/>";

		text += "Caselle avversario: <br/>";
		count = 0;
		for (String c : board.getGiocatoreBot().getCaselleResidenziali()) {
			if (++count > 4) {
				text += "<br/>";
				count = 0;
			}
			text += c + ",";
			casellePresentiBot.add(c);
		}
		text = text.substring(0, text.length() - 1);
		text += "<br/>";

		JLabel label = new JLabel(text + "</pre></html>");

		Box box = Box.createVerticalBox();
		JLabel caselleDare = new JLabel("Casella da dare: ");
		final JTextField caselleDaDare = new JTextField(20);

		JLabel caselleRicevere = new JLabel("Casella da ricevere: ");
		final JTextField caselleDaRicevere = new JTextField(20);

		box.add(caselleDare);
		box.add(caselleDaDare);
		box.add(caselleRicevere);
		box.add(caselleDaRicevere);
		this.add(box);
		this.add(label);
		this.add(confermButtonProponiScambio);
		this.setVisible(true);
		temp = this;
		
		confermButtonProponiScambio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String caselleToBot = caselleDaDare.getText();
				String caselleToYou = caselleDaRicevere.getText();
				if(caselleToYou.equals("")) {
					caselleToYou = "Casella non selezionata";
				}
				if(caselleToBot.equals("")) {
					caselleToBot = "Casella non selezionata";
				}

				if (casellePresentiGiocatore.contains(caselleToBot) && casellePresentiBot.contains(caselleToYou)) {
					Casella casellaDaScambiare = board.getCasellaDaNome(caselleToBot);
					Casella casellaDaPrendere = board.getCasellaDaNome(caselleToYou);

					boolean accettato = board.getGiocatoreBot().chiediSeAccettaScambio2Caselle(caselleToYou,
							caselleToBot, board.getGiocatori().get(board.getGiocatoreAvversarioIndex()),
							board.getGiocatoreCorrente());
					if (accettato) {
						board.scambia(casellaDaPrendere, board.getGiocatoreCorrente(),
								board.getGiocatori().get(board.getGiocatoreAvversarioIndex()), casellaDaScambiare);
						temp.setVisible(false);
						avversarioAccetta();
					}
					else {
						temp.setVisible(false);
						avversarioRifiuta();
					}
				} else {
					offertaNonValida();
				}
			}
		});
	}
	

	
	
	public void chiediVendita() {
		panel.removeAll();

		String text = "<html><pre>Soldi a disposizione:<br/>";
		Button confermButtonProponiScambio;
		confermButtonProponiScambio = new Button("Conferma");

		casellePresentiBot.clear();

		text += board.getGiocatoreCorrente().getSoldi();
		text += "<br/>";

		text += "Caselle avversario: <br/>";
		int count = 0;
		for (String c : board.getGiocatoreBot().getCaselleResidenziali()) {
			if (++count > 4) {
				text += "<br/>";
				count = 0;
			}
			text += c + ",";
			casellePresentiBot.add(c);
		}
		text = text.substring(0, text.length() - 1);
		text += "<br/>";

		JLabel label = new JLabel(text + "</pre></html>");

		Box box = Box.createVerticalBox();
		JLabel caselleDare = new JLabel("Soldi da dare: ");
		final JTextField caselleDaDare = new JTextField(20);

		JLabel caselleRicevere = new JLabel("Casella da ricevere: ");
		final JTextField caselleDaRicevere = new JTextField(20);

		box.add(caselleDare);
		box.add(caselleDaDare);
		box.add(caselleRicevere);
		box.add(caselleDaRicevere);
		this.add(box);
		this.add(label);
		this.add(confermButtonProponiScambio);
		this.setVisible(true);
		temp = this;
		
		confermButtonProponiScambio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String soldiDaDare = caselleDaDare.getText();
				String caselleToYou = caselleDaRicevere.getText();

				if(caselleToYou.equals("")) {
					caselleToYou = "Casella non selezionata";
				}
				if(soldiDaDare.equals("")) {
					soldiDaDare = "0";
				}
				int soldi = Integer.parseInt(soldiDaDare);
				
				if (casellePresentiBot.contains(caselleToYou) && soldi>=0) {
					Casella casellaDaPrendere = board.getCasellaDaNome(caselleToYou);

					boolean accettato = board.getGiocatoreBot().chiediSeAccettaVendita(caselleToYou, soldiDaDare, 
							board.getGiocatori().get(board.getGiocatoreAvversarioIndex()),
							board.getGiocatoreCorrente());
					if (accettato) {
						board.compraCasellaAvversaria(casellaDaPrendere, board.getGiocatoreCorrente(),
								board.getGiocatori().get(board.getGiocatoreAvversarioIndex()), soldi);
						
						temp.setVisible(false);
						avversarioAccetta();
					}
					else {
						temp.setVisible(false);
						avversarioRifiuta();
					}
				} else {
					offertaNonValida();
				}
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
