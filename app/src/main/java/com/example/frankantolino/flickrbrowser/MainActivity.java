package com.example.frankantolino.flickrbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.onDataAvailable,
                                                    RecyclerItemClickListener.OnRecyclerClickListener
{
    private static final String TAG = "MainActivity";
    private FlickrRecyclerViewAdapter mFlickrRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar(false);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        mFlickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(this , new ArrayList<Photo>());
        recyclerView.setAdapter(mFlickrRecyclerViewAdapter);

        Log.d(TAG, "onCreate: Ends");
    }

    @Override
    protected void onResume(){
        Log.d(TAG, "onResume: starts");
        super.onResume();
        GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this,"https://api.flickr.com/services/feeds/photos_public.gne", "en-us",true);
        //getFlickrJsonData.execeuteOnSameThread("android, nougat");
        getFlickrJsonData.execute("android, nougat");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu: returned " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        Log.d(TAG, "onOptionsItemSelected() returned: returned");
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status){
        Log.d(TAG, "onDataAvailable: starts");
        if(status == DownloadStatus.OK){
            mFlickrRecyclerViewAdapter.loadNewData(data);
        }
        else{
            Log.e(TAG, "onDataAvailable: Failed with status" + status );
        }

        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
        Toast.makeText(MainActivity.this, "Normal tap at position" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: starts");
        //Toast.makeText(MainActivity.this, "Long tap at position" + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER, mFlickrRecyclerViewAdapter.getPhoto(position));
        startActivity(intent);
    }
}
