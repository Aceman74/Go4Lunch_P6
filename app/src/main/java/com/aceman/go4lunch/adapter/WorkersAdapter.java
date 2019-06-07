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
import com.aceman.go4lunch.activities.placesDetailActivity.PlacesDetailActivity;
import com.aceman.go4lunch.models.RestaurantPublic;
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
 * Created by Lionel JOFFRAY - on 14/03/2019.
 *
 * <b>Shared Adapter</b> for his API Call response
 */
public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.MyViewHolder> {

    private List<RestaurantPublic> mUserList;
    private final RequestManager glide;
    private final Context mContext;
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
    }

    /**
     * Update RecyclerView with list info, handle click on item position with webview intent
     *
     * @param user   Article in the list
     * @param glide  use for get image of article
     * @param holder view holder
     */
    private void updateWithFreshInfo(final RestaurantPublic user, RequestManager glide, final MyViewHolder holder, int position) {

        userWithSamePlace(user, holder);
        loadUserPictureWithGlide(user, holder);
        onClickListener(user, holder);
    }

    private void onClickListener(final RestaurantPublic user, MyViewHolder holder) {
        holder.mItemListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getDate() != null && user.getDate().equals(DateSetter.getFormattedDate())) {

                    Timber.tag(user.getUsername()).d("is Clicked");
                     EventBus.getDefault().postSticky(new RestaurantPublicEvent(user));
                    Intent workers = new Intent(mContext, PlacesDetailActivity.class);
                    mIntent = mContext.getString(R.string.workers);
                    workers.putExtra("detail_intent", mIntent);
                    mContext.startActivity(workers);
                }else {
                    Toast.makeText(mContext,user.getUsername()+" has not choose his lunch.",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

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

    private void userWithSamePlace(RestaurantPublic user, WorkersAdapter.MyViewHolder holder) {
        String date = DateSetter.getFormattedDate();

        for (int i = 0; i < mUserList.size(); i++) {
            if (user.getDate() != null && user.getDate().equals(date)) {
                String getID = mUserList.get(i).getRestaurantID();
                if (getID != null && getID.equals(user.getRestaurantID())) {
                    if (HourSetter.getHour() < 13) {
                        holder.mTextView.setText(user.getUsername() + " is eating at " + user.getRestaurantName() + "!");
                    } else {
                        holder.mTextView.setText(user.getUsername() + " ate today at " + user.getRestaurantName() + "!");
                    }
                }
            } else {
                holder.mTextView.setAlpha(0.6f);
                holder.mTextView.setText(user.getUsername() + " has not made his choice yet .");
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