package com.sengab.bubbelshooter;



import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager {

	  private SoundPool soundPool;
	  private int[] sm;
	  Context context;
	  
	  public SoundManager(Context context) {
		    this.context = context;
		    soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		    sm = new int[MainThread.NUM_SOUNDS];
		    sm[MainThread.SOUND_WON] = soundPool.load(context, R.raw.applause, 1);
		    sm[MainThread.SOUND_LOST] = soundPool.load(context, R.raw.lose, 1);
		    sm[MainThread.SOUND_COLLISION] = soundPool.load(context, R.raw.hit, 1);
		    sm[MainThread.SOUND_FALLING] =
		        soundPool.load(context, R.raw.explosion,1);
		    sm[MainThread.SOUND_BACKGROUND] =
		        soundPool.load(context, R.raw.hs, 1);
		    sm[MainThread.SOUND_TOUCH] = soundPool.load(context, R.raw.touch, 1);
		  }
	  
	  public final void playSound(int sound) {
		    if (MainThread.getSoundOn()) {
		      AudioManager mgr = (AudioManager)context.getSystemService(
		          Context.AUDIO_SERVICE);  
		      float streamVolumeCurrent =
		          mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		      float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		      float volume = streamVolumeCurrent / streamVolumeMax;
		      soundPool.play(sm[sound], volume, volume, 1, 0, 1f);
		    }
		  }

		  public final void cleanUp() {
		    sm = null;
		    context = null;
		    soundPool.release();
		    soundPool = null;
		  }
}
