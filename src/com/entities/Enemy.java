package com.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.main.Game;
import com.world.Camera;
import com.world.World;

public class Enemy extends Entity {
//	nextInt(120-45)+45+1;
//	public double speed = Game.rand.nextDouble(1-0.6)+0.6+1;
	public double speed = 1;
	
	private int maskx = 8, masky=8, maskw = 10, maskh=10;
	
	private int life =10;
	
	private boolean isDamaged;
	private int damageFrames= 10,damageCurrent=0;
	
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	private BufferedImage[] spriteEnemy;
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		spriteEnemy = new BufferedImage[2];				
		spriteEnemy[0] = Game.spritesheet.getSprite(7*16, 32, 16, 16);
		spriteEnemy[1] = Game.spritesheet.getSprite(8*16, 32, 16, 16);

	}

	public void tick() {	
		if(isCollidingWithPlayer() == false) {
			if ((int) x < Game.player.getX() && World.isFree((int) (x + speed), this.getY())
					&& !isColliding((int) (x + speed), this.getY())) {
				x += speed;
			} else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed), this.getY())
					&& !isColliding((int) (x - speed), this.getY())) {
				x -= speed;
			}
			if ((int) y < Game.player.getY() && World.isFree(this.getX(), (int) (y + speed))
					&& !isColliding(this.getX(), (int) (y + speed))) {
				y += speed;
			} else if ((int) y > Game.player.getY() && World.isFree(this.getX(), (int) (y - speed))
					&& !isColliding(this.getX(), (int) (y - speed))) {
				y -= speed;
			}
			
		}else {
			if(Game.rand.nextInt(100) < 10) {
				Game.player.life-=Game.rand.nextInt(5);
				Game.player.isDamage = true;
				if(Game.player.life <= 0) {
					//Game Over
				}
				System.out.println("Life -> "+Game.player.life);
			}
		}
				frames++;
				if (frames == maxFrames) {
					frames = 0;
					index++;
					if (index > maxIndex) {
						index = 0;
					}
				}
				
				if(isDamaged) {
					this.damageCurrent++;
					if(this.damageCurrent == this.damageFrames) {
						this.damageCurrent =0;
						this.isDamaged  = false;
					}
				}
				
			this.collidingBullet();
			if(life <=0) {
				this.destroySelf();
				return;
			}
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void collidingBullet() {
		for(int i=0; i< Game.bullets.size();i++) {
			Entity e = Game.bullets.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isColliding(this, e)) {
					isDamaged = true;
					life--;
					Game.bullets.remove(i);
					return;
				}
			}
		}		
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX()+maskx, this.getY()+masky,maskw,maskh);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(),16,16);
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColliding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext+maskx, ynext+masky,maskw,maskh);
		for(int i=0;i<Game.enemies.size();i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) continue;
			
			Rectangle targetEnemy = new Rectangle(e.getX()+maskx, e.getY()+maskh, maskw,maskh);
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}
	
	public void render(Graphics g) {	
		if(!isDamaged) {
			g.drawImage(spriteEnemy[index], this.getX()-Camera.x, this.getY()-Camera.y, null);
		}else {
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX()-Camera.x, this.getY()-Camera.y, null);
		}
	}
	
	

}
