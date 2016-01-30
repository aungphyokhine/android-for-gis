package com.tg.google.map;



import java.util.Date;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.gson.Gson;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.ModelGCMMessage;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.ModelShareLocation;
import com.tg.google.map.common.OnResults;
import com.tg.google.map.common.PostAsyncAcceptRequest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class DialougeShareLocationActivity extends Activity  {
	
	private DBHelper db;
	private GoogleMap gm;
	SupportMapFragment mapFragment;
	MarkerOptions marker;
	CameraUpdate center= null;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		db = new DBHelper(getApplicationContext());		
		final String token = db.getAppData().getToken();
		
		String shareddata = (String) getIntent().getExtras().getString("shareddata");
		
		ModelShareLocation shareloc = new Gson().fromJson(shareddata, ModelShareLocation.class);
		final String email = (String) getIntent().getExtras().getString("email");
		
		LatLng latlng = new LatLng(shareloc.getLoc().get(1),shareloc.getLoc().get(0));
		marker = new MarkerOptions()
	                .position(latlng)
	             .snippet( email + ":" + shareloc.getMessage())
	             .title("Time : " + new Date(shareloc.getDate() * 1000));
		
		setContentView(R.layout.dialouge_share_loc);
		gm = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		 center= CameraUpdateFactory.newLatLngZoom(latlng, 15);
				gm.addMarker(marker);
				gm.moveCamera(center);
	        
		
//		mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
       // mapFragment.getMapAsync(this);
		
		
//		 this.findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					PostAsyncAcceptRequest acceptrequest = new PostAsyncAcceptRequest(DialougeShareLocationActivity.this,new OnResults() {
//						
//						@Override
//						public void onTaskResult(ModelResult result) {
//							// TODO Auto-generated method stub
//							if(result.getSuccess()){
//								
//								Intent intent = new Intent(DialougeShareLocationActivity.this, MainActivity.class);
//							    startActivity(intent);      
//							}
//						}
//					});
//					
//					acceptrequest.execute(email,token);
//					
//					
//				}
//		    	 
//		     });
	}
	
	
	 @Override
	    public void onResume() {
	        super.onResume();
	        MapsInitializer.initialize(this);
	        if (gm == null) {
				gm = mapFragment.getMap();
				gm.addMarker(marker);
				gm.moveCamera(center);
	        }
			
			
	    }
	    





	



}