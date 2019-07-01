package com.aceman.go4lunch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.placesDetail.PlacesDetailActivity;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.utils.AnimationClass;
import com.aceman.go4lunch.utils.DateSetter;
import com.aceman.go4lunch.utils.HourSetter;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 12/05/2019.
 * <p>
 * Workers Joining Adapter for showing, under restaurant detail on PlacesDetail, the list of worker joining.
 *
 * @see PlacesDetailActivity
 */
public class WorkersJoiningAdapter extends RecyclerView.Adapter<WorkersJoiningAdapter.MyViewHolder> {

    private final RequestManager glide;
    private final Context mContext;
    private List<RestaurantPublic> mUserList;
    private String mUsername;


    public WorkersJoiningAdapter(List<RestaurantPublic> userList, RequestManager glide, Context context, String username) {
        this.mUserList = userList;
        this.glide = glide;
        this.mContext = context;
        this.mUsername = username;
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
        String date = DateSetter.getFormattedDate();
        if ((mUserList.get(position).getRestaurantName() != null && mUserList.get(position).getRestaurantName().equals(PlacesDetailActivity.mName)) && mUserList.get(position).getDate().equals(date)) {
            updateWithFreshInfo(mUserList.get(position), glide, holder, position);
        } else {
            holder.mItemListener.setVisibility(View.GONE);
        }
    }

    /**
     * Update if a user eat here today, and change text if before/after 12pm.
     *
     * @param user     co worker
     * @param glide    profile picture
     * @param holder   view holder
     * @param position pos
     */
    private void updateWithFreshInfo(final RestaurantPublic user, RequestManager glide, final MyViewHolder holder, int position) {
        String text;
        if (HourSetter.getHour() < 13) {
            if (mUsername.equals(user.getUsername())) {
                text = mContext.getString(R.string.user_is_joining) + user.getRestaurantName() + mContext.getString(R.string.exclam);
            } else {
                text = user.getUsername() + mContext.getString(R.string.is_joining) + user.getRestaurantName() + mContext.getString(R.string.exclam);
            }
            holder.mTextView.setText(text);
        } else {
            if (mUsername.equals(user.getUsername())) {
                text = mContext.getString(R.string.user_ate_today_at) + user.getRestaurantName() + mContext.getString(R.string.exclam);
            } else {
                text = user.getUsername() + mContext.getString(R.string.ate_today_at) + user.getRestaurantName() + mContext.getString(R.string.exclam);
            }
            holder.mTextView.setText(text);
        }
        loadUserPictureWithGlide(user, holder);
        onClickListener(user, holder);
    }

    /**
     * On click user animation. Only visual.
     *
     * @param user   co worker
     * @param holder view holder
     */
    private void onClickListener(final RestaurantPublic user, final MyViewHolder holder) {
        holder.mItemListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mItemListener.startAnimation(AnimationClass.animClick(mContext));
                Timber.tag(user.getUsername()).d("is Clicked");

            }
        });
    }

    /**
     * Load profile pic with Glide.
     *
     * @param user   co-worler
     * @param holder view holder
     */
    private void loadUserPictureWithGlide(RestaurantPublic user, MyViewHolder holder) {
        try {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // resize large image
            glide.asDrawable()
                    .load(user.getUrlPicture()) //  Base URL added in Data
                    .apply(RequestOptions.fitCenterTransform()) //  Adapt to placeholder size
                    .into(holder.mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Shared").e("Loading error");
        }
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