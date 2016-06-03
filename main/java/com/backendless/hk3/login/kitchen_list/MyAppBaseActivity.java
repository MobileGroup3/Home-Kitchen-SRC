package com.backendless.hk3.login.kitchen_list;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.backendless.Backendless;
import com.backendless.exceptions.BackendlessException;
import com.backendless.hk3.login.LoginActivity;
import com.backendless.hk3.login.R;
import com.backendless.hk3.login.utility.BackendSettings;

/**
 * Created by clover on 6/2/16.
 */
public abstract class MyAppBaseActivity extends AppCompatActivity{
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
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
        if (id == R.id.search) {
            Intent searchIntent = new Intent(this, SearchableActivity.class);
            startActivity(searchIntent);
//            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        else if (id == R.id.likes) {
            Intent likedKitchen = new Intent(this, FollowedKitchenActivity.class);
            startActivity(likedKitchen);
        }

        else if (id == R.id.logout) {
            userLogout();
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            startActivity(logoutIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();
        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void userLogout () {
        Backendless.initApp(this, BackendSettings.APPLICATION_ID, BackendSettings.ANDROID_SECRET_KEY, BackendSettings.VERSION);
        try {
            Backendless.UserService.logout();
        } catch (BackendlessException exception) {
            exception.printStackTrace();
        }
    }
}
