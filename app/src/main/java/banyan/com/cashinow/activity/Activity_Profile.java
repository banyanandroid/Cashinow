package banyan.com.cashinow.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.adapter.List_Active_Challenge_Adapter;
import banyan.com.cashinow.adapter.List_Complete_Challenge_Adapter;
import banyan.com.cashinow.adapter.List_Fb_Friends_Adapter;
import banyan.com.cashinow.fragment.Fragment_Active_Challenge;
import banyan.com.cashinow.fragment.Fragment_Completed_Challenge;
import banyan.com.cashinow.fragment.Fragment_Create_Challenge;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class Activity_Profile extends AppCompatActivity {

    private static final String TAG = Activity_Profile.class.getSimpleName();

    private Toolbar mToolbar;
    private TextView txt_fb_link, txt_user_name, txt_challenges, txt_won, txt_lost;
    private CircleImageView profile_picture;
    private Button btn_account_details, btn_friends_list;

    private ImageView img_share;

    JSONObject response, profile_pic_data, profile_pic_url;
    String str_email, str_name, str_picture;

    // Session Manager Class
    SessionManager session;
    String str_session_data, str_session_id;

    SpotsDialog dialog;
    public static RequestQueue queue;

    public static final String TAG_CHALLENGE_ID = "challenge_id";
    public static final String TAG_CHALLENGE_TITLE = "event_title";
    public static final String TAG_EVENT_NO = "event_no";
    public static final String TAG_CHALLENGE_TYPE = "type";
    public static final String TAG_CHALLENGE_EVENT = "event";
    public static final String TAG_CHALLENGE_CHALLENGE_NAME = "challenge_name";
    public static final String TAG_CHALLENGE_DATE = "date";
    public static final String TAG_CHALLENGE_AMOUNT = "amount";
    public static final String TAG_CHALLENGER_USER_ID = "challenger_userid";
    public static final String TAG_CHALLENGER_NAME = "challenger_name";
    public static final String TAG_CHALLENGE_PHOTO = "challenger_photo";
    public static final String TAG_OPPONENT_USER_ID = "opponent_userid";
    public static final String TAG_OPPONENT_NAME = "opponent_name";
    public static final String TAG_OPPONENT_PHOTO = "opponent_photo";
    public static final String TAG_STATUS = "status";
    public static final String TAG_AMOUNT_WON = "amount_won";
    public static final String TAG_AMOUNT_LOST = "amount_lost";
    public static final String TAG_ACCURACY = "accuracy";
    public static final String TAG_PAYMENT_STATUS = "payment";

    static ArrayList<HashMap<String, String>> active_challenge_list;
    static ArrayList<HashMap<String, String>> complete_challenge_list;

    HashMap<String, String> params = new HashMap<String, String>();

    public List_Active_Challenge_Adapter adapter;
    public List_Complete_Challenge_Adapter adapter_completed;

    //account details
    EditText edt_account_details;
    String str_account_details ="";
    SpotsDialog dialog_account_details, dialog_update_account_details;

    // challenges
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int int_items = 2;

    // calling acitivity
    public static final String TAG_PROFILE_CALLING_TYPE = "calling_type";
    public static final String TAG_PROFILE_MY_PROFILE = "my_profile";
    public static final String TAG_PROFILE_OTHER_PROFILE = "other_profile";
    public static final String TAG_PROFILE_USER_ID = "user_id";

    public static final String TAG_PROFILE_CURRENT_PROFILE_ID = "current_profile_id";

    String str_calling_type = "";

    //other profile
    Button btn_direct_challenge;

    public static final String TAG_PROFILE_CALLING_ACTIVITY_PROFILE = "Activity_Profile";

//    profile
    public static final String TAG_PROFILE_CURRENT_PROFILE_USER_ID = "Current_Profile_User_Id";
    public static final String TAG_PROFILE_CURRENT_PROFILE_USER_NAME = "Current_Profile_User_Name";
    public static final String TAG_PROFILE_CURRENT_PROFILE_USER_PHOTO = "Current_Profile_User_Photo";

    public static final String TAG_FB_USER_ID = "user_id";
    public static final String TAG_FB_NAME = "name";
    public static final String TAG_FB_PHOTO = "photo";

    TextView txt_view_image_uploaded;
    Button btn_add_enquiry_image;
    String image_type, str_selected_image;
    SpotsDialog dialog_upload_image;
    private ArrayList<Image> images = new ArrayList<>();
    String encodedstring = "", listString;
    ArrayList<String> Arraylist_image_encode , Arraylist_dummy ;

    private final String TAG_USER_PROFILE_USER_ID = "user_id";
    private final String TAG_USER_PROFILE_IMAGE = "image";


//    update profile
    ArrayList<String> array_list_app_user_id = new ArrayList<>();
    ArrayList<String> array_list_app_user_name = new ArrayList<>();
    ArrayList<String> array_list_app_user_image = new ArrayList<>();
    SearchableSpinner srch_spi_app_users_list;

//    app user
    TextView txt_search;
    ListView list_view_player;
    SpotsDialog dialog_user;
    public final String TAG_APP_USER_USER_ID = "user_id";
    public final String TAG_APP_USER_USER_NAME = "user_name";
    public final String TAG_APP_USER_USER_EMAIL = "user_email";
    public final String TAG_APP_USER_USER_FACEBOOK_ID = "user_facebook_id";
    public final String TAG_APP_USER_USER_PHOTO = "user_photo";
    List_Fb_Friends_Adapter  list_fb_friends_adapter;


// need to pay

    static ArrayList<HashMap<String, String>> list_win, list_lose, list_payment;
    SpotsDialog dialog_win_lose_payment;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        /*
        * find view by id
        * */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
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

        txt_fb_link = (TextView) findViewById(R.id.txt_fb_link);
        txt_user_name = (TextView) findViewById(R.id.profile_txt_name);
        txt_challenges = (TextView) findViewById(R.id.profile_txt_challenges);
        txt_won = (TextView) findViewById(R.id.profile_txt_won);
        txt_lost = (TextView) findViewById(R.id.profile_txt_lost);

        profile_picture = (CircleImageView) findViewById(R.id.profile_img_profilePic);

        img_share = (ImageView) findViewById(R.id.profile_img_share);

        btn_direct_challenge = (Button) findViewById(R.id.btn_direct_challenge);
        btn_account_details = (Button) findViewById(R.id.btn_account_details);
        btn_friends_list = (Button) findViewById(R.id.btn_friends_list);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);


        /*
        * get calling activity details
        * */
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Activity_Profile.this);
        str_calling_type = sharedPreferences.getString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_CALLING_TYPE);

