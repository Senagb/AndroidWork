package com.sengab.bouncingball;

import android.app.Activity;
import android.os.Bundle;

public class Game extends Activity {
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		GameView view = new GameView(this);
		setContentView(view);
	}
}
