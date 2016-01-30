package com.tg.google.map;

import com.tg.google.map.FragmentCustom.OnFragmentStatusListener;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.ModelAppDataJson;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.OnResults;
import com.tg.google.map.common.PostAsyncAcceptRequest;
import com.tg.google.map.common.PostAsyncFriendRemove;
import com.tg.google.map.common.PostAsyncLogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentFriendActions extends FragmentCustom {
	EditText txt_email;
	EditText txt_pwd;
	TextView signup;
	private DBHelper db;
	String Email,Group;
	LinearLayout track,track2,remove,remove2,accept,acccept2;
	
	 @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		Email = getArguments().getString("email");
		Group = getArguments().getString("group");
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
	     
	     TextView textemail = (TextView)view.findViewById(R.id.txtViewfriendid);
	     textemail.setText(Email);
	     LinearLayout track,track2,remove,remove2,accept,acccept2;
	     
	     LinearLayout trackll = (LinearLayout) view.findViewById(R.id.LinearLayoutTrack);
	     LinearLayout track2ll = (LinearLayout) view.findViewById(R.id.LinearLayoutTrack2);
	     LinearLayout removell = (LinearLayout) view.findViewById(R.id.LinearLayoutRemove);
	     LinearLayout remove2ll = (LinearLayout) view.findViewById(R.id.LinearLayoutRemove2);
	     LinearLayout acceptll = (LinearLayout) view.findViewById(R.id.LinearLayoutAccept);
	     LinearLayout accept2ll = (LinearLayout) view.findViewById(R.id.LinearLayoutAccept2);
	     
	     if(Group.equals("0")){
	    	 acceptll.setVisibility(View.GONE);
	    	 accept2ll.setVisibility(View.GONE);
	     }
	     else if(Group.equals("1")){
	    	 trackll.setVisibility(View.GONE);
	    	 track2ll.setVisibility(View.GONE);
	    	 acceptll.setVisibility(View.GONE);
	    	 accept2ll.setVisibility(View.GONE);
	    	 
	     }
	     else{
	    	 trackll.setVisibility(View.GONE);
	    	 track2ll.setVisibility(View.GONE);

	     }
	     
	     view.findViewById(R.id.buttontrack).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					fragmentCallback.onRequestData(HOME_GO_TRACK_OTHER,Email);
					
				}
		    	 
		     });
	     view.findViewById(R.id.buttonremove).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
					PostAsyncFriendRemove removerequest = new PostAsyncFriendRemove(getActivity(),new OnResults() {
						
						@Override
						public void onTaskResult(ModelResult result) {
							// TODO Auto-generated method stub
							if(result.getSuccess()){
								fragmentCallback.onChangeStatus(FRIEND_LIST);
								  
							}
						}
						
					
					});
					
					String token = db.getAppData().getToken();
					removerequest.execute(Email,token);
				}
		    	 
		     });
	     view.findViewById(R.id.buttonaccept).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
						PostAsyncAcceptRequest acceptrequest = new PostAsyncAcceptRequest(getActivity(),new OnResults() {
						
						@Override
						public void onTaskResult(ModelResult result) {
							// TODO Auto-generated method stub
							if(result.getSuccess()){
								fragmentCallback.onChangeStatus(FRIEND_LIST);
								  
							}
						}
					});
					String token = db.getAppData().getToken();
					acceptrequest.execute(Email,token);
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
        
        View pageview = inflater.inflate(R.layout.friend_actions, container, false);
        

        // Inflate the layout for this fragment
        return pageview;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

	
	}
