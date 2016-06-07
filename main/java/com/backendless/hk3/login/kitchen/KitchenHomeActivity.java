package com.backendless.hk3.login.kitchen;

/**
 * Created by zini on 5/24/16.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import com.backendless.hk3.login.Defaults;
import com.backendless.hk3.login.R;
import com.backendless.hk3.login.entities.Dish;
import com.backendless.hk3.login.entities.DishItem;
import com.backendless.hk3.login.entities.Kitchen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class KitchenHomeActivity extends AppCompatActivity {

    public static final int EDIT_DISH_ITEM = 1;
    public static final int CREATE_DISH_ITEM = 2;
    ImageView kitPicField;
    TextView kitNameField;
    ImageView addDishButton;
    RecyclerView dishRecyclerView;
    LinearLayoutManager llm;
    DishAdapter dishAdapter;
    private Kitchen kitchen;
    private String kitchenID;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dish);
        Backendless.initApp(this, Defaults.APPLICATION_ID,Defaults.SECRET_KEY,Defaults.VERSION);

        kitPicField=(ImageView)findViewById(R.id.kitchenPic);
        kitNameField=(TextView)findViewById(R.id.kitchenName);


        addDishButton=(ImageView)findViewById(R.id.addButton);
        dishRecyclerView=(RecyclerView)findViewById(R.id.recyclerView);

        llm=new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        dishRecyclerView.setLayoutManager(llm);

        Intent intent = getIntent();
        kitchenID = intent.getStringExtra("kitchen_objectID");
        Backendless.Persistence.of(Kitchen.class).findById(kitchenID, new AsyncCallback<Kitchen>() {
            @Override
            public void handleResponse(Kitchen k) {
                kitchen = k;
                Picasso.with(KitchenHomeActivity.this).load(kitchen.getKitchenPic()).into(kitPicField);
                kitNameField.setText(kitchen.getKitchenName());
                List<DishItem> l=null;
                if (k.getDish() != null) {
                    l = k.getDish().getDishItem();

                }
                if (l== null) {
                    l = new ArrayList<DishItem>();
                }

                dishAdapter= new DishAdapter(KitchenHomeActivity.this,l);
                dishRecyclerView.setAdapter(dishAdapter);
                progress.dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("save failed",fault.getMessage()+ " "+fault.getDetail());
                progress.dismiss();

            }
        });
        // TODO: show progress dialog to block UI until kitchen is loaded.
        progress = ProgressDialog.show(KitchenHomeActivity.this, "upload ",
                "uploading", true);
        addDishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent1=new Intent(KitchenHomeActivity.this, CreateOrEditDishActivity.class);
                startActivityForResult(intent1,CREATE_DISH_ITEM);

            }
        });





    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            if (requestCode == CREATE_DISH_ITEM) {
                String dashItemId = data.getStringExtra("dishItemId");
                Backendless.Persistence.of(DishItem.class).findById(dashItemId, new AsyncCallback<DishItem>() {
                    @Override
                    public void handleResponse(DishItem di) {
                        Dish d = kitchen.getDish();
                        List<DishItem> l = new ArrayList<>(d.getDishItem());
                        l.add(di);
                        d.setDishItem(l);
                        dishAdapter.setData(l);
                        Backendless.Persistence.save(d, new AsyncCallback<Dish>() {
                            @Override
                            public void handleResponse(Dish response) {
                                Log.i("save", "succeed");
                                progress.dismiss();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                    }
                });
            } else if (requestCode == EDIT_DISH_ITEM) {
                Backendless.Persistence.of(Kitchen.class).findById(kitchenID, new AsyncCallback<Kitchen>() {
                    @Override
                    public void handleResponse(Kitchen k) {
                        kitchen = k;
                        dishAdapter.setData(k.getDish().getDishItem());
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.i("Kitchen not found",fault.getMessage()+" "+fault.getDetail());
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.buyer_action_flow,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        switch (id){
            case R.id.view_order:
                Intent intent = new Intent(KitchenHomeActivity.this,ViewOrdersActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
