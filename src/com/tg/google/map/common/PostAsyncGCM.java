package com.tg.google.map.common;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class PostAsyncGCM extends  AsyncTask<String, String, ModelResult> {
	GoogleCloudMessaging gcm;
	String regid;
	String PROJECT_NUMBER = "381062206141";
	OnResults listener;
	Context context;
	DBHelper db;
	
	public PostAsyncGCM(Context context, OnResults listener) {
		super();
		db = new DBHelper(context);
		this.context = context;
		this.listener = listener;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected ModelResult doInBackground(String... arg0) {
		
		ModelAppDataJson appjson = db.getAppData();
		if (appjson.getGcmid() == null) {
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(PROJECT_NUMBER);
				msg = "Device registered, registration ID=" + regid;
				Log.i("GCM", msg);
				
				appjson.setGcmid(regid);
				db.saveAppData(appjson);
				ModelResult result = new ModelResult(true, regid, "200");
				
				return result;

			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				return new ModelResult(false, msg, "200");
			}

		} else {
			return new ModelResult(true, appjson.getGcmid(), "200");
		}
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onPostExecute(ModelResult result) {
		// TODO Auto-generated method stub
		
		
		listener.onTaskResult(result);	
		}
	
	

}
