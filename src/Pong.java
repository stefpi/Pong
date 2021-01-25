/**
 * Name: Stefan Pitigoi
 * Class: ICS4U
 * Date: January 6, 2021
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import csta.ibm.pong.Game;

enum state { START, PLAYING};

public class Pong extends Game {
	/**
	 * created variables for each of the key press functions, so that if the controls want to be changed in the future, it is easier to replace in code.
	 * This can also lead to an option in the start menu where players can select their own controls and they own keys.
	 */
	private boolean P1UP, P1DOWN, P2UP, P2DOWN;
	
	private static Ball ball;
	private int ballYDirection;
	private Paddle leftPaddle, rightPaddle;
	private Color leftPaddleColour, rightPaddleColour;
	private static int windowHeight, windowWidth, resRatio;
	private JLabel score;
	public static state gameState;
	private int artificialDifficulty;
	
	private ImageIcon pongIcon;
	
	/**
	 * returns the x coordinate of the ball
	 * @return ball.x
	 */
	public static int getBallX() {
		return ball.getX();
	}
	
	/**
	 * returns the y coordinate of the ball
	 * @return ball.y
	 */
	public static int getBallY() {
		return ball.getY();
	}
	
	/**
	 * returns the game window height
	 * @return windowHeight
	 */
	public static int getWindowHeight() {
		return windowHeight;
	}
	
	/**
	 * returns the game window width
	 * @return windowWidth
	 */
	public static int getWindowWidth() {
		return windowWidth;
	}
	
	public void setup() {
		URL iconUrl = getClass().getResource("pongicon.png");
		setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));
		
		 /** sets location of the game in the middle of the screen */
		this.setLocation((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - windowWidth/2, 
				(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - windowHeight/2);

		/** sets window size to exact desired size, excluding the top bar menu created by the game class*/
		this.setSize(windowWidth + (windowWidth-getFieldWidth()-(resRatio*200)), windowHeight + (windowHeight-getFieldHeight()-(resRatio*200)));
		this.setResizable(false);
		
		gameState = state.START;
		
		ball = new Ball(windowWidth/26, windowHeight/26, Color.WHITE, windowHeight/2, windowWidth/2);
		
		leftPaddle = new Paddle(windowWidth/20, windowHeight/4, leftPaddleColour, 0);
		rightPaddle = new Paddle(windowWidth/20, windowHeight/4, rightPaddleColour, artificialDifficulty);
		
		leftPaddle.setX(5);
		leftPaddle.setY((windowHeight/2)-(leftPaddle.getHeight()/2));
		
		rightPaddle.setX(windowWidth-rightPaddle.getWidth()-5);
		rightPaddle.setY((windowHeight/2)-(rightPaddle.getHeight()/2));
		
		score = new JLabel();
		score.setForeground(Color.WHITE);
		score.setHorizontalAlignment(SwingConstants.CENTER);
		score.setSize(windowWidth/5, windowWidth/10);
		score.setBounds(windowWidth/2-score.getWidth()/2, windowHeight/10, windowWidth/5, windowHeight/10);
		score.setFont(new Font("Sans Serif", Font.BOLD, windowWidth/20));
		score.setText(leftPaddle.getScore() + " : " + rightPaddle.getScore());
		
		add(ball);
		add(leftPaddle);
		add(rightPaddle);
		add(score);
		repaint();
		
		this.startGame();
	}
	
	/**
	 * When the game starts, if the gameState is START, which means that no paddle has been touched yet,
	 * the game will auto randomize the way in which the ball bounces off.
	 */
	public void randomizeFirstBounce() {
		if(ball.collides(rightPaddle) && Pong.gameState == state.START) { // if the point just started, then the ball will be moving horizontally, so this if statement randomizes the ball direction
			Pong.gameState = state.PLAYING;
			Random r = new Random();
			if(rightPaddle.getY() - ball.getHeight() < ball.getY() && ball.getY() <= rightPaddle.getY()+rightPaddle.getHeight()/2) {
				ball.setYSpeed(-1*(r.nextInt(3) + 1));
			} else {
				ball.setYSpeed(r.nextInt(3) + 1);
			}
		} else if (ball.collides(leftPaddle) && Pong.gameState == state.START) { // if the point just started, then the ball will be moving horizontally, so this if statement randomizes the ball direction
			Pong.gameState = state.PLAYING;
			Random r = new Random();
			if(leftPaddle.getY() - ball.getHeight() < ball.getY() && ball.getY() <= leftPaddle.getY()+leftPaddle.getHeight()/2) {
				ball.setYSpeed(-1*(r.nextInt(3) + 1));
			} else {
				ball.setYSpeed(r.nextInt(3) + 1);
			}
		}
	}
	
	public void act() { 
		//update key press booleans
		P2UP = this.MKeyPressed();
		P2DOWN = this.NKeyPressed();
		P1UP = this.XKeyPressed();
		P1DOWN = this.ZKeyPressed();
		
		/** if the game won, or the startMenu is still active, the game does not run */
		if(startMenu != JOptionPane.OK_OPTION) {
			this.stopGame();
		}
		
		/** sets a delay, making the game and collisions more reliable*/
		setDelay(10);
		
		randomizeFirstBounce();
		
		// checks for key pressed event and then moves right paddle accordingly
		if(P2UP && rightPaddle.getY() >= 0 && rightPaddle.getDifficulty() == 0) {
			rightPaddle.setSpeed(5);
			rightPaddle.moveUp();
		} else if (P2DOWN && (rightPaddle.getY()+rightPaddle.getHeight()) < this.getFieldHeight() && rightPaddle.getDifficulty() == 0) {
			rightPaddle.setSpeed(5);
			rightPaddle.moveDown();
		} else {
			rightPaddle.setSpeed(0);
		}
		
		// checks for key pressed event and then moves left paddle accordingly
		if (P1UP && leftPaddle.getY() >= 0) {
			leftPaddle.setSpeed(5);
			leftPaddle.moveUp();
		} else if (P1DOWN && (leftPaddle.getY()+leftPaddle.getHeight()) < this.getFieldHeight()) {
			leftPaddle.setSpeed(5);
			leftPaddle.moveDown();
		} else {
			rightPaddle.setSpeed(0);
		}
		
		// detects if the ball touches the top and bottom walls and then makes it bounce
		if (ball.getY() < 0) {
			ball.bounceVertical();
		} else if ((ball.getY() + ball.getHeight()) > windowHeight) {
			ball.bounceVertical();
		}
		
		/**
		 * deals with collision detection between the ball and the right paddle, but only if 
		 * ball.bounces is divisible by 2. This insures that the code does not detect multiple
		 * detections when only 1 occurred. Explained better in external log.
		 */
		if(ball.collides(rightPaddle) && ball.getBounces() % 2 == 0) {
			ball.incBounces();
			//top collision (if ball's top left corner is above the right paddle.)
			if(ball.getY() < rightPaddle.getY()) { 
				int xdisp = (ball.getX() + ball.getWidth()) - rightPaddle.getX(); // how far the ball entered into the paddle in the x direction
				int ydisp = (ball.getY() + ball.getHeight()) - rightPaddle.getY(); // how far the ball entered into the paddle in the y direction
				
				/** if the paddle is more into the paddle sideways than vertically, then the ball is on top of the ball*/
				if(ydisp < xdisp) {
					ball.setY(rightPaddle.getY() - ball.getWidth());
					if (P2UP) {
						ball.setYSpeed(-5); // if the paddle is moving up as well, ball speed is increased
						ball.bounceHorizontal();
					} else {
						ball.bounceVertical();
					}
				} else if (ydisp >= xdisp) { // if the ball is more into the paddle vertically than horizontally, then the ball is beside the paddle
					ball.bounceHorizontal();
				}
			} else if (ball.getY() + ball.getHeight() > rightPaddle.getY() + rightPaddle.getHeight()) { // bottom collision
				int xdisp = (ball.getX() + ball.getWidth()) - rightPaddle.getX();
				int ydisp = (rightPaddle.getY() + rightPaddle.getHeight()) - ball.getY();
				
				if(ydisp < xdisp) {
					ball.setY(rightPaddle.getY() + rightPaddle.getHeight() + ball.getWidth());
					if (P2DOWN) {
						ball.setYSpeed(5);
						ball.bounceHorizontal();
					} else {
						ball.bounceVertical();
					}
				} else if (ydisp >= xdisp) {
					ball.bounceHorizontal();
				}
			} else {
				ball.bounceHorizontal();
				if(ballYDirection == -1) { //if the ball is going in the negative direction (upwards towards y=0)
					if(P2UP) {
						ball.setYSpeed(ball.getYSpeed() - 2); //ball is sped up because the paddle moved with it
					} else if (P2DOWN) {
						ball.setYSpeed(ball.getYSpeed() + 2); //ball is slowed down because the paddle moved against it
					}
				} else if (ballYDirection == 0) { // if the paddle is moving horizontally
					if(P2UP) {
						ball.setYSpeed(ball.getYSpeed() - 1); // if the paddle moved up, the ball is also shifted slightly upwards
					} else if (P2DOWN) {
						ball.setYSpeed(ball.getYSpeed() + 1); // if the paddle moved downwards, the ball is also shifted slightly downwards
					}
				} else if (ballYDirection == 1) { // if the ball is going in the positive direction (downwards towards y=windowHeight)
					if(P2UP) {
						ball.setYSpeed(ball.getYSpeed() - 2); //ball is slowed down because the paddle moved against it
					} else if (P2DOWN) {
						ball.setYSpeed(ball.getYSpeed() + 2); //ball is sped up because the paddle moved with it
					}
				}
			}
		}
		
		if (ball.collides(leftPaddle) && ball.getBounces() % 2 == 1) {
			ball.incBounces();
			//top collision
			if(ball.getY() < leftPaddle.getY()) {
				int xdisp = (leftPaddle.getX() + leftPaddle.getWidth()) - ball.getX();
				int ydisp = (ball.getY() + ball.getHeight()) - leftPaddle.getY();
				
				if(ydisp < xdisp) {
					ball.setY(leftPaddle.getY() - ball.getWidth());
					if (P1UP) {
						ball.setYSpeed(-5);
						ball.bounceHorizontal();
					} else {
						ball.bounceVertical();
					}
				} else if (ydisp >= xdisp) {
					ball.bounceHorizontal();
				}
			} else if (ball.getY() + ball.getHeight() > leftPaddle.getY() + leftPaddle.getHeight()) { // bottom collision
				int xdisp = (leftPaddle.getX() + leftPaddle.getWidth()) - ball.getX();
				int ydisp = (leftPaddle.getY() + leftPaddle.getHeight()) - ball.getY();
				
				if(ydisp < xdisp) {
					ball.setY(leftPaddle.getY() + leftPaddle.getHeight() + ball.getWidth());
					if (P1DOWN) {
						ball.setYSpeed(5);
						ball.bounceHorizontal();
					} else {
						ball.bounceVertical();
					}
				} else if (ydisp >= xdisp) {
					ball.bounceHorizontal();
				}
			} else {
				ball.bounceHorizontal();
				if(ballYDirection == -1) {
					if(P1UP) {
						ball.setYSpeed(ball.getYSpeed() - 2);
					} else if (P1DOWN) {
						ball.setYSpeed(ball.getYSpeed() + 2);
					}
				} else if (ballYDirection == 0) {
					if(P1UP) {
						ball.setYSpeed(ball.getYSpeed() - 1);
					} else if (P1DOWN) {
						ball.setYSpeed(ball.getYSpeed() + 1);
					}
				} else if (ballYDirection == 1) {
					if(P1UP) {
						ball.setYSpeed(ball.getYSpeed() - 2);
					} else if (P1DOWN) {
						ball.setYSpeed(ball.getYSpeed() + 2);
					}
				}
			}
		}
		
		/** to determine the ball direction (used above) the signum() function is used to determine the direction of the ball.
		 * The balls y speed is negative when going upwards and positive when it goes downwards.
		 */
		ballYDirection = Integer.signum(ball.getYSpeed());
		
		// detects if the ball touches the side walls, and increases score + changes scoreboard
		if (ball.getX() < 0) {
			rightPaddle.incScore();
			score.setText(leftPaddle.getScore() + " : " + rightPaddle.getScore());
			resetPoint();
			ball.setXSpeed(2);
			ball.resetBounces(0);
		} else if ((ball.getX() + ball.getWidth()) > windowWidth) {
			leftPaddle.incScore();
			score.setText(leftPaddle.getScore() + " : " + rightPaddle.getScore());
			resetPoint();
			ball.setXSpeed(-2);
			ball.resetBounces(1);
		}
		
		if(leftPaddle.getScore() == 11) {
			remove(ball);
			this.p1Wins();
			stopGame();
		} else if (rightPaddle.getScore() == 11) { // wins at 11 points
			remove(ball);
			this.p2Wins();
			stopGame();
		}
	}
	
	/**
	 * After a point is won, this method is called to reset the positions of the paddles and ball.
	 */
	public void resetPoint() {
		remove(ball);
		ball = new Ball(windowWidth/26, windowHeight/26, Color.WHITE, windowWidth/2-ball.getWidth()/2, windowHeight/2-ball.getHeight()/2);
		add(ball);
		gameState = state.START; // sets gameState back to start, to indicate no paddle has been touched yet.
		leftPaddle.setY(windowHeight/2-(windowHeight/8));
		rightPaddle.setY(windowHeight/2-(windowHeight/8));
		repaint();
	}
	
	int startMenu; //stores startmenu response
	/**
	 * Start menu method that shows end user the introduction, instructions and which settings they can tweak.
	 * If closed, the app exits, but if the ok button is pressed the game starts.
	 */
	public void startMenu() {
		try {
			pongIcon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("pongicon.png")));
		} catch(IOException e) {
			pongIcon = null;
		}
		
		String[] resolutions = {"400x400", "600x600", "800x800", "1000x1000"};
		String[] players = {"Player vs Player", "Player vs Easy AI (1)", "Player vs Medium AI (2)", "Player vs Hard AI (3)"};
		String[] colours = {"White", "Blue", "Red", "Green"};
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		JComboBox resCombo = new JComboBox(resolutions);
		JComboBox playerCount = new JComboBox(players);
		JComboBox p1Colours = new JComboBox(colours);
		JComboBox p2Colours = new JComboBox(colours);
		
		Object[] inputs = {
				"<html><body width='"
				+ "'><p>A classical pong game, but with computer controlled paddles, "
				+ "<p>easy-to-learn ball control and 'stunning' graphics!"
				+ "<p>"
				+ "<p>Created by Stefan Pitigoi, 335386983 (2020-2021)"
				+ "<h2>Instructions</h2>"
				+ "<p>P1 Controls (Left Paddle) => Z move down, X move up"
				+ "<p>P2 Controls (Right Paddle) => N move down, M move up"
				+ "<p>First Player to 11 Points Wins"
				+ "<p>Good Luck :D"
				+ "<h3>Select your prefferred settings and press ok to start!</h2>"
				+ "<p>Your monitors max screen resolution is: "
				+ (int)screenSize.getWidth() + " x " + (int)screenSize.getHeight()
				+ "<p>=====",
				"Resolution:", resCombo,
				"Number/Type of Players:", playerCount,
				"Player 1 Paddle Colour:", p1Colours,
				"Player 2 Paddle Colour:", p2Colours
		};
		
		startMenu = JOptionPane.showConfirmDialog(null, inputs, "Pong!", JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION, pongIcon);
		
		if (startMenu == JOptionPane.OK_OPTION) {
			windowHeight = Integer.valueOf(resCombo.getSelectedItem().toString().split("x")[0]);
			windowWidth = Integer.valueOf(resCombo.getSelectedItem().toString().split("x")[1]);
			resRatio = (windowHeight-400)/200;
		    
		    switch(playerCount.getSelectedIndex()) {
		    case 0: 
		    	artificialDifficulty = 0;
		    	break;
		    case 1: 
		    	artificialDifficulty = 1;
		    	break;
		    case 2: 
		    	artificialDifficulty = 2;
		    	break;
		    case 3: 
		    	artificialDifficulty = 3;
		    	break;
		    }
		    
		    switch(p1Colours.getSelectedIndex()) {
		    case 0:
		    	leftPaddleColour = Color.WHITE;
		    	break;
		    case 1:
		    	leftPaddleColour = Color.RED;
		    	break;
		    case 2:
		    	leftPaddleColour = Color.GREEN;
		    	break;
		    case 3:
		    	leftPaddleColour = Color.BLUE;
		    	break;
		    }
		    switch(p2Colours.getSelectedIndex()) {
		    case 0:
		    	rightPaddleColour = Color.WHITE;
		    	break;
		    case 1:
		    	rightPaddleColour = Color.RED;
		    	break;
		    case 2:
		    	rightPaddleColour = Color.GREEN;
		    	break;
		    case 3:
		    	rightPaddleColour = Color.BLUE;
		    	break;
		    }
		} else {
		    System.out.println("Start Menu Exited");
		    System.exit(0);
		}
	}
	
	public static void main(String[] args)  {
		Pong p = new Pong();
		p.startMenu();
		p.setVisible(true);
		p.initComponents();
	}
}