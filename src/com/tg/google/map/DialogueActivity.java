package com.tg.google.map;

import java.util.concurrent.ExecutionException;

import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.OnResults;
import com.tg.google.map.common.PostAsyncAcceptRequest;
import com.tg.google.map.common.PostAsyncFriendRequest;
import com.tg.google.map.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class DialogueActivity extends Activity {
	
	private DBHelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DBHelper(getApplicationContext());		
		final String token = db.getAppData().getToken();
		
		String message = (String) getIntent().getExtras().getString("message");
		final String email = (String) getIntent().getExtras().getString("email");
		Log.d("Dialogue res : ", message);

		setContentView(R.layout.activity_dialogue);
		TextView messagetext = (TextView)this.findViewById(R.id.fr_text_message);
		messagetext.setText(message);
		 this.findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					PostAsyncAcceptRequest acceptrequest = new PostAsyncAcceptRequest(DialogueActivity.this,new OnResults() {
						
						@Override
						public void onTaskResult(ModelResult result) {
							// TODO Auto-generated method stub
							if(result.getSuccess()){
								
								Intent intent = new Intent(DialogueActivity.this, MainActivity.class);
							    startActivity(intent);      
							}
						}
					});
					
					acceptrequest.execute(email,token);
					
					
				}
		    	 
		     });
	}
	
	



	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		
	}



}
