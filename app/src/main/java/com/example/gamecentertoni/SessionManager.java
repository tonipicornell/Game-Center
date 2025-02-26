package com.example.gamecentertoni;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void createSession(String username, int userId) {
        mEditor.putString(KEY_USERNAME, username);
        mEditor.putInt(KEY_USER_ID, userId);
        mEditor.putBoolean(KEY_LOGGED_IN, true);

        mEditor.apply();
    }

    public boolean isLoggedIn() {
        return mSharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUserName() {
        return mSharedPreferences.getString(KEY_USERNAME, null);
    }

    public int getUserId() {
        return mSharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public void logOut() {
        mEditor.clear();
        mEditor.apply();
    }
}
