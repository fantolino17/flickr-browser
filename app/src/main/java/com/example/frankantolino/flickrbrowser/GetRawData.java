package com.example.frankantolino.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus{IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK};

/**
 * Created by Frank on 1/8/2017.
 */

class GetRawData extends AsyncTask<String, Void, String> {
    public static final String TAG = "GetRawData";
    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mcallBack;//to reference mainactivity

    interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    public GetRawData(OnDownloadComplete callBack) {
        this.mDownloadStatus = DownloadStatus.IDLE;
        this.mcallBack = callBack;
    }

    void runInSameThread(String s){
        Log.d(TAG, "runInSameThread: starts");
        //onPostExecute(doInBackground(s));
        if(mcallBack != null){
            String result = doInBackground(s);
            mcallBack.onDownloadComplete(result, mDownloadStatus);
        }
        Log.d(TAG, "runInSameThread: ends");
    }

    @Override//After data is downloaded, onPostExecute is called,
    // then callBack's(mainactivity)'s ondownloadcomplete method is called, so that onDownloadComplete
    //doesnt need to be called in GetRawData class, (mainActivity can do whatever it wants with this data)
    protected void onPostExecute(String s) {
        //Log.d(TAG, "onPostExecute: Parameter is " + s);
        if(mcallBack!=null){
            mcallBack.onDownloadComplete(s,mDownloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override //Gets URL passed in as a String in params[0]; creates URL object from this string, opensConnection, gets inputStream,
    // passes it to inputStream reader, which passes that object to buffered reader,  reader reads line at a time until null and appends
    // each line to variable result, (finally is called to close reader and connection), then result is returned
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if(params == null){
            mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }

        try{
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(params[0]);//URL string is passed in as a parameter, make it URL type
            connection = (HttpURLConnection) url.openConnection(); //Open url connection as HttpURLConnection
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: response code was " + response);
            StringBuilder result = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));//get InputStream from (HttpUrlConnection)connection

            String line;
            while(null != (line = reader.readLine())){//read line from the bufferedReader,which has inputStream from inputStreamReader, which gets inputStream from HttpURLConnection connection
                result.append(line).append("\n");//add line to result
            }
            mDownloadStatus = DownloadStatus.OK;
            return result.toString();

        }catch(MalformedURLException e){
            Log.e(TAG, "doInBackground: INVALID URL" + e.getMessage() );
        }catch(IOException e){
            Log.e(TAG, "doInBackground: IOEXCEPTION" + e.getMessage() );
        }catch(SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception, Needs permission?" + e.getMessage() );
        }finally{//Always called, whether exception is thrown or not, so its good to close connection and readers in here. (Called just before return statements, (Line 58) )
            if(connection!=null){
                connection.disconnect();
            }
            if(reader!=null){
                try{
                    reader.close();
                }catch(IOException e){
                    Log.e(TAG, "doInBackground: error closing reader" + e.getMessage() );
                }
            }
        }
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }


}
