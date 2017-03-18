package com.example.henu.step.Util;

/**
 * Created by Administrator on 2017/3/13.
 */

public  class DataHelper {
	public DataHelper() {
	}
	public static String changedata(long time){
		String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date((time+1483200000L) * 1000));
		return date;
	}

	public static String chagetime(int temp){
		//处理运动时间
		if (temp > 59) {
			int fen = temp / 60;
			int miao = temp % 60;
			return "累计用时： " + fen + "分 " + miao + "秒";
		} else {
			return "累计用时： 0分 " + temp + "秒";
		}
	}
}
