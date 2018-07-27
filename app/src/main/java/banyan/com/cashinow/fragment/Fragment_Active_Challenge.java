package banyan.com.cashinow.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.Activity_Challenge_Edit;
import banyan.com.cashinow.activity.Activity_Profile;
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.adapter.List_Active_Challenge_Adapter;
import banyan.com.cashinow.adapter.List_My_Challenges_Adapter;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;

import static banyan.com.cashinow.activity.MainActivity.TAG_CHALLENGE_USER_ID;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_Active_Challenge extends Fragment  {


    private static final String TAG = "Frag_Active_Chal";
//    view
    private ListView list_active_challenges ;
    private TextView txt_empty;
    private RequestQueue queue;

//    values
    static ArrayList<HashMap<String, String>> active_challenge_list;
    private List_Active_Challenge_Adapter adapter;
    private SessionManager session;
    private String str_session_data, str_session_id;

//    constants
    public static final String TAG_CHALLENGE_ID = "challenge_id";
    public static final String TAG_CHALLENGE_TITLE = "event_title";
    public static final String TAG_CHALLENGE_NAME = "challenge_name";
    public static final String TAG_EVENT_NO = "event_no";
    public static final String TAG_CHALLENGE_TYPE = "type";
    public static final String TAG_CHALLENGE_EVENT = "event";
    public static final String TAG_CHALLENGE_CHALLENGE_NAME = "challenge_name";
    public static final String TAG_CHALLENGE_DATE = "date";
    public static final String TAG_CHALLENGE_AMOUNT = "amount";
    public static final String TAG_CHALLENGER_USER_ID = "challenger_userid";
    public static final String TAG_CHALLENGER_NAME = "challenger_name";
    public static final String TAG_CHALLENGE_PHOTO = "challenger_photo";
    public static final String TAG_OPPONENT_USER_ID = "opponent_id";
    public static final String TAG_OPPONENT_NAME = "opponent";
    public static final String TAG_OPPONENT_PHOTO = "opponent_photo";
    public static final String TAG_STATUS = "status";
    public static final String TAG_AMOUNT_WON = "amount_won";
    public static final String TAG_AMOUNT_LOST = "amount_lost";
    public static final String TAG_ACCURACY = "accuracy";
    public static final String TAG_PAYMENT_STATUS = "payment";
    public static final String TAG_UPDATE_LIMIT = "update_limit";
    public static final String TAG_CHALLENGE_ACTIVE_UPTO = "active_upto";


    public static final String TAG_CALLING_ACTIVITY_ACTIVE_CHALLENGE = "Fragment_Active_Challenge";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        /****************
        * find view by id
        * *****************/
        View root = inflater.inflate(R.layout.fragment_active_challenges, null);

        list_active_challenges = (ListView) root.findViewById(R.id.search_result_active_list);
        txt_empty = (TextView) root.findViewById(R.id.emptyElement);

        /****************
        * get details
        * ****************/
        //get user details
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        session.is_need_to_pay();

        HashMap<String, String> user = session.getUserDetails();
        str_session_data = user.get(SessionManager.KEY_USER);
        str_session_id = user.get(SessionManager.KEY_ID);

//        get current profile user id
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        String str_current_profile_id = sharedPreferences.getString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_ID, "");
        str_session_id = str_current_profile_id;
        System.out.println("### str_current_profile_id "+str_session_id);
