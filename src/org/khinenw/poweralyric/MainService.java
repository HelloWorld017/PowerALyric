package org.khinenw.poweralyric;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import com.maxmpz.poweramp.player.PowerampAPI;
import com.maxmpz.poweramp.player.RemoteTrackTime;
import com.maxmpz.poweramp.player.RemoteTrackTime.TrackTimeListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service{

	private RemoteTrackTime mRemoteTrackTime;
	private Intent mTrackIntent;
	private Intent mStatusIntent;
	private Map<Integer, String> currentLyric = new TreeMap<Integer, String>();
	private PopupViewer lyricViewer;
	
	public final boolean DEBUG_MODE = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		lyricViewer = new PopupViewer(this);
		lyricViewer.show();
		mRemoteTrackTime = new RemoteTrackTime(this);
		mRemoteTrackTime.setTrackTimeListener(new TrackTimeListener(){
			
			@Override
			public void onTrackDurationChanged(int duration) {}

			@Override
			public void onTrackPositionChanged(int position) {
				
				if(currentLyric.containsKey(position)){
					if(DEBUG_MODE) Log.d("POWERAMPTEST", "LYRIC : " + currentLyric.get(position));
					lyricViewer.setText(currentLyric.get(position));
				}
				
				if(DEBUG_MODE) Log.d("POWERAMPTEST", "POSITION : " + position);
			}
			
		});
		mRemoteTrackTime.registerAndLoadStatus();
		
		mTrackIntent = registerReceiver(mTrackReceiver, new IntentFilter(PowerampAPI.ACTION_TRACK_CHANGED));
		mStatusIntent = registerReceiver(mStatusReceiver, new IntentFilter(PowerampAPI.ACTION_STATUS_CHANGED));
	}
	
	private BroadcastReceiver mTrackReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			mTrackIntent = intent;
			lyricViewer.setText(" ");
			if(DEBUG_MODE) Log.d("POWERAMPTEST", "TRACK_MOVED");
			startService(PowerampAPI.newAPIIntent().putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.POS_SYNC));	
			try{
				Bundle trackData = intent.getBundleExtra(PowerampAPI.TRACK);
				String hash = LyricLib.getHash(new File(trackData.getString(PowerampAPI.Track.PATH)));
				if(DEBUG_MODE) Log.d("POWERAMPTEST", "LYRIC_HASH : " + hash);
				LyricTask task = new LyricTask();
				task.execute(hash);
			}catch(Exception e) {
				if(DEBUG_MODE) Log.e("POWERAMPTEST", Log.getStackTraceString(e));
			}
		}
	};
	
	private BroadcastReceiver mStatusReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			mStatusIntent = intent;
			
			if(intent.getBooleanExtra(PowerampAPI.PAUSED, true)){
				mRemoteTrackTime.stopSongProgress();
			}else{
				mRemoteTrackTime.startSongProgress();
			}
			
			int pos = intent.getIntExtra(PowerampAPI.Track.POSITION, -1);
			if(pos != -1) {
				mRemoteTrackTime.updateTrackPosition(pos);
			}
		}
	};
	
	public void onDestroy(){
		super.onDestroy();
		if(lyricViewer != null){
			lyricViewer.dismiss();
		}
		
		mRemoteTrackTime.unregister();
		try{
			if(mTrackIntent != null) {
				unregisterReceiver(mTrackReceiver);
			}
			
			if(mStatusIntent != null) {
				unregisterReceiver(mStatusReceiver);
			}
		} catch(Exception e){}
	}
	
	private class LyricTask extends AsyncTask<String, Void, Void>{
		@Override
		protected Void doInBackground(String... params) {
			try{
				currentLyric = LyricLib.parseLyric(LyricLib.getLyric(params[0]));
			}catch (Exception e){
				if(DEBUG_MODE) Log.d("POWERAMPTEST", "LYRIC_LOADED : " + currentLyric.size());
			}
			
			return null;
		}
	}

}

