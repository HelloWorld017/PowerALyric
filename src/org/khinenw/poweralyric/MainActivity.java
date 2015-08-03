package org.khinenw.poweralyric;

import org.khinenw.poweralyric.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String METHOD_NAME = "METHOD_NAME";
	public static final String METHOD_NAME_TOGGLE = "TOGGLE";
	public static final String INTENT_NAME = "org.khinenw.poweralyric.toggle";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		((Button) findViewById(R.id.toggle_service)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				toggleService();
			}
			
		});
		
		((Button) findViewById(R.id.toggle_debug)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(MainService.DEBUG_MODE){
					MainService.DEBUG_MODE = false;
					Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.debug_mode_off), Toast.LENGTH_SHORT).show();
				}else{
					MainService.DEBUG_MODE = true;
					Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.debug_mode_on), Toast.LENGTH_SHORT).show();
				}
			}
			
		});
	}
	
	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		
		if(intent.getStringExtra(METHOD_NAME).equals(METHOD_NAME_TOGGLE)){
			toggleService();
		}
	}
	
	/*public void shortcutInstall(){
		Intent shortcutIntent = new Intent(this, MainActivity.class);
		shortcutIntent.putExtra(METHOD_NAME, METHOD_NAME_TOGGLE);
		
		Intent installIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		installIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		installIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, this.getString(R.string.toggle_service));
		installIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
		sendBroadcast(installIntent);
	}*/
	
	public void toggleService(){
		if(isServiceRunning(MainService.class)){
			//stop service
			stopService(new Intent(INTENT_NAME));
			((TextView) findViewById(R.id.toggle_service)).setText(R.string.start_service);
		}else{
			startService(new Intent(INTENT_NAME));
			((TextView) findViewById(R.id.toggle_service)).setText(R.string.stop_service);
		}
	}
	
	private boolean isServiceRunning(Class<? extends Service> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    
	    return false;
	}
}
