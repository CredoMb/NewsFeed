package com.example.android.newsfeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.android.newsfeed.Data.ArticleAdapter;
import com.example.android.newsfeed.Data.ArticleLoader;
import com.example.android.newsfeed.Data.QueryUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    /*The Article Loader ID*/
    private static int LOADER_ID = 1;

    /*The base url that will be used to request
     data from the Guardian API*/

    private String  GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?";

    /* The recycler view will contain
    *  a list of articles. Each article
    *  is built using the "news_list_item.xml" layout */
    private RecyclerView mArticlesList;

    /*Will be used to populate data to the
    * RecyclerView
    * */
    private ArticleAdapter mAdapter;

    /* The variable will store the spinner
    *  used to express that the app is
    *  working */
    private ProgressBar mProgressSpinner;

    /*
     * The group view that will contain the
     * empty state for a bad internet connection
     */

    private RelativeLayout emptyStateRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inflate the RecyclerView
        mArticlesList = (RecyclerView) findViewById(R.id.rv_articles);

        ArrayList<Article> data =
        QueryUtils.extractArticles(QueryUtils.theSampleJson());

        // Create a LinearLayoutManager and attach it to the
        // recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mArticlesList.setLayoutManager(layoutManager);

        //Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView

        mArticlesList.setHasFixedSize(true);

        // Initialize the adapter and attach it
        // to our RecyclerView
        mAdapter = new ArticleAdapter(this, data);
        mArticlesList.setAdapter(mAdapter);

        // Store the progress spinner
        // inside this variable.
        mProgressSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

    }

    /**
     * Checks the Network connection and return true or false
     * based on the network connection state
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        // Remove the empty state view
        emptyStateRl.setVisibility(View.GONE);

        // Set the visibility of the spinner.
        mProgressSpinner.setVisibility(View.VISIBLE);

        // The key can not appear on github as this is a public repo
        String API_KEY = "";
        String fields_to_show = "thumbnail,trailText";

        // Make an Uri Builder with the GUARDIAN_REQUEST_URL as the base Uri
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Add query parameters to the base Uri.
        // show-fields : determine which additional field will be part of the result.
        // In our case, we will need two additional fields: the thumbnail and the trailText
        // api-key : The key will unlock the access to the API.

        uriBuilder.appendQueryParameter("q","obama");
        uriBuilder.appendQueryParameter("show-fields", fields_to_show);
        uriBuilder.appendQueryParameter("api_key", API_KEY);

        // I should make an empty state with a "no_result found" type of
        // message.
        return new ArticleLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {

    }

    /** Execute certain task based on the internet connection status.
     * If the connection is on, initiate the loader
     * other wise, display the empty state view
     * */

    private void startLoaderOrEmptyState(int loaderId) {

        // Check the status of the network, then either launch the Loader or
        // display the Empty State

        if (isNetworkConnected()) {
            getSupportLoaderManager().initLoader(loaderId, null, this).forceLoad();
        } else {

            //.setVisibility(View.GONE);
            //emptyStateRl.setVisibility(View.VISIBLE);
        }
    }
}
