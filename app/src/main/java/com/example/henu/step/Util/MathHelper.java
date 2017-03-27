package com.example.henu.step.Util;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */

public class MathHelper {

	//单例模式应用（懒汉模式）
	private final static MathHelper mathhelper = new MathHelper();
	private MathHelper() {
	}

	public static MathHelper getInstance(){
		return mathhelper;
	}

	public double getLength(List<LatLng> points){
		LatLng point1 = null;
		LatLng point2 = null;
		double stepLength = 0;
		for (int i = 0;i+1<points.size();i++){
			point1 = points.get(i);
			point2 = points.get(i+1);
			stepLength = stepLength + DistanceUtil.getDistance(point1,point2);
		}
		return stepLength;
	}


}
