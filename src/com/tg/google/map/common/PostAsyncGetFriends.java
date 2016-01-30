package com.tg.google.map.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.gson.Gson;
import com.tg.google.map.MainActivity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PostAsyncGetFriends{

	PostHttpClient jsonParser;
	private Context context;
    private ProgressDialog pDialog;
    private static final String URL = "https://express4-gistracker.rhcloud.com/request/get";
    
    
    
	 public PostAsyncGetFriends(Context context,final OnFriendsResult listener) {
		super();
		this.context = context;
		jsonParser = new PostHttpClient(context,new OnTaskCompleted() {
			
			@Override
			public void onTaskCompleted(String result) {
				// TODO Auto-generated method stub
				if (result != null) {
	                Log.d("JSON result", result.toString());
	                ModelRequestInfoData requests = new Gson().fromJson(result, ModelRequestInfoData.class);
	                listener.onTaskResult(requests);
	                
	            }
	            else{
	            	ModelRequestInfoData requestdata = new ModelRequestInfoData();
	            	requestdata.setItems(new ArrayList<ModelRequestInfo>());
	            	
	            	listener.onTaskResult(requestdata);
	            }
			}
		});
		
		// TODO Auto-generated constructor stub
	}

	
	public void execute(String... arg0) {
		// TODO Auto-generated method stub
try {
	
	String t;
	String params = "{\"token\" :\"" + arg0[0] + "\"" +"}";
		
        	
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
