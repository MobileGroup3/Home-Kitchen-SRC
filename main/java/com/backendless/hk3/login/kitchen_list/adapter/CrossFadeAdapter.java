package com.backendless.hk3.login.kitchen_list.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.backendless.hk3.login.R;
import com.squareup.picasso.Picasso;

/**
 * Created by clover on 5/24/16.
 */
public class CrossFadeAdapter extends PagerAdapter {
    private int[] image_resources = {
            R.drawable.makalong,
            R.drawable.sushi,
            R.drawable.kitchen,
            R.drawable.smoothie};
    private Context context;
    private LayoutInflater layoutInflater;

    public CrossFadeAdapter (Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return (view == (LinearLayout)o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.custom_crossfade, container, false);
        ImageView imageView = (ImageView) item_view.findViewById(R.id.single_view_pager);

        int resId = image_resources[position];
        Picasso.with(context).load(resId).resize(512,320).into(imageView);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;
//        Resources res = context.getResources();
//        int id = image_resources[position];
//
//        Bitmap picture = BitmapFactory.decodeResource(res, id,options);
//        imageView.setImageBitmap(picture);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


}
