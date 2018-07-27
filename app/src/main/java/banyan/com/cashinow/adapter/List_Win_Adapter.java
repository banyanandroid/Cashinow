package banyan.com.cashinow.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;

import static banyan.com.cashinow.utils.AppConfig.url_app_image;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jo on 9/7/2017.
 */

public class List_Win_Adapter extends BaseAdapter {
    private final String str_user_id;
    private Activity activity;
    private Context context;
    private LinearLayout singleMessageContainer;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    private RequestQueue queue;

    private SpotsDialog dialog, dialog_close_win;


    public List_Win_Adapter(Activity a, ArrayList<HashMap<String, String>> d, String str_user_id) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.str_user_id = str_user_id;
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


        ViewHolder holder ;

        if (convertView == null){

            convertView = inflater.inflate(R.layout.list_win_view, null);

            holder = new ViewHolder();

//        find view by id
            holder.img_profile_pic = (ImageView) convertView.findViewById(R.id.list_view_win_img_profile_pic);
            holder.txt_win_event_title = (TextView) convertView.findViewById(R.id.txt_win_event_title);
            holder.txt_win_player_name = (TextView) convertView.findViewById(R.id.txt_win_player_name);
            holder.txt_win_event_amount = (TextView) convertView.findViewById(R.id.txt_win_event_amount);
            holder.txt_win_share_fb = (TextView) convertView.findViewById(R.id.txt_win_share_fb);
            holder.txt_win_share_fb.setTag(position);

            convertView.setTag(holder);

        }else{

            holder = (ViewHolder) convertView.getTag();
            holder.txt_win_share_fb.setTag(position);

        }

        //        get win details
        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);
        System.out.println("### get list row position "+position);
        String str_wining_popup_id = result.get(MainActivity.TAG_WIN_POPUP_ID);
        String str_win_opponent_name = result.get(MainActivity.TAG_WIN_OPPONENT_NAME);
        String str_win_opponent_photo = result.get(MainActivity.TAG_WIN_OPPONENT_PHOTO);
        String str_event_title = result.get(MainActivity.TAG_WIN_EVENT_TITLE);
        String str_amount = result.get(MainActivity.TAG_WIN_AMOUNT);
        String str_win_betting_amount = result.get(MainActivity.TAG_WIN_BETTING_AMOUNT);

//        set win details
        Picasso.with(activity)
                .load(str_win_opponent_photo)
                .placeholder(R.drawable.distributor)
                .into(holder.img_profile_pic);

//        set details
        holder.txt_win_event_title.setText(str_event_title);
        holder.txt_win_player_name.setText(str_win_opponent_name);
        holder.txt_win_event_amount.setText(str_amount +" INR");
        
        /*
        action
        */

        holder.txt_win_share_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer position = (Integer)v.getTag();
                System.out.println("### txt_win_share_fb position" +position);

                try{

                dialog = new SpotsDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
                queue = Volley.newRequestQueue(activity);
                Function_Fb_Share(position);

                dialog_close_win = new SpotsDialog(activity);
                dialog_close_win.setCancelable(false);
                dialog_close_win.show();
                queue = Volley.newRequestQueue(activity);
                Function_Close_Win_Popup(v);

                }catch (Exception e){

                }
            }
        });

        return convertView;

    }


    /***************************
     * GET Filter Challenge
     **************************
     * @param position*/

    public void Function_Fb_Share(Integer position) {

        dialog.dismiss();

//        get win details
        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);
        String str_win_opponent_name = result.get(MainActivity.TAG_WIN_OPPONENT_NAME);
        String str_win_betting_amount = result.get(MainActivity.TAG_WIN_BETTING_AMOUNT);

//        get login user details
        SessionManager sessionManager = new SessionManager(activity);
        HashMap<String, String>  user = sessionManager.getUserDetails();
        String str_user_name = user.get(SessionManager.KEY_USER);

        ShareDialog shareDialog = new ShareDialog(activity);  // initialize facebook shareDialog.
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Cashinow, I won a Challenge")
                .setImageUrl(Uri.parse(url_app_image))
                .setContentDescription( str_user_name +" Won Rs "+str_win_betting_amount+ " Vs "+str_win_opponent_name)
                .setContentUrl(Uri.parse(AppConfig.url_fb_share))
                .build();
        shareDialog.show(linkContent);  // Show facebook ShareDialog

    }

    /********************************
     *FUNCTION CLOSE WIN POPUP
     *********************************/
    private void Function_Close_Win_Popup(View v) {

        final Integer index = (Integer) v.getTag();
        System.out.println("### delete index "+index);
        //        get win details
        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(index);
       final String str_wining_popup_id = result.get(MainActivity.TAG_WIN_POPUP_ID);

        System.out.println("### AppConfig.url_close_win_popup "+AppConfig.url_close_win_popup);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_close_win_popup, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d("### List Win Adapter", "### "+response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {
                        dialog_close_win.dismiss();

                        try{
//                        clear win row in popup
                            data.remove(index.intValue());
                            notifyDataSetChanged();

                        }catch (Exception e){
                            System.out.println("### Exception "+e.getLocalizedMessage());
                        }

                    } else {

                        dialog_close_win.dismiss();
//                        TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                    }
                    dialog_close_win.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog_close_win.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("### onErrorResponse");
                dialog_close_win.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(MainActivity.TAG_WIN_CLOSE_USER_ID, str_user_id);
                params.put(MainActivity.TAG_WIN_CLOSE_POPUP_ID, str_wining_popup_id);

                System.out.println("### getParams "+MainActivity.TAG_WIN_CLOSE_USER_ID+" : " + str_user_id);
                System.out.println("### getParams "+MainActivity.TAG_WIN_CLOSE_POPUP_ID+" : " + str_wining_popup_id);

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

    private class ViewHolder {

        public ImageView img_profile_pic;
        public TextView txt_win_event_title, txt_win_player_name, txt_win_event_amount, txt_win_share_fb ;

    }

}