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

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.MainActivity;
import banyan.com.cashinow.utils.AppConfig;
import dmax.dialog.SpotsDialog;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jo on 9/7/2017.
 */

public class List_Payment_Completed_Adapter extends BaseAdapter implements View.OnClickListener {

    private static final int TYPE_MAX_COUNT = 2, TYPE_NORMAL_PAY = 1, TYPE_DISCOUNT_PAY = 2;

    private final String str_user_id;
    private Activity activity;
    private Context context;
    private LinearLayout singleMessageContainer;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    private RequestQueue queue;

    private SpotsDialog dialog;

    private boolean bol_share_1 = false, bol_share_2 = false, bol_share_3 = false, bol_share_4 = false, bol_share_5 = false;


    public List_Payment_Completed_Adapter(Activity a, ArrayList<HashMap<String, String>> d, String str_user_id) {
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

        ViewHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_payment_completed_view, null);

            holder = new ViewHolder();
//        find view by id
            holder.cv_normal = (CardView) convertView.findViewById(R.id.cv_normal);
            holder.txt_message = (TextView) convertView.findViewById(R.id.txt_message);
            holder.img_profile_pic = (ImageView) convertView.findViewById(R.id.list_view_win_img_profile_pic);
            holder.txt_win_player_name = (TextView) convertView.findViewById(R.id.txt_win_player_name);
            holder.txt_win_account_details = (TextView) convertView.findViewById(R.id.txt_win_account_details);
            holder.txt_win_amount = (TextView) convertView.findViewById(R.id.txt_win_amount);
            holder.btn_pay = (Button) convertView.findViewById(R.id.btn_pay);
            holder.btn_pay.setTag(position);

            /****************
             *  special view
             * ***************/

//    find view by id

            holder.cv_special = (CardView) convertView.findViewById(R.id.cv_special);
            holder.txt_special_message = (TextView) convertView.findViewById(R.id.txt_special_message);
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

        } else {

            holder = (ViewHolder) convertView.getTag();

            holder.btn_pay.setTag(position);
            holder.btn_special_pay.setTag(position);
            holder.btn_whatsapp_pay.setTag(position);

            holder.txt_share_1.setOnClickListener(this);
            holder.txt_share_2.setOnClickListener(this);
            holder.txt_share_3.setOnClickListener(this);
            holder.txt_share_4.setOnClickListener(this);
            holder.txt_share_5.setOnClickListener(this);

        }


        System.out.println("### selected row position "+position);
//        get win details
        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(position);
        System.out.println("### get data position "+position);
        String str_betting_id = result.get(MainActivity.TAG_PAYMENT_BETTING_ID);
        String str_event_title = result.get(MainActivity.TAG_PAYMENT_EVENT_TITLE);
        String str_paid_to = result.get(MainActivity.TAG_PAYMENT_PAID_TO);
        String str_paid_to_photo = result.get(MainActivity.TAG_PAYMENT_PAID_TO_PHOTO);
        String str_admin_account_detail = result.get(MainActivity.TAG_PAYMENT_ACCOUNT_DETAILS);
        String str_winning_amount = result.get(MainActivity.TAG_PAYMENT_WINNIG_AMOUNT);
        String str_amount = result.get(MainActivity.TAG_PAYMENT_AMOUNT);
        String str_received_from = result.get(MainActivity.TAG_PAYMENT_RECEIVED_FROM);
        String str_received_from_photo = result.get(MainActivity.TAG_PAYMENT_RECEIVED_FROM_PHOTO);

        int int_admin_share = 0;
        int_admin_share = Integer.parseInt(str_amount);

//        set win details
        holder.txt_message.setText("Superb! You Received a Payment " + str_winning_amount + "INR");
        Picasso.with(activity)
                .load(str_received_from_photo)
                .placeholder(R.mipmap.ic_challenge)
                .into(holder.img_profile_pic);
        holder.txt_win_player_name.setText(str_received_from);
        holder.txt_win_account_details.setText(str_admin_account_detail);
        holder.txt_win_amount.setText(int_admin_share + " ( 10% From Amount ) ");

//        action
        holder.btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new SpotsDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
                queue = Volley.newRequestQueue(activity);
                Function_Pay_Commission(v);

            }
        });


        //action
        //to show special pay only is admin share is 1 rs
        if (int_admin_share == 1) {
            holder.cv_special.setVisibility(View.VISIBLE);
            holder.cv_normal.setVisibility(View.GONE);
        } else {
            holder.cv_special.setVisibility(View.GONE);
            holder.cv_normal.setVisibility(View.VISIBLE);
        }

        holder.btn_special_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new SpotsDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
                queue = Volley.newRequestQueue(activity);
                Function_Pay_Commission(v);

            }
        });

        holder.btn_whatsapp_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bol_share_1 || !bol_share_2 || !bol_share_3 || !bol_share_4 || !bol_share_5) {

                    TastyToast.makeText(activity, "Some WhatsApp Sharing Is Missing.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);

                } else {

                    dialog = new SpotsDialog(activity);
                    dialog.setCancelable(false);
                    dialog.show();
                    queue = Volley.newRequestQueue(activity);
                    Function_Pay_Commission(v);

                }

            }
        });



        return  convertView;

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.txt_share_1) {

            bol_share_1 = true;
            Function_Share_In_Whatsapp();

        } else if (v.getId() == R.id.txt_share_2) {

            bol_share_2 = true;
            Function_Share_In_Whatsapp();

        } else if (v.getId() == R.id.txt_share_3) {

            bol_share_3 = true;
            Function_Share_In_Whatsapp();

        } else if (v.getId() == R.id.txt_share_4) {

            bol_share_4 = true;
            Function_Share_In_Whatsapp();

        } else if (v.getId() == R.id.txt_share_5) {

            bol_share_5 = true;
            Function_Share_In_Whatsapp();

        }

    }

    public void Function_Share_In_Whatsapp() {

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


    /***************************
     * GET Filter Challenge
     ***************************/

    public void Function_Pay_Commission(View v) {

        final Integer index = (Integer) v.getTag();
        System.out.println("### delete row position index "+index);
        HashMap<String, String> result = new HashMap<String, String>();
        result = data.get(index);
        final String str_betting_id = result.get(MainActivity.TAG_PAYMENT_BETTING_ID);

        System.out.println("### AppConfig.url_pay_commision " + AppConfig.url_pay_commision);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_pay_commision, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d(" ", "### " + response.toString());
                try {
                    System.out.println("### onResponse ");
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        new AlertDialog.Builder(activity)
                                .setTitle(R.string.app_name)
                                .setMessage("Commission Paid Successfully !")
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

                        data.remove(index.intValue());
                        notifyDataSetChanged();

                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Commission Not Paid, Try Again.", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

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

                params.put("user_id", str_user_id);
                params.put("betting_id", str_betting_id);

                System.out.println("### user_id " + str_user_id);
                System.out.println("### betting_id " + str_betting_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    private class ViewHolder {

        //        normal
        public CardView cv_normal;
        public TextView txt_message, txt_win_player_name, txt_win_account_details, txt_win_amount;
        public ImageView img_profile_pic;
        public Button btn_pay;

        //        special
        public CardView cv_special;
        public TextView txt_special_message, txt_whatsapp_message;
        public Button btn_special_pay, btn_whatsapp_pay;
        public TextView txt_share_1, txt_share_2, txt_share_3, txt_share_4, txt_share_5;

    }
}