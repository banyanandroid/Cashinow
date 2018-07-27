package banyan.com.cashinow.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aromajoin.actionsheet.ActionSheet;
import com.aromajoin.actionsheet.OnActionListener;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.adapter.List_Challenge_Adapter;
import banyan.com.cashinow.adapter.List_Lose_Adapter;
import banyan.com.cashinow.adapter.List_My_Challenges_Adapter;
import banyan.com.cashinow.adapter.List_Notifications_Adapter;
import banyan.com.cashinow.adapter.List_Payment_Completed_Adapter;
import banyan.com.cashinow.adapter.List_Win_Adapter;
import banyan.com.cashinow.app.App;
import banyan.com.cashinow.fragment.Fragment_Create_Challenge;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.NotificationUtils;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;

import static banyan.com.cashinow.utils.AppConfig.url_app_image;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private Button btn_create_challenge;
    private FloatingActionButton fab_filter;
    TextView txt_empty;

    JSONObject response, profile_pic_data, profile_pic_url;
    String str_email, str_name, str_picture;

    private static long back_pressed;

    // Session Manager Class
    SessionManager session;
    String str_session_data, str_session_id, str_sort_by;

    // Toolbar Notification batch
    RelativeLayout notification_Count, notification_batch, message_Count, message_batch;
    TextView  tv_message;
    public  TextView tv_notification;
    Button btn_bell;
    int i = 0;
    String value = "nothing";
    String search_key, search_id = "";

    public static RequestQueue queue;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView list_challenges;

    public static final String TAG_CHALLENGE_ID= "challenge_id";
    public static final String TAG_CHALLENGE_USERNAME = "created_username";
    public static final String TAG_CHALLENGE_PHOTO = "created_userphoto";
    public static final String TAG_CHALLENGE_TITLE = "event_title";
    public static final String TAG_CHALLENGE_TYPE = "type";
    public static final String TAG_CHALLENGE_EVENT = "event";
    public static final String TAG_CHALLENGE_EVENT_TITLE = "event_title";
    public static final String TAG_CHALLENGE_DATE = "date";
    public static final String TAG_CHALLENGE_AMOUNT = "amount";
    public static final String TAG_CHALLENGE_CHALLENGE_NAME = "challenge_name";
    public static final String TAG_CHALLENGE_USER_ID= "user_id";

    static ArrayList<HashMap<String, String>> Challenge_list;

    HashMap<String, String> params = new HashMap<String, String>();


    //payment
    public static final String TAG_PAYMENT_BETTING_ID = "betting_id";
    public static final String TAG_PAYMENT_EVENT_TITLE= "event_title";
    public static final String TAG_PAYMENT_PAID_TO = "amount_paidto";
    public static final String TAG_PAYMENT_PAID_TO_PHOTO = "amount_paidto_photo";
    public static final String TAG_PAYMENT_ACCOUNT_DETAILS = "account_details";
    public static final String TAG_PAYMENT_AMOUNT = "amount";
    public static final String TAG_PAYMENT_WINNIG_AMOUNT = "winning_amount";
    public static final String TAG_PAYMENT_RECEIVED_FROM = "received_from";
    public static final String TAG_PAYMENT_RECEIVED_FROM_PHOTO = "received_from_photo";

    public static final String TAG_PAYMENT_CHALLENGE_AMOUNT= "challenge_amount";
