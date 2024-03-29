package Elementi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

import Gioco.Main;

public class MazzoCarte {

	String nomiCartaChance[];			//es. vai in prigione
	String azioniCartaChance[];			//es. moveToLand
	String valoriCartaChance[];			//es. prigione
	
	String nomiCartaChest[];			//es. vai in prigione
	String azioniCartaChest[];			//es. moveToLand
	String valoriCartaChest[];			//es. prigione
	
	public MazzoCarte()  {
		
		nomiCartaChance = new String[11];
		azioniCartaChance = new String[11];
		valoriCartaChance = new String[11];
		
		nomiCartaChest = new String[11];
		azioniCartaChest = new String[11];
		valoriCartaChest = new String[11];
		
		try {
			InputStream istream = Main.class.getResourceAsStream("/cards.txt");
			Scanner scanner = new Scanner(istream);
			int posChance = 0;
			int posChest = 0;
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line.isEmpty())  {
					continue;
				}
				
				String[] infoCarta = line.split(",");
				
				String tipo = infoCarta[0];
				String nome = infoCarta[1];
				String azione = infoCarta[2];
				String valore = infoCarta[3];
				
				if(tipo.equals("CommunityChest"))  {
					
					nomiCartaChest[ posChest ] = nome;
					azioniCartaChest[ posChest ] = azione;
					valoriCartaChest[ posChest ] = valore;
					posChest++;
				} else  {
					
					nomiCartaChance[ posChance ] = nome;
					azioniCartaChance[ posChance ] = azione;
					valoriCartaChance[ posChance ] = valore;
					posChance++;
				}
			}
			
			scanner.close();
			//mischia();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * forse pi� avanti, non serve per forza
	 * 
	public void mischia()  {
		
	}
	*/
	
	//non esatta totalmente, ma va bene
	public String[] pescaChance()  {
		
		Random random = new Random();
		int n = random.nextInt(10);
		
		String[] carta = new String[3];
		carta[0] = nomiCartaChance[ n ];
		carta[1] = azioniCartaChance[ n ];
		carta[2] = valoriCartaChance[ n ];
		
		return carta;
	}
	
	public String[] pescaChest()  {
		
		Random random = new Random();
		int n = random.nextInt(10);
		
		String[] carta = new String[3];
		carta[0] = nomiCartaChest[ n ];
		carta[1] = azioniCartaChest[ n ];
		carta[2] = valoriCartaChest[ n ];
		
		return carta;
	}
}
