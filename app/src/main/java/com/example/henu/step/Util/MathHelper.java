package com.example.henu.step.Util;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */

public class MathHelper {
	public MathHelper() {

	}

	public double getLength(List<LatLng> points){
		LatLng point1 = null;
		LatLng point2 = null;
		double stepLength = 0;
		for (int i = 0;i+1<points.size();i++){
			point1 = points.get(i);
			point2 = points.get(i+1);
			stepLength = stepLength + DistanceUtil.getDistance(point1,point2);
			System.out.println("stepLength"+i+"=="+stepLength);
		}
		System.out.println("stepLength="+stepLength);
		return stepLength;
	}


}
