package banyan.com.cashinow.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.app.App;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.NotificationUtils;
import banyan.com.cashinow.utils.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

/*
* function
* to accept the challenge from notification
* to show resulf of challenge
* to create challenge by accept or reject
* */
public class Activity_Challenge_Accept_Result_Challenge extends AppCompatActivity {

    private static final String TAG = Activity_Challenge_Accept_Result_Challenge.class.getSimpleName();

    private Toolbar mToolbar;
    private Button btn_challenge;
    private TextView txt_user_name, txt_event_name, txt_event_amount, txt_team_title1, txt_team_title2,
            text_bottom_title1, text_bottom_title2, textProgress1, textProgress2, txt_team_title1_pb, txt_team_title2_pb,
            text_bottom_title1_pb, text_bottom_title2_pb, textProgress1_pb, textProgress2_pb;
    private TextView txt_ins, txt_opponent_name, txt_my_name;
    private SeekBar seekBar_team1, seekBar_team2, seekBar_team1_pb, seekBar_team2_pb;
    private CircleImageView img_view_opponent_profile_pic, img_view_my_profile_pic, profile_picture;
    private CardView card_cricket, card_stock, card_instruction;
    private LinearLayout linear_opponent;
    private EditText edt_challenger_point, edt_opponent_points;

    TextView t1;

    JSONObject response, profile_pic_data, profile_pic_url;
    String str_email, str_name, str_picture;

    // Session Manager Class
    SessionManager session;
    String str_session_data, str_session_id;
    String str_notification_id, str_challenge_id, str_challenger_name = "", str_challenger_photo = "", str_calling_activity = "";

    String str_event_type = "";

    // Toolbar Notification batch
    RelativeLayout notification_Count, notification_batch, message_Count, message_batch;
    TextView tv_notification, tv_message;
    //    menu
    Button btn_bell;
    int i = 0;
    String value = "nothing";
    String search_key, search_id = "";

    SpotsDialog dialog;
    public static RequestQueue queue;

    public static final String TAG_CHALLENGE_ID = "challenge_id";
    public static final String TAG_CHALLENGE_EVENT_TITLE = "event_title";
    public static final String TAG_CHALLENGE_CHALLENGER_ID = "challenger_id";
    public static final String TAG_CHALLENGE_CHALLENGER_NAME = "challenger_name";
    public static final String TAG_CHALLENGE_CHALLENGER_PHOTO = "challenger_photo";

    public static final String TAG_CHALLENGE_OPPONENT_ID = "opponent_id";
    public static final String TAG_CHALLENGE_OPPONENT_NAME = "opponent_name";
    public static final String TAG_CHALLENGE_OPPONENT_PHOTO = "opponent_photo";
    public static final String TAG_CHALLENGE_DESCRIPTION = "description";


    public static final String TAG_CHALLENGE_CREATED_BY = "created_by";
    public static final String TAG_CHALLENGE_CREATED_BY_PHOTO = "created_by_photo";
    public static final String TAG_CHALLENGE_MATCH_TYPE = "match_type";
    public static final String TAG_CHALLENGE_TYPE = "type";
    public static final String TAG_CHALLENGE_EVENT = "event";
    public static final String TAG_CHALLENGE_CHALLENGE_NAME = "challenge_name";
    public static final String TAG_CHALLENGE_TEAMA = "teamA";
    public static final String TAG_CHALLENGE_TEAMB = "teamB";
    public static final String TAG_CHALLENGE_TEAMA_PERCENT = "challenger_teamA_percentage";
    public static final String TAG_CHALLENGE_TEAMB_PERCENT = "challenger_teamB_percentage";
    public static final String TAG_CHALLENGE_OP_TEAMA_PERCENT = "opponent_teamA_percentage";
    public static final String TAG_CHALLENGE_OP_TEAMB_PERCENT = "opponent_teamB_percentage";
    public static final String TAG_CHALLENGER_POINTS = "challenger_points";
    public static final String TAG_OPPONENT_POINTS = "opponent_points";
    public static final String TAG_CHALLENGE_CHALLENGER_AMOUNT = "challenger_amount";
    public static final String TAG_CHALLENGE_INS = "instructions";
    public static final String TAG_CHALLENGE_INS2 = "instructions2";
    public static final String TAG_CHALLENGE_INS3 = "instructions3";
    public static final String TAG_CHALLENGE_DATE = "date";
    public static final String TAG_CHALLENGE_WINNER = "Winner";
    public static final String TAG_CHALLENGE_ACCURACY = "Accuracy";
    public static final String TAG_CHALLENGE_AMOUNT = "Amount";
    public static final String TAG_CHALLENGE_RESULT = "Result";
    public static final String TAG_CHALLENGE_CHALLENGE_AMOUNT = "challenge_amount";

    String str_challenge_type, str_challenge_create_by = "";
    String str_op_team_a_percent = "", str_op_team_b_percent = "";
    String str_op_share_point = "";


    //cricket
    TextView txt_challenge_name_cricket;
    Button btn_my_challenge_team_1, btn_my_challenge_team_2, btn_opponent_challenge_team_1, btn_opponent_challenge_team_2;
    LinearLayout lo_my_profile;
    String challenger_teamA_percentage = "";

