package game;

import game.GameScreen.GameState;

import java.util.ArrayList;

import menus.MainMenuScreen;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.View;

public class Character {
	private float xOffset; 
	private float yOffset;
	private Bitmap image;
	private boolean up = false;
	//Position
	private float x;
	private float y;
	//Score
	private int score;
	private int highScore;
	//Gravity
	private double startSpeed = 0.3;
	private double speed = startSpeed;
	private double accel = 0.014;
	private double terminalSpeed = 2.5;
	//Jump - makes it so that jump is over many clockticks
	private int jumpCount;
	private int clockTicks = 20;
	//Angle
	private float angle = 0;
	//Highscore
	private SharedPreferences sharedPrefs;
	private boolean saveHighScore = true;
	//Animation
	private ArrayList<Bitmap> images;
	private int imageIndex;
	private int count;
	//Menu Screen
	private boolean changeDirection;

	public Character(Context context, int xOffset, int yOffset, Bitmap image, ArrayList<Bitmap> images){
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.image = image;
		this.images = images;
		//getting preferences
		sharedPrefs = context.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
		//Load highscore
		highScore = sharedPrefs.getInt("key", 0);
	}

	/**
	 * Move up
	 */
	public void moveUp(){
		//Bounds
		if (GameScreen.GAME_HEIGHT/2 - image.getHeight()/2 + yOffset > 0){
			//Start speed
			speed = startSpeed;

			yOffset -= 1;
			//set y position
			y = GameScreen.GAME_HEIGHT/2 - image.getHeight()/2  + yOffset;
			//set x position
			x =  GameScreen.GAME_WIDTH/4 - image.getWidth()/2 + xOffset;
		}

	}

	/**
	 * Move down
	 */
	public void moveDown(){
		//Bounds
		if (GameScreen.GAME_HEIGHT/2 - image.getHeight()/2 + yOffset + speed 
				< GameScreen.GAME_HEIGHT - image.getHeight()){

			if (speed < terminalSpeed){
				speed += accel;
			} else {
				speed = terminalSpeed;
			}


			yOffset += speed;
			//set y position
			y = GameScreen.GAME_HEIGHT/2 - image.getHeight()/2  + yOffset;
			//set x position
			x =  GameScreen.GAME_WIDTH/4 - image.getWidth()/2 + xOffset;
		}
	}

	/**
	 * Draw
	 */
	public void draw(Canvas canvas, Paint brush, boolean gameScreen){
		//Rotation
		Matrix matrix = new Matrix();
		if (gameScreen){
			matrix.preTranslate(canvas.getWidth()/4 - image.getWidth()/2 + xOffset, 
					canvas.getHeight()/2 - image.getHeight()/2  + yOffset);
		} else {
			//main menu
			matrix.preTranslate(canvas.getWidth()/2 - image.getWidth()/2 + xOffset, 
					canvas.getHeight()/2 - image.getHeight()/2  + yOffset);
		}
		matrix.preRotate(angle, image.getWidth()/2, image.getHeight()/2);

		//Draw image
		canvas.drawBitmap(images.get(imageIndex), matrix, brush);
	}


	/**
	 * Clock Tick
	 */
	public void clockTick(){
		//Animation
		count++;
		if (count > 10){
			count = 0;
		}
		int arraySize = images.size()-1;
		if (imageIndex < arraySize && count % 10 == 0){
			imageIndex++;
		} else if (imageIndex >= arraySize){
			imageIndex = 0;
		}

		//Mechanics
		if (!up){
			moveDown();
		} else if (jumpCount == clockTicks){
			up = false;
			jumpCount = 0;
		} else {
			jumpCount++;
		}

	}

	public float getY(){
		return y;
	}


	public Bitmap getImage(){
		return image;
	}

