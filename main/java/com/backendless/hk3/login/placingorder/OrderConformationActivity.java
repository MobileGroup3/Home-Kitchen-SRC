package com.backendless.hk3.login.placingorder;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.hk3.login.R;
import com.backendless.hk3.login.entities.DishItem;
import com.backendless.hk3.login.entities.Order;
import com.backendless.hk3.login.entities.OrderItem;
import com.backendless.hk3.login.entities.SimpleCartItem;
import com.backendless.hk3.login.kitchen_list.KitchenHomepageActivity;
import com.backendless.hk3.login.utility.BackendSettings;
import com.backendless.messaging.BodyParts;

import java.util.ArrayList;
import java.util.List;


public class OrderConformationActivity extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener{
    public static final String CART_LIST_EXTRA_KEY = "cart_list_extra_key";
    public static final String TOTAL_AMOUNT_EXTRA_KEY = "total_amount_extra_key";
    public static final String ADDRESS_EXTRA_KEY = "address_extra_key";
    public static final String PHONE_EXTRA_KEY = "phone_number_extra_key";
    public static final String KITCHEN_OBJECT_ID_EXTRA_KEY = "kitchen_object_id_extra_key";
    public static final String KITCHEN_NAME_EXTRA_KEY = "kitchen_name_extra_key";
    public static final String KITCHEN_EMAIL_EXTA_KEY = "kitchen_email_extra_key";

    ArrayList<SimpleCartItem> simpleCartItemList;
    List<OrderItem> orderList;
    TextView phoneTextView;
    EditText noteEditText;
    ListView orderListView;
    OrderAdapter orderAdapter;
    TextView totalAmountTextView;
    TextView addressTextView;
    String address;
    String phoneNumber;
    Button orderConformButton;
    Button timePickerButton;

    Context context;
    BackendlessUser customer;

    String kitchenObjectId;
    String kitchenName;
    String kitchenEmail;
    double totalAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_conformation);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Backendless.initApp(this, BackendSettings.APPLICATION_ID, BackendSettings.ANDROID_SECRET_KEY, BackendSettings.VERSION);

//        String channelName = "test";
//        Backendless.Messaging.subscribe( channelName,
//                new AsyncCallback<List<Message>>()
//                {
//                    public void handleResponse( List<Message> response )
//                    {
//                        for( Message message : response )
//                        {
//                            String publisherId = message.getPublisherId();
//                            Object data = message.getData();
//                        }
//                    }
//                    public void handleFault( BackendlessFault fault )
//                    {
//                        Toast.makeText( OrderConformationActivity.this, fault.getMessage(), Toast.LENGTH_SHORT ).show();                                       }
//                },
//                new AsyncCallback<Subscription>()
//                {
//                    public void handleResponse( Subscription response )
//                    {
//                        Subscription subscription = response;
//                    }
//                    public void handleFault( BackendlessFault fault )
//                    {
//                        Toast.makeText( OrderConformationActivity.this, fault.getMessage(), Toast.LENGTH_SHORT ).show();
//                    }
//                }
//        );

//        Backendless.Messaging.registerDevice("homekitchen-1330", "default", new AsyncCallback<Void>() {
//            @Override
//            public void handleResponse(Void response) {
//
//            }
//
//            @Override
//            public void handleFault(BackendlessFault fault) {
//
//            }
//        });

        customer = Backendless.UserService.CurrentUser();
