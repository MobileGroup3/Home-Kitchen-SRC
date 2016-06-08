//package com.backendless.hk3.login.kitchen;
//
//import android.app.Activity;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//
//import com.backendless.hk3.login.R;
//import com.backendless.hk3.login.entities.Order;
//import com.backendless.hk3.login.entities.OrderItem;
//
//import java.util.List;
//
///**
// * Created by zini on 6/5/16.
// */
//public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.MyViewHolder> {
//    private List<OrderItem> orderItemList;
//    private Order order;
//    private Activity activity;
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    public ViewOrderAdapter (Activity a, List<OrderItem>orderItemList){
//        this.activity=a;
//        this.orderItemList=orderItemList;
//    }
//
//    class MyViewHolder extends RecyclerView.ViewHolder{
//        TextView customerNameField;
//        TextView dishNameField;
//        TextView quatityField;
//        TextView pickupTimeField;
//        TextView totalField;
//        Button confirmation;
//
//        public MyViewHolder(final View itemView){
//            super(itemView);
////            customerNameField=(TextView) itemView.findViewById(R.id.customer_name);
////            dishNameField=(TextView) itemView.findViewById(R.id.dish_name);
////            quatityField=(TextView)itemView.findViewById(R.id.dish_quantity);
////            pickupTimeField=(TextView)itemView.findViewById(R.id.pickuptime);
////            totalField=(TextView)itemView.findViewById(R.id.total);
//            confirmation=(Button)itemView.findViewById(R.id.confirmBtn);
//        }
//    }
//
//    @Override
//    public int getItemCount(){
//        return orderItemList.size();
//    }
//}