    //stock
    LinearLayout lo_stock_my_profile, lo_stock_opponent_profile;
    CircleImageView challenge_view_img_stock_my_profile_pic, challenge_view_img_stock_opponent_profile_pic;
    TextView challenge_view_txt_stock_my_name, challenge_view_txt_stock_opponent_name;

    TextView txt_challenge_name_stock;
    LinearLayout lo_opponent_profile;

    //common
    String created_by = "", created_by_photo = "", match_type = "";


    // notification
    TextView txt_challenge_result_winner, txt_challenge_result_accuracy, txt_challenge_result_win_amount, txt_challenge_result;
    Button btn_noti_deny_challenge, btn_noti_accept_challenge;
    SpotsDialog dialog_notification, dialog_clear_notification;
    CardView challenge_view_card_result;
    String str_type = "", str_title = "", str_message = "", str_user_name = "", str_user_photo = "", str_challenge_normal_or_direct = "";
    String str_selected_accept_status = "", str_bet_type = "";

    //profile
    String str_challenger_id = "", str_opponent_id = "", opponent_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_accept_result_challenge);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        /*****************
        * find view by id
        * ****************/
        
//        profile_picture = (CircleImageView) findViewById(R.id.challenge_view_img_profile_pic);
//        txt_user_name = (TextView) findViewById(R.id.challenge_view_txt_name);

        txt_event_name = (TextView) findViewById(R.id.challenge_view_txt_event_name);
        txt_event_amount = (TextView) findViewById(R.id.challenge_view_txt_amount);

        //profile image and name
        img_view_opponent_profile_pic = (CircleImageView) findViewById(R.id.challenge_view_img_opponent_profile_pic);
        txt_opponent_name = (TextView) findViewById(R.id.challenge_view_txt_opponent_name);
        img_view_my_profile_pic = (CircleImageView) findViewById(R.id.challenge_view_img_my_profile_pic);
        txt_my_name = (TextView) findViewById(R.id.challenge_view_txt_my_name);

        txt_team_title1 = (TextView) findViewById(R.id.challange_view_text_team_title1_pa);
        txt_team_title2 = (TextView) findViewById(R.id.challange_view_text_team_title2_pa);
        text_bottom_title1 = (TextView) findViewById(R.id.challange_view_text_team1_pa);
        text_bottom_title2 = (TextView) findViewById(R.id.challange_view_text_team2_pa);
        textProgress1 = (TextView) findViewById(R.id.challenge_view_text_progress1_pa);
        textProgress2 = (TextView) findViewById(R.id.challenge_view_text_progress2_pa);

        txt_team_title1_pb = (TextView) findViewById(R.id.challange_view_text_team_title1_pb);
        txt_team_title2_pb = (TextView) findViewById(R.id.challange_view_text_team_title2_pb);
        text_bottom_title1_pb = (TextView) findViewById(R.id.challange_view_text_team1_pb);
        text_bottom_title2_pb = (TextView) findViewById(R.id.challange_view_text_team2_pb);
        textProgress1_pb = (TextView) findViewById(R.id.challenge_view_text_progress1_pb);
        textProgress2_pb = (TextView) findViewById(R.id.challenge_view_text_progress2_pb);

        txt_ins = (TextView) findViewById(R.id.challenge_view_txt_instruction1);

        seekBar_team1 = (SeekBar) findViewById(R.id.challenge_view_seek_bar_team1_pa);
        seekBar_team2 = (SeekBar) findViewById(R.id.challenge_view_seek_bar_team2_pa);

        seekBar_team1_pb = (SeekBar) findViewById(R.id.challenge_view_seek_bar_team1_pb);
        seekBar_team2_pb = (SeekBar) findViewById(R.id.challenge_view_seek_bar_team2_pb);

        card_cricket = (CardView) findViewById(R.id.challenge_view_card_cricket_pa);
        card_stock = (CardView) findViewById(R.id.challenge_view_card_stock);
        card_instruction = (CardView) findViewById(R.id.challenge_view_card_instruction);

        linear_opponent = (LinearLayout) findViewById(R.id.challenge_view_opponent_data_layout);

        edt_challenger_point = (EditText) findViewById(R.id.challenge_view_edt_enter_point_pa);
        edt_opponent_points = (EditText) findViewById(R.id.challenge_view_edt_enter_point_pb);

        btn_challenge = (Button) findViewById(R.id.challenge_view_btn_accept);

        //cricket
        txt_challenge_name_cricket = (TextView) findViewById(R.id.txt_challenge_name_cricket);
        btn_my_challenge_team_1 = (Button) findViewById(R.id.btn_my_challenge_team_1);
        btn_my_challenge_team_2 = (Button) findViewById(R.id.btn_my_challenge_team_2);

        btn_opponent_challenge_team_1 = (Button) findViewById(R.id.btn_opponent_challenge_team_1);
        btn_opponent_challenge_team_2 = (Button) findViewById(R.id.btn_opponent_challenge_team_2);

        lo_my_profile = (LinearLayout) findViewById(R.id.lo_my_profile);
        lo_opponent_profile = (LinearLayout) findViewById(R.id.lo_opponent_profile);

        //stock
        txt_challenge_name_stock = (TextView) findViewById(R.id.txt_challenge_name_stock);

        lo_stock_my_profile = (LinearLayout) findViewById(R.id.lo_stock_my_profile);
        lo_stock_opponent_profile = (LinearLayout) findViewById(R.id.lo_stock_opponent_profile);

        challenge_view_img_stock_my_profile_pic = (CircleImageView) findViewById(R.id.challenge_view_img_stock_my_profile_pic);
        challenge_view_img_stock_opponent_profile_pic = (CircleImageView) findViewById(R.id.challenge_view_img_stock_opponent_profile_pic);

        challenge_view_txt_stock_my_name = (TextView) findViewById(R.id.challenge_view_txt_stock_my_name);
        challenge_view_txt_stock_opponent_name = (TextView) findViewById(R.id.challenge_view_txt_stock_opponent_name);
        
        //notification

        txt_challenge_result_winner = (TextView) findViewById(R.id.txt_challenge_result_winner);
        txt_challenge_result_accuracy = (TextView) findViewById(R.id.txt_challenge_result_accuracy);
        txt_challenge_result_win_amount = (TextView) findViewById(R.id.txt_challenge_result_win_amount);
        txt_challenge_result = (TextView) findViewById(R.id.txt_challenge_result);
        btn_noti_deny_challenge = (Button) findViewById(R.id.challenge_view_notification_btn_deny);
        btn_noti_accept_challenge = (Button) findViewById(R.id.challenge_view_notification_btn_accept);
        challenge_view_card_result = (CardView) findViewById(R.id.challenge_view_card_result);
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Challenge");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Activity_Challenge_Accept_Result_Challenge.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        /***********************
        *  Get Session Details 
        * **********************/
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        session.check_need_to_pay();

        HashMap<String, String> user = session.getUserDetails();
        str_session_data = user.get(SessionManager.KEY_USER);
        str_session_id = user.get(SessionManager.KEY_ID); // userid
        //get session details
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        str_name = sharedPreferences.getString(Activity_Login.TAG_LOGIN_USER_NAME ,"");
        str_picture = sharedPreferences.getString(Activity_Login.TAG_LOGIN_USER_IMAGE ,"");

        try {

            System.out.println("### JSON RESPONSE : " + str_session_data);
            System.out.println("### EMAIL : " + str_email);
            System.out.println("### NAME : " + str_name);
            System.out.println("### PICTURE : " + str_picture);

            txt_my_name.setText("" + str_name);
            Picasso.with(this).load(str_picture)
                    .into(img_view_my_profile_pic);

            txt_opponent_name.setText("" + str_name);
            Picasso.with(this).load(str_picture)
                    .into(img_view_opponent_profile_pic);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        get notification count
        try{

            queue = Volley.newRequestQueue(this);
            dialog_notification = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
            dialog_notification.setCancelable(false);
            dialog_notification.show();
            Function_Get_Notification();

        }catch (Exception e){

        }

        /* **************
        *  action
        * ***************/
        // get data from calling activity
        
        str_calling_activity = sharedPreferences.getString("str_calling_activity", "str_calling_activity");
        System.out.println("### str_calling_activity "+str_calling_activity);

        if (str_calling_activity.equals("Activity_Notifications") || str_calling_activity.equals("Activity_Profile") ) {

            str_notification_id = sharedPreferences.getString("str_notification_id", "str_notification_id");
            str_challenge_id = sharedPreferences.getString("str_noti_challenge_no", "str_noti_challenge_no");
            str_type = sharedPreferences.getString("str_type", "str_type");

            str_title = sharedPreferences.getString("str_title", "str_title");
            str_message = sharedPreferences.getString("str_message", "str_message");

            str_user_name = sharedPreferences.getString("str_user_name", "str_user_name");
            str_user_photo = sharedPreferences.getString("str_user_photo", "str_user_photo");
            str_challenge_normal_or_direct = sharedPreferences.getString("str_challenge_type", "str_challenge_type");

            System.out.println("###  str_notification_id "+str_notification_id);
            System.out.println("###  str_noti_challenge_no "+str_challenge_id);
            System.out.println("###  str_type "+str_type);
            System.out.println("###  str_title "+str_title);
            System.out.println("###  str_message "+str_message);
            System.out.println("###  str_user_name "+str_user_name);
            System.out.println("###  str_user_photo "+str_user_photo);
            System.out.println("###  str_challenge_type "+str_challenge_normal_or_direct);

        }


        /*************************
         *  Set Player A Date
         * ************************/
        btn_opponent_challenge_team_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (challenger_teamA_percentage.equals("100")) // means win
                {

                    TastyToast.makeText(Activity_Challenge_Accept_Result_Challenge.this, "Can't Choose This, Opponent Already Choosed It.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                } else {

                    str_op_team_a_percent = "100";
                    str_op_team_b_percent = "0";

                    btn_opponent_challenge_team_1.setBackground(getResources().getDrawable(R.drawable.bg_cross));

                }

            }
        });

        btn_opponent_challenge_team_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (challenger_teamA_percentage.equals("100")) // means win
                {

                    str_op_team_a_percent = "0";
                    str_op_team_b_percent = "100";
                    btn_opponent_challenge_team_2.setBackground(getResources().getDrawable(R.drawable.bg_cross));

                } else {

                    TastyToast.makeText(Activity_Challenge_Accept_Result_Challenge.this, "Can't Choose This, Opponent Already Choosed It.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                }
            }
        });


        seekBar_team1_pb.setMax(100);
        seekBar_team2_pb.setMax(100);

        seekBar_team1_pb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar circularSeekBar, int progress, boolean fromUser) {

                str_op_team_a_percent = "" + progress;
                Log.d("Round", str_op_team_a_percent);
                textProgress1_pb.setText(str_op_team_a_percent + "%");

                int int_seek2 = 100 - progress;

                seekBar_team2_pb.setProgress(int_seek2);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("Main", "onStopTrackingTouch");
                text_bottom_title1_pb.setText("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("Main", "onStartTrackingTouch");
                text_bottom_title1_pb.setText("Team 1 | ");

            }
        });

        seekBar_team2_pb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar circularSeekBar, int progress, boolean fromUser) {

                str_op_team_b_percent = "" + progress;
                Log.d("Round", str_op_team_b_percent);
                textProgress2_pb.setText(str_op_team_b_percent + "%");

                int int_seek1 = 100 - progress;

                seekBar_team1_pb.setProgress(int_seek1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("Main", "onStopTrackingTouch");
                text_bottom_title2_pb.setText("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("Main", "onStartTrackingTouch");
                text_bottom_title2_pb.setText("Team 2 | ");

            }
        });

        //go to profile
        lo_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //               when challenget and login user is same, go to login user profile
                if (str_challenger_id.equals(str_session_id)) // user id
                {

                    str_challenger_id = str_session_id;
                    Function_Go_To_Profile(str_challenger_id, Activity_Profile.TAG_PROFILE_MY_PROFILE);

                } else {// other wise go to other user profile

                    Function_Go_To_Profile(str_challenger_id, Activity_Profile.TAG_PROFILE_OTHER_PROFILE);
                }


            }
        });

        linear_opponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_opponent_id.equals(str_session_id)) // user id
                { // calling activity notification

                    Function_Go_To_Profile(str_session_id, Activity_Profile.TAG_PROFILE_MY_PROFILE);

                } else {

                    Function_Go_To_Profile(str_opponent_id, Activity_Profile.TAG_PROFILE_OTHER_PROFILE);
                }


            }
        });

        lo_stock_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // go to login user_pofile
                if (str_challenger_id.equals(str_session_id)) // user id
                {

                    str_challenger_id = str_session_id;
                    Function_Go_To_Profile(str_challenger_id, Activity_Profile.TAG_PROFILE_MY_PROFILE);

                } else {// other wise go to other user profile

                    Function_Go_To_Profile(str_challenger_id, Activity_Profile.TAG_PROFILE_OTHER_PROFILE);
                }

            }
        });

        lo_stock_opponent_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_opponent_id.equals(str_session_id)) // user id
                { // calling activity notification

                    Function_Go_To_Profile(str_session_id, Activity_Profile.TAG_PROFILE_MY_PROFILE);

                } else {

                    Function_Go_To_Profile(str_opponent_id, Activity_Profile.TAG_PROFILE_OTHER_PROFILE);
                }

            }
        });
        
        btn_noti_deny_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_selected_accept_status = "2";

                dialog_notification = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
                dialog_notification.setCancelable(false);
                dialog_notification.show();

                queue = Volley.newRequestQueue(Activity_Challenge_Accept_Result_Challenge.this);
                Function_Accept_Reject_Challenge();
            }
        });

        btn_noti_accept_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // if direct challenge,  then call make challenge
                if (str_challenge_normal_or_direct.equals("2")){

                    if (str_challenge_type.equals("Cricket")) {

                        if (str_op_team_a_percent.equals("")) {
                            TastyToast.makeText(getApplicationContext(), "Please Select Some Value", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else if (str_op_team_b_percent.equals("")) {
                            TastyToast.makeText(getApplicationContext(), "Please Select Some Value", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else {
                            try {
                                dialog = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
                                dialog.setCancelable(false);
                                dialog.show();
                                queue = Volley.newRequestQueue(Activity_Challenge_Accept_Result_Challenge.this);
                                Function_Make_Challenge();
                            } catch (Exception e) {

                            }
                        }

                    } else if (str_challenge_type.equals("Share market")) {

                        str_op_share_point = edt_opponent_points.getText().toString().trim();

                        if (str_op_share_point.equals("")) {
                            TastyToast.makeText(getApplicationContext(), "Please Enter an Point", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else {

                            try {
                                dialog = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
                                dialog.setCancelable(false);
                                dialog.show();
                                queue = Volley.newRequestQueue(Activity_Challenge_Accept_Result_Challenge.this);
                                Function_Make_Challenge();
                            } catch (Exception e) {

                            }

                        }
                    }

                }else{ // accept challenge throught notification

                    str_selected_accept_status = "1";

                    dialog_notification = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
                    dialog_notification.setCancelable(false);
                    dialog_notification.show();

                    queue = Volley.newRequestQueue(Activity_Challenge_Accept_Result_Challenge.this);
                    Function_Accept_Reject_Challenge();

                }

            }
        });

       if (str_calling_activity.equals("Activity_Notifications")
               || str_calling_activity.equals("Activity_Profile")) {

            try {
                dialog = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
                dialog.setCancelable(false);
                dialog.show();
                queue = Volley.newRequestQueue(Activity_Challenge_Accept_Result_Challenge.this);
                Fuction_Get_Challenge_Details_For_Notification_Challenge();

            } catch (Exception e) {

            }

        }


    }

    /**********************************
     * Main Menu
     *********************************/

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
                    .getDefaultSharedPreferences(Activity_Challenge_Accept_Result_Challenge.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_MY_PROFILE);

            editor.commit();

            Intent i = new Intent(getApplicationContext(), Activity_Profile.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Activity_Challenge_Accept_Result_Challenge.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    /***************************
     * Accept or Deny Challenge
     ***************************/

    public void Function_Accept_Reject_Challenge() {

        System.out.println("### AppConfig.url_accept_through_challenge " + AppConfig.url_accept_through_challenge);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_accept_through_challenge, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        dialog_notification.dismiss();

                        //clear notification
                        dialog_clear_notification = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
                        dialog_clear_notification.setCancelable(false);
                        dialog_clear_notification.show();
                        queue = Volley.newRequestQueue(Activity_Challenge_Accept_Result_Challenge.this);
                        Function_Clear_Notification();


                        //set message
                        String message = "";
                        if (str_selected_accept_status.equals("1")){
                            message ="Challenge Accepted Successfully !";
                        }else if (str_selected_accept_status.equals("2")){
                            message ="Challenge Denied Successfully !";
                        }

                        new AlertDialog.Builder(Activity_Challenge_Accept_Result_Challenge.this)
                                .setTitle(R.string.app_name)
                                .setMessage(message)
                                .setIcon(R.mipmap.app_icon)
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,

                                                                int which) {
                                                // TODO Auto-generated method stub

                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(i);

                                            }

                                        }).show();


                    } else if (success == 0) {

                        dialog_notification.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Challenge Can't Accepted, Please Try Again", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

                    }

                    dialog_notification.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog_notification.dismiss();

