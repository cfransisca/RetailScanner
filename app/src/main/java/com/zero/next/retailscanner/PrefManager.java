package com.zero.next.retailscanner;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cicak on 31/03/2018.
 */

public class PrefManager {
    SharedPreferences pref;
    public SharedPreferences.Editor editor;
    Context context;
    public static final String USER_ID="user_id";
    public static final String USER_EMAIL="user_email";
    public static final String USER_NAME="user_name";
    public static final String GRAND_TOTAL="grand_total";
    public static final String PREF_NAME = "omni";
    public static final int PRIVATE_MODE = 0;

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setUserId(String userId) {
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public void setUserEmail(String userEmail) {
        editor.putString(USER_EMAIL, userEmail);
        editor.commit();
    }

    public void setUserName(String userName) {
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    public void setGrandTotal(String grandTotal) {
        editor.putString(GRAND_TOTAL, grandTotal);
        editor.commit();
    }


}
