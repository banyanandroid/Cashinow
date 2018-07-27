package banyan.com.cashinow.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.app.App;
import banyan.com.cashinow.fragment.Fragment_Create_Challenge;
import banyan.com.cashinow.fragment.Fragment_My_Challenge;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.NotificationUtils;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;

public class Activity_Challenge extends AppCompatActivity {

    private Toolbar mToolbar;
    private RelativeLayout notification_Count,notification_batch;
    private TextView tv_notification;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int int_items = 2;

//    menu
    Button btn_bell;

    //    notification
    SpotsDialog dialog_notification;

//    session
    private String str_session_id = "";
    private RequestQueue  queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Challenge");
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        /**
         *Inflate tab_layout and setup Views.
         */
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //get session details
        SessionManager session = new SessionManager(getApplicationContext());

        session.checkLogin();
        session.check_need_to_pay();
        HashMap<String, String> user = session.getUserDetails();
        str_session_id = user.get(SessionManager.KEY_ID);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

//        get notification count
        try{

            queue = Volley.newRequestQueue(this);
            dialog_notification = new SpotsDialog(Activity_Challenge.this);
            dialog_notification.setCancelable(false);
            dialog_notification.show();
            Function_Get_Notification();

        }catch (Exception e){

        }


    }

    /**********************************
     * Main Menu
     *********************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item1 = menu.findItem(R.id.action_notification);
        MenuItemCompat.setActionView(item1, R.layout.notification_update_count_layout);
        notification_Count = (RelativeLayout) MenuItemCompat.getActionView(item1);
        notification_batch = (RelativeLayout) MenuItemCompat.getActionView(item1);
        tv_notification = (TextView) notification_Count.findViewById(R.id.badge_notification_2);
        btn_bell = (Button) notification_Count.findViewById(R.id.button2);

        btn_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Notifications.class);
                startActivity(i);
            }
        });
        tv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Notifications.class);
                startActivity(i);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_user_profile) {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(Activity_Challenge.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_MY_PROFILE);

            editor.commit();

            Intent i = new Intent(getApplicationContext(), Activity_Profile.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                // case 0 : return new Tab_MySchedulee();
                case 0 : return new Fragment_Create_Challenge();
                case 1 : return new Fragment_My_Challenge();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "CREATE NEW CHALLENGE";
                case 1 :
                    return "MY CHALLENGES";
            }
            return null;
        }
    }


    /***************************
     * GET Active Challenges
     ***************************/

    public void Function_Get_Notification() {
        System.out.println("###  AppConfig.url_list_notifications " + AppConfig.url_list_notifications) ;
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_notifications, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("###  onResponse");
                Log.d("", "### "+response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");
                        arr.length();

//                        set notification count
                        tv_notification.setVisibility(View.VISIBLE);
                        tv_notification.setText(""+arr.length());

                        dialog_notification.dismiss();
                    } else if (success == 0) {

                        tv_notification.setVisibility(View.GONE);
                        dialog_notification.dismiss();
                    }

                    dialog_notification.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog_notification.dismiss();
                tv_notification.setVisibility(View.GONE);
                System.out.println("### onErrorResponse ") ;

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_session_id);

                System.out.println("### USER ID : " +str_session_id) ;

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();

    }

}
