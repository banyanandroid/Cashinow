package banyan.com.cashinow.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import banyan.com.cashinow.activity.Activity_Challenge_Accept_Result_Challenge;
import banyan.com.cashinow.activity.Activity_Profile;
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.adapter.List_Complete_Challenge_Adapter;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_Completed_Challenge extends Fragment  {


    private static final String TAG = "Frag_Active_Chal";
//    view
    private ListView list_completed_challenges ;
    private TextView txt_empty_completed;
    private RequestQueue queue;

//    values
    static ArrayList<HashMap<String, String>> complete_challenge_list;
    private List_Complete_Challenge_Adapter adapter_completed;
    private SessionManager session;
    private String str_session_data, str_session_id;
    
//    constants
    public static final String TAG_CHALLENGE_ID = "challenge_id";
    public static final String TAG_CHALLENGE_TITLE = "event_title";
    public static final String TAG_EVENT_NO = "event_no";
    public static final String TAG_CHALLENGE_TYPE = "type";
    public static final String TAG_CHALLENGE_EVENT = "event";
    public static final String TAG_CHALLENGE_CHALLENGE_NAME = "challenge_name";
    public static final String TAG_CHALLENGE_DATE = "date";
    public static final String TAG_CHALLENGE_AMOUNT = "amount";
    public static final String TAG_CHALLENGE_GAIN = "gain";
    public static final String TAG_CHALLENGER_USER_ID = "challenger_userid";
    public static final String TAG_CHALLENGER_NAME = "challenger_name";
    public static final String TAG_CHALLENGE_PHOTO = "challenger_photo";
    public static final String TAG_OPPONENT_USER_ID = "opponent_id";
    public static final String TAG_OPPONENT_NAME = "opponent";
    public static final String TAG_OPPONENT_PHOTO = "opponent_photo";
    public static final String TAG_CHALLENGE_WINNER = "winner";
    public static final String TAG_CHALLENGE_LOSER = "loser";
    public static final String TAG_STATUS = "status";
    public static final String TAG_AMOUNT_WON = "amount_won";
    public static final String TAG_AMOUNT_LOST = "amount_lost";
    public static final String TAG_ACCURACY = "accuracy";
    public static final String TAG_PAYMENT_STATUS = "payment";
    


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        /*
        * find view by id
        * */
        View root = inflater.inflate(R.layout.fragment_active_challenges, null);

        list_completed_challenges = (ListView) root.findViewById(R.id.search_result_active_list);
        txt_empty_completed = (TextView) root.findViewById(R.id.emptyElement);

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

        String str_current_profile_id = sharedPreferences.getString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_ID, str_session_id);
        str_session_id = str_current_profile_id;
        System.out.println("### str_current_profile_id "+str_session_id);
/*
* get active challenges
* */
        try {
            
            complete_challenge_list = new ArrayList<HashMap<String, String>>();

            queue = Volley.newRequestQueue(getActivity());
            Function_Get_Completed_Challenge_List();
        } catch (Exception e) {

        }

