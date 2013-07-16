package com.example.mygcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
 
public class GCMIntentService extends GCMBaseIntentService {
 
    private static final String TAG = "GCM Tutorial::Service";
 
    // Use your PROJECT ID from Google API into SENDER_ID
    public static final String SENDER_ID = "1088521993156";
    //need to adjust SERVER_URL to address of raspberrypi
    public static final String SERVER_URL = "http://mulliganhome.dyndns.org:8083/reg";
 
    public GCMIntentService() {
        super(SENDER_ID);
    }
 
    @Override
    protected void onRegistered(Context context, String registrationId) {
    	try {
			postData(registrationId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.i(TAG, "onRegistered: registrationId=" + registrationId);
        
    }
 
    @Override
    protected void onUnregistered(Context context, String registrationId) {
 
        Log.i(TAG, "onUnregistered: registrationId=" + registrationId);
    }
 
    @Override
    protected void onMessage(Context context, Intent data) {
        String message;
        // Message from PHP server
        message = data.getStringExtra("message");
        // Open a new activity called GCMMessageView
        Intent intent = new Intent(this, GCMMessageView.class);
        // Pass data to the new activity
        intent.putExtra("message", message);
        // Starts the activity on notification click
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the notification with a notification builder
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Door Bell!")
                .setContentText(message).setContentIntent(pIntent)
                //.getNotification();
        		.build();
        	
        // Remove the notification on click
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
 
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(R.string.app_name, notification);
 
        {
            // Wake Android Device when notification received
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            final PowerManager.WakeLock mWakelock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
            mWakelock.acquire();
 
            // Timer before putting Android Device to sleep mode.
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    mWakelock.release();
                }
            };
            timer.schedule(task, 5000);
        }
 
    }
 
    @Override
    protected void onError(Context arg0, String errorId) {
 
        Log.e(TAG, "onError: errorId=" + errorId);
    }
    
    public void postData(String registrationId) throws JSONException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(SERVER_URL);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("username", GCMMainActivity.edit_text_value));
            nameValuePairs.add(new BasicNameValuePair("regId", registrationId));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            ResponseHandler<String> responseHandler=new BasicResponseHandler();
            String responseBody = httpclient.execute(httppost, responseHandler);
            JSONObject response=new JSONObject(responseBody);
            Log.i(TAG, "http response: "+ response.getString("status") );
            Log.i(TAG,"Name entered = "+ GCMMainActivity.edit_text_value);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	new AlertDialog.Builder(this).setTitle("Error").setMessage("No Internet Connection!").setNeutralButton("Close", null).show(); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    } 
}
