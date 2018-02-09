package com.tusadmin.trackurspot_admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.tusadmin.trackurspot_admin.Extras.QuickstartPreferences;
import com.tusadmin.trackurspot_admin.Fragments.AboutFragment;
import com.tusadmin.trackurspot_admin.Fragments.DashboardFragment;
import com.tusadmin.trackurspot_admin.Fragments.FcmFragment;
import com.tusadmin.trackurspot_admin.Fragments.MapsFragment;
import com.tusadmin.trackurspot_admin.Fragments.OverSpeedFragment;
import com.tusadmin.trackurspot_admin.Fragments.SOSFragment;

/**
 * Created by KishoreKumar on 7/5/2016.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView view;
    Toolbar toolbar;
    TextView name_text;
    TextView email_text;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    static enum Fragments {DASHBOARD_FRAGMENT, MAP_FRAGMENT, SOS_FRAGMENT, OVERSPEED_FRAGMENT, GCM_FRAGMENT, ABOUTUS_FRAGMENT, LOGOUT};
    Fragments c, i;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        view = (NavigationView) findViewById(R.id.navigation);

        FirebaseMessaging.getInstance().subscribeToTopic("topic1");
        FirebaseMessaging.getInstance().subscribeToTopic("topic2");

        setUpToolbar();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (c != i) {
                    i = c;

                    switch (c) {
                        case DASHBOARD_FRAGMENT:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left).replace(R.id.mainContent, new DashboardFragment()).commit();
                            break;
                        case MAP_FRAGMENT:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left).replace(R.id.mainContent, new MapsFragment()).commit();
                            break;
                        case SOS_FRAGMENT:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out).replace(R.id.mainContent, new SOSFragment()).commit();
                            break;
                        case OVERSPEED_FRAGMENT:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out).replace(R.id.mainContent, new OverSpeedFragment()).commit();
                            break;
                        case GCM_FRAGMENT:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out).replace(R.id.mainContent, new FcmFragment()).commit();
                            break;
                        case ABOUTUS_FRAGMENT:
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out).replace(R.id.mainContent, new AboutFragment()).commit();
                            break;
                        case LOGOUT:
                            QuickstartPreferences.remove();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();

                    }
                }
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);


            }


        };

        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                //selecting the corresponding fragment & setting the actionbar title

                if (menuItem.getItemId() == R.id.dashboard) {
                    c = Fragments.DASHBOARD_FRAGMENT;
                }

                if (menuItem.getItemId() == R.id.map_view) {
                    c = Fragments.MAP_FRAGMENT;
                }
                if (menuItem.getItemId() == R.id.sos) {
                    c = Fragments.SOS_FRAGMENT;
                }
                if (menuItem.getItemId() == R.id.overspeed) {
                    c = Fragments.OVERSPEED_FRAGMENT;
                }
                if (menuItem.getItemId() == R.id.fcm_menu) {
                    c = Fragments.GCM_FRAGMENT;
                }
                if (menuItem.getItemId() == R.id.about_us) {
                    c = Fragments.ABOUTUS_FRAGMENT;
                }
                if (menuItem.getItemId() == R.id.logout) {
                    c = Fragments.LOGOUT;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        c = Fragments.DASHBOARD_FRAGMENT;
        i = Fragments.DASHBOARD_FRAGMENT;
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new DashboardFragment()).commit();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        name_text = (TextView) findViewById(R.id.name_header);
        email_text = (TextView) findViewById(R.id.email_header);
        drawer_head();





    }





    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setting the custom actionbar
        setSupportActionBar(toolbar);
        //setting the home button
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void drawer_head() {


        name_text.setText(QuickstartPreferences.extractString(QuickstartPreferences.USER_NAME));
        email_text.setText(QuickstartPreferences.extractString(QuickstartPreferences.USER_EMAIL));


    }


}
