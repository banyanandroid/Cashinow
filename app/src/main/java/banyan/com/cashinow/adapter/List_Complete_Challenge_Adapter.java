package banyan.com.cashinow.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.Activity_Profile;
import banyan.com.cashinow.fragment.Fragment_Completed_Challenge;
import banyan.com.cashinow.utils.SessionManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jo on 9/7/2017.
 */

public class List_Complete_Challenge_Adapter extends BaseAdapter {
    private Activity activity;
    private Context context;
    private LinearLayout singleMessageContainer;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    private String[] bgColors;

    public List_Complete_Challenge_Adapter(Activity a, ArrayList<HashMap<String, String>> d) {
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

        ViewHolder holder ;
        if (convertView == null){

            convertView = inflater.inflate(R.layout.list_complete_challenge_view, null);

            holder = new ViewHolder();
            /*
             * find view by id
             * */
            holder.list_prof_img = (ImageView) convertView.findViewById(R.id.list_view_img_profile_pic);
            holder.list_prof_name = (TextView) convertView.findViewById(R.id.list_view_txt_name);
            holder.list_prof_amount = (TextView) convertView.findViewById(R.id.list_view_txt_amount);
            holder.list_prof_status = (TextView) convertView.findViewById(R.id.list_view_txt_status);

            holder.list_op_prof_img = (ImageView) convertView.findViewById(R.id.list_view_op_img_profile_pic);
            holder.list_op_prof_name = (TextView) convertView.findViewById(R.id.list_view_op_txt_name);

            holder.list_prof_event_name = (TextView) convertView.findViewById(R.id.list_view_txt_event_name);
            holder.list_view_txt_payment_status = (TextView) convertView.findViewById(R.id.list_view_txt_payment_status);

            holder.lo_challenger = (LinearLayout) convertView.findViewById(R.id.lo_challenger);
            holder.lo_opponent = (LinearLayout) convertView.findViewById(R.id.lo_opponent);
            holder.lo_payment_status = (LinearLayout) convertView.findViewById(R.id.lo_payment_status);


            convertView.setTag(holder);

        }else{

            holder = (ViewHolder)convertView.getTag();
        }



        /*
         * get challenge details
         * */
        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);

        String str_event_title = result.get(Fragment_Completed_Challenge.TAG_CHALLENGE_TITLE);
        String str_amount = result.get(Fragment_Completed_Challenge.TAG_CHALLENGE_AMOUNT);
        String str_gain = result.get(Fragment_Completed_Challenge.TAG_CHALLENGE_GAIN);
        final String str_opponent_id = result.get(Fragment_Completed_Challenge.TAG_OPPONENT_USER_ID);
        String str_op_username = result.get(Fragment_Completed_Challenge.TAG_OPPONENT_NAME);
        String op_impath = result.get(Fragment_Completed_Challenge.TAG_OPPONENT_PHOTO);
        String str_status = result.get(Fragment_Completed_Challenge.TAG_STATUS);
        String str_winner = result.get(Fragment_Completed_Challenge.TAG_CHALLENGE_WINNER);
        String str_loser = result.get(Fragment_Completed_Challenge.TAG_CHALLENGE_LOSER);
        String str_accuracy = result.get(Fragment_Completed_Challenge.TAG_ACCURACY);
        final  String str_payment_status = result.get(Fragment_Completed_Challenge.TAG_PAYMENT_STATUS);
        String str_challenge_name = result.get(Fragment_Completed_Challenge.TAG_CHALLENGE_CHALLENGE_NAME);

        /************************
         * get  current profile user details *
         * ***********************/
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity);

        String str_current_profile_name = sharedPreferences.getString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_NAME, "");
        String str_current_profile_photo = sharedPreferences.getString(Activity_Profile.TAG_PROFILE_CURRENT_PROFILE_USER_PHOTO, "");

        String str_challenger_name = str_current_profile_name;
        final String str_challenger_photo = str_current_profile_photo ;

        /************************
         * set challenge details *
         * ***********************/
        holder.list_prof_name.setText(str_challenger_name);
        holder.list_prof_amount.setText(str_gain);
        holder.list_prof_status.setText(str_status);
        holder.list_op_prof_name.setText(str_op_username);
        holder.list_prof_event_name.setText(str_event_title + " ");
        holder.list_view_txt_payment_status.setText(str_payment_status);

        if (str_challenger_photo.isEmpty()){
            Picasso.with(activity)
                    .load(R.mipmap.ic_challenge)
                    .placeholder(R.mipmap.ic_challenge)
                    .into(holder.list_prof_img);
        }else{
            Picasso.with(activity)
                    .load(str_challenger_photo)
                    .placeholder(R.mipmap.ic_challenge)
                    .into(holder.list_prof_img);
        }

        Picasso.with(activity)
                .load(op_impath)
                .placeholder(R.mipmap.ic_challenge)
                .into(holder.list_op_prof_img);

        if (str_payment_status.equals("Payment Successful")) {

            holder.list_view_txt_payment_status.setBackground(activity.getResources().getDrawable(R.drawable.bg_green));
            holder.list_view_txt_payment_status.setTextColor(Color.WHITE);

        } else if (str_payment_status.equals("Payment Pending")) {

            holder.list_view_txt_payment_status.setBackground(activity.getResources().getDrawable(R.drawable.bg_yellow));
            holder.list_view_txt_payment_status.setTextColor(Color.WHITE);

        } else if (str_payment_status.equals("Challenge Draw")) {

            holder.list_view_txt_payment_status.setBackground(activity.getResources().getDrawable(R.drawable.bg_grey));
            holder.list_view_txt_payment_status.setTextColor(Color.WHITE);

        }

        return convertView;

    }

    private class ViewHolder {

        public ImageView list_prof_img, list_op_prof_img;
        public TextView list_prof_name, list_prof_amount, list_prof_status, list_op_prof_name, list_prof_event_name, list_view_txt_payment_status;
        public LinearLayout lo_challenger, lo_opponent, lo_payment_status;

    }
}