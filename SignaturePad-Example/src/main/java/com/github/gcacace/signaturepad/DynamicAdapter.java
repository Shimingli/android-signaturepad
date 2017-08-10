package com.github.gcacace.signaturepad;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import static com.github.gcacace.signaturepad.ScreenUtil.dip2px;



public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private ArrayList<String> paths;
    private final Context mContext;
    private final ArrayList<ImageView> mCacheImagview;

    public DynamicAdapter(ArrayList<String> path, Context context) {
        mContext = context;
        if (path==null){
            paths=new ArrayList<String>();
        }else {
            this.paths = path;
        }
        mCacheImagview = new ArrayList<>();

    }
    /***
     * 创建的画布的大小和第一个imageView一样大
     * @param arrayList  ImageView数组
     * @return  Bitmap 和成后的Bitmap
     */
    private Bitmap SaveBitmap(ArrayList<ImageView> arrayList) {
        if (arrayList==null|arrayList.size()<=0) return null;
        Bitmap bmOverlay = Bitmap.createBitmap(arrayList.get(0).getWidth(), arrayList.get(0).getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bmOverlay);
        for (ImageView imageView :arrayList){
            imageView.setDrawingCacheEnabled(true);
            canvas.drawBitmap(imageView.getDrawingCache(), 0, 0, null);
            imageView.setDrawingCacheEnabled(false);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
        canvas.restore();// 存储
        return bmOverlay;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout frameLayout=new FrameLayout(parent.getContext());
        ImageView imageView = new ImageView(parent.getContext());
        int i = ScreenUtil.getDisplayWidth() - dip2px(72);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(i / 5,i / 5);
        layoutParams.rightMargin= dip2px(6);
        layoutParams.leftMargin= dip2px(4);
        layoutParams.topMargin= dip2px(7);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        FrameLayout.LayoutParams layoutParams1=new
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.topMargin= dip2px(3);
        ImageView deleteioc=new ImageView(parent.getContext());
        deleteioc.setLayoutParams(new FrameLayout.LayoutParams(dip2px(20),
                dip2px(20), Gravity.RIGHT));

        frameLayout.setLayoutParams(layoutParams1);
        frameLayout.addView(imageView);
        frameLayout.addView(deleteioc);
        ViewHolder vh = new ViewHolder(frameLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.mImageView.setImageBitmap(paths.get(position));
        mCacheImagview.add(holder.mImageView);
        Glide.with(mContext).load(paths.get(position)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public void setNewBitmap(Bitmap signatureBitmap) {
//        paths.add(signatureBitmap);
        notifyDataSetChanged();
    }

    public void setNewPath(String newPath) {
       paths.add(newPath);
        notifyDataSetChanged();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mImageView = (ImageView) ((FrameLayout) view).getChildAt(0);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(View v, int position);
    }
}