//       get sesssion details
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        session.check_need_to_pay();

        //get check user have to pay amount or not
//        by check count of lose and pay popup counts
//      get account details
//        get profile details
//        then load active challenge , completed challenge
        try{

            list_lose = new ArrayList<>();
            list_win = new ArrayList<>();
            list_payment = new ArrayList<>();

            dialog_win_lose_payment = new SpotsDialog(Activity_Profile.this);
            dialog_win_lose_payment.setCancelable(false);
            dialog_win_lose_payment.show();
            queue = Volley.newRequestQueue(this);
            Function_Challenge_Win_Details();

        }catch (Exception e){

        }


        /**************
         *  action
         * *************/
        // if calling activity is my profile
        if (str_calling_type.equals(TAG_PROFILE_MY_PROFILE)){

            //hide and show
            btn_direct_challenge.setVisibility(View.GONE);
            btn_friends_list.setVisibility(View.VISIBLE);
            btn_account_details.setVisibility(View.VISIBLE);

            //get user details

            HashMap<String, String> user = session.getUserDetails();
            str_session_data = user.get(SessionManager.KEY_USER);
            str_session_id = user.get(SessionManager.KEY_ID);

            str_name = sharedPreferences.getString(Activity_Login.TAG_LOGIN_USER_NAME ,"");
            str_picture = sharedPreferences.getString(Activity_Login.TAG_LOGIN_USER_IMAGE ,"");

            try {
                System.out.println("### JSON RESPONSE : " + str_session_data);
                System.out.println("### EMAIL : " + str_email);
                System.out.println("### NAME : " + str_name);
                System.out.println("### PICTURE : " + str_picture);

                txt_user_name.setText("" + str_name);

                Picasso.with(this)
                        .load(str_picture)
                        .placeholder(R.mipmap.ic_challenge)
                        .into(profile_picture);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else { // if calling activity is other profile

            System.out.println("### other profile");
            str_session_id = sharedPreferences.getString(Activity_Profile.TAG_PROFILE_USER_ID, Activity_Profile.TAG_PROFILE_USER_ID);
            //hide and show
            btn_direct_challenge.setVisibility(View.VISIBLE);
            btn_friends_list.setVisibility(View.GONE);
            btn_account_details.setVisibility(View.GONE);

            System.out.println("### str_session_id "+str_session_id);

        }

        // Hashmap for ListView
        active_challenge_list = new ArrayList<HashMap<String, String>>();
        complete_challenge_list = new ArrayList<HashMap<String, String>>();

        txt_fb_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Activity_Profile.this, Activity_FB_Profile_Webview.class);
                startActivity(intent);
                finish();
            }
        });

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = "Download & Join on Cashinow";
                String url = "https://play.google.com/store/apps";
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share..."));

            }
        });

        btn_account_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show and update account details
                //get account details
                LayoutInflater li = LayoutInflater.from(Activity_Profile.this);
                View view = li.inflate(R.layout.dialog_account_details, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Profile.this);
                alertDialogBuilder.setView(view);

                edt_account_details = (EditText) view.findViewById(R.id.edt_account_details);


                //get account details
                edt_account_details.setText(str_account_details);

                // SET DIALOG MESSAGE
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Update",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        // get user input and set it to result
                                        str_account_details = edt_account_details.getText().toString();

                                        if (str_account_details == "") {

                                            TastyToast.makeText(getApplicationContext(), "Enter Account Details", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                            edt_account_details.setError("Account Details Should Not Be Empty");

                                        } else {

                                            str_account_details = edt_account_details.getText().toString();

                                            dialog_update_account_details = new SpotsDialog(Activity_Profile.this);
                                            dialog_update_account_details.setCancelable(false);
                                            dialog_update_account_details.show();
                                            queue = Volley.newRequestQueue(Activity_Profile.this);
                                            Function_Update_Account_Details();

                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setTitle("Account Details");
                alertDialog.show();

            }
        });

        //get fb friends list
        btn_friends_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Alert_Show_FB_Friends_List();

            }
        });


        btn_direct_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences.Editor editor = sharedPreferences.edit();

                SessionManager sessionManager = new SessionManager(Activity_Profile.this);
                HashMap<String, String> user = sessionManager.getUserDetails();

                String login_user_id = SessionManager.KEY_ID;
                String opponent_user_id = str_session_id;

                editor.putString(Fragment_Create_Challenge.TAG_CHALLENGE_CALLING_TYPE, Fragment_Create_Challenge.TAG_CHALLENGE_DIRECT_CHALLENGE );
                editor.putString(Fragment_Create_Challenge.TAG_CHALLENGE_CHALLENGE_USER_ID, user.get(login_user_id));
                editor.putString(Fragment_Create_Challenge.TAG_CHALLENGE_OPPONENT_USER_ID, opponent_user_id);

                editor.commit();

                Intent intent = new Intent(Activity_Profile.this, Activity_Challenge.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);