//                TastyToast.makeText(getApplicationContext(), "Internal Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("betting_id", str_challenge_id);
                params.put("accept_status", str_selected_accept_status);

                //normal challenge or direct challenge
                if (str_challenge_normal_or_direct.equals("2")){
                    params.put("bet_type", "2");
                    System.out.println("### bet_type : 2");
                }
                else{
                    params.put("bet_type", "1");
                    System.out.println("### bet_type : 1 ");
                }


                System.out.println("### betting_id : " + str_challenge_id);
                System.out.println("### accept_status : " + str_selected_accept_status);

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


    /***************************
     * Get Challenge Details for accept challenge from notification
     ***************************/

    public void Fuction_Get_Challenge_Details_For_Notification_Challenge() {

        System.out.println("### CAME 1");
        System.out.println("### AppConfig.url_get_challenge_details" + AppConfig.url_get_challenge_details);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_get_challenge_details, new Response.Listener<String>() {

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

                        /**********************************
                         * get challenge data from server
                         * *******************************/

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String challenge_id = obj1.getString(TAG_CHALLENGE_ID);
                            String event_title = obj1.getString(TAG_CHALLENGE_EVENT_TITLE);

                            String challenger_id = obj1.getString(TAG_CHALLENGE_CHALLENGER_ID);
                            String challenger_name = obj1.getString(TAG_CHALLENGE_CHALLENGER_NAME);
                            String challenger_photo = obj1.getString(TAG_CHALLENGE_CHALLENGER_PHOTO);

                            opponent_id = obj1.getString(TAG_CHALLENGE_OPPONENT_ID);
                            String opponent_name = obj1.getString(TAG_CHALLENGE_OPPONENT_NAME);
                            String opponent_photo = obj1.getString(TAG_CHALLENGE_OPPONENT_PHOTO);
                            match_type = obj1.getString(TAG_CHALLENGE_MATCH_TYPE);
                            String type = obj1.getString(TAG_CHALLENGE_TYPE);
                            String challenge_name = obj1.getString(TAG_CHALLENGE_CHALLENGE_NAME);
                            String description = obj1.getString(TAG_CHALLENGE_DESCRIPTION);

                            String teamA = obj1.getString(TAG_CHALLENGE_TEAMA);
                            String teamB = obj1.getString(TAG_CHALLENGE_TEAMB);
                            challenger_teamA_percentage = obj1.getString(TAG_CHALLENGE_TEAMA_PERCENT);
                            String challenger_teamB_percentage = obj1.getString(TAG_CHALLENGE_TEAMB_PERCENT);
                            String challenger_points = obj1.getString(TAG_CHALLENGER_POINTS);

                            String opponent_teamA_percentage = obj1.getString(TAG_CHALLENGE_OP_TEAMA_PERCENT);
                            String opponent_teamB_percentage = obj1.getString(TAG_CHALLENGE_OP_TEAMB_PERCENT);
                            String opponent_points = obj1.getString(TAG_OPPONENT_POINTS);
                            String challenge_amount = obj1.getString(TAG_CHALLENGE_CHALLENGE_AMOUNT);
                            String instructions = obj1.getString(TAG_CHALLENGE_INS);
                            String date = obj1.getString(TAG_CHALLENGE_DATE);
                            String Winner = obj1.getString(TAG_CHALLENGE_WINNER);
                            String Accuracy = obj1.getString(TAG_CHALLENGE_ACCURACY);
                            String Amount = obj1.getString(TAG_CHALLENGE_AMOUNT);
                            String Result = obj1.getString(TAG_CHALLENGE_RESULT);


                            // go to profile
                            str_challenger_id = challenger_id;
                            str_opponent_id = opponent_id;

                            /******************************
                             *   set challenge data
                             * ***************************/
                            try {
//                                txt_user_name.setText("" + str_challenger_name);

                                txt_event_name.setText("" + event_title);
                                txt_event_amount.setText("" + challenge_amount);

                                txt_opponent_name.setText(opponent_name);
                                str_challenge_type = type;

                                btn_my_challenge_team_1.setText(teamA);
                                btn_my_challenge_team_2.setText(teamB);
                                btn_opponent_challenge_team_1.setText(teamA);
                                btn_opponent_challenge_team_2.setText(teamB);
                                //get login user details
                                SessionManager session = new SessionManager(getApplicationContext());
                                HashMap<String, String> login_user =session.getLoginUserDetails();

                                String str_login_user_id = login_user.get("user_id");

                                /********************************
                                * challenge by user or opponent
                                * *******************************/
                                String impath;
                                String opp_path;
                                //challenge is login user
                                if (challenger_id.equals(str_login_user_id))
                                {
                                    impath = str_picture;

                                }else {
                                    impath = challenger_photo;
                                }
                                // if  challenge is opponent
                                txt_my_name.setText("" + challenger_name);
                                challenge_view_txt_stock_my_name.setText("" + challenger_name);

                                Picasso.with(Activity_Challenge_Accept_Result_Challenge.this)
                                        .load(impath)
                                        .placeholder(R.mipmap.ic_challenge)
                                        .into(img_view_my_profile_pic);

                                Picasso.with(Activity_Challenge_Accept_Result_Challenge.this)
                                        .load(impath)
                                        .placeholder(R.mipmap.ic_challenge)
                                        .into(challenge_view_img_stock_my_profile_pic);

                                //opponent  is login user
                                if (opponent_id.equals(str_login_user_id))
                                {
                                    opp_path = str_picture;

                                }else {
                                    opp_path = opponent_photo;
                                }
                                //opponent
                                txt_opponent_name.setText("" + opponent_name);
                                challenge_view_txt_stock_opponent_name.setText("" + opponent_name);

                                Picasso.with(Activity_Challenge_Accept_Result_Challenge.this)
                                        .load(opp_path)
                                        .placeholder(R.mipmap.ic_challenge)
                                        .into(img_view_opponent_profile_pic);

                                Picasso.with(Activity_Challenge_Accept_Result_Challenge.this)
                                        .load(opp_path)
                                        .placeholder(R.mipmap.ic_challenge)
                                        .into(challenge_view_img_stock_opponent_profile_pic);

                                /*****************************************
                                 * set challenge data based on event type *
                                 ******************************************/

                                if (type.equals("Cricket")) {


                                    //common hide and show
                                    card_cricket.setVisibility(View.VISIBLE);

                                    btn_noti_deny_challenge.setVisibility(View.VISIBLE);
                                    btn_noti_accept_challenge.setVisibility(View.VISIBLE);

                                    card_stock.setVisibility(View.GONE);
                                    btn_challenge.setVisibility(View.GONE);

                                    if (match_type.equals("1")) // mean win or lose
                                    {
                                        //hide
                                        txt_team_title1.setVisibility(View.GONE);
                                        txt_team_title2.setVisibility(View.GONE);
                                        txt_team_title1_pb.setVisibility(View.GONE);
                                        txt_team_title2_pb.setVisibility(View.GONE);

                                        seekBar_team1.setVisibility(View.GONE);
                                        seekBar_team2.setVisibility(View.GONE);

                                        seekBar_team1_pb.setVisibility(View.GONE);
                                        seekBar_team2_pb.setVisibility(View.GONE);

                                        text_bottom_title1.setVisibility(View.GONE);
                                        text_bottom_title2.setVisibility(View.GONE);

                                        textProgress1.setVisibility(View.GONE);
                                        textProgress2.setVisibility(View.GONE);

                                        //visible
                                        btn_my_challenge_team_1.setVisibility(View.VISIBLE);
                                        btn_my_challenge_team_2.setVisibility(View.VISIBLE);

                                        btn_opponent_challenge_team_1.setVisibility(View.VISIBLE);
                                        btn_opponent_challenge_team_2.setVisibility(View.VISIBLE);

                                        //set challenge value
                                        if (challenger_teamA_percentage.equals("100")) { //  100 means win

                                            btn_my_challenge_team_1.setBackground(getResources().getDrawable(R.drawable.bg_cross));
                                            btn_my_challenge_team_2.setBackgroundResource(android.R.drawable.btn_default);

                                            btn_opponent_challenge_team_1.setBackgroundResource(android.R.drawable.btn_default);
                                            btn_opponent_challenge_team_2.setBackground(getResources().getDrawable(R.drawable.bg_cross));

                                            //set value
                                            str_op_team_a_percent = "0";
                                            str_op_team_b_percent = "100";

                                        } else {

                                            btn_my_challenge_team_1.setBackgroundResource(android.R.drawable.btn_default);
                                            btn_my_challenge_team_2.setBackground(getResources().getDrawable(R.drawable.bg_cross));

                                            btn_opponent_challenge_team_1.setBackground(getResources().getDrawable(R.drawable.bg_cross));
                                            btn_opponent_challenge_team_2.setBackgroundResource(android.R.drawable.btn_default);

                                            //set value
                                            str_op_team_a_percent = "100";
                                            str_op_team_b_percent = "0";
                                        }


                                        //disable my challenge toggle
                                        btn_my_challenge_team_1.setEnabled(false);
                                        btn_my_challenge_team_1.setFocusable(false);

                                        btn_my_challenge_team_2.setEnabled(false);
                                        btn_my_challenge_team_2.setFocusable(false);

                                        btn_opponent_challenge_team_1.setEnabled(false);
                                        btn_opponent_challenge_team_1.setFocusable(false);

                                        btn_opponent_challenge_team_2.setEnabled(false);
                                        btn_opponent_challenge_team_2.setFocusable(false);

                                    } else if (match_type.equals("2")) // mean win % or lose %
                                    {

                                        //visible
                                        txt_team_title1.setVisibility(View.VISIBLE);
                                        txt_team_title2.setVisibility(View.VISIBLE);
                                        txt_team_title1_pb.setVisibility(View.VISIBLE);
                                        txt_team_title2_pb.setVisibility(View.VISIBLE);

                                        seekBar_team1.setVisibility(View.VISIBLE);
                                        seekBar_team2.setVisibility(View.VISIBLE);

                                        seekBar_team1_pb.setVisibility(View.VISIBLE);
                                        seekBar_team2_pb.setVisibility(View.VISIBLE);

                                        textProgress1.setVisibility(View.VISIBLE);
                                        textProgress2.setVisibility(View.VISIBLE);

                                        text_bottom_title1.setVisibility(View.VISIBLE);
                                        text_bottom_title2.setVisibility(View.VISIBLE);

                                        //hide
                                        btn_my_challenge_team_1.setVisibility(View.GONE);
                                        btn_my_challenge_team_2.setVisibility(View.GONE);

                                        btn_opponent_challenge_team_1.setVisibility(View.GONE);
                                        btn_opponent_challenge_team_2.setVisibility(View.GONE);

                                        // froze my challenge seek bar
                                        seekBar_team1.setEnabled(false);
                                        seekBar_team2.setEnabled(false);

                                        //set challenge value
                                        int int_opponent_teamA_percentage = 0, int_opponent_teamB_percentage = 0;
                                        if (!opponent_teamA_percentage.isEmpty())
                                        int_opponent_teamA_percentage = Integer.parseInt(opponent_teamA_percentage);
                                        if (!opponent_teamB_percentage.isEmpty())
                                        int_opponent_teamB_percentage = Integer.parseInt(opponent_teamB_percentage);

                                        seekBar_team1_pb.setProgress(int_opponent_teamA_percentage);
                                        seekBar_team2_pb.setProgress(int_opponent_teamB_percentage);

                                        // froze opponent challenge seek bar
                                        seekBar_team1_pb.setEnabled(false);
                                        seekBar_team2_pb.setEnabled(false);

                                    }


                                    System.out.println("### str_session_id : " + str_session_id);

                                    //visible opponent part
                                    linear_opponent.setVisibility(View.VISIBLE);

                                    //set value for cricket
                                    txt_challenge_name_cricket.setText("" + challenge_name);

                                    txt_team_title1.setText("" + teamA);
                                    txt_team_title2.setText("" + teamB);

                                    txt_team_title1_pb.setText("" + teamA);
                                    txt_team_title2_pb.setText("" + teamB);

                                    int int_teama = Integer.parseInt(challenger_teamA_percentage);
                                    int int_teamb = Integer.parseInt(challenger_teamB_percentage);

                                    seekBar_team1.setProgress(int_teama);
                                    seekBar_team2.setProgress(int_teamb);

                                    textProgress1.setText(int_teama + "%");
                                    textProgress2.setText(int_teamb + "%");

                                    txt_ins.setText("" + instructions);


                                } else if (str_challenge_type.equals("Share market")) {

                                    card_cricket.setVisibility(View.GONE);
                                    btn_challenge.setVisibility(View.GONE);

                                    card_stock.setVisibility(View.VISIBLE);
                                    btn_noti_deny_challenge.setVisibility(View.VISIBLE);
                                    btn_noti_accept_challenge.setVisibility(View.VISIBLE);

                                    edt_opponent_points.setVisibility(View.VISIBLE);

                                    //set challenge data
                                    txt_challenge_name_stock.setText("" + challenge_name);
                                    edt_challenger_point.setText("" + challenger_points);
                                    edt_opponent_points.setText("" + opponent_points);

                                    txt_ins.setText("" + instructions);

                                    edt_opponent_points.setEnabled(false);


                                }


                                /*********************
                                * from notification
                                * ********************/
                                //from notification : show challenge results win / lose amount
                                // type 1 = accept , deny notification

                                if (str_type.equals("2")){

                                    btn_challenge.setVisibility(View.GONE);
                                    btn_noti_accept_challenge.setVisibility(View.GONE);
                                    btn_noti_deny_challenge.setVisibility(View.GONE);

                                    challenge_view_card_result.setVisibility(View.VISIBLE);

                                    txt_challenge_result_winner.setText(Winner);
                                    txt_challenge_result_accuracy.setText(Accuracy +"%");
                                    txt_challenge_result_win_amount.setText(Amount +" INR");
                                    txt_challenge_result.setText(Result);


                                }
                                    //if challenge is direct challenge then make chalenge then accept challenge
                                if (str_challenge_normal_or_direct.equals("2")){

                                    System.out.println("### str_challenge_normal_or_direct is true");
//                    show  input view
                                    seekBar_team1_pb.setEnabled(true);
                                    seekBar_team2_pb.setEnabled(true);

                                    edt_opponent_points.setEnabled(true);

                                }
// type 4 = update notification
                                if (str_type.equals("4")) {

                                    btn_challenge.setVisibility(View.GONE);
                                    btn_noti_accept_challenge.setVisibility(View.GONE);
                                    btn_noti_deny_challenge.setVisibility(View.GONE);
                                    challenge_view_card_result.setVisibility(View.GONE);

                                    //clear notification only is calling activity is notification
                                    if (str_calling_activity.equals("Activity_Notifications")) {

                                        dialog_clear_notification = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
                                        dialog_clear_notification.setCancelable(false);
                                        dialog_clear_notification.show();
                                        queue = Volley.newRequestQueue(Activity_Challenge_Accept_Result_Challenge.this);
                                        Function_Clear_Notification();

                                    }

                                }

                            } catch (Exception e) {
                                System.out.println("### Exception e " + e.getMessage());
                            }

                        }

                    } else if (success == 0) {

                        dialog.dismiss();
//                        TastyToast.makeText(getApplicationContext(), "Error In Getting Data.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

                    }

                    dialog.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("### onErrorResponse");
                dialog.dismiss();

                Intent intent = new Intent(Activity_Challenge_Accept_Result_Challenge.this, MainActivity.class);
                startActivity(intent);

