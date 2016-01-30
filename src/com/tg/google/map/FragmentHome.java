package com.tg.google.map;

import com.tg.google.map.FragmentCustom.OnFragmentStatusListener;
import com.tg.google.map.common.DBHelper;
import com.tg.google.map.common.ModelAppDataJson;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.textservice.SentenceSuggestionsInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

public class FragmentHome extends FragmentCustom {

	Button Next, Previous;
	private View thisFragment;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private GestureDetector gestureDetector;
	public static String FETCH_EMAIL = "";

	private ViewFlipper riddleFlipper;
	private DBHelper db;
	private Toolbar toolbar; 
	private ImageView history,friends;
	private TextView recordmin,syncmin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		db = new DBHelper(getActivity());
		
		try {
			fragmentCallback = (OnFragmentStatusListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(this.toString() + " must implement OnFragmentStatusListener");
		}
		
		
	}
	
	@Override
	public void onCreateOptionsMenu(
	      Menu menu, MenuInflater inflater) {
	   inflater.inflate(R.layout.menu_main, menu);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		ModelAppDataJson appdata = db.getAppData();
		thisFragment = LayoutInflater.from(getActivity()).inflate(R.layout.home_page, null);
		
		//toolbar.setSubtitle("Settings");
		
		setHasOptionsMenu(true);
		// views
		riddleFlipper = (ViewFlipper) thisFragment.findViewById(R.id.ViewFlipper01);
		/*final TextView fetchonoff = (TextView) thisFragment
				.findViewById(R.id.TextViewFetchOnOff);*/
		final TextView recordhonoff = (TextView) thisFragment
				.findViewById(R.id.TextViewRecordOnOff);
		final TextView synconoff = (TextView) thisFragment
				.findViewById(R.id.TextViewSyncOnOff);
		
		recordmin = (TextView) thisFragment
				.findViewById(R.id.textViewRecordTime);
		syncmin = (TextView) thisFragment
				.findViewById(R.id.textViewSyncTime);
		
		history = (ImageView) thisFragment.findViewById(R.id.imageViewHistroy);
		friends = (ImageView) thisFragment.findViewById(R.id.imageViewFriends);

		com.zcw.togglebutton.ToggleButton togglerecord = (com.zcw.togglebutton.ToggleButton) thisFragment
				.findViewById(R.id.toggleRecord);

		if (appdata.getRecord()) {
			togglerecord.setToggleOn();
			recordhonoff.setText("On");
		} else {
			togglerecord.setToggleOff();
			recordhonoff.setText("Off");
		}

		togglerecord.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean isChecked) {
				Log.d("ToggleRecord", isChecked + "");
				ModelAppDataJson appjson = db.getAppData();
				appjson.setRecord(isChecked);
				db.saveAppData(appjson);

				if (isChecked) {
					// The toggle is enabled
					recordhonoff.setText("On");
					fragmentCallback.onChangeEvent(HOME_LOC_RECORD_ON);

				} else {
					// The toggle is disabled
					recordhonoff.setText("Off");
					fragmentCallback.onChangeEvent(HOME_LOC_RECORD_OFF);
				}

			}

		});

		com.zcw.togglebutton.ToggleButton togglesync = (com.zcw.togglebutton.ToggleButton) thisFragment
				.findViewById(R.id.toggleSync);

		if (appdata.getSync()) {
			synconoff.setText("On");
			togglesync.setToggleOn();
		} else {
			synconoff.setText("Off");
			togglesync.setToggleOff();
		}

		togglesync.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean isChecked) {
				Log.d("toggleSync", isChecked + "");
				ModelAppDataJson appjson = db.getAppData();
				appjson.setSync(isChecked);
				db.saveAppData(appjson);

				if (isChecked) {
					// The toggle is enabled
					synconoff.setText("On");
					fragmentCallback.onChangeEvent(HOME_LOC_SYNC_ON);

				} else {
					// The toggle is disabled
					synconoff.setText("Off");
					fragmentCallback.onChangeEvent(HOME_LOC_SYNC_OFF);
				}

			}

		});
		
		
