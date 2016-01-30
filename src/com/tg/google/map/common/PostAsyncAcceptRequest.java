package com.tg.google.map.common;

import com.google.gson.Gson;
import com.tg.google.map.MainActivity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PostAsyncAcceptRequest{

	PostHttpClient jsonParser;
	private Context context;
    private ProgressDialog pDialog;
    private static final String URL = "https://express4-gistracker.rhcloud.com/request/verify";
    
    public PostAsyncAcceptRequest(Context context,final OnResults listener) {
		super();
		this.context = context;
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
	
	String t;
	String params = "{\"accept_uid\": \"" + arg0[0] + "\",\"token\" :\"" + arg0[1] + "\"" +"}";
		
        	Log.d("params", params);
           /* HashMap<String, Object[]> params = new HashMap<>();
            
            
            params.put("name", args[0]);
            params.put("password", args[1]);
*/
            Log.d("request", params);

            jsonParser.execute(URL, "POST", params);
            

        } catch (Exception e) {
            e.printStackTrace();
           
        }

       
		
		
	}


}
