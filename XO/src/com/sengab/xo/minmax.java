package com.sengab.xo;


public class minmax {

	public static int[] minmax_algorithm(int[][] grid, int computer, int player) {
		int[][] fake_grid = grid.clone();
		int free_cells = 0;

		for (int i = 0; i < fake_grid.length; i++) {
			for (int j = 0; j < fake_grid.length; j++) {
				if (fake_grid[i][j] == 0)
					free_cells++;
			}
		}
		int[] pos = new int[2];
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < fake_grid.length; i++) {
			for (int j = 0; j < fake_grid.length; j++) {
				if (fake_grid[i][j] == 0) {
					fake_grid[i][j] = computer;
					int val = min(fake_grid, computer, player, free_cells);
					System.out.println(val);
					fake_grid[i][j] = 0;
					if (val > max) {
						max = val;
						pos[0] = i;
						pos[1] = j;
					}
				}
			}
		}
		// System.out.println(pos[0] + " " + pos[1]);
		return pos;
	}

	private static int max(int[][] fake_grid, int computer, int player,
			int free_cells) {
		if (winner(fake_grid) == player) {
			return -1 * free_cells;
		}
		if (free_cells <= 0) {
			return 0;
		} else {
			int holder = Integer.MIN_VALUE;
			for (int i = 0; i < fake_grid.length; i++) {
				for (int j = 0; j < fake_grid.length; j++) {
					if (fake_grid[i][j] == 0) {
						fake_grid[i][j] = computer;
						holder = Math.max(
								holder,
								min(fake_grid, computer, player,
										(free_cells - 1)));
						fake_grid[i][j] = 0;
					}
				}
			}

			return holder;
		}

	}

	private static int min(int[][] fake_grid, int computer, int player,
			int free_cells) {
		if (winner(fake_grid) == computer)
			return 1 * free_cells;
		if (free_cells <= 0) {
			return 0;
		} else {
			int holder = Integer.MAX_VALUE;
			for (int i = 0; i < fake_grid.length; i++) {
				for (int j = 0; j < fake_grid.length; j++) {
					if (fake_grid[i][j] == 0) {
						fake_grid[i][j] = player;
						holder = Math.min(
								holder,
								max(fake_grid, computer, player,
										(free_cells - 1)));
						fake_grid[i][j] = 0;
					}
				}
			}
			return holder;
		}

	}

	private static int winner(int[][] fake_grid) {
		if (fake_grid[0][0] == fake_grid[0][1]
				&& fake_grid[0][1] == fake_grid[0][2] && fake_grid[0][0] > 0) {

			return fake_grid[0][0];
		}
		if (fake_grid[1][0] == fake_grid[1][1]
				&& fake_grid[1][1] == fake_grid[1][2] && fake_grid[1][0] > 0) {
			return fake_grid[1][0];

		}
		if (fake_grid[2][0] == fake_grid[2][1]
				&& fake_grid[2][1] == fake_grid[2][2] && fake_grid[2][0] > 0) {
			return fake_grid[2][0];
		}
		if (fake_grid[0][0] == fake_grid[1][0]
				&& fake_grid[1][0] == fake_grid[2][0] && fake_grid[0][0] > 0) {
			return fake_grid[0][0];
		}
		if (fake_grid[0][1] == fake_grid[1][1]
				&& fake_grid[1][1] == fake_grid[2][1] && fake_grid[0][0] > 0) {
			return fake_grid[0][1];
		}
		if (fake_grid[0][2] == fake_grid[1][2]
				&& fake_grid[1][2] == fake_grid[2][2] && fake_grid[0][2] > 0) {
			return fake_grid[0][2];
		}
		if (fake_grid[0][0] == fake_grid[1][1]
				&& fake_grid[1][1] == fake_grid[2][2] && fake_grid[0][0] > 0) {
			return fake_grid[0][0];
		}
		if (fake_grid[0][2] == fake_grid[1][1]
				&& fake_grid[1][1] == fake_grid[2][0] && fake_grid[0][2] > 0) {
			return fake_grid[0][2];
		}
		return 0;
	}
}