//		com.zcw.togglebutton.ToggleButton togglefetch = (com.zcw.togglebutton.ToggleButton) thisFragment
//				.findViewById(R.id.toggleFetch);
//		togglefetch.setToggleOff();
//		/*if (appdata.getFetch()) {
//			togglefetch.setToggleOn();
//		} else {
//			togglefetch.setToggleOff();
//		}*/
//		
//		
//
//		togglefetch.setOnToggleChanged(new OnToggleChanged() {
//
//			@Override
//			public void onToggle(boolean isChecked) {
//				Log.d("toggleFetch", isChecked + "");
//				//ModelAppDataJson appjson = db.getAppData();
//				//appjson.setFetch(isChecked);
//				//db.saveAppData(appjson);
//
//				if (isChecked) {
//					// The toggle is enabled
//					EditText fetchmail = (EditText) thisFragment
//							.findViewById(R.id.editTextFectEmail);
//					fetchonoff.setText("On");
//					Log.d("fetchmail", fetchmail.getText().toString());
//					FETCH_EMAIL = fetchmail.getText().toString();
//					fragmentCallback.onChangeEvent(HOME_LOC_FETCH_ON);
//					
//
//				} else {
//					// The toggle is disabled
//					fetchonoff.setText("Off");
//					fragmentCallback.onChangeEvent(HOME_LOC_FETCH_OFF);
//				}
//
//			}
//
//		});

		SeekBar seekrecord = (SeekBar) thisFragment.findViewById(R.id.seekBarRecord);
		seekrecord.setProgress(appdata.getRecorddelay());
		recordmin.setText(appdata.getRecorddelay() + " sec");
		
		seekrecord.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			
				// TODO Auto-generated method stub
				
				Log.d("seekrecord", seekBar.getProgress() + "");
				recordmin.setText(seekBar.getProgress() + " sec");
				ModelAppDataJson appjson = db.getAppData();
				appjson.setRecorddelay(seekBar.getProgress());
				db.saveAppData(appjson);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if(progress < 10){
					seekBar.setProgress(5);
				}

			}
		});

		SeekBar seeksync = (SeekBar) thisFragment.findViewById(R.id.seekBarSync);
		seeksync.setProgress(appdata.getSyncdelay());
		syncmin.setText(appdata.getSyncdelay() + " sec");
		seeksync.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Log.d("seekrecord", seekBar.getProgress() + "");
				syncmin.setText(seekBar.getProgress() + " sec");
				ModelAppDataJson appjson = db.getAppData();
				appjson.setSyncdelay(seekBar.getProgress());
				db.saveAppData(appjson);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if(progress < 10){
					seekBar.setProgress(5);
				}
			}
		});

		///////////////////////////
	
		thisFragment.findViewById(R.id.imageViewHistroy).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				fragmentCallback.onChangeStatus(HOME_GO_TRACK_ME);
			}

		});
		thisFragment.findViewById(R.id.textViewviewhistory).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				fragmentCallback.onChangeStatus(HOME_GO_TRACK_ME);
			}

		});
		
		
		thisFragment.findViewById(R.id.imageViewFindFriends).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				fragmentCallback.onChangeStatus(FRIEND_LIST);
			}

		});
		/*thisFragment.findViewById(R.id.Next).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				riddleFlipper.setInAnimation(getActivity(), R.anim.in_right);
				riddleFlipper.setOutAnimation(getActivity(), R.anim.out_left);
				riddleFlipper.showNext();

			}

		});

		thisFragment.findViewById(R.id.Previous).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				riddleFlipper.setInAnimation(getActivity(), R.anim.in_left);
				riddleFlipper.setOutAnimation(getActivity(), R.anim.out_right);
				riddleFlipper.showPrevious();

			}

		});*/
		gestureDetector = new GestureDetector(getActivity(), new MyGestureDetector());

		thisFragment.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);

			}
		});

		return thisFragment;
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					riddleFlipper.setInAnimation(getActivity(), R.anim.in_right);
					riddleFlipper.setOutAnimation(getActivity(), R.anim.out_left);
					riddleFlipper.showNext();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					riddleFlipper.setInAnimation(getActivity(), R.anim.in_left);
					riddleFlipper.setOutAnimation(getActivity(), R.anim.out_right);

					riddleFlipper.showPrevious();
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
	}

}
