package banyan.com.cashinow.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.cashinow.R;
import banyan.com.cashinow.app.Config;
import banyan.com.cashinow.utils.AppConfig;
import banyan.com.cashinow.utils.NotificationUtils;
import banyan.com.cashinow.utils.SessionManager;
import dmax.dialog.SpotsDialog;

/*
* function
*
*
* */
public class Activity_Login extends AppCompatActivity {

    private static final String TAG = Activity_Login.class.getSimpleName();

    CallbackManager callbackManager;
    TextView txtRegId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    SpotsDialog dialog;
    public static RequestQueue queue;

    JSONObject response, profile_pic_data, profile_pic_url;
    String str_id, str_email, str_name, str_picture;

    JSONObject json_object_session;
    // Session Manager Class
    SessionManager session;

    String str_user_id, str_user_name = "";
    String str_reg_id = "";

    //account details and profile image
    TextView txt_view_image_uploaded;
    EditText edt_account_details;
    Button btn_add_enquiry_image;
    String str_account_details = "", image_type= "", encodedstring = "", listString= "", str_selected_image= "";
    private ArrayList<Image> images = new ArrayList<>();
    ArrayList<String> Arraylist_image_encode , Arraylist_dummy ;
    SpotsDialog dialog_upload_image, dialog_account;

    private static long back_pressed;

    private final String TAG_USER_PROFILE_USER_ID = "user_id";
    private final String TAG_USER_PROFILE_IMAGE = "image";

//    profile details
    SpotsDialog dialog_profile;
    public static final String TAG_LOGIN_USER_NAME = "login_user_name";
    public static final String TAG_LOGIN_USER_IMAGE = "login_user_image";

//    fb profile url
    public static final String TAG_LOGIN_USER_FB_PROFILE_URL = "fb_url";
    public static final String TAG_LOGIN_USER_USER_ID = "user_id";

    String str_fb_profile_url = "https://www.facebook.com/";
    SpotsDialog dialog_profile_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtRegId = (TextView) findViewById(R.id.txt_reg_id);

        String fontPath = "fonts/annabel.ttf";
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // For Device SHA
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("banyan.com.cashinow", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }


