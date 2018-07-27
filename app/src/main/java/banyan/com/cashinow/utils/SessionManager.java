package banyan.com.cashinow.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import banyan.com.cashinow.activity.Activity_Login;
import banyan.com.cashinow.activity.MainActivity;


public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_USER = "name";
    public static final String KEY_ID = "id";

//    payment
private static final String IS_NEED_TO_PAY = "IsNeedToPay";
    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String id) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_USER, name);
        editor.putString(KEY_ID, id);

        // commit changes
        editor.commit();
    }


    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Activity_Login.class); // Change it as Login Activity
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USER, pref.getString(KEY_USER, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // return user
        return user;
    }

    public HashMap<String, String> getLoginUserDetails() {
        String str_picture = "";
        HashMap<String, String> user = this.getUserDetails();
        String str_session_data = user.get(SessionManager.KEY_USER);
        String str_user_id = user.get(SessionManager.KEY_ID);

        try {
            JSONObject response = new JSONObject(str_session_data);
            JSONObject profile_pic_data = new JSONObject(response.get("picture").toString());
            JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            str_picture = profile_pic_url.getString("url");

        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> user_details = new HashMap<>();
        user_details.put("user_image", str_picture);
        user_details.put("user_id", str_user_id);

        return user_details;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Activity_Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /*
    * is user have to pay amount to admin or lose challenge
    * */
    public boolean is_need_to_pay() {
        return pref.getBoolean(IS_NEED_TO_PAY, false);
    }
    /*
     * set user have to pay amount to admin or lose challenge
     * */
    public void set_need_to_pay(boolean isNeedToPay) {

        editor.putBoolean(IS_NEED_TO_PAY, isNeedToPay);

        // commit changes
        editor.commit();
    }

    /*
    * when login user need to pay amount then go to main activity and show alert only and can not go to other activity
    * */
    public void check_need_to_pay() {
        // Check payment status
        if (this.is_need_to_pay()) {
            // user should go to main activity and show alert until he pay
            Intent i = new Intent(_context, MainActivity.class); // go to main activity onlye
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

}