//                TastyToast.makeText(getApplicationContext(), "Internal Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("challenge_id", str_challenge_id);

                System.out.println("### challenge_id : " + str_challenge_id);

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


    /***************************
     * Clear Notification
     **************************
     * */

    public void Function_Clear_Notification() {
        String tag_json_obj = "json_obj_req";
        System.out.println("CAME 1");
        System.out.println("### AppConfig.url_clear_notifications "+AppConfig.url_clear_notifications);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_clear_notifications, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        dialog_clear_notification.dismiss();

                    } else if (success == 0) {

                        dialog_clear_notification.dismiss();

                    }

                    dialog_clear_notification.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog_clear_notification.dismiss();
//                TastyToast.makeText(Activity_Challenge_Accept_Result_Challenge.this, "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("user_id", str_session_id);
                params.put("notification_id", str_notification_id);

                System.out.println("### user_id : " +str_session_id) ;
                System.out.println("### notification_id  : " +str_notification_id) ;
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    public void Function_Go_To_Profile(String user_id, String calling_type){

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, calling_type);
        editor.putString(Activity_Profile.TAG_PROFILE_USER_ID, user_id);

        editor.commit();

        Intent intent = new Intent(this, Activity_Profile.class);
        this.startActivity(intent);

    }


    /***************************
     * Accept Challenge
     ***************************/

    public void Function_Make_Challenge() {

        System.out.println("###  AppConfig.url_accept_challenge "+ AppConfig.url_accept_challenge);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_accept_challenge, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d(TAG, "### "+response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        dialog.dismiss();

//                         if direct challnege then create challenge then clear notification
                        if (str_challenge_normal_or_direct.equals("2")){

                            dialog_clear_notification = new SpotsDialog(Activity_Challenge_Accept_Result_Challenge.this);
                            dialog_clear_notification.setCancelable(false);
                            dialog_clear_notification.show();
                            queue = Volley.newRequestQueue(Activity_Challenge_Accept_Result_Challenge.this);
                            Function_Clear_Notification();

                        }

                        new AlertDialog.Builder(Activity_Challenge_Accept_Result_Challenge.this)
                                .setTitle(R.string.app_name)
                                .setMessage("Challenge Accepted Successfully !")
                                .setIcon(R.mipmap.app_icon)
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,

                                                                int which) {
                                                // TODO Auto-generated method stub

                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(i);
                                            }

                                        }).show();

                        // for direct challenge , create challenge then close notification



                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Challenge Can't Accepted, Please Try Again", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

                    }

                    dialog.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("### onErrorResponse");
                dialog.dismiss();
