package com.example.frankantolino.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 1/10/2017.
 */

class GetFlickrJsonData extends AsyncTask<String,Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";
    private List<Photo> mPhotoList = null;
    private String mBaseURL;
    private String mLanguage;
    private boolean mMatchAll;

    private final onDataAvailable mCallBack;
    private boolean runningOnSameThread = false;

    interface onDataAvailable{
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    public GetFlickrJsonData(onDataAvailable callBack,String baseURL, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData: called");
        mBaseURL = baseURL;
        mCallBack = callBack;
        mLanguage = language;
        mMatchAll = matchAll;
    }

    void execeuteOnSameThread(String searchCriteria){
        Log.d(TAG, "execeuteOnSameThread: starts");
        runningOnSameThread = true;
        String destinationUri = createUri(searchCriteria,mLanguage,mMatchAll);//creates Uri with given attributes

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "execeuteOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");
        if(mCallBack!=null){
            mCallBack.onDataAvailable(mPhotoList,DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUri = createUri(params[0], mLanguage, mMatchAll);
        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");
        return mPhotoList;
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll){
        Log.d(TAG, "createUri: starts");

        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags",searchCriteria)
                .appendQueryParameter("tagMode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
        
    }

    @Override //when download is complete, we can parse the data
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: status = " + status);
        if(status == DownloadStatus.OK){
            mPhotoList = new ArrayList<>();
            try{
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");
                for (int i=0; i<itemsArray.length(); i++){
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorID = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");
                    String photoUrl = jsonPhoto.getJSONObject("media").getString("m");
                    String link = photoUrl.replaceFirst("_m.","_b.");

                    Photo photoObject = new Photo(title,author,authorID,link,tags,photoUrl);
                    this.mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: " + photoObject.toString());
                }
            }catch(JSONException e){
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: " + e.getMessage() );
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }
        if(runningOnSameThread && mCallBack != null){
            //inform caller that process is done, or return null if there was an error
            mCallBack.onDataAvailable(mPhotoList, status);
        }
        Log.d(TAG, "onDownloadComplete: ends");

    }
}



























