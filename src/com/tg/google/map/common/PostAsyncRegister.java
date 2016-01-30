package com.tg.google.map.common;


import com.google.gson.Gson;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class PostAsyncRegister{

	PostHttpClient jsonParser;
	private Context context;
	private DBHelper db;
    private ProgressDialog pDialog;
    private String email;
    private static final String VERIFY_URL = "https://express4-gistracker.rhcloud.com/user/add";
    
    
    
	 public PostAsyncRegister(Context context,final OnResults listener) {
		super();
		this.context = context;
		db = new  DBHelper(context);
		jsonParser = new PostHttpClient(context, new OnTaskCompleted() {
			
			@Override
			public void onTaskCompleted(String result) {
				// TODO Auto-generated method stub
				if (result != null) {
	                Log.d("JSON result", result.toString());

	                ModelResult resultmessage = new Gson().fromJson(result.toString(), ModelResult.class);
	                if(resultmessage.getSuccess()){
	                   ModelAppDataJson data =	db.getAppData();
	                   data.setEmail(email);
	                   db.saveAppData(data);
	                }
	                
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
        	
        	
        	String params = "{\"email\":\"" + arg0[0] + "\",\"password\":\"" + arg0[1] + "\"}";
        	email = arg0[0];
        	Log.d("params", params);
           /* HashMap<String, Object[]> params = new HashMap<>();
            
            
            params.put("name", args[0]);
            params.put("password", args[1]);
*/
            Log.d("request", params);

            jsonParser.execute(VERIFY_URL, "POST", params);
            

        } catch (Exception e) {
            e.printStackTrace();
           
        }

       
		
	}
	
	
	


}
