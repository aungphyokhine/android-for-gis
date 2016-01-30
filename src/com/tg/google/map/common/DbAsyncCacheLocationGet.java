
package com.tg.google.map.common;

import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class DbAsyncCacheLocationGet extends AsyncTask<String, String, List<ModelCacheLocation>> {
	private DBHelper db;
	private GPSTracker mGPS;
	private Context context;
	private OnDBGetCacheLocation listener;
	private ProgressDialog dialog;

	public DbAsyncCacheLocationGet(Context context, OnDBGetCacheLocation listener) {
		super();
		this.context = context;
		db = new DBHelper(this.context);
		this.listener = listener;
		dialog = new ProgressDialog(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("getting data, please wait.");
		try {
			//dialog.show();
		} catch (Exception e) {
			// WindowManager$BadTokenException will be caught and the app would
			// not display
			// the 'Force Close' message
		}
	}

	@Override
	protected List<ModelCacheLocation> doInBackground(String... arg0) {
		// TODO Auto-generated method stub

		try {

			long from = Long.parseLong(arg0[0]);
			long to = Long.parseLong(arg0[1]);
			int count = Integer.parseInt(arg0[2]);
			List<ModelCacheLocation> locs = db.getCacheLocations(from, to, count);
			Log.d("DbAsyncCacheLocationGet", locs.size() + "," + "from : " + from + ", to : " + to);
			return locs;

		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void onPostExecute(List<ModelCacheLocation> save) {
		try {
			/*if (dialog != null) {
				dialog.dismiss();

			}*/
		} catch (Exception e) {
			// WindowManager$BadTokenException will be caught and the app would
			// not display
			// the 'Force Close' message
		}
		listener.onTaskResult(save);

	}

}