	/**
	 * Boundary box
	 * 
	 */
	public boolean isTouching(Obstacle obs){
		int width = image.getWidth();
		//Try to miss out on the spikes of the dragon
		int diff = (int)(image.getHeight()*0.1);
		int height = image.getHeight();
		//Touching
		if ((x < obs.getX() + obs.getWidth() && x > obs.getX() || x + width < obs.getX() + obs.getWidth() 
				&& x + width > obs.getX()) && (y+diff < obs.getY() + obs.getHeight()
						&& y > obs.getY()|| y + height < obs.getY() + obs.getHeight()
						&& y  + height > obs.getY() )){
			return true;
		}

		return false;
	}

	public void pressedUp(){
		up = true;
	}


	/**
	 * Reset Character - new game etc
	 */
	public void reset(){
		speed = startSpeed;
		xOffset = 0;
		yOffset = 0;
		score = 0;
		angle = 0;		
		y = GameScreen.GAME_HEIGHT/2 - image.getHeight()/2;
		saveHighScore = true;
	}

	/**
	 * Increase score
	 * 
	 * @param obs
	 */
	public boolean incrementScore(Obstacle obs){
		//Increment score
		if (obs.getX() + (obs.getWidth()/2) <= x + (image.getWidth()/2)+1 && obs.incrementScore()){
			score++;
			obs.setIncrementScoreToFalse();
			return true;
		}

		return false;
	}


	/**
	 * Draw Score
	 * 
	 * @param canvas
	 * @param brush
	 */
	public void drawScore(Canvas canvas, Paint brush, GameState state, boolean splash){
		int textSize = (int)(canvas.getHeight()*0.065);
		brush.setColor(Color.WHITE);
		brush.setTextSize(textSize);
		String currScore = ""+score;
		if (state != GameState.GAMEOVER){
			//SCORE 
			canvas.drawText(currScore, canvas.getWidth()/2, canvas.getHeight()/6, brush);
		} else if (state == GameState.GAMEOVER && splash){
			//GAME OVER SCORE
			canvas.drawText(currScore, canvas.getWidth() * 0.25f,canvas.getHeight() * 0.5f, brush);
		}
	}

	public void drawHighScore(Canvas canvas, Paint brush){
		int textSize = (int)(canvas.getHeight()*0.065);
		brush.setColor(Color.WHITE);
		brush.setTextSize(textSize);
		canvas.drawText(""+highScore, canvas.getWidth() * 0.75f - textSize, canvas.getHeight() * 0.5f , brush);
		//canvas.drawRect(canvas.getWidth() * 0.25f, canvas.getHeight() * 0.25f, canvas.getWidth() * 0.75f, canvas.getHeight() * 0.75f, brush);
	}

	public boolean getUp(){
		return up;
	}

	public void setAngle(float degrees){
		angle = degrees;
	}

	public float getX(){
		return (GameScreen.GAME_WIDTH/4 - image.getWidth()/2 + xOffset);
	}

	/**
	 * Saves highscore under SharedPreferences - even when it program terminates 
	 * it stores the highscore. 
	 */
	public void saveHighScore(){
		if (score > highScore && saveHighScore){	
			//setting preferences
			Editor editor = sharedPrefs.edit();
			editor.putInt("key", score);
			//Commit
			editor.commit();
			//save highscore only When game is over
			highScore = sharedPrefs.getInt("key", 0); //0 is the default value
			//Only saves it once
			saveHighScore = false; 
		}
	}

	/**
	 * Main Menu
	 */
	public void menuClockTick(){
		//Animation
		count++;
		if (count > 10){
			count = 0;
		}
		int arraySize = images.size()-1;
		if (imageIndex < arraySize && count % 10 == 0){
			imageIndex++;
		} else if (imageIndex >= arraySize){
			imageIndex = 0;
		}

		//Changes direction
		if (yOffset >= image.getHeight()/2){
			changeDirection = true;
		} else if (yOffset < 0)
		{
			changeDirection = false;
		}

		//Change the y values for the image
		if (changeDirection){
			yOffset-= 0.3;
		}
		else {
			yOffset+= 0.3;
		}

	}
}

