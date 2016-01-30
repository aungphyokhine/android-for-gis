
package com.tg.google.map.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundHttpClient extends AsyncTask<String, String, String> {

	String charset = "UTF-8";
	HttpURLConnection conn;
	DataOutputStream wr;
	StringBuilder result;
	URL urlObj;
	JSONObject jObj = null;
	StringBuilder sbParams;
	String paramsString;
	private Context context;
	private ProgressDialog dialog;

	private OnTaskCompleted listener;

	public BackgroundHttpClient(Context context, OnTaskCompleted listener) {
		super();
		this.context = context;
		Log.d("HttpClient", "start");
		this.listener = listener;
		dialog = new ProgressDialog(context);
		// this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onPreExecute() {
		

	}
	
	@Override
	protected String doInBackground(String... arg0) {

		String url = arg0[0];
		String method = arg0[1];
		String params = arg0[2];
		result = new StringBuilder();


		Log.d("httpclient", arg0[0]);
		if (method.equals("POST")) {
			// request method is POST
			try {
				urlObj = new URL(url);

				conn = (HttpURLConnection) urlObj.openConnection();

				conn.setDoOutput(true);

				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept-Charset", charset);

				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);

				conn.connect();

				paramsString = params;

				wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes(paramsString);
				wr.flush();
				wr.close();
				
				int statusCode;
				statusCode = conn.getResponseCode();
				if (statusCode == 200) {
					InputStream in = new BufferedInputStream(conn.getInputStream());
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
									String line;
					while ((line = reader.readLine()) != null) {
						result.append(line);
					}
					Log.d("return", result.toString());

				} else {
					Log.d("return", result.toString());
				}
				
				conn.disconnect();
				return result.toString();

				

			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
		
		return null;
		/* 200 represents HTTP OK */

		
	}

	// While Downloading Music File
	protected void onProgressUpdate(String... progress) {
		// Set progress percentage
		// prgDialog.setProgress(Integer.parseInt(progress[0]));
	}

	// Once Music File is downloaded
	@Override
	protected void onPostExecute(String result) {
		listener.onTaskCompleted(result);

		// Dismiss the dialog after the Music file was downloaded
		// dismissDialog(progress_bar_type);
		// Toast.makeText(getApplicationContext(), "Download complete, playing
		// Music", Toast.LENGTH_LONG).show();
		// Play the music
		// playMusic();
	}

	

}