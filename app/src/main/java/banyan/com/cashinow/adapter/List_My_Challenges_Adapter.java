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

import java.util.ArrayList;
import java.util.HashMap;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.Activity_Profile;
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.utils.SessionManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jo on 9/7/2017.
 */

public class List_My_Challenges_Adapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    private LinearLayout singleMessageContainer;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    private String[] bgColors;

    public List_My_Challenges_Adapter(Activity a, ArrayList<HashMap<String, String>> d) {
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

        ViewHolder holder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_event_view, null);

            holder = new ViewHolder();

            holder.list_prof_img = (ImageView) convertView.findViewById(R.id.list_view_img_profile_pic);
            holder.list_prof_name = (TextView) convertView.findViewById(R.id.list_view_txt_name);
            holder.list_prof_event_title = (TextView) convertView.findViewById(R.id.list_view_txt_event_title);
            holder.list_prof_amount = (TextView) convertView.findViewById(R.id.list_view_txt_amount);

            convertView.setTag(holder);
        }else{

            holder = (ViewHolder)convertView.getTag();

        }




        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);

        String str_user_name = result.get(MainActivity.TAG_CHALLENGE_USERNAME);
        String str_event_title = result.get(MainActivity.TAG_CHALLENGE_TITLE);
        String str_event_name = result.get(MainActivity.TAG_CHALLENGE_CHALLENGE_NAME);
        String str_date = result.get(MainActivity.TAG_CHALLENGE_DATE);
        String str_amount = result.get(MainActivity.TAG_CHALLENGE_AMOUNT);
        final String str_user_id = result.get(MainActivity.TAG_CHALLENGE_USER_ID);
        String str_challenger_photo = result.get(MainActivity.TAG_CHALLENGE_PHOTO);

        holder.list_prof_name.setText(str_user_name);
        holder.list_prof_event_title.setText(str_event_title + " ");
        holder.list_prof_amount.setText(str_amount);

        Picasso.with(activity)
                .load(str_challenger_photo)
                .placeholder(R.mipmap.ic_challenge)
                .into(holder.list_prof_img);

        //action
        holder.list_prof_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // know login user
                SessionManager sessionManager = new SessionManager(activity);
                HashMap<String, String> user = sessionManager.getUserDetails();
                String login_user_id = user.get(SessionManager.KEY_ID);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(activity);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (login_user_id.equals(str_user_id)){
                    editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_MY_PROFILE);
                    editor.putString(Activity_Profile.TAG_PROFILE_USER_ID, str_user_id);
                }else{
                    editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_OTHER_PROFILE);
                    editor.putString(Activity_Profile.TAG_PROFILE_USER_ID, str_user_id);
                }

                editor.commit();

                Intent intent = new Intent(activity, Activity_Profile.class);
                activity.startActivity(intent);
            }
        });

        holder.list_prof_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // know login user
                SessionManager sessionManager = new SessionManager(activity);
                HashMap<String, String> user = sessionManager.getUserDetails();
                String login_user_id = user.get(SessionManager.KEY_ID);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(activity);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (login_user_id.equals(str_user_id)){
                    editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_MY_PROFILE);
                    editor.putString(Activity_Profile.TAG_PROFILE_USER_ID, str_user_id);
                }else{
                    editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_OTHER_PROFILE);
                    editor.putString(Activity_Profile.TAG_PROFILE_USER_ID, str_user_id);
                }

                editor.commit();

                Intent intent = new Intent(activity, Activity_Profile.class);
                activity.startActivity(intent);

            }
        });

        return convertView;

    }

    private class ViewHolder {

        public ImageView list_prof_img;
        public TextView list_prof_name, list_prof_event_title, list_prof_amount;

    }
}