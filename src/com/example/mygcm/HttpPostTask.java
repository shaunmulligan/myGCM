package com.example.mygcm;

import java.net.URLEncoder;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mygcm.HttpRequest;

class HttpPostTask extends AsyncTask<String, Void, ResponseStatus> {
	String TAG = "GCM Tutorial::HttpPostTask";
    @Override
    protected ResponseStatus doInBackground( String... params ){

        try {
            // do HTTP post to google sheet 
        	postData(params[0],params[1]);
            return ResponseStatus.SUCCESS;
        } catch( Exception e ){
            return ResponseStatus.FAILURE;
        }

    }

    @Override
    protected void onPostExecute( ResponseStatus status ){
        switch( status ){
        case SUCCESS:
            // run your success callback
            break;
        case FAILURE:
            // run the failure callback
            break;
        }
    }

	public void postData(String name,String status) {

		String fullUrl = "https://docs.google.com/forms/d/1ZWJbDZiYtUDU6geTfkhKJUaCZRdxboAXkOjtBu1M-y8/formResponse";
		HttpRequest mReq = new HttpRequest();
		String col1 = name;
		String col2 = status;
		
		String data = "entry.101031925=" + URLEncoder.encode(col1) + "&" + 
					  "entry.469940721=" + URLEncoder.encode(col2);
		String response = mReq.sendPost(fullUrl, data);
		Log.i(TAG, response);
	} 
}

enum ResponseStatus {
    SUCCESS,
    FAILURE
}
