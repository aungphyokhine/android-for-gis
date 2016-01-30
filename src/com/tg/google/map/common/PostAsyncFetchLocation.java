package com.tg.google.map.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.tg.google.map.FragmentHome;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PostAsyncFetchLocation {
	BackgroundHttpClient jsonParser;

    private ProgressDialog pDialog;
    private DBHelper db;
    private Context context;
    PostHttpClient httpclient;
    private List<ModelFetchLocation> dblocations = new ArrayList<ModelFetchLocation>();
    private OnDBFetchLocations listener;
    
    public PostAsyncFetchLocation(final Context context,final OnDBFetchLocations listener) {
		super();
		this.context = context;
		db = new DBHelper(this.context);
		this.context = context;
		this.listener = listener;
		
		httpclient = new PostHttpClient(context,new OnTaskCompleted() {
			
			@Override
			public void onTaskCompleted(String result) {
				if (result != null) {
	                Log.d("JSON result", result.toString());

	                GISInfoData gisinfos = new Gson().fromJson(result.toString(), GISInfoData.class);
	                
	                if(gisinfos.getSuccess()){
	                	long lastfetchdate = Long.MIN_VALUE;
	                	List<ModelFetchLocation> locations = dblocations;
	                	List<ModelFetchLocation> forinsert = new ArrayList<ModelFetchLocation>();
		                for(Iterator<GISInfo> i = gisinfos.items.iterator(); i.hasNext(); ) {
		                	GISInfo item = i.next();
		                	LatLng loc = new LatLng(item.getLoc().get(1),item.getLoc().get(0));
		                	ModelFetchLocation fetchloc = new ModelFetchLocation();
		                	fetchloc.setDate(item.getDate());
		                	Log.d("fd", item.getDate() + "");
		                	fetchloc.setDatestring(item.getDatestring());
		                	fetchloc.setStart_date_time(item.getStart_date_time());
		                	fetchloc.setLoc(item.getLoc());
		                	fetchloc.setUid(gisinfos.getEmail());
		                	locations.add(fetchloc);
		                	forinsert.add(fetchloc);
		                	if(lastfetchdate < item.getDate()){
		                		lastfetchdate = item.getDate();
		                	}
		                	
		            	}
		                
		                
		                
		                if(lastfetchdate != Long.MIN_VALUE){
		                	Log.d("save last fetch date", lastfetchdate + "");
			                db.saveLastFetchDate(lastfetchdate + "");
		                }
		                
		    			
		                DbAsyncFetchLocationInsert dbinsert = new DbAsyncFetchLocationInsert(context, forinsert, "", new OnResults() {
							
							@Override
							public void onTaskResult(ModelResult result) {
							
								
							}
						});
		                dbinsert.execute();
		                listener.onTaskResult(locations);
	                }
	                else{
	                	listener.onTaskResult(dblocations);
	                }
	                
	            }
	            else{
	            	listener.onTaskResult(dblocations);
	            	
	            }
			}
		});
	}



   
    public GISInfoData execute(String... args) {

        try {
        	
        	final String email = args[0];
        	final String token = db.getAppData().getToken();
        	final String url = "http://express4-gistracker.rhcloud.com/gisinfo/getbyuser";
        	final String start = args[1];
        	final String end = args[2];
        	//String start = db.getMaxFetchDateByUser(email) + "";
        	//long unixTime = System.currentTimeMillis() / 1000L;	
        	
            //String end = unixTime + ""; //intent.getStringExtra("end");
        	Log.d("download range", "from " + start + ",to " + end);
    		
    		
    		
    		
    		///get from cache first
    		final long maxcachedate = db.getMaxFetchDateByUser(email);
    		
    		DbAsyncFetchLocationGet dbasynfetch = new DbAsyncFetchLocationGet(context,new OnDBFetchLocations() {
					
					@Override
					public void onTaskResult(List<ModelFetchLocation> result) {
						// TODO Auto-generated method stub
						dblocations = result;
						if(maxcachedate != 0){
							db.saveLastFetchDate(maxcachedate + "");
						}
						
						
						long startdate = Long.parseLong(start);
						String paramsString = "";
						
						Log.d("date compare", maxcachedate + " > " + startdate);
						if(maxcachedate > startdate){
							paramsString =  "{\"start\":" + maxcachedate + ",\"end\":" + end + ",\"email\":\"" + email + "\",\"token\": \""+ token +"\"}";
						}
						else{
							paramsString =  "{\"start\":" + start + ",\"end\":" + end + ",\"email\":\"" + email + "\",\"token\": \""+ token +"\"}";
						}
						
						httpclient.execute(url, "POST", paramsString);
					}
				});
    			
    			dbasynfetch.execute(start,end,email);
    		
    			
    		
    		
    		
            

        } catch (Exception e) {
            e.printStackTrace();
            
        }

        return null;
    }

   

        
    

}
