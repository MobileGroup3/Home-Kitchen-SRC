package com.backendless.hk3.login.kitchen_list;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.hk3.login.R;
import com.backendless.hk3.login.entities.Kitchen;
import com.backendless.hk3.login.kitchen_list.adapter.SearchResultAdapter;
import com.backendless.hk3.login.utility.BackendSettings;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends MyAppBaseActivity {
    private List<Kitchen> totalKitchens = new ArrayList<>();
    private TextView searchTitle;
    private TextView resultTitle;
    private LinearLayoutManager llm;
    private SearchResultAdapter searchResultAdapter;
    private RecyclerView searchRecyclerView;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());

        mContext = getApplicationContext();
        searchTitle = (TextView) findViewById(R.id.search_page_title );
        resultTitle = (TextView) findViewById(R.id.search_res_title);

        searchRecyclerView = (RecyclerView) findViewById(R.id.search_result);
        llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecyclerView.setLayoutManager(llm);
        searchResultAdapter = new SearchResultAdapter(this, totalKitchens);
        searchRecyclerView.setAdapter(searchResultAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        Backendless.initApp( this, BackendSettings.APPLICATION_ID, BackendSettings.ANDROID_SECRET_KEY, BackendSettings.VERSION );

        String whereClause = "kitchenName = '" + query + "'";
        final BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Log.d("Search query is: ", dataQuery.toString());

        new AsyncTask<Void,Void,List<Kitchen>>() {
            @Override
            protected List<Kitchen> doInBackground(Void... params) {
                BackendlessCollection<Kitchen> result = Backendless.Persistence.of(Kitchen.class).find(dataQuery);
                if (result.getTotalObjects() > 0) {
                    return result.getData();
                }
                else {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Kitchen> kitchens) {
                if (kitchens != null) {
                    resultTitle.setVisibility(View.VISIBLE);
                    searchTitle.setVisibility(View.INVISIBLE);
                    searchResultAdapter.setData(kitchens);
                }
                else {
                    searchResultAdapter.clearData();
                    resultTitle.setVisibility(View.INVISIBLE);
                    searchTitle.setVisibility(View.VISIBLE);
                }

            }
        }.execute();
    }

}
