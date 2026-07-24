package com.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;


import com.main.Game;
import com.world.Camera;
import com.world.World;

public class Player extends Entity {

	public boolean right, left, up, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	
	private int damageFrames =0;
	private boolean hasGun=false;

	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	public double speed = 1.8;
	private BufferedImage playerDamage;
	public int ammo =0;
	public double life = 100,maxLife=100;
	
	public int mx,my;
	
	public boolean isDamage= false;
	public boolean shoot=false, mouseShoot = false;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);

		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		for (int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
		}

		for (int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
		}

	}

	public void tick() {

		if (right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x += speed;
		} else if (left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		if (up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			y -= speed;
		} else if (down && World.isFree(this.getX(),(int)(y+speed))) {
			moved = true;
			y += speed;
		}

		if (moved) {
			frames++;
			if (frames == maxFrames) {
				frames = 0;
				index++;
				if (index > maxIndex) {
					index = 0;
				}
			}
			
		}
		
		this.checkCollisionLifePack();
		this.checkCollisionAmmo();
		this.checkCollisionGun();
		
		if(isDamage) {
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames =0;
				isDamage = false;
			}
		}
		if(shoot) {
			shoot = false;
		   if(hasGun && ammo > 0) {
			ammo--;
			// Ammo and Shooting
			int dx =0;
			int px=0;
			int py =6;
			shoot = false;
			if(dir ==  right_dir) {
				px=20;
				dx = 1;
				py =6;
			}else {
				px=-9;
				dx = -1; 
		 	}
			BulletShoot bullets = new BulletShoot(this.getX()+px, this.getY()+py,BulletShoot.bulletsw,BulletShoot.bulletsh,null, dx, 0);
			Game.bullets.add(bullets);
			
		   }
		   		   
		}
		
		if(mouseShoot) {
//			System.out.println("Shooting...");
			mouseShoot = false;
			
		   if(hasGun && ammo > 0) {
			ammo--;
	
			int px=0;
			int py =6;
			double angle =0;
			if(dir ==  right_dir) {
				angle = Math.atan2(my-(this.getY()+py-Camera.y), mx-(this.getX()+px-Camera.x));
				px =20;
			}else {
				px = -9;
				angle = Math.atan2(my-(this.getY()+py-Camera.y), mx-(this.getX()+px-Camera.x));				 
		 	}
			
			double dx = Math.cos(angle);
			double dy =Math.sin(angle);
			
			BulletShoot bullets = new BulletShoot(this.getX()+px, this.getY()+py, BulletShoot.bulletsw, BulletShoot.bulletsh, null, dx, dy);
			Game.bullets.add(bullets);
			
		   }
		}
		
		if(life <=0) {
			life =0;
			Game.gameState = "GAME_OVER";
		}
		
		this.updateCamera();	
	}
	
	public void checkCollisionAmmo() {
		for(int i=0;i<Game.entities.size();i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColliding(this, atual)) {
					ammo+=100000; // 10
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void updateCamera() {
		Camera.x =Camera.clamp((this.getX() - Game.WIDTH/2),0,World.WIDTH*16 - Game.WIDTH);
		Camera.y =Camera.clamp((this.getY() - Game.HEIGHT/2),0,World.HEIGHT*16- Game.HEIGHT);
	}
	
	public void checkCollisionLifePack() {
		for(int i=0;i<Game.entities.size();i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Lifepack) {
				if(Entity.isColliding(this, atual)) {
					life+=10;
					if(life>100) life = 100;
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void checkCollisionGun() {
		for(int i=0;i<Game.entities.size();i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isColliding(this, atual)) {
					hasGun= true;
					System.out.println("Weapon: "+hasGun);
					Game.entities.remove(atual);
				}
			}
		}
	}

	public void render(Graphics g) {
		if(!isDamage) {
		if (dir == right_dir) {
			g.drawImage(rightPlayer[index], this.getX()-Camera.x, this.getY()-Camera.y, null);
			if(hasGun) {
				// Right weapon
				g.drawImage(Entity.GUN_RIGHT , this.getX()+10-Camera.x, this.getY()-Camera.y, null);
			}
		} else if (dir == left_dir) {
			g.drawImage(leftPlayer[index], this.getX()-Camera.x, this.getY()-Camera.y, null);
			if(hasGun) {
				// Left weapon
				g.drawImage(Entity.GUN_LEFT , this.getX()-10-Camera.x, this.getY()-Camera.y, null);
			}
		}
		
	  }else {
		if (dir == right_dir) {
		  g.drawImage(playerDamage, this.getX()-Camera.x, this.getY()-Camera.y,null);
		  if(hasGun) {
				g.drawImage(Entity.GUN_RIGHT_LOSE , this.getX()+10-Camera.x, this.getY()-Camera.y, null);
			}
		}else if (dir == left_dir) {
			  g.drawImage(playerDamage, this.getX()-Camera.x, this.getY()-Camera.y,null);
			if(hasGun) {
				// Left weapon
				g.drawImage(Entity.GUN_LEFT_LOSE , this.getX()-10-Camera.x, this.getY()-Camera.y, null);
			}
		}
		
	}
	}
}
