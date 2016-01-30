package com.tg.google.map.common;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class DbAsyncFetchLocationGet extends AsyncTask<String, String, List<ModelFetchLocation>>{
	private DBHelper db;
	private GPSTracker mGPS;
	private Context context;
	private ProgressDialog dialog;
	private OnDBFetchLocations listener;
	 public DbAsyncFetchLocationGet(Context context,final OnDBFetchLocations listener) {
		super();
		this.context = context;
		db = new DBHelper(this.context);
		dialog = new ProgressDialog(context);
		this.listener = listener;
		// TODO Auto-generated constructor stub
	}

	@Override
	    protected void onPreExecute() {
		dialog.setMessage("Doing something, please wait.");
		try {
			//dialog.show();
		} catch (Exception e) {
			// WindowManager$BadTokenException will be caught and the app would
			// not display
			// the 'Force Close' message
		}
	    }
	
	@Override
	protected List<ModelFetchLocation> doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
		
		try {
		 
			long from = Long.parseLong(arg0[0]);
			long to = Long.parseLong(arg0[1]);
			String email = arg0[2];
			List<ModelFetchLocation> locs =  db.getFetchLocations(from, to, email);
			Log.d("DbAsyncFetchLocationGet", locs.size() + "," + "from : " + from + ", to : " + to +" " + email);
			return locs;
			
			
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	@Override
	 protected void onPostExecute(List<ModelFetchLocation> save) {
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
			listener.onTaskResult(save);
	}

}
