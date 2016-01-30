
package com.tg.google.map;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.DbAsyncCacheLocationGet;
import com.tg.google.map.common.GISInfo;
import com.tg.google.map.common.ModelAppDataJson;
import com.tg.google.map.common.ModelCacheLocation;
import com.tg.google.map.common.ModelFetchLocation;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.ModelTripInfo;
import com.tg.google.map.common.OnDBGetCacheLocation;
import com.tg.google.map.common.PostAsyncVerify;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
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

public class FragmentTrackMe extends FragmentCustom{
	//EditText txt_verify;
	
	private GoogleMap gm;
	Long fetchDelay = (long) (10 * 1000);
	Long start = Long.MIN_VALUE;
	Long end = Long.MAX_VALUE;
	int backhours = 1;
	TextView textTrackBackInfo,b;
	TextView textTotalDistance;
	float totaldistance = 0;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
      
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
           fragmentCallback = (OnFragmentStatusListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginStatusListener");
        }
    }
 
    SupportMapFragment fragment;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
	 @Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
				
		}
	
	@Override
	 public void onViewCreated(final View view, Bundle savedInstanceState) {
	     super.onViewCreated(view, savedInstanceState);
	     final ImageView startstop = (ImageView) view.findViewById(R.id.imageViewStartStop);
	     b = (TextView) view.findViewById(R.id.textViewStartStop);
	     startstop.setOnClickListener(new View.OnClickListener() {
	    	
				@Override
				public void onClick(View arg0) {
					
					if (b.getText().equals("pause")) {
	                	//fetchLocationsTimerHandler.removeCallbacks(fetchLocationsRunnable);
	                	intentFetchTimerHandler.removeCallbacks(intentFetchRunnable);
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
	                	
	                    intentFetchTimerHandler.postDelayed(intentFetchRunnable, 0);
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
	     
	     
	     ///for trips dialogue
	     final AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
	     
	     /*builderSingle.setAdapter(alertlistadapter, new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	             //TODO - Code when list item is clicked (int which - is param that gives you the index of clicked item)
	            }
	        })
	        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {

	            }
	        })
	        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            }
	        })
	        .setTitle("Dialog Title")
	        .setCancelable(false)
	        .show();*/
	     
	     
	     
	     //builderSingle.setIcon(R.drawable.ic_flight_takeoff_black_36dp);
	     builderSingle.setTitle("Select One Route : ");
//
//	     final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//	    		 getActivity(),
//	             android.R.layout.select_dialog_singlechoice);
//	     arrayAdapter.add("Hardik");
//	     arrayAdapter.add("Archit");
//	     arrayAdapter.add("Jignesh");
//	     arrayAdapter.add("Umang");
//	     arrayAdapter.add("Gatti");

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
					//final ArrayAdapter<String> arrayAdapter = getTripAdapter();
					builderSingle.setAdapter(
							getTripAdapter(),
				             new DialogInterface.OnClickListener() {
				                 @Override
				                 public void onClick(DialogInterface dialog, int which) {
				                     /*String strName = arrayAdapter.getItem(which);
				                     AlertDialog.Builder builderInner = new AlertDialog.Builder(
				                    		 getActivity());
				                     builderInner.setMessage(strName);
				                     builderInner.setTitle("Your Selected Item is");
				                     builderInner.setPositiveButton(
				                             "Ok",
				                             new DialogInterface.OnClickListener() {
				                                 @Override
				                                 public void onClick(
				                                         DialogInterface dialog,
				                                         int which) {
				                                     dialog.dismiss();
				                                 }
				                             });
				                     builderInner.show();*/
				                     
				                     /////////////////////////////////////////
				                
				                    intentFetchTimerHandler.removeCallbacks(intentFetchRunnable);
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
					                
				                    
				                     
				                //     List<ModelCacheLocation> selecttrip = (List<ModelCacheLocation>) (new ArrayList<ModelCacheLocation>(locationslist.values())).get(which);
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     ///////////////////////////////////////////////////////
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                     
				                 }
				             });
					builderSingle.show();
					
				}
		    	 
		     });
	     

	 }//
	
	//private List<List<ModelCacheLocation>> locationinfos = new ArrayList<List<ModelCacheLocation>>();
	//private List<Long> adapterlongvalue = new ArrayList<Long>();
	private AdapterAlertList getTripAdapter(){
		
		AdapterAlertList alertlistadapter = new AdapterAlertList(getActivity(), R.layout.alert_list_row, tripinfos);
			     
	/*
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
	    		 getActivity(),
	             android.R.layout.select_dialog_singlechoice);
		try{
		
			
			for(Iterator<ModelTripInfo> i = tripinfos.iterator(); i.hasNext(); ) {
				ModelTripInfo tripinfo = i.next();
				
				arrayAdapter.add(tripinfo.getMessage());
				//adapterlongvalue.add(tripdate);
			}
		
		
		}catch(Exception ex){
			
		}*/
	     
	     return alertlistadapter;
	} 
	 @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, 
       Bundle savedInstanceState) {

       // If activity recreated (such as from screen rotate), restore
       // the previous article selection set by onSaveInstanceState().
       // This is primarily necessary when in the two-pane layout.
       if (savedInstanceState != null) {
           //mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
       }
       
       View pageview = inflater.inflate(R.layout.track_me, container, false);
       
       /*int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());

	    if (status == ConnectionResult.SUCCESS) {
	      SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
	      gm = supportMapFragment.getMap();
	    }
	    else {
	      int requestCode = 10;
	      Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	      dialog.show();
	    }*/
       // Inflate the layout for this fragment
       return pageview;
   }
	 

   @Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		intentFetchTimerHandler.removeCallbacks(intentFetchRunnable);
	}

