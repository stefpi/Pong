import csta.ibm.pong.GameObject;
import java.awt.Color;
import java.util.Random;

public class Paddle extends GameObject {
	private int speed = 0;
	private int paddleWidth;
	private int paddleHeight;
	int score = 0;
	int artificialDifficulty; // 0 = no AI, 1 = easy, 2 = medium, 3 = hard
	
	/**
	 * Paddle constructor
	 * @param sizex horizontal size
	 * @param sizey vertical size
	 * @param color paddle color
	 * @param x coordinate
	 * @param y	coordinate
	 * @param difficulty computer difficulty, (0) if controlled by player
	 */
	public Paddle(int sizex, int sizey, Color color, int x, int y, int difficulty) {		
		super();
		this.setSize(sizex, sizey);
		this.setColor(color);
		this.setX(x);
		this.setY(y);
		
		paddleWidth = sizex;
		paddleHeight = sizey;
		
		artificialDifficulty = difficulty;
	}
	
	/**
	 * Paddle constructor with default starting position
	 * @param sizex horizontal size
	 * @param sizey vertical size
	 * @param color paddle color
	 * @param difficulty computer difficulty, (0) if controlled by player
	 */
	public Paddle(int sizex, int sizey, Color color, int difficulty) {
		this(sizex, sizey, color, 0, 0, difficulty);
	}
	
	/**
	 * moves paddle upwards by variable speed
	 */
	public void moveUp() {
		this.setY(getY()-speed);
	}
	
	/**
	 * moves paddle downwards by variable speed
	 */
	public void moveDown() {
		this.setY(getY()+speed);
	}
	
	/**
	 * @return paddleHeight
	 */
	public int getHeight() {
		return paddleHeight;
	}
	
	/**
	 * @return paddleWidth
	 */
	public int getWidth() {
		return paddleWidth;
	}
	
	/**
	 * @return speed
	 */
	public int getSpeed() {
		return speed;
	}
	
	/**
	 * sets paddle speed to s variable
	 * @param s speed
	 */
	public void setSpeed(int s) {
		speed = s;
	}
	
	/**
	 * @return score
	 */
	public int getScore() {
		return score;
	}
	
	/** a paddle acts as a player, so the paddle's score is the players score. This method increases the player score by 1*/
	public void incScore() {
		score++;
	}
	
	/**
	 * @return paddleHeight
	 */
	public int getDifficulty() {
		return artificialDifficulty;
	}
	
	/**
	 * method that defines the movement of the computer controlled paddle on the easy setting. Paddle moves slowly and reacts slowly.
	 */
	public void moveEasy() {
		// when the ball is 6 pixels above or below the middle of the paddle, the paddle moves towards the ball
		if(Pong.getBallY() + 6 < getY() + getHeight()/2 && getY() > 0) {
			setSpeed(1);
			moveUp();
		} else if (Pong.getBallY() - 6 > getY() + getHeight()/2 && getY() + getHeight() < Pong.getWindowHeight()) {
			setSpeed(1);
			moveDown();
		} else {
			return;
		}
		setSpeed(0);
	}
	
	/**
	 * method that defines the movement of the computer controlled paddle on the medium setting. Paddle moves moderately fast and reacts slow.
	 */
	public void moveMedium() {
		if(Pong.getBallY() + 5 < getY() + getHeight()/2 && getY() > 0) {
			setSpeed(2);
			moveUp();
		} else if (Pong.getBallY() - 5 > getY() + getHeight()/2 && getY() + getHeight() < Pong.getWindowHeight()) {
			setSpeed(2);
			moveDown();
		} else {
			return;
		}
		setSpeed(0);
	}
	
	/**
	 * method that defines the movement of the computer controlled paddle on the hard setting. Paddle moves quickly and reacts fast.
	 */
	public void moveHard() {
		if(Pong.getBallY() + 4 <= getY() + getHeight()/2 && getY() > 0) {
			setSpeed(4);
			moveUp();
		} else if (Pong.getBallY() - 4 >= getY() + getHeight()/2 && getY() + getHeight() < Pong.getWindowHeight()) {
			setSpeed(4);
			moveDown();
		} else {
			return;
		}
		setSpeed(0);
	}
	
	/**
	 * every tick that the game is being played, if the paddle is set to be computer controlled it is moved by calling its specified move function
	 */
	public void act() {
		switch(artificialDifficulty) {
		case 0: 
			break;
		case 1:
			moveEasy();
			break;
		case 2:
			moveMedium();
			break;
		case 3:
			moveHard();
			break;
		}
	}
}