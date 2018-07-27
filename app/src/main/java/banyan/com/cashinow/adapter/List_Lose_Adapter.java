package banyan.com.cashinow.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.util.TreeSet;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.utils.AppConfig;
import dmax.dialog.SpotsDialog;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jo on 9/7/2017.
 */

public class List_Lose_Adapter extends BaseAdapter implements View.OnClickListener {
    private static final int TYPE_MAX_COUNT = 2 ,TYPE_NORMAL_PAY = 1 ,TYPE_DISCOUNT_PAY = 2;
    private final String str_user_id;
    private Activity activity;
    private Context context;
    private LinearLayout singleMessageContainer;

    private ArrayList<HashMap<String, String>> data;
    private TreeSet<Integer> mSpecial_Payment = new TreeSet<Integer>();
    private static LayoutInflater inflater = null;

    private RequestQueue queue;

    private SpotsDialog dialog;


    private boolean bol_share_1 = false, bol_share_2 = false, bol_share_3 = false, bol_share_4 = false, bol_share_5 = false;


    //hold the view
    public class ViewHolder {

        //        normal
        public CardView cardview_lose_normal;
        public ImageView img_prof_img;
        public TextView txt_player_name, txt_event_title, txt_account_details, txt_challenge_amount, txt_amount;
        public Button btn_payment;

        //    special
        public CardView card_lose_view_special;
        public TextView  txt_special_message, txt_special_player_name, txt_special_event_title, txt_special_account_details, txt_special_challenge_amount, txt_special_amount, txt_whatsapp_message;
        public ImageView img_special_prof_img;
        public Button  btn_special_pay, btn_whatsapp_pay;
        public TextView txt_share_1, txt_share_2, txt_share_3, txt_share_4, txt_share_5;
    }

    public List_Lose_Adapter(Activity a, ArrayList<HashMap<String, String>> d, String str_user_id) {
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

//        set layout for convertView
//        from layout inflator
//        or from existing convert view
        if (convertView == null){

            convertView = inflater.inflate(R.layout.list_lose_view, null);

            //        find view by id
//        normal
            holder = new ViewHolder();

            holder.cardview_lose_normal = (CardView) convertView.findViewById(R.id.cardview_lose_normal);
            holder.img_prof_img = (ImageView) convertView.findViewById(R.id.list_view_lose_img_profile_pic);
            holder.txt_player_name = (TextView) convertView.findViewById(R.id.txt_lose_player_name);
            holder.txt_event_title = (TextView) convertView.findViewById(R.id.txt_lose_event_title);
            holder.txt_account_details = (TextView) convertView.findViewById(R.id.txt_lose_account_details);
            holder.txt_challenge_amount = (TextView) convertView.findViewById(R.id.txt_lose_challenge_amount);
            holder.txt_amount = (TextView) convertView.findViewById(R.id.txt_lose_amount);
            holder.btn_payment = (Button) convertView.findViewById(R.id.btn_pay);
            holder.btn_payment.setTag(position);

            //        special

            holder.card_lose_view_special = (CardView) convertView.findViewById(R.id.card_lose_view_special);
            holder.txt_special_message = (TextView) convertView.findViewById(R.id.txt_special_message);
            holder.img_special_prof_img = (ImageView) convertView.findViewById(R.id.list_view_special_img_profile_pic);
            holder.txt_special_player_name = (TextView) convertView.findViewById(R.id.txt_special_player_name);
            holder.txt_special_event_title = (TextView) convertView.findViewById(R.id.txt_special_event_title);
            holder.txt_special_account_details = (TextView) convertView.findViewById(R.id.txt_special_account_details);
            holder.txt_special_challenge_amount = (TextView) convertView.findViewById(R.id.txt_special_challenge_amount);
            holder.txt_special_amount = (TextView) convertView.findViewById(R.id.txt_special_amount);
            holder.btn_special_pay = (Button) convertView.findViewById(R.id.btn_special_pay);
            holder.txt_whatsapp_message = (TextView) convertView.findViewById(R.id.txt_whatsapp_message);

            holder.txt_share_1 = (TextView) convertView.findViewById(R.id.txt_share_1);
            holder.txt_share_2 = (TextView) convertView.findViewById(R.id.txt_share_2);
            holder.txt_share_3 = (TextView) convertView.findViewById(R.id.txt_share_3);
            holder.txt_share_4 = (TextView) convertView.findViewById(R.id.txt_share_4);
            holder.txt_share_5 = (TextView) convertView.findViewById(R.id.txt_share_5);

            holder.btn_whatsapp_pay = (Button) convertView.findViewById(R.id.btn_special_whatsapp_pay);
            holder.btn_special_pay.setTag(position);
            holder.btn_whatsapp_pay.setTag(position);

            holder.txt_share_1.setOnClickListener(this);
            holder.txt_share_2.setOnClickListener(this);
            holder.txt_share_3.setOnClickListener(this);
            holder.txt_share_4.setOnClickListener(this);
            holder.txt_share_5.setOnClickListener(this);

            convertView.setTag(holder);

        }else{

            holder = (ViewHolder) convertView.getTag();

            holder.btn_payment.setTag(position);
            holder.btn_special_pay.setTag(position);
            holder.btn_whatsapp_pay.setTag(position);

            holder.txt_share_1.setOnClickListener(this);
            holder.txt_share_2.setOnClickListener(this);
            holder.txt_share_3.setOnClickListener(this);
            holder.txt_share_4.setOnClickListener(this);
            holder.txt_share_5.setOnClickListener(this);

        }

        System.out.println("### get selected position "+position);

        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);
        final String str_challenge_amount = result.get(MainActivity.TAG_LOSE_CHALLENGE_AMOUNT);
        String str_amt = result.get(MainActivity.TAG_LOSE_AMOUNT);

