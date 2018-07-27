package banyan.com.cashinow.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;


public class Activity_FB_Profile_Webview extends AppCompatActivity {

    private Toolbar mToolbar;
    private WebView mWebView;
    private ProgressBar progressBar;

    SpotsDialog dialog;
    private RequestQueue queue;
    String str_session_data, str_session_id, str_fb_url = "www.facebook.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__fb_profile_webview);

//        find view by id
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Facebook Profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Activity_FB_Profile_Webview.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar_busmap);
        mWebView = (WebView) findViewById(R.id.webView1);


//        get session details
        SessionManager session = new SessionManager(getApplicationContext());

        session.checkLogin();
        session.is_need_to_pay();

        HashMap<String, String> user = session.getUserDetails();
        str_session_data = user.get(SessionManager.KEY_USER);
        str_session_id = user.get(SessionManager.KEY_ID);

//        get user fb profile url then start web view
        try{

            queue = Volley.newRequestQueue(Activity_FB_Profile_Webview.this);
            dialog = new SpotsDialog(Activity_FB_Profile_Webview.this);
            dialog.setCancelable(false);
            dialog.show();
            Function_Get_Profile_URL();

        }catch (Exception e){

        }

    }

    private void StartWebview(String str_url) {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(str_url);
        mWebView.setWebViewClient(new HelloWebViewClient());

    }


    /*
     * to get fb profile url to server
     * */
    public void Function_Get_Profile_URL() {

        System.out.println("### AppConfig.url_get_profile_url "+ AppConfig.url_get_profile_url);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_get_profile_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONObject obj_data = obj.getJSONObject("data");

                        str_fb_url = obj_data.getString(Activity_Login.TAG_LOGIN_USER_FB_PROFILE_URL);

                        System.out.println("### str_fb_url "+str_fb_url);
                        dialog.dismiss();

                        StartWebview(str_fb_url);

                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), "No Data Found !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                new AlertDialog.Builder(Activity_FB_Profile_Webview.this)
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
                                        Intent intent = new Intent(Activity_FB_Profile_Webview.this, MainActivity.class);
                                        startActivity(intent);

                                    }

                                }).show();

//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(Activity_Login.TAG_LOGIN_USER_USER_ID, str_session_id);

                System.out.println("### "+Activity_Login.TAG_LOGIN_USER_USER_ID+" : " + str_session_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    private class HelloWebViewClient extends WebViewClient {

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
            progressBar.setVisibility(View.GONE);
            loadError();
        }

        private void loadError() {

            String html = "<html><body><table width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">"
                    + "<tr>"
                    + "<td><div align=\"center\"><font color=\"#606060\" size=\"10pt\">No Internet Try Again Later!</font></div></td>"
                    + "</tr>" + "</table><html><body>";
            System.out.println("html " + html);

            String base64 = android.util.Base64.encodeToString(html.getBytes(),
                    android.util.Base64.DEFAULT);
            mWebView.loadData(base64, "text/html; charset=utf-8", "base64");
            System.out.println("loaded html");

        }
    }

}