package com.example.mygcm;
 
import com.google.android.gcm.GCMRegistrar;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import java.text.DateFormat;
import java.util.Date;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.mygcm.utilities;
 
public class GCMMainActivity extends Activity {
 
    String TAG = "GCM Tutorial::Activity";
    static String edit_text_value; 
    //change wifiName to sandCastle wifi
    final static String wifiName = "homenew";
    TextView txtmsg;
    String username = "null";
    
    
    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = this.getSharedPreferences("com.example.mygcm", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_gcmmain);
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        //logs current wifi connection to console
        Log.i(TAG,"wifi: "+utilities.getCurrentSsid(this));	
                
        //check the registration of the app.
        boolean isRegistered = prefs.contains("isRegistered");
        
        Log.i(TAG,"prefs contains isRegistered?: "+isRegistered);
        
        // Register Device Button
        final Button regbtn = (Button) findViewById(R.id.register);
        final EditText edit_text = (EditText)
        findViewById(R.id.message_text);
        //clock in for work button
        final Button clkInBtn = (Button) findViewById(R.id.clockIn);
        // Locate the TextView
        txtmsg = (TextView) findViewById(R.id.statusMessage);
        // Set the data into TextView
        txtmsg.setText(" Sucessfully registered ");
        //make clkInBtn invisible
        clkInBtn.setVisibility(View.GONE);
        
        if(isRegistered){
        	regbtn.setVisibility(View.GONE);
        	edit_text.setVisibility(View.GONE);
        	clkInBtn.setVisibility(1);
        }

        //registration button event listener
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
                edit_text_value = edit_text.getText().toString();
                username = edit_text_value;
                // Retrieve the sender ID from GCMIntentService.java
                // Sender ID will be registered into GCMRegistrar
                GCMRegistrar.register(GCMMainActivity.this,GCMIntentService.SENDER_ID);
                //make registration form elements disappear
                regbtn.setVisibility(View.GONE);
                edit_text.setVisibility(View.GONE);
               
                clkInBtn.setVisibility(View.VISIBLE);
                prefs.edit().putString("username", username).commit();
                prefs.edit().putBoolean("isRegistered", true).commit();
                prefs.edit().putBoolean("clockedIn",false).commit();
                Log.i(TAG,"after click, isRegistered?: "+prefs.getBoolean("isRegistered", false));
                Log.i(TAG,"after registration, clockedIn?: "+prefs.getBoolean("clockedIn", false));
            }
        });
        //Clock in and out button event
        clkInBtn.setOnClickListener(new View.OnClickListener(){
        	@Override
        	public void onClick(View v){
        		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        		try{
        			String wifiConnection = utilities.getCurrentSsid(GCMMainActivity.this);
	        		Log.i(TAG,": current wifi is:"+wifiConnection);
	        		String prefName = prefs.getString("username", "null");
	        		Log.i(TAG,"after registration, clockedIn?: "+prefs.getBoolean("clockedIn", false));
	        		//toggle clock in/out button
	        		if (!prefs.getBoolean("clockedIn", false) && wifiConnection.equals(wifiName) ){
	        			txtmsg.setText(prefName+" clocked IN for work at "+currentDateTimeString);
	        			clkInBtn.setText("Clock Out");
	        			prefs.edit().putBoolean("clockedIn",true).commit();
	        			//log data to google spreadsheet
	        			//####################################
	        			new HttpPostTask().execute(prefName,"clock in");
	        			new AlertDialog.Builder(GCMMainActivity.this).setTitle("Success").setMessage("You have successfully clocked in for work").setNeutralButton("Close", null).show();
	        			//####################################
	        		}else
	        		if(prefs.getBoolean("clockedIn", false) && wifiConnection.equals(wifiName)){
	        			txtmsg.setText(prefName+" clocked OUT for work at "+currentDateTimeString);
	        			clkInBtn.setText("Clock In");
	        			prefs.edit().putBoolean("clockedIn", false).commit();
	        			//log data to google spreadsheet
	        			//####################################
	        			new HttpPostTask().execute(prefName,"clock out");
	        			new AlertDialog.Builder(GCMMainActivity.this).setTitle("Success").setMessage("You have successfully clocked Out of work").setNeutralButton("Close", null).show();
	        			//####################################
	        		}else{
	        			//if they are not in the B&B's registered wifizone.
	        			txtmsg.setText("you are outside the wifi zone");
	        			new AlertDialog.Builder(GCMMainActivity.this).setTitle("Error").setMessage("Please Connect to SandCastle wifi!").setNeutralButton("Close", null).show();
	        		}
        		}catch(NullPointerException e){
        			Log.i(TAG,"null pointer exception raised: "+e);
        			new AlertDialog.Builder(GCMMainActivity.this).setTitle("Error").setMessage("No Internet Connection!").setNeutralButton("Close", null).show();
        		}
        	}
        });
       
    }   

}
