package com.example.frankantolino.flickrbrowser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Frank on 5/17/2017.
 */

public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotoList;
    private Context mContext;

    public FlickrRecyclerViewAdapter(Context context, List<Photo> photoList) {
        mContext = context;
        mPhotoList = photoList;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Called by the layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder holder, int position) {
        //Called by layout manager when it wants new data in an existing row

        Photo photoItem = mPhotoList.get(position);
        Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + " --> " + position);
        Picasso.with(mContext).load(photoItem.getImage())
                .error(R.drawable.placeholder)//if error, use placeholder image
                .placeholder(R.drawable.placeholder)//while waiting, use placeholder image
                .into(holder.thumbnail);//Load into FlickrImageViewHolder thumbnail attribute

        holder.title.setText(photoItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return ((mPhotoList != null) && (mPhotoList.size() !=0) ? mPhotoList.size() : 0);
    }

    void loadNewData(List<Photo> newPhotos){
        mPhotoList = newPhotos;
        notifyDataSetChanged();;
    }

    public Photo getPhoto(int position){
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.get(position) : null );
    }


    //------------new class---------------------------
    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;
        public FlickrImageViewHolder(View itemView){
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.title);

        }  
    }
}