//        show edit profile image only for login user
        if (str_calling_type.equals(TAG_PROFILE_MY_PROFILE)){

            MenuItem item = menu.findItem(R.id.action_edit_profile_image);
            item.setVisible(true);//

        }else{

            MenuItem item = menu.findItem(R.id.action_edit_profile_image);
            item.setVisible(false);//

        }

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit_profile_image) {

            // show alert to update profile image
            Arraylist_image_encode = new ArrayList<>();
            Alert_Update_Profile();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();

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

                        try {
                            txt_challenges.setText("" + participated);
                            txt_won.setText("" + won);
                            txt_lost.setText("" + lost);

                            txt_user_name.setText("" + user_name);

                            Picasso.with(Activity_Profile.this)
                                    .load(str_picture)
                                    .placeholder(R.mipmap.ic_challenge)
                                    .into(profile_picture);



                        } catch (Exception e) {

                        }

                        //set current profile user id
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Activity_Profile.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(TAG_PROFILE_CURRENT_PROFILE_USER_ID, str_session_id);
                        editor.putString(TAG_PROFILE_CURRENT_PROFILE_USER_NAME, user_name);
                        editor.putString(TAG_PROFILE_CURRENT_PROFILE_USER_PHOTO, user_image);

                        editor.putString(Activity_Login.TAG_LOGIN_USER_NAME, user_name);
                        editor.putString(Activity_Login.TAG_LOGIN_USER_IMAGE, user_image);


                        System.out.println("### "+Activity_Login.TAG_LOGIN_USER_NAME+" "+user_name);
                        System.out.println("### "+Activity_Login.TAG_LOGIN_USER_IMAGE+" "+user_image);

                        System.out.println("### "+TAG_PROFILE_CURRENT_PROFILE_USER_ID+" "+str_session_id);
                        System.out.println("### "+TAG_PROFILE_CURRENT_PROFILE_USER_NAME+" "+user_name);
                        System.out.println("### "+TAG_PROFILE_CURRENT_PROFILE_USER_PHOTO+" "+user_image);

                        editor.commit();

//                        update profile image
                        Picasso.with(Activity_Profile.this)
                                .load(user_image)
                                .placeholder(R.mipmap.ic_challenge)
                                .into(profile_picture);

//                        set view page only after current user details updated
                        viewPager.setAdapter(new Activity_Profile.MyAdapter(getSupportFragmentManager()));
                        tabLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                tabLayout.setupWithViewPager(viewPager);
                            }
                        });

                        dialog_win_lose_payment.dismiss();

                    } else if (success == 0) {

                        dialog_win_lose_payment.dismiss();
                        TastyToast.makeText(getApplicationContext(), "No Data Found !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }

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
                new AlertDialog.Builder(Activity_Profile.this)
                        .setTitle(R.string.app_name)
                        .setMessage("Internal Error")
                        .setIcon(R.mipmap.app_icon)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,

                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(Activity_Profile.this, MainActivity.class);
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

                System.out.println("### USER ID : " + str_session_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }





    /********************************
     *FUNCTION ACCOUNT DETAILS
     *********************************/
    private void Function_Get_Account_Details() {

        System.out.println("###  AppConfig.url_account_details " + AppConfig.url_get_account_details);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_get_account_details, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {


                        str_account_details =  obj.getString("data");


                    } else {


                    }

                    /*******************
                     *    get details
                     * *****************/
                    //get profile details
//        update current user
//        update login user name and image
//        set view page only after current user details updated
                    try {

                        queue = Volley.newRequestQueue(Activity_Profile.this);
                        Function_Get_Profile();
                    } catch (Exception e) {

                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog_win_lose_payment.dismiss();

                new AlertDialog.Builder(Activity_Profile.this)
                        .setTitle(R.string.app_name)
                        .setMessage("Internal Error")
                        .setIcon(R.mipmap.app_icon)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,

                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(Activity_Profile.this, MainActivity.class);
                                        startActivity(intent);

                                    }

                                }).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id);

                System.out.println("### user_id  " + str_session_id);

                return checkParams(params);
            }

            private Map<String, String> checkParams(Map<String, String> map) {
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
                    if (pairs.getValue() == null) {
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }
        };

        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        // Adding request to request queue
        queue.add(request);
    }


    /********************************
     *FUNCTION UPDATE ACCOUNT DETAILS
     *********************************/
    private void Function_Update_Account_Details() {

        System.out.println("###  AppConfig.url_account_details " + AppConfig.url_account_details);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_account_details, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        dialog_update_account_details.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Account Details Updated Successfully.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);


                    } else {

                        dialog_update_account_details.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Account Details Not Added, Try Again.", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog_update_account_details.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog_update_account_details.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id);
                params.put("account_details", str_account_details);

                System.out.println("### user_id  " + str_session_id);
                System.out.println("### account_details  " + str_account_details);

                return checkParams(params);
            }

            private Map<String, String> checkParams(Map<String, String> map) {
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
                    if (pairs.getValue() == null) {
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }
        };

        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        // Adding request to request queue
        queue.add(request);

    }


    /********************************
     * UPLOAD PROFILE IMAGE
     *********************************/
    private void Function_Upload_Profile_Image() {

        System.out.println("###  AppConfig.url_upload_profile_image " + AppConfig.url_upload_profile_image);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_upload_profile_image, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d(TAG, "### " +response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        dialog_upload_image.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Profile Image Updated Successfully.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);


                        try{

                            dialog_win_lose_payment = new SpotsDialog(Activity_Profile.this);
                            dialog_win_lose_payment.setCancelable(false);
                            dialog_win_lose_payment.show();
                            queue = Volley.newRequestQueue(Activity_Profile.this);
                            Function_Get_Profile();

                        }catch (Exception e){

                        }


                    } else {

                        dialog_upload_image.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Profile Image Not Updated, Try Again.", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                    }
                    dialog_upload_image.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog_upload_image.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("### onErrorResponse");
                dialog_upload_image.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(TAG_USER_PROFILE_USER_ID, str_session_id);
                params.put(TAG_USER_PROFILE_IMAGE, encodedstring);

                System.out.println("### "+TAG_USER_PROFILE_USER_ID +" " + str_session_id);
                System.out.println("### "+TAG_USER_PROFILE_IMAGE +" " + encodedstring);

                return checkParams(params);
            }

            private Map<String, String> checkParams(Map<String, String> map) {
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
                    if (pairs.getValue() == null) {
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }
        };

        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        // Adding request to request queue
        queue.add(request);
    }


    public void Alert_Update_Profile(){

        //get account details
        LayoutInflater li = LayoutInflater.from(Activity_Profile.this);
        View view = li.inflate(R.layout.dialog_update_profile, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Profile.this);
        alertDialogBuilder.setView(view);

        txt_view_image_uploaded = (TextView) view.findViewById(R.id.txt_view_image_uploaded);
        btn_add_enquiry_image = (Button) view.findViewById(R.id.btn_add_enquiry_image);

        //action
        // ADD IMAGE FOR ENQUIRY
        btn_add_enquiry_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image_type = "Location photos";
                Function_ImagePicker();

            }
        });

        // SET DIALOG MESSAGE
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                              if (str_selected_image == "") {

                                    TastyToast.makeText(getApplicationContext(), "Upload Profile Image", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                    edt_account_details.setError("Profile Image Should Not Be Empty");

                                }
                                else {

                                    //save account details
                                    try{

                                        queue = Volley.newRequestQueue(Activity_Profile.this);
                                        dialog_upload_image = new SpotsDialog(Activity_Profile.this);
                                        dialog_upload_image.setCancelable(false);
                                        dialog_upload_image.show();
                                        Function_Upload_Profile_Image();

                                    }catch (Exception e){

                                    }

                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setIcon(R.mipmap.app_icon);
        alertDialog.setTitle("Edit Profile Picture");
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    /*******************************
     *  PIC UPLOADER
     * ***************************/

// Recomended builder
    public void Function_ImagePicker () {

        ImagePicker.with(this)
                .setFolderMode(true) // set folder mode (false by default)
                .setFolderTitle("FOLDER") // folder selection title
                .setImageTitle("TAP TO SELECT") // image selection title
                .setMultipleMode(true) // multi mode (default mode)
                .setMaxSize(1)// max images can be selected (999 by default)
                .setKeepScreenOn(true)
                .start();

        com.nguyenhoanglam.imagepicker.model.Config config = new com.nguyenhoanglam.imagepicker.model.Config();

    }

    // GET IMAGE FROM IMAGE PICKER
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){


        if (requestCode == com.nguyenhoanglam.imagepicker.model.Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            images = data.getParcelableArrayListExtra(com.nguyenhoanglam.imagepicker.model.Config.EXTRA_IMAGES);

            StringBuilder sb = new StringBuilder();
            for (int i = 0, l = images.size(); i < l; i++) {

                String str_img_path = images.get(i).getPath();

                Bitmap bmBitmap = BitmapFactory.decodeFile(str_img_path);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bmBitmap.compress(Bitmap.CompressFormat.JPEG, 25, bao);
                byte[] ba = bao.toByteArray();
                encodedstring = Base64.encodeToString(ba, 0);
                Log.e("base64", "### -----" + encodedstring);

                Arraylist_image_encode.add(encodedstring);

                txt_view_image_uploaded.setText("Image Added Sucessfully.");
                btn_add_enquiry_image.setText("Change Image / File");
            }
            Function_Encode_Image1();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void Function_Encode_Image1 () {

        for (String s : Arraylist_image_encode) {
            listString += s + "IMAGE:";
        }
        str_selected_image = listString;
        System.out.print("###  Image Uploaded " + listString);
    }

    public void Function_Get_App_Users_List(){

        System.out.println("### AppConfig.url_app_users_list "+AppConfig.url_app_users_list);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_app_users_list, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray array_data =obj.getJSONArray("data");

                        for (int count =0; count < array_data.length(); count++){
                            JSONObject obj1;

                            obj1 = array_data.getJSONObject(count);

                            String str_user_id = obj1.getString(TAG_APP_USER_USER_ID);
                            String str_user_name = obj1.getString(TAG_APP_USER_USER_NAME);
                            String str_user_email = obj1.getString(TAG_APP_USER_USER_EMAIL);
                            String str_user_facebook_id = obj1.getString(TAG_APP_USER_USER_FACEBOOK_ID);
                            String str_user_photo = obj1.getString(TAG_APP_USER_USER_PHOTO);

//                            set user data in arry
                            array_list_app_user_id.add(str_user_id);
                            array_list_app_user_name.add(str_user_name);
                            array_list_app_user_image.add(str_user_photo);

                        }

                        list_fb_friends_adapter = new List_Fb_Friends_Adapter(Activity_Profile.this, array_list_app_user_image, array_list_app_user_name, array_list_app_user_id, str_session_id);
                        list_view_player.setAdapter(list_fb_friends_adapter);

                        dialog_user.dismiss();

                    } else if (success == 0) {

                        dialog_user.dismiss();
                        TastyToast.makeText(getApplicationContext(), "No Data Found !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                dialog_user.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog_user.dismiss();
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

    public void Alert_Show_FB_Friends_List(){

        //get account details
        LayoutInflater li = LayoutInflater.from(Activity_Profile.this);
        View view = li.inflate(R.layout.dialog_fb_friendship_list, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Profile.this);
        alertDialogBuilder.setView(view);
//find view by id
        txt_search = (TextView) view.findViewById(R.id.txt_search);
        list_view_player = (ListView) view.findViewById(R.id.list_view_player);

//        get app user list and set data in adapter
        try{

            array_list_app_user_id.clear();
            array_list_app_user_name.clear();
            array_list_app_user_image.clear();

            dialog_user = new SpotsDialog(Activity_Profile.this);
            dialog_user.setCancelable(false);
            dialog_user.show();
            Function_Get_App_Users_List();

        }catch (Exception e){
            System.out.println("### Exception "+e.getMessage());
        }

        //action

       /*
       * on click fb friend list row, go to selected person profile
       * */

        txt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                System.out.println("Text ["+s+"]");
                list_fb_friends_adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


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
        alertDialog.setTitle("Cashinow Players List");
        alertDialog.show();

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

                        queue = Volley.newRequestQueue(Activity_Profile.this);
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

                        queue = Volley.newRequestQueue(Activity_Profile.this);
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


                    //get account details
                    try {

                        queue = Volley.newRequestQueue(Activity_Profile.this);
                        Function_Get_Account_Details();
                    } catch (Exception e) {

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


    /****************************
*       Pager Adapter
* ***************************/
    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                // case 0 : return new Tab_MySchedulee();
                case 0 : return new Fragment_Active_Challenge();
                case 1 : return new Fragment_Completed_Challenge();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Active Challenges";
                case 1 :
                    return "Completed Challenges";
            }
            return null;
        }
    }

}
