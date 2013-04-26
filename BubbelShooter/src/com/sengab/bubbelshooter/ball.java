package com.sengab.bubbelshooter;

import android.graphics.Bitmap;

public class ball implements Cloneable {

	Bitmap image;
	float x, y;
	int c;
	int radius,xIndex,yIndex;
	boolean isNew, isBlocked, isMoving, isUser,isMarked;

	public ball(Bitmap im, float X, float Y, int my_c) {
		x = X;
		y = Y;
		image = im;
		c = my_c;
		isMarked=false;
		xIndex=0;
		yIndex=0;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		ball b = new ball(this.image, this.x, this.y, this.c);
		return b;
	}

}