//                TastyToast.makeText(getApplicationContext(), "Internal Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("challenge_id", str_challenge_id);
                params.put("opponent_userid", str_session_id);

                //normal challenge or direct challenge
                if (str_challenge_normal_or_direct.equals("2")){
                    params.put("bet_type", "2");
                    System.out.println("### bet_type : 2");
                }
                else{
                    params.put("bet_type", "1");
                    System.out.println("### bet_type : 1 ");
                }

                System.out.println("### challenge_id : " + str_challenge_id);
                System.out.println("### opponent_userid : " + str_session_id);
                if (match_type.equals("1") || match_type.equals("2")) // cricket
                {
                    params.put("opponent_teamA_percentage", str_op_team_a_percent);
                    params.put("opponent_teamB_percentage", str_op_team_b_percent);

                    System.out.println("### opponent_teamA_percentage : " + str_op_team_a_percent);
                    System.out.println("### opponent_teamB_percentage : " + str_op_team_b_percent);
                } else if (match_type.equals("3") || match_type.equals("4")) { // stock

                    params.put("opponent_points", str_op_share_point);

                    System.out.println("### opponent_points : " + str_op_share_point);
                }


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

                        dialog_notification.dismiss();
                    } else if (success == 0) {

                        tv_notification.setVisibility(View.GONE);
                        dialog_notification.dismiss();
                    }

                    dialog_notification.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog_notification.dismiss();
                tv_notification.setVisibility(View.GONE);
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

}
