package com.example.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.notes.R;

public class MyAdapter extends BaseAdapter {

	private Context context;
	private Cursor cursor;
	private RelativeLayout layout;

	public MyAdapter(Context context, Cursor cursor) {
		this.context = context;
		this.cursor = cursor;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		return cursor.getPosition();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		layout = (RelativeLayout) LayoutInflater.from(context).inflate(
				R.layout.cell, null);
		TextView tv_title = (TextView) layout.findViewById(R.id.list_title);
		TextView tv_content = (TextView) layout.findViewById(R.id.list_content);
		TextView tv_time = (TextView) layout.findViewById(R.id.list_time);
		ImageView iv_img = (ImageView) layout.findViewById(R.id.list_img);
		ImageView iv_video = (ImageView) layout.findViewById(R.id.list_video);
		ImageView video = (ImageView) layout.findViewById(R.id.list_video);
		
		//设置数据
		cursor.moveToPosition(position);
		tv_title.setText(cursor.getString(cursor.getColumnIndex("title")));
		tv_content.setText(cursor.getString(cursor.getColumnIndex("content")));
		tv_time.setText(cursor.getString(cursor.getColumnIndex("time")));

		String url = cursor.getString(cursor.getColumnIndex("path"));
		iv_img.setImageBitmap(getImageThumbnail(url, 200, 200));
//		iv_img.setImageBitmap(BitmapFactory.decodeFile(url));

		String videoUrl = cursor.getString(cursor.getColumnIndex("video"));
		video.setImageBitmap(getVideoThumbnail(videoUrl, 200, 200,
				MediaStore.Images.Thumbnails.MICRO_KIND));
		return layout;
	}

	/**
	 * 获取压缩后的图片
	 * @return
	 */
	public Bitmap getImageThumbnail(String uri, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(uri, options);
		options.inJustDecodeBounds = false;
		int beWidth = options.outWidth / width;
		int beHeight = options.outHeight / height;
		// 加一个条件，防止图片过小而再次变小
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(uri, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/** 截取视频中的第一张图片保存*/
	public Bitmap getVideoThumbnail(String uri, int width, int height, int kind) {
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(uri, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

}
