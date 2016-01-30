package com.tg.google.map;
import com.google.gson.Gson;
import com.tg.google.map.FragmentCustom;
import com.tg.google.map.R;
import com.tg.google.map.FragmentCustom.OnFragmentStatusListener;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.ModelResult;
import com.tg.google.map.common.OnTaskCompleted;
import com.tg.google.map.common.PostHttpClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class FragmentResetPassword extends FragmentCustom{
	EditText email;
	DBHelper db;
	

	

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
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			db = new DBHelper(getActivity());			
		}
	
	@Override
	 public void onViewCreated(View view, Bundle savedInstanceState) {
	     super.onViewCreated(view, savedInstanceState);
	     email = (EditText)view.findViewById(R.id.text_email);
	    
	    
	     view.findViewById(R.id.button_reset).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String regemail = email.getText().toString();
				
				PostHttpClient http = new PostHttpClient(getActivity(), new OnTaskCompleted() {
					
					@Override
					public void onTaskCompleted(String result) {
						// TODO Auto-generated method stub
						ModelResult resultmessage = new Gson().fromJson(result.toString(), ModelResult.class);
						if(resultmessage.getSuccess()){
							Toast.makeText(getActivity(), "Reset Success", 1000).show();
						}
						else{
							Toast.makeText(getActivity(), resultmessage.getMessage(), 1000).show();
						}
					}
				});
				String params = "{"+
						"\"email\": \""+regemail+"\"}";
				http.execute("https://express4-gistracker.rhcloud.com/user/reset_password","POST",params);
							
				
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
       
       View pageview = inflater.inflate(R.layout.password_reset, container, false);
       

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
