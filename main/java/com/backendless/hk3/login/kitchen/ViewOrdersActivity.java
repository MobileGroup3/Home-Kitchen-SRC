package com.backendless.hk3.login.kitchen;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.exceptions.BackendlessFault;


import com.backendless.hk3.login.DefaultCallback;
import com.backendless.hk3.login.R;
import com.backendless.hk3.login.entities.Order;
import com.backendless.hk3.login.entities.OrderItem;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.List;

/**
 * Created by zini on 6/4/16.
 */
public class ViewOrdersActivity extends AppCompatActivity{

    private List<Order> allOrders;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_order);
        listView = (ListView) findViewById(R.id.listView);
        BackendlessDataQuery query = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addRelated( "customer" );
        queryOptions.addRelated( "orderItem" );
        queryOptions.addRelated( "orderItem.dishItem" );
        query.setQueryOptions( queryOptions );

        Backendless.Data.of(Order.class).find(query, new DefaultCallback<BackendlessCollection<Order>>(this, "Loading Orders") {
            @Override
            public void handleResponse(BackendlessCollection<Order> response) {
                super.handleResponse(response);
                allOrders = response.getCurrentPage();
                listView.setAdapter(new OrderAdapter(ViewOrdersActivity.this, R.layout.view_order_custom_row, allOrders));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                super.handleFault(fault);
                Toast.makeText(ViewOrdersActivity.this, "Failed loading orders.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class OrderAdapter extends ArrayAdapter<Order> {

        public OrderAdapter(Context context, int resource, List<Order> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.view_order_custom_row, null);
            }

            final Order order = getItem(position);


            TextView customerNameView = (TextView) v.findViewById(R.id.customer_name);
            customerNameView.setText(order.getCustomer().getEmail());
            TextView orderDetailView = (TextView) v.findViewById(R.id.order_detail_text);

            StringBuffer sb = new StringBuffer();
            for (OrderItem di : order.getOrderItem()) {
                String dishName = di.getDishItem().getName();
                Integer orderQuantity = di.getOrderQuantity();
                sb.append(dishName).append(" X ").append(orderQuantity).append("\n");
            }
            orderDetailView.setMaxLines(order.getOrderItem().size());
            orderDetailView.setText(sb.toString());

            TextView pickupTimeView = (TextView) v.findViewById(R.id.pickuptime_text);
            pickupTimeView.setText(order.getPickTime().toString());


            Button confirmBtn = (Button) v.findViewById(R.id.confirmBtn);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Backendless.Messaging.sendHTMLEmail("Order Confirmed", buildEmailHTML(order), order.getCustomer().getEmail(),
                            new DefaultCallback<Void>(ViewOrdersActivity.this, "Sending confirmation to " + order.getCustomer().getEmail()) {
                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    super.handleFault(fault);
                                    Toast.makeText(ViewOrdersActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void handleResponse(Void response) {
                                    super.handleResponse(response);
                                    Toast.makeText(ViewOrdersActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            return v;
        }
    }

    private String buildEmailHTML(Order o) {
        StringBuffer sb = new StringBuffer();
        for (OrderItem di : o.getOrderItem()) {
            String dishName = di.getDishItem().getName();
            Integer orderQuantity = di.getOrderQuantity();
            sb.append(dishName).append(" X ").append(orderQuantity).append("<br/>");
        }
        return sb.toString();
    }

}
