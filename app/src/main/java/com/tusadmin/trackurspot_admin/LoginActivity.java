package com.tusadmin.trackurspot_admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tusadmin.trackurspot_admin.Extras.QuickstartPreferences;

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


public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    ProgressDialog progressDialog;
    Login_class login_class;
    AsyncTask<Void, Void, Void> mRegisterTask;
    static String email, password;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }


    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("This field is required");

        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("This field is required");

        } else if (!isEmailValid(email)) {
            mEmailView.setError("invalid_email");

        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("This password is too short");
        } else {
            if (isOnline()) {
                try {
                    login_class = new Login_class();
                    login_class.execute();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (login_class.getStatus() == AsyncTask.Status.RUNNING) {
                                if (progressDialog != null)
                                    progressDialog.cancel();
                                login_class.cancel(true);
                                Utility.toast(LoginActivity.this, "Something Went Wrong");
                            }
                        }

                    }, 15000);
                } catch (Exception e) {
                    //   progressDialog.cancel();
                }
            } else {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptLogin();
                            }
                        });

                //F:\Androidkeystore\Trackurspotkey.jks keytool -list -keystore
// D9:13:7B:7A:25:C0:54:34:EB:8B:EE:95:9F:84:85:D6:52:72:B7:B7
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();
            }
        }


    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private class Login_class extends AsyncTask<String, Integer, HttpResponse> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("Logging In");
            progressDialog.setMessage("Hang in there...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected HttpResponse doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.trackurspot.com/loginapi/index.php");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("tag", "login"));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                return httpclient.execute(httppost);


            } catch (Exception e) {
                progressDialog.cancel();
                Utility.toast(LoginActivity.this, "Something Went Wrong");
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
                    if (root.getString("tag").equals("login"))
                        if (root.getString("success").equals("1")) {
                            QuickstartPreferences.insertString(QuickstartPreferences.USER_EMAIL, root.getString("email"));
                            QuickstartPreferences.insertString(QuickstartPreferences.USER_NAME, root.getString("name"));
                            QuickstartPreferences.insertString(QuickstartPreferences.USER_INSTITUTION_NAME, root.getString("institution_name"));
                            QuickstartPreferences.insertString(QuickstartPreferences.USER_INSTITUTION_ID, root.getString("institution"));
                            QuickstartPreferences.insertString(QuickstartPreferences.NOTIFICATION_MESSAGE, "");
                            progressDialog.cancel();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            progressDialog.cancel();
                            Utility.toast(LoginActivity.this, "Incorrect Username or Password");
                        }
                }
            } catch (Exception e) {
                progressDialog.cancel();
                Utility.toast(LoginActivity.this, "Something Went Wrong");
                e.printStackTrace();
            }
            progressDialog.cancel();
        }

    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() >= 3;
    }



    public void change_pass_click(View v) {
        Intent i = new Intent(getApplicationContext(),PasswordReset.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }

        super.onDestroy();
    }
}


