package com.tg.google.map.common;


import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.tg.google.map.DialogueActivity;
import com.tg.google.map.DialougeShareLocationActivity;

import android.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class GcmMessageHandler extends IntentService {

     String mes;
     String type;
     private Handler handler;
     static String FriendRequestMessage = "FR",LocationShareMessage = "LS";
     
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        
        type = intent.getStringExtra("type");
        mes = intent.getStringExtra("request");
        Log.d("type", type);
        Log.d("mes", mes);
        if(FriendRequestMessage.equals(type)){
        	
        	ModelGCMMessage request = new Gson().fromJson(mes.toString(), ModelGCMMessage.class);
        	//sendNotification(extras, messageType, request.getMessage());
        	
        	//sendNotify();
        	sendNotify("One Friend Request on GIS",request.getMessage(),request.getEmail());
        	Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        	 // Vibrate for 500 milliseconds
        	 v.vibrate(500);
        }
        else if(LocationShareMessage.equals(type)){
        	ModelGCMMessage request = new Gson().fromJson(mes.toString(), ModelGCMMessage.class);
        	//sendNotification(extras, messageType, request.getMessage());
        	ModelShareLocation shared =  new Gson().fromJson(request.getMessage(), ModelShareLocation.class);
        	//sendNotify();
        	sendShareNotify("One Friend Request on GIS",shared,request.getEmail(),request.getMessage());
        	Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        	 // Vibrate for 500 milliseconds
        	 v.vibrate(500);
        }


        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),mes , Toast.LENGTH_LONG).show();
            }
         });

    }
    
    
    private void sendShareNotify(String title, ModelShareLocation content, String email,String raw){
    	NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_dialog_info)
                        .setContentTitle(title)
                        .setContentText(content.getMessage());
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(this, DialougeShareLocationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("shareddata", raw);
        bundle.putString("email", email);
        targetIntent.putExtras(bundle); 
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }
    
    
    private void sendNotify(String title, String content, String email){
    	NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_dialog_info)
                        .setContentTitle(title)
                        .setContentText(content);
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(this, DialogueActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("message", content);
        bundle.putString("email", email);
        targetIntent.putExtras(bundle); 
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }
    
    
}