package com.backendless.hk3.login.kitchen_list.adapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.hk3.login.R;
import com.backendless.hk3.login.entities.Kitchen;
import com.backendless.hk3.login.placingorder.PlacingOrderActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by clover on 5/24/16.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {
    private List<Kitchen> kitchens;
    private Context mContext;

    public SearchResultAdapter(Context context, List<Kitchen> kitchens)
    {
        this.mContext = context;
        this.kitchens = kitchens;
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        protected TextView search_kitchenNameView;
        protected TextView search_categoryView;
        protected ImageView search_kitchenPic;

        public SearchViewHolder(View view) {
            super (view);
            search_kitchenNameView = (TextView) view.findViewById( R.id.search_kitchenName );
            search_categoryView = (TextView) view.findViewById( R.id.search_kitchenCategory );
            search_kitchenPic = (ImageView) view.findViewById(R.id.search_imageView);
        }
    }

    @Override
    public int getItemCount() {
        return kitchens.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder kitchenViewHolder, int i) {
        Kitchen item = kitchens.get(i);
        kitchenViewHolder.search_kitchenNameView.setText(item.getKitchenName());
        kitchenViewHolder.search_categoryView.setText(item.getCategory());
        final String objectId = item.getObjectId();

        Picasso.with(mContext).load(item.getKitchenPic()).into(kitchenViewHolder.search_kitchenPic);
        kitchenViewHolder.search_kitchenPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kitchenDetail = new Intent(mContext, PlacingOrderActivity.class);
                kitchenDetail.putExtra("object_id_extra_key", objectId);
                mContext.startActivity(kitchenDetail);
            }
        });
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_search_res, viewGroup, false);
        return new SearchViewHolder(itemView);
    }

    public void setData(List<Kitchen> list) {
        kitchens = list;
        notifyDataSetChanged();
    }

    public void clearData() {
        int size = this.getItemCount();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.kitchens.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }
}

