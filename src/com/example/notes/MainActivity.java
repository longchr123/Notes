package com.example.notes;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.MyAdapter;
import com.example.db.NotesDB;

public class MainActivity extends BaseActivity implements OnClickListener {

	private ListView lv;
	private Intent intent;
	private MyAdapter adapter;
	private Cursor cursor;
	/** 底部菜单，用于指删除 */
	private LinearLayout mBottomLayout;
	private Button mDeleteButton;
	private Button mCancelButton;

	private Button mAddButton;
	/** 搜索按钮 */
	private Button mSearchButton;
	private EditText mEditText;

	/** 用于遍历ListView中的每一个item */
	private AdapterView<?> mParent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
	}

	private void initViews() {
		lv = (ListView) findViewById(R.id.list);
		mEditText = (EditText) findViewById(R.id.et_search);
		mDeleteButton = (Button) findViewById(R.id.btn_delete);
		mCancelButton = (Button) findViewById(R.id.btn_cancel);
		mAddButton = (Button) findViewById(R.id.btn_add);
		mSearchButton = (Button) findViewById(R.id.btn_search);
		mBottomLayout = (LinearLayout) findViewById(R.id.ll_bottom);
		mCancelButton.setOnClickListener(this);
		mDeleteButton.setOnClickListener(this);
		mAddButton.setOnClickListener(this);
		mSearchButton.setOnClickListener(this);

		// item的点击事件，将数据以Bundle的形式传入下一个Activity
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				cursor.moveToPosition(position);
				Intent intent = new Intent(MainActivity.this,
						SelectActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(NotesDB.ID,
						cursor.getInt(cursor.getColumnIndex("_id")));
				bundle.putString(NotesDB.TITLE,
						cursor.getString(cursor.getColumnIndex("title")));
				bundle.putString(NotesDB.CONTENT,
						cursor.getString(cursor.getColumnIndex("content")));
				bundle.putString(NotesDB.TIME,
						cursor.getString(cursor.getColumnIndex("time")));
				bundle.putString(NotesDB.PATH,
						cursor.getString(cursor.getColumnIndex("path")));
				bundle.putString(NotesDB.VIDEO,
						cursor.getString(cursor.getColumnIndex("video")));
				intent.putExtra("bundle", bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.fade, R.anim.hold);
			}
		});
		// item的长按事件：显示每一个item的CheckBox
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mParent = parent;
				for (int i = 0; i < cursor.getCount(); i++) {
					View view2 = parent.getChildAt(i);
					CheckBox cb = (CheckBox) view2.findViewById(R.id.cb);
					cb.setVisibility(View.VISIBLE);
					mBottomLayout.setVisibility(View.VISIBLE);
				}
				return true;
			}
		});

	}

	@Override
	public void onClick(View v) {
		intent = new Intent(this, AddContent.class);
		switch (v.getId()) {
		// 点击取消按钮，CheckBox隐藏
		case R.id.btn_cancel:
			for (int i = 0; i < cursor.getCount(); i++) {
				View view2 = mParent.getChildAt(i);
				CheckBox cb = (CheckBox) view2.findViewById(R.id.cb);
				cb.setChecked(false);
				cb.setVisibility(View.GONE);
			}
			mBottomLayout.setVisibility(View.GONE);
			break;

			// 批量删除
		case R.id.btn_delete:
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				View view2 = mParent.getChildAt(i);
				if (((CheckBox) view2.findViewById(R.id.cb)).isChecked()) {
					delete(cursor.getInt(cursor.getColumnIndex("_id")));
				}
			}
			selectDB();
			break;

		case R.id.btn_add:
			startActivity(intent);
			break;
			// 搜索事件
		case R.id.btn_search:
			selectDBForSearch();
			break;
		default:
			break;
		}
	}

	/** 查询数据 */
	public void selectDB() {
		cursor = dbWriter.query(notesDB.TABLE_NAME, null, null, null, null,
				null, null);
		adapter = new MyAdapter(this, cursor);
		lv.setAdapter(adapter);
	}

	/** 根据搜索内容查询数据 */
	public void selectDBForSearch() {
		cursor = dbWriter
				.query(notesDB.TABLE_NAME, null, "content like '%"
						+ mEditText.getText().toString() + "%'", null, null,
						null, null);
		if (cursor != null && cursor.getCount() > 0) {
			adapter = new MyAdapter(this, cursor);
			lv.setAdapter(adapter);
			lv.setVisibility(View.VISIBLE);
		}
	}

	/** 删除数据 */
	public void delete(int id) {
		dbWriter.delete(NotesDB.TABLE_NAME, "_id=" + id, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		selectDB();
	}

	@Override
	public View setContentView() {
		return View.inflate(this, R.layout.activity_main, null);
	}

	private float exitTime = 0;

	/**
	 *连续点击两次才可返回主页面
	 */
	public void restart() {

		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(MainActivity.this, "�ٰ�һ�ν������û���", Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
			if (mParent != null) {
				// 第一次点击返回时隐藏所有item的CheckBox
				for (int i = 0; i < cursor.getCount(); i++) {
					View view2 = mParent.getChildAt(i);
					CheckBox cb = (CheckBox) view2.findViewById(R.id.cb);
					cb.setVisibility(View.GONE);
				}
				mBottomLayout.setVisibility(View.GONE);
			}
		} else {
			MainActivity.this.finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			restart();
		}
		return false;
	}
}
