package com.tg.google.map.common;

import java.util.Arrays;
import java.util.List;

import com.tg.google.map.MainActivity;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class DbAsyncCacheLocationInsert extends AsyncTask<String, String, Boolean>{
	private DBHelper db;
	
	
	 public DbAsyncCacheLocationInsert(Context context) {
		super();
	
		db = new DBHelper(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	    protected void onPreExecute() {
	       
	    }
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
		
		try {
			if(MainActivity.mgps != null)			
			if(MainActivity.mgps.canGetLocation ){
				MainActivity.mgps.getLocation();
			    double lattitude = MainActivity.mgps.getLatitude();
			    double longitude = MainActivity.mgps.getLongitude();
			    
			    Log.d("Location : ",longitude + ","+ lattitude);
			    
			    Long starttripdate = db.getStartTripDate();
			    
			    
			    if(longitude > 0 && lattitude > 0 && starttripdate > 0){
			    	
			    	Boolean willrecord = true;
			    	Location newlocation = new Location("cur");
			    	Location previouspoint = db.getCurrentLocation();
			    	if(previouspoint != null){
			    		
			    		newlocation.setLatitude(lattitude);
			    		newlocation.setLongitude(longitude);
			   			float distance = previouspoint.distanceTo(newlocation);
			   			
			   			//will record just the distance greater than 5 meter.
			    		if(distance < 3){
			    			willrecord = false;
			    		}
			    	}
			    	
			    	
			    	if(willrecord){
			    	
			    	long unixTime = System.currentTimeMillis() / 1000L;
				    ModelCacheLocation location = new ModelCacheLocation();
				    location.setDate(unixTime);
				    Long start_tarck_time = db.getStartTripDate();
				    Log.d("start date : ", start_tarck_time + "");
				    location.setStart_date_time(start_tarck_time);
				    location.setType("");
				    
				    
				    List<Double> loc = Arrays.asList(longitude,lattitude);
				    location.setLoc(loc);
				    db.addCacheLocation(location);
				    db.saveCurrentLocation(newlocation);
			    	}
			    }
			    
			    
			    //db.saveLastUploadDate(location.getDate());
			}
			
			
			
			
			//long lastuploaddate = db.getLastUploadDate();  
			//Log.d("LastUploadDate : ",lastuploaddate + "");
        	//List<CacheLocation> locations =  db.getCacheLocations(Long.MIN_VALUE,Long.MAX_VALUE);
        	//Log.d("Location Count : ",locations.size() + "");
			
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		

		return true;
	}
	
	@Override
	 protected void onPostExecute(Boolean save) {

	}

}
