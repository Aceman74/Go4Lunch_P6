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
import com.aceman.go4lunch.activities.placesDetailActivity.PlacesDetailActivity;
import com.aceman.go4lunch.models.History;
import com.aceman.go4lunch.models.HistoryDetails;
import com.aceman.go4lunch.models.RestaurantPublic;
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
 * Created by Lionel JOFFRAY - on 14/03/2019.
 *
 * <b>Shared Adapter</b> for his API Call response
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private final Context mContext;
    public List<HistoryDetails> mHistoryList;
    @BindColor(R.color.colorError)
    int mRed;
    @BindColor(R.color.quantum_lightgreen500)
    int mGreen;


    public HistoryAdapter(List<HistoryDetails> historyList, Context context) {
        mHistoryList = historyList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View sharedView = inflater.inflate(R.layout.history_item_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(sharedView);
        ButterKnife.bind(this, parent);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        updateWithFreshInfo(mHistoryList.get(position), holder, position);
    }

    /**
     * Update RecyclerView with list info, handle click on item position with webview intent
     *
     * @param item   Article in the list
     * @param holder view holder
     */
    private void updateWithFreshInfo(final HistoryDetails item, final MyViewHolder holder, int position) {
        holder.mName.setText(item.getName());
        holder.mDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }


    /**
     * View Hoodler using ButterKnife
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_history_name)
        TextView mName;
        @BindView(R.id.item_history_date)
        TextView mDate;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}