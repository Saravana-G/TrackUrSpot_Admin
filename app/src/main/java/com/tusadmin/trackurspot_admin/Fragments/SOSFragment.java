package com.tusadmin.trackurspot_admin.Fragments;

/**
 * Created by KishoreKumar on 06-Jul-16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tusadmin.trackurspot_admin.Databases.SOSDatabase;
import com.tusadmin.trackurspot_admin.DatabaseHandler;
import com.tusadmin.trackurspot_admin.R;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SOSFragment extends Fragment implements
        SwipeActionAdapter.SwipeActionListener{

    private SwipeActionAdapter mAdapter;
    private View view;
    private ListView lv;


    private String TAG = "OverSpeedFragment";
    private ArrayAdapter stringAdapter;
    private DatabaseHandler db_handler;
    private List<SOSDatabase> sos_list;

    //to handle continuous swipe before disappearing prev snack bar
    private int cur_position_list , prev_position_list = -1;
    private boolean flag_snackbar_showing = false;
    private String name;
    private Snackbar bar;

    private BroadcastReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overspeed,container,false);
        lv = (ListView) view.findViewById(R.id.listview);

        db_handler = new DatabaseHandler(getContext());

        sos_list = db_handler.getAll_SOSdata();
        String[] content = new String[sos_list.size()];

        for (int i = 0; i < sos_list.size(); i++) {
            content[i] = sos_list.get(i).getDate().replaceAll("(.{2})", "$1:")+"-"+sos_list.get(i).getName();
        }
        //content[0] = "rr";
        //content[1] = "11";

        stringAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.fragment_support_row_bg,
                R.id.text,
                new ArrayList<>(Arrays.asList(content))
        );

        mAdapter = new SwipeActionAdapter(stringAdapter);
        mAdapter.setSwipeActionListener(this)
                .setDimBackgrounds(true)
                .setListView(lv);

        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //to send message
                open_dialog((String) stringAdapter.getItem(position));
                //Toast.makeText(getContext(), position+"", Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.fragment_support_row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.fragment_support_row_bg_right);
        return view;
    }



    @Override
    public boolean hasActions(int i, SwipeDirection direction) {
        if(direction.isLeft()) return true;
       // if(direction.isRight()) return true;
        return false;
    }

    @Override
    public boolean shouldDismiss(int i, SwipeDirection direction) {
        return direction == SwipeDirection.DIRECTION_NORMAL_LEFT || direction == SwipeDirection.DIRECTION_FAR_LEFT;
    }

    @Override
    public void onSwipe(int[] positionList, SwipeDirection[] directionList) {
        for(int i=0;i<positionList.length;i++) {
            SwipeDirection direction = directionList[i];
            final int position = positionList[i];
            name = (String) stringAdapter.getItem(position);

            switch (direction) {
                case DIRECTION_FAR_LEFT:
                case DIRECTION_NORMAL_LEFT:
                    if(prev_position_list == -1){
                        prev_position_list = position;
                    }
                    cur_position_list = position;
                    stringAdapter.remove(stringAdapter.getItem(position));
                    stringAdapter.notifyDataSetChanged();
                    Log.w(TAG, "left swipe");

                    perform_snackbar();
                    break;

                /*
                case DIRECTION_FAR_RIGHT:
                case DIRECTION_NORMAL_RIGHT:
                    open_dialog();
                    break;
                */
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    private void open_dialog(String content) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.alertdialog_respond_msg, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptsView);
        final TextView userInput = (TextView) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(content);
        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                //result.setText(userInput.getText());
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void perform_snackbar() {
        bar = Snackbar.make(getView(), "Notification deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
        bar.show();
        flag_snackbar_showing = true;

        bar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                flag_snackbar_showing = false;
                String[] tokens = name.split("-");
                tokens[0]=tokens[0].replace(":","");

                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE:
                        flag_snackbar_showing = true;
                        //delete from data base
                        db_handler.delete_SOS(tokens[0]);
                        Log.w(TAG, "Snackbar dismissed continuously own " + prev_position_list);
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                        //delete from data base
                        db_handler.delete_SOS(tokens[0]);
                        Log.w(TAG, "Snackbar dismissed own " + cur_position_list);
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_MANUAL:
                        //delete from data base
                        db_handler.delete_SOS(tokens[0]);
                        Log.w(TAG, "Snackbar dismissed manually " + cur_position_list);
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        stringAdapter.insert(name, cur_position_list);
                        Log.w(TAG, "Snackbar dismissed by action " + cur_position_list);
                        break;
                }
                prev_position_list = cur_position_list;

            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
            }
        });

    }

    @Override
    public void onStop() {

        Log.w(TAG, "stop snackbar showing = " + flag_snackbar_showing);
        if(flag_snackbar_showing){
            bar.dismiss();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    private void registerReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.w(TAG,"received some data");
                //extract our message from intent
                if (intent.getExtras() != null) {
                    Log.w(TAG,"received some data");
                    Bundle bundle = intent.getExtras();
                    HashMap<String, String> data = (HashMap<String, String>) bundle.getSerializable("sos");
                    //log our message value
                    if (data != null) {
                        Log.d(TAG, "received");

                        // Get a set of the entries
                        Set set = data.entrySet();
                        // Get an iterator
                        Iterator i = set.iterator();
                        // Display elements
                        while (i.hasNext()) {
                            Map.Entry me = (Map.Entry) i.next();
                            Log.d(TAG, " key = " + me.getKey() + " value=" + me.getValue());
                        }

                        if (data.get("message") != null) {
                            Log.w(TAG, data.get("message"));
                            stringAdapter.add(data.get("time").replaceAll("(.{2})", "$1:") + "-" + data.get("message"));
                            stringAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "no data");
                        }
                    }
                }
            }
        };
        //registering our receiver
        IntentFilter intentFilter = new IntentFilter("com.trackurspot.action.sos");
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister our receiver
        getActivity().unregisterReceiver(this.mReceiver);
    }
}
