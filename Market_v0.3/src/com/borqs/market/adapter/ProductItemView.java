package com.borqs.market.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.borqs.market.R;
import com.borqs.market.db.DownLoadHelper;
import com.borqs.market.json.Product;
import com.borqs.market.utils.ImageRun;
import com.borqs.market.utils.IntentUtil;

public class ProductItemView {
    public static final String TAG = ProductItemView.class.getSimpleName();
    public View convertView;
    public LinearLayout itemContainer;
    public int childCount;
    
    public int image_portrait_width;
    public int image_portrait_height;
//    public int image_landscape_width;
//    public int image_landscape_height;
//    public int product_item_image_square;
//    public int object_margin_bottom;
    public int height;
    public int sacaled_width;
    public int mColumn;
    private Activity mActivity;
    private Context mContext;
    private LayoutInflater inflater;
//    private DownLoadHelper downLoadHelper;
//    private String[] productIds;
//    private ArrayList<Product> mDatas;
//    private ApiUtil mApiUtil;
//    private boolean isPortrait;
    
    public ProductItemView(Activity activity, int column, int itemMinWidth) {
        super();
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mColumn = column;
        inflater = LayoutInflater.from(mContext);
        
        calculateWidth(itemMinWidth);
//        image_landscape_width = mContext.getResources().getDimensionPixelSize(R.dimen.product_item_image_landscape_width);
//        image_landscape_height = mContext.getResources().getDimensionPixelSize(R.dimen.product_item_image_landscape_height);
//        product_item_image_square = mContext.getResources().getDimensionPixelSize(R.dimen.product_item_image_square);
//        object_margin_bottom = mContext.getResources().getInteger(R.integer.object_margin_bottom);
//        this.downLoadHelper = downLoadHelper;
//        this.isPortrait = isPortrait;
//        this.isPortrait = true;
        
        convertView = inflater.inflate(R.layout.list_item_view, null);
        itemContainer = (LinearLayout)convertView.findViewById(R.id.item_container);
        convertView.setTag(this);
    }
    private void calculateWidth(int itemMinWidth) {
        image_portrait_width = itemMinWidth;
        image_portrait_height = itemMinWidth * 3 / 2;
        height = image_portrait_height;
    }
    public void setmColumn(int mColumn, int itemMinWidth) {
        calculateWidth(itemMinWidth);
        this.mColumn = mColumn;
    }

    public void initUI(int position, ArrayList<Product> prds) {
//        mDatas = prds;
        if(itemContainer.getChildCount() != mColumn) {
            itemContainer.removeAllViews();
        }
        if(prds != null && prds.size() > 0) {
            int prdSize = prds.size();
            for(int i = 0;i < mColumn; i++) {
                View child = null;
                Holder holder = null;
                if(itemContainer.getChildCount() > i) {
                    child = itemContainer.getChildAt(i);
                }
                if(child == null) {
                        child = inflater.inflate(R.layout.product_view, null);
                    holder = new Holder();
                    holder.imageCover = (ImageView) child.findViewById(R.id.img_cover);
                    holder.layout_product_view = child.findViewById(R.id.layout_product_view);
                    holder.textName = (TextView) child.findViewById(R.id.tv_name);
                    
                    child.setTag(holder);
                    itemContainer.addView(child, i, new LinearLayout.LayoutParams(image_portrait_width,height, 0));
                }else {
                    holder = (Holder)child.getTag();
                }
                if(i < prdSize) {
                    final Product product = prds.get(i);
                    child.setVisibility(View.VISIBLE);
                    
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.layout_product_view.getLayoutParams();
                    params.width = image_portrait_width;
                    params.height = image_portrait_height;
                    params.setMargins(0, 0, 0, 0);
                    sacaled_width = image_portrait_width;
                    if(!TextUtils.isEmpty(product.product_id) && product.product_id.startsWith("USP_")) {
                        if(TextUtils.isEmpty(product.author)) {
                            holder.textName.setText(product.name);
                        }else {
                            holder.textName.setText(product.author);
                        }
                    }else {
                        holder.textName.setText(product.name);
                    }
                    holder.imageCover.setTag(product.cover_url);
                    holder.imageCover.setImageResource(R.drawable.picture_loading);
                    final ImageView img = holder.imageCover;
                    if(position > 2) {
                        holder.imageCover.postDelayed(new Runnable() {
                            
                            @Override
                            public void run() {
                                shootImageRunner(product.cover_url, img);
                            }
                        }, 500);
                    }else {
                        shootImageRunner(product.cover_url, img);
                    }
                    holder.imageCover.setOnClickListener(new View.OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                            IntentUtil.startProductDetailActivity(mActivity,
                                    product.product_id,
                                    product.version_code,
                                    product.name,
                                    product.supported_mod);
                        }
                    });
                }else {
                    child.setVisibility(View.INVISIBLE);
                }
            }
        }
        if(itemContainer.getChildCount() > prds.size()) {
            for(int i = prds.size(); i < itemContainer.getChildCount(); i++) {
                itemContainer.getChildAt(i).setVisibility(View.INVISIBLE);
            }
        }
        
    }
    
    static class Holder {
        public View layout_product_view;
        public ImageView imageCover;
        public TextView textName;
    }
    
    private void shootImageRunner(String url, ImageView imageView) {
        if(TextUtils.isEmpty(url)) return;
        if(imageView.getTag() == null) return;
        if(!url.equals(imageView.getTag()))  return;
        ImageRun photo_1 = new ImageRun(null, url, 0);
        photo_1.width = image_portrait_width;
        photo_1.addHostAndPath = true;
        photo_1.noimage = true;
        photo_1.need_scale = true;
        photo_1.setImageView(imageView);
        photo_1.post(null);
    }
}
