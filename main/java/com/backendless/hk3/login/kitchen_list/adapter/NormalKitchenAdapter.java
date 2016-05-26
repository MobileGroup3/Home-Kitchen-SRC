package com.backendless.hk3.login.kitchen_list.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.hk3.login.R;
import com.backendless.hk3.login.entities.Kitchen;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.backendless.hk3.login.placingorder.PlacingOrderActivity;

/**
 * Created by clover on 5/25/16.
 */
public class NormalKitchenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static enum ITEM_TYPE {ITEM_TYPE_HEADER, ITEM_TYPE_KITCHEN_LIST}
    private static List<Kitchen> kitchens;
    private static Context mContext;

    public NormalKitchenAdapter(Context context, List<Kitchen> kitchens)
    {
        this.mContext = context;
        this.kitchens = kitchens;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        protected TextView followedTitle;

        public HeaderViewHolder(View view) {
            super (view);
            followedTitle = (TextView) view.findViewById(R.id.followed_title);
        }
    }

    public static class KitchenViewHolder extends RecyclerView.ViewHolder{
        protected TextView kitchenNameView;
        protected TextView categoryView;
        protected TextView dishesNumberView;
        protected ImageView kitchenPic;

        public KitchenViewHolder(View view) {
            super (view);
            kitchenNameView = (TextView) view.findViewById( R.id.followed_kitchenName );
            categoryView = (TextView) view.findViewById( R.id.followed_kitchenCategory );
//            dishesNumberView = (TextView) view.findViewById( R.id.dishNumber );
            kitchenPic = (ImageView) view.findViewById(R.id.followed_imageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = getPosition();
                    String objectId = kitchens.get(i-1).getObjectId();
                    Log.d("kitchen object id ", objectId);
                    Intent kitchenDetail = new Intent(mContext, PlacingOrderActivity.class);
                    kitchenDetail.putExtra("object_id_extra_key", objectId);
                    mContext.startActivity(kitchenDetail);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
//        Log.d("kitchen count is:", String.valueOf(kitchens.size()));
        return kitchens.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof KitchenViewHolder) {
            Kitchen item = kitchens.get(i-1);
            ((KitchenViewHolder)holder).kitchenNameView.setText(item.getKitchenName());
            ((KitchenViewHolder)holder).categoryView.setText(item.getCategory());
//            final String objectId = item.getObjectId();
            /**display dish number */
//            if (item.getDish() != null) {
//                ((KitchenViewHolder)holder).dishesNumberView.setText(String.valueOf(item.getDish().getDishItem().size()));
//            } else {
//                ((KitchenViewHolder)holder).dishesNumberView.setText("No dish published yet");
//            }
            //        kitchenViewHolder.dishesNumberView.setText("2");
            Picasso.with(mContext).load(item.getKitchenPic()).resize(512, 512).into(((KitchenViewHolder)holder).kitchenPic);

//            ((KitchenViewHolder)holder).kitchenPic.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent kitchenDetail = new Intent(mContext, PlacingOrderActivity.class);
//                    kitchenDetail.putExtra("object_id_extra_key", objectId);
//                    mContext.startActivity(kitchenDetail);
//                }
//            });
            //        DownloadImageTask downloadImageTask = new DownloadImageTask(kitchenViewHolder.kitchenPic);
            //        downloadImageTask.execute(item.getKitchenPic());
        }
        else if (holder instanceof HeaderViewHolder) {
            Log.d("item count is:", String.valueOf(getItemCount()));
//            if (getItemCount() == 1) {
//                ((HeaderViewHolder)holder).followedTitle.setText("You haven't followed any kitchen yet");
//            }
//            else {
//                ((HeaderViewHolder)holder).followedTitle.setText("Your following list");
//            }
            ((HeaderViewHolder)holder).followedTitle.setText("Your following list");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == ITEM_TYPE.ITEM_TYPE_HEADER.ordinal()) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_followed_header, viewGroup, false);
            return new HeaderViewHolder(itemView);
        }
        else {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_followed_list, viewGroup, false);
            return new KitchenViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? ITEM_TYPE.ITEM_TYPE_HEADER.ordinal() : ITEM_TYPE.ITEM_TYPE_KITCHEN_LIST.ordinal();
    }

    public void setData(List<Kitchen> list) {
        kitchens = list;
        notifyDataSetChanged();
    }


//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//        @Override
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap bm = null;
//            try {
//                InputStream is = new java.net.URL(urldisplay).openStream();
////                BitmapFactory.Options options = new BitmapFactory.Options();
////                options.inSampleSize = 8;
////                bm = BitmapFactory.decodeStream(is, null, options);
//                bm = BitmapFactory.decodeStream(is);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return bm;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }

//    @Override
//    public void destroyItem(View collection, int position, Object o) {
//        View view = (View) o;
//        ((AutoScrollViewPager) collection).removeView(view);
//        view = null;
//    }
}

