package com.tg.google.map;

import java.util.concurrent.ExecutionException;

import com.tg.google.map.common.PostAsyncAddLocation;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.OnResults;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiverPostLocation extends BroadcastReceiver {
	private static int count = 1;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.d("AlarmReceiverPostLocation",  "onReceive");
		if(context != null){}
		ModelResult data;
		
		PostAsyncAddLocation post = new PostAsyncAddLocation(context,new OnResults() {
			
			@Override
			public void onTaskResult(ModelResult data) {
				// TODO Auto-generated method stub
				if(data.getSuccess()){
					Log.d("post success", count + "");
				}
				else{
					Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		post.execute();
		
		
		
	}
	 
}