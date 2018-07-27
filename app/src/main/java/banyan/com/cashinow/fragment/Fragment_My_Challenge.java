package banyan.com.cashinow.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.adapter.List_My_Challenges_Adapter;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;

import static banyan.com.cashinow.activity.MainActivity.TAG_CHALLENGE_USER_ID;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_My_Challenge extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = Fragment_My_Challenge.class.getSimpleName();


    JSONObject response, profile_pic_data, profile_pic_url;
    String str_email, str_name, str_picture;

    // Session Manager Class
    SessionManager session;
    String str_session_data, str_session_id, str_sort_by;

    // Toolbar Notification batch
    RelativeLayout notification_Count, notification_batch, message_Count, message_batch;
    TextView tv_notification, tv_message;
    int i = 0;
    String value = "nothing";
    String search_key, search_id = "";

    TextView txt_empty;

    public static RequestQueue queue;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView list_challenges;

    public static final String TAG_USER_ID = "user_id";

    public static final String TAG_CHALLENGE_ID = "challenge_id";
    public static final String TAG_CHALLENGE_USERNAME = "created_username";
    public static final String TAG_CHALLENGE_PHOTO = "created_userphoto";
    public static final String TAG_CHALLENGE_TITLE = "event_title";
    public static final String TAG_CHALLENGE_TYPE = "type";
    public static final String TAG_CHALLENGE_EVENT = "event";
    public static final String TAG_CHALLENGE_CHALLENGE_NAME = "challenge_name";

//    challenge_name
    public static final String TAG_CHALLENGE_DATE = "date";
    public static final String TAG_CHALLENGE_AMOUNT = "amount";

    String str_selected_challenge_id = "";

    SpotsDialog dialog_delete_challenge;

    static ArrayList<HashMap<String, String>> Challenge_list;

    HashMap<String, String> params = new HashMap<String, String>();

    public List_My_Challenges_Adapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        /*
        * find view by id
        * */
        View x = inflater.inflate(R.layout.fragment_create_challange_my_challenge, null);

        list_challenges = (ListView) x.findViewById(R.id.search_result_my_challenge_list);
        swipeRefreshLayout = (SwipeRefreshLayout) x.findViewById(R.id.search_my_challenge_swipe_refresh_layout);
        txt_empty = (TextView) x.findViewById(R.id.emptyElement);

        swipeRefreshLayout.setOnRefreshListener(this);

        String fontPath = "fonts/fontawesome-webfont.ttf";

        /*
        * get session details
        * */
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        session.is_need_to_pay();

        HashMap<String, String> user = session.getUserDetails();
        str_session_data = user.get(SessionManager.KEY_USER);
        str_session_id = user.get(SessionManager.KEY_ID);

        // Hashmap for ListView
        Challenge_list = new ArrayList<HashMap<String, String>>();

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

           /* Picasso.with(this).load(profile_pic_url.getString("url"))
                    .into(user_picture);*/

        } catch (Exception e) {
            e.printStackTrace();
        }

/*
* action
* */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        try {

                                            queue = Volley.newRequestQueue(getContext());
                                            Function_Get_Challenge_List();

                                        } catch (Exception e) {
                                            // TODO: handle exception
                                        }
                                    }
                                }
        );

        list_challenges.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                str_selected_challenge_id = Challenge_list.get(pos).get(TAG_CHALLENGE_ID);

                Alert_Delete_Task();

                return true;
            }
        });

        return x;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        try {

            Challenge_list.clear();
            queue = Volley.newRequestQueue(getContext());
            Function_Get_Challenge_List();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    /***************************
     * GET Active Challenges
     ***************************/

    public void Function_Get_Challenge_List() {

        System.out.println("###  AppConfig.url_list_my_challenges "+AppConfig.url_list_my_challenges);
        String tag_json_obj = "json_obj_req";
        System.out.println("### CAME 1");
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_my_challenges, new Response.Listener<String>() {

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
                            String created_username = obj1.getString(TAG_CHALLENGE_USERNAME);
                            String created_userphoto = obj1.getString(TAG_CHALLENGE_PHOTO);
                            String event_title = obj1.getString(TAG_CHALLENGE_TITLE);
                            String type = obj1.getString(TAG_CHALLENGE_TYPE);
                            String challenge_name = obj1.getString(TAG_CHALLENGE_CHALLENGE_NAME);
                            String date = obj1.getString(TAG_CHALLENGE_DATE);
                            String amount = obj1.getString(TAG_CHALLENGE_AMOUNT);
                            String user_id = str_session_id;

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_CHALLENGE_ID, challenge_id);
                            map.put(TAG_CHALLENGE_USERNAME, created_username);
                            map.put(TAG_CHALLENGE_PHOTO, created_userphoto);
                            map.put(TAG_CHALLENGE_TITLE, event_title);
                            map.put(TAG_CHALLENGE_TYPE, type);
                            map.put(TAG_CHALLENGE_CHALLENGE_NAME, challenge_name);
                            map.put(TAG_CHALLENGE_DATE, date);
                            map.put(TAG_CHALLENGE_AMOUNT, amount);
                            map.put(TAG_CHALLENGE_USER_ID, user_id);

                            Challenge_list.add(map);

                        }

                        System.out.println("### HASHMAP ARRAY" + Challenge_list);
                        txt_empty.setVisibility(View.GONE);


                    } else if (success == 0) {


                    }

                    //                    set list in adapter even have data or no data
                    adapter = new List_My_Challenges_Adapter(getActivity(),
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
                if (Challenge_list.size() ==0)
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

    /************************************
     * Delete My Task Alert Dialog
     ***********************************/

    private void Alert_Delete_Task() {

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.app_name))
                .setMessage("Want To Delete Challange?")
                .setIcon(R.mipmap.app_icon)
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                })
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                                System.out.println("### Challenge Id " + str_selected_challenge_id);

                                dialog_delete_challenge = new SpotsDialog(getActivity());
                                dialog_delete_challenge.setCancelable(false);
                                dialog_delete_challenge.show();
                                queue = Volley.newRequestQueue(getActivity());
                                Function_Delete_Challenge();

                            }
                        }).show();
    }

    /************************************
     * Delete My Task Function
     ***********************************/

    private void Function_Delete_Challenge() {

        System.out.println("### AppConfig.url_delete_challenge "+AppConfig.url_delete_challenge);
        
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_delete_challenge, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d(TAG, response.toString());
                Log.d("USER_REGISTER", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int status = obj.getInt("status");

                    System.out.println("REG" + status);

                    if (status == 1) {

                        dialog_delete_challenge.dismiss();
                        TastyToast.makeText(getActivity(), "Challenge Deleted Successfully.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                        try {
                            Challenge_list.clear();
                            queue = Volley.newRequestQueue(getActivity());
                            Function_Get_Challenge_List();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else {

                        dialog_delete_challenge.dismiss();
                        TastyToast.makeText(getActivity(), "Challenge Not Deleted. Try Again", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog_delete_challenge.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("### onErrorResponse");
                dialog_delete_challenge.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(TAG_USER_ID, str_session_id);
                params.put(TAG_CHALLENGE_ID, str_selected_challenge_id);

                System.out.println("### " + TAG_USER_ID + " " + str_session_id );
                System.out.println("### " + TAG_CHALLENGE_ID + " " + str_selected_challenge_id );

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }
    
}
