package com.sengab.bouncingball;

import android.graphics.Paint;

public class ball {

	private Paint paint;
	private int x, y, stepx, stepy;

	public ball(int x, int y, int stepx, int stepy, int c) {
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setColor(c);
		this.x = x;
		this.y = y;
		this.stepx = stepx;
		this.stepy = stepy;

	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getStepx() {
		return stepx;
	}

	public void setStepx(int stepx) {
		this.stepx = stepx;
	}

	public int getStepy() {
		return stepy;
	}

	public void setStepy(int stepy) {
		this.stepy = stepy;
	}

	public void move(int width, int height) {
		x += stepx;
		y += stepy;

		// ... Bounce the ball off the walls if necessary.
		if (x < 0) { // If at or beyond left side
			x = 30; // Place against edge and
			stepx = -stepx; // reverse direction.

		} else if (x > width - 30) { // If at or beyond right side
			x = width - 30; // Place against right edge.
			stepx = -stepx; // Reverse direction.
		}

		if (y < 0) { // if we're at top
			y = 30;
			stepy = -stepy;

		} else if (y > height - 30) { // if we're at bottom
			y = height - 30;
			stepy = -stepy;
		}
	}

}
