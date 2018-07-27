package banyan.com.cashinow.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.Activity_Profile;
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.fragment.Fragment_Active_Challenge;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.SessionManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jo on 9/7/2017.
 */

public class List_Active_Challenge_Adapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    private LinearLayout singleMessageContainer;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    private String[] bgColors;

    public List_Active_Challenge_Adapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
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

        View v = convertView;

        ViewHolder holder;

        if (convertView == null) {

            convertView  = inflater.inflate(R.layout.list_active_challenge_view, null);

            /*******************
             * find view by id  *
             * *****************/
            holder = new ViewHolder();
            holder.list_prof_img = (ImageView) convertView.findViewById(R.id.list_view_img_profile_pic);
            holder.list_prof_name = (TextView) convertView.findViewById(R.id.list_view_txt_name);
            holder.list_prof_amount = (TextView) convertView.findViewById(R.id.list_view_txt_amount);

            holder.list_op_prof_img = (ImageView) convertView.findViewById(R.id.list_view_op_img_profile_pic);
            holder.list_op_prof_name = (TextView) convertView.findViewById(R.id.list_view_op_txt_name);

            holder.list_prof_event_name = (TextView) convertView.findViewById(R.id.list_view_txt_event_name);

            convertView.setTag(holder);

        }else{

            holder = (ViewHolder)convertView.getTag();

        }



        /************************
         * get challenge details *
         * ***********************/
        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);

        String str_event_title = result.get(Fragment_Active_Challenge.TAG_CHALLENGE_TITLE);
        String str_amount = result.get(Fragment_Active_Challenge.TAG_CHALLENGE_AMOUNT);
        String str_op_username = result.get(Fragment_Active_Challenge.TAG_OPPONENT_NAME);
        String op_impath = result.get(Fragment_Active_Challenge.TAG_OPPONENT_PHOTO);
        final String str_opponent_id = result.get(Fragment_Active_Challenge.TAG_OPPONENT_USER_ID);

        /************************
         * get  current profile user details *
         * ***********************/
        //        get current profile user id
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);

        String str_current_profile_name = sharedPreferences.getString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_NAME, "");
        String str_current_profile_photo = sharedPreferences.getString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_PHOTO, AppConfig.url_app_image);

        String str_user_name = str_current_profile_name;

        /***********************
         * set challege details *
         * **********************/

        holder.list_prof_name.setText(str_user_name);
        holder.list_prof_amount.setText(str_amount);
        holder.list_op_prof_name.setText(str_op_username);
        holder.list_prof_event_name.setText(str_event_title + " ");

        if (str_current_profile_photo.isEmpty()){ // is image url is empty, load default icon{
            Picasso.with(activity)
                    .load(R.mipmap.ic_challenge)
                    .placeholder(R.mipmap.ic_challenge)
                    .into(holder.list_prof_img);
        }else{
            Picasso.with(activity)
                .load(str_current_profile_photo)
                .placeholder(R.mipmap.ic_challenge)
                .into(holder.list_prof_img);
        }

        Picasso.with(activity)
                .load(op_impath)
                .placeholder(R.mipmap.ic_challenge)
                .into(holder.list_op_prof_img);


        return convertView;

    }

    private class ViewHolder {

        public ImageView list_prof_img, list_op_prof_img;
        public TextView list_prof_name, list_prof_amount, list_op_prof_name, list_prof_event_name;

    }
}