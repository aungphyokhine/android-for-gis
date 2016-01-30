package com.tg.google.map;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;

public class FragmentCustom extends Fragment {
	OnFragmentStatusListener fragmentCallback;
	public static final String LOGIN_SUCCESS = "00";
	public static final String NEED_LOGIN = "04";
	public static  final String SIGN_START= "01";
	public static  final String VERIFY_START = "02";
	public static  final String VERIFY_SUCCESS = "03";
	public static final String FRIEND_REQUEST = "05";
	public static final String FRIEND_LIST = "06";
	public static final String FRIEND_ACTION = "07";
	
	public static final String PROFILE_UPLOAD = "08";
	public static final String PASSWORD_CHANGE = "09";
	public static final String PASSWORD_RESET = "09";
	
	
	public static  final String HOME_LOC_RECORD_ON = "10";
	public static  final String HOME_LOC_RECORD_OFF = "11";
	public static  final String HOME_LOC_SYNC_ON = "12";
	public static  final String HOME_LOC_SYNC_OFF = "13";
	public static  final String HOME_LOC_FETCH_ON = "14";
	public static  final String HOME_LOC_FETCH_OFF = "15";
	public static  final String HOME_GO_TRACK_ME = "16";
	public static  final String HOME_GO_TRACK_OTHER = "17";
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	// The container Activity must implement this interface so the frag can deliver messages
    public interface OnFragmentStatusListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onChangeStatus(String status);
        public void onChangeEvent(String evt);
        public void onRequestData(String type,String... values);
    }

}