/*
* get active challenges
* */
        try {

            active_challenge_list = new ArrayList<HashMap<String, String>>();

            queue = Volley.newRequestQueue(getActivity());
            Function_Get_Active_Challenge_List();
        } catch (Exception e) {

        }

        //actions
        list_active_challenges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("### onItemClick ");
                HashMap<String, String> challenge = active_challenge_list.get(position);

                String str_challenge_id = challenge.get(TAG_CHALLENGE_ID);
                String str_event_title = challenge.get(TAG_CHALLENGE_TITLE);
                String str_challenger_name = challenge.get(TAG_OPPONENT_NAME);
                String str_challenge_photo = challenge.get(TAG_OPPONENT_PHOTO);
                String str_update_limit = challenge.get(TAG_UPDATE_LIMIT);
                String str_active_upto = challenge.get(TAG_CHALLENGE_ACTIVE_UPTO);

                //call view challenge activity
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("str_calling_activity", Activity_Profile.TAG_PROFILE_CALLING_ACTIVITY_PROFILE );
                editor.putString("str_noti_challenge_no", str_challenge_id);
                editor.putString("str_type", "2");  // type is 2 to show winner details like winner, win amount, accuracy
                editor.putString("str_title", str_event_title);
                editor.putString("str_message", "");
                editor.putString("str_user_name", str_challenger_name);
                editor.putString("str_user_photo", str_challenge_photo);
                editor.putString(TAG_UPDATE_LIMIT, str_update_limit);
                editor.putString(TAG_CHALLENGE_ACTIVE_UPTO, str_active_upto);
                editor.commit();

                System.out.println("### str_calling_activity " + Activity_Profile.TAG_PROFILE_CALLING_ACTIVITY_PROFILE);
                System.out.println("### str_noti_challenge_no  "+str_challenge_id);
                System.out.println("### str_type   2 ");
                System.out.println("### str_title  "+str_event_title);
                System.out.println("### str_message   ");
                System.out.println("### str_user_name  "+str_challenger_name);
                System.out.println("### str_user_photo  "+str_challenge_photo);
                System.out.println("### "+ TAG_UPDATE_LIMIT + " " + str_update_limit);
                System.out.println("### "+ TAG_CHALLENGE_ACTIVE_UPTO + " " + str_active_upto);

                Intent intent = new Intent(getActivity(), Activity_Challenge_Edit.class);
                startActivity(intent);

            }
        });

        return root;
    }

    /***************************
     * GET My Active Challenges
     ***************************/

    public void Function_Get_Active_Challenge_List() {

        System.out.println("### AppConfig.url_list_active_challenges "+AppConfig.url_list_active_challenges);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_active_challenges, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String challenge_id = obj1.getString(TAG_CHALLENGE_ID);
                            String event_title = obj1.getString(TAG_CHALLENGE_TITLE);
                            String challenge_name = obj1.getString(TAG_CHALLENGE_CHALLENGE_NAME);
                            String amount = obj1.getString(TAG_CHALLENGE_AMOUNT);
                            String opponent_name = obj1.getString(TAG_OPPONENT_NAME);
                            String opponent_photo = obj1.getString(TAG_OPPONENT_PHOTO);
                            String opponent_userid = obj1.getString(TAG_OPPONENT_USER_ID);
                            String update_limit = obj1.getString(TAG_UPDATE_LIMIT);
                            String active_upto = obj1.getString(TAG_CHALLENGE_ACTIVE_UPTO);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_CHALLENGE_ID, challenge_id);
                            map.put(TAG_CHALLENGE_TITLE, event_title);
                            map.put(TAG_CHALLENGE_CHALLENGE_NAME, challenge_name);
                            map.put(TAG_CHALLENGE_AMOUNT, amount);
                            map.put(TAG_OPPONENT_USER_ID, opponent_userid);
                            map.put(TAG_OPPONENT_NAME, opponent_name);
                            map.put(TAG_OPPONENT_PHOTO, opponent_photo);
                            map.put(TAG_UPDATE_LIMIT, update_limit);
                            map.put(TAG_CHALLENGE_ACTIVE_UPTO, active_upto);

                            System.out.println("### " + TAG_CHALLENGE_ID + " " +challenge_id);
                            System.out.println("### " + TAG_CHALLENGE_TITLE + " " +event_title);
                            System.out.println("### " + TAG_CHALLENGE_CHALLENGE_NAME + " " +challenge_name);
                            System.out.println("### " + TAG_CHALLENGE_AMOUNT + " " +amount);
                            System.out.println("### " + TAG_OPPONENT_USER_ID + " " +opponent_userid);
                            System.out.println("### " + TAG_OPPONENT_NAME + " " +opponent_name);
                            System.out.println("### " + TAG_OPPONENT_PHOTO + " " +opponent_photo);
                            System.out.println("### " + TAG_UPDATE_LIMIT + " " +update_limit);
                            System.out.println("### " + TAG_CHALLENGE_ACTIVE_UPTO + " " +active_upto);

                            active_challenge_list.add(map);

                            System.out.println("### HASHMAP ARRAY" + active_challenge_list);

                            txt_empty.setVisibility(View.GONE);


                        }

                    } else if (success == 0) {

                        txt_empty.setVisibility(View.VISIBLE);

                    }

                    //                    set list in adapter even have data or no data
                    adapter = new List_Active_Challenge_Adapter(getActivity(),
                            active_challenge_list);
                    list_active_challenges.setAdapter(adapter);

                    if (active_challenge_list.size() == 0){
                        list_active_challenges.setVisibility(View.GONE);
                        txt_empty.setVisibility(View.VISIBLE);
                    }else if (active_challenge_list.size()> 0){
                        list_active_challenges.setVisibility(View.VISIBLE);
                        txt_empty.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                txt_empty.setVisibility(View.VISIBLE);

                new android.app.AlertDialog.Builder(getActivity())
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


}
