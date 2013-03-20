package com.sengab.bouncingball;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	final String TAG = "Game";
	private int width, height;
	private Paint background;
	private ball[] balls;

	Handler viewHandler = new Handler();
	Runnable updateView = new Runnable() {

		@Override
		public void run() {
			Log.d(TAG, "starting the changing thread");
			for (int i = 0; i < balls.length; i++) {
				balls[i].move(width, height);
			}

			invalidate();
			viewHandler.postDelayed(updateView, 35);

		}
	};

	public GameView(Context context) {
		super(context);
		Random rand = new Random();
		balls = new ball[30];
		for (int i = 0; i < balls.length; i++) {
			balls[i] = new ball(rand.nextInt(), rand.nextInt(),
					rand.nextInt(90), rand.nextInt(90), rand.nextInt());
		}
		background = new Paint();
		background.setColor(Color.GRAY);
		setFocusable(true);
		this.setFocusableInTouchMode(true);
		Log.d(TAG, "finished inisilaization");

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		width = (w < h) ? w : h;
		height = (w < h) ? h : w; // for now square mazes
		Log.d(TAG, "Changes the dimensions");
		viewHandler.post(updateView);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(0, 0, width, height, background);
		for (int i = 0; i < balls.length; i++) {

			canvas.drawCircle(balls[i].getX(), balls[i].getY(), 30,
					balls[i].getPaint());
		}
		Log.d(TAG, "drawn");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int x = (int) event.getX();
		int y = (int) event.getY();

		invalidate();// to tell that the scene is invalid
		Log.d(TAG, "Touch event with x: " + x + " and y : " + y);
		return true;
	}
}
