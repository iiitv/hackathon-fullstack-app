package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

/**
 * Created by pratyush on 13/3/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackathon.fulstack.hackathon_fullstack_app.Manager.DatabaseManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Feed;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Preference;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static MyClickListener myClickListener;
    Context context;
    private ArrayList<Feed> mDataset;


    public MyRecyclerViewAdapter(ArrayList<Feed> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        MyRecyclerViewAdapter.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.individual_feed, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        Preference temp = DatabaseManager.getInstance(context).get_preference_by_id(mDataset.get(position).pid);

        holder.img.setText(mDataset.get(position).src.substring(0, 1));
        holder.dateTime.setText(timediff(mDataset.get(position).time));
        holder.content.setText(mDataset.get(position).content);
        holder.search.setText(temp.search_param);
        holder.refine.setText(temp.refine);
    }

    String timediff(String time) {
        return "0h";
    }

    public void addItem(Feed dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void swap(ArrayList<Feed> a) {
        mDataset.clear();
        mDataset.addAll(a);
        notifyDataSetChanged();
    }

    public void resetData(ArrayList<Feed> x) {
        mDataset = x;
        notifyDataSetChanged();
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
        TextView refine, search, img;
        TextView content;
        TextView dateTime;

        public DataObjectHolder(View itemView) {
            super(itemView);
            search = (TextView) itemView.findViewById(R.id.actual);
            refine = (TextView) itemView.findViewById(R.id.refine);
            img = (TextView) itemView.findViewById(R.id.TagChar);
            dateTime = (TextView) itemView.findViewById(R.id.time);
            content = (TextView) itemView.findViewById(R.id.content);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

}