package com.example.henu.step;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class WeatherActivity extends AppCompatActivity {

    private TextView tv_high_low_temp, tv_high_temp, tv_low_temp,
            tv_state_weather, tv_week_cur_day, tv_quality_air, tv_update_time,
            tv_temp_cur_day, tv_second_temp, tv_third_temp, tv_cur_day,
            tv_second_day, tv_third_day, tv_cur_date, tv_second_date,
            tv_third_date, tv_temp_dynamic, tv_cityName;
    private ImageView iv_current_am, iv_second_am, iv_third_am, iv_current_pm,
            iv_second_pm, iv_third_pm, weather_add, back, weather_location;
    private LinearLayout position_ll;
    private String cityNameOrCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
   //     this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
        getData();
    }


    private void init() {
        tv_state_weather = (TextView) findViewById(R.id.state_weather);
        tv_week_cur_day = (TextView) findViewById(R.id.week_current_day);
        tv_quality_air = (TextView) findViewById(R.id.quality_air);
        tv_temp_cur_day = (TextView) findViewById(R.id.temperature_current_day);
        tv_second_temp = (TextView) findViewById(R.id.temperature_second);
        tv_third_temp = (TextView) findViewById(R.id.temperature_third_day);
        tv_cur_day = (TextView) findViewById(R.id.current_day);
        tv_second_day = (TextView) findViewById(R.id.second_day);
        tv_third_day = (TextView) findViewById(R.id.third_day);
        tv_cur_date = (TextView) findViewById(R.id.current_date);
        tv_second_date = (TextView) findViewById(R.id.second_date);
        tv_third_date = (TextView) findViewById(R.id.third_date);
        iv_current_am = (ImageView) findViewById(R.id.image_current_am);
        iv_current_pm = (ImageView) findViewById(R.id.image_current_pm);
        iv_second_am = (ImageView) findViewById(R.id.image_second_am);
        iv_second_pm = (ImageView) findViewById(R.id.image_second_pm);
        iv_third_am = (ImageView) findViewById(R.id.image_third_am);
        iv_third_pm = (ImageView) findViewById(R.id.image_third_pm);
        tv_week_cur_day = (TextView) findViewById(R.id.week_current_day);
        tv_temp_dynamic = (TextView) findViewById(R.id.temp_current_day);
        position_ll = (LinearLayout) findViewById(R.id.position_ll);
        tv_cityName = (TextView) findViewById(R.id.city_name);

    }


    private void getData() {
        cityNameOrCoord = "开封市";
        final WeatherEntity weather = new WeatherEntity();
        HttpUtils httpUtils = new HttpUtils(60000);
        String ak = "Em8bLaBbUcQ1CnKjcYxbSO0UH8hwugCu";
        String url = "";
        try {
            url = "http://api.map.baidu.com/telematics/v3/weather?"
                    + "location=" + URLEncoder.encode(cityNameOrCoord,"UTF-8") + "&output=json&ak="
                    + ak+"&mcode="+"55:42:F5:75:32:28:A8:5D:30:04:98:C7:4D:12:65:B1:7E:71:F9:9F;com.example.weatherdemo";
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Toast.makeText(WeatherActivity.this, "请查看是否已经开启网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                try {
                    JSONObject json = new JSONObject(arg0.result);
                    if (json.getString("status").equals("success")) {
                        JSONArray array = json.getJSONArray("results");
                        JSONObject jsonObject = array.getJSONObject(0);
                        weather.current_city = jsonObject
                                .getString("currentCity");
                        weather.pm2_5 = jsonObject.getString("pm25");
                        JSONArray weatherJson = jsonObject
                                .getJSONArray("weather_data");
                        weather.weatherDateList = new ArrayList<WeatherDataEntity>();
                        for (int i = 0; i < 3; i++) {
                            JSONObject js = weatherJson.getJSONObject(i);
                            WeatherDataEntity weatherData = new WeatherDataEntity();
                            String date = js.getString("date");
                            if (i == 0) {
                                String week = date.substring(0, 2);
                                weatherData.cur_week = week;
                                weatherData.cur_date = date.substring(3, 9);
                                int startIndex = date.indexOf("：") + 1;
                                int endIndex = date.indexOf(")");
                                weather.current_temp = date.substring(
                                        startIndex, endIndex);
                            } else {
                                weatherData.cur_week = date;
                            }
                            weatherData.current_image_day = js
                                    .getString("dayPictureUrl");
                            weatherData.current_image_night = js
                                    .getString("nightPictureUrl");
                            String str = js.getString("weather");
                            weather.state_weather = str;
                            if (str.contains("转")) {
                                String[] splitStr = str.split("转");
                                weatherData.state_weather_day = splitStr[0];
                                weatherData.state_weather_night = splitStr[1];
                            } else {
                                weatherData.state_weather_day = str;
                                weatherData.state_weather_night = str;
                            }
                            weatherData.wind = js.getString("wind");
                            weatherData.high_low_temp = js
                                    .getString("temperature");
                            weather.weatherDateList.add(weatherData);
                        }
                        setData(weather);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData(WeatherEntity weather) {
        tv_cityName.setText(weather.current_city);
        tv_state_weather.setText(weather.state_weather);
        if (weather.pm2_5.equals("")) {
            tv_quality_air.setText("未知");
        } else {
            tv_quality_air.setText(weather.pm2_5);
        }
        tv_temp_dynamic.setText(weather.current_temp);
        ArrayList<WeatherDataEntity> weatherDateList = weather.weatherDateList;
        WeatherDataEntity weatherData1 = weatherDateList.get(0);
        tv_cur_day.setText("今天");
        String day1 = weatherData1.cur_date;
        String dayNumStr = day1.substring(day1.indexOf("月") + 1,
                day1.indexOf("日"));
        String monthNumStr = day1.substring(0, 2);
        int dayNum = Integer.valueOf(dayNumStr);
        int monthNum = Integer.valueOf(monthNumStr);
        tv_cur_date.setText(day1);
        tv_week_cur_day.setText(weatherData1.cur_week);
        tv_temp_cur_day.setText(weatherData1.high_low_temp);
        //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        ImageLoader.getInstance().displayImage(weatherData1.current_image_day,
                iv_current_am);
        ImageLoader.getInstance().displayImage(
                weatherData1.current_image_night, iv_current_pm);

        WeatherDataEntity weatherData2 = weatherDateList.get(1);
        tv_second_day.setText(weatherData2.cur_week);
        dayNum = dayNum + 1;
        tv_second_date.setText(monthNum + "月" + dayNum + "日");
        tv_second_temp.setText(weatherData2.high_low_temp);
        ImageLoader.getInstance().displayImage(weatherData2.current_image_day,
                iv_second_am);
        ImageLoader.getInstance().displayImage(
                weatherData2.current_image_night, iv_second_pm);
        WeatherDataEntity weatherData3 = weatherDateList.get(2);
        tv_third_day.setText(weatherData3.cur_week);
        dayNum = dayNum + 1;
        tv_third_date.setText(monthNum + "月" + dayNum + "日");
        tv_third_temp.setText(weatherData3.high_low_temp);
        ImageLoader.getInstance().displayImage(weatherData3.current_image_day,
                iv_third_am);
        ImageLoader.getInstance().displayImage(
                weatherData3.current_image_night, iv_third_pm);
    }
}
