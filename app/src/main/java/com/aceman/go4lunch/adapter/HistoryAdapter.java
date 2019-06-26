package com.aceman.go4lunch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.data.models.HistoryDetails;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lionel JOFFRAY - on 12/05/2019.
 * <p>
 * History Adapter for the user History list (show user history)
 *
 * @see com.aceman.go4lunch.activities.settings.SettingsActivity
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
     * Update RecyclerView with restaurant list and date.
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
     * View Holder using ButterKnife.
     *
     * @see ButterKnife
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