//        actions

        list_completed_challenges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, String> challenge = complete_challenge_list.get(position);

                String str_challenge_id = challenge.get(TAG_CHALLENGE_ID);
                String str_event_title = challenge.get(TAG_CHALLENGE_TITLE);

                /************************
                 * get session details *
                 * ***********************/
                SessionManager session = new SessionManager(getApplicationContext());

                HashMap<String, String> user = session.getUserDetails();
                String str_session_data = user.get(SessionManager.KEY_USER);

                String str_session_user_picture = "", str_session_user_name = "";
                try {
                    JSONObject response = new JSONObject(str_session_data);
                    str_session_user_name = response.get("name").toString();
                    JSONObject profile_pic_data = new JSONObject(response.get("picture").toString());
                    JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                    str_session_user_picture = profile_pic_url.getString("url");

                } catch(Exception e){
                    e.printStackTrace();
                }

                String str_challenger_name = str_session_user_name;
                String str_challenger_photo = str_session_user_picture;

                //call view challenge activity
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("str_calling_activity", "Activity_Profile");
                editor.putString("str_noti_challenge_no", str_challenge_id);
                editor.putString("str_type", "2");  // type is 2 to show winner details like winner, win amount, accuracy
                editor.putString("str_title", str_event_title);
                editor.putString("str_message", "");
                editor.putString("str_user_name", str_challenger_name);
                editor.putString("str_user_photo", str_challenger_photo);
                editor.putString("str_challenge_type", "1");
                editor.commit();

                System.out.println("### str_calling_activity  Activity_Profile ");
                System.out.println("### str_noti_challenge_no  "+str_challenge_id);
                System.out.println("### str_type   2 ");
                System.out.println("### str_title  "+str_event_title);
                System.out.println("### str_message   ");
                System.out.println("### str_user_name  "+str_challenger_name);
                System.out.println("### str_user_photo  "+str_challenger_photo);
                System.out.println("### str_challenge_type : 1 ");

                Intent intent = new Intent(getActivity(), Activity_Challenge_Accept_Result_Challenge.class);
                startActivity(intent);

            }
        });


        return root;
    }

    /***************************
     * GET My Completed Challenges
     ***************************/

    public void Function_Get_Completed_Challenge_List() {

        System.out.println("### AppConfig.url_list_completed_challenges "+AppConfig.url_list_completed_challenges);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_completed_challenges, new Response.Listener<String>() {

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
                            String gain = obj1.getString(TAG_CHALLENGE_GAIN);
                            String opponent_name = obj1.getString(TAG_OPPONENT_NAME);
                            String opponent_photo = obj1.getString(TAG_OPPONENT_PHOTO);
                            String opponent_userid = obj1.getString(TAG_OPPONENT_USER_ID);
                            String status = obj1.getString(TAG_STATUS);
                            String winner = obj1.getString(TAG_CHALLENGE_WINNER);
                            String loser = obj1.getString(TAG_CHALLENGE_LOSER);
                            String accuracy = obj1.getString(TAG_ACCURACY);
                            String payment_status = obj1.getString(TAG_PAYMENT_STATUS);


                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_CHALLENGE_ID, challenge_id);
                            map.put(TAG_CHALLENGE_TITLE, event_title);
                            map.put(TAG_CHALLENGE_CHALLENGE_NAME, challenge_name);
                            map.put(TAG_CHALLENGE_AMOUNT, amount);
                            map.put(TAG_CHALLENGE_GAIN, gain);
                            map.put(TAG_OPPONENT_USER_ID, opponent_userid);
                            map.put(TAG_OPPONENT_NAME, opponent_name);
                            map.put(TAG_OPPONENT_PHOTO, opponent_photo);
                            map.put(TAG_STATUS, status);
                            map.put(TAG_CHALLENGE_WINNER, winner);
                            map.put(TAG_CHALLENGE_LOSER, loser);
                            map.put(TAG_PAYMENT_STATUS, payment_status);
                            map.put(TAG_ACCURACY, accuracy);

                            complete_challenge_list.add(map);

                            System.out.println("### HASHMAP ARRAY" + complete_challenge_list);

                            txt_empty_completed.setVisibility(View.GONE);




                        }


                    } else if (success == 0) {

                        txt_empty_completed.setVisibility(View.VISIBLE);

                    }

                    //                    set list in adapter even have data or no data
                    adapter_completed = new List_Complete_Challenge_Adapter(getActivity(),
                            complete_challenge_list);
                    list_completed_challenges.setAdapter(adapter_completed);


                    if (complete_challenge_list.size() == 0){
                        list_completed_challenges.setVisibility(View.GONE);
                        txt_empty_completed.setVisibility(View.VISIBLE);
                    }else if (complete_challenge_list.size()> 0){
                        list_completed_challenges.setVisibility(View.VISIBLE);
                        txt_empty_completed.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                txt_empty_completed.setVisibility(View.VISIBLE);

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
