package ru.hse.shugurov;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class HSETracker {
	public static void sendEvent(Context context, String action, String label, Long value){
		 // property ID.
		  EasyTracker easyTracker = EasyTracker.getInstance(context);
		  // that are set and sent with the hit.
		  easyTracker.send(MapBuilder
		      .createEvent("event",     // Event category (required)
		                  action,  // Event action (required)
		                  label,   // Event label
		                   value)            // Event value
		      .build()
		  );
	}
	public static void sendEvent(Context context, String action, String label){
		 sendEvent(context, action, label, null);
	}
	public static void sendEvent(Context context, String action){
		sendEvent(context, action, null, null);
	}
	public static void sendViewReport(){
	
	}
	

}
