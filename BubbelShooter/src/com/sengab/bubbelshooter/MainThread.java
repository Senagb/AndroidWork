package com.sengab.bubbelshooter;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

public class MainThread extends Thread {
	private static final String TAG = MainThread.class.getSimpleName();
	private GameView view;
	private static boolean running = false;
	static boolean continueMan = false, first = true;
	private int countMarked = 0;
	Queue<ball> evaluationQueue;
	int speed;
	boolean isPlayed = false;

	public final static int SOUND_WON = 0;
	public final static int SOUND_LOST = 1;
	public final static int SOUND_COLLISION = 2;
	public final static int SOUND_FALLING = 3;
	public final static int SOUND_BACKGROUND = 4;
	public final static int SOUND_TOUCH = 5;
	public final static int NUM_SOUNDS = 6;

	private static boolean soundOn = true;

	public MainThread(GameView view, int speed) {
		this.view = view;
		this.speed = speed;
	}

	public void setRunning(boolean run) {
		running = run;
	}

	public static void stopThread(Boolean b) {
		running = b;
	}

	@Override
	public void run() {
		try {
			long tickCount = 0L;
			evaluationQueue = new LinkedList<ball>();
			Log.d(TAG, "Starting game loop");
			Canvas c = null;

			while (running) {
				if (GameActivity.container.isEmpty()) {
					GameActivity.win = true;
					GameActivity.soundManager.playSound(SOUND_WON);
				} else if (GameActivity.container.size() == 1) {
					int counter = 0;
					for (int i = 0; i < GameActivity.maxColumns; i++) {
						if (GameActivity.container.get(1)[i] == null)
							counter++;
					}
					if (counter == GameActivity.maxColumns) {
						GameActivity.win = true;
						GameActivity.soundManager.playSound(SOUND_WON);
					}
				} else if (GameActivity.container.size()
						* GameActivity.diameter >= GameActivity.height
						- GameActivity.diameter) {
					GameActivity.lose = true;
					GameActivity.soundManager.playSound(SOUND_LOST);
				}
				if (GameActivity.win || GameActivity.lose) {
					running = false;
					continue;
				}

				tickCount++;
				// GameActivity.soundManager.playSound(SOUND_BACKGROUND);

				updateMoving();
				updateFallingBalls(c);
				if (tickCount % speed == 0 && tickCount != 0)
					updateblock();
				if (tickCount % speed == 0 || GameView.isMoving || continueMan
						|| first || GameActivity.fallingBalls.size() > 0) {
					try {
						c = view.getHolder().lockCanvas();
						synchronized (view.getHolder()) {
							view.onDraw(c);
						}
					} finally {
						if (c != null) {
							view.getHolder().unlockCanvasAndPost(c);
						}
					}
					if (!GameView.isMoving)
						continueMan = false;
					if (first)
						first = false;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				c = view.getHolder().lockCanvas();
				synchronized (view.getHolder()) {
					view.onDraw(c);
				}
			} finally {
				if (c != null) {
					view.getHolder().unlockCanvasAndPost(c);
				}
			}
			Log.d(TAG, "Game loop executed " + tickCount + " times");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void updateFallingBalls(Canvas c) {
		for (int i = 0; i < GameActivity.fallingBalls.size(); i++) {
			ball g = GameActivity.fallingBalls.get(i);
			if (g.y - g.radius < GameActivity.height)
				GameActivity.fallingBalls.get(i).y = GameActivity.fallingBalls
						.get(i).y + 18;

			else {
				ball b = GameActivity.fallingBalls.remove(i);
				GameActivity.factory.disapper(b);

			}
		}
		if (GameActivity.fallingBalls.size() > 0) {
			GameActivity.soundManager.playSound(SOUND_FALLING);
		}
	}

	private void updateMoving() {
		if (GameActivity.b != null) {
			if (GameActivity.b.x + GameActivity.b.radius >= GameActivity.width) {

				GameView.isRight = false;
				GameView.isLeft = true;
				GameView.slope = -1 * GameView.slope;
				GameView.x2 = GameActivity.b.x - 500;
				GameView.y2 = (((-1 * GameView.slope)
						* (GameView.x2 - GameActivity.b.x) + GameActivity.b.y));

			} else if (GameActivity.b.x - GameActivity.b.radius <= 0) {

				GameView.isRight = true;
				GameView.isLeft = false;
				GameView.slope = -1 * GameView.slope;
				GameView.x2 = GameActivity.b.x + 500;
				GameView.y2 = (((-1 * GameView.slope)
						* (GameView.x2 - GameActivity.b.x) + GameActivity.b.y));
			}
			// try {
			checkCollison();

			if (GameActivity.b != null) {

				if (GameView.isRight) {
					GameActivity.b.x = GameActivity.b.x + GameView.speed;
					GameActivity.b.y = ((GameView.slope
							* (GameActivity.b.x - (GameActivity.b.x - GameView.speed)) + GameActivity.b.y));
					Log.d(TAG, "is Right speed x: " + GameActivity.b.x
							+ " speed y: " + GameActivity.b.y);
				} else if (GameView.isLeft) {
					GameActivity.b.x = GameActivity.b.x - GameView.speed;
					GameActivity.b.y = ((GameView.slope
							* (GameActivity.b.x - (GameActivity.b.x + GameView.speed)) + GameActivity.b.y));
					Log.d(TAG, "is Left speed x: " + GameActivity.b.x
							+ " speed y: " + GameActivity.b.y);
				} else if (GameView.isEqual) {

					GameActivity.b.y = GameActivity.b.y - 18;
					GameActivity.b.x = (GameActivity.width / 2);
					Log.d(TAG, "is Equal speed x: " + GameActivity.b.x
							+ " speed y: " + GameActivity.b.y);
				}
			}
		}

	}

	private void updateblock() {
		if (GameActivity.container.size() < GameActivity.maxRows) {

			ball[] b = new ball[GameActivity.maxColumns];
			for (int i = 0; i < GameActivity.maxColumns; i++) {
				b[i] = GameActivity.factory.getBall();
				b[i].setRadius(GameActivity.diameter / 2);
			}

			GameActivity.container.addFirst(b);
		}

	}

	private void checkCollison() {
		// boolean NullExists = false;
		float x_distance = Integer.MAX_VALUE;
		float y_distance = Integer.MAX_VALUE;
		ball touched = null;
		int row = 0, column = 0;
		for (int i = GameActivity.container.size() - 1; i >= 0; i--) {
			ball[] temp = GameActivity.container.get(i);
			for (int j = 0; j < temp.length; j++)
				if (temp[j] != null) {
					if (check(temp[j], GameActivity.b)) {
						if (x_distance >= Math
								.abs(GameActivity.b.x - temp[j].x)) {
							if (y_distance >= Math.abs(GameActivity.b.y
									- temp[j].y))
								x_distance = Math.abs(GameActivity.b.x
										- temp[j].x);
							y_distance = Math.abs(GameActivity.b.y - temp[j].y);
							touched = temp[j];
							row = i;
							column = j;

						}
					}
				}
			// else {
			// NullExists = true;
			// }
			// if (!NullExists)
			// break;

		}
		if (touched != null) {

			Log.d(TAG, "collision happended by row: " + row + " column: "
					+ column);
			float diff_x = Math.abs(GameActivity.b.x - touched.x);
			float sign_y = GameActivity.b.y - touched.y;

			if (diff_x <= touched.radius && diff_x >= 0) {
				Log.d(TAG, "up-down collision y sign: " + sign_y + " x diff:"
						+ diff_x);
				if (sign_y > 0) {
					if (GameActivity.container.size() > row + 1
							&& GameActivity.container.get(row + 1)[column] == null) {
						GameActivity.container.get(row + 1)[column] = GameActivity.b;
						GameActivity.b = null;
					} else {
						ball[] temp = new ball[GameActivity.maxColumns];
						temp[column] = GameActivity.b;
						GameActivity.b = null;
						GameActivity.container.addLast(temp);

					}
					checkScore(row + 1, column,
							GameActivity.container.get(row + 1)[column].c);
					updateContainer(countMarked);

				} else {

					if (GameActivity.container.get(row - 1)[column] == null) {
						GameActivity.container.get(row - 1)[column] = GameActivity.b;
						checkScore(row - 1, column, GameActivity.b.c);
						updateContainer(countMarked);
						GameActivity.b = null;
					} else {
						Log.d(TAG, "****************************3");
					}
				}

			} else if (diff_x > touched.radius && diff_x <= touched.radius * 2) {
				float sign = GameActivity.b.x - touched.x;
				Log.d(TAG, "left-right collision  diffx: " + diff_x + " sign: "
						+ sign);
				if (sign > 0) {
					if (column + 1 < GameActivity.maxColumns
							&& GameActivity.container.get(row)[column + 1] == null) {
						GameActivity.container.get(row)[column + 1] = GameActivity.b;
						checkScore(row, column + 1, GameActivity.b.c);
						updateContainer(countMarked);
						GameActivity.b = null;
					} else {
						Log.d(TAG, "****************************2");
					}
				} else {
					if (column - 1 >= 0
							&& GameActivity.container.get(row)[column - 1] == null) {
						GameActivity.container.get(row)[column - 1] = GameActivity.b;
						checkScore(row, column - 1, GameActivity.b.c);
						updateContainer(countMarked);
						GameActivity.b = null;
					} else {
						Log.d(TAG, "****************************1");
					}
				}

			}
			GameActivity.soundManager.playSound(SOUND_COLLISION);
			GameView.isMoving = false;
			countMarked = 0;

		} else {
			if (GameActivity.b != null
					&& GameActivity.b.y <= GameActivity.diameter + 70) {

				for (int i = 0; i * GameActivity.diameter < GameActivity.width; i++) {
					if (GameActivity.b.x > i
							&& GameActivity.b.x <= (i + 1)
									* GameActivity.diameter) {
						GameActivity.container.get(0)[i] = GameActivity.b;
						checkScore(0, i, GameActivity.b.c);
						updateContainer(countMarked);
						GameActivity.b = null;
						break;

					}
				}
				GameActivity.soundManager.playSound(SOUND_COLLISION);
				GameView.isMoving = false;
				countMarked = 0;
			}
		}

	}

	private void updateContainer(int count) {
		if (count <= 2) {
			for (int i = 0; i < GameActivity.container.size(); i++) {
				for (int j = 0; j < GameActivity.container.get(i).length; j++) {
					if (GameActivity.container.get(i)[j] != null
							&& GameActivity.container.get(i)[j].isMarked) {
						GameActivity.container.get(i)[j].isMarked = false;
					}

				}
			}
		} else {

			for (int i = 0; i < GameActivity.container.size(); i++) {
				for (int j = 0; j < GameActivity.container.get(i).length; j++) {
					if (GameActivity.container.get(i)[j] != null
							&& GameActivity.container.get(i)[j].isMarked) {
						GameActivity.container.get(i)[j].c = Color.BLACK;
						GameActivity.fallingBalls.add(GameActivity.container
								.get(i)[j]);
						GameActivity.container.get(i)[j] = null;
						if ((i + 1) < GameActivity.container.size()) {
							if (GameActivity.container.get(i + 1)[j] != null) {
								GameActivity.container.get(i + 1)[j].isMarked = true;

							}
						}

					}

				}
			}

			GameActivity.score += GameActivity.fallingBalls.size();
		}

	}

	private boolean check(ball still, ball moving) {
		int diff_x = (int) Math.abs(moving.x - still.x);
		int diff_y = (int) Math.abs(moving.y - still.y);
		return ((Math.pow(diff_x, 2) + Math.pow(diff_y, 2)) - moving.radius * 2
				* 2 * moving.radius) < 0;
	}

	private void checkScore(int i, int j, int c) {
		GameActivity.container.get(i)[j].isMarked = true;
		GameActivity.container.get(i)[j].xIndex = i;
		GameActivity.container.get(i)[j].yIndex = j;
		countMarked++;
		evaluationQueue.add(GameActivity.container.get(i)[j]);

		while (evaluationQueue.size() > 0) {
			ball b = evaluationQueue.poll();
			i = b.xIndex;
			j = b.yIndex;

			if ((i - 1) >= 0) {
				if (GameActivity.container.get(i - 1)[j] != null
						&& GameActivity.container.get(i - 1)[j].c == c
						&& (!(GameActivity.container.get(i - 1)[j].isMarked))) {
					GameActivity.container.get(i - 1)[j].isMarked = true;
					GameActivity.container.get(i - 1)[j].xIndex = i - 1;
					GameActivity.container.get(i - 1)[j].yIndex = j;
					evaluationQueue.add(GameActivity.container.get(i - 1)[j]);
					countMarked++;
				}
			}

			if ((i + 1) < GameActivity.container.size() && (i - 1)>=0) {
				if (GameActivity.container.get(i + 1)[j] != null
						&& GameActivity.container.get(i + 1)[j].c == c
						&& (!(GameActivity.container.get(i + 1)[j].isMarked))) {
					GameActivity.container.get(i + 1)[j].isMarked = true;
					GameActivity.container.get(i - 1)[j].xIndex = i + 1;
					GameActivity.container.get(i - 1)[j].yIndex = j;
					evaluationQueue.add(GameActivity.container.get(i + 1)[j]);
					countMarked++;
				}
			}

			if ((j + 1) < GameActivity.container.get(i).length) {
				if (GameActivity.container.get(i)[j + 1] != null
						&& GameActivity.container.get(i)[j + 1].c == c
						&& (!(GameActivity.container.get(i)[j + 1].isMarked))) {
					GameActivity.container.get(i)[j + 1].isMarked = true;
					GameActivity.container.get(i)[j + 1].xIndex = i;
					GameActivity.container.get(i)[j + 1].yIndex = j + 1;
					evaluationQueue.add(GameActivity.container.get(i)[j + 1]);
					countMarked++;
				}
			}

			if ((j - 1) >= 0) {
				if (GameActivity.container.get(i)[j - 1] != null
						&& GameActivity.container.get(i)[j - 1].c == c
						&& (!(GameActivity.container.get(i)[j - 1].isMarked))) {
					GameActivity.container.get(i)[j - 1].isMarked = true;
					GameActivity.container.get(i)[j - 1].xIndex = i;
					GameActivity.container.get(i)[j - 1].yIndex = j - 1;
					evaluationQueue.add(GameActivity.container.get(i)[j - 1]);
					countMarked++;
				}
			}
		}

	}

	public static boolean getSoundOn() {
		return soundOn;
	}

	public static void setSoundOn(boolean so) {
		soundOn = so;
	}

}
