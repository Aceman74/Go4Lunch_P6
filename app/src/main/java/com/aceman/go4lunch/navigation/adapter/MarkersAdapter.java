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
import com.aceman.go4lunch.utils.AdapterCallback;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.aceman.go4lunch.navigation.activities.CoreActivity.mResults;

/**
 * Created by Lionel JOFFRAY - on 14/03/2019.
 *
 * <b>Shared Adapter</b> for his API Call response
 */
public class MarkersAdapter extends RecyclerView.Adapter<MarkersAdapter.MyViewHolder> {

    @BindString(R.string.mUrl_begin)
    String mUrlBegin;
    @BindString(R.string.mUrlNext)
    String mUrlNext;
    private final RequestManager glide;
    private final Context mContext;
    private AdapterCallback mAdapterCallback;
    String mPhotoReference;
    String mUrl;
    Double mRating;
    int mDistanceRounded;
    @BindColor(R.color.colorError)
    int mRed;
    @BindColor(R.color.quantum_lightgreen500)
    int mGreen;
    String API_KEY = BuildConfig.google_maps_key;


    public MarkersAdapter(List<Result> listResult, RequestManager glide, Context context, AdapterCallback callback) {
        mResults = listResult;
        this.glide = glide;
        this.mContext = context;
        this.mAdapterCallback = callback;
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
        mDistanceRounded = Math.round(item.getDistanceTo());
        mDistanceRounded = ((mDistanceRounded + 4) / 5) * 5;    //  Round distance to 5m
        holder.mDistance.setText(mDistanceRounded + "m");
        if (item.getOpeningHours() != null && item.getOpeningHours().getOpenNow()) {

            holder.mOpen.setText("Open now");
            holder.mOpen.setTextColor(mGreen);
        } else {
            holder.mOpen.setText("Closed");
            holder.mOpen.setTextColor(mRed);
        }

        if (item.getRating() != null) {
            mRating = item.getRating();
            ratingMethod(mRating, holder);
        }

        if (item.getPhotos() != null) {
            mPhotoReference = item.getPhotos().get(0).getPhotoReference();
            mUrl = mUrlBegin + API_KEY + mUrlNext + mPhotoReference;
        } else {
            mUrl = item.getIcon();
        }
        try {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // resize large image
            glide.asDrawable()
                    .load(mUrl) //  Base URL added in Data
                    .apply(RequestOptions.fitCenterTransform()) //  Adapt to placeholder size
                    .into(holder.mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Loading").e("Loading error");
        }


        holder.mItemListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag(item.getName()).d("is Clicked");
                mAdapterCallback.onMethodCallback(item, holder.mStar, mUrl);

            }
        });

    }

    private int ratingMethod(Double rating, MyViewHolder holder) {

        if (rating >= 1 && rating <= 2.4) {
            holder.mStar1.setVisibility(View.VISIBLE);
            holder.mStar = 1;
        }
        if (rating >= 2.5 && rating <= 3.9) {
            holder.mStar1.setVisibility(View.VISIBLE);
            holder.mStar2.setVisibility(View.VISIBLE);
            holder.mStar = 2;
        }
        if (rating >= 4 && rating <= 5) {
            holder.mStar1.setVisibility(View.VISIBLE);
            holder.mStar2.setVisibility(View.VISIBLE);
            holder.mStar3.setVisibility(View.VISIBLE);
            holder.mStar = 3;
        }
        return holder.mStar;
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }


    /**
     * View Hoodler using ButterKnife
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public int mStar;
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

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}