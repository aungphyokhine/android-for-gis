package com.tg.google.map;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.tg.google.map.common.BackgroundHttpClient;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.DbAsyncFetchLocationInsert;
import com.tg.google.map.common.GISInfo;
import com.tg.google.map.common.GISInfoData;
import com.tg.google.map.common.PostHttpClient;
import com.tg.google.map.common.ModelAppDataJson;
import com.tg.google.map.common.ModelFetchLocation;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.OnResults;
import com.tg.google.map.common.OnTaskCompleted;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class LocationsPullService extends IntentService {
	public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private static final String TAG = "DownloadService";

	public LocationsPullService() {
		super(LocationsPullService.class.getName());
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String email = intent.getStringExtra("email");
        String token = intent.getStringExtra("token");
        String url = intent.getStringExtra("url");
        String start = intent.getStringExtra("start");
        
        long unixTime = System.currentTimeMillis();
        String end = unixTime + ""; //intent.getStringExtra("end");
        
        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
            	
            	
                String results = downloadData(url,start,email,token);

                /* Sending result back to activity */
                if (null != results) {
                    bundle.putString("result", results);
                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }
	
	
	private String downloadData(String requestUrl,String start,String email,String token)  {
		//geting start date
    	
    	
    	long unixTime = System.currentTimeMillis() / 1000L;	
        String end = unixTime + ""; //intent.getStringExtra("end");
    	Log.d("download range", "from " + start + ",to " + end);
		String paramsString =  "{\"start\":" + start + ",\"end\":" + end + ",\"email\":\"" + email + "\",\"token\": \""+ token +"\"}";;
		Log.d("start fetch params", paramsString);
		BackgroundHttpClient client =  new BackgroundHttpClient(this.getBaseContext(), new OnTaskCompleted() {
			
			@Override
			public void onTaskCompleted(String result) {
				// TODO Auto-generated method stub
				if (result != null) {
	                Log.d("JSON result", result.toString());

	                GISInfoData gisinfos = new Gson().fromJson(result.toString(), GISInfoData.class);
	                
	                if(gisinfos.getSuccess()){
	                	List<ModelFetchLocation> locations = new ArrayList<ModelFetchLocation>();
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
		                	
		            	}
		    			
		                DbAsyncFetchLocationInsert dbinsert = new DbAsyncFetchLocationInsert(getApplicationContext(), locations, "", new OnResults() {
							
							@Override
							public void onTaskResult(ModelResult result) {
								// TODO Auto-generated method stub
								if(!result.getSuccess())
								Toast.makeText(getBaseContext(), result.getMessage(), 1000).show();
							}
						});
		                dbinsert.execute();
	                }
	                
	            }
			}
		});
		String result;
		try {
			
		client.execute(requestUrl, "POST", paramsString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return null;
        
        //int statusCode = urlConnection.getResponseCode();

        /* 200 represents HTTP OK */
       /* if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            String results = parseResult(response);
            
            Log.d("01 result", results);
            return results;
        } else {
            throw new DownloadException("Failed to fetch data!!");
        }*/
    }


    
}