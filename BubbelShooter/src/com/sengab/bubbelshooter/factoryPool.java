package com.sengab.bubbelshooter;

import java.util.Random;
import android.graphics.Color;

public class factoryPool {
	static factoryPool factory;
	ball[] balls;
	int[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA };
	static Random rand;

	static factoryPool getInstance() {

		if (factory == null) {
			factory = new factoryPool();
			rand = new Random();
		}
		return factory;
	}

	void generate(int num_balls) {
		balls = new ball[num_balls];
		for (int i = 0; i < balls.length; i++) {
			balls[i] = new ball(null, -1, -1,
					colors[rand.nextInt(colors.length)]);
			balls[i].isNew = true;
		}
	}

	ball getBall() {
		for (int i = 0; i < balls.length; i++) {
			if (balls[i].isNew) {
				balls[i].isNew = false;
				return balls[i];
			}
		}
		return null;
	}

	void disapper(ball b) {
		b.isNew = true;
		b.isBlocked = false;
		b.isMoving = false;
		b.isUser = false;
		b.c = colors[rand.nextInt(colors.length)];
		b.x = -1;
		b.y = -1;
		b.isMarked=false;
		b.xIndex=0;
		b.yIndex=0;
	}

}