        // Facebook Login
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email, publish_actions");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                displayFirebaseRegId();
                getUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        // Firebase Push Notification
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    /*txtMessage.setText(message);*/
                }
            }
        };

        displayFirebaseRegId();
    }


    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        // Trigger an event

                        try {
                            System.out.println("### DATA : " + json_object);
                            json_object_session = json_object;
                            System.out.println("### DATA json_object_session : " + json_object_session);

                            str_id = json_object.get("id").toString();
                            str_email = json_object.get("email").toString();
                            str_name = json_object.get("name").toString();
                            profile_pic_data = new JSONObject(json_object.get("picture").toString());
                            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                            str_picture = profile_pic_url.getString("url");

                            System.out.println("### ID : " + str_id);
                            System.out.println("### EMAIL : " + str_email);
                            System.out.println("### NAME : " + str_name);
                            System.out.println("### PICTURE : " + str_picture);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                       // txtRegId.setText("Firebase Reg Id: " + str_reg_id);

                        try {
                            displayFirebaseRegId();
                            dialog = new SpotsDialog(Activity_Login.this);
                            dialog.setCancelable(false);
                            dialog.show();
                            queue = Volley.newRequestQueue(Activity_Login.this);
                            Function_Login(json_object.toString());
                        } catch (Exception e) {

                        }


                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(175).height(175)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        str_reg_id = regId;

    }

    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        // Notification

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);

        // Notification

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    /********************************
     *FUNCTION LOGIN
     *********************************/
    private void Function_Login(String params) {

        System.out.println("### AppConfig.url_login "+AppConfig.url_login);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d(TAG, "### "+response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {
                        dialog.dismiss();

                        TastyToast.makeText(getApplicationContext(), "Login Successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                        JSONObject obj2 = obj.getJSONObject("data");

                        str_user_id = obj2.getString("user_id");
                        str_user_name = obj2.getString("user_name");

                        System.out.println("### DATA :ID: " + str_user_id);
                        System.out.println("### DATA :NAME: " + str_user_name);


                        Alert_Get_User_Details();

                    } else {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), " Oops..! Login Failed", TastyToast.LENGTH_LONG, TastyToast.ERROR);

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
                System.out.println("### onErrorResponse");
                dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_name", str_name);
                params.put("user_email", str_email);
                params.put("user_facebook_id", str_id);
                params.put("user_photo", str_picture);
                params.put("device_token", str_reg_id);

                System.out.println("### user_name  " + str_name);
                System.out.println("### user_email  " + str_email);
                System.out.println("### user_facebook_id  " + str_id);
                System.out.println("### user_photo  " + str_picture);
                System.out.println("### device_token  " + str_reg_id);

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

    /********************************
     *FUNCTION ACCOUNT DETAILS
     *********************************/
    private void Function_Account_Details() {

        System.out.println("###  AppConfig.url_account_details " + AppConfig.url_account_details);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_account_details, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {


                        TastyToast.makeText(getApplicationContext(), "Account Details Added Successfully.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                        // to update login user name and image
                        queue = Volley.newRequestQueue(Activity_Login.this);
                        Function_Get_Profile();


                    } else {

                        dialog_upload_image.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Account Details Not Added, Try Again.", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog_upload_image.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_user_id);
                params.put("account_details", str_account_details);

                System.out.println("### user_id  " + str_name);
                System.out.println("### account_details  " + str_account_details);

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

    /********************************
     * UPLOAD PROFILE IMAGE
     *********************************/
    private void Function_Upload_Profile_Image() {

        System.out.println("###  AppConfig.url_upload_profile_image " + AppConfig.url_upload_profile_image);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_upload_profile_image, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        TastyToast.makeText(getApplicationContext(), "Profile Image Added Successfully.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                        try{

                            queue = Volley.newRequestQueue(Activity_Login.this);
                            Function_Account_Details();

                        }catch (Exception e){

                        }


                    } else {

                        dialog_upload_image.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Profile Image Not Added, Try Again.", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog_upload_image.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(TAG_USER_PROFILE_USER_ID, str_user_id);
                params.put(TAG_USER_PROFILE_IMAGE, encodedstring);

                System.out.println("### "+TAG_USER_PROFILE_USER_ID +" " + str_user_id);
                System.out.println("### "+TAG_USER_PROFILE_IMAGE +" " + encodedstring);

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


    /*******************************
     *  PIC UPLOADER
     * ***************************/

// Recomended builder
    public void Function_ImagePicker () {

        ImagePicker.with(this)
                .setFolderMode(true) // set folder mode (false by default)
                .setFolderTitle("FOLDER") // folder selection title
                .setImageTitle("TAP TO SELECT") // image selection title
                .setMultipleMode(true) // multi mode (default mode)
                .setMaxSize(1)// max images can be selected (999 by default)
                .setKeepScreenOn(true)
                .start();

        com.nguyenhoanglam.imagepicker.model.Config config = new com.nguyenhoanglam.imagepicker.model.Config();

    }

    // GET IMAGE FROM IMAGE PICKER
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == com.nguyenhoanglam.imagepicker.model.Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {

            images = data.getParcelableArrayListExtra(com.nguyenhoanglam.imagepicker.model.Config.EXTRA_IMAGES);

            StringBuilder sb = new StringBuilder();
            for (int i = 0, l = images.size(); i < l; i++) {

                String str_img_path = images.get(i).getPath();

                Bitmap bmBitmap = BitmapFactory.decodeFile(str_img_path);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bmBitmap.compress(Bitmap.CompressFormat.JPEG, 25, bao);
                byte[] ba = bao.toByteArray();
                encodedstring = Base64.encodeToString(ba, 0);
                Log.e("base64", "-----" + encodedstring);

                Arraylist_image_encode.add(encodedstring);

                txt_view_image_uploaded.setText("Image Added Suucessfully.");
                btn_add_enquiry_image.setText("Change Image / File");
            }
            Function_Encode_Image1();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void Function_Encode_Image1 () {

        for (String s : Arraylist_image_encode) {
            listString += s + "IMAGE:";
        }
        str_selected_image = listString;
        System.out.print("###  Image Uploaded " + listString);
    }

    public void Alert_Get_User_Details(){

        //get account details
        LayoutInflater li = LayoutInflater.from(Activity_Login.this);
        View view = li.inflate(R.layout.dialog_user_details, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_Login.this);
        alertDialogBuilder.setView(view);

        edt_account_details = (EditText) view.findViewById(R.id.edt_account_details);
        txt_view_image_uploaded = (TextView) view.findViewById(R.id.txt_view_image_uploaded);
        btn_add_enquiry_image = (Button) view.findViewById(R.id.btn_add_enquiry_image);

        //action
        // ADD IMAGE FOR ENQUIRY
        btn_add_enquiry_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Arraylist_image_encode = new ArrayList<>();
                image_type = "Location photos";
                Function_ImagePicker();

            }
        });

        // SET DIALOG MESSAGE
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // get user input and set it to result
                                str_account_details = edt_account_details.getText().toString();
                                if (str_account_details.isEmpty()) {

                                    TastyToast.makeText(getApplicationContext(), "Enter Account Details", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                                    edt_account_details.setError("Account Details Should Not Be Empty");

                                }else if (encodedstring.isEmpty()) {

                                    TastyToast.makeText(getApplicationContext(), "Upload Profile Image", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

                                }
                                else {

                                    //save account details
                                    try{

                                        queue = Volley.newRequestQueue(Activity_Login.this);
                                        dialog_upload_image = new SpotsDialog(Activity_Login.this);
                                        dialog_upload_image.setCancelable(false);
                                        dialog_upload_image.show();
                                        Function_Upload_Profile_Image();

                                    }catch (Exception e){

                                    }

                                }
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle("User Details");
        alertDialog.setIcon(R.mipmap.app_icon);
        alertDialog.setCancelable(false);
        alertDialog.show();

    }


    /***************************
     * GET Profile
     *
     ***************************/

    /*
    * to update the login user name and photo
    * */
    public void Function_Get_Profile() {
        System.out.println("### AppConfig.url_profile "+AppConfig.url_profile);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_profile, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONObject obj1;

                        obj1 = obj.getJSONObject("data");


                        String user_name = obj1.getString("user_name");
                        String user_image = obj1.getString("user_image");


                        //set current profile user id
                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(Activity_Login.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(TAG_LOGIN_USER_NAME, user_name);
                        editor.putString(TAG_LOGIN_USER_IMAGE, user_image);


                        System.out.println("### "+TAG_LOGIN_USER_NAME+" "+user_name);
                        System.out.println("### "+TAG_LOGIN_USER_IMAGE+" "+user_image);

                        editor.commit();

//                       to upload fb profile url to server
                        try{

                            queue = Volley.newRequestQueue(Activity_Login.this);
                            Function_Set_Profile_URL();
                        }catch (Exception e){

                        }


                    } else if (success == 0) {

                        dialog_upload_image.dismiss();
                        TastyToast.makeText(getApplicationContext(), "No Data Found !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog_upload_image.dismiss();
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_user_id);

                System.out.println("### USER ID : " + str_user_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /*
     * to set fb profile url to server
     * */
    public void Function_Set_Profile_URL() {

        System.out.println("### AppConfig.url_set_profile_url "+AppConfig.url_set_profile_url);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_set_profile_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                System.out.println("### onResponse");
                Log.d(TAG, "### "+response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        dialog_upload_image.dismiss();

//                        make user is login in shared prefference
                        session.createLoginSession(json_object_session.toString(), str_user_id);

                        Intent intent = new Intent(Activity_Login.this, MainActivity.class);
//                        intent.putExtra("userProfile", json_object_session.toString());
                        startActivity(intent);
                        finish();

                    } else if (success == 0) {

                        dialog_upload_image.dismiss();
                        TastyToast.makeText(getApplicationContext(), "No Data Found !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("### onErrorResponse");
                dialog_upload_image.dismiss();
//                TastyToast.makeText(getApplicationContext(), "Data Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(TAG_LOGIN_USER_USER_ID, str_user_id);
                params.put(TAG_LOGIN_USER_FB_PROFILE_URL, str_fb_profile_url);

                System.out.println("### "+TAG_LOGIN_USER_USER_ID+" : " + str_user_id);
                System.out.println("### "+TAG_LOGIN_USER_FB_PROFILE_URL+" : " + str_fb_profile_url);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

}
