package com.aceman.go4lunch.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.placesDetail.PlacesDetailActivity;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.utils.AnimationClass;
import com.aceman.go4lunch.utils.DateSetter;
import com.aceman.go4lunch.utils.HourSetter;
import com.aceman.go4lunch.utils.events.RestaurantPublicEvent;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 12/05/2019.
 * <p>
 * Workers Adapter for the Workmates Fragments recyclerview (use Userlist).
 *
 * @see com.aceman.go4lunch.fragments.workmates.WorkmatesFragment
 */
public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.MyViewHolder> {

    private final RequestManager glide;
    private final Context mContext;
    private List<RestaurantPublic> mUserList;
    private String mIntent;


    public WorkersAdapter(List<RestaurantPublic> userList, RequestManager glide, Context context) {
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
        updateWithFreshInfo(this.mUserList.get(position), this.glide, holder, position);
        AnimationClass.setFadeAnimation(holder.itemView, mContext);
    }

    /**
     * Update recyclerview with all co-worker and show where they eat.
     *
     * @param user     co worker
     * @param glide    image pic
     * @param holder   view holder
     * @param position pos in recyclerview
     */
    private void updateWithFreshInfo(final RestaurantPublic user, RequestManager glide, final MyViewHolder holder, int position) {
        setUserDescription(user, holder);
        loadUserPictureWithGlide(user, holder);
        onClickListener(user, holder);
    }

    /**
     * On click co-worker, PlacesDetail open with restaurant details.
     *
     * @param user   co-worker
     * @param holder view holder
     */
    private void onClickListener(final RestaurantPublic user, final MyViewHolder holder) {
        holder.mItemListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mItemListener.startAnimation(AnimationClass.animClick(mContext));
                if (user.getDate() != null && user.getDate().equals(DateSetter.getFormattedDate())) {

                    Timber.tag(user.getUsername()).d("is Clicked");
                    EventBus.getDefault().postSticky(new RestaurantPublicEvent(user));
                    Intent workers = new Intent(mContext, PlacesDetailActivity.class);
                    mIntent = mContext.getString(R.string.workers);
                    workers.putExtra(mContext.getString(R.string.detail_intent), mIntent);
                    mContext.startActivity(workers);
                } else {
                    Toast.makeText(mContext, user.getUsername() + mContext.getString(R.string.has_not_choose), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /**
     * Load profile picture with Glide
     *
     * @param user   user
     * @param holder view holder
     */
    private void loadUserPictureWithGlide(RestaurantPublic user, MyViewHolder holder) {
        try {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // resize large image
            glide.asDrawable()
                    .load(user.getUrlPicture()) //  Base URL added in Data
                    .apply(RequestOptions.circleCropTransform()) //  Adapt to placeholder size
                    .into(holder.mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Shared").e("Loading error");
        }
    }

    /**
     * This method show the correct text if a user choose, or not a place and if it's before/after 12PM.
     *
     * @param user   co-worker
     * @param holder view holder
     */
    private void setUserDescription(RestaurantPublic user, WorkersAdapter.MyViewHolder holder) {
        String date = DateSetter.getFormattedDate();

        for (int i = 0; i < mUserList.size(); i++) {
            if (user.getDate() != null && user.getDate().equals(date)) {
                String getID = mUserList.get(i).getRestaurantID();
                if (getID != null && getID.equals(user.getRestaurantID())) {
                    if (HourSetter.getHour() < 13) {
                        String text = user.getUsername() + mContext.getString(R.string.is_eating) + user.getRestaurantName() + mContext.getString(R.string.exclam);
                        holder.mTextView.setText(text);
                    } else {
                        String text = user.getUsername() + mContext.getString(R.string.ate_today_at) + user.getRestaurantName() + mContext.getString(R.string.exclam);
                        holder.mTextView.setText(text);
                    }
                }
            } else {
                holder.mTextView.setAlpha(0.6f);
                String text = user.getUsername() + mContext.getString(R.string.not_choice_yet);
                holder.mTextView.setText(text);
            }
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