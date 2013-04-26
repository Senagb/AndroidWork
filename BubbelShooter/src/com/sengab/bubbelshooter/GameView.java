package com.sengab.bubbelshooter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView {
	LinkedList<ball[]> container;
	final String TAG = "Game View";
	SurfaceHolder holder;
	static MainThread gameLoopThread;
	static float x2 = 0;
	static float y2 = 0;
	int radius = 0;
	static float slope;
	static boolean isMoving = false;
	static boolean isLeft = false;
	static boolean isRight = false;
	static boolean isEqual = false;
	static boolean isDied = false; // will be used if the running bubble end
	static int speed;
	factoryPool factory;

	// SoundManager soundManager = new SoundManager(this.getContext());

	public GameView(Context context, int speed) {
		super(context);
		setFocusable(true);
		this.setFocusableInTouchMode(true);

		gameLoopThread = new MainThread(this, speed);
		holder = getHolder();
		factory = factoryPool.getInstance();
		holder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				boolean retry = true;
				gameLoopThread.setRunning(false);
				while (retry) {
					try {
						gameLoopThread.join();
						retry = false;
					} catch (InterruptedException e) {
					}
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				gameLoopThread.setRunning(true);
				gameLoopThread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});
	}

	long starttime, counter;

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		counter++;
		if (counter == 1) {
			starttime = SystemClock.elapsedRealtime();
		} else if (counter == 100) {
			long endtime = SystemClock.elapsedRealtime();
			double res = 100 / ((endtime - starttime) * Math.pow(10, -3));
			Log.d(TAG, "fps = " + res);
		}
		Queue<ball> p = GameActivity.getPlayer();
		radius = p.peek().radius;

		// Log.d(TAG, "Start drawing");
		Paint paint = new Paint();

		Resources res = getResources();
		Bitmap background = BitmapFactory.decodeResource(res, R.drawable.wall);
		Bitmap sBackground = Bitmap.createScaledBitmap(background, getWidth(),
				getHeight(), true);
		// canvas.drawColor(0xFFAAAAAA);
		canvas.drawBitmap(sBackground, 0, 0, paint);

		container = GameActivity.getContainer();
		paint.setColor(Color.BLACK);
		canvas.drawRect(0, 0, getWidth(), 70, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(30);
		canvas.drawText("Score is " + GameActivity.score, getWidth() / 2 - 100,
				35, paint);

		int space_y = 70;
		int space_x = 0;
		int counter = 0;
		for (int i = 0; i < GameActivity.container.size(); i++) {
			ball[] b = GameActivity.container.get(i);
			counter = 0;
			for (int j = 0; j < GameActivity.maxColumns; j++) {
				if (b[j] != null) {
					paint.setColor(b[j].c);
					b[j].x = radius + space_x;
					b[j].y = radius + space_y;
					// Log.d(TAG, "x: " + b[j].x + " y: " + b[j].y + " raduis: "
					// + b[j].radius);
					canvas.drawCircle(b[j].x, b[j].y, b[j].radius, paint);
				} else
					counter++;
				space_x += radius * 2;
			}
			if (counter == GameActivity.maxColumns
					&& i == GameActivity.container.size() - 1)
				GameActivity.container.remove(i);
			space_y += radius * 2;
			space_x = 0;
		}

		if (GameActivity.b != null) {
			paint.setColor(GameActivity.b.c);
			canvas.drawCircle(GameActivity.b.x, GameActivity.b.y,
					GameActivity.b.radius, paint);
		}

		Object[] player = GameActivity.getPlayer().toArray();
		for (int i = 0; i < player.length; i++) {
			ball b = (ball) player[i];
			paint.setColor(b.c);
			canvas.drawCircle(b.x - i * 2 * b.radius, b.y - b.radius, b.radius,
					paint);

		}
		Random rand = new Random();
		for (int k = 0; k < GameActivity.fallingBalls.size(); k++) {
			ball b = GameActivity.fallingBalls.get(k);
			if (rand.nextInt(10) % 4 == 0) {
				paint.setColor(b.c);
			} else {
				paint.setColor(Color.WHITE);
			}

			canvas.drawCircle(b.x, b.y, b.radius, paint);

		}

		if (GameActivity.win) {
			res = getResources();
			Bitmap winner = BitmapFactory.decodeResource(res,
					R.drawable.win_panel);
			Bitmap swinner = Bitmap.createScaledBitmap(winner, getWidth(),
					winner.getHeight(), true);
			// canvas.drawColor(0xFFAAAAAA);
			canvas.drawBitmap(swinner, 0,
					(getHeight() / 2) - swinner.getHeight(), paint);
		}
		if (GameActivity.lose) {
			res = getResources();
			Bitmap loser = BitmapFactory.decodeResource(res,
					R.drawable.lose_panel);
			Bitmap sloser = Bitmap.createScaledBitmap(loser, getWidth(),
					loser.getHeight(), true);
			// canvas.drawColor(0xFFAAAAAA);
			canvas.drawBitmap(sloser, 0,
					(getHeight() / 2) - sloser.getHeight(), paint);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (!isMoving) {
			if (event.getAction() != MotionEvent.ACTION_DOWN)
				return super.onTouchEvent(event);
			// TODO Auto-generated method stub
			GameActivity.b = GameActivity.player.poll();
			GameActivity.b.y -= GameActivity.b.radius;
			GameActivity.soundManager.playSound(MainThread.SOUND_TOUCH);
			isEqual = false;
			isRight = false;
			isLeft = false;
			slope = 0;
			x2 = 0;
			y2 = 0;
			x2 = (int) event.getX();
			if (Math.abs(x2 - (getWidth() / 2)) <= 20)
				x2 = getWidth() / 2;
			if (x2 > (getWidth() / 2))
				isRight = true;
			else if (x2 < (getWidth() / 2))
				isLeft = true;
			else if (x2 == getWidth() / 2)
				isEqual = true;
			y2 = (int) event.getY();
			slope = (y2 - GameActivity.b.y) / (x2 - GameActivity.b.x);
			if (slope <= 1 && slope >= -1)
				speed = 21;
			else if ((slope > 1 && slope <= 3) || (slope >= -3 && slope < -1))
				speed = 11;
			else if ((slope > 3 && slope <= 7) || (slope >= -7 && slope < -3))
				speed = 5;
			else if ((slope > 7 && slope <= 10) || (slope >= -10 && slope < -7))
				speed = 3;
			else if (slope > 10 || slope < -10)
				speed = 2;
			// Log.d(TAG, "slope: " + slope);
			ball b = GameActivity.factory.getBall();
			b.setRadius(GameActivity.diameter / 2);
			b.x = getWidth() / 2;
			b.y = getHeight();
			GameActivity.player.add(b);
			isMoving = true;
			MainThread.continueMan = true;
		}
		if (GameActivity.lose || GameActivity.win) {
			Intent intent = new Intent();
			intent.setClass(GameActivity.con, LevelChoice.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			GameActivity.con.startActivity(intent);
		}
		return super.onTouchEvent(event);
	}

	public Context getCont() {
		return this.getContext();
	}
}
