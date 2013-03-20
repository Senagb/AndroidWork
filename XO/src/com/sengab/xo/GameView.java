package com.sengab.xo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
	final String TAG = "Game";

	private int width, height, Xscore, Yscore, margin = 42, Ties;
	private static Paint background, grid_color, text;
	private int[][] points = new int[8][2];
	private String now_player = "X";
	int map_arr[][] = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } }; // friend and
																	// enemy map
																	// initialization.
	private SoundPool sounds;
	private int sExplosion;
	int analysis_arr[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 },
			{ 0, 0 }, { 0, 0 }, { 0, 0 } }; // analysis_arr
	private int sCongratulations;
	private int[][] grid = new int[3][3];
	private Bitmap X, O;
	private Context x;
	private boolean playComputer = true;
	private boolean turn = false;
	private int counter = 1;
	private int mode = 1;
	private int[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.WHITE };
	private int[] X_colors = { R.drawable.x, R.drawable.x_green,
			R.drawable.x_blue, R.drawable.x_white };
	private int[] O_colors = { R.drawable.o, R.drawable.o_green,
			R.drawable.o_blue, R.drawable.o_white };

	public GameView(Context context) {
		super(context);
		X = BitmapFactory.decodeResource(getResources(), X_colors[Game.getX()]);
		O = BitmapFactory.decodeResource(getResources(), O_colors[Game.getO()]);
		this.x = context;
		grid_color = new Paint();
		background = new Paint();
		text = new Paint();
		sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		sExplosion = sounds.load(context, R.raw.explosion, 1);
		sCongratulations = sounds.load(context, R.raw.congratulations, 1);
		grid_color.setStrokeWidth(5);
		text.setColor(Color.WHITE);
		text.setTextSize(20);
		setFocusable(true);
		this.setFocusableInTouchMode(true);
		boolean isComputer = Game.isGame_mode();
		if (!isComputer)
			mode = 0;
		else
			mode = 1;
		Log.d(TAG, "finished inisilaization");

	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		width = (w < h) ? w : h;
		height = (w < h) ? h : w;
		points[0][0] = margin;
		points[0][1] = margin;

		points[1][0] = width - margin;
		points[1][1] = margin;

		points[2][0] = width - margin;
		points[2][1] = width - margin;

		points[3][0] = margin;
		points[3][1] = width - margin;

		points[4][0] = (width - margin) / 3 + 20;
		points[4][1] = margin;

		points[5][0] = (((width - margin) / 3) * 2) + 10;
		points[5][1] = margin;

		points[6][0] = ((width - margin) / 3) + 20;
		points[6][1] = width - margin;

		points[7][0] = (((width - margin) / 3) * 2) + 10;
		points[7][1] = width - margin;

		Log.d(TAG, "Changes the dimensions");
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		X = BitmapFactory.decodeResource(getResources(), X_colors[Game.getX()]);
		O = BitmapFactory.decodeResource(getResources(), O_colors[Game.getO()]);
		grid_color.setColor(colors[Game.getGrid_color()]);
		background.setColor(colors[Game.getBackground()]);
		text.setColor(colors[Game.getGrid_color()]);
		// draw background
		canvas.drawRect(0, 0, width, height, background);

		// draw grid
		canvas.drawLine(points[0][0], points[0][1], points[1][0], points[1][1],
				grid_color);
		canvas.drawLine(points[1][0], points[1][1], points[2][0], points[2][1],
				grid_color);
		canvas.drawLine(points[2][0], points[2][1], points[3][0], points[3][1],
				grid_color);
		canvas.drawLine(points[3][0], points[3][1], points[0][0], points[0][1],
				grid_color);

		canvas.drawLine(points[4][0], points[4][1], points[6][0], points[6][1],
				grid_color);

		canvas.drawLine(points[5][0], points[5][1], points[7][0], points[7][1],
				grid_color);

		canvas.drawLine(points[4][1], points[4][0], points[6][1], points[6][0],
				grid_color);

		canvas.drawLine(points[5][1], points[5][0], points[7][1], points[7][0],
				grid_color);

		canvas.drawText("Player (" + now_player + ") turn", points[4][0],
				points[3][1] + margin, text);
		canvas.drawText(" X Player won (" + Xscore + ") times", points[3][0],
				points[3][1] + 2 * margin, text);
		canvas.drawText(" O Player won (" + Yscore + ") times", points[3][0],
				points[3][1] + 3 * margin, text);
		canvas.drawText(" Tie (" + Ties + ") times", points[3][0], points[3][1]
				+ 4 * margin, text);
		// draw X and O
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				Bitmap temp = null;
				if (grid[i][j] == 1) {
					temp = X;
				} else if (grid[i][j] == 2) {
					temp = O;
				}
				if (temp != null)
					if (i == 0 && j == 0)
						canvas.drawBitmap(temp, points[0][0], points[0][0],
								background);
					else if (i == 0 && j == 1)
						canvas.drawBitmap(temp, points[4][0], points[4][1],
								background);
					else if (i == 0 && j == 2)
						canvas.drawBitmap(temp, points[5][0], points[5][1],
								background);
					else if (i == 1 && j == 0)
						canvas.drawBitmap(temp, points[4][1], points[4][0],
								background);
					else if (i == 1 && j == 1)
						canvas.drawBitmap(temp, points[4][0], points[4][0],
								background);
					else if (i == 1 && j == 2)
						canvas.drawBitmap(temp, points[5][0], points[4][0],
								background);
					else if (i == 2 && j == 0)
						canvas.drawBitmap(temp, points[5][1], points[5][0],
								background);
					else if (i == 2 && j == 1)
						canvas.drawBitmap(temp, points[4][0], points[5][0],
								background);
					else if (i == 2 && j == 2)
						canvas.drawBitmap(temp, points[5][0], points[5][0],
								background);

			}
		}
		boolean result = result_check(counter);
		getResult(result, counter, false);
		if (!result && mode == 1 && turn) {
			if (playComputer) {
				// CompGame();
				int[] temp = minmax.minmax_algorithm(grid, 2, 1);
				grid[temp[0]][temp[1]] = 2;
				Log.d(TAG, "computer :" + temp[0] + " " + temp[1]);
				turn = false;
			}
			result = result_check(2);
			getResult(result, 2, true);
		}
		if (mode == 0) {
			if (turn) {
				if (counter == 2) {
					counter = 1;
				} else {
					counter = 2;
				}
				turn = false;
			}
		}

		Log.d(TAG, "done drawing");
	}

	public boolean grid_isFull() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (grid[i][j] == 0)
					return false;
		return true;
	}

	public void newGame() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				grid[i][j] = 0;

		// if(mode==1)CompGame();

	}

	public void set_score(int player_number) {

		if (player_number == 1)
			Xscore += 1;
		else if (player_number == 2)
			Yscore += 1;
		else if (player_number == 3)
			Ties += 1;

	}

	public void clear_score() {
		Xscore = 0;
		Yscore = 0;
		Ties = 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		int x = (int) event.getX();
		int y = (int) event.getY();
		// cell 0,0
		if (y > points[0][1] && y < points[4][0]) {

			if (x > points[0][0] && x < points[4][0] && grid[0][0] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[0][0] = 2;
					now_player = "X";
				} else {
					grid[0][0] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";
				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "0,0");
			}
			// cell 0,1
			else if (x > points[4][0] && x < points[5][0] && grid[0][1] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[0][1] = 2;
					now_player = "X";

				} else {
					grid[0][1] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";
				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "0,1");
			}
			// cell 0,2
			else if (x > points[5][0] && x < points[1][0] && grid[0][2] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[0][2] = 2;
					now_player = "X";

				} else {
					grid[0][2] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";
				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "0,2");
			}
		}
		// cell 1,0
		else if (y > points[4][1] && y < points[5][0]) {
			if (x > points[0][0] && x < points[4][0] && grid[1][0] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[1][0] = 2;
					now_player = "X";

				} else {
					grid[1][0] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";

				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "1,0");
			}
			// cell 1,1
			else if (x > points[4][0] && x < points[5][0] && grid[1][1] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[1][1] = 2;
					now_player = "X";

				} else {
					grid[1][1] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";

				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "1,1");
			}
			// cell 1,2
			else if (x > points[5][0] && x < points[1][0] && grid[1][2] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[1][2] = 2;
					now_player = "X";

				} else {
					grid[1][2] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";

				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "1,2");
			}
		}
		// cell 2,0
		else if (y > points[5][0] && y < points[3][1]) {
			if (x > points[0][0] && x < points[4][0] && grid[2][0] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[2][0] = 2;
					now_player = "X";

				} else {
					grid[2][0] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";

				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "2,0");
			}
			// cell 2,1
			else if (x > points[4][0] && x < points[5][0] && grid[2][1] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[2][1] = 2;
					now_player = "X";

				} else {
					grid[2][1] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";

				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "2,1");
			}
			// cell 2,2
			else if (x > points[5][0] && x < points[1][0] && grid[2][2] == 0) {
				turn = true;
				if (counter % 2 == 0) {
					grid[2][2] = 2;
					now_player = "X";
				} else {
					grid[2][2] = 1;
					if (mode == 0)
						now_player = "O";
					else
						now_player = "X";

				}
				if (!Game.isMute())
					sounds.play(sExplosion, 0.5f, 0.5f, 0, 0, 1.5f);
				playComputer = true;
				Log.d(TAG, "2,2");
			}
		}
		invalidate();// to tell that the scene is invalid

		Log.d(TAG, "Touch event with x: " + x + " and y : " + y);
		return true;
	}

	private void getResult(boolean result, int player, boolean computer) {

		if (result == true) {
			if (!Game.isMute())
				sounds.play(sCongratulations, 1.0f, 1.0f, 0, 0, 1.5f);
			// check for the player number.
			if (player == 1) {
				set_score(1);
				show_result("Congrats. X wins !!");
			} else {
				set_score(2);
				show_result("Congrats. O wins !!");

			}
			// newGame();

		} else if ((result == false) && grid_isFull()) {
			set_score(3);
			show_result("    Game Draw !    ");
			// newGame();
		} else if (computer)
			invalidate();

	}

	public boolean result_check(int player_local) {
		boolean win = true;
		int k = 0;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (grid[i][j] != player_local) {
					win = false;
					break;
				}
			}
			if (win == true) {
				return true;
			}
			win = true;
		}

		win = true;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (grid[j][i] != player_local) {
					win = false;
					break;
				}
			}
			if (win == true) {
				return true;
			}
			win = true;
		}

		win = true;

		for (int i = 0; i < 3; i++)
			if (grid[i][k++] != player_local) {
				win = false;
				break;
			}

		if (win == true) {
			return true;
		}

		k = 2;
		win = true;

		for (int i = 0; i < 3; i++)
			if (grid[i][k--] != player_local) {
				win = false;
				break;
			}

		if (win == true) {
			return true;
		}

		return false;
	}

	public boolean show_result(CharSequence message) {
		if (mode == 1) {
			counter = 1;
		}
		newGame();
		AlertDialog.Builder builder = new AlertDialog.Builder(x);
		builder.setCancelable(false);
		builder.setMessage(message).setPositiveButton("Continue",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						invalidate();
					}
				});
		AlertDialog alert = builder.create();

		alert.show();
		return true;
	}

	/**
	 * Master function for the computer's play (AI).
	 */
	public void CompGame() {
		// player = 1;
		// count++;
		analysis_array();
		if (easy_move_win() == true)
			return;
		else if (easy_move_block() == true)
			return;
		else {
			f_e_map();
			best_move();
		}

	}

	//
	// /**
	// * best move calculation : the f_e_map is traversed to see the highest
	// numbered
	// * (x, y) position and the move is made.
	// */
	public void best_move() {
		int highest = 0, k = 0; // k - increment the x_pos, y_pos.
		int pos[][] = { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 },
				{ 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } };
		int random_index = 0; // stores the random index number.
		int x = 0, y = 0; // compatibility with comp_play (int, int)

		// calculate the highest score in the map_arr.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (map_arr[i][j] > highest)
					highest = map_arr[i][j];

		// traverse map_arr and store all the highest score indices (x, y) in
		// pos[][].
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (map_arr[i][j] == highest) {
					pos[k][0] = i;
					pos[k][1] = j;
					k++;
				}

		// get a random index ( <= k ).
		random_index = ((int) (Math.random() * 10)) % (k);
		x = pos[random_index][0];
		y = pos[random_index][1];

		comp_play(x, y);
	}

	//
	// /**
	// * Creates a friend and enemy map, based on all available moves
	// * and the current position of the game.
	// *
	// * Searches for (1, 0) combination in analysis_array and then increment
	// * the corresponding row/col/diagonal in map_arr by 1. Also, the elements
	// * in map_arr with value = 0, are not changed.
	// *
	// */
	public void f_e_map() {
		int k = 0; // for diagonal traversal.

		// reset map_arr to all 1's every time function is called.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				map_arr[i][j] = 1;

		// search for existing moves and mark 0 in map_arr, if found in arr.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if ((grid[i][j] == 1) || (grid[i][j] == 2))
					map_arr[i][j] = 0;

		for (int i = 0; i < 8; i++) {
			if (((analysis_arr[i][0] == 1) && (analysis_arr[i][1] == 0))
					|| ((analysis_arr[i][0] == 0) && (analysis_arr[i][1] == 1)))
				if (i < 3) {
					for (int j = 0; j < 3; j++)
						if (map_arr[i][j] != 0)
							map_arr[i][j] += 1;
				} else if (i < 6) {
					for (int j = 0; j < 3; j++)
						if (map_arr[j][i - 3] != 0)
							map_arr[j][i - 3] += 1;
				} else if (i == 6) {
					k = 0;
					for (int m = 0; m < 3; m++) {
						if (map_arr[m][k] != 0)
							map_arr[m][k] += 1;
						k++;
					}
				} else if (i == 7) {
					k = 2;
					for (int m = 0; m < 3; m++) {
						if (map_arr[m][k] != 0)
							map_arr[m][k] += 1;
						k--;
					}
				}
		}
	}

	//
	// /**
	// * Easy move block function : searches the analysis_arr for (0, 2)
	// combination
	// * and makes the move if found, returning a true value.
	// *
	// * @return True if an easy Block Move is available.
	// */
	public boolean easy_move_block() {
		boolean flag = false; // temporary flag to indicate a (0, 2) find.
		int i, k = 0; // k used for diagonal search.
		// search analysis_arr for (0, 2) combination.
		for (i = 0; i < 8; i++)
			if ((analysis_arr[i][0] == 0) && (analysis_arr[i][1] == 2)) {
				flag = true;
				break;
			}

		if (flag == true) {
			// when position < 3, it is one of the 3 rows.
			if (i < 3) {
				// search for the vacant position
				for (int j = 0; j < 3; j++)
					if (grid[i][j] == 0) {
						comp_play(i, j);
						return true;
					}
			} else if (i < 6) {
				for (int j = 0; j < 3; j++)
					if (grid[j][i - 3] == 0) {
						comp_play(j, (i - 3));
						return true;
					}
			} else if (i == 6) {
				for (int j = 0; j < 3; j++) {
					if (grid[j][k] == 0) {
						comp_play(j, k);
						return true;
					}
					k++;
				}
			} else if (i == 7) {
				k = 2;
				for (int j = 0; j < 3; j++) {
					if (grid[j][k] == 0) {
						comp_play(j, k);
						return true;
					}
					k--;
				}
			}
		}
		return false; // false if easy move win is NOT available.
	}

	//
	// /**
	// * Easy move win function : searches the analysis_arr for (2,0)
	// combination
	// * and makes the move if found, returning a true value.
	// * @return True if an easy Win Move is available.
	// */
	public boolean easy_move_win() {
		boolean flag = false; // temporary flag to indicate a (2,0) find.
		int i, k = 0; // k used for diagonal search.
		// search analysis_arr for (2,0) combination.
		for (i = 0; i < 8; i++)
			if ((analysis_arr[i][0] == 2) && (analysis_arr[i][1] == 0)) {
				flag = true;
				break;
			}

		if (flag == true) {
			// when position < 3, it is one of the 3 rows.
			if (i < 3) {
				// search for the vacant position
				for (int j = 0; j < 3; j++)
					if (grid[i][j] == 0) {
						comp_play(i, j);
						return true;
					}
			} else if (i < 6) {
				for (int j = 0; j < 3; j++)
					if (grid[j][i - 3] == 0) {
						comp_play(j, (i - 3));
						return true;
					}
			} else if (i == 6) {
				for (int j = 0; j < 3; j++) {
					if (grid[j][k] == 0) {
						comp_play(j, k);
						return true;
					}
					k++;
				}
			} else if (i == 7) {
				k = 2;
				for (int j = 0; j < 3; j++) {
					if (grid[j][k] == 0) {
						comp_play(j, k);
						return true;
					}
					k--;
				}
			}
		}
		return false; // false if easy move win is NOT available.
	}

	//
	//
	// /**
	// * Make the computer's move.
	// * @param x : the x co-ordinate of the move to made.
	// * @param y : the y co-ordinate of the move to made.
	// */
	public void comp_play(int x, int y) {
		int value = 0;

		// set ib_id to exact ImageButton Id
		if ((x == 0) && (y == 0)) {
			// ib_id same as initialized value.
		} else {
			if (x == 0)
				value -= y; // minus '-' : because id number not in proper
							// order.
			else if (x == 1)
				value += (3 - y);
			else if (x == 2)
				value += (6 - y);
		}
		switch (value) {
		case 0: {
			if (grid[0][0] == 0)
				grid[0][0] = 2;
			else
				fill();
			now_player = "X";
		}
			break;
		case 1: {
			if (grid[0][1] == 0)
				grid[0][1] = 2;
			else
				fill();
			now_player = "X";
		}
			break;
		case 2: {
			if (grid[0][2] == 0)
				grid[0][2] = 2;
			else
				fill();

			now_player = "X";
		}
			break;
		case 3: {
			if (grid[1][0] == 0)
				grid[1][0] = 2;
			else
				fill();

			now_player = "X";
		}
			break;
		case 4: {
			if (grid[1][1] == 0)
				grid[1][1] = 2;
			else
				fill();
			now_player = "X";
		}
			break;
		case 5: {
			if (grid[1][2] == 0)
				grid[1][2] = 2;
			else
				fill();
			now_player = "X";
		}
			break;
		case 6: {
			if (grid[2][0] == 0)
				grid[2][0] = 2;
			else
				fill();
			now_player = "X";
		}
			break;
		case 7: {
			if (grid[2][1] == 0)
				grid[2][1] = 2;
			else
				fill();
			now_player = "X";
		}
			break;
		case 8: {
			if (grid[2][2] == 0)
				grid[2][2] = 2;
			else
				fill();
			now_player = "X";
		}
			break;
		default:
			fill();

		}
		playComputer = false;
		invalidate();
	}

	private void fill() {
		if (grid[0][0] == 0)
			grid[0][0] = 2;
		else if (grid[0][0] == 0)
			grid[0][0] = 2;
		else if (grid[0][1] == 0)
			grid[0][1] = 2;
		else if (grid[0][2] == 0)
			grid[0][2] = 2;
		else if (grid[1][0] == 0)
			grid[1][0] = 2;
		else if (grid[1][1] == 0)
			grid[1][1] = 2;
		else if (grid[1][2] == 0)
			grid[1][2] = 2;
		else if (grid[2][0] == 0)
			grid[2][0] = 2;
		else if (grid[2][1] == 0)
			grid[2][1] = 2;
		else if (grid[2][2] == 0)
			grid[2][2] = 2;
	}

	//
	// /**
	// * Function to set the analysis array.
	// * The analysis array stores the count of Friendly Positions and the Enemy
	// Positions in an
	// * 8 x 2 array. The first 3 rows refer to the 3 rows in the original 'arr'
	// array. The next 3
	// * refers to the 3 columns of the 'arr' and the last 2 rows of the
	// analysis array refers
	// * to the 2 diagonals in 'arr'. The original array 'arr' is traversed 3
	// times and then
	// * the values of the analysis array are incremented when and if an enemy
	// or friend is found.
	// */
	// /*
	// * F E
	// * ---------
	// * R1 | 0 | 0 |
	// * R2 | 0 | 0 |
	// * R3 | 0 | 0 |
	// * C1 | 0 | 0 |
	// * C2 | 0 | 0 |
	// * C3 | 0 | 0 |
	// * D1 | 0 | 0 |
	// * D2 | 0 | 0 |
	// * ---------
	// */
	public void analysis_array() {

		// Initialise to zero every time this function is called.
		for (int i = 0; i < 8; i++)
			analysis_arr[i][0] = analysis_arr[i][1] = 0;

		// row-wise traversal and increment the value.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (grid[i][j] == 1) // 1 = player 1 : computer
					analysis_arr[i][0] += 1;
				else if (grid[i][j] == 2) // 2 = player 2 : human
					analysis_arr[i][1] += 1;

		// column-wise traversal and increment the value.
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (grid[j][i] == 1) // 1 = player 1
					analysis_arr[i + 3][0] += 1;
				else if (grid[j][i] == 2) // 2 = player 2, i + 3 to change index
											// to refer to column.
					analysis_arr[i + 3][1] += 1;

		// diagonal 1 traversal.
		int k = 0;
		for (int i = 0; i < 3; i++) {
			if (grid[i][k] == 1)
				analysis_arr[6][0] += 1;
			else if (grid[i][k] == 2)
				analysis_arr[6][1] += 1;
			k++;
		}

		// diagonal 2 traversal.
		// --> reset k to point to the 1st row, and last(3rd) element.
		k = 2;
		for (int i = 0; i < 3; i++) {
			if (grid[i][k] == 1)
				analysis_arr[7][0] += 1;
			else if (grid[i][k] == 2)
				analysis_arr[7][1] += 1;
			k--;
		}

		// ------ end of analysis array initialization ------------- //
	}

}
