package com.hackathon.fulstack.hackathon_fullstack_app.Activity;

/**
 * Created by pratyush on 13/3/16.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackathon.fulstack.hackathon_fullstack_app.Manager.DatabaseManager;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Feed;
import com.hackathon.fulstack.hackathon_fullstack_app.Models.Preference;
import com.hackathon.fulstack.hackathon_fullstack_app.R;

import java.io.InputStream;
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
                .inflate(R.layout.individual_feed_image, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        Preference temp = DatabaseManager.getInstance(context).get_preference_by_id(mDataset.get(position).pid);

        if (mDataset.get(position).src.matches("twitter"))
            holder.img.setImageResource(R.mipmap.twitter);
        else if (mDataset.get(position).src.matches("instagram"))
            holder.img.setImageResource(R.mipmap.instagram);
        else
            holder.img.setImageResource(R.mipmap.tumblr);
        holder.dateTime.setText(timediff(mDataset.get(position).time));
        holder.content.setText(mDataset.get(position).content);
        holder.search.setText(temp.search_param);
        holder.refine.setText(temp.refine);
        Log.i("Downloading Image ", mDataset.get(position).image_url);
        new DownloadImageTask(holder.image).execute(mDataset.get(position).image_url);
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

    public static class MasterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MasterHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView refine, search;
        TextView content;
        TextView dateTime;
        ImageView image, img;
        public DataObjectHolder(View itemView) {
            super(itemView);
            search = (TextView) itemView.findViewById(R.id.actual);
            refine = (TextView) itemView.findViewById(R.id.refine);
            img = (ImageView) itemView.findViewById(R.id.TagImg);
            dateTime = (TextView) itemView.findViewById(R.id.time);
            content = (TextView) itemView.findViewById(R.id.content);
            image = (ImageView) itemView.findViewById(R.id.post_image);
        }
        @Override
        public void onClick(View v) {

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}