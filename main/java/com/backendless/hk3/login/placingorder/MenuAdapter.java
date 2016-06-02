package com.backendless.hk3.login.placingorder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.hk3.login.R;
import com.backendless.hk3.login.entities.DishItem;
import com.backendless.hk3.login.entities.Kitchen;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public enum  holderType {ITEM_TYPE_HEADER, ITEM_TYPE_DISH_LIST}

    private List<DishItem> dishItemList;
    private Context context;
    private DishAddedListener dishAddedListener;
    private Kitchen kitchen;
    private boolean flag;

    public MenuAdapter(Context context, List<DishItem> dishItemList, Kitchen kitchen, DishAddedListener dishAddedListener,boolean flag) {
        this.context = context;
        this.dishItemList = dishItemList;
        this.dishAddedListener = dishAddedListener;
        this.kitchen = kitchen;
        this.flag = flag;

    }

    public class HeaderHolder extends RecyclerView.ViewHolder{
        ImageView kitchenThumbImageView;
        TextView kitchenNameTextView;
        Button addressButton;
        Button phoneNumberButton;
        ImageView followedKitchenImageView;

        public HeaderHolder(View headerView) {
            super(headerView);
            kitchenThumbImageView = (ImageView) headerView.findViewById(R.id.image_view_kitchen_thumb);
            kitchenNameTextView = (TextView) headerView.findViewById(R.id.text_view_kitchen_name);
            addressButton = (Button) headerView.findViewById(R.id.button_address);
            phoneNumberButton = (Button) headerView.findViewById(R.id.button_phone_number);
            followedKitchenImageView = (ImageView) headerView.findViewById(R.id.image_view_follow_kitchen);

        }

        public void bindHeader(Kitchen kitchen, final boolean flag) {

            Picasso.with(context).load(kitchen.getKitchenPic()).into(kitchenThumbImageView);

            String name = kitchen.getKitchenName();
            kitchenNameTextView.setText(name);

            final String address = kitchen.getStreet() + ", " + kitchen.getCity() + ", " + kitchen.getZipcode();
            addressButton.setText(address);
            addressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    context.startActivity(mapIntent);
                }
            });

            //phoneNumberButton.setText(kitchen.getPhoneNumber());
            final String phoneNumber = kitchen.getPhoneNumber();
            phoneNumberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    context.startActivity(callIntent);
                }
            });


            if(flag) {
                followedKitchenImageView.setImageResource(R.drawable.followed_icon);

            }else {
                followedKitchenImageView.setImageResource(R.drawable.unfollow_icon);
            }


            followedKitchenImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(flag) {
                        setFlagData(false);
                        followedKitchenImageView.setImageResource(R.drawable.unfollow_icon);
                        dishAddedListener.deleteFollowedKitchen();;
                    }else {
                        setFlagData(true);
                        followedKitchenImageView.setImageResource(R.drawable.followed_icon);
                        dishAddedListener.addFollowedKitchen();
                    }

                }
            });



        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImageView;
        TextView dishNameTextView;
        TextView dishDescriptionTextView;
        TextView dishPriceTextView;
        TextView dishRemainingNumberTextView;
        Button dishAddButton;


        public MyViewHolder(final View itemView) {
            super(itemView);
            dishImageView = (ImageView) itemView.findViewById(R.id.image_view_dish);
            dishNameTextView = (TextView) itemView.findViewById(R.id.text_view_dish_name);
            dishDescriptionTextView = (TextView) itemView.findViewById(R.id.text_view_dish_description);
            dishPriceTextView = (TextView) itemView.findViewById(R.id.text_view_dish_price);
            dishRemainingNumberTextView = (TextView) itemView.findViewById(R.id.text_view_remaining_number);
            dishAddButton = (Button) itemView.findViewById(R.id.button_add_dish);


        }

        public void bind(final DishItem dish) {
            dishNameTextView.setText(dish.getName());
            dishDescriptionTextView.setText(dish.getDescription());
            dishPriceTextView.setText(String.valueOf(dish.getPrice()));
            dishRemainingNumberTextView.setText(String.valueOf(dish.getMax_num()));
            Picasso.with(context).load(dish.getPicture()).into(dishImageView);

            dishAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double dishPrice = dish.getPrice();
                    int dishNumber = dish.getMax_num();

                    if(dishNumber == 0) {
                        Toast.makeText(itemView.getContext(),"Sold Out",Toast.LENGTH_SHORT).show();

                    }else {
                        dishNumber--;
                        dish.setMax_num(dishNumber);
                        dishAddedListener.onDishAdded(dish);

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dishItemList.size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == holderType.ITEM_TYPE_HEADER.ordinal()) {
            View headerView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.header_placing_order_item,parent,false);
            return new HeaderHolder(headerView);

        }else {
            View itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.menu_view,parent,false);
            return new MyViewHolder(itemView);

        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bindHeader(kitchen,flag);

        }else if(holder instanceof MyViewHolder){
            DishItem dishItem = dishItemList.get(position - 1);
            Picasso.with(context).load(dishItem.getPicture()).into( ((MyViewHolder) holder).dishImageView);
           ((MyViewHolder) holder).bind(dishItem);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? holderType.ITEM_TYPE_HEADER.ordinal() : holderType.ITEM_TYPE_DISH_LIST.ordinal();
    }

    public void setData(List<DishItem> list,Kitchen myKitchen) {
        dishItemList = list;
        kitchen = myKitchen;
        notifyDataSetChanged();

    }

    public void setFlagData(boolean followedFlag) {
        flag = followedFlag;
        notifyDataSetChanged();
    }

}
