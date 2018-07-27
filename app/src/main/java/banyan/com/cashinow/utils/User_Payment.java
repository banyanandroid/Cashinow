package banyan.com.cashinow.utils;

import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.adapter.List_Challenge_Adapter;

public class User_Payment {


   /* public int Function_User_Pay(){

        *//***************************
         * GET Active Challenges
         ***************************//*


            String tag_json_obj = "json_obj_req";
            System.out.println("### AppConfig.url_list_challenges "+AppConfig.url_list_challenges);
            StringRequest request = new StringRequest(Request.Method.POST,
                    AppConfig.url_list_challenges, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d("User Pay", response.toString());
                    try {
                        JSONObject obj = new JSONObject(response);
                        int success = obj.getInt("status");

                        if (success == 1) {


                        } else if (success == 0) {

                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {


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
*/

}