//    win
    public static final String TAG_WIN_BETTING_ID= "betting_id";
    public static final String TAG_WIN_EVENT_TITLE= "event_title";
    public static final String TAG_WIN_PAID_TO_IMAGE= "amount_paidto_photo";
    public static final String TAG_WIN_PAID_TO= "amount_paidto";
    public static final String TAG_WIN_ADMIN_ACCOUNT_DETAIL= "account_details";
    public static final String TAG_WIN_AMOUNT= "amount";
    public static final String TAG_WIN_WINNING_AMOUNT= "winning_amount";
    public static final String TAG_WIN_RECEIVED_FROM= "received_from";
    public static final String TAG_WIN_RECEIVED_FROM_PHOTO= "received_from_photo";
    public static final String TAG_WIN_POPUP_ID= "popup_id";
    public static final String TAG_WIN_OPPONENT_NAME= "opponent_name";
    public static final String TAG_WIN_OPPONENT_PHOTO= "opponent_photo";
    public static final String TAG_WIN_BETTING_AMOUNT= "betting_amount";

    public static final String TAG_WIN_CLOSE_USER_ID= "user_id";
    public static final String TAG_WIN_CLOSE_POPUP_ID= "popup_id";

    //    lose
    public static final String TAG_LOSE_BETTING_ID= "betting_id";
    public static final String TAG_LOSE_EVENT_TITLE= "event_title";
    public static final String TAG_LOSE_PLAYER_IMAGE= "amount_paidto_photo";
    public static final String TAG_LOSE_PLAYER_NAME= "amount_paidto";
    public static final String TAG_LOSE_ACCOUNT_DETAILS= "account_details";
    public static final String TAG_LOSE_CHALLENGE_AMOUNT= "challenge_amount";
    public static final String TAG_LOSE_AMOUNT= "amount";

    static ArrayList<HashMap<String, String>> list_win, list_lose, list_payment;

    SpotsDialog dialog_lose,dialog_payment;

    List_Lose_Adapter  lose_adapter;
    List_Win_Adapter win_adapter;
    List_Payment_Completed_Adapter payment_adapter;

    public List_Challenge_Adapter adapter;

    //filter
    String str_selectd_filter_type= "";
    SpotsDialog dialog_filter;

    //payment
    String str_selected_bitting_id = "";
    SpotsDialog dialog_win_lose_payment;

//    challenge
    SpotsDialog dialog_challenge;

//    fb share
    public static final int FACEBOOK_ADD_STICKER_TO_STORY_REQUEST = 10;
    public static final String FACEBOOK_SHARE_STICKER_INTENT = "com.facebook.share.ADD_STICKER_TO_STORY";


//     notification
    SpotsDialog dialog_notification;

