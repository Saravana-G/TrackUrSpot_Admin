package com.tusadmin.trackurspot_admin.Extras;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by KishoreKumar on 30-Sep-16.
 */

public class Util {

    public static void showToast(String s, Context c){
        Toast.makeText(c,s,Toast.LENGTH_SHORT).show();
    }

}
