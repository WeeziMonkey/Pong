import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {

	// PONG

	Game game;

	Graphics graphics;

	public static int roomWidth;
	public static int roomHeight;

	Image img_wall, img_bat;
	
	Wall[] walls;

	int batHeight = 64;
	int batWidth = 16;
	
	int ballspeedX = 6;
	int ballspeedY = 2;
	int ballStartingX = 200;
	int ballStartingY = 250;

	Bat player;
	Ai ai;
	Ball ball;

	boolean isRunning;
	
	int FPS = 1000 / 60;

	public Game(int roomWidth, int roomHeight) {

		Game.roomWidth = roomWidth;
		Game.roomHeight = roomHeight;

		try {

			loadGraphics();
		} catch (IOException e) {

			e.printStackTrace();
		}

		this.setSize(roomWidth, roomHeight);
		this.requestFocus();

		game = this;

		game.addMouseMotionListener(new MouseListener());

		player = new Bat(roomWidth - 70, roomHeight / 2 - 20, batWidth, batHeight, img_bat, game);
		ai = new Ai(70, roomHeight / 2 - 20, batWidth, batHeight, img_bat, game);
		ball = new Ball(ballStartingX, ballStartingY, 16, 16, ballspeedX, ballspeedY, Direction.RIGHT, Direction.UP);
		
		walls = new Wall[2];
		walls[0] = new Wall(0, 0, roomWidth, 16, img_wall);
		walls[1] = new Wall(0, roomHeight - 45, roomWidth, 16, img_wall);

		isRunning = true;
	}
	
	
	@Override
	public void run() {

		while (isRunning) {
						
			gameLoop();
			
			try {
				Thread.sleep(FPS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

	}
	
	public void gameLoop() {
		
		if (checkBallColission() != null) {
			
			ball.changeDirection(checkBallColission());
			
		}
		
		ball.updatePosition();
		ai.updatePosition();

		repaint();
	}
	
	public Ball getBall() {
		return ball;
	}
	
	public String checkBallColission() {
		
		if (ball.intersects(player)) {
			System.out.println("player collision");
			return "player";
		}
		
		else if (ball.intersects(ai)) {
			return "ai";
		}
		
		else if (ball.intersects(walls[0]) || ball.intersects(walls[1])) {
			return "wall";
		}
		
		else return null;
	}

	public void start() {

		new Thread(this).start();
	}

	@Override
	public void paint(Graphics g) {

		//g.drawImage(img_wall, 0, 0, null);
		//g.drawImage(img_wall, 0, roomHeight - 45, null);
		
		g.setColor(Color.WHITE);
		
		ball.draw(g);
		ai.draw(g);
		player.draw(g);
		
		for (Wall wall : walls) {
			wall.draw(g);
		}

	}

	public void loadGraphics() throws IOException {

		img_wall = fetchResizedImage("wall.png", roomWidth, 16);
		img_bat = fetchResizedImage("wall.png", batWidth, batHeight);

	}

	public Image fetchResizedImage(String fileName, int width, int length) throws IOException {
		// length = x
		// width = y
		return fetchImage(fileName).getScaledInstance(width, length, 0);

	}

	public BufferedImage fetchImage(String fileName) throws IOException {
		return ImageIO.read(this.getClass().getResourceAsStream(fileName));

	}

	public static void main(String[] args) {

		Game g = new Game(720, 480);
		g.setBackground(Color.BLACK);

		JFrame frame = new JFrame("Pong");

		frame.add(g);
		frame.setSize(720, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.requestFocus();

		g.start();

	}

	public class MouseListener extends MouseAdapter {

		@Override
		public void mouseMoved(MouseEvent e) {

			double newy = e.getY();

			player.updatePosition(newy);

		}

	}

	// dbing
	public void update(Graphics g) {
		Graphics offgc;
		Image offscreen = null;
		Dimension d = size();

		// create the offscreen buffer and associated Graphics
		offscreen = createImage(d.width, d.height);
		offgc = offscreen.getGraphics();
		// clear the exposed area
		offgc.setColor(getBackground());
		offgc.fillRect(0, 0, d.width, d.height);
		offgc.setColor(getForeground());
		// do normal redraw
		paint(offgc);
		// transfer offscreen to window
		g.drawImage(offscreen, 0, 0, this);
	}

}
