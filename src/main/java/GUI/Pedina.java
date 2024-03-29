package GUI;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import Gioco.Main;

public class Pedina {
	
	int casella;
	int posizioneX;
	int posizioneY;
	int height;
	int width;
	Image image;
	
	public Pedina(int num) {
		casella = 0;
		init(num);
		posizioneX = 0;
		posizioneY = 0;
		height = 50;
		width = 50;
	}
	
	public void init(int num) {
		String path = "/images/pedina";
		
		switch(num)  {
			case 0: path += "1.png";
			break;
			
			case 1: path += "2.png";
			break;
			
			case 2: path += "3.png";
			break;
			
			case 3: path += "4.png";
			break;
		}
		try {
			URL url = Main.class.getResource(path);
			image = ImageIO.read(url);
			image = image.getScaledInstance(30, 30, Image.SCALE_DEFAULT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getPosizioneX() {
		return posizioneX;
	}

	public void setPosizioneX(int posizioneX) {
		this.posizioneX = posizioneX;
	}

	public int getPosizioneY() {
		return posizioneY;
	}

	public void setPosizioneY(int posizioneY) {
		this.posizioneY = posizioneY;
	}

	public Image getImg() {
		return this.image;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void avanza(int numPlaces)  {
		casella += numPlaces;
		if(casella > 39)  {
			casella -= 40;
		}
	}
	
	public int getCasella()  {
		return casella;
	}
	
	public void setCasella(int casella)  {
		this.casella = casella;
	}
}
