package menus;

import utility.clockThread;
import game.GameScreen;

import com.example.flappybirds.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainMenuScreen extends Activity {
	public static GameScreen game;
	public static clockThread clock;
	//Test
	public static AssetManager assets;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//No title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//find screen dimensions
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		GameScreen.GAME_WIDTH = size.x;
		GameScreen.GAME_HEIGHT = size.y;
		//Make new game
		assets = this.getAssets();
		game = new GameScreen(this);
		//Clock
		game.setOnTouchListener(game);
		clock = new clockThread(game, 5);
		//Set view
		setContentView(game);
		//Start thread
		clock.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu_screen, menu);
		return true;
	}
	@Override
	public void onBackPressed()
	{
	   // super.onBackPressed(); // Comment this super call to avoid calling finish()
	}

}
