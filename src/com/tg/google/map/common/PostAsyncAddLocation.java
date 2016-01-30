package com.tg.google.map.common;

import java.util.Iterator;
import java.util.List;
import com.google.gson.Gson;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;


public class PostAsyncAddLocation {
    
    private ProgressDialog pDialog;
    
    private DBHelper db;
    private static final String URL = "http://express4-gistracker.rhcloud.com/gisinfo/add";

    private Context context;
    private long LAST_UPLOAD_DATE;
    private BackgroundHttpClient client;
    private OnResults listener;
    
    
    public PostAsyncAddLocation(Context context,final OnResults listener) {
		super();
		this.context = context;
		db = new DBHelper(this.context);
		//db.saveLastUploadDate(Long.MIN_VALUE);
		this.listener = listener;
	}




    public void execute(String... args) {

        try {
        	final String token = db.getAppData().getToken();
        	if(token ==  null){
        		Log.d("token", null);
        		
        	}
        	
        	
        	Log.d("PostAsyncAddLocation", "Start");
        	LAST_UPLOAD_DATE = db.getLastUploadDate();
        	//String token = args[0];
        	Log.d("LAST_UPLOAD_DATE", LAST_UPLOAD_DATE + "");
        	
        	DbAsyncCacheLocationGet dbcacheget = new DbAsyncCacheLocationGet(this.context, new OnDBGetCacheLocation() {
				
				@Override
				public void onTaskResult(List<ModelCacheLocation> locations) {
					// TODO Auto-generated method stub
					String locs = "";
		        	if(locations.size() > 0){
		        	
			        	for(Iterator<ModelCacheLocation> i = locations.iterator(); i.hasNext(); ) {
			        		ModelCacheLocation item = i.next();
			        			LAST_UPLOAD_DATE = item.getDate();
			        			long START_DATE_TIME = item.getStart_date_time();
				        	    locs += "{\"date\":" + LAST_UPLOAD_DATE + ",\"start_date_time\":" + START_DATE_TIME + ",\"loc\":[" + item.getLoc().get(0) + "," + item.getLoc().get(1) + "]},"; 	   
				        	    
			        	}
			        	final long recordlastuploaddate = LAST_UPLOAD_DATE;
			        	locs = "[" + locs.substring(0,locs.length() - 1) + "]";
			        	
			        	String params = "{\"gisdata\":" + locs + ",\"token\": \""+ token +"\"}";
			        	
			        	 Log.d("request", params);
			            Log.d("request", locations.size() + " loc is sent.");
			           
			           
			            if(context != null){
			            	client = new BackgroundHttpClient(context,new OnTaskCompleted() {
				    			
				    			@Override
				    			public void onTaskCompleted(String result) {
				    				// TODO Auto-generated method stub
				    				if ( result != null) {
				    	                Log.d("JSON result",  result);
				    	                ModelResult return_message = new Gson().fromJson(result.toString(), ModelResult.class);
				    	                if(return_message.getSuccess()){
				    	                	//ever things fine, saving last upload date
				    	                	 Log.d("Save Last Upload Date",  "" + Long.parseLong(return_message.getMessage()));
				    	                	db.saveLastUploadDate(Long.parseLong(return_message.getMessage()));
				    	                	
				    	                }
				    	                listener.onTaskResult(return_message);
				    	                
				    	            }
				    	            else{
				    	            	listener.onTaskResult(new ModelResult(false,"No Result","500"));
				    	            	
				    	            }
				    				
				    			}
				    		});
			            	
			            	if(locations.size() > 0){
				           	client.execute(URL, "POST", params);
			            	}
			            }
			            
			           
			        	
		        	}
		        	else{
		        		 
		        	}
					
				}
			});
        	
        	dbcacheget.execute(LAST_UPLOAD_DATE + "",Long.MAX_VALUE + "",100 + "");
        	
        	

        } catch (Exception e) {
            e.printStackTrace();
            
        }

       
    }

    

}
