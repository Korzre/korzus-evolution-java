package com.main;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.graficos.Spritesheet;
import com.world.World;

public class Menu {

	
	public String[] options = {"novo jogo","carregar jogo","sair"};
	public int currentOptions =0;
	public int maxOption = options.length - 1;
	public boolean up, down, enter;
	
	public static boolean pause;
	public static boolean saveExists = false;
	public static boolean saveGame = false;
	
	public Spritesheet spritesheet;
	
	public void tick() {
		File file = new File("save.txt");
		if(file.exists()) {
			saveExists = true;
		}else {
			saveExists = false;
		}
		
		if(up) {
			up = false;
			currentOptions--;
			if(currentOptions < 0)
				currentOptions =maxOption;
		}
		
		if(down) {
			down = false;
			currentOptions++;
			if(currentOptions > maxOption)
				currentOptions =0;
		}
		
		
		if(enter) {
			enter = false;
			if(options[currentOptions] == "novo jogo" || options[currentOptions] == "continuar") {
				Game.gameState ="Normal";
				pause =false;
				file = new File("save.txt");
				file.delete();
			}else if(options[currentOptions] == "carregar jogo"){
				file = new File("save.txt");
				if(file.exists()) {
					String saver = loadGame(10);
					applySave(saver);
				}
			}else if(options[currentOptions] == "sair") {
				System.exit(1);
			}
		}
		
	}
	
	public static void applySave(String str) {
		String [] spl = str.split("/");
		for(int i=0; i < spl.length; i++) {
			String [] spl2 = spl[i].split(":");
			switch(spl2[0]) {
			
				case  "level":
					World.restartGame("level"+spl2[1]+".png");
					Game.gameState = "Normal";
					pause = false;
					break;
				case "life":
					Game.player.life = Integer.parseInt(spl2[1]);
					break;
			}
		}
	}
	
	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		if(file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while((singleLine = reader.readLine()) != null) {
						String[] trans = singleLine.split(":");
						char [] val = trans[1].toCharArray();
						trans[1] = "";
						for(int i=0; i < val.length; i++) {
							val[i] -= encode;
							trans[1] += val[i];
						}
						line +=trans[0];
						line +=":";
						line+=trans[1];
						line+="/";
					}
				}catch(IOException e) {}
			}catch(FileNotFoundException e) {}
		}
		
		return line;
	}
	
	
	public static void saveGame(String [] val1, int[] val2, int encode) {
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter("save.txt"));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<val1.length;i++) {
			String current = val1[i];
			current +=":";
			char [] value = Integer.toString(val2[i]).toCharArray();
			for(int n=0;n<value.length;n++) {
				value[n] += encode;
				current +=value[n];
			}
			try {
				write.write(current);
				if(i < val1.length - 1) {
					write.newLine();
				}
			}catch(IOException e) {}
		}
		try {
			write.flush();
			write.close();
		}catch(IOException e) {}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		spritesheet = new Spritesheet("/BackGround1.PNG");

		BufferedImage Wallpaper = spritesheet.getSprite(0,0, 610,378);
		
		g2.drawImage(Wallpaper, 0,0,Game.WIDTH*3,Game.HEIGHT*3,null);
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 36));
		g.drawString("Korzus: Evolution", (Game.WIDTH*Game.SCALE)/2-320, (Game.HEIGHT*Game.SCALE)/2-150);
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.ITALIC, 18));
		g.drawString("Uma história de korzre", (Game.WIDTH*Game.SCALE)/2-320, 60);
		
		// Options
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 24));
		if(pause == false) {
			g.drawString("Novo jogo", (Game.WIDTH*Game.SCALE)/2-320, 250);
		}else{
			g.drawString("Continuar", (Game.WIDTH*Game.SCALE)/2-320, 250);
		}
		
		g.drawString("Carregar jogo", (Game.WIDTH*Game.SCALE)/2-320, 300);
		g.drawString("Sair", (Game.WIDTH*Game.SCALE)/2-320, 350);
		
		if(options[currentOptions] == "novo jogo") {
			g.drawString("<", (Game.WIDTH*Game.SCALE)/2-350, 250);
		}else if(options[currentOptions] == "carregar jogo") {
			g.drawString("<", (Game.WIDTH*Game.SCALE)/2-350, 300);
		}else if(options[currentOptions] == "sair") {
			g.drawString("<", (Game.WIDTH*Game.SCALE)/2-350, 350);
		}
			
	}
	
}
