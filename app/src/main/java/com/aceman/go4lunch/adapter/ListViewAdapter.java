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

import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.R;
import com.aceman.go4lunch.activities.placesDetail.PlacesDetailActivity;
import com.aceman.go4lunch.data.models.RestaurantPublic;
import com.aceman.go4lunch.data.places.nearby_search.Result;
import com.aceman.go4lunch.utils.AnimationClass;
import com.aceman.go4lunch.utils.DateSetter;
import com.aceman.go4lunch.utils.events.PlacesDetailEvent;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * Created by Lionel JOFFRAY - on 12/05/2019.
 * <p>
 * ListView Adapter for the user radius search Places ( result list from maps search ).
 *
 * @see com.aceman.go4lunch.fragments.maps.MapsFragment
 * @see com.aceman.go4lunch.fragments.listView.ListViewFragment
 */
public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.MyViewHolder> {

    private final RequestManager glide;
    private final Context mContext;
    public List<Result> mResults;
    public List<RestaurantPublic> mUserList;
    @BindString(R.string.mUrl_begin)
    String mUrlBegin;
    @BindString(R.string.mUrlNext)
    String mUrlNext;
    @BindColor(R.color.colorError)
    int mRed;
    @BindColor(R.color.quantum_lightgreen500)
    int mGreen;
    String API_KEY = BuildConfig.google_maps_key;
    private String mIntent;


    public ListViewAdapter(List<Result> listResult, List<RestaurantPublic> userList, RequestManager glide, Context context) {
        mResults = listResult;
        this.glide = glide;
        this.mContext = context;
        this.mUserList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View sharedView = inflater.inflate(R.layout.item_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(sharedView);
        ButterKnife.bind(this, parent);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        updateWithFreshInfo(mResults.get(position), this.glide, holder, position);
        AnimationClass.setFadeAnimation(holder.itemView, mContext);
    }

    /**
     * Update RecyclerView with restaurants details.
     */
    private void updateWithFreshInfo(final Result item, RequestManager glide, final MyViewHolder holder, int position) {
        holder.mName.setText(item.getName());
        holder.mAdress.setText(item.getFormattedAddress());
        holder.mDistance.setText(item.getDistanceToInt() + "m");
        setOpenOrClose(item, holder);
        userWithSamePlace(item, holder);
        int mRating = item.getRatingStars();
        ratingMethod(mRating, holder);
        Timber.tag("Adapter Rating").i("%s %s %s", item.getName(), item.getRatingStars(), mRating);
        setPhotoUrl(item, holder);
        loadPhotoWithGlide(holder);
        onClickItemListener(item, holder);
    }

    /**
     * On click a restaurant, PlacesDetailActivity open with detailled view.
     *
     * @param item   the restaurant object
     * @param holder view holder
     * @see PlacesDetailActivity
     */
    private void onClickItemListener(final Result item, final MyViewHolder holder) {

        holder.mItemListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mItemListener.startAnimation(AnimationClass.animClick(mContext));
                Timber.tag(item.getName()).d("is Clicked");
                EventBus.getDefault().postSticky(new PlacesDetailEvent(item, holder.mUrl));
                Intent detail = new Intent(mContext, PlacesDetailActivity.class);
                mIntent = mContext.getString(R.string.adapter);
                detail.putExtra("detail_intent", mIntent);
                mContext.startActivity(detail);

            }
        });
    }

    /**
     * Load places pictures on recyclerview.
     *
     * @param holder view holder
     */
    private void loadPhotoWithGlide(MyViewHolder holder) {
        try {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // resize large image
            glide.asDrawable()
                    .load(holder.mUrl) //  Base URL added in Data
                    .apply(RequestOptions.fitCenterTransform()) //  Adapt to placeholder size
                    .into(holder.mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Loading").e("Loading error");
        }
    }

    /**
     * Get the photo url and set it to a "readable" URL for Glide.
     *
     * @param item   restaurant object
     * @param holder view holder
     */
    private void setPhotoUrl(Result item, MyViewHolder holder) {

        if (item.getPhotos() != null) {
            holder.mPhotoReference = item.getPhotos().get(0).getPhotoReference();
            holder.mUrl = mUrlBegin + API_KEY + mUrlNext + holder.mPhotoReference;
        } else {
            holder.mUrl = item.getIcon();
        }
    }

    /**
     * Set the open state of the place.
     *
     * @param item   restaurant object
     * @param holder view holder
     */
    private void setOpenOrClose(Result item, MyViewHolder holder) {
        if (item.getOpeningHours() != null && item.getOpeningHours().getOpenNow()) {

            holder.mOpen.setText(mContext.getString(R.string.open_now));
            holder.mOpen.setTextColor(mGreen);
        } else {
            holder.mOpen.setText(mContext.getString(R.string.closed_now));
            holder.mOpen.setTextColor(mRed);
        }
    }

    /**
     * Take the public userlist to compare it to the place, to show if a co-workers join.
     *
     * @param item   restaurant object
     * @param holder view holder
     */
    private void userWithSamePlace(Result item, MyViewHolder holder) {
        int userJoining = 0;

        String date = DateSetter.getFormattedDate();
        for (int i = 0; i < mUserList.size(); i++) {
            if (mUserList.get(i).getRestaurantID() != null && mUserList.get(i).getDate().equals(date)) {
                String getID = mUserList.get(i).getRestaurantID();
                if (getID.equals(item.getPlaceId())) {
                    userJoining++;
                }
            }
        }
        if (userJoining > 0) {
            holder.mIsUser.setText("(" + userJoining + ")");
        } else {
            holder.mIsUser.setVisibility(View.GONE);
            holder.mWorkerImg.setVisibility(View.GONE);

        }
    }

    /**
     * Set the star number with custom rating.
     *
     * @param rating nbr star
     * @param holder view holder
     */
    private void ratingMethod(int rating, MyViewHolder holder) {
        if (rating == 1) {
            holder.mStar1.setVisibility(View.VISIBLE);
            holder.mStar2.setVisibility(View.GONE);
            holder.mStar3.setVisibility(View.GONE);
        }
        if (rating == 2) {
            holder.mStar1.setVisibility(View.VISIBLE);
            holder.mStar2.setVisibility(View.VISIBLE);
            holder.mStar3.setVisibility(View.GONE);
        }
        if (rating == 3) {
            holder.mStar1.setVisibility(View.VISIBLE);
            holder.mStar2.setVisibility(View.VISIBLE);
            holder.mStar3.setVisibility(View.VISIBLE);
        }
        if (rating == 0) {
            holder.mStar1.setVisibility(View.GONE);
            holder.mStar2.setVisibility(View.GONE);
            holder.mStar3.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }


    /**
     * View Holder using ButterKnife
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public String mUrl;
        public String mPhotoReference;
        @BindView(R.id.item_restaurant_name)
        TextView mName;
        @BindView(R.id.item_restaurant_adress)
        TextView mAdress;
        @BindView(R.id.item_restaurant_open)
        TextView mOpen;
        @BindView(R.id.item_imageview)
        ImageView mImageView;
        @BindView(R.id.item_id)
        LinearLayout mItemListener;
        @BindView(R.id.item_rating_star_1)
        ImageView mStar1;
        @BindView(R.id.item_rating_star_2)
        ImageView mStar2;
        @BindView(R.id.item_rating_star_3)
        ImageView mStar3;
        @BindView(R.id.item_distance)
        TextView mDistance;
        @BindView(R.id.item_worker_is_here)
        TextView mIsUser;
        @BindView(R.id.item_workers)
        ImageView mWorkerImg;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}