package com.example.android.booklistingapp;

import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.booklistingapp.R.id.emptyview;
import static com.example.android.booklistingapp.R.id.search;

public class MainActivity extends AppCompatActivity {

    String searchUrlBasis = "https://www.googleapis.com/books/v1/volumes?";
    BookAdapter adapter;
    TextView emptyView;
    ListView listview;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create ArrayList of Book objects
        ArrayList<Book> books = new ArrayList<Book>();
        //find ListView for the visualization of the Book objects
        listview = (ListView) findViewById(R.id.listview);
        //create BookAdapter with the ArrayList of the Book objects and set it to the ListView
        adapter = new BookAdapter(this, books);
        listview.setAdapter(adapter);
        //set an empty text field to the ListView which is showed when no search term is entered
        emptyView = (TextView) findViewById(R.id.emptyview);
        listview.setEmptyView(emptyView);
        emptyView.setText(getResources().getString(R.string.empty_state));
        //find progressBar for later actions
        progress = (ProgressBar) findViewById(R.id.progress);
    }

    //set the menu for the Activity, in which only a search button is added to the app bar
    // to search for books
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // implementation found on the android developer website
        // meant to make sure the added search button in the app bar
        //works, i.e. upon clicking the icon, a textfield shows to enter the search term
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        //set Listener to get notified when the user pushes the enter button after typing
        // their search term. After pushing the enter button, the query is generated
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //set visibile progressbAr to let the user know that data is loading
                progress.setVisibility(View.VISIBLE);
                //create Uri
                Uri baseUri = Uri.parse(searchUrlBasis);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("q", query);

                Log.v("TEST. URL IS ","" + uriBuilder);
                //remove the keyboard from the screen
                searchView.clearFocus();

                //check internet connection of the device
                ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo nw = cm.getActiveNetworkInfo();
                boolean isConnected = nw != null && nw.isConnectedOrConnecting();
                if(isConnected){
                    //execute AsyncTask to retrieve the list of books depending on the search input
                    // of the user
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(uriBuilder.toString());
                } else{
                    //clear adapter and thus also the listview to show the emptyview textview, with
                    // a text to explain the user that there is no internet connection. The progressbar
                    // is also set to gone because no data is loading
                    adapter.clear();
                    listview.setAdapter(adapter);
                    progress.setVisibility(View.GONE);
                    emptyView.setText(getResources().getString(R.string.no_internet));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    //background thread for executing the http request and processing the JSON response
    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(String... params) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (params.length < 1 || params[0] == null) {
                return null;
            }
            // create the List of Book objects out of the JSON response
            List<Book> result = HelperMethods.fetchBooks(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            // Clear the adapter of previous earthquake data and hide the progressbar
            adapter.clear();
            progress = (ProgressBar) findViewById(R.id.progress);
            progress.setVisibility(View.GONE);

            // If there is a valid list of Book objects, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (books != null && !books.isEmpty()) {
                adapter.addAll(books);
            }
            emptyView.setText(getResources().getString(R.string.empty_state));
        }
    }
}
