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

public class PostHttpClient extends AsyncTask<String, String, String> {

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

	public PostHttpClient(Context context, OnTaskCompleted listener) {
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
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
			showSettingsAlert();
		} else {
			dialog.setMessage("connecting with server, please wait.");
			try {
				dialog.show();
			} catch (Exception e) {
				// WindowManager$BadTokenException will be caught and the app
				// would
				// not display
				// the 'Force Close' message
			}
		}

	}
	/*
	 * public String makeHttpRequest(Context context,String url, String method,
	 * String params) throws IOException, DownloadException, NotOnlineException{
	 * 
	 * 
	 * ConnectivityManager cm = (ConnectivityManager)
	 * context.getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo
	 * netInfo = cm.getActiveNetworkInfo(); if( netInfo == null ||
	 * !netInfo.isConnectedOrConnecting()){ throw new NotOnlineException(
	 * "Network Not Available"); }
	 * 
	 * 
	 * if (method.equals("POST")) { // request method is POST try { urlObj = new
	 * URL(url);
	 * 
	 * conn = (HttpURLConnection) urlObj.openConnection();
	 * 
	 * conn.setDoOutput(true);
	 * 
	 * conn.setRequestMethod("POST");
	 * conn.setRequestProperty("Content-Type","application/json");
	 * conn.setRequestProperty("Accept-Charset", charset);
	 * 
	 * conn.setReadTimeout(10000); conn.setConnectTimeout(15000);
	 * 
	 * conn.connect();
	 * 
	 * paramsString = params;
	 * 
	 * wr = new DataOutputStream(conn.getOutputStream());
	 * wr.writeBytes(paramsString); wr.flush(); wr.close();
	 * 
	 * } catch (IOException e) { e.printStackTrace(); } } else
	 * if(method.equals("GET")){ // request method is GET
	 * 
	 * if (sbParams.length() != 0) { url += "?" + sbParams.toString(); }
	 * 
	 * try { urlObj = new URL(url);
	 * 
	 * conn = (HttpURLConnection) urlObj.openConnection();
	 * 
	 * conn.setDoOutput(false);
	 * 
	 * conn.setRequestMethod("GET");
	 * conn.setRequestProperty("Content-Type","application/json");
	 * conn.setRequestProperty("Accept-Charset", charset);
	 * 
	 * conn.setConnectTimeout(15000);
	 * 
	 * conn.connect();
	 * 
	 * } catch (IOException e) { e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * int statusCode = conn.getResponseCode();
	 * 
	 * 200 represents HTTP OK if (statusCode == 200) { InputStream in = new
	 * BufferedInputStream(conn.getInputStream()); BufferedReader reader = new
	 * BufferedReader(new InputStreamReader(in)); result = new StringBuilder();
	 * String line; while ((line = reader.readLine()) != null) {
	 * result.append(line); } Log.d("return", result.toString());
	 * 
	 * } else { throw new DownloadException("Failed to fetch data!!"); }
	 * conn.disconnect();
	 * 
	 * return result.toString(); }
	 */

	@Override
	protected String doInBackground(String... arg0) {

		String url = arg0[0];
		String method = arg0[1];
		String params = arg0[2];
		result = new StringBuilder();


		Log.d("httpclient", arg0[0]);
		Log.d("httpclient param", arg0[2]);
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
				Log.d("httpclient result", result.toString());
				return result.toString();

				

			} catch (IOException e) {
				e.printStackTrace();
			}
		/*} else if (method.equals("GET")) {
			// request method is GET

			if (sbParams.length() != 0) {
				url += "?" + sbParams.toString();
			}

			try {
				urlObj = new URL(url);

				conn = (HttpURLConnection) urlObj.openConnection();

				conn.setDoOutput(false);

				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept-Charset", charset);

				conn.setConnectTimeout(15000);

				conn.connect();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}*/
		}
		
		return null;
		/* 200 represents HTTP OK */

		
	}

	// While Downloading Music File
	@Override
	protected void onProgressUpdate(String... progress) {
		// Set progress percentage
		// prgDialog.setProgress(Integer.parseInt(progress[0]));
	}

	// Once Music File is downloaded
	@Override
	protected void onPostExecute(String result) {
		try {
			if (dialog != null) {
				dialog.dismiss();

			}
		} catch (Exception e) {
			// WindowManager$BadTokenException will be caught and the app would
			// not display
			// the 'Force Close' message
		}
		listener.onTaskCompleted(result);

		// Dismiss the dialog after the Music file was downloaded
		// dismissDialog(progress_bar_type);
		// Toast.makeText(getApplicationContext(), "Download complete, playing
		// Music", Toast.LENGTH_LONG).show();
		// Play the music
		// playMusic();
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);

		// Setting Dialog Title
		alertDialog.setTitle("Connection settings");

		// Setting Dialog Message
		alertDialog.setMessage("No connection is available. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);

				PostHttpClient.this.context.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

}