package com.example.cody_c.pagefragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cody_c.R;
import com.example.cody_c.adapter.DailyItemAdapter;
import com.example.cody_c.adapter.HourlyItemAdapter;
import com.example.cody_c.data.DailyItem;
import com.example.cody_c.data.HourlyItem;
import com.example.cody_c.util.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragment_main_weather extends Fragment {
    private View rootView;
    //private DrawerLayout drawerLayout;
    //private ImageButton menuButton;
    //private NavigationView navigationView;
    private TextView dateNow;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton searchButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton delButton;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private ImageView weathericon;

    private TextView region;                //도시
    private TextView current_temp;          //현재기온
    private TextView current_location;      //현재 위치
    private TextView current_bodily_temp;   //현재 위치
    private TextView current_rain;          //강우량
    private TextView current_desc;          //설명
    private TextView current_temp_min_max; //최저기온 ~ 최고기온
    private TextView current_pressure;
    private TextView current_humidity;
    private TextView current_sunrise;
    private TextView current_sunset;
    private String temperature;
    private String bodily_temperature;

    private String Daily_image;
    private ImageButton search_button;
    private String dailyLow;
    private String dailyHigh;
    private String temp_f;

    private String rain_1h;
    private String rain_3h;
    private String address_text;
    private String level1;
    private String level2;
    private ArrayList<HourlyItem> hourlyItemList = new ArrayList<>();
    private ArrayList<DailyItem> dailyItemList = new ArrayList<>();
    private String temp_extra;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_weather, container, false);
        initView(inflater, container, savedInstanceState);
        displayWeather(rootView.getContext());
        closeProgressDialog();
        return rootView;
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        if(PreferenceManager.getBoolean(getContext(),"IS_ADDRESS_CHANGED")==true){
            PreferenceManager.setBoolean(getContext(),"IS_ADDRESS_CHANGED",false);
            displayWeather(getContext());
        }
    }
