package com.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.main.Game;

public class Tiles {
	
	private BufferedImage sprite;
	private int x,y;
	
	public Tiles(int x, int y, BufferedImage sprite) {
		this.x  = x;
		this.y =y;
		this.sprite = sprite;
	}
	
	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);
		
	public void render(Graphics g) {
		g.drawImage(sprite,x-Camera.x,y-Camera.y,null);
	}
}
