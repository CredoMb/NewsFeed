package com.example.android.newsfeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.newsfeed.Data.QueryUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity
{

/*implements LoaderManager.LoaderCallbacks<List<Article>>*/

    private static int LOADER_ID = 1;

    // Create the base URL and then,
    // test the loader with the base Url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QueryUtils.extractArticles(QueryUtils.theSampleJson());
    }

    /*
    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {

    }*/
}
