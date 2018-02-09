package com.tusadmin.trackurspot_admin.Fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.tusadmin.trackurspot_admin.Extras.QuickstartPreferences;
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


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment fragment;
    private Spinner status;

    private static final String TAG = "MapsFragment";

    private HttpPost httppost;
    private ProgressDialog progressDialog;

    private String id[];
    private String busno[];
    private Double lat[];
    private Double longi[];
    private String date[];
    private String device_name[];

    private Runnable synchTimer;
    final Handler synchHandler = new Handler();
    private int length=0;

    private String p_selected = "all";
    private String c_selected = "all";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        FragmentManager fm = this.getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        fragment.getMapAsync(this);

        status = (Spinner) view.findViewById(R.id.status);

        final List<String> categories = new ArrayList<String>();
        categories.add("all");
        categories.add("active");
        categories.add("idle");
        categories.add("stopped");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        status.setAdapter(dataAdapter);

        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.w(TAG, categories.get(position) + " Selected");
                c_selected = categories.get(position);
                if (!c_selected.equals(p_selected)) {
                    p_selected = c_selected;

                    showDialog("Loading....");

                    new location_class().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private void showDialog(String s) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Fetching data");
        progressDialog.setMessage(s);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null) {
            fragment.getMapAsync(this);
        }
        showDialog("Loading...");
        synchfunction();
    }


    @Override
    public void onPause() {
        progressDialog.cancel();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        progressDialog.cancel();
        synchHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(QuickstartPreferences.latitude_single, QuickstartPreferences.longitude_single))      // Sets the center of the map to Mountain View
                .zoom(10.0f)                 // Sets the tilt of the camera to 30 degrees
                .build();
        //   Log.w("123", mMap.getCameraPosition().zoom + "");// Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        setUpMap();
    }

    private void setUpMap() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(QuickstartPreferences.latitude_single, QuickstartPreferences.longitude_single))      // Sets the center of the map to Mountain View
                .zoom(10.0f)                 // Sets the tilt of the camera to 30 degrees
                .build();
        //   Log.w("123", mMap.getCameraPosition().zoom + "");// Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // get all buses last updated location and mark it


        mMap.addMarker(new MarkerOptions().position(new LatLng(11.01, 75.9)).title("two"));

        //mMap.addMarker(new MarkerOptions().position(new LatLng(11.01, 76.9)).title("one").anchor((float) 0.5, (float) 0.5).rotation((float) 90.0));

    }

    public void synchfunction() {

        synchTimer = new Runnable() {
            @Override
            public void run() {
                new location_class().execute();
                synchHandler.postDelayed(this, 5000);
            }
        };
        synchHandler.postDelayed(synchTimer, 0);
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

            httppost = new HttpPost("http://www.trackurspot.com/admin_app/get_data.php");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            Log.w(TAG, c_selected);
            nameValuePairs.add(new BasicNameValuePair(c_selected, "1"));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e) {
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

                    id = new String[length];
                    busno = new String[length];
                    lat = new Double[length];
                    longi = new Double[length];
                    date = new String[length];
                    device_name = new String[length];

                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //if (!jsonObject.getString("longi").equals(""))
                        if (jsonObject != null) {
                            Log.w("Received ", "Received " + jsonObject.getString("id"));

                            id[i] = jsonObject.getString("id");
                            busno[i] = jsonObject.getString("busno");
                            lat[i] = Double.parseDouble(jsonObject.getString("lat"));
                            longi[i] = Double.parseDouble(jsonObject.getString("longi"));
                            date[i] = jsonObject.getString("date");
                            device_name[i] = jsonObject.getString("device_name");

                        }
                    }
                    plot();
                }
            } catch (Exception e) {
                progressDialog.cancel();
            }
            progressDialog.cancel();
        }

        private void plot(){
            mMap.clear();
            IconGenerator iconGenerator = new IconGenerator(getContext());
            Log.w(TAG,"Length = "+length);
            for (int i=0; i<length; i++){
                try {
                    Bitmap bitmap = iconGenerator.makeIcon(busno[i]);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat[i], longi[i])).title(date[i]).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                }
                catch (Exception e){
                    progressDialog.cancel();
                }
            }
        }

    }


}
