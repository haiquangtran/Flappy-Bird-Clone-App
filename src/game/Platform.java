package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;


public class Platform {
	private float movement;
	private double speed = GameScreen.GAME_WIDTH*0.0014583; 
	private Bitmap image;
	private int offsetX;

	public Platform(int offsetX, Bitmap image) {
		this.image = image;
		this.offsetX = offsetX;
	}

	/**
	 * Draw obstacles
	 */
	public void draw(Canvas canvas, Paint brush){
		canvas.drawBitmap(image, (canvas.getWidth() + offsetX) - movement, canvas.getHeight() - image.getHeight(), brush);
	}

	/**
	 * clock tick
	 */
	public void clockTick(){
		//Continuous movement of obstacles 
		if (movement  <= image.getWidth()){
			movement += speed;
		} else {
			movement = 0;
		}
	}

	public float getX(){
		return (GameScreen.GAME_WIDTH + offsetX) - movement; 
	}

	public int getWidth(){
		return image.getWidth();
	}

	public int getHeight(){
		return image.getHeight();
	}

	public int getY(){
		return GameScreen.GAME_HEIGHT - image.getHeight();
	}

	public void reset(){
		movement = 0;
	}
	
	/**
	 * Boundary box
	 * 
	 * @param character
	 * @return
	 */
	public boolean isTouching(Character character){
		if ((character.getY() + character.getImage().getHeight()) >= GameScreen.GAME_HEIGHT - image.getHeight()){
			return true;
		}
		return false;
	}

}