        int int_challenge_amount = Integer.parseInt(str_challenge_amount);
        int int_amount = Integer.parseInt(str_amt);


//        get lose details
        String str_betting_id = result.get(MainActivity.TAG_LOSE_BETTING_ID);
        String str_event_title = result.get(MainActivity.TAG_LOSE_EVENT_TITLE);
        String str_player_image = result.get(MainActivity.TAG_LOSE_PLAYER_IMAGE);
        String str_player_name = result.get(MainActivity.TAG_LOSE_PLAYER_NAME);
        String str_account_details = result.get(MainActivity.TAG_LOSE_ACCOUNT_DETAILS);
        String str_amount = result.get(MainActivity.TAG_LOSE_AMOUNT);

//        set lose details
        Picasso.with(activity)
                .load(str_player_image)
                .placeholder(R.mipmap.ic_challenge)
                .into(holder.img_prof_img);
        holder.txt_player_name.setText(str_player_name);
        holder.txt_event_title.setText(str_event_title);
        holder.txt_account_details.setText(str_account_details);
        holder.txt_challenge_amount.setText(str_challenge_amount + " INR");
        holder.txt_amount.setText(str_amount + " INR");

//        action
        //to show normal pay or specail pay base on challnege and amount difference
        if ( Integer.parseInt(str_challenge_amount) == Integer.parseInt(str_amount) ) {
            holder.cardview_lose_normal.setVisibility(View.VISIBLE);
            holder.card_lose_view_special.setVisibility(View.GONE);
        }
        else {
            holder.cardview_lose_normal.setVisibility(View.GONE);
            holder.card_lose_view_special.setVisibility(View.VISIBLE);
        }

        holder.btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View convertView) {

                dialog = new SpotsDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
                queue = Volley.newRequestQueue(activity);
                Function_Make_Payment(convertView);

            }
        });


        /********************
         * special view
         * ******************/

//        set details
        holder.txt_special_message.setText("You Lost a Challenge For " + str_amount + " INR.");
        holder.txt_whatsapp_message.setText("Share on Whatsapp For 5 Members or Group and Pay Only " + str_amount + " INR");
        Picasso.with(activity)
                .load(str_player_image)
                .placeholder(R.mipmap.ic_challenge)
                .into(holder.img_special_prof_img);
        holder.txt_special_player_name.setText(str_player_name);
        holder.txt_special_event_title.setText(str_event_title);
        holder.txt_special_account_details.setText(str_account_details);
        holder.txt_special_challenge_amount.setText(str_challenge_amount + " INR");
        holder.txt_special_amount.setText(str_amount + " INR");

        //action
        holder.btn_special_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View convertView) {

                dialog = new SpotsDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
                queue = Volley.newRequestQueue(activity);
                Function_Make_Payment(convertView);

            }
        });


        holder.btn_whatsapp_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( !bol_share_1 || !bol_share_2 || !bol_share_3 || !bol_share_4 || !bol_share_5 ){

                    TastyToast.makeText(activity, "Some WhatsApp Sharing Is Missing.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                }else{

                    dialog = new SpotsDialog(activity);
                    dialog.setCancelable(false);
                    dialog.show();
                    queue = Volley.newRequestQueue(activity);
                    Function_Make_Payment(v);

                }



            }
        });


        return convertView;

    }

    /***************************
     * GET Filter Challenge
     **************************/

    public void Function_Make_Payment(View v) {

       final Integer index = (Integer) v.getTag();
        System.out.println("###  delete row index "+index);

        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(index);
       final String str_betting_id = result.get(MainActivity.TAG_LOSE_BETTING_ID);

        System.out.println("### AppConfig.url_make_payment "+ AppConfig.url_make_payment);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_make_payment, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d("### onResponse ", "### "+response.toString());
                try {
                    System.out.println("### onResponse ");
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        new AlertDialog.Builder(activity)
                                .setTitle(R.string.app_name)
                                .setMessage("Payment Done Successfully !")
                                .setIcon(R.mipmap.app_icon)
                                .setCancelable(false)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,

                                                                int which) {
                                                // TODO Auto-generated method stub

                                            }

                                        }).show();

//                        delete row in list and update list
                        data.remove(index.intValue());
                        notifyDataSetChanged();

                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Payment Not Completed, Try Again.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

                    }

                    dialog.dismiss();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("### onErrorResponse ");
                dialog.dismiss();
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_user_id );
                params.put("betting_id", str_betting_id );

                System.out.println("### user_id " + str_user_id);
                System.out.println("### betting_id " + str_betting_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }



    public void  Function_Share_In_Whatsapp(){

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, AppConfig.url_app);
        try {
            activity.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            TastyToast.makeText(activity, "Whatsapp have not been installed.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
        }

    }

    @Override
    public void onClick(View v) {

        if (v.getId()== R.id.txt_share_1){

            bol_share_1 = true ;
            Function_Share_In_Whatsapp();

        } else if (v.getId()== R.id.txt_share_2){

            bol_share_2 = true ;
            Function_Share_In_Whatsapp();

        }else if (v.getId()== R.id.txt_share_3){

            bol_share_3 = true ;
            Function_Share_In_Whatsapp();

        }else if (v.getId()== R.id.txt_share_4){

            bol_share_4 = true ;
            Function_Share_In_Whatsapp();

        }else if (v.getId()== R.id.txt_share_5){

            bol_share_5 = true ;
            Function_Share_In_Whatsapp();

        }

    }


}
