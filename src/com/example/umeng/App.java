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

	// 鍚勪釜骞冲彴鐨勯厤缃紝寤鸿鏀惧湪鍏ㄥ眬Application鎴栬?呯▼搴忓叆鍙?
	{
		PlatformConfig.setWeixin("wx967daebe835fbeac",
				"5bb696d9ccd75a38c8a0bfe0675559b3");
		// 微信 appid appsecret
		PlatformConfig.setSinaWeibo("3921700954",
				"04b48b094faeb16683c32669824ebdad");
		// 新浪微博 appkey appsecret
		PlatformConfig.setQQZone("100424468",
				"c7394704798a158208a74ab60104f0ba");
		// QQ和Qzone appid appkey
		PlatformConfig.setAlipay("2015111700822536");
		// 支付宝 appid
		PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
		// 易信 appkey
		PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi",
				"MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
		// Twitter appid appkey
		PlatformConfig.setPinterest("1439206");
		// Pinterest appid
		PlatformConfig.setLaiwang("laiwangd497e70d4",
				"d497e70d4c3e4efeab1381476bac4c5e");
		// 来往 appid appkey
	}
}
