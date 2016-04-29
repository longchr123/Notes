package com.example.notes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.db.NotesDB;
import com.example.utils.SoundPlayer;
import com.example.utils.SoundRecorder;

public class AddContent extends BaseActivity implements OnClickListener {

	/** include引入的布局文件 */
	private View iRecoderView;
	private LinearLayout mRecoderLayout;

	/** 内容 */
	private EditText mContentEditText;

	/** 标题 */
	private EditText mTitleEditText;
	private ImageView mImageView;
	private VideoView mVideoView;

	/** 存储文件 */
	private File phoneFile;
	private File videoFile;

	/** 底部菜单 */
	private ImageView mImgButton;
	private ImageView mVideoButton;
	private ImageView mRecoderButton;
	private ImageView mImgOutButton;

	/** 用于判断是否从SelectActivity页面进入 */
	private Bundle bundle;

	/** 录音相关变量 */
	SoundPlayer mSoundPlayer;
	SoundRecorder mSoundRecorder;

	/** 录音动画 */
	private LinearLayout mRecordLinearLayout;
	private ImageView mRecordImageView;
	private AnimationDrawable frameAnim;

	private ImageView mSaveImageView;
	private Cursor cursor;
	private String oldSoundPath=null;
	private String currentTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bundle = getIntent().getBundleExtra("bundle");
		currentTime=getTime2();
		initViews();
	}

	private void initViews() {
		iRecoderView = findViewById(R.id.i_recorder);
		mSaveImageView = (ImageView) findViewById(R.id.save);
		mImgButton = (ImageView) findViewById(R.id.img);
		mRecoderButton = (ImageView) findViewById(R.id.recorder);
		mVideoButton = (ImageView) findViewById(R.id.video);
		mContentEditText = (EditText) findViewById(R.id.et_text);
		mTitleEditText = (EditText) findViewById(R.id.et_title);
		mImageView = (ImageView) findViewById(R.id.c_img);
		mVideoView = (VideoView) findViewById(R.id.c_video);
		mImgOutButton = (ImageView) findViewById(R.id.img_out);
		mSaveImageView.setOnClickListener(this);
		mImgButton.setOnClickListener(this);
		mVideoButton.setOnClickListener(this);
		mVideoView.setOnClickListener(this);
		mRecoderButton.setOnClickListener(this);
		mImgOutButton.setOnClickListener(this);

		mRecordLinearLayout = (LinearLayout) findViewById(R.id.ll_record);
		mSoundPlayer = new SoundPlayer();
		mSoundRecorder = new SoundRecorder();
		mRecordImageView = (ImageView) findViewById(R.id.iv_record);
		iRecoderView.setOnClickListener(this);
		if (bundle != null) {
			judgeButton();
		}else {
//			mEditor.putString("currentTime", currentTime);
		}

		mContentEditText.setOnClickListener(textListener);
	}

	/**
	 * 如果是从SeclectActivity页面进入则获取数据填入页面中。
	 */
	private void judgeButton() {
		mImageView.setVisibility(View.GONE);
		mVideoView.setVisibility(View.GONE);
//		currentTime=mSharedPreferences.getString("currentTime", "");
		// 查询数据库中此行数据
		query();

		// 视频判断
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
		mTitleEditText.setText(cursor.getString(cursor
				.getColumnIndex(NotesDB.TITLE)));
		// 从手机拍照传过来的图片
		getImgText2(mContentEditText,
				cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			// 从SeclectActivity页面进入时，执行数据库的Updata操作
			if (bundle != null) {
				change();
				Intent intent = new Intent(this, SelectActivity.class);
				intent.putExtra("bundle", bundle);
				startActivity(intent);
			} else {
				addDB();
			}
			finish();
			break;

		case R.id.img:

			// 图片
			Intent imgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			phoneFile = new File("/sdcard/" + getTime2().toString().trim()
					+ ".jpg");
			startActivityForResult(imgIntent, 1);
			break;

		case R.id.video:
			// 视频录制
			Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			videoFile = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile() + "/" + getTime2().toString() + ".mp4");
			videoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(videoFile));
			startActivityForResult(videoIntent, 2);
			break;

			// 点击视频控制进行播放
		case R.id.c_video:
			mVideoView.setVideoURI(Uri.fromFile(videoFile));
			mVideoView.start();
			break;

			// 录音操作
		case R.id.recorder:
			if (!mSoundRecorder.isRecording) {
				// 开始录音状态
				mSoundRecorder.startRecording();
				frameAnim = (AnimationDrawable) mRecordImageView
						.getBackground();
				frameAnim.start();
				mRecordLinearLayout.setVisibility(View.VISIBLE);
				mSoundRecorder.isRecording = true;
			} else {
				// 停止录音状态
				mSoundRecorder.stopRecording();
				mRecordLinearLayout.setVisibility(View.GONE);
				mSoundRecorder.isRecording = false;
				iRecoderView.setVisibility(View.VISIBLE);
				((TextView) iRecoderView.findViewById(R.id.txtSize))
						.setText((new File(mSoundRecorder.path)).length()
								/ 1024 + " kb");
				((TextView) iRecoderView.findViewById(R.id.txtText))
						.setText((new File(mSoundRecorder.path)).getName());
				mContentEditText.post(new Runnable() {

					@Override
					public void run() {
						Bitmap bitmap = LayoutToBitmap(iRecoderView);
						String pathString = "/sdcard/"
								+ getTime2().toString().trim() + ".jpg";
						AddBitmapToEditText(bitmap, pathString);
						oldSoundPath=mSoundRecorder.path;
						iRecoderView.setVisibility(View.GONE);
						FileOutputStream b = null;
						try {
							b = new FileOutputStream(new File(pathString));
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 锟斤拷锟斤拷锟斤拷写锟斤拷锟侥硷拷
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} finally {
							try {
								b.flush();
								b.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
			break;

			// 点击录制好的文件进行播放所录的音
		case R.id.i_recorder:
			mSoundPlayer.startPlaying((new File(mSoundRecorder.path))
					.getAbsolutePath());
			break;

			// 从图库获取图片
		case R.id.img_out:
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 3);
			break;
		default:
			break;
		}
	}

	// 查询数据库中此行数据
	public void query() {
		cursor = dbWriter.query(NotesDB.TABLE_NAME, null, "_id=?",
				new String[] { bundle.getInt("_id") + "" }, null, null, null);
		cursor.moveToFirst();
	}

	/** 往数据库中增加数据 */
	public void addDB() {
		ContentValues cv = new ContentValues();
		cv.put(NotesDB.TITLE, mTitleEditText.getText().toString().trim());
		cv.put(NotesDB.CONTENT, mContentEditText.getText().toString().trim());
		cv.put(NotesDB.TIME, getTime());
		cv.put(NotesDB.PATH, phoneFile + "");
		cv.put(NotesDB.VIDEO, videoFile + "");
		cv.put(NotesDB.CURRENT_TIME, currentTime);
		cv.put(NotesDB.RECORDER, mSoundRecorder.path);
		dbWriter.insert(NotesDB.TABLE_NAME, null, cv);
	}

	/** 往数据库中更新数据 */
	public void change() {
		ContentValues cv = new ContentValues();
		cv.put(NotesDB.TITLE, mTitleEditText.getText().toString().trim());
		cv.put(NotesDB.CONTENT, mContentEditText.getText().toString().trim());
		cv.put(NotesDB.TIME, getTime());
		if (mSoundRecorder.path != null) {
			cv.put(NotesDB.RECORDER, mSoundRecorder.path);
		}
		if (phoneFile != null) {
			cv.put(NotesDB.PATH, phoneFile + "");
		}
		if (videoFile != null) {
			cv.put(NotesDB.VIDEO, videoFile + "");
		}
		dbWriter.update(NotesDB.TABLE_NAME, cv,
				"_id=" + bundle.getInt(NotesDB.ID, 0), null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 锟斤拷锟絪d锟角凤拷锟斤拷锟�
				Log.i("TestFile",
						"SD card is not avaiable/writeable right now.");
				return;
			}
			// 返回的是相机图片
			if (requestCode == 1) {
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");// 锟斤拷取锟斤拷锟斤拷锟斤拷氐锟斤拷锟斤拷荩锟斤拷锟阶拷锟轿狟itmap图片锟斤拷式
				AddBitmapToEditText(bitmap, phoneFile.toString().trim());
				FileOutputStream b = null;
				try {
					b = new FileOutputStream(phoneFile);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 锟斤拷锟斤拷锟斤拷写锟斤拷锟侥硷拷
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						b.flush();
						b.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// 返回的是视频
			} else if (requestCode == 2) {
				mVideoView.setVisibility(View.VISIBLE);
				mVideoView.setVideoURI(Uri.fromFile(videoFile));
				mVideoView.start();
				// 返回的是图库中选中的图片
			} else if (requestCode == 3) {
				choicePhoto(data);
			}

		}

	}

	private void AddBitmapToEditText(Bitmap bitmap, String pathString) {

		Drawable drawable = new BitmapDrawable(bitmap);
		drawable.setBounds(0, 0,500,
				(int) (500*drawable.getIntrinsicHeight()/drawable.getIntrinsicWidth()));
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		// 要让图片替代指定的文字就要用ImageSpan
		// ImageSpan imageSpan = new ImageSpan(AddContent.this, bitmap);
		// 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
		SpannableString spannableString = new SpannableString(pathString);
		// 用ImageSpan对象替换face
		spannableString.setSpan(imageSpan, 0, pathString.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 将随机获得的图像追加到EditText控件的最后
		addPic(mContentEditText, imageSpan, spannableString);
	}

	public static void insertImageToEditText(Context context,
			EditText editText, Bitmap bitmap, SpannableStringBuilder ss) {
		Drawable drawable = new BitmapDrawable(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 10,
				drawable.getIntrinsicHeight() * 10);
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		ss.setSpan(imageSpan, 0, ss.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		Editable editable = editText.getText();
		int start = editText.getSelectionStart();
		editable.insert(start, ss);
		editText.setText(editable);
		editText.setSelection(start + ss.length());
	}

	/**
	 *调用系统图库，选择一张图片并显示在ImageView上
	 * 
	 * @param data
	 */
	private void choicePhoto(Intent data) {
		Uri selectedImageUri = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(selectedImageUri,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		phoneFile = new File(cursor.getString(columnIndex));
		getImgText(mContentEditText, phoneFile + "");
		cursor.close();
	}

	/** 设置布局文件 */
	@Override
	public View setContentView() {
		return View.inflate(this, R.layout.add_content, null);
	}

	public void getImgText(EditText text, String imagePath) {
		SpannableString ss = new SpannableString(imagePath);
		Pattern p = Pattern.compile("/storage/.+?\\.\\w{3}");
		Matcher m = p.matcher(imagePath);
		while (m.find()) {
			Bitmap bm = BitmapFactory.decodeFile(m.group());
			Drawable drawable = new BitmapDrawable(bm);
			drawable.setBounds(0, 0,500,
					(int) (500*drawable.getIntrinsicHeight()/drawable.getIntrinsicWidth()));
			ImageSpan imageSpan = new ImageSpan(drawable,
					ImageSpan.ALIGN_BASELINE);
			ss.setSpan(imageSpan, m.start(), m.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			addPic(text, imageSpan, ss);
		}
	}

	public void getImgText2(EditText text, String imagePath) {
		SpannableString ss = new SpannableString(imagePath);
		Pattern p = Pattern
				.compile("^/(sdcard/)|(storage/sdcard1/DCIM/Camera/)?.+?\\.\\w{3}");
		Matcher m = p.matcher(imagePath);
		ImageSpan span = null;
		while (m.find()) {
			Bitmap bm = BitmapFactory.decodeFile(m.group());
			Drawable drawable = new BitmapDrawable(bm);
			drawable.setBounds(0, 0,500,
					(int) (500*drawable.getIntrinsicHeight()/drawable.getIntrinsicWidth()));
			span = new ImageSpan(drawable,
					ImageSpan.ALIGN_BASELINE);
//			span = new ImageSpan(this, bm);
			ss.setSpan(span, m.start(), m.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		}
		addPic(text, span, ss);
	}

	public void addPic(EditText txtEdit, ImageSpan imageSpan, SpannableString ss) {
		Editable editable = txtEdit.getEditableText();
		int selectedIndex = txtEdit.getSelectionStart();
		ss.getSpans(0, ss.length(), ImageSpan.class);
		if (selectedIndex < 0)
			editable.append(ss);
		else
			editable.insert(selectedIndex, ss);
		if (oldSoundPath!=mSoundRecorder.path) {
			mEditor.putString(currentTime+selectedIndex, mSoundRecorder.path).commit();
		}

	}

	// 图片的点击事件
	private OnClickListener textListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 关闭软键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mContentEditText.getWindowToken(), 0);
			Spanned s = mContentEditText.getText();
			ImageSpan[] imageSpans = s.getSpans(0, s.length(), ImageSpan.class);
			int selectionStart = mContentEditText.getSelectionStart();
			for (ImageSpan span : imageSpans) {
				int start = s.getSpanStart(span);
				int end = s.getSpanEnd(span);
				if (selectionStart >= start && selectionStart < end)// 锟揭碉拷图片
				{
					Bitmap bitmap = ((BitmapDrawable) span.getDrawable())
							.getBitmap();
					if (!mSharedPreferences.getString(currentTime+selectionStart, "").equals("")) {
						mSoundPlayer.startPlaying((new File(mSharedPreferences.getString(currentTime+selectionStart, "")))
								.getAbsolutePath());
					}
					return;
				}
			}
			imm.showSoftInput(mContentEditText, 0);
		}
	};

	private Bitmap LayoutToBitmap(View mImgLayout) {
		mImgLayout.buildDrawingCache(true);
		Bitmap bitmap = mImgLayout.getDrawingCache(true).copy(Config.RGB_565,
				false);
		mImgLayout.destroyDrawingCache();
		return bitmap;
	}

}
