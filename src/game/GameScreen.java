package game;


import java.util.ArrayList;

import utility.clockThread;
import menus.MainMenuScreen;

import com.example.flappybirds.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class GameScreen extends View implements OnTouchListener{
	public static Character birdy;
	private Obstacle obs;
	private Obstacle obs2;
	private Obstacle topObs;
	private Obstacle topObs2;
	//Ground
	public static Platform ground1;
	public static Platform ground2;
	//Background
	public static Bitmap background;
	//tap instruction
	public Bitmap tap;
	//Gap
	private int gapCounter; 
	public static int GAME_WIDTH;
	public static int GAME_HEIGHT;
	//STATE
	public GameState state = GameState.MENU;
	//Start gap
	private int startCounter;
	private boolean startPipes;
	//Dimensions
	private int pipeWidth = GAME_WIDTH/6;
	private int gap = GAME_WIDTH/2 - pipeWidth/2; 
	private int pipeGap = (int) (GAME_HEIGHT * 0.25);
	private int jump = (int) ((GAME_HEIGHT * 0.10625)/20);	//20 is the clock ticks
	private int pipeHeight1 = GAME_HEIGHT/2 - pipeGap/2;
	private int pipeHeight2 = GAME_HEIGHT/2 - pipeGap/2 + (int) (GAME_HEIGHT * 0.125);	
	private int pipeHeight3 = GAME_HEIGHT/2 - pipeGap/2 - (int) (GAME_HEIGHT * 0.125);
	private int lavaSize = (int) (GAME_HEIGHT * 0.125);
	private int splashSize = (int)(GAME_HEIGHT *0.125);
	//Pipes
	private Bitmap pipe1;
	private Bitmap pipe2;
	private Bitmap pipe3;
	private Bitmap pipe4;
	private Bitmap pipe5;
	private Bitmap pipe6;
	//Fall animation
	private boolean fall = false;
	private float angle = 0;
	private Bitmap splashImage;
	private boolean splash = false;
	private float splashY = GAME_HEIGHT-lavaSize;
	private int alpha = 255;
	private Paint splashBrush = new Paint();
	//Music Player and Sound Effects - SoundPool
	private SoundPool soundEffects;
	private int upSound;
	private int hitSound;
	private int pipeSound;
	private int splashSound;
	private boolean splashSoundOn = false;
	//Start Button
	private Button startButt;
	//Replay Button
	private Button replayButt;
    private Bitmap replay;
	//Brush 
	private Paint brush = new Paint();

	public GameScreen(Context context) {
		super(context);
		setUp(context);
	}

	//Game State
	public enum GameState{
		MENU, START, PAUSED, RUNNING, GAMEOVER;
	}

	/**
	 * Set up map etc
	 */
	private void setUp(Context context) {

		//Sound
		soundEffects = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		upSound = soundEffects.load(context, R.raw.flap, 1); 
		hitSound = soundEffects.load(context, R.raw.slap, 1); 
		pipeSound = soundEffects.load(context, R.raw.ding, 1); 
		splashSound = soundEffects.load(context, R.raw.splash, 1); 
		//Buttons
		Bitmap button = BitmapFactory.decodeResource(getResources(), R.drawable.startbutton);
		Bitmap replay = BitmapFactory.decodeResource(getResources(), R.drawable.replay);
		startButt = new Button(GameScreen.GAME_WIDTH/2 - button.getWidth()/2, GameScreen.GAME_HEIGHT/2 + GameScreen.GAME_HEIGHT/6, button);
		replayButt = new Button(GameScreen.GAME_WIDTH/2 - button.getWidth()/2, GameScreen.GAME_HEIGHT/2 + GameScreen.GAME_HEIGHT/6, replay);
		//Character
		ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon1));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon2));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon2));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon4));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon5));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon6));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon7));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon6));
		images.add(BitmapFactory.decodeResource(getResources(), R.drawable.dragon5));
		birdy = new Character(this.getContext(), 0, 0, BitmapFactory.decodeResource(getResources(), R.drawable.dragon), images);	
		splashImage =  Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.splash),splashSize,splashSize,true);
		//Store pipes
		pipe1 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipe1), pipeWidth, pipeHeight1, true);
		pipe2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipedown1), pipeWidth, GAME_HEIGHT - pipeHeight1 - pipeGap, true);
		pipe3 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipe1), pipeWidth, pipeHeight2, true);
		pipe4 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipedown1), pipeWidth, GAME_HEIGHT - pipeHeight2 - pipeGap, true);
		pipe5 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipe1), pipeWidth, pipeHeight3, true);
		pipe6 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipedown1), pipeWidth, GAME_HEIGHT - pipeHeight3 - pipeGap, true);
		//background
		background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.background2), GAME_WIDTH, GAME_HEIGHT, true);
		//Generate the obstacles
		generateObstacles(false);
		generateObstacles(true);
		//Ground
		ground1 = new Platform(0, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lava), GAME_WIDTH + 1, lavaSize, true));
		ground2 = new Platform(-GAME_WIDTH, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.lava), GAME_WIDTH + 1, lavaSize, true));
		//tap instruction
		tap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tap),(int)(GAME_WIDTH * .208),(int) ((GAME_WIDTH * .208) * 1.31), true);
		//Brush
		brush.setColor(Color.WHITE);
		brush.setStyle(Style.FILL);
		//Set Font
		Typeface typeFace=Typeface.createFromAsset(MainMenuScreen.assets,"fonts/8bitlimo.ttf");
		brush.setTypeface(typeFace);

	}

	/**
	 * Draw - called on by itself
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//Paint
		paint(canvas, brush);
	}

	/**
	 * Paint graphics 
	 * 
	 * @param canvas
	 * @param brush
	 */
	public void paint(Canvas canvas, Paint brush){

		//draw background
		canvas.drawBitmap(background, 0, 0, brush);

		//----------START STATE-------------------

		if (state == GameState.MENU){
			//START STATE
			int textSize = (int)(canvas.getHeight()*0.095);
			//draw birdy
			birdy.draw(canvas, brush, false);
			//draw button
			startButt.draw(canvas, brush);
			//draw text
			brush.setTextSize(textSize);
			String name = "Flaps";
			String flappy = "Flappy";
			float flappyWidth = brush.measureText(flappy);
			float nameWidth= brush.measureText(name);
			canvas.drawText(flappy, (canvas.getWidth()-flappyWidth)/2, canvas.getHeight()/6, brush);
			canvas.drawText(name, (canvas.getWidth()-nameWidth)/2, canvas.getHeight()/6 + textSize, brush);
		} else {

			//draw Obstacles
			if (obs != null && obs2 != null && topObs2 != null  && topObs != null){
				obs.draw(canvas, brush);
				obs2.draw(canvas, brush);
				topObs2.draw(canvas, brush);
				topObs.draw(canvas, brush);
			}

			//Game over
			if (state == GameState.GAMEOVER && splash){
				int textSize = (int)(canvas.getHeight()*0.075);
				brush.setStyle(Style.FILL);
				brush.setARGB(80, 0, 0, 0);
				float blackWidth = canvas.getWidth()*0.80f;
				//BLACK END GAME SCREEN
				canvas.drawRect((canvas.getWidth()-blackWidth)/2, canvas.getHeight() * 0.25f, canvas.getWidth()-(canvas.getWidth()-blackWidth)/2, canvas.getHeight() * 0.75f, brush);
				brush.setColor(Color.WHITE);
				brush.setTextSize(textSize);
				String gameover = "GAME OVER";
				float gameoverWidth = brush.measureText(gameover);
				canvas.drawText(gameover, (canvas.getWidth()-gameoverWidth)/2, (canvas.getHeight() * 0.25f)/2 + (textSize/2), brush);
				//Draw highscore
				birdy.drawHighScore(canvas, brush);
				//draw Replay
				replayButt.draw(canvas, brush);
				textSize = (int)(canvas.getHeight()*0.045);
				brush.setTextSize(textSize);
				String retry = "Tap to Retry";
				float retryWidth = brush.measureText(retry);
				canvas.drawText(retry, (canvas.getWidth()-retryWidth)/2, (canvas.getHeight() * 0.25f)/2 - (textSize) + (canvas.getHeight()/4)*3, brush);
				//Draw Splash Animation	
				splashBrush.setAlpha(alpha);
				canvas.drawBitmap(splashImage, birdy.getX(), splashY, splashBrush);
			}

			//draw Bird
			if (birdy != null){
				birdy.draw(canvas, brush, true);
				//draw score
				birdy.drawScore(canvas, brush, state, splash);
			}

			//Draw tap instruction
			if (state == GameState.START){
				canvas.drawBitmap(tap, canvas.getWidth()/2 - tap.getWidth()/2, canvas.getHeight()/2 + birdy.getImage().getHeight()/2, brush);
			}
		}

		//Draw lava
		ground1.draw(canvas, brush);
		ground2.draw(canvas, brush);
	}



	/**
	 * Game Clock
	 */
	public void clockTick(){	
		if (state == GameState.GAMEOVER){
			//Save High score
			birdy.saveHighScore();
			//GAME OVER
			deadFallAnimation();
		}

		if (state == GameState.RUNNING){

			//Make different sizes of obstacles
			if (obs.getNewObstacle()){
				generateObstacles(false);
			} else if (obs2.getNewObstacle()){
				generateObstacles(true);
			}

			//Unit collisions
			if (birdy.isTouching(obs) || birdy.isTouching(obs2) ||
					birdy.isTouching(topObs) || birdy.isTouching(topObs2)){
				//Play sound
				soundEffects.play(hitSound, 1, 1, 0, 0, 1);
				//Game Over
				state = GameState.GAMEOVER;
				fall = true;
			} else if (ground1.isTouching(birdy) || ground2.isTouching(birdy)){
				//Game Over
				state = GameState.GAMEOVER;
				fall = true;
			}

			//Clock ticks
			birdy.clockTick();

			//Start interval with pipes
			if (startCounter == GAME_WIDTH){
				startPipes = true;
			} else if (!startPipes){
				startCounter++;
			}

			if (startPipes){
				obs.clockTick();
				topObs.clockTick();
				//Create gap with second pipe
				if (gapCounter == obs.getWidth() + gap){
					obs2.clockTick();
					topObs2.clockTick();
				} else {
					gapCounter++;
				}
			}

			//-------MOVE UP-------------
			if (birdy.getUp()){
				for (int i = 0; i < jump; i++){
					birdy.moveUp();
					//redraw
					postInvalidate();
				}
			}

			//Score
			if (birdy.incrementScore(obs) | birdy.incrementScore(obs2)){
				//Play sound
				soundEffects.play(pipeSound, 1, 1, 0, 0, 1);
			}

		} else if (state == GameState.MENU || state == GameState.START){
			//Birdy animation
			birdy.menuClockTick();
		} 

		//Creates lava animation
		ground1.clockTick();
		ground2.clockTick();

	}

	private void generateObstacles(boolean obstacle2) {
		int random = (int) (Math.random() * 3);

		if (!obstacle2){
			switch (random){
			case 0:
				obs = new Obstacle(0, false, pipe1);
				topObs = new Obstacle(0, true, pipe2);
				break;
			case 1:
				obs = new Obstacle(0,false, pipe3);
				topObs = new Obstacle(0, true, pipe4);
				break;
			case 2:
				obs = new Obstacle(0,false, pipe5);
				topObs = new Obstacle(0, true, pipe6);
				break;
			}
		} else {
			switch (random){
			case 0:
				obs2 = new Obstacle(0,false, pipe1);
				topObs2 = new Obstacle(0, true, pipe2);
				break;
			case 1:
				obs2 = new Obstacle(0,false, pipe3);
				topObs2 = new Obstacle(0, true, pipe4);
				break;
			case 2:
				obs2 = new Obstacle(0,false, pipe5);
				topObs2 = new Obstacle(0, true, pipe6);
				break;
			}
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
			float x = event.getX();
			float y = event.getY();

			//MENU
			if (state == GameState.MENU){
				//Touching the start button
				if (startButt.isTouching(x, y)){
					//Run the game
					state = GameState.START;
					return true;
				}
			}
			//START GAME
			if (state == GameState.START){
				state = GameState.RUNNING;
				return true;
			}
			//RESTART GAME
			if (state == GameState.GAMEOVER && splash && replayButt.isTouching(x,y)){
				resetGame();
				return true;
			}
			//RUNNING GAME
			if (state == GameState.RUNNING){
				//Play sound
				soundEffects.play(upSound, 1, 1, 0, 0, 1);

				//Instruct to move up
				birdy.pressedUp();
			}

			return true;
		}
		return false;
	}

	/**
	 * Fall Animation - Plummets into it's death
	 */
	public void deadFallAnimation(){

		//Make it jump first then fall
		if (fall){
			birdy.pressedUp();
			fall = false;
			splashSoundOn = true;
		}
		//Splash Animation
		if (birdy.getImage().getHeight() + birdy.getY() > ground1.getY()){			
			splash = true;
			splashY -= 2;
			if (alpha > 3){
				alpha -= 3;
			}
		}
		//Fall Animation
		if (birdy.getImage().getHeight() + birdy.getY() < ground1.getY() + birdy.getImage().getHeight()){

			birdy.clockTick();

			//90 degrees max
			if (angle < 75){
				birdy.setAngle(angle);
				angle += 0.2;
			}
		} else {
			//Play sound
			if (splashSoundOn){
				soundEffects.play(splashSound, 1, 1, 0, 0, 1);
				splashSoundOn = false;
			}
		}
	}

	public void resetGame(){
		state = GameState.START;
		//reset gap
		gapCounter = 0;
		angle = 0;
		//reset splash
		splash = false;
		splashY = GAME_HEIGHT-lavaSize;
		alpha = 255;
		//reset starting gap for pipes
		startPipes = false;
		startCounter = 0;
		fall = false;
		birdy.reset();
		obs.reset();
		obs2.reset();
		topObs.reset();
		topObs2.reset();
		ground1.reset();
		ground2.reset();
		//android.os.Process.killProcess(android.os.Process.myPid());
	}



}
