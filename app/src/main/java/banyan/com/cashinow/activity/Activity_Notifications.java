package banyan.com.cashinow.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.adapter.List_Notifications_Adapter;
import banyan.com.cashinow.app.App;
import banyan.com.cashinow.fragment.Fragment_Create_Challenge;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.NotificationUtils;
import banyan.com.cashinow.utils.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class Activity_Notifications extends AppCompatActivity {

    private static final String TAG = Activity_Notifications.class.getSimpleName();

    private Toolbar mToolbar;
    private TextView txt_user_name, txt_challenges, txt_won, txt_lost, emptyElement;
    private CircleImageView profile_picture;
    ListView list_view;

    JSONObject response, profile_pic_data, profile_pic_url;
    String str_email, str_name, str_picture;

    // Session Manager Class
    SessionManager session;
    String str_session_data, str_session_id;

    public static RequestQueue queue;

    public static final String TAG_NOTIFICATION_TYPE = "type";
    public static final String TAG_NOTIFICATION_NOTIFICATION_ID = "notification_id";
    public static final String TAG_NOTIFICATION_CHALLENGE_ID = "challenge_id";
    public static final String TAG_NOTIFICATION_MSG = "message";
    public static final String TAG_NOTIFICATION_MESSAGE = "message";
    public static final String TAG_NOTIFICATION_EVENT_TITLE = "event_title";
    public static final String TAG_NOTIFICATION_EVENT_NO = "event_no";
    public static final String TAG_NOTIFICATION_DATE = "date";
    public static final String TAG_NOTIFICATION_AMOUNT = "amount";
    public static final String TAG_NOTIFICATION_USER_NAME= "user_name";
    public static final String TAG_NOTIFICATION_USER_PHOTO = "user_photo";
    public static final String TAG_NOTIFICATION_CHALLENGE_TYPE = "challenge_type";
    static ArrayList<HashMap<String, String>> notifications_list;

    HashMap<String, String> params = new HashMap<String, String>();

    public List_Notifications_Adapter adapter;
    
    private String str_selected_challenge_no;

    SwipeRefreshLayout swipe_refresh;

//    current profile
    SpotsDialog dialog_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        /******************
        * find view by id
        * *****************/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        emptyElement =(TextView) findViewById(R.id.emptyElement);
        /***********************
        *  get session details
        * **********************/
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        session.check_need_to_pay();

        HashMap<String, String> user = session.getUserDetails();
        str_session_data = user.get(SessionManager.KEY_USER);
        str_session_id = user.get(SessionManager.KEY_ID);

        try {
            response = new JSONObject(str_session_data);
            str_email = response.get("email").toString();
            str_name = response.get("name").toString();
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            str_picture = profile_pic_url.getString("url");

            System.out.println("JSON RESPONSE : " + str_session_data);
            System.out.println("EMAIL : " + str_email);
            System.out.println("NAME : " + str_name);
            System.out.println("PICTURE : " + str_picture);


        } catch (Exception e) {
            e.printStackTrace();
        }


        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        list_view = (ListView) findViewById(R.id.my_recycler_view);

        notifications_list = new ArrayList<HashMap<String, String>>();


//        get current profile
        try{

            queue = Volley.newRequestQueue(Activity_Notifications.this);
            dialog_profile = new SpotsDialog(Activity_Notifications.this);
            dialog_profile.setCancelable(false);
            dialog_profile.show();
            Function_Get_Profile();

        }catch (Exception e){

        }

        /*************
        *  action
        * ************/
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {

                try {

                    swipe_refresh.setRefreshing(true);
                    queue = Volley.newRequestQueue(Activity_Notifications.this);
                    Function_Get_Notification();

                } catch (Exception e) {

                }
            }
        });

        //action
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Function_Refresh_Notification_list();

            }
        });


        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str_notification_id =notifications_list.get(position).get(TAG_NOTIFICATION_NOTIFICATION_ID);
                String str_challenge_no =notifications_list.get(position).get(TAG_NOTIFICATION_CHALLENGE_ID);
                String str_type =notifications_list.get(position).get(TAG_NOTIFICATION_TYPE);
                String str_title =notifications_list.get(position).get(TAG_NOTIFICATION_EVENT_TITLE);
                String str_message =notifications_list.get(position).get(TAG_NOTIFICATION_MESSAGE);
                String str_user_name =notifications_list.get(position).get(TAG_NOTIFICATION_USER_NAME);
                String str_user_photo =notifications_list.get(position).get(TAG_NOTIFICATION_USER_PHOTO);
                String str_challenge_type =notifications_list.get(position).get(TAG_NOTIFICATION_CHALLENGE_TYPE);

                str_selected_challenge_no = str_challenge_no;

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(Activity_Notifications.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("str_calling_activity", "Activity_Notifications");
                editor.putString("str_notification_id", str_notification_id);
                editor.putString("str_noti_challenge_no", str_selected_challenge_no);
                editor.putString("str_type", str_type);
                editor.putString("str_title", str_title);
                editor.putString("str_message", str_message);
                editor.putString("str_user_name", str_user_name);
                editor.putString("str_user_photo", str_user_photo);
                editor.putString("str_challenge_type", str_challenge_type);
                editor.commit();

                //if str_type =1 (accept challege), and 2 (Win or lose amount) then go to challenge view  activity
                // type =0 stay int this activity
                // type 4 = update challenge
                //  type 5 = create challenge

                //call challenge view activity
                if (str_type.equals("1") || str_type.equals("2") || str_type.equals("4") ) {

                    Intent intent = new Intent(Activity_Notifications.this, Activity_Challenge_Accept_Result_Challenge.class);
                    startActivity(intent);

                }
                if (str_type.equals("5")) {

                    // setup for new implicit intent
                    SharedPreferences sharedPreferences2 = PreferenceManager
                            .getDefaultSharedPreferences(Activity_Notifications.this);
                    SharedPreferences.Editor editor2 = sharedPreferences.edit();

                    editor2.putString(Fragment_Create_Challenge.TAG_CHALLENGE_CALLING_TYPE, Fragment_Create_Challenge.TAG_CHALLENGE_CREATE_CHALLENGE_FROM_NOTIFICATION );
                    editor2.putString(Fragment_Create_Challenge.TAG_CHALLENGE_NOTIFICATION_ID, str_notification_id);

                    editor2.commit();

                    Intent intent = new Intent(Activity_Notifications.this, Activity_Challenge.class);
                    startActivity(intent);

                }

            }
        });

    }

    /***************************
     * GET Active Challenges
     ***************************/

    public void Function_Get_Notification() {
        System.out.println("###  AppConfig.url_list_notifications " + AppConfig.url_list_notifications) ;
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_notifications, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("###  onResponse");
                Log.d(TAG, "### "+response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject obj1 = arr.getJSONObject(i);

                            String type = obj1.getString(TAG_NOTIFICATION_TYPE);
                            String notification_id = obj1.getString(TAG_NOTIFICATION_NOTIFICATION_ID);
                            String challenge_id = obj1.getString(TAG_NOTIFICATION_CHALLENGE_ID);
                            String message = obj1.getString(TAG_NOTIFICATION_MSG);
                            String event_title = obj1.getString(TAG_NOTIFICATION_EVENT_TITLE);
                            String event_no = obj1.getString(TAG_NOTIFICATION_EVENT_NO);
                            String amount = obj1.getString(TAG_NOTIFICATION_AMOUNT);
                            String user_name = obj1.getString(TAG_NOTIFICATION_USER_NAME);
                            String user_photo = obj1.getString(TAG_NOTIFICATION_USER_PHOTO);
                            String challenge_type = obj1.getString(TAG_NOTIFICATION_CHALLENGE_TYPE);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_NOTIFICATION_TYPE, type);
                            map.put(TAG_NOTIFICATION_NOTIFICATION_ID, notification_id);
                            map.put(TAG_NOTIFICATION_CHALLENGE_ID, challenge_id);
                            map.put(TAG_NOTIFICATION_MSG, message);
                            map.put(TAG_NOTIFICATION_EVENT_TITLE, event_title);
                            map.put(TAG_NOTIFICATION_EVENT_NO, event_no);
                            map.put(TAG_NOTIFICATION_AMOUNT, amount);
                            map.put(TAG_NOTIFICATION_USER_NAME, user_name);
                            map.put(TAG_NOTIFICATION_USER_PHOTO, user_photo);
                            map.put(TAG_NOTIFICATION_CHALLENGE_TYPE, challenge_type);

                            //if type = 1 then dont add that data in notification list
                            if (!type.equals("0"))
                            notifications_list.add(map);

                        }

                        System.out.println("HASHMAP ARRAY" + notifications_list);

                        swipe_refresh.setRefreshing(false);

                    } else if (success == 0) {

                        swipe_refresh.setRefreshing(false);


                    }

