package com.tusadmin.trackurspot_admin.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.tusadmin.trackurspot_admin.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.tusadmin.trackurspot_admin.Extras.Util;

/**
 * Created by KishoreKumar on 7/6/2016.
 */
public class FcmFragment extends Fragment {
    private String TAG = "FcmFragment";

    private Spinner institute_spinner, bus_spinner;
    private EditText fcm_text;
    private Button fcm_button;

    private int length = 0;
    private String c_selected = "";

    private HttpPost httppost;
    private ProgressDialog progressDialog;

    private ArrayAdapter<String> instituteAdapter;
    private ArrayAdapter<String> busAdapter;

    private List<String> institution;
    private List<String> busno;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fcm, container, false);

        institute_spinner = (Spinner) view.findViewById(R.id.institute_spinner);
        bus_spinner = (Spinner) view.findViewById(R.id.bus_spinner);
        fcm_text = (EditText) view.findViewById(R.id.fcm_text);
        fcm_button = (Button) view.findViewById(R.id.send);
        final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);

        institution = new ArrayList<String>();
        institution.add("ALL");

        busno = new ArrayList<String>();

        instituteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, institution);
        busAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, busno);

        //to add more space between items in spinner
        instituteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        institute_spinner.setAdapter(instituteAdapter);
        bus_spinner.setAdapter(busAdapter);

        institute_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        institute_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                c_selected = institution.get(position);
                showDialog(c_selected, "Fetching data....");
                busAdapter.clear();
                busAdapter.add("ALL");
                new busid_class().execute();

                bus_spinner.setVisibility(View.VISIBLE);
                bus_spinner.setAnimation(animation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*
        bus_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */

        fcm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = fcm_text.getText().toString();
                String selected_institution = institute_spinner.getSelectedItem().toString();
                String selected_busid = bus_spinner.getSelectedItem().toString();
                Util.showToast(selected_institution+" "+selected_busid+" "+msg,getContext());
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //showDialog("Institution","Loading...");
        new insitution_class().execute();

    }

    private void showDialog(String c_selected, String s) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(c_selected);
        progressDialog.setMessage(s);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private class insitution_class extends AsyncTask<String, Integer, HttpResponse> {

        @Override
        protected void onPreExecute() {
            Log.w(TAG,"on pre execute");
            super.onPreExecute();
        }

        @Override
        protected HttpResponse doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpclient = new DefaultHttpClient();

            httppost = new HttpPost("http://www.trackurspot.com/admin_app/fcm.php");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            Log.w(TAG, c_selected);
            nameValuePairs.add(new BasicNameValuePair("getins", "1"));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                Util.showToast("Error Fetching Data",getContext());
                progressDialog.cancel();
                e.printStackTrace();
            }

            JSONObject json = new JSONObject();

            try {
                // JSON data:
                JSONArray postjson = new JSONArray();
                postjson.put(json);
                // Execute HTTP Post Request
                response = httpclient.execute(httppost);
                Log.w(TAG, "Request sent " + httppost);
            } catch (Exception e) {
                Util.showToast("Error Fetching Data",getContext());
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
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null; ) {
                        builder.append(line).append("\n");
                    }
                    Log.w("wow3", builder.toString());
                    JSONTokener tokener = new JSONTokener(builder.toString());
                    JSONArray jsonArray = new JSONArray(tokener);
                    length = jsonArray.length();
                    Log.w(TAG, "length = " + length);
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject != null) {
                            //Log.w("Received ", "Received " + jsonObject.getString("busno"));
                            institution.add(jsonObject.getString("ins_name"));
                        }
                    }
                }
            } catch (Exception e) {
                Util.showToast("Error Fetching Data",getContext());
                progressDialog.cancel();
            }
            instituteAdapter.notifyDataSetChanged();
            progressDialog.cancel();
        }

    }

    private class busid_class extends AsyncTask<String, Integer, HttpResponse> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HttpResponse doInBackground(String... params) {
            HttpResponse response = null;
            HttpClient httpclient = new DefaultHttpClient();

            httppost = new HttpPost("http://www.trackurspot.com/admin_app/fcm.php");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            Log.w(TAG, c_selected);
            nameValuePairs.add(new BasicNameValuePair("getbus", c_selected));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
                Util.showToast("Error Fetching Data",getContext());
                progressDialog.cancel();
                e.printStackTrace();
            }

            JSONObject json = new JSONObject();

            try {
                // JSON data:
                JSONArray postjson = new JSONArray();
                postjson.put(json);
                // Execute HTTP Post Request
                response = httpclient.execute(httppost);
                Log.w(TAG, "Request sent " + httppost);
            } catch (Exception e) {
                Util.showToast("Error Fetching Data",getContext());
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
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null; ) {
                        builder.append(line).append("\n");
                    }
                    Log.w("wow3", builder.toString());
                    JSONTokener tokener = new JSONTokener(builder.toString());
                    JSONArray jsonArray = new JSONArray(tokener);
                    length = jsonArray.length();
                    Log.w(TAG, "length = " + length);
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject != null) {
                            //Log.w("Received ", "Received " + jsonObject.getString("busno"));
                            busno.add(jsonObject.getString("busno"));
                        }
                    }
                }
            } catch (Exception e) {
                Util.showToast("Error Fetching Data",getContext());
                progressDialog.cancel();
            }
            busAdapter.notifyDataSetChanged();
            progressDialog.cancel();
        }

    }


}
