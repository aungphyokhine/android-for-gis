package com.tg.google.map.common;

import com.google.gson.Gson;
import com.tg.google.map.MainActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class PostAsyncPublishLocation{

	PostHttpClient jsonParser;
	private Context context;
    private ProgressDialog pDialog;
    private DBHelper db;
    private static final String URL = "https://express4-gistracker.rhcloud.com/gisinfo/share_location";
    
    public PostAsyncPublishLocation(final Context context,final OnResults listener) {
		super();
		this.context = context;
		db = new DBHelper(context);
		jsonParser = new PostHttpClient(context,new OnTaskCompleted() {
		
			@Override
			public void onTaskCompleted(String result) {
				// TODO Auto-generated method stub
				if (result != null) {
					
	                Log.d("JSON result", result.toString());

	                ModelResult resultmessage = new Gson().fromJson(result.toString(), ModelResult.class);
	            
	                listener.onTaskResult(resultmessage);

	            }
	            else{
	            	listener.onTaskResult(new ModelResult(false,"No Result","500"));
	            	
	            }
			}
		});
		// TODO Auto-generated constructor stub
	}

	
	public void execute(String... arg0) {
		// TODO Auto-generated method stub
try {
	if(MainActivity.mgps == null){			
		MainActivity.mgps = new GPSTracker(this.context);
	}
	
	if(MainActivity.mgps.canGetLocation ){
		MainActivity.mgps.getLocation();
	    double lattitude = MainActivity.mgps.getLatitude();
	    double longitude = MainActivity.mgps.getLongitude();
	    long unixTime = System.currentTimeMillis() / 1000L;
	    String token = db.getAppData().getToken();
	    String friendstr = db.getFriendsSTR();
	    
	    String params = "{\"data\":{\"date\":" + unixTime + ",\"loc\":["+ longitude +"," + lattitude + "],\"message\":\""+ arg0[0] +"\",\"friends\":"+  friendstr  +"},\"token\" :\"" + token + "\"" +"}";
	    if (MainActivity.mgps != null)
	    	MainActivity.mgps.stopUsingGPS();
    	Log.d("params", params);
       /* HashMap<String, Object[]> params = new HashMap<>();
        
        
        params.put("name", args[0]);
        params.put("password", args[1]);
*/
        Log.d("request", params);

        jsonParser.execute(URL, "POST", params);
	}
	
	
            

        } catch (Exception e) {
            e.printStackTrace();
           
        }

       
		
		
	}


}