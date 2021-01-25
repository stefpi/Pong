import csta.ibm.pong.GameObject;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Ball extends GameObject {	
	private int dx, dy;
	private int bounces = 0;
	private Color c = Color.WHITE;
	
	/**
	 * Ball Constructor
	 * @param sizex horizontal size
	 * @param sizey vertical size
	 * @param color ball color
	 * @param x coordinate
	 * @param y coordinate
	 */
	public Ball(int sizex, int sizey, Color color, int x, int y) {
		super();
		this.setSize(sizex, sizey);
		this.setColor(color);
		this.setX(x);
		this.setY(y);
		
		dx = 2;
		dy = 0;
	}
	
	/**
	 * Ball Constructor with default starting position
	 * @param sizex horizontal size
	 * @param sizey vertical size
	 * @param color ball color
	 */
	public Ball(int sizex, int sizey, Color color) {
		this(sizex, sizey, color, 0, 0);
	}
	
	/** Overrides the paint method to be able to make the ball a circle instead of a rectangle*/
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
	}
	
	/** Overrides the setColor method to make it accessible to the ball class*/
	public void setColor(Color c) {
		this.c = c;
	}
	 
	/**
	 * moves the ball based on the values of dx and dy
	 */
	public void move() {
		this.setX(getX()+dx);
		this.setY(getY()+dy);
	}
	
	/**
	 * flips the direction of the horizontal speed
	 */
	public void bounceHorizontal() {
		dx = -dx;
	}
	
	/**
	 * flips the direction of the vertical speed
	 */
	public void bounceVertical() {
		dy = -dy;
	}
	
	/**
	 * increase the number of times the ball has bounced off of a paddle
	 */
	public void incBounces() {
		bounces++;
	}
	
	/**
	 * sets number of bounces to a starting number in accordance with who won so that the ball collision works when winner gets the ball served to them
	 * @param winnerSide which side won the game: 0 (right won) or 1 (left won)
	 */
	public void resetBounces(int winnerSide) {
		if(winnerSide == 0 || winnerSide == 1) {
			bounces = winnerSide;
		} else {
			bounces = 0;
		}
	}
	
	/**
	 * @return the number of times the ball has bounced
	 */
	public int getBounces() {
		return bounces;
	}
	
	/**
	 * @return horizontal speed
	 */
	public int getXSpeed() {
		return dx;
	}
	
	/**
	 * @return vertical speed
	 */
	public int getYSpeed() {
		return dy;
	}
	
	/**
	 * sets dx to param speed
	 * @param speed horizontal speed
	 */
	public void setXSpeed(int speed) {
		dx = speed;
	}
	
	/**
	 * sets dy to param speed
	 * @param speed vertical speed
	 */
	public void setYSpeed(int speed) {
		dy = speed;
	}
	
	/**
	 * Every tick of the game being started, the ball is moved
	 */
	public void act() {
		move();
	}
}