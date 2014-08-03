package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;

public class Obstacle {
	private float movement;
	private float offset;
	private float speed = (float) (GameScreen.GAME_WIDTH*0.002083); 
	private Bitmap image;
	private boolean newObstacle = false;
	private boolean top;
	private boolean incrementScore = true;

	public Obstacle(int offset, boolean top, Bitmap image) {
		this.offset = offset;;
		this.image = image;
		this.top = top;
	}

	/**
	 * Draw obstacles
	 */
	public void draw(Canvas canvas, Paint brush){
		if (top){
			canvas.drawBitmap(image, (canvas.getWidth() - movement + offset),  0, brush);
		}
		else {
			canvas.drawBitmap(image, (canvas.getWidth() - movement + offset),  canvas.getHeight() - image.getHeight(), brush);
		}
	}
	/**
	 * Used for the Increment of the Score
	 * Need it because we using floats for the speed of pillars. 
	 * @return
	 */
	public boolean incrementScore(){
		return incrementScore;
	}
	public void setIncrementScoreToFalse(){
		incrementScore = false;
	}
	
	/**
	 * clock tick
	 */
	public void clockTick(){
		//TODO: 
		//Continuous movement of obstacles 
		if (movement  <= GameScreen.GAME_WIDTH + image.getWidth()){
			movement += speed;
		} else {
			newObstacle = true;
		}
	}

	public float getX(){
		return GameScreen.GAME_WIDTH - movement + offset; 
	}

	public int getWidth(){
		return image.getWidth();
	}

	public int getHeight(){
		return image.getHeight();
	}

	public int getY(){
		if (top){
			return 0;
		}
		return GameScreen.GAME_HEIGHT - image.getHeight();
	}

	public void reset(){
		movement = 0;
	}

	public boolean getNewObstacle(){
		return newObstacle;
	}
}
