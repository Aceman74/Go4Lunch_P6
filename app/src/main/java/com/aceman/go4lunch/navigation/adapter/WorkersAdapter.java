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
import com.aceman.go4lunch.data.nearby_search.Result;
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
public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.MyViewHolder> {

    private final List<Result> mResults;
    private final RequestManager glide;
    private final Context mContext;


    public WorkersAdapter(List<Result> listResult, RequestManager glide, Context context) {
        this.mResults = listResult;
        this.glide = glide;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View sharedView = inflater.inflate(R.layout.item_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(sharedView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        updateWithFreshInfo(this.mResults.get(position), this.glide, holder, position);
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
        holder.mOpen.setText(item.getReference());

        try {
            holder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // resize large image
            glide.asDrawable()
                    .load(item.getIcon()) //  Base URL added in Data
                    .apply(RequestOptions.fitCenterTransform()) //  Adapt to placeholder size
                    .into(holder.mImageView);
        } catch (Exception e) {
            Timber.tag("Image_Shared").e("Loading error");
        }


        holder.mItemListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag(item.getName()).d("is Clicked");

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.mResults.size();
    }


    /**
     * View Hoodler using ButterKnife
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
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

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}