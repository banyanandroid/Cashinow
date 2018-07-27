package banyan.com.cashinow.utils;

/**
 * Created by User on 9/22/2016.
 */
public class AppConfig {

   public static String URL_BASE = "http://epictech.in/cashinow/";

//   login
   public static String url_login = URL_BASE+"Apicontroller/user_login";

//   challenge
//   list
   public static String url_list_challenges = URL_BASE+"Apicontroller/list_challenges";
   public static String url_list_my_challenges = URL_BASE+"Apicontroller/my_challenges";
   public static String url_list_events = URL_BASE+"Apicontroller/list_activeevents";

   public static String url_list_active_challenges = URL_BASE+"Apicontroller/my_profile_activechallenges";
   public static String url_list_completed_challenges = URL_BASE+"Apicontroller/my_profile_completedchallenges";

//   create
   public static String url_create_challenge = URL_BASE+"Apicontroller/create_challenge";
   public static String url_create_direct_challenge = URL_BASE+"Apicontroller/direct_challenge";

   public static String url_update_challenge = URL_BASE + "Apicontroller/edit_challenges";

   public static String url_delete_challenge = URL_BASE + "Apicontroller/delete_mychallenge";;

//   details
   public static String url_single_challenge = URL_BASE+"Apicontroller/single_challenge";
   public static String url_get_challenge_details = URL_BASE+"Apicontroller/challenge_details";

//   accept
   public static String url_accept_challenge = URL_BASE+"Apicontroller/accept_challenge";

   public static String url_filter_challenges = URL_BASE+"Apicontroller/challenge_filter";

   //   profile
   public static String url_profile = URL_BASE+"Apicontroller/my_profile";
   public static String url_account_details = URL_BASE + "Apicontroller/account_details";
   public static String url_get_account_details = URL_BASE + "Apicontroller/get_account_details";
   public static String url_upload_profile_image = URL_BASE + "Apicontroller/update_photo";
   public static String url_get_profile_url = URL_BASE + "Apicontroller/get_fb_profile_url";
   public static String url_set_profile_url = URL_BASE + "Apicontroller/set_fb_profile_url";

//   notification
   public static String url_list_notifications = URL_BASE+"Apicontroller/my_notifications";
   public static String url_clear_notifications = URL_BASE+"Apicontroller/ok_notification";
   public static String url_accept_through_challenge = URL_BASE+"Apicontroller/accept_through_notification";


//   payment
   public static String url_user_pay = URL_BASE+"Apicontroller/user_pay";

   public static String url_payment_pending_details = URL_BASE + "Apicontroller/payment_pending_details";
   public static String url_make_payment = URL_BASE + "Apicontroller/pay_amount";
   public static String url_challenge_win_details = URL_BASE + "Apicontroller/winning_popup";
   public static String url_pay_commision = URL_BASE + "Apicontroller/pay_commission";
   public static String url_payment_received = URL_BASE + "Apicontroller/admin_commission_details";

//   share
   public static String url_fb_share = "http://epictech.in/cashinow2/winning.php";
   public static String url_app = "https://play.google.com/store/apps";
   public static String url_app_image = URL_BASE + "skin/default/images/logo/app_icon.png";

//   app user list
   public static String url_app_users_list = URL_BASE + "Apicontroller/list_users";


//   alert win lose payment
   public static String url_close_win_popup = URL_BASE + "Apicontroller/close_winning_popup";

}
