package com.backendless.hk3.login.kitchen_list;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.hk3.login.LoginActivity;
import com.backendless.hk3.login.R;
import com.backendless.hk3.login.entities.Kitchen;
import com.backendless.hk3.login.kitchen_list.adapter.KitchenListAdapter;
import com.backendless.hk3.login.placingorder.OrderHistoryActivity;
import com.backendless.hk3.login.utility.BackendSettings;
import com.backendless.hk3.login.utility.LoadingCallback;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitchenHomepageActivity extends MyAppBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private BackendlessCollection<Kitchen> kitchen;
    private List<Kitchen> totalKitchens = new ArrayList<>();
    private boolean isLoadingItems = false;
    private RecyclerView recList;
    private KitchenListAdapter adapter;
    private GridLayoutManager glm;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private BackendlessDataQuery query;
    private TextView userName;
    private TextView userEmail;
    private BackendlessUser current_user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_homepage);

        /** Navigation Drawer  */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Log.d("Where", drawer.toString());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);

        userName = (TextView)headerLayout.findViewById(R.id.user_name);
        userEmail = (TextView)headerLayout.findViewById(R.id.user_email);

        /** Lookup the swipe container view */
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setProgressViewOffset(true, 50, 100);
//        swipeRefreshLayout.setRefreshing(true);

        /** Set adapter */
        recList = (RecyclerView) findViewById(R.id.kitchenList);
//        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recList.setLayoutManager(llm);

        /** Using GridLayoutManager to display */
        glm = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == 0 ? 2 : 1);
            }
        });
        recList.setLayoutManager(glm);

        /** If using StaggeredGridLayoutManager */
//        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(3, 1);
//        recList.setLayoutManager(sglm);
//        recList.setLayoutManager(sglm);


        adapter = new KitchenListAdapter(this, totalKitchens);
        recList.setAdapter(adapter);

        /** Connect to Backendless to do database manipulations */
        Backendless.initApp(this, BackendSettings.APPLICATION_ID, BackendSettings.ANDROID_SECRET_KEY, BackendSettings.VERSION);
        current_user = Backendless.UserService.CurrentUser();

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setRelated(Arrays.asList("dish"));

        query = new BackendlessDataQuery(queryOptions);
        retrieveData(query);

        userName.setText((String) current_user.getProperty("name"));
        userEmail.setText(current_user.getEmail());
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

       if (id == R.id.nav_order_history) {
            Intent intent = new Intent(KitchenHomepageActivity.this, OrderHistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_favorite) {
            Intent intent = new Intent(KitchenHomepageActivity.this, FollowedKitchenActivity.class);
            startActivity(intent);

        }  else if (id == R.id.nav_log) {
            userLogout();
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            startActivity(logoutIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRefresh() {
        retrieveData(query);
        onItemLoadComplete();
    }

    public void onItemLoadComplete() {
        adapter.clear();
        adapter.addAll(totalKitchens);
        swipeRefreshLayout.setRefreshing(false);
    }


    public void retrieveData(BackendlessDataQuery query) {
        Backendless.Data.of(Kitchen.class).find(query, new LoadingCallback<BackendlessCollection<Kitchen>>(this, getString(R.string.loading_kitchens), true) {
            @Override
            public void handleResponse(BackendlessCollection<Kitchen> kitchensBackendlessCollection) {
                kitchen = kitchensBackendlessCollection;

                addMoreItems(kitchensBackendlessCollection);

                super.handleResponse(kitchensBackendlessCollection);
            }
        });

        recList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(recList, dx, dy);
                if (dy > 0) {
                    visibleItemCount = glm.getChildCount();
                    totalItemCount = glm.getItemCount();
                    firstVisibleItem = glm.findFirstVisibleItemPosition();

//                    if (firstVisibleItem > this.mLastFirstVisibleItem) {
//                        KitchenHomepageActivity.this.getSupportActionBar().show();
//                    } else if (firstVisibleItem < this.mLastFirstVisibleItem) {
//                        KitchenHomepageActivity.this.getSupportActionBar().hide();
//                    }
//                    this.mLastFirstVisibleItem = firstVisibleItem;

                    if (needToLoadItems(firstVisibleItem, visibleItemCount, totalItemCount)) {
                        isLoadingItems = true;
                        kitchen.nextPage(new LoadingCallback<BackendlessCollection<Kitchen>>(KitchenHomepageActivity.this) {
                            @Override
                            public void handleResponse(BackendlessCollection<Kitchen> nextPage) {
                                kitchen = nextPage;
                                addMoreItems(nextPage);
                                isLoadingItems = false;
                            }
                        });
                    }

                }
            }
        });
    }

    /**
     * Determines whether is it needed to load more items as user scrolls down.
     *
     * @param firstVisibleItem number of the first item visible on screen
     * @param visibleItemCount number of items visible on screen
     * @param totalItemCount   total number of items in list
     * @return true if user is about to reach the end of a list, else false
     */
    private boolean needToLoadItems( int firstVisibleItem, int visibleItemCount, int totalItemCount )
    {
        return !isLoadingItems && totalItemCount != 0 && totalItemCount - (visibleItemCount + firstVisibleItem) < visibleItemCount / 2;
    }

    /**
     * Adds more items to list and notifies Android that dataset has changed.
     *
     * @param nextPage list of new items
     */
    private void addMoreItems( BackendlessCollection<Kitchen> nextPage )
    {
        totalKitchens.addAll( nextPage.getCurrentPage() );
        adapter.notifyDataSetChanged();
    }

}
