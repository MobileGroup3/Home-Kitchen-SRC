package com.backendless.hk3.login.kitchen_list;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
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

public class SearchableActivity extends AppCompatActivity {
    private List<Kitchen> totalKitchens = new ArrayList<>();
    private TextView searchTitle;
    private LinearLayoutManager llm;
    private SearchResultAdapter searchResultAdapter;
    private RecyclerView searchRecyclerView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        handleIntent(getIntent());

        mContext = getApplicationContext();
        searchTitle = (TextView) findViewById(R.id.search_page_title );

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
                    searchTitle.setText("Find Following:");
                    searchResultAdapter.setData(kitchens);
//                    final String objectId = kitchen.getObjectId();
//                    Log.d("object id is: ", objectId);
//                    searchTitle.setText(kitchen.getKitchenName());
//                    searchTitle.setVisibility(View.VISIBLE);
//                    Picasso.with(getApplicationContext()).load(kitchen.getKitchenPic()).into(kitchenPicView);
//                    kitchenPicView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent kitchenDetail = new Intent(getApplicationContext(), PlacingOrderActivity.class);
//                            kitchenDetail.putExtra("object_id_extra_key", objectId);
//                            kitchenDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            getApplicationContext().startActivity(kitchenDetail);
//                        }
//                    });
                }
                else {
                    findViewById(R.id.search_page_title).setVisibility(View.VISIBLE);

                }

            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.likes) {
            Intent likedKitchen = new Intent(SearchableActivity.this, FollowedKitchenActivity.class);
            startActivity(likedKitchen);
        }
        return super.onOptionsItemSelected(item);
    }
}
