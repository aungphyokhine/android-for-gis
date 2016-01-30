
package com.tg.google.map;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.DbAsyncFetchLocationGet;
import com.tg.google.map.common.DbAsyncFetchLocationInsert;
import com.tg.google.map.common.ModelCacheLocation;
import com.tg.google.map.common.ModelFetchLocation;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.ModelTripInfo;
import com.tg.google.map.common.OnDBFetchLocations;
import com.tg.google.map.common.OnResults;
import com.tg.google.map.common.PostAsyncFetchLocation;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentTrackOther extends FragmentCustom {
	// EditText txt_verify;

	private GoogleMap gm;
	Long fetchDelay = (long) (30 * 1000);
	Long start = Long.MIN_VALUE;
	Long end = Long.MAX_VALUE;
	int backhours = 1;
	TextView textTrackBackInfo,b;
	TextView textTotalDistance;
	String fetchemail;
	float totaldistance = 0;
	private Boolean fetchstart = false;
	private DBHelper db;

	private List<ModelTripInfo> tripinfos;
	private LinkedHashMap<Long,List<ModelCacheLocation>> locationslist = new LinkedHashMap<Long,List<ModelCacheLocation>>();
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		db = new DBHelper(getActivity());
		db.saveLastFetchDate(0 + "");
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			fragmentCallback = (OnFragmentStatusListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnLoginStatusListener");
		}
	}

	SupportMapFragment fragment;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fetchemail = getArguments().getString("email");

		android.support.v4.app.FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map_container, fragment).commit();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (gm == null) {
			gm = fragment.getMap();
			gm.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	 @Override
		public void onDestroyView() {
			// TODO Auto-generated method stub
			super.onDestroyView();
			fetchFriendDataTimerHandler.removeCallbacks(fetchFriendDataRunnable);
		}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final ImageView startstop = (ImageView) view.findViewById(R.id.imageViewStartStop);
	     b = (TextView) view.findViewById(R.id.textViewStartStop);
	     startstop.setOnClickListener(new View.OnClickListener() {
	    	
				@Override
				public void onClick(View arg0) {
					
					if (b.getText().equals("pause")) {
	                	//fetchLocationsTimerHandler.removeCallbacks(fetchLocationsRunnable);
						fetchFriendDataTimerHandler.removeCallbacks(fetchFriendDataRunnable);
	                    b.setText("start");
	                    startstop.setImageResource(R.drawable.ic_play_for_work_black_36dp);
	                } else {
	                   // startTime = System.currentTimeMillis();
	                    //fetchLocationsTimerHandler.postDelayed(fetchLocationsRunnable, 0);
	                	locationslist = new LinkedHashMap<Long,List<ModelCacheLocation>>();
	                	totaldistance = 0;
	                	Date backtime = new Date(System.currentTimeMillis() - (backhours * 60 * 60 * 1000 ));
	                	Long current_time = System.currentTimeMillis() / 1000L;
	                	start = backtime.getTime() / 1000L;
	                	end = current_time;
	                	
	                	Log.d("start", start + "");
	                	Log.d("end", end + "");
	                	
	                	fetchFriendDataTimerHandler.postDelayed(fetchFriendDataRunnable, 0);
	                    b.setText("pause");
	                    startstop.setImageResource(R.drawable.ic_settings_backup_restore_black_36dp);
	                    gm.clear();
	                }
					
					
				}
		    	 
		     });
	     //or
	     //getActivity().findViewById(R.id.yourId).setOnClickListener(this);
	     textTrackBackInfo = (TextView) view.findViewById(R.id.textTrackBack);
	     textTotalDistance = (TextView) view.findViewById(R.id.textTotalDistance);
	     SeekBar seektrack = (SeekBar) view.findViewById(R.id.seekBarTrackTime);
	     seektrack.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					Log.d("seekrecord", seekBar.getProgress() + "");
					
					backhours = seekBar.getProgress();
					
					
					textTrackBackInfo.setText("Back data from : " + backhours + "hrs");
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					// TODO Auto-generated method stub

				}
			});
		
		
		
		
		
		
		
		ImageView clear = (ImageView) view.findViewById(R.id.imageViewCleanHistory);

		clear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				new AlertDialog.Builder(getActivity())
		           .setMessage("Are you sure to clean the cache? it will need more bandwidth usage.")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   db.clearFetchLocation(fetchemail);
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();
				//

			}

		});
		// or
		// getActivity().findViewById(R.id.yourId).setOnClickListener(this);
		textTrackBackInfo = (TextView) view.findViewById(R.id.textTrackBack);
		textTotalDistance = (TextView) view.findViewById(R.id.textTotalDistance);
		
		final AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
		//builderSingle.setIcon(R.drawable.ic_flight_takeoff_black_36dp);
	     builderSingle.setTitle("Select One Route : ");
	     builderSingle.setPositiveButton(
	             "cancel",
	             new DialogInterface.OnClickListener() {
	                 @Override
	                 public void onClick(DialogInterface dialog, int which) {
	                     dialog.dismiss();
	                 }
	             });
		final ImageView imgtriplist = (ImageView) view.findViewById(R.id.imageViewFindTrips);
	     
	     imgtriplist.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
				
					builderSingle.setAdapter(
				    		 getTripAdapter(),
				             new DialogInterface.OnClickListener() {
				                 @Override
				                 public void onClick(DialogInterface dialog, int which) {
			
				                     
				                     /////////////////////////////////////////
				                
				                	 fetchFriendDataTimerHandler.removeCallbacks(fetchFriendDataRunnable);
					                b.setText("start");
					                startstop.setImageResource(R.drawable.ic_play_for_work_black_36dp);
				                    
					                
					               gm.clear();
					                ModelTripInfo info = tripinfos.get(which);
					                if(info.getCircle() != null && info.getMarker() != null && info.getPolylineOptions() != null && info.getUpdate() != null){
					                	gm.addCircle(info.getCircle());
					                	gm.addPolyline(info.getPolylineOptions());
					                	gm.addMarker(info.getMarker());
					                	gm.moveCamera(info.getUpdate());
					                	
					                }
					                

				                 }
				             });
					builderSingle.show();
					
				}
		    	 
		     });

	}
	
	
	
	private void createMap(List<ModelFetchLocation> newlocations){
		tripinfos = new ArrayList<ModelTripInfo>();
		for(Iterator<ModelFetchLocation> i = newlocations.iterator(); i.hasNext(); ) {
   			ModelCacheLocation item = i.next();
   			List<ModelCacheLocation> sametrip = locationslist.get(item.getStart_date_time());
   			if(sametrip == null){
   				List<ModelCacheLocation> newtrip = new ArrayList<ModelCacheLocation>();
   				newtrip.add(item);
   				locationslist.put(item.getStart_date_time(), newtrip);
   				
   			}
   			else{
   				locationslist.get(item.getStart_date_time()).add(item);   
   			}
   						
		}
		
		CameraUpdate center = null;
		for(Iterator<List<ModelCacheLocation>> i = locationslist.values().iterator(); i.hasNext(); ){
			List<ModelCacheLocation> trip = i.next();
			
			
			Location previouspoint = null;
			float distance = 0;
			
			ModelTripInfo tripinfo = new ModelTripInfo();
			ArrayList<LatLng> polypoints = new ArrayList<LatLng>();
			for(Iterator<ModelCacheLocation> j = trip.iterator(); j.hasNext(); ) {
				
	   			ModelCacheLocation item = j.next();
	   			
	   			LatLng latlng = new LatLng(item.getLoc().get(1),item.getLoc().get(0));
	   			polypoints.add(latlng);
	   			
	   			Location location = new Location("cur");
	   			location.setLatitude(latlng.latitude);
	   			location.setLongitude(latlng.longitude);
	   		///first step
	   			if(previouspoint == null){	
	   				previouspoint = location;
	   				CircleOptions circleOptions = new CircleOptions()
	        				  .center(latlng)   //set center
	        				  .radius(50)   //set radius in meters
	        				  .fillColor(Color.RED)
	        				  .strokeColor(Color.BLACK)
	        				  .strokeWidth(5);
	        				
	        		gm.addCircle(circleOptions);
	        		tripinfo.setCircle(circleOptions);
	   			}
	   			else{
	   				distance += previouspoint.distanceTo(location);
	   				previouspoint = location;
	   			}
	   			
	   			//last step
   				
	   			
	   			if(!j.hasNext()){
	   				Long duration =  (item.getDate() - item.getStart_date_time()) * 1000;
	        		
	        		int seconds = (int) (duration / 1000) % 60 ;
	        		int minutes = (int) ((duration / (1000*60)) % 60);
	        		int hours   = (int) ((duration / (1000*60*60)) % 24);
	        		
	        		String message = " Dis : " + distance + " meters, Duration : " + hours + ":" + minutes + ":" + seconds + "";
	        		MarkerOptions marker = new MarkerOptions()
		   	                .position(latlng)
		   	             .snippet(message)
		   	             .title("Time : " + new Date(item.getDate() * 1000));
	        		gm.addMarker(marker)
	        		.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));	
	        		tripinfo.setMarker(marker);
	        		center= CameraUpdateFactory.newLatLngZoom(latlng, 15);
	   	        	
	   	        	
	   	        	Random rnd = new Random(); 
					int color = Color.argb(255, 50+ rnd.nextInt(100),50+ rnd.nextInt(100), 50+rnd.nextInt(100));
					PolylineOptions poly = new PolylineOptions()
		   	   	    	     .addAll(polypoints)
		   	   	    	     .width(10)
		   	   	    	     .color(color);
					Polyline line = gm.addPolyline(poly);
					tripinfo.setMessage(message);
					tripinfo.setUpdate(center);
					tripinfo.setPolylineOptions(poly);
					tripinfo.setDate(item.getDate() * 1000L);
					tripinfos.add(tripinfo);
	   			}
	   			
			}
			
		}
		if(center != null && locationslist.size() > 0){
			gm.moveCamera(center);
		}
		
	}

	private AdapterAlertList getTripAdapter(){
		
		AdapterAlertList alertlistadapter = new AdapterAlertList(getActivity(), R.layout.alert_list_row, tripinfos);
//		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//	    		 getActivity(),
//	             android.R.layout.select_dialog_singlechoice);
//		try{
//		
//			
//			for(Iterator<ModelTripInfo> i = tripinfos.iterator(); i.hasNext(); ) {
//				ModelTripInfo tripinfo = i.next();
//				
//				arrayAdapter.add(tripinfo.getMessage());
//				//adapterlongvalue.add(tripdate);
//			}
//		
//		
//		}catch(Exception ex){
//			
//		}
	     
	     return alertlistadapter;
	} 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			// mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		View pageview = inflater.inflate(R.layout.track_other, container, false);

		/*
		 * int status =
		 * GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
		 * 
		 * if (status == ConnectionResult.SUCCESS) { SupportMapFragment
		 * supportMapFragment = (SupportMapFragment)
		 * getChildFragmentManager().findFragmentById(R.id.map); gm =
		 * supportMapFragment.getMap(); } else { int requestCode = 10; Dialog
		 * dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
		 * requestCode); dialog.show(); }
		 */
		// Inflate the layout for this fragment
		return pageview;
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been
		// applied to the fragment at this point so we can safely call the
		// method
		// below that sets the article text.
		Bundle args = getArguments();
		/*
		 * if (args != null) { // Set article based on argument passed in
		 * updateArticleView(args.getInt(ARG_POSITION)); } else if
		 * (mCurrentPosition != -1) { // Set article based on saved instance
		 * state defined during onCreateView
		 * updateArticleView(mCurrentPosition); }
		 */
	}

	long track_start_date_time = Long.MIN_VALUE;

	



	private PostAsyncFetchLocation fetchloc;
	Handler fetchFriendDataTimerHandler = new Handler();
	Runnable fetchFriendDataRunnable = new Runnable() {
		@Override
		public void run() {
			if (getActivity() != null) {
				fetchloc = new PostAsyncFetchLocation(getActivity(), new OnDBFetchLocations() {

					@Override
					public void onTaskResult(List<ModelFetchLocation> result) {
						if(result == null){
							result = new ArrayList<ModelFetchLocation>();
						}
						createMap(result);
						
						// TODO Auto-generated method stub
//						if (result != null) {
//							Date track_date = null;
//
//							Long last_start_time = null;
//
//							// TODO Auto-generated method stub
//							List<List<ModelFetchLocation>> sub_tracks = new ArrayList<List<ModelFetchLocation>>();
//
//							List<ModelFetchLocation> sub = new ArrayList<ModelFetchLocation>();
//							for (Iterator<ModelFetchLocation> i = result.iterator(); i.hasNext();) {
//								ModelFetchLocation item = i.next();
//
//								if (last_start_time == null) {
//									last_start_time = item.getStart_date_time();
//								}
//
//								if (last_start_time != item.getStart_date_time()) {
//									last_start_time = item.getStart_date_time();
//									sub_tracks.add(sub);
//									sub = new ArrayList<ModelFetchLocation>();
//									sub.add(item);
//								} else {
//									sub.add(item);
//								}
//
//							}
//							sub_tracks.add(sub);
//							Log.d("Subtracks", sub_tracks.size() + "");
//							Location current_loc = null;
//							Location previous_loc = null;
//							for (Iterator<List<ModelFetchLocation>> i = sub_tracks.iterator(); i.hasNext();) {
//								List<ModelFetchLocation> items = i.next();
//
//								float subdistane = 0;
//								Location prevloc = null;
//								int idex = 0;
//								
//								ArrayList<LatLng> subpolylines = new ArrayList<LatLng>();
//								for (Iterator<ModelFetchLocation> j = items.iterator(); j.hasNext();) {
//									ModelFetchLocation item = j.next();
//
//									/// calculating distance
//									LatLng loc = new LatLng(item.getLoc().get(1), item.getLoc().get(0));
//									current_loc = new Location("cur");
//									current_loc.setLatitude(loc.latitude);
//									current_loc.setLongitude(loc.longitude);
//
//									track_date = new Date(item.getDate() * 1000);
//									if (prevloc != null) {
//										subdistane += prevloc.distanceTo(current_loc);
//									}
//									prevloc = current_loc;
//									subpolylines.add(loc);
//
//									if (idex == 0) {
//										CircleOptions circleOptions = new CircleOptions().center(loc) // set
//																										// center
//												.radius(50) // set radius in
//															// meters
//												.fillColor(Color.RED).strokeColor(Color.BLACK).strokeWidth(5);
//
//										gm.addCircle(circleOptions);
//										/*
//										 * gm.addMarker(new MarkerOptions()
//										 * .position(loc) .snippet("Start here")
//										 * .title("Time : " +
//										 * track_date.toString()));
//										 */
//									}
//									idex = idex + 1;
//									if (!j.hasNext()) {
//										Long duration = (item.getDate() - item.getStart_date_time()) * 1000;
//
//										int seconds = (int) (duration / 1000) % 60;
//										int minutes = (int) ((duration / (1000 * 60)) % 60);
//										int hours = (int) ((duration / (1000 * 60 * 60)) % 24);
//
//										String durationstr = hours + ":" + minutes + ":" + seconds + "";
//
//										gm.addMarker(new MarkerOptions().position(loc)
//												.snippet(" Dis : " + subdistane + ", Duration : " + durationstr)
//												.title("Time : " + track_date.toString()))
//												.setIcon(BitmapDescriptorFactory
//														.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//
//										CameraUpdate center = CameraUpdateFactory.newLatLngZoom(loc, 15);
//										gm.moveCamera(center);
//
//									}
//								}
//								Random rnd = new Random();
//								int color = Color.argb(255, 50 + rnd.nextInt(100), 50 + rnd.nextInt(100),
//										50 + rnd.nextInt(100));
//								Polyline line = gm
//										.addPolyline(new PolylineOptions().addAll(subpolylines).width(10).color(color));
//								totaldistance += subdistane;
//							}
//
//							textTotalDistance.setText("Total Distance : " + totaldistance + " meters.");
//						}
//						//// saving data...
//						DbAsyncFetchLocationInsert dbinsert = new DbAsyncFetchLocationInsert(getContext(), result, "",
//								new OnResults() {
//
//							@Override
//							public void onTaskResult(ModelResult result) {
//
//								// listener.onTaskResult(result);
//							}
//						});
						
						
						//dbinsert.execute();
						
						start = end;
						Date currenttime = new Date();
						end = currenttime.getTime() / 1000L;
					}
				});
				/*Date backtime = new Date(System.currentTimeMillis() - (backhours * 60 * 60 * 1000));
				start = backtime.getTime() / 1000L;
				long lastfetchdate = Long.parseLong(db.getLastFetchDate());
				Log.d("initail date compart", lastfetchdate + " > " + start);
				if(lastfetchdate > start){
					start = lastfetchdate;
				}
				
				
				Date currenttime = new Date();
				
				long unixTime = currenttime.getTime() / 1000L;		        	
	            String end = unixTime + "";*/
				fetchloc.execute(fetchemail, start + "",end + "");

				

				fetchFriendDataTimerHandler.postDelayed(this, fetchDelay);
			}

		}
	};

}
