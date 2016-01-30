package com.tg.google.map;


import java.util.concurrent.ExecutionException;

import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.ModelAppDataJson;
import com.tg.google.map.common.PostAsyncLogin;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.OnResults;
import com.tg.google.map.common.PostAsyncFriendRequest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentFriendRequest extends FragmentCustom  {
		EditText txt_email;
	
		private DBHelper db;
		
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
		     txt_email = (EditText)view.findViewById(R.id.text_fri_mail);
		    
		     view.findViewById(R.id.button_invite_friend).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String email = txt_email.getText().toString();
					
					
					PostAsyncFriendRequest requestasync =  new PostAsyncFriendRequest(getActivity(), new OnResults() {
						
						@Override
						public void onTaskResult(ModelResult result) {
							if(result.getSuccess()){
								
								Log.d("request result", result.getSuccess()+"");
								//fragmentCallback.onChangeStatus(LOGIN_SUCCESS);
								
							}
							
						}
					});
					String token = db.getAppData().getToken();
					if(token !=  null){
						requestasync.execute(email,token);
						
						
						
						
					}
					else{
						fragmentCallback.onChangeStatus(NEED_LOGIN);
					}
					
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
	        
	        View pageview = inflater.inflate(R.layout.page_friend_request, container, false);
	        

	        // Inflate the layout for this fragment
	        return pageview;
	    }

	    @Override
	    public void onStart() {
	        super.onStart();

	    }

		}
