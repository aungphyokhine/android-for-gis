package com.tg.google.map;

import java.util.concurrent.ExecutionException;

import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.PostAsyncLogin;
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
import android.widget.Toast;

public class FragmentVerification extends FragmentCustom{
	EditText txt_verify;
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
	     txt_verify = (EditText)view.findViewById(R.id.text_verify);
	    
	     view.findViewById(R.id.button_verify).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String verifytext = txt_verify.getText().toString();
				
				PostAsyncVerify verifyasync =  new PostAsyncVerify(getActivity(), new OnResults() {
					
					@Override
					public void onTaskResult(ModelResult result) {
						// TODO Auto-generated method stub
						if(result.getSuccess()){
							fragmentCallback.onChangeStatus(VERIFY_SUCCESS);
							
						}
						else{
							Toast.makeText(getActivity(), result.getMessage(), 1000).show();
						}
					}
				});
				String email = db.getAppData().getEmail();
				verifyasync.execute(email,verifytext);
				
				
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
       
       View pageview = inflater.inflate(R.layout.verification_page, container, false);
       

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
