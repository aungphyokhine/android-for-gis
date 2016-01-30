package com.tg.google.map;


import com.tg.google.map.common.DbAsyncCacheLocationInsert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiverCheckLocation extends BroadcastReceiver {
	private static int count = 1;
	@Override
	 	public void onReceive(Context arg0, Intent arg1) {
	 		// For our recurring task, we'll just display a message
	 		Toast.makeText(arg0, "I'm running : " + count, Toast.LENGTH_SHORT).show();
	 		count++;
	 		new DbAsyncCacheLocationInsert(arg0).execute();
	 		
	 	}
	 
}
