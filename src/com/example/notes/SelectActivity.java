package com.example.notes;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.example.db.NotesDB;
import com.example.utils.LinkMovementMethodExt;
import com.example.utils.SoundPlayer;
import com.example.utils.MessageSpan;


public class SelectActivity extends BaseActivity implements OnClickListener {

	private TextView mShowTextView;
	private TextView mTitleTextView;
	private VideoView mVideoView;

	private Bundle bundle;
	private TextView mCollectionTextView;
	private TextView mShareTextView;
	private TextView mEditTextView;
	private TextView mMoreTextView;
	private Cursor cursor;
	private String current_time;

	private View iRecoderView;
	SoundPlayer mSoundPlayer;
	private String soundPath;
	private String currentTime;
	private String sharePhotoPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = getIntent().getBundleExtra("bundle");
		ShareSDK.initSDK(this);
		initViews();
	}

	private void initViews() {
		iRecoderView = findViewById(R.id.i_recorder);
		mCollectionTextView = (TextView) findViewById(R.id.tv_collection);
		mShareTextView = (TextView) findViewById(R.id.tv_share);
		mEditTextView = (TextView) findViewById(R.id.tv_edit);
		mMoreTextView = (TextView) findViewById(R.id.tv_more);

		mShowTextView = (TextView) findViewById(R.id.tv_show);
		mTitleTextView = (TextView) findViewById(R.id.tv_title);
		mVideoView = (VideoView) findViewById(R.id.s_video);
		mCollectionTextView.setOnClickListener(this);
		mShareTextView.setOnClickListener(this);
		mEditTextView.setOnClickListener(this);
		mMoreTextView.setOnClickListener(this);
		iRecoderView.setOnClickListener(this);
		mSoundPlayer = new SoundPlayer();

		query();
		current_time=cursor.getString(cursor.getColumnIndex(NotesDB.CURRENT_TIME));
		soundPath = cursor.getString(cursor.getColumnIndex(NotesDB.RECORDER));
		if (cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)) == null
				|| cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO))
						.equals("null")) {
			mVideoView.setVisibility(View.GONE);
		} else {
			mVideoView.setVisibility(View.VISIBLE);
			mVideoView.setVideoURI(Uri.parse(bundle.getString(NotesDB.VIDEO)));
			mVideoView.start();
		}
		currentTime=cursor.getString(cursor.getColumnIndex(NotesDB.CURRENT_TIME));
		mTitleTextView.setText(cursor.getString(cursor
				.getColumnIndex(NotesDB.TITLE)));
		sharePhotoPath=cursor.getString(cursor.getColumnIndex(NotesDB.PATH));
		getImgText(mShowTextView,
				cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
		
	}

	@Override
	public void onClick(View v) {
		Intent intent2;
		switch (v.getId()) {
		case R.id.tv_share:
			showShare();
			break;

		case R.id.tv_edit:
			intent2 = new Intent(SelectActivity.this, AddContent.class);
			intent2.putExtra("bundle", bundle);
			startActivity(intent2);
			finish();
			break;

		case R.id.tv_more:
			break;
			
		case R.id.i_recorder:
			mSoundPlayer.startPlaying((new File(soundPath)).getAbsolutePath());
			break;

		case R.id.tv_collection:
			System.err.println("0000000000");
			break;
			
		default:
			break;
		}
	}

	public void delete() {
		dbWriter.delete(NotesDB.TABLE_NAME,
				"_id=" + bundle.getInt(NotesDB.ID, 0), null);
		Toast.makeText(this, bundle.getInt(NotesDB.ID, 0) + "",
				Toast.LENGTH_SHORT).show();
	}

	public void query() {
		cursor = dbWriter.query(NotesDB.TABLE_NAME, null, "_id=?",
				new String[] { bundle.getInt("_id") + "" }, null, null, null);
		cursor.moveToFirst();
	}

	@Override
	public View setContentView() {
		return View.inflate(this, R.layout.activity_select, null);
	}

	public void getImgText(TextView text, String imagePath) {
//		text.setText(Html.fromHtml(imagePath, new MImageGetter(text,SelectActivity.this), new MTagHandler()));
		SpannableString ss = new SpannableString(imagePath);
		Pattern p = Pattern
				.compile("^/(sdcard/)|(storage/sdcard1/DCIM/Camera/)?.+?\\.\\w{3}");
		Matcher m = p.matcher(imagePath);
		while (m.find()) {
			Bitmap bm = BitmapFactory.decodeFile(m.group());
			Drawable drawable = new BitmapDrawable(bm);
			drawable.setBounds(0, 0,500,
					(int) (500*drawable.getIntrinsicHeight()/drawable.getIntrinsicWidth()));
			ImageSpan span = new ImageSpan(drawable,
					ImageSpan.ALIGN_BASELINE);
//			ImageSpan span = new ImageSpan(this, bm);
			ss.setSpan(span, m.start(), m.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		text.setText(ss);
		text.setMovementMethod(LinkMovementMethodExt.getInstance(handler, ImageSpan.class));  
	}
	
	Handler handler = new Handler() {
 		public void handleMessage(Message msg) {
 			int what = msg.what;
 			if (what == 200) {
 				MessageSpan ms = (MessageSpan) msg.obj;
 				Object[] spans = (Object[])ms.getObj();
 				for (Object span : spans) {
 					if (span instanceof ImageSpan) {
// 	            		bundle.putString("picUrl",((ImageSpan) span).getSource()); 
 						if (!mSharedPreferences.getString(currentTime+LinkMovementMethodExt.off, "").equals("")) {
 							mSoundPlayer.startPlaying((new File(mSharedPreferences.getString(currentTime+LinkMovementMethodExt.off, "")))
 									.getAbsolutePath());
 						}
 					}
 				}
 			}
 		};
 	};
	
	/**1.图片，不能为红色，蓝色，正装。
	 * 2.表格，
	 * 3.模板，文字简洁
	 * 4.自我评价，3条
	 */
	
	
	private void showShare() {
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(mTitleTextView.getText()+"");
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		 oks.setTitleUrl("http://sharesdk.cn");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText(mShowTextView.getText()+"");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 oks.setImagePath(sharePhotoPath);//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
//		 oks.setUrl("/sdcard/20160327191546.jpg");
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		 oks.setComment("我是测试评论文本");
		 // site是分享此内容的网站名称，仅在QQ空间使用
//		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		 oks.setSiteUrl("http://sharesdk.cn");
		 
		// 启动分享GUI
		 oks.show(this);
	}

}
