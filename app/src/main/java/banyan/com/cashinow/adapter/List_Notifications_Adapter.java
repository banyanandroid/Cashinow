package banyan.com.cashinow.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.Activity_Notifications;
import banyan.com.cashinow.activity.Activity_Profile;
import banyan.com.cashinow.utils.AppConfig;
import dmax.dialog.SpotsDialog;

/**
 * Created by Jo on 9/7/2017.
 */

public class List_Notifications_Adapter extends BaseAdapter {

    private final Context context;
    private Activity activity;
    private LinearLayout singleMessageContainer;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    private String[] bgColors;
    private String str_session_id, str_challenge_id;

    private SpotsDialog dialog_clear_notification;

    private RequestQueue queue;

    public List_Notifications_Adapter(Context context, Activity a, ArrayList<HashMap<String, String>> d, String str_session_id) {
        this.context = context;
        activity = a;
        this.str_session_id = str_session_id;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_notification, null);

            holder = new ViewHolder();

//        find view by id
            holder.list_msg = (TextView) convertView.findViewById(R.id.notification_msg);
            holder.list_name = (TextView) convertView.findViewById(R.id.notification_event_name);
            holder.list_amount = (TextView) convertView.findViewById(R.id.notification_amount);
            holder.img_view_clear = (ImageView) convertView.findViewById(R.id.img_view_clear);
            holder.img_view_clear.setTag(position);
            convertView.setTag(holder);

        }else{

            holder = (ViewHolder)convertView.getTag();
            holder.img_view_clear.setTag(position);

        }

//       get notification details
        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);

        String str_notification_id = result.get(Activity_Notifications.TAG_NOTIFICATION_NOTIFICATION_ID);
        String str_type = result.get(Activity_Notifications.TAG_NOTIFICATION_TYPE);
        String str_msg = result.get(Activity_Notifications.TAG_NOTIFICATION_MSG);
        String str_name = result.get(Activity_Notifications.TAG_NOTIFICATION_EVENT_TITLE);
        String str_amount = result.get(Activity_Notifications.TAG_NOTIFICATION_AMOUNT);
        String str_challenge_id = result.get(Activity_Notifications.TAG_NOTIFICATION_CHALLENGE_ID);

//        set notification details
        holder.list_msg.setText("" + str_msg);
        holder.list_name.setText("" + str_name );
        holder.list_amount.setText("" + str_amount);

        holder.img_view_clear.setTag(position);
        System.out.println("### selected ");

//        action
        holder.img_view_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final Integer  position =(Integer)v.getTag();
//       get notification details
                HashMap<String, String> result = new HashMap<String, String>();
                result = data.get(position);

                String str_notification_id = result.get(Activity_Notifications.TAG_NOTIFICATION_NOTIFICATION_ID);



                dialog_clear_notification = new SpotsDialog(activity);
                dialog_clear_notification.setCancelable(false);
                dialog_clear_notification.show();

                queue = Volley.newRequestQueue(activity);
                Function_Clear_Notification(str_notification_id);

            }
        });

//if notification type 5 then hide rupee symbol

        if (str_type.equals("5")){
            holder.list_amount.setVisibility(View.GONE);
        }else{
            holder.list_amount.setVisibility(View.VISIBLE);
        }
        return convertView;

    }

    /***************************
     * Clear Notification
     **************************
     * */

    public void Function_Clear_Notification(final String notification_id) {

        System.out.println("### AppConfig.url_clear_notifications "+AppConfig.url_clear_notifications);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_clear_notifications, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("", "### "+response.toString());

                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        TastyToast.makeText(activity, "Notification Cleared Successfully.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                        dialog_clear_notification.dismiss();

                        ((Activity_Notifications)context).Function_Refresh_Notification_list();

                    } else if (success == 0) {

                        TastyToast.makeText(activity, "Notification Not Cleared, Try Again Later.", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
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
//                TastyToast.makeText(activity, "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id);
                params.put("notification_id", notification_id);

                System.out.println("### USER ID : " +str_session_id) ;
                System.out.println("### notification_id  : " +notification_id) ;
                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    private class ViewHolder {

        public TextView list_msg,list_name, list_amount;
        public ImageView img_view_clear;

    }
}