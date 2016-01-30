package com.tg.google.map;

import java.util.concurrent.ExecutionException;

import com.tg.google.map.FragmentCustom.OnFragmentStatusListener;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.PostAsyncRegister;
import com.tg.google.map.common.PostAsyncVerify;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.OnResults;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class FragmentRegister extends FragmentCustom{
	EditText txt_email;
	EditText txt_pwd;
	EditText txt_confirm;
	DBHelper db;
	LinearLayout beginpnl, endpnl;
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
	     final LinearLayout beginpnl = (LinearLayout)view.findViewById(R.id.Linerregisterpnl);
	     final LinearLayout endpnl = (LinearLayout)view.findViewById(R.id.Linerregistersuccesspnl);
	     endpnl.setVisibility(View.GONE);
	     
	     txt_email = (EditText)view.findViewById(R.id.text_mail);
	     txt_pwd = (EditText)view.findViewById(R.id.text_password);
	     txt_confirm = (EditText)view.findViewById(R.id.text_confirm);
	    
	     view.findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String email = txt_email.getText().toString();
				String pwd = txt_pwd.getText().toString();
				String confirm = txt_confirm.getText().toString();
				
				if(pwd.length() < 5){
					Toast.makeText(getActivity(), "Password must be at least 5 charcters long", Toast.LENGTH_LONG).show();
				}
				else{
				PostAsyncRegister registerasync =  new PostAsyncRegister(getActivity(),new OnResults() {
					
					@Override
					public void onTaskResult(ModelResult result) {
						// TODO Auto-generated method stub
						if(result.getSuccess()){
							beginpnl.setVisibility(View.GONE);
							endpnl.setVisibility(View.VISIBLE);
						}
						
						Toast.makeText(getActivity(), result.getMessage(), 1000).show();
					}
				});
				if(pwd.equals(confirm)){
					registerasync.execute(email,pwd);
					
				}
				else{
					Toast.makeText(getActivity(), "Password not match", 1000).show();
				}
				}
				
			}
	    	 
	     });
	     view.findViewById(R.id.link_login).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					fragmentCallback.onChangeStatus(VERIFY_SUCCESS);
				}
	     });
	     
	     view.findViewById(R.id.link_verification).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					fragmentCallback.onChangeStatus(VERIFY_START);
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
       
       View pageview = inflater.inflate(R.layout.register_page, container, false);
       

       // Inflate the layout for this fragment
       return pageview;
   }

   @Override
   public void onStart() {
       super.onStart();

       // During startup, check if there are arguments passed to the fragment.
       // onStart is a good place to do this because the layout has already been
       // applied to the fragment at this point so we can safely call the method
       // below that sets the article text.
       Bundle args = getArguments();
       /*if (args != null) {
           // Set article based on argument passed in
           updateArticleView(args.getInt(ARG_POSITION));
       } else if (mCurrentPosition != -1) {
           // Set article based on saved instance state defined during onCreateView
           updateArticleView(mCurrentPosition);
       }*/
   }
}
