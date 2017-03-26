package com.example.henu.step.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/13.
 */

public class DateHelper {

	public static String changeDateToString(long time){
		return new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date((time+1483200000L) * 1000));
	}

	public  static Long changeStringToDate(String str){
		try {
			return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(str).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	static String chagetime(int temp){
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
