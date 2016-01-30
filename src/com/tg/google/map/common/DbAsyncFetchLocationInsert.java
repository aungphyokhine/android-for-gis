package com.tg.google.map.common;


import java.util.Iterator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class DbAsyncFetchLocationInsert extends AsyncTask<String, String, Boolean>{
	private DBHelper db;
	private List<ModelFetchLocation> locations;
	private String uid;
	private OnResults listener;
	private ProgressDialog dialog;
	
	 public DbAsyncFetchLocationInsert(Context context,List<ModelFetchLocation> locations,String uid,OnResults listener) {
		super();
		db = new DBHelper(context);
		this.locations = locations;
		this.uid = uid;
		this.listener = listener;
		dialog = new ProgressDialog(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	    protected void onPreExecute() {
		dialog.setMessage("saving data, please wait.");
		try {
			//dialog.show();
		} catch (Exception e) {
			// WindowManager$BadTokenException will be caught and the app would
			// not display
			// the 'Force Close' message
		}
	    }
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
		
		try {
			
			db.addFetchLocation(locations);
			
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		

		return true;
	}
	
	@Override
	 protected void onPostExecute(Boolean success) {
		 try {
//				if (dialog != null) {
//					dialog.dismiss();
//
//				}
			} catch (Exception e) {
				// WindowManager$BadTokenException will be caught and the app would
				// not display
				// the 'Force Close' message
			}
		 if(success){
			 listener.onTaskResult(new ModelResult(success, "", "200"));
		 }
		 else{
			 listener.onTaskResult(new ModelResult(false, "Inserting Fail", "500"));
		 }
			
	}

}
