package utility;

import game.GameScreen;

public class clockThread extends Thread{
	private int delay;
	private GameScreen game;

	public clockThread(GameScreen game, int delay){
		this.delay = delay;
		this.game = game;
	}


	public void run() {
	
		while (true){
			try {
				Thread.sleep(delay);
					//game clock
					game.clockTick();
					//redraw
					game.postInvalidate();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
