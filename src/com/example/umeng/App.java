package com.example.umeng;

import android.app.Application;
import android.content.Context;

import com.umeng.socialize.PlatformConfig;

/**
 * Created by ntop on 15/7/8.
 */
public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

	}

	// 各个平台的配置，建议放在全局Application或�?�程序入�?
	{
		PlatformConfig.setWeixin("wx967daebe835fbeac",
				"5bb696d9ccd75a38c8a0bfe0675559b3");
		// ΢�� appid appsecret
		PlatformConfig.setSinaWeibo("3921700954",
				"04b48b094faeb16683c32669824ebdad");
		// ����΢�� appkey appsecret
		PlatformConfig.setQQZone("100424468",
				"c7394704798a158208a74ab60104f0ba");
		// QQ��Qzone appid appkey
		PlatformConfig.setAlipay("2015111700822536");
		// ֧���� appid
		PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
		// ���� appkey
		PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi",
				"MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
		// Twitter appid appkey
		PlatformConfig.setPinterest("1439206");
		// Pinterest appid
		PlatformConfig.setLaiwang("laiwangd497e70d4",
				"d497e70d4c3e4efeab1381476bac4c5e");
		// ���� appid appkey
	}
}
