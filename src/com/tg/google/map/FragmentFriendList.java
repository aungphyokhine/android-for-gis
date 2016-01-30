package com.tg.google.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.ModelAppDataJson;
import com.tg.google.map.common.ModelFetchLocation;
import com.tg.google.map.common.ModelFriend;
import com.tg.google.map.common.ModelRequestInfo;
import com.tg.google.map.common.ModelRequestInfoData;
import com.tg.google.map.common.OnFriendsResult;
import com.tg.google.map.common.PostAsyncGetFriends;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentFriendList extends FragmentCustom{
	EditText txt_verify;
	DBHelper db;
	/*ListView friendsview;
	ListView requestsview;
	ListView invitesview;
    ArrayList<ModelFriend> itemsList = new ArrayList<>();
    AdapterFriend friendsadapter;
    AdapterFriend invitesadapter;
    AdapterFriend requestsadapter;*/
	ExpandableListView expListView;
	public static int FRIEND = 0,PENDING =1,REQUEST = 2;
	PostAsyncGetFriends getfriends;

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
			getfriends = new PostAsyncGetFriends(getActivity(), new OnFriendsResult() {
				
				@Override
				public void onTaskResult(ModelRequestInfoData data) {
					ModelAppDataJson appdata = db.getAppData();
					listDataHeader = new ArrayList<String>();
			        listDataChild = new HashMap<String, List<ModelFriend>>();
					
					 listAdapter = new ExpandableAdapterFriend(getContext(), listDataHeader, listDataChild);

				       // setting list adapter
				       expListView.setAdapter(listAdapter);
				      
				       
					// TODO Auto-generated method stub
					
			 
			        // Adding child data
			        listDataHeader.add("Friends");
			        listDataHeader.add("Pendings");
			        listDataHeader.add("Requests");
			 
			        // Adding child data
			        final List<ModelFriend> Friends = new ArrayList<ModelFriend>();	 
			        final List<ModelFriend> Pendings = new ArrayList<ModelFriend>();	     
			        final List<ModelFriend> Requests = new ArrayList<ModelFriend>();
					for(Iterator<ModelRequestInfo> i = data.getItems().iterator(); i.hasNext(); ) {
						  ModelRequestInfo item = i.next();
						  Log.d("item", item.getApproved() + "," + item.getFrom_uid() + "," + item.getTo_uid());
						  ModelFriend items1 = new ModelFriend();
					        items1.setEmail(item.getFrom_uid());
					        items1.setImage(R.drawable.ic_plusone_medium_off_client);
					        
					        ModelFriend fritem = new ModelFriend();
					        if(appdata.getEmail().equals(item.getFrom_uid())){
					        	
					        	fritem.setEmail(item.getTo_uid());
					        	
					        	if(item.getApproved()){
					        		fritem.setStatus(FRIEND);
						        	Friends.add(fritem);
						        }
					        	else{
					        		fritem.setStatus(PENDING);
					        		Pendings.add(fritem);
					        	}
					        	
					        }
					        else if(appdata.getEmail().equals(item.getTo_uid())){
					        	fritem.setEmail(item.getFrom_uid());
					        	if(item.getApproved()){
					        		fritem.setStatus(FRIEND);
						        	Friends.add(fritem);
						        }
					        	else{
					        		fritem.setStatus(REQUEST);
					        		Requests.add(fritem);
					        	}
					        }
					        
					  }
			 
			        listDataChild.put(listDataHeader.get(0), Friends); // Header, Child data
			        listDataChild.put(listDataHeader.get(1), Pendings);
			        listDataChild.put(listDataHeader.get(2), Requests);
			        
			        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			            @Override
			            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			            	if(groupPosition == FRIEND){
			            		fragmentCallback.onRequestData(FRIEND_ACTION,Friends.get(childPosition).getEmail(),groupPosition + "");
			            	}
			            	else if(groupPosition == PENDING){
			            		fragmentCallback.onRequestData(FRIEND_ACTION,Pendings.get(childPosition).getEmail(),groupPosition + "");
			            	}
			            	else{
			            		fragmentCallback.onRequestData(FRIEND_ACTION,Requests.get(childPosition).getEmail(),groupPosition + "");
			            	}
			               
			                
			                return true;
			            }
			        });
			        expListView.setOnItemClickListener(new OnItemClickListener() {

				   		@Override
				   		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				   			// TODO Auto-generated method stub
				   			 Toast.makeText(getActivity(), position + "", 1000).show();
				   			
				   			}
				          });
				       
			        
					
				}
			});
			 
		}
	
	@Override
	 public void onViewCreated(View view, Bundle savedInstanceState) {
	     super.onViewCreated(view, savedInstanceState);
	    
	    
	        
	     //or
	     //getActivity().findViewById(R.id.yourId).setOnClickListener(this);

	 }
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	/* private ArrayList<ModelFriend> friendsdata = new ArrayList<>();
	 private ArrayList<ModelFriend> requestsdata = new ArrayList<>();
	 private ArrayList<ModelFriend> invitessdata = new ArrayList<>();
	 
	 public void getData()
	    {
		 	ModelAppDataJson appdata = db.getAppData();
		 	String token = appdata.getToken();
		 	PostAsyncGetFriends getfriends = new PostAsyncGetFriends(getActivity());
		 	//ArrayList<ModelFriend> it = new ArrayList<ModelFriend>();
		 	try {
		 		 
			  ModelRequestInfoData data =	 getfriends.execute(token).get();
			  for(Iterator<ModelRequestInfo> i = data.getItems().iterator(); i.hasNext(); ) {
				  ModelRequestInfo item = i.next();
				  Log.d("item", item.getApproved() + "," + item.getFrom_uid() + "," + item.getTo_uid());
				  ModelFriend items1 = new ModelFriend();
			        items1.setEmail(item.getFrom_uid());
			        items1.setImage(R.drawable.ic_plusone_medium_off_client);
			        
			        if(item.getApproved()){
			        	friendsdata.add(items1);
			        }
			        else if(appdata.getEmail().equals(item.getFrom_uid())){
			        	invitessdata.add(items1);
			        }
			        else if(appdata.getEmail().equals(item.getTo_uid())){
			        	requestsdata.add(items1);
			        }
			        
			  }
			  
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 

	    }*/
	ExpandableListAdapter listAdapter;
  
    List<String> listDataHeader;
    HashMap<String, List<ModelFriend>> listDataChild;
    View pageview;
	 @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, 
       Bundle savedInstanceState) {

       // If activity recreated (such as from screen rotate), restore
       // the previous article selection set by onSaveInstanceState().
       // This is primarily necessary when in the two-pane layout.
       if (savedInstanceState == null) {
    	   
           //mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
       }
       
       pageview = inflater.inflate(R.layout.el_friend_list, container, false);
       
       expListView = (ExpandableListView) pageview.findViewById(R.id.lvExp);
       prepareListData();
       // preparing list data
      
       ImageView addfriend =(ImageView) pageview.findViewById(R.id.imageViewAddfriend);
       addfriend.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			fragmentCallback.onChangeStatus(FRIEND_REQUEST);
		}
	});
       ImageView refreshfriend =(ImageView) pageview.findViewById(R.id.imageViewRefresh);
       refreshfriend.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			fragmentCallback.onChangeStatus(FRIEND_LIST);
		}
	});
       pageview.findViewById(R.id.textViewAddfriend).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				fragmentCallback.onChangeStatus(HOME_GO_TRACK_ME);
			}

		});
		
       /*getData();
       friendsview = (ListView) pageview.findViewById(R.id.ListFriends);
       requestsview = (ListView) pageview.findViewById(R.id.ListRequests);
       invitesview = (ListView) pageview.findViewById(R.id.ListInvites);
       friendsadapter = new AdapterFriend(getActivity(), friendsdata);
       friendsview.setAdapter(friendsadapter);
       requestsadapter = new AdapterFriend(getActivity(), requestsdata);
       requestsview.setAdapter(requestsadapter);
       invitesadapter = new AdapterFriend(getActivity(), invitessdata);
       invitesview.setAdapter(invitesadapter);*/
       
       // Inflate the layout for this fragment
       return pageview;
   }
	 

	 
	 private void prepareListData() {
	        
	        final ModelAppDataJson appdata = db.getAppData();
		 	String token = appdata.getToken();
		 	
		 	getfriends.execute(token);
			  
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
