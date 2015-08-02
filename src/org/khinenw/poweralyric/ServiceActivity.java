package org.khinenw.poweralyric;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ServiceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		toggleService();
		finish();
	}
	
	public void toggleService(){
		if(isServiceRunning(MainService.class)){
			//stop service
			stopService(new Intent(MainActivity.INTENT_NAME));
			Toast.makeText(this, this.getString(R.string.stopped_service), Toast.LENGTH_SHORT).show();
		}else{
			startService(new Intent(MainActivity.INTENT_NAME));
			Toast.makeText(this, this.getString(R.string.started_service), Toast.LENGTH_SHORT).show();
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
