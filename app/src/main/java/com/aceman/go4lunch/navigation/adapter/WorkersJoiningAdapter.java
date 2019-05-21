package com.aceman.go4lunch.navigation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.models.UserPublic;
import com.aceman.go4lunch.navigation.activities.PlacesDetailActivity;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 14/03/2019.
 *
 * <b>Shared Adapter</b> for his API Call response
 */
public class WorkersJoiningAdapter extends RecyclerView.Adapter<WorkersJoiningAdapter.MyViewHolder> {

    private List<UserPublic> mUserList;
    private final RequestManager glide;
    private final Context mContext;


    public WorkersJoiningAdapter(List<UserPublic> userList, RequestManager glide, Context context) {
        this.mUserList = userList;
        this.glide = glide;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View sharedView = inflater.inflate(R.layout.workers_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(sharedView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if((mUserList.get(position).getRestaurantName() != null && mUserList.get(position).getRestaurantName().equals(PlacesDetailActivity.mRestaurantName))) {
            updateWithFreshInfo(this.mUserList.get(position), this.glide, holder, position);
        }else{
            holder.mItemListener.setVisibility(View.GONE);
        }
    }

    /**
     * Update RecyclerView with list info, handle click on item position with webview intent
     *
     * @param user   Article in the list
     * @param glide  use for get image of article
     * @param holder view holder
     */
    private void updateWithFreshInfo(final UserPublic user, RequestManager glide, final MyViewHolder holder, int position) {

        holder.mTextView.setText(user.getUsername()+" is Joining " +user.getRestaurantName() + " !");

        try {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // resize large image
            glide.asDrawable()
                    .load(user.getUrlPicture()) //  Base URL added in Data
                    .apply(RequestOptions.fitCenterTransform()) //  Adapt to placeholder size
                    .into(holder.mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Shared").e("Loading error");
        }


        holder.mItemListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag(user.getUsername()).d("is Clicked");

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mUserList.size();
    }


    /**
     * View Hoodler using ButterKnife
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.workers_list_textview)
        TextView mTextView;
        String mRestaurant;
        String mName;
        @BindView(R.id.workmates_profile_picture)
        ImageView mImageView;
        @BindView(R.id.item_id_workmates)
        LinearLayout mItemListener;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}