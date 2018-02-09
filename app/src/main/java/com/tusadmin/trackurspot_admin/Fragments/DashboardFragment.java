package com.tusadmin.trackurspot_admin.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tusadmin.trackurspot_admin.LoginActivity;
import com.tusadmin.trackurspot_admin.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by KishoreKumar on 7/6/2016.
 */
public class DashboardFragment extends Fragment {


    private ProgressDialog progressDialog;
    private CircleProgressView active_veh, running_veh, idle_veh, stopped_veh;
    private TextView text_active,text_running,text_idle,text_stopped;

    private Runnable synchTimer;
    final Handler synchHandler = new Handler();

    private HttpPost httppost;

    private static final String TAG = "DashboardFragment";
    private int all=0,active=0,inactive=0,running=0,idle=0,stopped=0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dashboard,
                container, false);

        active_veh = (CircleProgressView) view.findViewById(R.id.circleView1);
        running_veh = (CircleProgressView) view.findViewById(R.id.circleView2);
        idle_veh = (CircleProgressView) view.findViewById(R.id.circleView3);
        stopped_veh = (CircleProgressView) view.findViewById(R.id.circleView4);

        text_active = (TextView) view.findViewById(R.id.active_count);
        text_running = (TextView) view.findViewById(R.id.running_count);
        text_idle = (TextView) view.findViewById(R.id.idle_count);
        text_stopped = (TextView) view.findViewById(R.id.stopped_count);


        active_veh.setVisibility(View.VISIBLE);
        active_veh.setTextMode(TextMode.VALUE);
        running_veh.setTextMode(TextMode.VALUE);
        idle_veh.setTextMode(TextMode.VALUE);
        stopped_veh.setTextMode(TextMode.VALUE);


        active_veh.setUnit("%");
        running_veh.setUnit("%");
        idle_veh.setUnit("%");
        stopped_veh.setUnit("%");
        active_veh.setUnitVisible(true);
        running_veh.setUnitVisible(true);
        idle_veh.setUnitVisible(true);
        stopped_veh.setUnitVisible(true);

        return view;
    }

    @Override
    public void onResume() {
        Log.w(TAG,"resume syncing...");
        synchfunction();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Hang in there...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new location_class().execute();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        synchHandler.removeCallbacksAndMessages(null);
        progressDialog.cancel();
        super.onDestroy();
    }

    private class location_class extends AsyncTask<String, Integer, HttpResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HttpResponse doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpclient = new DefaultHttpClient();

            httppost = new HttpPost("http://www.trackurspot.com/admin_app/dashboard.php");
/*
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair(c_selected, "1"));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
*/
            JSONObject json = new JSONObject();

            try {
                // JSON data:
                JSONArray postjson = new JSONArray();
                postjson.put(json);
                // Execute HTTP Post Request
                response = httpclient.execute(httppost);
                Log.w(TAG, "Request sent " + httppost);
            } catch (Exception e) {
                progressDialog.cancel();
            }
            return response;
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            super.onPostExecute(response);
            try {
                Log.w(TAG, "null");
                if (response != null) {
                    String responseStr = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = new JSONObject(responseStr);
                    Log.w(TAG,jsonObject+"");
                    if( jsonObject.getString("all") != null ){
                        all = jsonObject.getInt("all");
                        active = jsonObject.getInt("active");
                        inactive = jsonObject.getInt("inactive");
                        running = jsonObject.getInt("idle");
                        stopped = jsonObject.getInt("stopped");
                        update();
                    }
                }
                progressDialog.cancel();
            } catch (Exception e) {
                progressDialog.cancel();
            }

        }

    }

    public void synchfunction() {

        synchTimer = new Runnable() {
            @Override
            public void run() {
                Log.w(TAG,"syncing...");
                new location_class().execute();
                synchHandler.postDelayed(this, 5000);
            }
        };
        synchHandler.postDelayed(synchTimer, 0);
    }

    private void update() {

        text_active.setText(active +" / "+ all);
        text_running.setText(running +" / "+ active);
        text_idle.setText(idle +" / "+ active);
        text_stopped.setText(stopped +" / "+ active);

        active_veh.setValue(find(active,all));
        running_veh.setValue(find(running,active));
        idle_veh.setValue(find(idle,active));
        stopped_veh.setValue(find(stopped,active));

    }

    private float find(float stopped, float active) {
        return (stopped/active)*100;
    }


}