*/
    private void initView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        region=(TextView)rootView.findViewById(R.id.region_text);
        current_temp = (TextView)rootView.findViewById(R.id.temp_now);
        current_location = (TextView)rootView.findViewById(R.id.region_text);
        current_bodily_temp=(TextView)rootView.findViewById(R.id.bodily_temp);
        current_rain=(TextView)rootView.findViewById(R.id.precipitation_text);
        current_desc=(TextView)rootView.findViewById(R.id.weather_description);
        weathericon = (ImageView)rootView.findViewById(R.id.image_weather);
        current_temp_min_max =(TextView)rootView.findViewById(R.id.temp_min_max_text);
        current_pressure =(TextView)rootView.findViewById(R.id.pressure);
        current_humidity =(TextView)rootView.findViewById(R.id.humidity);
        current_sunrise =(TextView)rootView.findViewById(R.id.sunrise);
        current_sunset =(TextView)rootView.findViewById(R.id.sunset);

        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_layout);

        /* pull to refresh */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /* 새로고침 시 수행될 코드 */
                displayWeather(rootView.getContext());
                /* 새로고침 완료 */
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void displayWeather(Context context) {
        openProgressDialog();
        float lat = PreferenceManager.getFloat(context,"LATITUDE");
        float lon = PreferenceManager.getFloat(context,"LONGITUDE");
        String address;
        String storedAddress = PreferenceManager.getString(context,"CITY");

        if(storedAddress == null) address = getCurrentAddress(lat, lon);
        else address = storedAddress;

        find_weather(lat,lon);
        find_future_weather(lat,lon);
        closeProgressDialog();

        if(address!=null){
            region.setText(PreferenceManager.getString(getContext(),"CITY"));
        }
        else {
            PreferenceManager.setFloat(getContext(),"LATITUDE",35F);
            PreferenceManager.setFloat(getContext(),"LONGITUDE",(float)127F);
            displayWeather(rootView.getContext());

            Toast.makeText(getContext(),"주소가 잘못되었습니다.",Toast.LENGTH_SHORT);
        }
    }

    public void find_weather(float latitude, float longitude){
        String url="http://api.openweathermap.org/data/2.5/weather?appid=bb5a2651f01077f8fcfec1ba21425991&units=metric&id=1835848&lang=kr"; //실질적으로 날씨 정보를 받아오는 부분.
        url += "&lat="+String.valueOf(latitude)+"&lon="+String.valueOf(longitude);
        Log.e("SEULGI WEATHER API URL", url);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray weather_object = response.getJSONArray("weather");
                    JSONObject sys_object = response.getJSONObject("sys");

                    //기온
                    temperature = main_object.getString("temp");
                    temperature = String.valueOf(Math.round(Double.valueOf(temperature)));
                    temp_extra = temperature;
                    current_temp.setText(temperature+getString(R.string.temperature_unit));

                    //체감온도
                    bodily_temperature = main_object.getString("feels_like");
                    bodily_temperature = String.valueOf(Math.round(Double.valueOf(bodily_temperature)));
                    current_bodily_temp.setText(bodily_temperature+getString(R.string.temperature_unit));

                    //최고온도
                    String temp_max = main_object.getString("temp_max");
                    temp_max = String.valueOf(Math.round(Double.valueOf(temp_max)));

                    //최저온도
                    String temp_min = main_object.getString("temp_min");
                    temp_min = String.valueOf(Math.round(Double.valueOf(temp_min)));

                    //최저~ 최고온도
                    current_temp_min_max.setText(temp_min + " ~ " + temp_max + getString(R.string.temperature_unit));

                    //기압
                    String pressure = main_object.getString("pressure");
                    current_pressure.setText(pressure+getString(R.string.pressure_unit));

                    //습도
                    String humidity = main_object.getString("humidity");
                    current_humidity.setText(humidity+getString(R.string.percent_unit));

                    //일출
                    String sunrise = sys_object.getString("sunrise");
                    long timestamp = Long.parseLong(sunrise);
                    Date date = new java.util.Date(timestamp*1000L);
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("a hh:mm");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                    sunrise = sdf.format(date);
                    current_sunrise.setText(sunrise);

                    //일몰
                    String sunset = sys_object.getString("sunset");
                    timestamp = Long.parseLong(sunset);
                    date = new java.util.Date(timestamp*1000L);
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                    sunset = sdf.format(date);
                    current_sunset.setText(sunset);

                    //강우량
                    if(response.has("rain")){
                        JSONObject rain_object = response.getJSONObject("rain");
                        if(rain_object.has("1h")){
                            rain_1h = rain_object.getString("1h");
                            rain_1h = String.valueOf(Math.round(Double.valueOf(rain_1h)*10));
                            current_rain.setText(rain_1h+getString(R.string.precipitation_unit));

                        }
                        else if(rain_object.has("3h")){
                            rain_3h = rain_object.getString("3h");
                            rain_3h = String.valueOf(Math.round(Double.valueOf(rain_3h)*10));
                            current_rain.setText(rain_3h+getString(R.string.precipitation_unit));
                        }
                        else {
                            current_rain.setText("0"+getString(R.string.precipitation_unit));
                        }
                    }
                    else {
                        current_rain.setText("0"+getString(R.string.precipitation_unit));
                    }

                    JSONObject weather= weather_object.getJSONObject(0);
                    String description = weather.getString("description");
                    current_desc.setText(description);

                    //날씨 아이콘
                    String icon = weather.getString("icon");
                    int resID = getResId("icon_"+icon, R.drawable.class);
                    weathericon.setImageResource(resID);

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SEULGI API RESPONSE",error.toString());
            }
        }
        );

        RequestQueue queue =  Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(jor);

    }

    public void find_future_weather(float latitude, float longitude) {
        //차후 날씨를 보여주는 것 같음.
        String url="http://api.openweathermap.org/data/2.5/onecall?appid=bb5a2651f01077f8fcfec1ba21425991&units=metric&id=1835848&lang=kr";
        //http://api.openweathermap.org/data/2.5/onecall?appid=bb5a2651f01077f8fcfec1ba21425991&units=metric&id=1835848&lang=kr&lat=35&lon=127
        url += "&lat="+String.valueOf(latitude)+"&lon="+String.valueOf(longitude);

        if(!hourlyItemList.isEmpty()) hourlyItemList.clear();
        hourlyItemList = new ArrayList<>();

        if(!dailyItemList.isEmpty()) dailyItemList.clear();
        dailyItemList = new ArrayList<>();

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray hourly_object = response.getJSONArray("hourly");
                    JSONArray daily_object = response.getJSONArray("daily");

                    for(int i=0;i<hourly_object.length() && i<36; i+=2){ //2시간 간격 | 18번만 나오게
                        JSONObject rec= hourly_object.getJSONObject(i);

                        //시간
                        String dt = rec.getString("dt");
                        long timestamp = Long.parseLong(dt);
                        Date date = new java.util.Date(timestamp*1000L);
                        SimpleDateFormat sdf = new java.text.SimpleDateFormat("a h" + "시");
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                        dt = sdf.format(date);

                        //온도
                        temp_f = rec.getString("temp");
                        temp_f = String.valueOf(Math.round(Double.valueOf(temp_f)));

                        JSONArray weather_object = rec.getJSONArray("weather");
                        JSONObject weather = weather_object.getJSONObject(0);
                        String icon = weather.getString("icon");
                        int resID = getResId("icon_"+icon, R.drawable.class);

                        if(i==0){
                            hourlyItemList.add(new HourlyItem("지금",resID,temp_f+getString(R.string.temperature_unit)));
                        }
                        else {
                            hourlyItemList.add(new HourlyItem(dt,resID,temp_f+getString(R.string.temperature_unit)));
                        }
                    }

                    for(int i=1; i<daily_object.length(); i++){
                        JSONObject rec = daily_object.getJSONObject(i);
                        JSONObject get_temp = rec.getJSONObject("temp");

                        //요일
                        String dt = rec.getString("dt");
                        long timestamp = Long.parseLong(dt);
                        Date date = new java.util.Date(timestamp*1000L);
                        SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE", Locale.KOREAN);
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                        dt = sdf.format(date);

                        //최저기온
                        dailyLow = get_temp.getString("min");
                        dailyLow = String.valueOf(Math.round(Double.valueOf(dailyLow)));

                        //최고기온
                        dailyHigh = get_temp.getString("max");
                        dailyHigh = String.valueOf(Math.round(Double.valueOf(dailyHigh)));

                        //아이콘
                        JSONArray weather_object = rec.getJSONArray("weather");
                        JSONObject weather = weather_object.getJSONObject(0);
                        String icon = weather.getString("icon");
                        int resID = getResId("icon_"+icon, R.drawable.class);
                        ;
                        dailyItemList.add(new DailyItem(dt,dailyLow+getString(R.string.temperature_unit),dailyHigh+getString(R.string.temperature_unit),resID));
                    }

                    /* HOURLY RECYCLERVIEW */
                    recyclerView = (RecyclerView) rootView.findViewById(R.id.hourly_recycler);
                    LinearLayoutManager layoutManager_h= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
                    recyclerView.setLayoutManager(layoutManager_h);

                    HourlyItemAdapter adapter_h;
                    adapter_h = new HourlyItemAdapter(getActivity(),hourlyItemList);
                    recyclerView.setAdapter(adapter_h);
                    adapter_h.notifyDataSetChanged();

                    /*DAILY RECYCLERVIEW */
                    recyclerView2 = (RecyclerView) rootView.findViewById(R.id.daily_recycler);
                    LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
                    recyclerView2.setLayoutManager(layoutManager);

                    DailyItemAdapter adapter;
                    adapter = new DailyItemAdapter(getActivity(),dailyItemList);
                    recyclerView2.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SEULGI API RESPONSE",error.toString());
            }
        }
        );

        RequestQueue queue =  Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(jor);

    }


    public String getCurrentAddress( double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(rootView.getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
        } catch (IOException ioException) {
            return null;
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
        if (addresses == null || addresses.size() == 0) {
            return null;
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0);
    }

    private void openProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("로딩중입니다..");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        progressDialog.dismiss();

    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}