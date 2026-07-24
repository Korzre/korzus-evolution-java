package com.main;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import com.entities.*;
import com.graficos.*;
import com.world.World;

@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

	private JFrame frame;
	private Thread thread;
	private boolean isRunning;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static int SCALE = 3;
	private BufferedImage image;
	public static ArrayList<Entity> entities;
	public static ArrayList<Enemy> enemies;
	public static Spritesheet spritesheet;
	public static World world;
	public static ArrayList<BulletShoot> bullets;
	
	boolean space, ctrl = false;
	
	public Menu menu;
	private int CURL_LEVEL = 1, MAX_LEVEL=2;
	
	public static Player player;
	public static Random rand;
	public UI ui;
	
	public static String gameState ="Menu";
	
	private boolean showMessageGameOver=true;
	private int framesGameOver=0;
	
	private boolean restartGame =false;
	
	public boolean saveGame = false;
	
	public Game() {
		initFrame();
		
		//Inicializando objetos.
		
		rand = new Random();
		ui = new UI();			
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_BGR);				
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
		world = new World("/level1.png");		
		entities.add(player);
		menu = new Menu();
	}
	
	public void initFrame() {
		addKeyListener(this);
		addMouseListener(this);
		frame = new JFrame("v1.0");
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		frame.add(this);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		try {
			thread.join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void tick() {
		if(gameState == "Normal") {
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level", "life"};
				int[] opt2 = {this.CURL_LEVEL, (int) player.life};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo salvo!");
			}
			this.restartGame = false;
		for(int i=0; i<entities.size();i++) {
			Entity e = entities.get(i);
			e.tick();
			
		}
	
		if(Game.enemies.size() == 0) {
			//System.out.println("Next Level!");
			CURL_LEVEL++;
			if(CURL_LEVEL > MAX_LEVEL) {
				CURL_LEVEL = 1;
			}
			
			String newWorld = "level"+CURL_LEVEL+".png";
			World.restartGame(newWorld);
		}
		
		for(int i=0;i<bullets.size();i++) {
			bullets.get(i).tick();
		}
			
		}else if(gameState == "GAME_OVER") {
			
			this.framesGameOver++;		
			
		if(this.framesGameOver  == 45) { 
			
			this.framesGameOver =0;		
			
			if(this.showMessageGameOver) {				
				this.showMessageGameOver =false;				
			}else {				
				this.showMessageGameOver = true;
			}
		}		
		if(restartGame) {
			CURL_LEVEL = 1;
			this.restartGame = false;
			gameState ="Normal";
			String newWorld = "level"+CURL_LEVEL+".png";
			World.restartGame(newWorld);
		}
		
		}else if(gameState == "Menu") {
			  menu.tick();
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.getGraphics();
		
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
		
		world.render(g);
		
		for(int i=0; i<entities.size();i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for(int i=0;i<bullets.size();i++) {
			bullets.get(i).render(g);
		}
		
		ui.render(g);
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image,0,0,WIDTH*SCALE, HEIGHT*SCALE, null);
		g.setFont(new Font("arial", Font.BOLD, 24));
		g.setColor(Color.WHITE);
		g.drawString("AMMO: "+player.ammo, 598,28);
		
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD,24));
		g.drawString((int)Game.player.life+"/"+(int)Game.player.maxLife,120,32);
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g2.setFont(new Font("arial", Font.BOLD, 40));
			g.setColor(Color.YELLOW);
			g2.drawString("Assim mesmo morreste?!", (WIDTH*SCALE)/2-270, (HEIGHT*SCALE)/2-10);
			g2.drawString("GAME OVER", (WIDTH*SCALE)/2-270, (HEIGHT*SCALE)/2-60);
			BufferedImage looser;
			looser = Game.spritesheet.getSprite(48+16+16, 32, 16, 16);
			g2.drawImage(looser, (WIDTH*SCALE)/2-290, (HEIGHT*SCALE)/2,80,80, null );
			
			if(showMessageGameOver) {
				g2.setFont(new Font("arial", Font.BOLD, 14));
				g.setColor(Color.white);
				g2.drawString("> Pressione o enter para continuar...", WIDTH*SCALE-WIDTH-30, (HEIGHT*SCALE)-60);		
			}
			
		}else if(gameState == "Menu") {
			Graphics2D g2 = (Graphics2D) g;
			menu.render(g2);
		}
		bs.show();
	}
		
	public void run() {
		requestFocus();
		long last = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = (1*Math.pow(10,9))/amountOfTicks;
		double timer = System.currentTimeMillis();
		int frames =0;
		double delta =0;
		
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now-last)/ns;
			last = now;
			
			if(delta >= 1) {
				frames++;
				tick();
				render();
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: "+frames);
				frames = 0;
				timer +=1000;
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			// Para direita
			player.right = true;
			
		}
		
		if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			// Para esquerda
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			// Para cima
			player.up = true;	
			if(gameState == "Menu") {
				menu.up = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			// Para baixo
			player.down = true;	
			if(gameState == "Menu") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.shoot = true;
		}
		
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			
			if(gameState == "Menu") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "Menu";
			menu.pause = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
			if (gameState == "Normal") {
				this.saveGame = true;
			}
			//(CapsLock)
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			// Para direita
			player.right = false;			
		}
		
		if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			// Para esquerda
			player.left = false;
		}		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			// Para cima
			player.up = false;	
		
		}		
		if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			// Para baixo
			player.down = false;	

		}	
	
			
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX()/SCALE);
		player.my = (e.getY()/SCALE);
		// System.out.println(player.mx +"x"+player.my);
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

}