@Override
   public void onStart() {
       super.onStart();

       // During startup, check if there are arguments passed to the fragment.
       // onStart is a good place to do this because the layout has already been
       // applied to the fragment at this point so we can safely call the method
       // below that sets the article text.
       Bundle args = getArguments();
       /*if (args != null) {
           // Set article based on argument passed in
           updateArticleView(args.getInt(ARG_POSITION));
       } else if (mCurrentPosition != -1) {
           // Set article based on saved instance state defined during onCreateView
           updateArticleView(mCurrentPosition);
       }*/
   }
  
   
   long track_start_date_time = Long.MIN_VALUE;
	
	Location current_loc = null;
	Location previous_loc = null;
	
	//private LinkedHashMap<Long,List<ModelCacheLocation>> hashmaptrips = new LinkedHashMap<Long,List<ModelCacheLocation>>();
	
	private List<ModelTripInfo> tripinfos;
	private LinkedHashMap<Long,List<ModelCacheLocation>> locationslist = new LinkedHashMap<Long,List<ModelCacheLocation>>();
	
	private void createMap(List<ModelCacheLocation> newlocations){
		tripinfos = new ArrayList<ModelTripInfo>();
		for(Iterator<ModelCacheLocation> i = newlocations.iterator(); i.hasNext(); ) {
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
		CameraUpdate center= null;
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
					tripinfo.setDate(item.getStart_date_time() * 1000L);
					tripinfos.add(tripinfo);
	   			}
	   			
			}
			
		}
		if(center != null && locationslist.size() > 0){
			gm.moveCamera(center);
		}
		
	}
	
   Handler intentFetchTimerHandler = new Handler();
   Runnable intentFetchRunnable = new Runnable() {
   	  @Override
	       public void run() {
   		  
   		
   		  
   		 
   		ModelCacheLocation lastitem = null;  
   	
   		List<ModelCacheLocation> locs;
   		
   		
		try {
			
			if(getActivity() != null && gm !=null){
				DbAsyncCacheLocationGet getcache = new DbAsyncCacheLocationGet(getActivity(), new OnDBGetCacheLocation() {
					
					@Override
					public void onTaskResult(List<ModelCacheLocation> locs) {
						createMap(locs);
						
//						Long last_start_time = null;
//						Date track_date = null;
//						// TODO Auto-generated method stub
//						List<List<ModelCacheLocation>> sub_tracks = new ArrayList<List<ModelCacheLocation>>();
//						
//						List<ModelCacheLocation> sub = new ArrayList<ModelCacheLocation>();
//						for(Iterator<ModelCacheLocation> i = locs.iterator(); i.hasNext(); ) {
//				   			ModelCacheLocation item = i.next();
//				   			
//				   			if(last_start_time == null){
//				        		last_start_time = item.getStart_date_time();
//				        	}
//				        	
//				   			
//				   			if(last_start_time != item.getStart_date_time()){
//				   				last_start_time = item.getStart_date_time();
//				   				sub_tracks.add(sub);
//				   				hashmaptrips.put(last_start_time,sub);
//				   				sub = new ArrayList<ModelCacheLocation>();
//				   				sub.add(item);
//				   			}
//				   			else{
//				   				sub.add(item);
//				   			}
//				   			
//				   			
//						}
//						sub_tracks.add(sub);
//						hashmaptrips.put(last_start_time,sub);
//						Log.d("HashMap Put",last_start_time + ", " + hashmaptrips.size());
//						for(Iterator<List<ModelCacheLocation>> i = sub_tracks.iterator(); i.hasNext(); ) {
//							List<ModelCacheLocation> items = i.next();
//							
//							float subdistane = 0;
//							Location prevloc = null;
//							int idex = 0;
//							
//							ArrayList<LatLng> subpolylines = new ArrayList<LatLng>();
//							for(Iterator<ModelCacheLocation> j = items.iterator(); j.hasNext(); ) {
//					   			ModelCacheLocation item = j.next();
//					   			
//					   			///calculating distance
//					   			LatLng loc = new LatLng(item.getLoc().get(1),item.getLoc().get(0));
//					   			current_loc = new Location("cur");
//					   			current_loc.setLatitude(loc.latitude);
//					   			current_loc.setLongitude(loc.longitude);
//					        	
//					        	track_date = new Date(item.getDate() * 1000);
//					        	if(prevloc != null){	        	
//					        		subdistane += prevloc.distanceTo(current_loc);
//					        	}
//					        	prevloc = current_loc;
//					        	subpolylines.add(loc);
//					        	
//					        	if(idex == 0){
//					        		CircleOptions circleOptions = new CircleOptions()
//					        				  .center(loc)   //set center
//					        				  .radius(50)   //set radius in meters
//					        				  .fillColor(Color.RED)
//					        				  .strokeColor(Color.BLACK)
//					        				  .strokeWidth(5);
//					        				
//					        		gm.addCircle(circleOptions);
//					        		/*gm.addMarker(new MarkerOptions()
//						   	                .position(loc)
//						   	             .snippet("Start here")
//						   	             .title("Time : " + track_date.toString()));*/
//					        	}
//					        	idex =idex+1;
//					        	if(!j.hasNext()){
//					        		Long duration =  (item.getDate() - item.getStart_date_time()) * 1000;
//					        		
//					        		int seconds = (int) (duration / 1000) % 60 ;
//					        		int minutes = (int) ((duration / (1000*60)) % 60);
//					        		int hours   = (int) ((duration / (1000*60*60)) % 24);
//					        		
//					        		String durationstr = hours + ":" + minutes + ":" + seconds + "";
//					        		
//					        		gm.addMarker(new MarkerOptions()
//						   	                .position(loc)
//						   	             .snippet(" Dis : " + subdistane + ", Duration : " + durationstr )
//						   	             .title("Time : " + track_date.toString()))
//					        		.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//					        		
//					        		
//					        		
//					        		CameraUpdate center= CameraUpdateFactory.newLatLngZoom(loc, 15);
//					   	        	gm.moveCamera(center);
//					        		
//					        	}
//							}
//							Random rnd = new Random(); 
//							  int color = Color.argb(255, 50+ rnd.nextInt(100),50+ rnd.nextInt(100), 50+rnd.nextInt(100));
//							Polyline line = gm.addPolyline(new PolylineOptions()
//				   	   	    	     .addAll(subpolylines)
//				   	   	    	     .width(10)
//				   	   	    	     .color(color));
//							totaldistance += subdistane;
//						}
//						
//						textTotalDistance.setText("Total Distance : " + totaldistance + " meters.");
//						
//						
						
					}
				});
				getcache.execute(start + "",end + "",0+"");
				
				
				
				
				
				
	   	        
			}
			
	   		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		start = end;
		Long current_time = System.currentTimeMillis() / 1000L;
    	end = current_time;
		
	
		/*Long current_time = System.currentTimeMillis() / 1000L;
		if(end > current_time){
			end = current_time;
		}
		*/
    	
		
		
		intentFetchTimerHandler.postDelayed(this, fetchDelay);
   		
   		  
   	  }
   };
 
}
