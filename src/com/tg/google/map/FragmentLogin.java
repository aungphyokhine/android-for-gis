package com.tg.google.map;


import java.util.concurrent.ExecutionException;

import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.PostHttpClient;
import com.tg.google.map.common.ModelAppDataJson;
import com.tg.google.map.common.PostAsyncLogin;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.OnResults;
import com.tg.google.map.common.OnTaskCompleted;
import com.tg.google.map.common.PostAsyncGCM;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentLogin extends FragmentCustom {
		EditText txt_email;
		EditText txt_pwd;
		TextView signup;
		private DBHelper db;
		private PostAsyncGCM asyncgcm;
		
		 @Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			db = new DBHelper(getActivity());	
			
		}
		 
		 @Override
		    public void onAttach(Activity activity) {
		        super.onAttach(activity);

		        // This makes sure that the container activity has implemented
		        // the callback interface. If not, it throws an exception.
		        try {
		            fragmentCallback = (OnFragmentStatusListener) activity;
		        } catch (ClassCastException e) {
		            throw new ClassCastException(activity.toString()
		                    + " must implement OnLoginStatusListener");
		        }
		    }
		 
		 @Override
		 public void onViewCreated(View view, Bundle savedInstanceState) {
		     super.onViewCreated(view, savedInstanceState);
		     txt_email = (EditText)view.findViewById(R.id.text_mail);
		     txt_pwd = (EditText)view.findViewById(R.id.text_password);
		     
		     view.findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					String email = txt_email.getText().toString();
					String password = txt_pwd.getText().toString();
					
					PostAsyncLogin loginasync =  new PostAsyncLogin(getActivity(),new OnResults() {
						
						@Override
						public void onTaskResult(ModelResult result) {
							// TODO Auto-generated method stub
							String email = txt_email.getText().toString();
							String password = txt_pwd.getText().toString();

							if(result.getSuccess()){
								//login successfull save mail token and call back
								ModelAppDataJson appjson = db.getAppData();
								appjson.setEmail(email);
								appjson.setToken(result.getMessage());
								db.saveAppData(appjson);
								final String Token = result.getMessage();
								
								asyncgcm = new PostAsyncGCM(getActivity(), new OnResults() {
									
									@Override
									public void onTaskResult(ModelResult result) {
										// TODO Auto-generated method stub
										if(result.getSuccess()){
											PostHttpClient jsonParser = new PostHttpClient(getActivity(),new OnTaskCompleted() {
												
												@Override
												public void onTaskCompleted(String output) {
													// TODO Auto-generated method stub
													Log.d("return gcm", output);
													//Toast.makeText(getActivity(), "Push ID Saved", 1000).show();
												}
											});
											ModelAppDataJson appdata = db.getAppData();
											appdata.setGcmid(result.getMessage());
											db.saveAppData(appdata);
											
											String url = "https://express4-gistracker.rhcloud.com/user/addgcm";
											String params = "{\"token\":\"" + Token + "\",\"gcmid\":\"" + result.getMessage() + "\"}";
											Log.d("gcmreg param", params);
											jsonParser.execute(url, "POST", params);
										}
										else{
											Toast.makeText(getActivity(), "Error On Push Message," + result.getMessage(), 1000).show();
										}
									}
								});
								asyncgcm.execute();
								
								fragmentCallback.onChangeStatus(LOGIN_SUCCESS);
								
							}
							else{
								//login fail
								
								if(result.getCode().equals("405")){

									//need verification will go verification page
									
									Toast.makeText(getActivity(), "Need Verification", 1000).show();
									ModelAppDataJson appjson = db.getAppData();
									appjson.setEmail(email);
									db.saveAppData(appjson);
									fragmentCallback.onChangeStatus(VERIFY_START);
									
									
								}
								else{
									//retry for login..
									Toast.makeText(getActivity(), result.getMessage(), 1000).show();
								}
							}
							
						}
					});
					loginasync.Do(email,password);
					
					
				}
		    	 
		     });

		     view.findViewById(R.id.link_signup).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						fragmentCallback.onChangeStatus(SIGN_START);
						
					}
			    	 
			     });
		     view.findViewById(R.id.textViewResetPage).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						fragmentCallback.onChangeStatus(PASSWORD_RESET);
						
					}
			    	 
			     });
		     //or
		     //getActivity().findViewById(R.id.yourId).setOnClickListener(this);

		 }
		 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	        Bundle savedInstanceState) {

	        // If activity recreated (such as from screen rotate), restore
	        // the previous article selection set by onSaveInstanceState().
	        // This is primarily necessary when in the two-pane layout.
	        if (savedInstanceState != null) {
	            //mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
	        }
	        
	        View pageview = inflater.inflate(R.layout.login_page, container, false);
	        

	        // Inflate the layout for this fragment
	        return pageview;
	    }

	    @Override
	    public void onStart() {
	        super.onStart();

	    }

		
		}