//        Backendless.UserService.login("tongying@gmail.com", "123", new AsyncCallback<BackendlessUser>() {
//            @Override
//            public void handleResponse(BackendlessUser response) {
//                customer = response;
//
//            }
//
//            @Override
//            public void handleFault(BackendlessFault fault) {
//
//            }
//        });
//

        context = this;
        timePickerButton = (Button) findViewById(R.id.button_time_picker);
        Intent intent = getIntent();

        totalAmount = intent.getDoubleExtra(TOTAL_AMOUNT_EXTRA_KEY,0);
        totalAmountTextView = (TextView) findViewById(R.id.text_view_order_amount);
        totalAmountTextView.setText("$" + String.valueOf(totalAmount));

        address = intent.getStringExtra(ADDRESS_EXTRA_KEY);
        addressTextView = (TextView) findViewById(R.id.text_view_order_address);
        addressTextView.setText(address);

        phoneNumber = intent.getStringExtra(PHONE_EXTRA_KEY );
        phoneTextView = (TextView) findViewById(R.id.text_view_order_phone);
        phoneTextView.setText(phoneNumber);

        kitchenObjectId = intent.getStringExtra(KITCHEN_OBJECT_ID_EXTRA_KEY);
        kitchenName = intent.getStringExtra(KITCHEN_NAME_EXTRA_KEY);

        noteEditText = (EditText) findViewById(R.id.edit_text_note);

        kitchenEmail = intent.getStringExtra(KITCHEN_EMAIL_EXTA_KEY);


        simpleCartItemList = intent.getParcelableArrayListExtra(CART_LIST_EXTRA_KEY);
        final int cartItemNumber = simpleCartItemList.size();


        orderListView = (ListView) findViewById(R.id.list_view_order_dish);
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this,R.layout.shopping_cart_item_view, orderList);
        orderListView.setAdapter(orderAdapter);

        new AsyncTask<Void,Void,List<OrderItem>>(){
            @Override
            protected List<OrderItem> doInBackground(Void... params) {
                for(int i = 0; i < cartItemNumber; i++) {

                    String menuObjectId = simpleCartItemList.get(i).getDishObjectId();
                    DishItem dishItem  = Backendless.Persistence.of(DishItem.class).findById(menuObjectId);

                    int orderQuantity = simpleCartItemList.get(i).getOrderQuantity();

                    OrderItem orderItem = new OrderItem();
                    orderItem.setDishItem(dishItem);
                    orderItem.setOrderQuantity(orderQuantity);
                    orderList.add(orderItem);

                }
                return orderList;

            }

            @Override
            protected void onPostExecute(List<OrderItem> orderList) {

                //Log.e("orderList",String.valueOf(orderList.size()));
                orderAdapter.setData(orderList);
            }

        }.execute();


        orderConformButton = (Button) findViewById(R.id.button_order_conform);
        orderConformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conformOrder();

            }
        });

    }

    public void conformOrder() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Your Order")
                .setMessage("Do you want to confirm your order?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveOrderToDatabase();
                        //Test , move toast to saveOrderToDataBase later
                        Toast.makeText(context,"Successful",Toast.LENGTH_SHORT).show();
                        sendEmail();
                        Intent homeIntent = new Intent(OrderConformationActivity.this, KitchenHomepageActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);

                    }
                }).
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"Order Canceled",Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void sendEmail() {
        String subject = "New Order";
        String textMessage = "You have a new order coming from HomeKitchen";
        String htmlMessage = "You have a new order coming from HomeKitchen";
        BodyParts bodyParts = new BodyParts(textMessage,htmlMessage);
        AsyncCallback<Void> responder = new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
//                Log.e("success email",response.toString());

            }

            @Override
            public void handleFault(BackendlessFault fault) {
//                Log.e("fail to email",fault.toString());

            }
        };
        Backendless.Messaging.sendEmail(subject, bodyParts,kitchenEmail,responder);

    }

    public void saveOrderToDatabase() {

        final Order order = new Order();
        // May be Needs to modify
        //BackendlessUser customer = Backendless.UserService.findById();
        order.setCustomer(customer);

        String pickTime = timePickerButton.getText().toString();
        order.setPickTime(pickTime);

        order.setKitchen_object_id(kitchenObjectId);
        order.setKitchen_name(kitchenName);

        order.setTotalAmount(totalAmount);

        String note = noteEditText.getText().toString();
        order.setNote(note);

        for(int i = 0; i < orderList.size(); i++) {
            OrderItem orderItem = orderList.get(i);
            DishItem dishItem = orderItem.getDishItem();
            int orderQuantity = orderItem.getOrderQuantity();
            int max_num = dishItem.getMax_num();
            max_num = max_num - orderQuantity;
            dishItem.setMax_num(max_num);
            dishItem.getUpdated();
            orderItem.setDishItem(dishItem);

        }

        order.setOrderItem(orderList);

        order.saveAsync(new AsyncCallback<Order>() {
            @Override
            public void handleResponse(Order response) {
                Log.e("Test", "Succeed: " + response.toString());

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("Test", "Failed: " + fault.toString());

            }
        });

    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setTimePickListener(this);
        newFragment.show(getSupportFragmentManager(),"timePicker");

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hourDisplay = String.valueOf(hourOfDay);
        String minuteDisplay = String.valueOf(minute);
        if(minute == 0) {
            minuteDisplay = "00";
        }
        if(hourOfDay == 0) {
            hourDisplay = "00";
        }
        String time = hourDisplay + " : " + minuteDisplay;
        timePickerButton.setText(time);


    }
}
