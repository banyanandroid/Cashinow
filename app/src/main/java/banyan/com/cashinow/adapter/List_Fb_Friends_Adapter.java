package banyan.com.cashinow.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import banyan.com.cashinow.R;
import banyan.com.cashinow.activity.Activity_Profile;


public class List_Fb_Friends_Adapter extends BaseAdapter implements Filterable {

    private final String str_login_user_id;
    private List<String>filtered_name_data = null;
    private List<String>original_name_data = null;
    
    private List<String>filtered_image_data = null;
    private List<String>original_image_data = null;

    private List<String>original_user_id_data = null;
    private List<String>filter_user_id_data = null;

    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
    private final Context context;
    
    public List_Fb_Friends_Adapter(Context context, List<String> list_profile_url, List<String> list_names, List<String> list_user_id, String str_login_user_id) {

        this.filtered_name_data = list_names ;
        this.original_name_data = list_names ;
        this.filtered_image_data = list_profile_url ;
        this.original_image_data = list_profile_url ;
        this.original_user_id_data = list_user_id;
        this.filter_user_id_data = list_user_id;
        this.str_login_user_id = str_login_user_id;
        this.context = context;
        mInflater = LayoutInflater.from(context);

    }

    public int getCount() {
        return filtered_name_data.size();
    }

    public Object getItem(int position) {
        return filtered_name_data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_fb_friends_row, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.img_profile = (ImageView) convertView.findViewById(R.id.img_profile);
            holder.card_view = (CardView) convertView.findViewById(R.id.card_view);
            holder.card_view.setTag(position);
            // Bind the data efficiently with the holder.

            convertView.setTag(holder);

        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
            holder.card_view = (CardView) convertView.findViewById(R.id.card_view);
            holder.card_view.setTag(position);

        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.txt_name.setText(filtered_name_data.get(position));
        Picasso.with(context)
                .load(filtered_image_data.get(position))
                .placeholder(R.mipmap.ic_challenge)
                .into(holder.img_profile);

        // If weren't re-ordering this you could rely on what you set last time
        holder.txt_name.setText(filtered_name_data.get(position));

        //            action
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer position = (Integer)v.getTag();
                String str_user_id =  filter_user_id_data.get(position);


                if (str_user_id.equals(str_login_user_id)){

                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_MY_PROFILE);
                    editor.putString(Activity_Profile.TAG_PROFILE_USER_ID, str_user_id);

                    editor.commit();

                }else{

                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString(Activity_Profile.TAG_PROFILE_CALLING_TYPE, Activity_Profile.TAG_PROFILE_OTHER_PROFILE);
                    editor.putString(Activity_Profile.TAG_PROFILE_USER_ID, str_user_id);

                    editor.commit();

                }

                Intent intent = new Intent(context, Activity_Profile.class);
                context.startActivity(intent);

            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txt_name;
        ImageView img_profile;
        CardView card_view;
    }

    public Filter getFilter() {
        if (mFilter == null)
            mFilter = new ItemFilter();
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> list = original_name_data;

            int count = list.size();
            final ArrayList<Integer> nlist = new ArrayList<Integer>(count);

            String filterableString ;

//            get filtered items positions only
//            used the filtered position list to get filterd name and images
            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(i);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            if (filterString.isEmpty()) {

                nlist.clear();

                for (int cnt =0; cnt < original_name_data.size(); cnt++){
                    nlist.add(cnt);
                }
                results.values = nlist;
                results.count = nlist.size();

            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            ArrayList<Integer> filter_position_list = (ArrayList<Integer>) results.values;

            ArrayList<String> result_name = new ArrayList<>();
            ArrayList<String> result_image = new ArrayList<>();
            ArrayList<String> result_user_id = new ArrayList<>();
            for (int count = 0; count < filter_position_list.size(); count++){

                Integer position = filter_position_list.get(count);

                System.out.println("### filtered position"+position);
                String filtered_name = original_name_data.get(position);
                String filtered_image = original_image_data.get(position);
                String filtered_user_id = original_user_id_data.get(position);

                System.out.println("### filtered_name "+filtered_name);
                System.out.println("### filtered_image "+filtered_image);
                result_name.add(filtered_name);
                result_image.add(filtered_image);
                result_user_id.add(filtered_user_id);

            }

            filtered_name_data = result_name;
            filtered_image_data = result_image;
            filter_user_id_data = result_user_id;

            notifyDataSetChanged();
        }

    }
}

