package com.sengab.bubbelshooter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

	static GameView view;
	static Queue<ball> player;
	static int maxColumns;
	static int maxRows;
	static LinkedList<ball[]> container;
	static LinkedList<ball> fallingBalls;
	static factoryPool factory;
	final String TAG = "Game Activity";
	static int diameter = 100, width, height;
	static ball b = null;
	static SoundManager soundManager;
	static boolean lose = false, win = false;
	static Context con;
	static int score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "start the game activity");
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth(); // deprecated
		height = display.getHeight(); // deprecated
		getSuitableRadius(width, height);
		fallingBalls = new LinkedList<ball>();
		container = new LinkedList<ball[]>();
		factory = factoryPool.getInstance();
		factory.generate((maxRows * maxColumns) + 30);
		soundManager = new SoundManager(this);
		lose = false;
		win = false;
		con = this;
		score = 0;
		b = null;
		GameView.isMoving = false;

		// GameActivity.soundManager.playSound(MainThread.SOUND_COLLISION);
		// /////////////////////////////
		int speed = getIntent().getExtras().getInt("rowSpeed");
		int num_row = getIntent().getExtras().getInt("rownum");

		for (int i = 0; i < num_row; i++) {
			ball[] b = new ball[maxColumns];
			for (int j = 0; j < maxColumns; j++) {
				b[j] = factory.getBall();
				b[j].setRadius(diameter / 2);
			}
			container.add(b);
		}
		player = new LinkedList<ball>();
		for (int i = 0; i < 3; i++) {
			ball b = factory.getBall();
			b.setRadius(diameter / 2);
			b.x = width / 2;
			b.y = height;
			player.add(b);

		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		memoryCrap();
		view = new GameView(this, speed);
		setContentView(view);

	}

	private void getSuitableRadius(int width, int height) {
		maxColumns = 8;
		while (width % maxColumns != 0) {
			maxColumns++;
		}
		diameter = width / maxColumns;
		maxRows = height / diameter;

	}

	public int getDiameter() {
		return diameter;
	}

	public static Queue<ball> getPlayer() {
		return player;
	}

	public static LinkedList<ball[]> getContainer() {
		return container;
	}

	public factoryPool getFactory() {
		return factory;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	public void onBackPressed() {
		MainThread.stopThread(false);
		this.finish();
	}

	public void memoryCrap() {
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);

		Log.i(TAG, " memoryInfo.availMem " + memoryInfo.availMem + "\n");
		Log.i(TAG, " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n");
		Log.i(TAG, " memoryInfo.threshold " + memoryInfo.threshold + "\n");

		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();

		Map<Integer, String> pidMap = new TreeMap<Integer, String>();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			pidMap.put(runningAppProcessInfo.pid,
					runningAppProcessInfo.processName);
		}

		Collection<Integer> keys = pidMap.keySet();

		for (int key : keys) {
			int pids[] = new int[1];
			pids[0] = key;
			android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager
					.getProcessMemoryInfo(pids);
			for (android.os.Debug.MemoryInfo pidMemoryInfo : memoryInfoArray) {
				Log.i(TAG, String.format("** MEMINFO in pid %d [%s] **\n",
						pids[0], pidMap.get(pids[0])));
				Log.i(TAG, " pidMemoryInfo.getTotalPrivateDirty(): "
						+ pidMemoryInfo.getTotalPrivateDirty() + "\n");
				Log.i(TAG,
						" pidMemoryInfo.getTotalPss(): "
								+ pidMemoryInfo.getTotalPss() + "\n");
				Log.i(TAG, " pidMemoryInfo.getTotalSharedDirty(): "
						+ pidMemoryInfo.getTotalSharedDirty() + "\n");
			}
		}
	}
}
