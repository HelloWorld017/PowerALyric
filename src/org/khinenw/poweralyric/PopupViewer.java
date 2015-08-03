package org.khinenw.poweralyric;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class PopupViewer {
	
	private TextView lyricView;
	private View layout;
	private Context ctx;
	private boolean isDismissed = true;
	private WindowManager.LayoutParams params;
	
	public PopupViewer(Context ctx){
		
		layout = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lyric_view, null);
		
		lyricView = (TextView) layout.findViewById(R.id.lyricView);
		lyricView.setTextColor(Color.BLACK);
		layout.setBackgroundColor(Color.argb(128, 255, 255, 255));
		
		params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		
		params.gravity = Gravity.BOTTOM;
		this.ctx = ctx;
	}
	
	public void setText(String text){
		lyricView.setText(text);
	}
	
	public void dismiss(){
		if(!isDismissed){
			((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).removeView(layout);
			isDismissed = true;
		}
	}
	
	public void show(){
		if(isDismissed){
			((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).addView(layout, params);
			isDismissed = false;
		}
	}
	
}