//                    set list in adapter even have data or no data
                    adapter = new List_Notifications_Adapter(Activity_Notifications.this,Activity_Notifications.this,
                            notifications_list, str_session_id);
                    list_view.setAdapter(adapter);

//                    show and hide empty list
                    if (notifications_list.size() == 0){
                        swipe_refresh.setVisibility(View.GONE);
                        emptyElement.setVisibility(View.VISIBLE);
                    }
                    else{
                        swipe_refresh.setVisibility(View.VISIBLE);
                        emptyElement.setVisibility(View.GONE);
                    }


                    swipe_refresh.setRefreshing(false);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("### onErrorResponse ") ;
                swipe_refresh.setRefreshing(false);

                new AlertDialog.Builder(Activity_Notifications.this)
                        .setTitle(R.string.app_name)
                        .setMessage("Internal Error")
                        .setIcon(R.mipmap.app_icon)
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,

                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(Activity_Notifications.this, MainActivity.class);
                                        startActivity(intent);

                                    }

                                }).show();

//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id);

                System.out.println("### USER ID : " +str_session_id) ;

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }




    public void Function_Refresh_Notification_list() {

        notifications_list.clear();
        swipe_refresh.setRefreshing(true);
        queue = Volley.newRequestQueue(Activity_Notifications.this);
        Function_Get_Notification();

    }

    /***************************
     * GET Profile
     ***************************/

    public void Function_Get_Profile() {
        System.out.println("### AppConfig.url_profile "+AppConfig.url_profile);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_profile, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONObject obj1;

                        obj1 = obj.getJSONObject("data");

                        String participated = obj1.getString("participated");
                        String won = obj1.getString("won");
                        String draw = obj1.getString("draw");
                        String lost = obj1.getString("lost");
                        String user_name = obj1.getString("user_name");
                        String user_image = obj1.getString("user_image");


                        //set current profile user id
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Activity_Notifications.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_ID, str_session_id);
                        editor.putString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_NAME, user_name);
                        editor.putString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_PHOTO, user_image);

                        editor.putString(Activity_Login.TAG_LOGIN_USER_NAME, user_name);
                        editor.putString(Activity_Login.TAG_LOGIN_USER_IMAGE, user_image);

                        System.out.println("### "+Activity_Login.TAG_LOGIN_USER_NAME+" "+user_name);
                        System.out.println("### "+Activity_Login.TAG_LOGIN_USER_IMAGE+" "+user_image);

                        System.out.println("### "+Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_ID+" "+str_session_id);
                        System.out.println("### "+Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_NAME+" "+user_name);
                        System.out.println("### "+Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_PHOTO+" "+user_image);

                        editor.commit();

                        dialog_profile.dismiss();

                    } else if (success == 0) {

                        dialog_profile.dismiss();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog_profile.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog_profile.dismiss();
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id);

                System.out.println("### USER ID : " + str_session_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();

    }
}
