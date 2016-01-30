package com.tg.google.map;

import java.io.IOException;
import java.security.spec.MGF1ParameterSpec;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.tg.google.map.DownloadResultReceiver.Receiver;
import com.tg.google.map.common.GISInfo;
import com.tg.google.map.common.GISInfoData;
import com.tg.google.map.common.GPSTracker;
import com.tg.google.map.common.ModelAppDataJson;
import com.tg.google.map.common.ModelFetchLocation;
import com.tg.google.map.common.OnGISInfo;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.DbAsyncCacheLocationInsert;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity
		implements DownloadResultReceiver.Receiver, FragmentCustom.OnFragmentStatusListener {

	private GoogleMap gm;
	public static long start_track_time;
	public static GPSTracker mgps;
	public static String gcmid = null;

	//////////////////////////////////
	private DownloadResultReceiver mReceiver;
	private Intent downloadIntent;
	private DBHelper db;
	////////////////////////////////////////////
	private PendingIntent checklocpendingIntent;
	private AlarmManager checklocmanager;

	private PendingIntent postlocpendingIntent;
	private AlarmManager postlocmanager;
	private LinearLayout toolbarmain, toolbarhome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
	
		super.onCreate(savedInstanceState);
		db = new DBHelper(MainActivity.this);
		
		setContentView(R.layout.main_page);
		toolbarmain = (LinearLayout) findViewById(R.id.toobarmain);
		toolbarhome = (LinearLayout) findViewById(R.id.toobarhome);
		
		
		Intent alarmpostIntent = new Intent(this, AlarmReceiverPostLocation.class);
		postlocpendingIntent = PendingIntent.getBroadcast(this, 0, alarmpostIntent, 0);

		//////////////////////////////// for record on db

		Intent alarmIntent = new Intent(this, AlarmReceiverCheckLocation.class);
		checklocpendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
		checklocmanager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// register gcm
		registerOnGCM();

		mReceiver = new DownloadResultReceiver(new Handler());
		mReceiver.setReceiver(MainActivity.this);
		downloadIntent = new Intent(Intent.ACTION_SYNC, null, MainActivity.this, LocationsPullService.class);
		// postlocmanager =
		// (AlarmManager)getSystemService(Context.ALARM_SERVICE);

		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}
			
			
			
			if(Authenticate()){
				setHomePage();
			}
			else{
				setLoginPage();
			}
			// Create an instance of ExampleFragment

		}

		ImageView gotofriend = (ImageView) findViewById(R.id.imageViewFriends);
		gotofriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setPasswordChangePage();
			}
		});
		/*ImageView gotosettings = (ImageView) findViewById(R.id.imageViewSettings);
		gotosettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setFriendListPage();
			}
		});*/
		ImageView gotologout = (ImageView) findViewById(R.id.imageViewExit);
		gotologout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(MainActivity.this)
		           .setMessage("Are you sure to logged out?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	ModelAppDataJson appdata = new ModelAppDataJson();
		   			    appdata.setRecord(false);
			    		appdata.setSync(false);
			    		appdata.setFetch(false);
			    		appdata.setRecorddelay(10);
			    		appdata.setSyncdelay(10);
			    		appdata.setFetchdelay(10);
					    db.saveAppData(appdata);
						setLoginPage();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();
			   
			}
		});

		// toolbarmain =
		// (android.support.v7.widget.CardView)findViewById(R.id.toolbarmain);
	
		
	}
	
	
	


	private String prevfragment = "",currentfragment = "";
	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
		
		prevfragment = currentfragment;
		currentfragment = fragment.getClass().getName();
		pageChangeSetup(currentfragment);
		
		
	}
	
	

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		if(Authenticate()){
			Log.d("Backpress", currentpage);
			if(currentpage.equals("com.tg.google.map.FragmentTrackOther") || currentpage.equals("com.tg.google.map.FragmentFriendRequest") || currentpage.equals("com.tg.google.map.FragmentFriendActions"))
			{
				setFriendListPage();
			}
			else if(currentpage.equals("com.tg.google.map.FragmentHome")){
				
				new AlertDialog.Builder(this)
		           .setMessage("Are you sure to exit the application?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   MainActivity.this.finish();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();
			}
			else{
				setHomePage();
			}
			
		}
		else{
			setLoginPage();
		}
		
	}
	
	private String currentpage = "";
	private void pageChangeSetup(String page){
		currentpage = page;
		Boolean isvalidpage =  IsAuthenticatePages(page);;
		if(isvalidpage){
			if(Authenticate()){
			toolbarhome.setVisibility(View.GONE);
			toolbarmain.setVisibility(View.VISIBLE);
			}
			else{
				setLoginPage();
			}
		}
		else{
			
			toolbarmain.setVisibility(View.GONE);
			toolbarhome.setVisibility(View.VISIBLE);
		}
		
	}
	
	private Boolean IsAuthenticatePages(String pagename){
	
		//Toast.makeText(getApplicationContext(),  pagename , 1000).show();
		if(pagename.equals("com.tg.google.map.FragmentHome") ||
				pagename.equals("com.tg.google.map.FragmentFriendList") ||
				pagename.equals("com.tg.google.map.FragmentFriendActions") ||
				pagename.equals("com.tg.google.map.FragmentTrackMe") ||
				pagename.equals("com.tg.google.map.FragmentTrackOther") ||
				pagename.equals("com.tg.google.map.FragmentFriendRequest")){
			return true;
		}
		
		return false;
	}


	private Boolean Authenticate(){
		ModelAppDataJson appjson = db.getAppData();
		
		if(appjson.getToken() == null){
			//toolbarmain.setVisibility(View.INVISIBLE);
			return false;
		}
		else{
			//toolbarmain.setVisibility(View.VISIBLE);
			return true;
		}
		
	}

	private void setLoginPage() {
		FragmentLogin firstFragment = new FragmentLogin();
		
		// In case this activity was started with special instructions from an
		// Intent,
		// pass the Intent's extras to the fragment as arguments
		firstFragment.setArguments(getIntent().getExtras());

		// Add the fragment to the 'fragment_container' FrameLayout
		//getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack so the user can navigate
		// back
		transaction.replace(R.id.fragment_container, firstFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	private void setHomePage() {
		// TODO Auto-generated method stub
		// toolbarmain.setVisibility(1);
		// toolbarhome.setVisibility(0);
		FragmentHome newHome = new FragmentHome();
		Bundle args = new Bundle();

		newHome.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack so the user can navigate
		// back
		transaction.replace(R.id.fragment_container, newHome);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	private void setFriendListPage() {
		// toolbarmain.setVisibility(1);
		// toolbarhome.setVisibility(0);
		// TODO Auto-generated method stub
		FragmentFriendList frilist = new FragmentFriendList();
		Bundle args = new Bundle();

		frilist.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack so the user can navigate
		// back
		transaction.replace(R.id.fragment_container, frilist);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
	
	
	private void setProfileUploadPage() {
		FragmentProfileImageUpload profileupload = new FragmentProfileImageUpload();
		Bundle args = new Bundle();

		profileupload.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack so the user can
		// navigate back
		transaction.replace(R.id.fragment_container, profileupload);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
	
	private void setPasswordChangePage() {
		FragmentPasswordChange passwordchange = new FragmentPasswordChange();
		Bundle args = new Bundle();

		passwordchange.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack so the user can
		// navigate back
		transaction.replace(R.id.fragment_container, passwordchange);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
	//////////////////////////////////////////////////////////////////////

	@Override
	public void onReceiveFetchLocationsResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
		case LocationsPullService.STATUS_RUNNING:
			setProgressBarIndeterminateVisibility(true);
			break;
		case LocationsPullService.STATUS_FINISHED:
			/* Hide progress & extract result from bundle */
			setProgressBarIndeterminateVisibility(false);

			/*
			 * String results = resultData.getString("result");
			 * 
			 * GISInfoData gisinfos = new Gson().fromJson(results,
			 * GISInfoData.class);
			 * ///////////////////////////////////////////////////////////////
			 * List<ModelFetchLocation> locations = new
			 * ArrayList<ModelFetchLocation>(); for(Iterator<GISInfo> i =
			 * gisinfos.items.iterator(); i.hasNext(); ) { GISInfo item =
			 * i.next(); LatLng loc = new
			 * LatLng(item.getLoc().get(1),item.getLoc().get(0));
			 * ModelFetchLocation fetchloc = new ModelFetchLocation();
			 * fetchloc.setDate(item.getDate()); Log.d("fd", item.getDate() +
			 * ""); fetchloc.setDatestring(item.getDatestring());
			 * fetchloc.setStart_date_time(item.getStart_date_time());
			 * fetchloc.setLoc(item.getLoc());
			 * fetchloc.setUid(gisinfos.getEmail()); locations.add(fetchloc);
			 * 
			 * }
			 * 
			 * new DbAsyncFetchLocationInsert(getApplicationContext(),
			 * locations, "").execute();
			 */

			/*
			 * double distance = 0; long end = start + fetchDuration; start =
			 * end - 1;
			 * 
			 * for(Iterator<GISInfo> i = gisinfos.items.iterator(); i.hasNext();
			 * ) { GISInfo item = i.next(); LatLng loc = new
			 * LatLng(item.getLoc().get(1),item.getLoc().get(0));
			 * polylines.add(loc); //Log.d("locdata", "date: " + item.date +
			 * ", log: " + item.loc.get(0) + ", lat: " + item.loc.get(1));
			 * if(last != new LatLng(1, 1)){ distance +=
			 * CalculationByDistance(loc,last); } last = loc; }
			 * 
			 * 
			 * Polyline line = gm.addPolyline(new PolylineOptions()
			 * .addAll(polylines) .width(10) .color(Color.RED));
			 * 
			 * 
			 * CameraUpdate center= CameraUpdateFactory.newLatLng(last);
			 * CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
			 * 
			 * gm.moveCamera(center); gm.animateCamera(zoom);
			 * 
			 * gm.addMarker(new MarkerOptions() .position(last) .title(
			 * "Distance : " + distance));
			 */

			/////////////////////////////////////////////////////////////////

			/* Update ListView with result */
			// arrayAdapter = new ArrayAdapter(MyActivity.this,
			// android.R.layout.simple_list_item_2, results);
			// listView.setAdapter(arrayAdapter);
			break;
		case LocationsPullService.STATUS_ERROR:
			/* Handle the error */
			String error = resultData.getString(Intent.EXTRA_TEXT);
			Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			break;
		}
	}

	///////////////////////////////////////////////////// intent
	private String email;
	Handler intentFetchTimerHandler = new Handler();
	Runnable intentFetchRunnable = new Runnable() {
		@Override
		public void run() {

			long end = Long.MAX_VALUE; // start + fetchDuration;

			String url = "http://express4-gistracker.rhcloud.com/gisinfo/getbyuser";

			/* Starting Download Service */

			String token = db.getAppData().getToken();
			String start = db.getMaxFetchDateByUser(FragmentHome.FETCH_EMAIL) + "";
			// List<ModelFetchLocation> locm =
			// db.getFetchLocations(Long.MIN_VALUE, Long.MAX_VALUE);

			if (token != null) {
				String email = "";
				if (FragmentHome.FETCH_EMAIL == null) {

					Toast.makeText(getBaseContext(), "invalid email", Toast.LENGTH_SHORT).show();
				} else {
					email = FragmentHome.FETCH_EMAIL;
					downloadIntent.putExtra("email", email);
					downloadIntent.putExtra("token", token);
					downloadIntent.putExtra("url", url);
					downloadIntent.putExtra("start", start);
					downloadIntent.putExtra("end", Long.MAX_VALUE + "");
					downloadIntent.putExtra("receiver", mReceiver);
					downloadIntent.putExtra("requestId", 101);

					startService(downloadIntent);
				}

				intentFetchTimerHandler.postDelayed(this, fetchDelay);
			}

		}
	};

	//////////////////////////////////////////////////////////////

	public double CalculationByDistance(LatLng StartP, LatLng EndP) {
		int Radius = 6371;// radius of earth in Km
		double lat1 = StartP.latitude;
		double lat2 = EndP.latitude;
		double lon1 = StartP.longitude;
		double lon2 = EndP.longitude;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		double valueResult = Radius * c;
		double km = valueResult / 1;
		DecimalFormat newFormat = new DecimalFormat("####");
		int kmInDec = Integer.valueOf(newFormat.format(km));
		double meter = valueResult % 1000;
		int meterInDec = Integer.valueOf(newFormat.format(meter));
		Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

		return Radius * c;
	}

	// runs without a timer by reposting this handler at the end of the runnable
	long startTime = 0;
	long start = 1451526492;
	LatLng last = new LatLng(1, 1);
	int fetchDuration = 50;
	long fetchDelay = 1000 * 10;

	ArrayList<LatLng> polylines = new ArrayList<LatLng>();

	@Override
	public void onChangeStatus(String status) {

		if (status.equals(FragmentCustom.LOGIN_SUCCESS)) {

			setHomePage();
		} else if (status.equals(FragmentCustom.VERIFY_START)) {

			// Create fragment and give it an argument for the selected article
			FragmentVerification newFragment = new FragmentVerification();
			Bundle args = new Bundle();

			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		} else if (status.equals(FragmentCustom.SIGN_START)) {
			FragmentRegister newFragment = new FragmentRegister();
			Bundle args = new Bundle();

			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		} else if (status.equals(FragmentCustom.VERIFY_SUCCESS)) {
			FragmentLogin newLogin = new FragmentLogin();
			Bundle args = new Bundle();

			newLogin.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newLogin);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		} else if (status.equals(FragmentCustom.NEED_LOGIN)) {
			FragmentLogin newLogin = new FragmentLogin();
			Bundle args = new Bundle();

			newLogin.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newLogin);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		} else if (status.equals(FragmentCustom.FRIEND_REQUEST)) {
			FragmentFriendRequest newRequest = new FragmentFriendRequest();
			Bundle args = new Bundle();

			newRequest.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newRequest);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		} else if (status.equals(FragmentCustom.HOME_GO_TRACK_OTHER)) {
			FragmentTrackOther trakother = new FragmentTrackOther();
			Bundle args = new Bundle();

			trakother.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, trakother);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		} else if (status.equals(FragmentCustom.HOME_GO_TRACK_ME)) {
			FragmentTrackMe trakme = new FragmentTrackMe();
			Bundle args = new Bundle();

			trakme.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, trakme);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		} else if (status.equals(FragmentCustom.FRIEND_LIST)) {
			setFriendListPage();
		}
		else if (status.equals(FragmentCustom.PROFILE_UPLOAD)) {
			setProfileUploadPage();
		}
		else if (status.equals(FragmentCustom.PASSWORD_RESET)) {
			FragmentResetPassword reset = new FragmentResetPassword();
			Bundle args = new Bundle();

			reset.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, reset);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

	@Override
	public void onChangeEvent(String evt) {
		// TODO Auto-generated method stub
		Log.d("loc record", evt);
		/// set start track time..
		

		if (evt.equals(FragmentCustom.HOME_LOC_RECORD_OFF)) {
			db.saveStartTripDate(0);
			if (checklocmanager != null) {

				if (mgps != null)
					mgps.stopUsingGPS();

				Log.d("loc record end", evt);
				if (checklocmanager != null)
					checklocmanager.cancel(checklocpendingIntent);
			}
		} else if (evt.equals(FragmentCustom.HOME_LOC_RECORD_ON)) {
			if (checklocmanager != null) {
				mgps = new GPSTracker(MainActivity.this);
				Log.d("loc record start", evt);
				start_track_time = System.currentTimeMillis() / 1000L;
				db.saveStartTripDate(start_track_time);
				Log.d("start date save : ", start_track_time + "");
				// int recordelay = db.getAppData().getRecorddelay();

				if (!mgps.canGetLocation) {
					MainActivity.mgps.showSettingsAlert();
				}

				int interval = db.getAppData().getRecorddelay() * 1000;
				checklocmanager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval,
						checklocpendingIntent);

			}

		}

		if (evt.equals(FragmentCustom.HOME_LOC_SYNC_OFF)) {
			if (checklocmanager != null) {
				Log.d("loc sync end", evt);
				if (checklocmanager != null)
					checklocmanager.cancel(postlocpendingIntent);
			}
		} else if (evt.equals(FragmentCustom.HOME_LOC_SYNC_ON)) {
			if (checklocmanager != null) {
				Log.d("loc sync start", evt);
				int interval = db.getAppData().getSyncdelay() * 1000;

				checklocmanager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval,
						postlocpendingIntent);
			}

		}

		if (evt.equals(FragmentCustom.HOME_LOC_FETCH_OFF)) {

			Log.d("loc fetch end", evt);
			intentFetchTimerHandler.removeCallbacks(intentFetchRunnable);

		} else if (evt.equals(FragmentCustom.HOME_LOC_FETCH_ON)) {

			Log.d("loc fetch start", evt);
			intentFetchTimerHandler.postDelayed(intentFetchRunnable, 0);

		}

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	GoogleCloudMessaging gcm;
	String regid;
	String PROJECT_NUMBER = "381062206141";

	public void registerOnGCM() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				ModelAppDataJson appjson = db.getAppData();
				if (appjson.getGcmid() == null) {
					String msg = "";
					try {
						if (gcm == null) {
							gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
						}
						regid = gcm.register(PROJECT_NUMBER);
						msg = "Device registered, registration ID=" + regid;
						Log.i("GCM", msg);
						
						appjson.setGcmid(regid);
						db.saveAppData(appjson);

						return appjson.getGcmid();

					} catch (IOException ex) {
						msg = "Error :" + ex.getMessage();
						return msg;
					}

				} else {
					return appjson.getGcmid();
				}

			}

			@Override
			protected void onPostExecute(String msg) {
				MainActivity.gcmid = msg;
			}
		}.execute(null, null, null);
	}

	@Override
	public void onRequestData(String type, String... values) {
		// TODO Auto-generated method stub
		if (type.equals(FragmentCustom.FRIEND_ACTION)) {

			FragmentFriendActions friact = new FragmentFriendActions();
			Bundle args = new Bundle();
			args.putString("email", values[0]);
			args.putString("group", values[1]);
			Log.d("FragmentFriendActions", values[0] + "," + values[1]);
			friact.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, friact);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();

		} else if (type.equals(FragmentCustom.HOME_GO_TRACK_OTHER)) {
			FragmentTrackOther trakother = new FragmentTrackOther();
			Bundle args = new Bundle();
			args.putString("email", values[0]);

			trakother.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, trakother);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();

		}

	}
}
