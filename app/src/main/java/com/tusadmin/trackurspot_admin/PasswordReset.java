package com.tusadmin.trackurspot_admin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PasswordReset extends AppCompatActivity {

    String email = "";
    EditText email_reset;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwordreset);
        email_reset = (EditText) findViewById(R.id.forpas);
    }

    public void reset_pass(View v) {
        email = email_reset.getText().toString();
        new change_class().execute();
    }

    public void back_to_login(View v) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private class change_class extends AsyncTask<String, Integer, HttpResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PasswordReset.this);
            progressDialog.setTitle("Logging In");
            progressDialog.setMessage("Hang in there...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected HttpResponse doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://trackurspot.com/loginapi/index.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("tag", "forpass"));
                nameValuePairs.add(new BasicNameValuePair("forgotpassword", email));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                return httpclient.execute(httppost);


            } catch (Exception e) {
                progressDialog.cancel();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HttpResponse response) {
            super.onPostExecute(response);
            try {
                if (response != null) {
                    String responseStr = EntityUtils.toString(response.getEntity());
                    JSONObject root = new JSONObject(responseStr);
                    Log.w("response", responseStr);
                    if (root.getString("tag").equals("forpass"))
                        if (root.getString("success").equals("1")) {
                            Toast.makeText(getApplicationContext(), "Please Check Your Mail", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplication(), LoginActivity.class));
                            progressDialog.cancel();
                            finish();
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(getApplicationContext(), "Incorrect Data", Toast.LENGTH_LONG).show();
                        }
                }
            } catch (Exception e) {
                progressDialog.cancel();
                e.printStackTrace();
            }


        }

    }
}





