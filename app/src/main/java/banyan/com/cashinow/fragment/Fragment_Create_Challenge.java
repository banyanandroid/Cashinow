package banyan.com.cashinow.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.Activity_Challenge_Accept_Result_Challenge;
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_Create_Challenge extends Fragment {

    private static final String TAG = Fragment_Create_Challenge.class.getSimpleName();

//    view
    private Toolbar mToolbar;
    private Button btn_create_challenge, btn_create_challenge_submit;
    private TextView  txt_team_title1, txt_team_title2, text_bottom_title1, text_bottom_title2, textProgress1, textProgress2;
    private TextView txt_bit_amt, txt_ins1;
    private SeekBar seekBar_team1, seekBar_team2;
    private EditText edt_amount, edt_share_point;
    private Spinner spinner_event;
    private CardView card_cricket, card_stock, card_instruction;
    private Button btn_team_1, btn_team_2;
    private Boolean bol_team_1_is_selected = false, bol_team_2_is_selected  = false;
    SpotsDialog dialog;
    public static RequestQueue queue;

//    values
    ArrayList<String> Arraylist_event_id = null;
    ArrayList<String> Arraylist_challenge_name = null;
    ArrayList<String> Arraylist_match_type = null;
    ArrayList<String> Arraylist_event_title = null;
    ArrayList<String> Arraylist_event_type = null;
    ArrayList<String> Arraylist_team_a = null;
    ArrayList<String> Arraylist_team_b = null;
    ArrayList<String> Arraylist_event_instruction1 = null;
    ArrayList<String> Arraylist_event_instruction2 = null;
    ArrayList<String> Arraylist_event_instruction3 = null;
    ArrayList<String> Arraylist_event_minimum = null;
    ArrayList<String> Arraylist_event_maximum = null;

    String  str_select_event_id = "", str_select_event_title, str_select_match_type, str_select_challenge_name,  str_select_event_type, str_select_team_a ,str_select_team_b ,  str_select_event_ins1, str_select_event_ins2,
            str_select_event_ins3, str_select_event_min, str_select_event_max = "";

    JSONObject response, profile_pic_data, profile_pic_url;
    String str_email, str_name, str_picture;

    // Session Manager Class
    SessionManager session;
    String str_session_data, str_session_id;

    String str_amount = "", str_team_a_percent="", str_team_b_percent="", str_challenger_point="", str_challenger_points_percentage ="";

    //cricket
    TextView txt_challenge_name_cricket;

//    stock
    TextView txt_challenge_name_stock;


    private SpotsDialog dialog_single_challenge;

    // direct challenge
    public static final String TAG_CHALLENGE_CALLING_TYPE = "calling_type";
    public static final String TAG_CHALLENGE_DIRECT_CHALLENGE = "direct_challenge";
    public static final String TAG_CHALLENGE_NORMAL_CHALLENGE = "normal_challenge";
    public static final String TAG_CHALLENGE_CREATE_CHALLENGE_FROM_NOTIFICATION = "create_challenge_from_notification";
    public static final String TAG_CHALLENGE_NOTIFICATION_ID = "notification_id";
    public static final String TAG_CHALLENGE_CHALLENGE_USER_ID = "challenger_userid";
    public static final String TAG_CHALLENGE_OPPONENT_USER_ID = "opponent_userid";

    String str_calling_type = "", str_challenger_user_id, str_opponent_user_id;
    Button btn_create_direct_challenge_submit;

    public static final String TAG_SPINNER_SELECT_EVENT = "Select Event";


//    create challege from notification
    SpotsDialog dialog_clear_notification;
    String str_notification_id ="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View x =  inflater.inflate(R.layout.fragment_create_challenge,null);

        //find view by id
        mToolbar = (Toolbar) x.findViewById(R.id.toolbar);

        txt_ins1 = (TextView) x.findViewById(R.id.challenge_txt_instruction1);

        txt_bit_amt = (TextView) x.findViewById(R.id.challange_txt_bit_amt);

        txt_team_title1 = (TextView) x.findViewById(R.id.challange_text_team_title1);
        txt_team_title2 = (TextView) x.findViewById(R.id.challange_text_team_title2);
        text_bottom_title1 = (TextView) x.findViewById(R.id.challange_text_team1);
        text_bottom_title2 = (TextView) x.findViewById(R.id.challange_text_team2);
        textProgress1 = (TextView) x.findViewById(R.id.challenge_text_progress1);
        textProgress2 = (TextView) x.findViewById(R.id.challenge_text_progress2);

        //cricket

        txt_challenge_name_cricket = (TextView) x.findViewById(R.id.txt_challenge_name_cricket);
        btn_team_1 = (Button) x.findViewById(R.id.btn_team_1);
        btn_team_2 = (Button) x.findViewById(R.id.btn_team_2);

        seekBar_team1 = (SeekBar) x.findViewById(R.id.challenge_seek_bar_team1);
        seekBar_team2 = (SeekBar) x.findViewById(R.id.challenge_seek_bar_team2);

        txt_challenge_name_stock = (TextView) x.findViewById(R.id.txt_challenge_name_stock);
        edt_amount = (EditText) x.findViewById(R.id.challenge_edt_amount);
        edt_share_point = (EditText) x.findViewById(R.id.challenge_edt_enter_point);

        spinner_event = (Spinner) x.findViewById(R.id.challenge_spiner_event);

        card_cricket = (CardView) x.findViewById(R.id.challenge_card_cricket);
        card_stock = (CardView) x.findViewById(R.id.challenge_card_stock);
        card_instruction = (CardView) x.findViewById(R.id.challenge_card_instruction);

        btn_create_challenge_submit = (Button) x.findViewById(R.id.challenge_btn_create);
        btn_create_direct_challenge_submit = (Button) x.findViewById(R.id.direct_challenge_btn_create);


        //get calling type data
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        str_calling_type = sharedPreferences.getString(Fragment_Create_Challenge.TAG_CHALLENGE_CALLING_TYPE, Fragment_Create_Challenge.TAG_CHALLENGE_CALLING_TYPE);

        if (str_calling_type.equals(Fragment_Create_Challenge.TAG_CHALLENGE_DIRECT_CHALLENGE)){

            str_challenger_user_id = sharedPreferences.getString(Fragment_Create_Challenge.TAG_CHALLENGE_CHALLENGE_USER_ID, Fragment_Create_Challenge.TAG_CHALLENGE_CHALLENGE_USER_ID);
            str_opponent_user_id = sharedPreferences.getString(Fragment_Create_Challenge.TAG_CHALLENGE_OPPONENT_USER_ID, Fragment_Create_Challenge.TAG_CHALLENGE_OPPONENT_USER_ID);

            //hide and show
            btn_create_challenge_submit.setVisibility(View.GONE);
            btn_create_direct_challenge_submit.setVisibility(View.VISIBLE);

        }else if (str_calling_type.equals(Fragment_Create_Challenge.TAG_CHALLENGE_CREATE_CHALLENGE_FROM_NOTIFICATION)){ // if crate challenge from notification , then clear notification

            str_notification_id = sharedPreferences.getString(Fragment_Create_Challenge.TAG_CHALLENGE_NOTIFICATION_ID, "0");

            dialog_clear_notification = new SpotsDialog(getActivity());
            dialog_clear_notification.setCancelable(false);
            dialog_clear_notification.show();
            queue = Volley.newRequestQueue(getActivity());
            Function_Clear_Notification();

        }


        /*
        * get session details
        * */
        session = new SessionManager(getContext());

        session.checkLogin();
        session.is_need_to_pay();

        HashMap<String, String> user = session.getUserDetails();
        str_session_data = user.get(SessionManager.KEY_USER);
        str_session_id = user.get(SessionManager.KEY_ID);


        edt_amount.setEnabled(false);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        * initialize values
        * */
        Arraylist_event_id = new ArrayList<String>();
        Arraylist_challenge_name = new ArrayList<String>();
        Arraylist_match_type = new ArrayList<String>();
        Arraylist_event_title = new ArrayList<String>();
        Arraylist_event_type = new ArrayList<String>();
        Arraylist_team_a = new ArrayList<String>();
        Arraylist_team_b = new ArrayList<String>();
        Arraylist_event_instruction1 = new ArrayList<String>();
        Arraylist_event_instruction2 = new ArrayList<String>();
        Arraylist_event_instruction3 = new ArrayList<String>();
        Arraylist_event_minimum = new ArrayList<String>();
        Arraylist_event_maximum = new ArrayList<String>();

        /*
        * set values
        * */
        Arraylist_event_title.add(TAG_SPINNER_SELECT_EVENT);
        Arraylist_event_id.add("");
        Arraylist_challenge_name.add("");
        Arraylist_match_type.add("");
        Arraylist_event_type.add("");
        Arraylist_team_a.add("");
        Arraylist_team_b.add("");
        Arraylist_event_instruction1.add("");
        Arraylist_event_instruction2.add("");
        Arraylist_event_instruction3.add("");
        Arraylist_event_minimum.add("");
        Arraylist_event_maximum.add("");

//        action
        spinner_event.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String str_selected_event = parent.getItemAtPosition(position).toString();

                edt_amount.setEnabled(true);

                // clear data
                str_amount = "";
                str_team_a_percent = "";
                str_team_b_percent = "";
                str_challenger_point = "";
                str_challenger_points_percentage = "";

                edt_amount.setText("");
                edt_share_point.setText("");

                if (str_selected_event.equals(TAG_SPINNER_SELECT_EVENT)) {

                    card_instruction.setVisibility(View.GONE);
                    card_cricket.setVisibility(View.GONE);
                    card_stock.setVisibility(View.VISIBLE);

                }else{

                    //                get event details for selected event
                    str_select_event_id = Arraylist_event_id.get(position);
                    str_select_event_title = Arraylist_event_title.get(position);
                    str_select_match_type = Arraylist_match_type.get(position);
                    str_select_event_type = Arraylist_event_type.get(position);
                    str_select_challenge_name = Arraylist_challenge_name.get(position);
                    str_select_team_a = Arraylist_team_a.get(position);
                    str_select_team_b = Arraylist_team_b.get(position);
                    str_select_event_ins1 = Arraylist_event_instruction1.get(position);
                    str_select_event_ins2 = Arraylist_event_instruction2.get(position);
                    str_select_event_ins3 = Arraylist_event_instruction3.get(position);
                    str_select_event_min = Arraylist_event_minimum.get(position);
                    str_select_event_max = Arraylist_event_maximum.get(position);

                    System.out.println("### TYPE :: " + str_select_event_type);

                    //set  challenge value
                    String str_bit_amt = "From : " + str_select_event_min + " To : " + str_select_event_max;
                    txt_bit_amt.setText("" + str_bit_amt);

                    if (str_select_event_type.equals("Cricket")) {


                        card_instruction.setVisibility(View.VISIBLE);
                        card_cricket.setVisibility(View.VISIBLE);
                        card_stock.setVisibility(View.GONE);

                        txt_challenge_name_cricket.setText(str_select_challenge_name);
                        txt_ins1.setText("" + str_select_event_ins1);

                        txt_team_title1.setText("" + str_select_team_a);
                        txt_team_title2.setText("" + str_select_team_b);

                        btn_team_1.setText("" + str_select_team_a);
                        btn_team_2.setText("" + str_select_team_b);
                        // win or lose
                        if (str_select_match_type.equals("1")){

                            txt_team_title1.setVisibility(View.GONE);
                            txt_team_title2.setVisibility(View.GONE);

                            seekBar_team1.setVisibility(View.GONE);
                            seekBar_team2.setVisibility(View.GONE);
                            text_bottom_title1.setVisibility(View.GONE);
                            text_bottom_title2.setVisibility(View.GONE);
                            textProgress1.setVisibility(View.GONE);
                            textProgress2.setVisibility(View.GONE);

                            btn_team_1.setVisibility(View.VISIBLE);
                            btn_team_2.setVisibility(View.VISIBLE);

                        }else if(str_select_match_type.equals("2")){ // if win % and lose %

                            txt_team_title1.setVisibility(View.VISIBLE);
                            txt_team_title2.setVisibility(View.VISIBLE);

                            seekBar_team1.setVisibility(View.VISIBLE);
                            seekBar_team2.setVisibility(View.VISIBLE);
                            text_bottom_title1.setVisibility(View.VISIBLE);
                            text_bottom_title2.setVisibility(View.VISIBLE);
                            textProgress1.setVisibility(View.VISIBLE);
                            textProgress2.setVisibility(View.VISIBLE);

                            btn_team_1.setVisibility(View.GONE);
                            btn_team_2.setVisibility(View.GONE);
                        }


                    } else if (str_select_event_type.equals("Share market")) {

                        card_instruction.setVisibility(View.VISIBLE);
                        card_cricket.setVisibility(View.GONE);
                        card_stock.setVisibility(View.VISIBLE);

                        txt_challenge_name_stock.setText(str_select_challenge_name);

                        txt_ins1.setText("" + str_select_event_ins1);

                    }

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // cricket : win or lose of team
        // for team 1
        btn_team_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bol_team_1_is_selected){
                    bol_team_1_is_selected = false;

                    str_team_a_percent = "0";
                    str_team_b_percent = "100";

                    btn_team_1.setBackgroundResource(android.R.drawable.btn_default);
                    btn_team_2.setBackground(getResources().getDrawable(R.drawable.bg_cross));
                }else{
                    bol_team_1_is_selected = true;

                    str_team_a_percent = "100";
                    str_team_b_percent = "0";

                    btn_team_1.setBackground(getResources().getDrawable(R.drawable.bg_cross));
                    btn_team_2.setBackgroundResource(android.R.drawable.btn_default);
                }

            }
        });

        // for team 2
        btn_team_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bol_team_2_is_selected){
                    bol_team_2_is_selected = false;

                    str_team_a_percent = "100";
                    str_team_b_percent = "0";

                    btn_team_1.setBackground(getResources().getDrawable(R.drawable.bg_cross));
                    btn_team_2.setBackgroundResource(android.R.drawable.btn_default);



                }else{
                    bol_team_2_is_selected = true;

                    str_team_a_percent = "0";
                    str_team_b_percent = "100";

                    btn_team_1.setBackgroundResource(android.R.drawable.btn_default);
                    btn_team_2.setBackground(getResources().getDrawable(R.drawable.bg_cross));
                }
            }
        });

        seekBar_team1.setMax(100);
        seekBar_team2.setMax(100);

        seekBar_team1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()  {
            @Override
            public void onProgressChanged(SeekBar SeekBar, int progress, boolean fromUser) {
                str_team_a_percent = "" + progress;
                Log.d("Round", str_team_a_percent);
                textProgress1.setText(str_team_a_percent + "%");

                int int_seek2 = 100 - progress;

                seekBar_team2.setProgress(int_seek2);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("Main", "onStopTrackingTouch");
                text_bottom_title1.setText("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("Main", "onStartTrackingTouch");
                text_bottom_title1.setText("Team 1 | ");

            }
        });

        seekBar_team2.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener()  {
            @Override
            public void onProgressChanged(SeekBar SeekBar, int progress, boolean fromUser) {
                str_team_b_percent = "" + progress;
                Log.d("Round", str_team_b_percent);
                textProgress2.setText(str_team_b_percent + "%");

                int int_seek1 = 100 - progress;

                seekBar_team1.setProgress(int_seek1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("Main", "onStopTrackingTouch");
                text_bottom_title2.setText("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("Main", "onStartTrackingTouch");
                text_bottom_title2.setText("Team 2 | ");

            }
        });


        btn_create_challenge_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("###  :: DONEEEEEEEEEE :: ");

                str_amount = edt_amount.getText().toString().trim();
                str_challenger_point = edt_share_point.getText().toString().trim();

                System.out.println("###  :: DONEEEEEEEEEE 1111 :: " + str_select_event_type);
                // for challenge amount


                if (str_select_event_type != null && !str_select_event_type.isEmpty() && !str_select_event_type.equals("null")) {

                    if (str_select_event_type.equals("Cricket")) {
                        if (str_amount.equals("")) {
                            TastyToast.makeText(getContext(), "Please Enter a Challenge Amount", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }else if( !( ( Integer.parseInt(str_amount) >= Integer.parseInt(str_select_event_min) )  &&  (Integer.parseInt(str_amount) <= Integer.parseInt(str_select_event_max)) ) ){
                            TastyToast.makeText(getContext(), "Challenge Amount Should Be With In Bit Amount.", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }else if (str_team_a_percent.equals("")) {
                            TastyToast.makeText(getContext(), "Please Select Some Value", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else if (str_team_b_percent.equals("")) {
                            TastyToast.makeText(getContext(), "Please Select Some Value", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else {
                            try {
                                dialog = new SpotsDialog(getContext());
                                dialog.setCancelable(false);
                                dialog.show();
                                queue = Volley.newRequestQueue(getContext());
                                Fuction_Create_Challenge();
                            } catch (Exception e) {

                            }
                        }
                    } else if (str_select_event_type.equals("Share market")) {
                        if (str_amount.equals("")) {
                            TastyToast.makeText(getContext(), "Please Enter a Challenge Amount", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }else if( !( ( Integer.parseInt(str_amount) >= Integer.parseInt(str_select_event_min) )  &&  (Integer.parseInt(str_amount) <= Integer.parseInt(str_select_event_max)) ) ){
                            TastyToast.makeText(getContext(), "Challenge Amount Should Be With In Bit Amount.", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }else if (str_challenger_point.equals("")) {
                            TastyToast.makeText(getContext(), "Please Enter an Point", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else {

                            try {
                                dialog = new SpotsDialog(getContext());
                                dialog.setCancelable(false);
                                dialog.show();
                                queue = Volley.newRequestQueue(getContext());
                                Fuction_Create_Challenge();
                            } catch (Exception e) {

                            }

                        }
                    }
                } else {
                    TastyToast.makeText(getContext(), "Please Select an Event", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }
        });

        btn_create_direct_challenge_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("###  :: DONEEEEEEEEEE :: ");

                str_amount = edt_amount.getText().toString().trim();
                str_challenger_point = edt_share_point.getText().toString().trim();

                System.out.println("###  :: DONEEEEEEEEEE 1111 :: " + str_select_event_type);
                // for challenge amount


                if (str_select_event_type != null && !str_select_event_type.isEmpty() && !str_select_event_type.equals("null")) {

                    if (str_select_event_type.equals("Cricket")) {
                        if (str_amount.equals("")) {
                            TastyToast.makeText(getContext(), "Please Enter a Challenge Amount", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }else if( !( ( Integer.parseInt(str_amount) >= Integer.parseInt(str_select_event_min) )  &&  (Integer.parseInt(str_amount) <= Integer.parseInt(str_select_event_max)) ) ){
                            TastyToast.makeText(getContext(), "Challenge Amount Should Be With In Bit Amount.", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }else if (str_team_a_percent.equals("")) {
                            TastyToast.makeText(getContext(), "Please Select Some Value", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else if (str_team_b_percent.equals("")) {
                            TastyToast.makeText(getContext(), "Please Select Some Value", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else {
                            try {
                                dialog = new SpotsDialog(getContext());
                                dialog.setCancelable(false);
                                dialog.show();
                                queue = Volley.newRequestQueue(getContext());
                                Fuction_Create_Direct_Challenge();
                            } catch (Exception e) {

                            }
                        }
                    } else if (str_select_event_type.equals("Share market")) {
                        if (str_amount.equals("")) {
                            TastyToast.makeText(getContext(), "Please Enter a Challenge Amount", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }else if( !( ( Integer.parseInt(str_amount) >= Integer.parseInt(str_select_event_min) )  &&  (Integer.parseInt(str_amount) <= Integer.parseInt(str_select_event_max)) ) ){
                            TastyToast.makeText(getContext(), "Challenge Amount Should Be With In Bit Amount.", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }else if (str_challenger_point.equals("")) {
                            TastyToast.makeText(getContext(), "Please Enter an Point", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        } else {

                            try {
                                dialog = new SpotsDialog(getContext());
                                dialog.setCancelable(false);
                                dialog.show();
                                queue = Volley.newRequestQueue(getContext());
                                Fuction_Create_Direct_Challenge();
                            } catch (Exception e) {

                            }

                        }
                    }
                } else {
                    TastyToast.makeText(getContext(), "Please Select an Event", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }
        });

        /*
        * get event list
        * */
        try {
            dialog = new SpotsDialog(getContext());
            dialog.setCancelable(false);
            dialog.show();
            queue = Volley.newRequestQueue(getContext());
            Fuction_Get_Event_List();
        } catch (Exception e) {

        }

        return x;
    }


    /***************************
     * GET Events
     ***************************/

    public void Fuction_Get_Event_List() {

        System.out.println("###  AppConfig.url_list_events "+AppConfig.url_list_events);
        String tag_json_obj = "json_obj_req";
        System.out.println("### CAME 1");
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_events, new Response.Listener<String>() {

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

                            String id = obj1.getString("event_no");
                            String title = obj1.getString("title");
                            String type = obj1.getString("type");
                            String teamA = obj1.getString("teamA");
                            String teamB = obj1.getString("teamB");
                            String match_type = obj1.getString("match_type");
                            String challenge_name = obj1.getString("challenge_name");
                            String instructions1 = obj1.getString("instructions1");
                            String instructions2 = obj1.getString("instructions2");
                            String instructions3 = obj1.getString("instructions3");
                            String minimum_challenge = obj1.getString("minimum_challenge");
                            String maximum_challenge = obj1.getString("maximum_challenge");

                            Arraylist_event_id.add(id);
                            Arraylist_challenge_name.add(challenge_name);
                            Arraylist_match_type.add(match_type);
                            Arraylist_event_type.add(type);
                            Arraylist_team_a.add(teamA);
                            Arraylist_team_b.add(teamB);
                            Arraylist_event_title.add(title);
                            Arraylist_event_instruction1.add(instructions1);
                            Arraylist_event_instruction2.add(instructions2);
                            Arraylist_event_instruction3.add(instructions3);
                            Arraylist_event_minimum.add(minimum_challenge);
                            Arraylist_event_maximum.add(maximum_challenge);

                            try {
                                spinner_event
                                        .setAdapter(new ArrayAdapter<String>(getContext(),
                                                android.R.layout.simple_spinner_dropdown_item,
                                                Arraylist_event_title));

                            } catch (Exception e) {

                            }

                            System.out.println("###  AppConfig.url_list_events hashmap event_no " +id);
                        }


                        dialog.dismiss();
                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getContext(), "No Active Events", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

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

                new AlertDialog.Builder(getActivity())
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
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);

                                    }

                                }).show();

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
     * Create Challenge
     ***************************/

    public void Fuction_Create_Challenge() {

        System.out.println("###  AppConfig.url_create_challenge "+AppConfig.url_create_challenge);

        String tag_json_obj = "json_obj_req";
        System.out.println("### CAME 1");
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_create_challenge, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {


                        dialog.dismiss();

                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.app_name)
                                .setMessage("Challenge Created Successfully !")
                                .setIcon(R.mipmap.app_icon)
                                .setCancelable(false)
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(i);
                                        getActivity().finish();
                                    }
                                })
                                .setPositiveButton("Share",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,

                                                                int which) {
                                                // TODO Auto-generated method stub

                                                String title = "Download & Join on Cashinow";
                                                String url = AppConfig.url_app;
                                                Intent shareIntent = new Intent();
                                                shareIntent.setAction(Intent.ACTION_SEND);
                                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                                                shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                                                shareIntent.setType("text/plain");
                                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                startActivity(Intent.createChooser(shareIntent, "Share..."));
                                                getActivity().finish();

                                            }

                                        }).show();


                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getContext(), "Challenge Not Created, Please Try Again", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

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

                dialog.dismiss();

//                TastyToast.makeText(getContext(), "Internal Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("event_no", str_select_event_id);
                params.put("challenge_createdby", str_session_id);
                params.put("challenger_amount", str_amount);

                if (str_select_match_type.equals("1") || str_select_match_type.equals("2"))
                {

                    params.put("challenger_teamA_percentage", str_team_a_percent);
                    params.put("challenger_teamB_percentage", str_team_b_percent);

                    System.out.println("### challenger_teamA_percentage" + str_team_a_percent);
                    System.out.println("### challenger_teamB_percentage" + str_team_b_percent);

                }else if (str_select_match_type.equals("3") || str_select_match_type.equals("4")){

                    params.put("challenger_points", str_challenger_point);

                    System.out.println("### challenger_points" + str_challenger_point);
                }

                System.out.println("### event_no" + str_select_event_id);
                System.out.println("### challenge_createdby" + str_session_id);
                System.out.println("### challenger_amount" + str_amount);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /***************************
     * Create Direct Challenge
     ***************************/

    public void Fuction_Create_Direct_Challenge() {

        System.out.println("###  AppConfig.url_create_direct_challenge "+AppConfig.url_create_direct_challenge);

        String tag_json_obj = "json_obj_req";
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_create_direct_challenge, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        dialog.dismiss();
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.app_name)
                                .setMessage("Direct Challenge Created Successfully !")
                                .setIcon(R.mipmap.app_icon)
                                .setCancelable(false)
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(i);
                                        getActivity().finish();
                                    }
                                })
                                .setPositiveButton("Share",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,

                                                                int which) {
                                                // TODO Auto-generated method stub

                                                String title = "Download & Join on Cashinow";
                                                String url = AppConfig.url_app;
                                                Intent shareIntent = new Intent();
                                                shareIntent.setAction(Intent.ACTION_SEND);
                                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                                                shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                                                shareIntent.setType("text/plain");
                                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                startActivity(Intent.createChooser(shareIntent, "Share..."));
                                                getActivity().finish();

                                            }

                                        }).show();

                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getContext(), "Challenge Not Created, Please Try Again", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

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

                dialog.dismiss();

//                TastyToast.makeText(getContext(), "Internal Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("challenger_userid", str_challenger_user_id);
                params.put("opponent_userid", str_opponent_user_id);
                params.put("event_no", str_select_event_id);
                params.put("amount", str_amount);


                if (str_select_match_type.equals("1") || str_select_match_type.equals("2"))
                {

                    params.put("challenger_teamA_percentage", str_team_a_percent);
                    params.put("challenger_teamB_percentage", str_team_b_percent);

                    System.out.println("### challenger_teamA_percentage" + str_team_a_percent);
                    System.out.println("### challenger_teamB_percentage" + str_team_b_percent);

                }else if (str_select_match_type.equals("3") || str_select_match_type.equals("4")){

                    params.put("challenger_points", str_challenger_point);

                    System.out.println("### challenger_points" + str_challenger_point);
                }

                System.out.println("### challenger_userid" + str_challenger_user_id);
                System.out.println("### opponent_userid" + str_opponent_user_id);
                System.out.println("### event_no" + str_select_event_id);
                System.out.println("### amount " + str_amount);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /***************************
     * Clear Notification
     **************************
     * */

    public void Function_Clear_Notification() {
        System.out.println("### AppConfig.url_clear_notifications "+AppConfig.url_clear_notifications);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_clear_notifications, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d("", "### "+response.toString());
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
                System.out.println("### onErrorResponse");
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

}
