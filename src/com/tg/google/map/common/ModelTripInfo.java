package com.tg.google.map.common;

import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ModelTripInfo {
	private PolylineOptions polyline;
	private MarkerOptions marker;
	private CircleOptions circle;
	private CameraUpdate update;
	private String Message;
	private Long Date;
	
	public ModelTripInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	


	public Long getDate() {
		return Date;
	}



	public void setDate(Long date) {
		Date = date;
	}



	public String getMessage() {
		return Message;
	}



	public void setMessage(String message) {
		Message = message;
	}



	public CameraUpdate getUpdate() {
		return update;
	}



	public void setUpdate(CameraUpdate update) {
		this.update = update;
	}



	public PolylineOptions getPolylineOptions() {
		return polyline;
	}
	public void setPolylineOptions(PolylineOptions polyline) {
		this.polyline = polyline;
	}
	public MarkerOptions getMarker() {
		return marker;
	}
	public void setMarker(MarkerOptions marker) {
		this.marker = marker;
	}
	public CircleOptions getCircle() {
		return circle;
	}
	public void setCircle(CircleOptions circle) {
		this.circle = circle;
	}
	
	
	
	
}
