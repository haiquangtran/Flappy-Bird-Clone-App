package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Button {
	private float x;
	private float y;
	private Bitmap image;
	
	public Button(float x, float y, Bitmap image){
		this.x = x;
		this.y = y;
		this.image = image;
	}
	
	public void draw(Canvas canvas, Paint brush){
		canvas.drawBitmap(image, x, y, brush);
	}
	
	public boolean isTouching(float xOffset, float yOffset){
		if (xOffset >= x && xOffset <= x + image.getWidth() && yOffset >= y && yOffset <= y + image.getHeight()){
			return true;
		}
		return false;
	}
}
