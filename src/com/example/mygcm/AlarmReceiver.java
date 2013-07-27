package com.example.mygcm;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.os.Bundle;

 
public class AlarmReceiver extends BroadcastReceiver {
	
 @Override
 public void onReceive(Context context, Intent intent) {
	 	final SharedPreferences prefs = context.getSharedPreferences("com.example.mygcm", Context.MODE_PRIVATE);
   try {	   
	   Bundle bundle = intent.getExtras();
	   String message = bundle.getString("alarm_message");
	   if(prefs.getBoolean("clockedIn", false)){
		   Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		   GCMMainActivity inst = GCMMainActivity.instance();
		   String prefName = prefs.getString("username", "null");
		   prefs.edit().putBoolean("clockedIn", false).commit();
		   new HttpPostTask().execute(prefName,"clock out","auto");
		   if(inst != null)  { // your activity can be seen, and you can update it's context 
		       inst.alertFromAlarm();
		   }
	   }
	   
    } catch (Exception e) {
    	Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_LONG).show();
    	e.printStackTrace();
    }
 }
 
}