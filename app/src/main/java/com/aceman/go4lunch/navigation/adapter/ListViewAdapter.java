package com.aceman.go4lunch.navigation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.R;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.utils.AdapterCallback;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * Created by Lionel JOFFRAY - on 14/03/2019.
 *
 * <b>Shared Adapter</b> for his API Call response
 */
public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.MyViewHolder> {

    private final RequestManager glide;
    private final Context mContext;
    public List<Result> mResults;
    public List<RestaurantPublic>mUserList;
    @BindString(R.string.mUrl_begin)
    String mUrlBegin;
    @BindString(R.string.mUrlNext)
    String mUrlNext;
    @BindColor(R.color.colorError)
    int mRed;
    @BindColor(R.color.quantum_lightgreen500)
    int mGreen;
    String API_KEY = BuildConfig.google_maps_key;
    private AdapterCallback mAdapterCallback;


    public ListViewAdapter(List<Result> listResult, List<RestaurantPublic> userList, RequestManager glide, Context context, AdapterCallback callback) {
        mResults = listResult;
        this.glide = glide;
        this.mContext = context;
        this.mAdapterCallback = callback;
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
    }

    /**
     * Update RecyclerView with list info, handle click on item position with webview intent
     *
     * @param item   Article in the list
     * @param glide  use for get image of article
     * @param holder view holder
     */
    private void updateWithFreshInfo(final Result item, RequestManager glide, final MyViewHolder holder, int position) {

        holder.mName.setText(item.getName());
        holder.mAdress.setText(item.getFormattedAddress());
        holder.mDistance.setText(item.getDistanceToInt() + "m");
        if (item.getOpeningHours() != null && item.getOpeningHours().getOpenNow()) {

            holder.mOpen.setText("Open now");
            holder.mOpen.setTextColor(mGreen);
        } else {
            holder.mOpen.setText("Closed");
            holder.mOpen.setTextColor(mRed);
        }

        int mRating = item.getRatingStars();
        ratingMethod(mRating, holder);
        Timber.tag("Adapter Rating").i("%s %s %s", item.getName(), item.getRatingStars(), mRating);

        if (item.getPhotos() != null) {
            holder.mPhotoReference = item.getPhotos().get(0).getPhotoReference();
            holder.mUrl = mUrlBegin + API_KEY + mUrlNext + holder.mPhotoReference;
        } else {
            holder.mUrl = item.getIcon();
        }
        try {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // resize large image
            glide.asDrawable()
                    .load(holder.mUrl) //  Base URL added in Data
                    .apply(RequestOptions.fitCenterTransform()) //  Adapt to placeholder size
                    .into(holder.mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Loading").e("Loading error");
        }


        holder.mItemListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag(item.getName()).d("is Clicked");
                mAdapterCallback.onMethodCallback(item, holder.mUrl);

            }
        });

    }

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
     * View Hoodler using ButterKnife
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

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}