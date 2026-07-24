package com.graficos;

import java.awt.Color;
import java.awt.Graphics;


import com.main.Game;

public class UI {
	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(20,4,70, 8);
		g.setColor(new Color(35,155,239));
		g.fillRect(20,4,(int)((Game.player.life/Game.player.maxLife)*70), 8);
		
	}
}