//    current user
    SpotsDialog dialog_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("### Main Activity : onCreate ");
        setContentView(R.layout.activity_main);

        //find view by id
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        btn_create_challenge = (Button) findViewById(R.id.main_btn_create_chellange);
        fab_filter = (FloatingActionButton) findViewById(R.id.main_fab_filter);

        list_challenges = (ListView) findViewById(R.id.search_result_challenge_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.search_challenge_swipe_refresh_layout);

        txt_empty = (TextView) findViewById(R.id.emptyElement);

        // Hashmap for ListView
        Challenge_list = new ArrayList<HashMap<String, String>>();

        swipeRefreshLayout.setOnRefreshListener(this);

        //get session details
        session = new SessionManager(getApplicationContext());

        session.checkLogin();

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

            System.out.println("### JSON RESPONSE : " + str_session_data);
            System.out.println("### EMAIL : " + str_email);
            System.out.println("### NAME : " + str_name);
            System.out.println("### PICTURE : " + str_picture);

        } catch(Exception e){
            e.printStackTrace();
        }

        //get win event details
        // update current user for profile
        try{

            list_lose = new ArrayList<>();
            list_win = new ArrayList<>();
            list_payment = new ArrayList<>();

            list_lose.clear();
            list_win.clear();
            list_payment.clear();

            dialog_win_lose_payment = new SpotsDialog(MainActivity.this);
            dialog_win_lose_payment.setCancelable(false);
            dialog_win_lose_payment.show();
            queue = Volley.newRequestQueue(this);
            Function_Challenge_Win_Details();


        }catch (Exception e){

        }

        //action
        btn_create_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // setup for new implicit intent
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(Fragment_Create_Challenge.TAG_CHALLENGE_CALLING_TYPE, Fragment_Create_Challenge.TAG_CHALLENGE_NORMAL_CHALLENGE );

                editor.commit();

                Intent i = new Intent(getApplicationContext(), Activity_Challenge.class);
                startActivity(i);
                finish();

            }
        });

        fab_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showActionSheet(v);

            }
        });

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        try {

                                            queue = Volley.newRequestQueue(MainActivity.this);
                                            Function_Get_Challenge_List();

                                        } catch (Exception e) {
                                            // TODO: handle exception
                                        }
                                    }
                                }
        );

        list_challenges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str_challenge_no =Challenge_list.get(position).get(TAG_CHALLENGE_ID);
                String str_challenger_name =Challenge_list.get(position).get(TAG_CHALLENGE_USERNAME);
                String str_challenger_photo =Challenge_list.get(position).get(TAG_CHALLENGE_PHOTO);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("str_calling_activity", "MainActivity");
                editor.putString("str_challenge_no", str_challenge_no);
                editor.putString("str_challenger_name", str_challenger_name);
                editor.putString("str_challenger_photo", str_challenger_photo);
                editor.commit();

                Intent i = new Intent(getApplicationContext(), Activity_Challenge_Create.class);
                startActivity(i);

            }
        });


    }

    private void showActionSheet(View anchor) {

        ActionSheet actionSheet = new ActionSheet(getApplicationContext());
        actionSheet.setTitle("Filter by");
        actionSheet.setSourceView(anchor);
        actionSheet.addAction("Recently Listed", ActionSheet.Style.DEFAULT, new OnActionListener() {
            @Override
            public void onSelected(ActionSheet actionSheet, String title) {
                performAction(title);
                actionSheet.dismiss();

                onRefresh();

            }
        });
        actionSheet.addAction("Price(low to high)", ActionSheet.Style.DEFAULT, new OnActionListener() {
            @Override
            public void onSelected(ActionSheet actionSheet, String title) {
                performAction(title);
                actionSheet.dismiss();

                //action
                Challenge_list.clear(); //empty data

                str_selectd_filter_type = "lth";

                dialog_filter= new SpotsDialog(MainActivity.this);
                dialog_filter.setCancelable(false);
                dialog_filter.show();

                queue = Volley.newRequestQueue(MainActivity.this);
                Function_Filter_Challenge();

            }
        });

        actionSheet.addAction("Price(high to low)", ActionSheet.Style.DEFAULT, new OnActionListener() {
            @Override
            public void onSelected(ActionSheet actionSheet, String title) {
                performAction(title);
                actionSheet.dismiss();

                //action
                Challenge_list.clear(); //empty data

                str_selectd_filter_type = "htl";

                dialog_filter= new SpotsDialog(MainActivity.this);
                dialog_filter.setCancelable(false);
                dialog_filter.show();

                queue = Volley.newRequestQueue(MainActivity.this);
                Function_Filter_Challenge();

            }
        });

        actionSheet.show();
    }


    private void performAction(String title) {

        if (title.equals("Recently Listed")) {
            str_sort_by = "1";
        } else if (title.equals("Price(low to high)")) {
            str_sort_by = "2";
        } else if (title.equals("Price(high to low)")) {
            str_sort_by = "3";
        } else if (title.equals("Cricket")) {
            str_sort_by = "4";
        } else if (title.equals("Stock Market")) {
            str_sort_by = "5";
        } else {
            str_sort_by = "";
        }

        Toast.makeText(getApplicationContext(), "Filter By " + title, Toast.LENGTH_LONG).show();

    }


    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        try {
            Challenge_list.clear();
            queue = Volley.newRequestQueue(MainActivity.this);
            swipeRefreshLayout.setRefreshing(true);
            Function_Get_Challenge_List();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item1 = menu.findItem(R.id.action_notification);
        MenuItemCompat.setActionView(item1, R.layout.notification_update_count_layout);
        notification_Count = (RelativeLayout) MenuItemCompat.getActionView(item1);
        notification_batch = (RelativeLayout) MenuItemCompat.getActionView(item1);
        tv_notification = (TextView) notification_Count.findViewById(R.id.badge_notification_2);
        btn_bell = (Button) notification_Count.findViewById(R.id.button2);


        btn_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Notifications.class);
                startActivity(i);
            }
        });
        tv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Notifications.class);
                startActivity(i);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_user_profile) {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_MY_PROFILE);

            editor.commit();

            Intent i = new Intent(getApplicationContext(), Activity_Profile.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }


    /***************************
     * GET Active Challenges
     ***************************/

    public void Function_Get_Challenge_List() {

        System.out.println("### AppConfig.url_list_challenges "+AppConfig.url_list_challenges);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_challenges, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d(TAG, "### "+response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String challenge_id = obj1.getString(TAG_CHALLENGE_ID);
                            String created_username = obj1.getString(TAG_CHALLENGE_USERNAME);
                            String created_userphoto = obj1.getString(TAG_CHALLENGE_PHOTO);
                            String event_title = obj1.getString(TAG_CHALLENGE_EVENT_TITLE);
                            String type = obj1.getString(TAG_CHALLENGE_TYPE);
                            String date = obj1.getString(TAG_CHALLENGE_DATE);
                            String amount = obj1.getString(TAG_CHALLENGE_AMOUNT);
                            String challenge_name = obj1.getString(TAG_CHALLENGE_CHALLENGE_NAME);
                            String user_id = obj1.getString(TAG_CHALLENGE_USER_ID);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_CHALLENGE_ID, challenge_id);
                            map.put(TAG_CHALLENGE_USERNAME, created_username);
                            map.put(TAG_CHALLENGE_PHOTO, created_userphoto);
                            map.put(TAG_CHALLENGE_TITLE, event_title);
                            map.put(TAG_CHALLENGE_TYPE, type);
                            map.put(TAG_CHALLENGE_DATE, date);
                            map.put(TAG_CHALLENGE_AMOUNT, amount);
                            map.put(TAG_CHALLENGE_CHALLENGE_NAME, challenge_name);
                            map.put(TAG_CHALLENGE_USER_ID, user_id);

                            Challenge_list.add(map);

                            txt_empty.setVisibility(View.GONE);

                        }

                        System.out.println("### HASHMAP ARRAY" + Challenge_list);


                        swipeRefreshLayout.setRefreshing(false);

                    } else if (success == 0) {

                        swipeRefreshLayout.setRefreshing(false);

                    }

                    //                    set list in adapter even have data or no data
                    adapter = new List_Challenge_Adapter(MainActivity.this,
                            Challenge_list);
                    list_challenges.setAdapter(adapter);

                    if (Challenge_list.size() == 0){
                        swipeRefreshLayout.setVisibility(View.GONE);
                        txt_empty.setVisibility(View.VISIBLE);
                    }else if (Challenge_list.size()> 0){
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        txt_empty.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage("Internal Error")
                        .setIcon(R.mipmap.app_icon)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,

                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        startActivity(intent);

                                    }

                                }).show();
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /***************************
     * GET Filter Challenge
     ***************************/

    public void Function_Filter_Challenge() {

        String tag_json_obj = "json_obj_req";
        System.out.println("### AppConfig.url_filter_challenges "+AppConfig.url_filter_challenges);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_filter_challenges, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    System.out.println("### onResponse ");
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String challenge_id = obj1.getString(TAG_CHALLENGE_ID);
                            String created_username = obj1.getString(TAG_CHALLENGE_USERNAME);
                            String created_userphoto = obj1.getString(TAG_CHALLENGE_PHOTO);
                            String event_title = obj1.getString(TAG_CHALLENGE_EVENT_TITLE);
                            String type = obj1.getString(TAG_CHALLENGE_TYPE);
                            String date = obj1.getString(TAG_CHALLENGE_DATE);
                            String amount = obj1.getString(TAG_CHALLENGE_AMOUNT);
                            String challenge_name = obj1.getString(TAG_CHALLENGE_CHALLENGE_NAME);
                            String user_id = obj1.getString(TAG_CHALLENGE_USER_ID);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_CHALLENGE_ID, challenge_id);
                            map.put(TAG_CHALLENGE_USERNAME, created_username);
                            map.put(TAG_CHALLENGE_PHOTO, created_userphoto);
                            map.put(TAG_CHALLENGE_TITLE, event_title);
                            map.put(TAG_CHALLENGE_TYPE, type);
                            map.put(TAG_CHALLENGE_DATE, date);
                            map.put(TAG_CHALLENGE_AMOUNT, amount);
                            map.put(TAG_CHALLENGE_CHALLENGE_NAME, challenge_name);
                            map.put(TAG_CHALLENGE_USER_ID, user_id);

                            Challenge_list.add(map);

                            txt_empty.setVisibility(View.GONE);

                        }

                        System.out.println("### HASHMAP ARRAY" + Challenge_list);



                    } else if (success == 0) {

                        txt_empty.setVisibility(View.VISIBLE);
//                        TastyToast.makeText(getApplicationContext(), "No Challenges Found", TastyToast.LENGTH_SHORT, TastyToast.WARNING);


                    }

                    //                    set list in adapter even have data or no data
                    adapter = new List_Challenge_Adapter(MainActivity.this,
                            Challenge_list);
                    list_challenges.setAdapter(adapter);

                    if (Challenge_list.size() == 0){
                        swipeRefreshLayout.setVisibility(View.GONE);
                        txt_empty.setVisibility(View.VISIBLE);
                    }else if (Challenge_list.size()> 0){
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        txt_empty.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                dialog_filter.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("### onErrorResponse ");
                dialog_filter.dismiss();
                txt_empty.setVisibility(View.VISIBLE);
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("type", str_selectd_filter_type );

                System.out.println("### type " + str_selectd_filter_type);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /***************************
     * GET Filter Challenge
     ***************************/

    public void Function_Challenge_Lose_Details() {

        System.out.println("### AppConfig.url_payment_pending_details "+AppConfig.url_payment_pending_details);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_payment_pending_details, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    System.out.println("### onResponse ");
                    Log.d(TAG, "### "+response);
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String betting_id = obj1.getString(TAG_LOSE_BETTING_ID);
//                            String event_title = "event details (Static)";
                            String event_title = obj1.getString(TAG_LOSE_EVENT_TITLE);
                            String amount_paidto = obj1.getString(TAG_LOSE_PLAYER_NAME);
                            String amount_paidto_photo = obj1.getString(TAG_LOSE_PLAYER_IMAGE);
                            String account_details = obj1.getString(TAG_LOSE_ACCOUNT_DETAILS);
                            String amount = obj1.getString(TAG_LOSE_AMOUNT);
                            String challenge_amount = obj1.getString(TAG_LOSE_CHALLENGE_AMOUNT);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_LOSE_BETTING_ID, betting_id);
                            map.put(TAG_LOSE_EVENT_TITLE, event_title);
                            map.put(TAG_LOSE_PLAYER_NAME, amount_paidto);
                            map.put(TAG_LOSE_PLAYER_IMAGE, amount_paidto_photo);
                            map.put(TAG_LOSE_ACCOUNT_DETAILS, account_details);
                            map.put(TAG_LOSE_AMOUNT, amount);
                            map.put(TAG_LOSE_CHALLENGE_AMOUNT, challenge_amount);

                            list_lose.add(map);

                        }

                        System.out.println("### HASHMAP ARRAY" + list_lose);

                      

                    } else if (success == 0) {

                      

                    }

