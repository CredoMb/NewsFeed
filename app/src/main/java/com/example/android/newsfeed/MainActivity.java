package com.example.android.newsfeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.app.LoaderManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.android.newsfeed.Data.ArticleAdapter;
import com.example.android.newsfeed.Data.ArticleLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>>,
        ArticleAdapter.ArticleAdapterOnClickHandler{

    /* The Key of the Web Intent Extra*/

    private final String ARTICLE_LINK = "article_link";

    /*The Article Loader ID*/
    private static int LOADER_ID = 1;

    /*The base url that will be used to request
     data from the Guardian API*/

    // I want my shit back, please!
    private String  GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?";

    /*Represent the topic to get from the API.
     * It will be added to the "GUARDIAN_REQUEST_URL",
     * it's could either be "search" or "news".
     * "news" is the default value because the application
     *  will start by displaying the news.
    * */
    private String mTopic ;

    /*The following values are the two
    * possible values that mTopic could have */
    private static String NEWS_TOPIC = "news";
    private static String SEARCH_TOPIC = "search";

    /* The recycler view will contain
     *  a list of articles. Each article
     *  is built using the "news_list_item.xml" layout */
    private RecyclerView mArticlesRv;

    /*Will be used to populate data to the
     * RecyclerView
     * */
    private ArticleAdapter mAdapter;

    /*  Will store the topic that will
     *   be entered by the user inside the
     *   search bar */
    private String mArticleTopic;

    /* The variable will store the spinner
     *  used to express that the app is
     *  working */
    private ProgressBar mProgressSpinner;

    /*
     * The group view that will contain the
     * empty state for a bad internet connection
     */

    private RelativeLayout mEmptyStateRl;

    /* The refresh TextView of the Empty
    *  State. Once clicked, it will restart
    *  the loader. */

    private TextView mRefreshTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inflate the RecyclerView
        mArticlesRv = (RecyclerView) findViewById(R.id.rv_articles);

        // Create a LinearLayoutManager and attach it to the
        // recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //Use this setting to improve performance if you know that changes in content do not
        // change the child layout size in the RecyclerView
        mArticlesRv.setHasFixedSize(true);

        // Set the layout manager onto the Recycler View
        mArticlesRv.setLayoutManager(layoutManager);

        // Initialize the adapter and attach it
        // to our RecyclerView
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>(),this);
        mArticlesRv.setAdapter(mAdapter);

        // Store the progress spinner
        // inside this variable.
        mProgressSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

        // Find and store the empty state group View
        mEmptyStateRl = (RelativeLayout) findViewById(R.id.empty_group_view);

        // The refresh textView from the empty State
        mRefreshTv = findViewById(R.id.refresh_tv);

        // Will start the loader
        mRefreshTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoaderOrEmptyState(LOADER_ID);
            }
        });

        // Set the default topic to "news"
        // so that the app will start by displaying
        // news articles.
        mTopic = NEWS_TOPIC;

        // Based on the internet connection, either start the loader
        // or display the "no internet" empty state view.
        startLoaderOrEmptyState(LOADER_ID);

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

    /**
     * This method contains the logic
     * to handle a click on a adapter item.
     *
     * @param position represent the position of the item that has been clicked on
     */
    @Override
    public void OnClick(int position) {

        // Extract the link of the clicked item
        String articleLink =  mAdapter.getArticleData().get(position).getFullArticleUrl();

        // Create the web intent
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(articleLink));

        // Check if there's an app available to resolve
        // the intent, then execute it.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /** onCreateOptionsMenu is called by the system to create the menu items. This will
     *  make the search bar appear on the AppBar.
     *
     *  @param menu the menu in which we place the search bar
     *
     * */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main_activity.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        // Find the menuItem that corresponds to the search View
        // and store it inside the searchItem variable
        MenuItem searchItem = menu.findItem(R.id.menu_search_view);


        // From the searchItem, which is a menu Item get the searchView
        // and store it into a variable
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Set a custom typeface to the searchView Text.
        // This will match the font of the text typed by the user
        // with the font of the texts in the app

        /*// Get the id of the SearchView's textView
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        // Set a custom Type Face on the searchView's TextView
        setCustomTypeFace(searchView, id,R.font.avenir_nextltpro_regular);*/

        // Prevent the searchView to take a full screen size when the device is on landscape
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                // Make the Empty View Invisible
                /*mEmptyStateView.setVisibility(View.INVISIBLE);*/

                // Display the progress spinner while the user is waiting for results
                mProgressSpinner.setVisibility(View.VISIBLE);

                // Destroy the previous loader so the system can create a new link
                getLoaderManager().destroyLoader(LOADER_ID);

                // After the user has submitted his search word,
                // insert the word inside the mArticleTopic variable.
                // Change the topic to "search".
                mArticleTopic = s;
                mTopic = SEARCH_TOPIC;

                // Based on the network connection status, start the loader
                // or display the empty state view
                startLoaderOrEmptyState(LOADER_ID);

                /*startLoaderOrEmptyState(R.drawable.empty_state_no_internet,
                        R.string.no_internet_title,
                        R.string.no_internet_subtitle);*/

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // How to wait until the person presses enter ?
                return true;
            }
        });
        return true;
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {

        // Remove the empty state view
        mEmptyStateRl.setVisibility(View.GONE);

        // Set the visibility of the spinner.
        mProgressSpinner.setVisibility(View.VISIBLE);

        // The key can not appear on github as this is a public repo
        String API_KEY = "";
        String fields_to_show = "thumbnail,trailText";

        // Le ration pixel - ecran : Je m'en rappelle
        
        // Make an Uri Builder with the GUARDIAN_REQUEST_URL as the base Uri
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL + mTopic);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Add query parameters to the base Uri.
        // show-fields : determine which additional field will be part of the result.
        // In our case, we will need two additional fields: the thumbnail and the trailText
        // api-key : The key will unlock the access to the API.

        if (mTopic == SEARCH_TOPIC) {
            uriBuilder.appendQueryParameter("q",mArticleTopic.trim());
        }

        uriBuilder.appendQueryParameter("show-fields", fields_to_show);
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        // Qu'est ce qui se passe lorsqu'il n'y
        // Return a new AsyncTaskLoader <List<Article>>.
        // The Loader will use the link we've built Previously
        return new ArticleLoader(this,uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {

        mProgressSpinner.setVisibility(View.GONE);

        // Clear the adapter by setting an empty ArrayList
        mAdapter.setArticleData(null);

        /*
         If there is a valid list of {@link Article}s, then add them to the adapter's
         data set. This will trigger the RecyclerView to update, as the notifyDataSetChanged()
         is called inside the "setArticleData" .*/

        if (data != null && !data.isEmpty()) {
            mAdapter.setArticleData(data);
        }
        // If there are no data returned,
        // show the "no result" empty state
        else {
                fillEmptyStateView(R.drawable.no_result_image,
                        R.string.no_result_title,
                        R.string.no_result_text,
                        R.string.close);

                mEmptyStateRl.setVisibility(View.VISIBLE);
                // Set back the topic to "news"
                mTopic = NEWS_TOPIC;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {

        // Add the click listener later, bitch !
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>(),this);
        mArticlesRv.setAdapter(mAdapter);

        // If there's no internet connection display the emptystate view
        if (!isNetworkConnected()) {

            fillEmptyStateView(R.drawable.no_internet_image, R.string.no_result_title,
                    R.string.no_internet_text, R.string.refresh_text);

            mEmptyStateRl.setVisibility(View.VISIBLE);
        }

    }

    /** Execute certain task based on the internet connection status.
     * If the connection is on, initiate the loader
     * other wise, display the empty state view
     * */

    private void startLoaderOrEmptyState(int loaderId) {

        // Check the status of the network, then either launch the Loader or
        // display the "No internet" Empty State

        if (isNetworkConnected()) {
            getLoaderManager().initLoader(loaderId, null, MainActivity.this).forceLoad();
        } else {

            fillEmptyStateView(R.drawable.no_internet_image, R.string.no_result_title,
                    R.string.no_internet_text, R.string.refresh_text);

            mEmptyStateRl.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method will help to fill the Image View and
     * the two textViews of the emptyState Group View
     */
    private void fillEmptyStateView(int emptyStateImageId, int emptyStateTitleId
            , int emptyStateSubtitleId, int emptyStateActionTextId) {

        // Set the correct image into the empty state image view
        ImageView emptyStateImage = (ImageView) mEmptyStateRl.findViewById(R.id.empty_state_image);
        emptyStateImage.setImageResource(emptyStateImageId);

        // Set the correct text into the empty state title text
        TextView emptyStateTitleText = (TextView) mEmptyStateRl.findViewById(R.id.empty_state_title);
        emptyStateTitleText.setText(emptyStateTitleId);

        // Set the correct text into the empty state subtitle text
        TextView emptyStateSubTitleText = (TextView) mEmptyStateRl.findViewById(R.id.empty_state_subtitle);
        emptyStateSubTitleText.setText(emptyStateSubtitleId);

        // Set the text of the textView that represent the possible action
        // either "refresh" or "close".
        TextView emptyStateAction = (TextView) mEmptyStateRl.findViewById(R.id.refresh_tv);
        emptyStateAction.setText(emptyStateActionTextId);
    }
}
