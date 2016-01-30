package com.tg.google.map.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	 
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "GisTracker";
    
    private static final String TABLE_CACHE_LOCATION = "cache_locations";
    private static final String TABLE_FETCH_LOCATION = "fetch_locations";
    private static final String TABLE_FRIENDS = "friends";
    private static final String KEY_DATE = "date";
    private static final String KEY_START_DATE = "start_date_time";
    private static final String KEY_UID = "uid";
    private static final String KEY_TYPE = "type";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    
    private static final String KEY_FRIEND_ID = "friend_id";
    private static final String KEY_FRIEND_STATUS = "status";
    
    private Context currentcontext;
 
    public DBHelper(Context context) { 	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
        this.currentcontext = context;
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_TABLE_CACHE_LOCATION = "CREATE TABLE " + TABLE_CACHE_LOCATION +" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TYPE + " TEXT,"+
                KEY_DATE + " REAL, "+
                KEY_START_DATE + " REAL, "+
                KEY_LON + " REAL, "+
                KEY_LAT + " REAL )";
 
        // create books table
        db.execSQL(CREATE_TABLE_CACHE_LOCATION);
        
        String CREATE_TABLE_FETCH_LOCATION = "CREATE TABLE " + TABLE_FETCH_LOCATION +" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_UID + " TEXT,"+
                KEY_TYPE + " TEXT,"+
                KEY_DATE + " REAL, "+
                KEY_START_DATE + " REAL, "+
                KEY_LON + " REAL, "+
                KEY_LAT + " REAL )";
 
        // create books table
        db.execSQL(CREATE_TABLE_FETCH_LOCATION);
        
       /* String CREATE_TABLE_FRIENDS = "CREATE TABLE " + TABLE_FRIENDS +" ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_FRIEND_ID + " TEXT,"+
                KEY_DATE + " REAL, "+
                KEY_FRIEND_STATUS + " INT )";
 
        // create books table
        db.execSQL(CREATE_TABLE_FRIENDS);*/
   
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        
        //this.onCreate(db);
    }
    
    
    public void addCacheLocation(ModelCacheLocation loc){
	        //for logging
	    	Log.d("addcache", loc.toString()); 
	
	    	// 1. get reference to writable DB
	    	SQLiteDatabase db = this.getWritableDatabase();
	
	    	// 2. create ContentValues to add key "column"/value
	    	ContentValues values = new ContentValues();
	    	values.put(KEY_DATE, loc.getDate());
	    	values.put(KEY_START_DATE, loc.getStart_date_time());
	    	values.put(KEY_TYPE, loc.getType()); 
	    
	    	values.put(KEY_LON, loc.getLoc().get(0)); 
	    	values.put(KEY_LAT, loc.getLoc().get(1)); 
	    	
	    	Log.d("DB Insert loc", loc.getLoc().get(0) + "," +loc.getLoc().get(1));
			// 3. insert
	    	db.insert(TABLE_CACHE_LOCATION, // table
	        null, //nullColumnHack
	        values); // key/value -> keys = column names/ values = column values
	
			// 4. close
			db.close(); 
	}
    
    public void clearFetchLocation(String user){
        //for logging
    	
    	// 1. get reference to writable DB
    	SQLiteDatabase db = this.getWritableDatabase();

    	// 2. create ContentValues to add key "column"/value
    	db.delete(TABLE_FETCH_LOCATION, KEY_UID + "= '" + user + "'", null);
    	
    	
		db.close(); 
    }
    
    public void addFetchLocation(List<ModelFetchLocation> locs){
        //for logging
    	
    	// 1. get reference to writable DB
    	SQLiteDatabase db = this.getWritableDatabase();

    	// 2. create ContentValues to add key "column"/value
    	Log.d("addfetch", locs.size() + ""); 
    	
    	for(Iterator<ModelFetchLocation> i = locs.iterator(); i.hasNext(); ) {
			ModelFetchLocation loc = i.next();
			
			
			ContentValues values = new ContentValues();
	    	values.put(KEY_UID, loc.getUid());
	    	values.put(KEY_DATE, loc.getDate() + "");
	    	values.put(KEY_START_DATE, loc.getStart_date_time());
	    	values.put(KEY_TYPE, loc.getType()); 
	    	values.put(KEY_LON, loc.getLoc().get(0)); 
	    	values.put(KEY_LAT, loc.getLoc().get(1)); 
	    	

			// 3. insert
	    	db.insert(TABLE_FETCH_LOCATION, // table
	        null, //nullColumnHack
	        values);
    	}
    	 // key/value -> keys = column names/ values = column values

		// 4. close
		db.close(); 
}

    public List<ModelFetchLocation> getFetchLocations(long from,long to, String email) {
        List<ModelFetchLocation> gisInfos = new LinkedList<ModelFetchLocation>();
  
        // 1. build the query
        String query = "SELECT id,"  + KEY_UID + ","+ KEY_DATE + ","+ KEY_TYPE + ","+ KEY_LON + "," + KEY_LAT   + "," + KEY_START_DATE +" FROM " + TABLE_FETCH_LOCATION + " WHERE " + KEY_DATE + " > " + from + " AND " + KEY_DATE + " < " + to + " AND " + KEY_UID + " = '" + email + "'" + " ORDER BY " + KEY_DATE ; // + " LIMIT 10";
  
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        // 3. go over each row, build book and add it to list
        ModelFetchLocation location = null;
        if (cursor.moveToFirst()) {
            do {
            	location = new ModelFetchLocation();
            	
            	location.setId(Integer.parseInt(cursor.getString(0)));
            	location.setUid(cursor.getString(1));
            	location.setDate(cursor.getLong(2));
            	location.setType(cursor.getString(3));
                List<Double> loc = Arrays.asList(cursor.getDouble(4),cursor.getDouble(5));
                location.setStart_date_time(cursor.getLong(6));
                location.setLoc(loc);
               
                // Add book to books
                gisInfos.add(location);
            } while (cursor.moveToNext());
        }
  
        Log.d("getAllgisInfos()", gisInfos.toString());
        db.close(); 
        // return books
        return gisInfos;
    }
    public List<ModelCacheLocation> getCacheLocations(long from,long to, int limit) {
        List<ModelCacheLocation> gisInfos = new LinkedList<ModelCacheLocation>();
  
        // 1. build the query
        String query = "SELECT id," + KEY_DATE + ","+ KEY_TYPE + ","+ KEY_LON + "," + KEY_LAT  + "," + KEY_START_DATE +" FROM " + TABLE_CACHE_LOCATION + " WHERE " + KEY_DATE + " > " + from + " AND " + KEY_DATE + " < " + to + " ORDER BY " + KEY_DATE ;
        if(limit > 0){
        	query +=  " LIMIT 100";
        }
        
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        // 3. go over each row, build book and add it to list
        ModelCacheLocation location = null;
        if (cursor.moveToFirst()) {
            do {
            	location = new ModelCacheLocation();
            	
            	location.setId(Integer.parseInt(cursor.getString(0)));
            	location.setDate(cursor.getLong(1));
            	location.setType(cursor.getString(2));
                List<Double> loc = Arrays.asList(cursor.getDouble(3),cursor.getDouble(4));
                location.setStart_date_time(cursor.getLong(5));
                location.setLoc(loc);
               Log.d("DB Select loc", cursor.getLong(1) + ","  + cursor.getDouble(3)+ "," +cursor.getDouble(4));
                // Add book to books
                gisInfos.add(location);
            } while (cursor.moveToNext());
        }
  
        Log.d("getAllgisInfos()", gisInfos.toString());
        db.close(); 
        // return books
        return gisInfos;
    }
    
    
    public long getMaxFetchDateByUser(String user) {
       // 1. build the query
        //String query = "SELECT id," + KEY_DATE + ","+ KEY_TYPE + ","+ KEY_LON + "," + KEY_LAT  +" FROM " + TABLE_CACHE_LOCATION + " WHERE " + KEY_DATE + " > " + from + " AND " + KEY_DATE + " < " + to + " ORDER BY " + KEY_DATE + " LIMIT 100";
  
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery(query, null);
        Cursor cursor = db.query(TABLE_FETCH_LOCATION, new String [] {"MAX("+ KEY_DATE +")"}, null, null, null, null, null);
        // 3. go over each row, build book and add it to list
        long lastdate = 0;
        if (cursor.moveToFirst()) {
            do {
            	lastdate = cursor.getLong(0);
            } while (cursor.moveToNext());
        }
  
        Log.d("last fetch date", lastdate + "");
        db.close(); 
        // return books
        return lastdate;
    }
    
    
    public void saveAppData(ModelAppDataJson appjson){
    	String appdata = new Gson().toJson(appjson);	
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	editor.putString("APP_DATA", appdata);
    	editor.commit();
    }
    
    public ModelAppDataJson getAppData(){
    	
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	String appjson = sharedPref.getString("APP_DATA", null);
    	ModelAppDataJson appdata = new ModelAppDataJson();
    	if(appjson != null){
    		appdata = new Gson().fromJson(appjson, ModelAppDataJson.class);
    	}
    	else{
    		appdata.setRecord(false);
    		appdata.setSync(false);
    		appdata.setFetch(false);
    		appdata.setRecorddelay(10);
    		appdata.setSyncdelay(10);
    		appdata.setFetchdelay(10);
    		saveAppData(appdata);
    	}
    	
    	return appdata;
    }
    
    public void saveLastUploadDate(long last_upload_date){
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	editor.putLong("LAST_UPLOAD_DATE", last_upload_date);
    	editor.commit();
    }
    
    public long getLastUploadDate(){
    	
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	return sharedPref.getLong("LAST_UPLOAD_DATE", 0);
    }
    
    public void saveLastFetchDate(String last_upload_date){
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	editor.putString("LAST_FETCH_DATE", last_upload_date);
    	editor.commit();
    }
    
    public String getLastFetchDate(){
    	
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	return sharedPref.getString("LAST_FETCH_DATE", "0");
    }
    
    public void saveStartTripDate(long last_start_trip_date){
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	editor.putLong("LAST_START_TRIP_DATE", last_start_trip_date);
    	editor.commit();
    }
    
    public long getStartTripDate(){
    	
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	return sharedPref.getLong("LAST_START_TRIP_DATE", 0);
    }
    
    public void saveCurrentLocation(Location last_loc){
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	String lastloc = new Gson().toJson(last_loc);
    	editor.putString("CURRENT_LOCATION", lastloc);
    	editor.commit();
    }
    
    public Location getCurrentLocation(){
    	
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	String lastloc = sharedPref.getString("CURRENT_LOCATION", "");
    	
    	if(lastloc == ""){
    		return null;
    	}
    	return new Gson().fromJson(lastloc, Location.class);
    }
    public void saveFriendsSTR(List<String> friends){
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPref.edit();   
    	String friendsstr = new Gson().toJson(friends);
    	editor.putString("FRIENDS_STR", friendsstr);
    	editor.commit();
    }
    
    public String getFriendsSTR(){
    	
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	String friendstr = sharedPref.getString("FRIENDS_STR", "");	
    	return friendstr;
    }
    /*public void saveToken(String token){
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	editor.putString("TOKEN", token);
    	editor.commit();
    }
    
    public String getToken(){
    	SharedPreferences sharedPref = this.currentcontext.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	return sharedPref.getString("TOKEN", "");
    }*/
    
    /*public void saveEmail(Context context,String email){
    	SharedPreferences sharedPref = context.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = sharedPref.edit();
    	editor.putString("USER_EMAIL", email);
    	editor.commit();
    }
    
    public String getEmail(Context context){
    	SharedPreferences sharedPref = context.getSharedPreferences("APP_SETTING", Context.MODE_PRIVATE);
    	return sharedPref.getString("USER_EMAIL", "");
    }*/
 
}