//                    get payment details
                    try{
                        
                        queue = Volley.newRequestQueue(MainActivity.this);
                        Function_Challenge_Payment_Received();

                    }catch (Exception e){

                    }


                    

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("### onErrorResponse ");
                dialog_win_lose_payment.dismiss();
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id );

                System.out.println("### user_id " + str_session_id);


                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /***************************
     * GET Filter Challenge
     ***************************/

    public void Function_Challenge_Payment_Received() {

        System.out.println("### AppConfig.url_payment_received "+AppConfig.url_payment_received);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_payment_received, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    System.out.println("### onResponse ");
                    Log.d(TAG, "### "+response);

                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String betting_id = obj1.getString(TAG_PAYMENT_BETTING_ID);
                            String event_title = obj1.getString(TAG_PAYMENT_EVENT_TITLE);
                            String amount_paidto = obj1.getString(TAG_PAYMENT_PAID_TO);
                            String amount_paidto_photo = obj1.getString(TAG_PAYMENT_PAID_TO_PHOTO);
                            String account_details = obj1.getString(TAG_PAYMENT_ACCOUNT_DETAILS);
                            String amount = obj1.getString(TAG_PAYMENT_AMOUNT);
                            String winning_amount = obj1.getString(TAG_PAYMENT_WINNIG_AMOUNT);
                            String received_from = obj1.getString(TAG_PAYMENT_RECEIVED_FROM);
                            String received_from_photo = obj1.getString(TAG_PAYMENT_RECEIVED_FROM_PHOTO);
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_PAYMENT_BETTING_ID, betting_id);
//                            map.put(TAG_PAYMENT_EVENT_TITLE, event_title);
                            map.put(TAG_PAYMENT_PAID_TO, amount_paidto);
                            map.put(TAG_PAYMENT_PAID_TO_PHOTO, amount_paidto_photo);
                            map.put(TAG_PAYMENT_ACCOUNT_DETAILS, account_details);
                            map.put(TAG_PAYMENT_AMOUNT, amount);
                            map.put(TAG_PAYMENT_WINNIG_AMOUNT, winning_amount);
                            map.put(TAG_PAYMENT_RECEIVED_FROM, received_from);
                            map.put(TAG_PAYMENT_RECEIVED_FROM_PHOTO, received_from_photo);

                            list_payment.add(map);

                        }

                        System.out.println("### HASHMAP ARRAY" + list_payment);
                        

                    } else if (success == 0) {

                        

                    }

                    //set user have to pay amount
                    if (list_lose.size() > 0 || list_payment.size() > 0)
                        session.set_need_to_pay(true);
                    else
                        session.set_need_to_pay(false);


