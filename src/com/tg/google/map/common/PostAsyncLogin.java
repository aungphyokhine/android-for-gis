package com.tg.google.map.common;

import com.google.gson.Gson;
import com.tg.google.map.MainActivity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PostAsyncLogin{

	PostHttpClient jsonParser;
	private Context context;
    private ProgressDialog pDialog;
    private static final String LOGIN_URL = "https://express4-gistracker.rhcloud.com/user/login";
    

   
    
	 public PostAsyncLogin(Context context,final OnResults listener) {
		super();
		this.context = context;		
		jsonParser = new PostHttpClient(context,new OnTaskCompleted() {
			
			@Override
			public void onTaskCompleted(String result) {
				// TODO Auto-generated method stub
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

	

	public void Do(String email,String password) {
		// TODO Auto-generated method stub
try {
        	
        	
        	String params = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\" }";
        	
        	Log.d("params", params);
           /* HashMap<String, Object[]> params = new HashMap<>();
            
            
            params.put("name", args[0]);
            params.put("password", args[1]);
*/
            Log.d("request", params);
            
            jsonParser.execute(LOGIN_URL, "POST", params);

           

        } catch (Exception e) {
            e.printStackTrace();
            
        }

       
		
		
	}





}
