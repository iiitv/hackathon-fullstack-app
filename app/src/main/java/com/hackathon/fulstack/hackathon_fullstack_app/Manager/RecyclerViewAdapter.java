package com.hackathon.fulstack.hackathon_fullstack_app.Manager;

/**
 * Created by Raju Kaushik on 3/13/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackathon.fulstack.hackathon_fullstack_app.Models.Preference;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView
        .Adapter<RecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "RecyclerViewAdapter";
    private static MyClickListener myClickListener;
    private ArrayList<Preference> mDataset;

    public RecyclerViewAdapter(ArrayList<Preference> myDataset) {
        mDataset = myDataset;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        RecyclerViewAdapter.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.individual_pref, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.searchParam.setText(mDataset.get(position).search_param);
        holder.refine1.setText(mDataset.get(position).refine);
    }

    public void addItem(Preference dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView searchParam;
        TextView refine1;

        public DataObjectHolder(View itemView) {
            super(itemView);
            searchParam = (TextView) itemView.findViewById(R.id.searchParamVar);
            refine1 = (TextView) itemView.findViewById(R.id.refine1);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}