//                    get notification count
                    try{

                        queue = Volley.newRequestQueue(MainActivity.this);
                        Function_Get_Notification();

                    }catch (Exception e){

                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

               
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("### onErrorResponse ");
                dialog_win_lose_payment.dismiss();
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id );

                System.out.println("### user_id " + str_session_id);


                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


    /***************************
     * GET ADMIN COMMITIONS DETAILS
     ***************************/

    public void Function_Challenge_Win_Details() {

        System.out.println("### AppConfig.url_challenge_win_details "+AppConfig.url_challenge_win_details);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_challenge_win_details, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d(TAG, "### "+response);
                Log.d(TAG, response.toString());
                try {
                    System.out.println("### onResponse ");
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String popup_id = obj1.getString(TAG_WIN_POPUP_ID);
                            String opponent_name = obj1.getString(TAG_WIN_OPPONENT_NAME);
                            String opponent_photo = obj1.getString(TAG_WIN_OPPONENT_PHOTO);
                            String event_title = obj1.getString(TAG_WIN_EVENT_TITLE);
                            String amount = obj1.getString(TAG_WIN_AMOUNT);
                            String betting_amount = obj1.getString(TAG_WIN_BETTING_AMOUNT);


                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_WIN_POPUP_ID, popup_id);
                            map.put(TAG_WIN_OPPONENT_NAME, opponent_name);
                            map.put(TAG_WIN_OPPONENT_PHOTO, opponent_photo);
                            map.put(TAG_WIN_EVENT_TITLE, event_title);
                            map.put(TAG_WIN_AMOUNT, amount);
                            map.put(TAG_WIN_BETTING_AMOUNT, betting_amount);

                            list_win.add(map);

                        }

                        System.out.println("### HASHMAP ARRAY" + list_win);


                    } else if (success == 0) {


                    }

