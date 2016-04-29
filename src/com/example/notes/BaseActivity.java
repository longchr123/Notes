package com.example.notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.example.db.NotesDB;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


public abstract class BaseActivity extends Activity implements
		AMapLocationListener {

	public NotesDB notesDB;
	public SQLiteDatabase dbWriter;
	private RelativeLayout mChildLinearLayout;
	private TextView mLocationTextView;
	public SharedPreferences mSharedPreferences;
	public SharedPreferences.Editor mEditor;

	// ����AMapLocationClient�����
	public AMapLocationClient mLocationClient = null;
	// ����mLocationOption����
	public AMapLocationClientOption mLocationOption = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		notesDB = new NotesDB(this);
		dbWriter = notesDB.getWritableDatabase();
		setContentView(R.layout.activity_base);
		mLocationTextView = (TextView) findViewById(R.id.tv_location);
		mChildLinearLayout = (RelativeLayout) findViewById(R.id.rl);
		// ����Ӳ���
		View child = setContentView();
		mSharedPreferences=getSharedPreferences("recoder", MODE_PRIVATE);
		mEditor=mSharedPreferences.edit();
		if (child != null) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			mChildLinearLayout.addView(child, params);
		}

		// ��ʼ����λ
		mLocationClient = new AMapLocationClient(getApplicationContext());

		// ��ʼ����λ����
		mLocationOption = new AMapLocationClientOption();
		// ���ö�λģʽΪ�߾���ģʽ��Battery_SavingΪ�͹���ģʽ��Device_Sensors�ǽ��豸ģʽ
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// �����Ƿ񷵻ص�ַ��Ϣ��Ĭ�Ϸ��ص�ַ��Ϣ��
		mLocationOption.setNeedAddress(true);
		// �����Ƿ�ֻ��λһ��,Ĭ��Ϊfalse
		mLocationOption.setOnceLocation(false);
		// �����Ƿ�ǿ��ˢ��WIFI��Ĭ��Ϊǿ��ˢ��
		mLocationOption.setWifiActiveScan(true);
		// �����Ƿ�����ģ��λ��,Ĭ��Ϊfalse��������ģ��λ��
		mLocationOption.setMockEnable(false);
		// ���ö�λ���,��λ����,Ĭ��Ϊ5000ms
		mLocationOption.setInterval(5000);
		// ����λ�ͻ��˶������ö�λ����
		mLocationClient.setLocationOption(mLocationOption);
		// ������λ
		mLocationClient.startLocation();
		// ���ö�λ�ص�����
		mLocationClient.setLocationListener(this);
	}

	public void SimpleToast(String s) {
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	public abstract View setContentView();

	public String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		Date currentDate = new Date();
		String str = format.format(currentDate);
		return str;
	}

	public String getTime2() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date currentDate = new Date();
		String str = format.format(currentDate);
		return str;
	}

	// ��λ�ص�
		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation != null) {
				if (amapLocation.getErrorCode() == 0) {
					// ��λ�ɹ��ص���Ϣ�����������Ϣ
					amapLocation.getLocationType();// ��ȡ��ǰ��λ�����Դ�������綨λ����������λ���ͱ�
					amapLocation.getLatitude();// ��ȡγ��
					amapLocation.getLongitude();// ��ȡ����
					amapLocation.getAccuracy();// ��ȡ������Ϣ
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date date = new Date(amapLocation.getTime());
					df.format(date);// ��λʱ��
					amapLocation.getAddress();// ��ַ�����option������isNeedAddressΪfalse����û�д˽�������綨λ����л��е�ַ��Ϣ��GPS��λ�����ص�ַ��Ϣ��
					amapLocation.getCountry();// ������Ϣ
					amapLocation.getProvince();// ʡ��Ϣ
					amapLocation.getCity();// ������Ϣ
					amapLocation.getDistrict();// ������Ϣ
					amapLocation.getStreet();// �ֵ���Ϣ
					mLocationTextView.setText(amapLocation.getProvince()+amapLocation.getCity()+amapLocation.getDistrict());
					amapLocation.getStreetNum();// �ֵ����ƺ���Ϣ
					amapLocation.getCityCode();// ���б���
					amapLocation.getAdCode();// ��������
					amapLocation.getAoiName();// ��ȡ��ǰ��λ���AOI��Ϣ
				} else {
					// ��ʾ������ϢErrCode�Ǵ����룬errInfo�Ǵ�����Ϣ������������
					Log.e("AmapError",
							"location Error, ErrCode:"
									+ amapLocation.getErrorCode() + ", errInfo:"
									+ amapLocation.getErrorInfo());
				}
			}
		}
		
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
		}
	
}