//get lose details
                     try{
                        
                        queue = Volley.newRequestQueue(MainActivity.this);
                        Function_Challenge_Lose_Details();

                    }catch (Exception e){

                    }




                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("### onErrorResponse ");
                dialog_win_lose_payment.dismiss();
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id );

                System.out.println("### user_id " + str_session_id);


                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
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
                        arr.length();

//                        set notification count
                        tv_notification.setVisibility(View.VISIBLE);
                        tv_notification.setText(""+arr.length());

                    } else if (success == 0) {

                        tv_notification.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

//                update current user for profile
                try{
                    queue = Volley.newRequestQueue(MainActivity.this);
                    Function_Get_Profile();
                }catch (Exception e){

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog_win_lose_payment.dismiss();
                System.out.println("### onErrorResponse ") ;

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


    public void Alert_Show_Win_Lose(){

        System.out.println("### Alert_Show_Win_Lose");
        //show lose challenge in alert dialog
        //get account details
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View view = li.inflate(R.layout.dialog_payment_list, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(view);
        
        /*
        * find view by id
        * */
        ListView list_view_win = (ListView) view.findViewById(R.id.list_view_win);
        TextView txt_win_message = (TextView) view.findViewById(R.id.txt_win_message);
        ListView list_view_lose = (ListView) view.findViewById(R.id.list_view_lose);
        TextView txt_lose_message = (TextView) view.findViewById(R.id.txt_lose_message);
        ListView list_view_payment = (ListView) view.findViewById(R.id.list_view_payment);
        TextView txt_payment_message = (TextView) view.findViewById(R.id.txt_payment_message);

        //set payment details
        win_adapter = new List_Win_Adapter(MainActivity.this,
                list_win, str_session_id);
        list_view_win.setAdapter(win_adapter);


        lose_adapter = new List_Lose_Adapter(MainActivity.this,
                list_lose, str_session_id);
        list_view_lose.setAdapter(lose_adapter);

        payment_adapter = new List_Payment_Completed_Adapter(MainActivity.this,
                list_payment, str_session_id);
        list_view_payment.setAdapter(payment_adapter);

// action
//        show no data message
        if (list_win.size() == 0 ){
            txt_win_message.setVisibility(View.VISIBLE);
            list_view_win.setVisibility(View.GONE);
        }
        if (list_lose.size() == 0 ){
            txt_lose_message.setVisibility(View.VISIBLE);
            list_view_lose.setVisibility(View.GONE);
        }

        if (list_payment.size() == 0 ){
            txt_payment_message.setVisibility(View.VISIBLE);
            list_view_payment.setVisibility(View.GONE);
        }

        // SET DIALOG MESSAGE
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {



                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setIcon(R.mipmap.app_icon);
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setCancelable(false);
        if (list_lose.size() > 0 || list_win.size() > 0 || list_payment.size() > 0){
            alertDialog.show();

        }else if (list_lose.size() > 0  || list_payment.size() > 0){
            session.set_need_to_pay(false); // no need to pay any pament
            System.out.println("### is need to make payment : "+session.is_need_to_pay());
        }

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
                                .getDefaultSharedPreferences(MainActivity.this);
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

                        dialog_win_lose_payment.dismiss();
                    } else if (success == 0) {

                        dialog_win_lose_payment.dismiss();
                    }

                    dialog_win_lose_payment.dismiss();
//                    show win lose pay alert dialog
                    Alert_Show_Win_Lose();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog_win_lose_payment.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog_win_lose_payment.dismiss();
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

        if (back_pressed + 2000 > System.currentTimeMillis()) {

            finishAffinity();

        } else {

            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();

        }
        back_pressed = System.currentTimeMillis();

    }

}
