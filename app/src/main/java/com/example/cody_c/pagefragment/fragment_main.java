package com.example.cody_c.pagefragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.cody_c.util.GpsTracker;
import com.example.cody_c.util.PreferenceManager;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_main extends Fragment {
    private Fragment mainCodyImg, mainCodySet;
    final int NUM_PAGES = 2;

    private GpsTracker gpsTracker;

    private View rootView;

    private TextView region;                //도시
    private TextView current_temp;          //현재기온
    private TextView current_location;      //현재 위치
    private TextView current_bodily_temp;   //현재 위치
    private String temperature;
    private String bodily_temperature;
    private TextView current_temp_diff;
    private TextView current_weather_ment;
    private TextView current_temp_diff_ment;
    private ImageView color1_up;
    private ImageView color1_down;
    private ImageView color2_up;
    private ImageView color2_down;
    private ImageView color3_up;
    private ImageView color3_down;
    private ImageView color4_up;
    private ImageView color4_down;
    private ImageView color5_up;
    private ImageView color5_down;

    private int temp_diff; // 일교차(정수형)
    private int cur_temperature;
    private String dailyLow;
    private String dailyHigh;
    private String temp_f;

    private String temp_extra;
    private String description;
    private ProgressDialog progressDialog;
    private String weather_Id;

    private String temp_diff_str;
    private String rain_1h; //1시간 후의 강수 예상
    private String rain_3h; //3시간 후의 강수예상
    private String pressure;
    private String humidity;
    private String sunrise;
    private String sunset;
    private String curRain;
    private Long curTime;
    private int iconResID;

    private ArrayList<HourlyItem> hourlyItemList = new ArrayList<>();
    private ArrayList<DailyItem> dailyItemList = new ArrayList<>();

    Bundle bundle;
    private Fragment detailWeatherFragment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_main newInstance(String param1, String param2) {
        fragment_main fragment = new fragment_main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        curTime = System.currentTimeMillis();

        //앱 처음 구동 시 bundle 초기화
        if(PreferenceManager.getBoolean(rootView.getContext(), "weatherInfoExist") == false) {
            bundle = new Bundle();
        }

        initView(inflater, container, savedInstanceState);
        displayWeather(rootView.getContext());
        recommendColorMatching();

        //앱 처음 구동시
        //1. SharedPreference 내 key : "weatherInfoExist"의 값을 true로 둠으로써, 앱을 이미 구동한 상황임을 저장
        //2. SharedPreference 내 key : "lastRequestTime"의 값을 첫 request를 받은 시간으로 설정
        if(PreferenceManager.getBoolean(rootView.getContext(), "weatherInfoExist") == false) {
            PreferenceManager.setBoolean(rootView.getContext(), "weatherInfoExist", true);
            PreferenceManager.setLong(rootView.getContext(), "lastRequestTime", curTime);
        }
        mainCodyImg = new fragment_main_codyimg();
        mainCodySet = new fragment_main_codyset();
        detailWeatherFragment = new fragment_main_weather();
        detailWeatherFragment.setArguments(bundle);

        ViewPager2 viewPager2 = rootView.findViewById(R.id.viewpager);
        viewPager2.setAdapter(new viewPagerAdapter(this)); // 여기서 this로 뷰페이저가 포함되어 있는 현재 프래그먼트(HomeFragment)를 인수로 넣어준다.
        viewPager2.setCurrentItem(0);

        current_temp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout,detailWeatherFragment).addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    private class viewPagerAdapter extends FragmentStateAdapter {
        public viewPagerAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 0) return mainCodyImg;
            else if(position == 1) return mainCodySet;
            else return mainCodyImg;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }


    private void initView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        region=(TextView)rootView.findViewById(R.id.main_region_text); //지역
        current_temp = (TextView)rootView.findViewById(R.id.main_temp_now); //현재 온도
        current_location = (TextView)rootView.findViewById(R.id.main_region_text); //현재 지역
        current_bodily_temp=(TextView)rootView.findViewById(R.id.main_bodily_temp); //체감 온도
        current_temp_diff = (TextView)rootView.findViewById(R.id.main_temp_diff); //일교차
        current_weather_ment = (TextView)rootView.findViewById(R.id.main_weather_ment); // 날씨에 따른 멘트
        current_temp_diff_ment = (TextView)rootView.findViewById(R.id.main_temp_diff_ment); // 일교차에 따른 멘트

        color1_up = (ImageView)rootView.findViewById(R.id.color1_up);
        color1_down = (ImageView)rootView.findViewById(R.id.color1_down);
        color2_up = (ImageView)rootView.findViewById(R.id.color2_up);
        color2_down = (ImageView)rootView.findViewById(R.id.color2_down);
        color3_up = (ImageView)rootView.findViewById(R.id.color3_up);
        color3_down = (ImageView)rootView.findViewById(R.id.color3_down);
        color4_up = (ImageView)rootView.findViewById(R.id.color4_up);
        color4_down = (ImageView)rootView.findViewById(R.id.color4_down);
        color5_up = (ImageView)rootView.findViewById(R.id.color5_up);
        color5_down = (ImageView)rootView.findViewById(R.id.color5_down);

    }

    public void displayWeather(Context context) {
        openProgressDialog();
        float lat = PreferenceManager.getFloat(context,"LATITUDE");
        float lon = PreferenceManager.getFloat(context,"LONGITUDE");
        String address;
        String storedAddress = PreferenceManager.getString(context,"CITY");
        if(storedAddress == null) address = getCurrentAddress(lat, lon);
        else address = storedAddress;

        Long lastRequestTime = PreferenceManager.getLong(rootView.getContext(), "lastRequestTime");

        /*
        1. 한 번도 날씨 정보를 request하지 않았거나 or 마지막 request 시간이 존재하지 않는 경우
        2. 마지막 request로부터 1시간이 지난 경우

        해당 조건에 걸릴 시, request새로 받고 해당 정보로 랜더링 그리고 마지막 request 시간 갱신.
         */

        if(PreferenceManager.getBoolean(rootView.getContext(), "weatherInfoExist") == false ||
        lastRequestTime== -1L ||
        curTime - lastRequestTime >= 3600000){
            find_weather(lat,lon, true);
            find_future_weather(lat,lon, true);
            PreferenceManager.setLong(rootView.getContext(), "lastRequestTime", curTime);
        }else{
            find_weather(lat,lon,false);
            find_future_weather(lat,lon,false);
        }

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

    public void find_weather(float latitude, float longitude, boolean isRequiredRequest){
        String url="http://api.openweathermap.org/data/2.5/weather?appid=bb5a2651f01077f8fcfec1ba21425991&units=metric&id=1835848&lang=kr"; //실질적으로 날씨 정보를 받아오는 부분.
        url += "&lat="+String.valueOf(latitude)+"&lon="+String.valueOf(longitude);

        if(isRequiredRequest) {
            Log.e("SEULGI WEATHER API URL", url);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject main_object = response.getJSONObject("main");
                        JSONArray weather_object = response.getJSONArray("weather");
                        JSONObject sys_object = response.getJSONObject("sys");

                        //기온
                        temperature = main_object.getString("temp");
                        temperature = String.valueOf(Math.round(Double.valueOf(temperature)));
                        temp_extra = temperature;
                        cur_temperature = Integer.parseInt(temperature);

                        //기온을 bundle에 저장.
                        bundle.putString("temperature", temperature);

                        current_temp.setText(temperature + getString(R.string.temperature_unit));

                        //체감온도
                        bodily_temperature = main_object.getString("feels_like");
                        bodily_temperature = String.valueOf(Math.round(Double.valueOf(bodily_temperature)));
                        bundle.putString("bodily_temperature", bodily_temperature);
                        current_bodily_temp.setText(bodily_temperature + getString(R.string.temperature_unit));

                        //최고온도
                        String temp_max = main_object.getString("temp_max");
                        temp_max = String.valueOf(Math.round(Double.valueOf(temp_max)));

                        //최저온도
                        String temp_min = main_object.getString("temp_min");
                        temp_min = String.valueOf(Math.round(Double.valueOf(temp_min)));

                        //최저 ~ 최고 온도 문장
                        temp_diff_str = temp_min + " ~ " + temp_max + getString(R.string.temperature_unit);
                        bundle.putString("temp_diff_str", temp_diff_str);
                        //현재 날씨
                        JSONObject weather = weather_object.getJSONObject(0);
                        description = weather.getString("description");

                        //현재 날씨 코드
                        weather_Id = weather.getString("id");
                        setCurrentWeatherMent();

                        //기압
                        pressure = main_object.getString("pressure");
                        bundle.putString("pressure", pressure);

                        //습도
                        humidity = main_object.getString("humidity");
                        bundle.putString("humidity", humidity);

                        //일출
                        sunrise = sys_object.getString("sunrise");
                        long timestamp = Long.parseLong(sunrise);
                        Date date = new java.util.Date(timestamp * 1000L);
                        SimpleDateFormat sdf = new java.text.SimpleDateFormat("a hh:mm");
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                        sunrise = sdf.format(date);

                        //일출
                        sunset = sys_object.getString("sunset");
                        timestamp = Long.parseLong(sunset);
                        date = new java.util.Date(timestamp * 1000L);
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                        sunset = sdf.format(date);

                        //강우량
                        if (response.has("rain")) {
                            JSONObject rain_object = response.getJSONObject("rain");
                            if (rain_object.has("1h")) {
                                rain_1h = rain_object.getString("1h");
                                rain_1h = String.valueOf(Math.round(Double.valueOf(rain_1h) * 10));
                                curRain = rain_1h + getString(R.string.precipitation_unit);

                            } else if (rain_object.has("3h")) {
                                rain_3h = rain_object.getString("3h");
                                rain_3h = String.valueOf(Math.round(Double.valueOf(rain_3h) * 10));
                                curRain = rain_3h + getString(R.string.precipitation_unit);
                            } else {
                                curRain = "0" + getString(R.string.precipitation_unit);
                            }
                        } else {
                            curRain = "0" + getString(R.string.precipitation_unit);
                        }
                        bundle.putString("curRain", curRain);

                        //날씨
                        description = weather.getString("description");
                        bundle.putString("description", description);

                        //날씨 아이콘
                        String icon = weather.getString("icon");
                        iconResID = getResId("icon_" + icon, R.drawable.class);
                        bundle.putInt("iconResID", iconResID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("SEULGI API RESPONSE", error.toString());
                }
            }
            );

            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            queue.add(jor);
        }
        else{

            //현재온도
            temp_extra = bundle.getString("temperature");
            current_temp.setText(bundle.getString("temperature") + getString(R.string.temperature_unit));

            //체감온도
            current_bodily_temp.setText(bundle.getString("bodily_temperature") + getString(R.string.temperature_unit));
        }

    }

    public void find_future_weather(float latitude, float longitude, boolean isRequiredRequest) {
        //차후 날씨를 보여주는 것 같음.
        String url="http://api.openweathermap.org/data/2.5/onecall?appid=bb5a2651f01077f8fcfec1ba21425991&units=metric&id=1835848&lang=kr";
        //http://api.openweathermap.org/data/2.5/onecall?appid=bb5a2651f01077f8fcfec1ba21425991&units=metric&id=1835848&lang=kr&lat=35&lon=127
        url += "&lat="+String.valueOf(latitude)+"&lon="+String.valueOf(longitude);

        if(isRequiredRequest) {
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray daily_object = response.getJSONArray("daily");
                        JSONArray hourly_object = response.getJSONArray("hourly");

                        for (int i = 1; i < hourly_object.length() && i < 36; i += 2) {
                            JSONObject rec = hourly_object.getJSONObject(i);
                            //시간
                            String dt = rec.getString("dt");
                            long timestamp = Long.parseLong(dt);
                            Date date = new java.util.Date(timestamp * 1000L);
                            SimpleDateFormat sdf = new java.text.SimpleDateFormat("a h" + "시");
                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                            dt = sdf.format(date);

                            //온도
                            temp_f = rec.getString("temp");
                            temp_f = String.valueOf(Math.round(Double.valueOf(temp_f)));

                            JSONArray weather_object = rec.getJSONArray("weather");
                            JSONObject weather = weather_object.getJSONObject(0);
                            String icon = weather.getString("icon");
                            int resID = getResId("icon_" + icon, R.drawable.class);

                            if (i == 0)
                                hourlyItemList.add(new HourlyItem("지금", resID, temp_f + getString(R.string.temperature_unit)));
                            else
                                hourlyItemList.add(new HourlyItem(dt, resID, temp_f + getString(R.string.temperature_unit)));
                        }

                        for (int i = 1; i < daily_object.length(); i++) {
                            JSONObject rec = daily_object.getJSONObject(i);
                            JSONObject get_temp = rec.getJSONObject("temp");

                            //요일
                            String dt = rec.getString("dt");
                            long timestamp = Long.parseLong(dt);
                            Date date = new java.util.Date(timestamp * 1000L);
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
                            int resID = getResId("icon_" + icon, R.drawable.class);
                            dailyItemList.add(new DailyItem(dt, dailyLow + getString(R.string.temperature_unit), dailyHigh + getString(R.string.temperature_unit), resID));
                        }

                        bundle.putString("dailyHigh", dailyHigh);
                        bundle.putString("dailyLow", dailyLow);

                        //일교차(정수)
                        temp_diff = Integer.parseInt(dailyHigh) - Integer.parseInt(dailyLow);
                        current_temp_diff.setText(dailyLow + " ~ " + dailyHigh + getString(R.string.temperature_unit));
                        bundle.putInt("temp_diff", temp_diff);

                        bundle.putParcelableArrayList("dailyItemList", (ArrayList<? extends Parcelable>) dailyItemList);
                        bundle.putParcelableArrayList("hourlyItemList", (ArrayList<? extends Parcelable>) hourlyItemList);
                        setCurrentTempDiffMent();
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("SEULGI API RESPONSE", error.toString());
                }
            }
            );

            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            queue.add(jor);
        }else{
            //일교차(정수)
            dailyHigh = bundle.getString("dailyHigh");
            dailyLow = bundle.getString("dailyLow");
            temp_diff = bundle.getInt("temp_diff");

            current_temp_diff.setText(dailyLow + " ~ " + dailyHigh + getString(R.string.temperature_unit));

            setCurrentTempDiffMent();
            progressDialog.dismiss();

        }

    }

    public void setCurrentWeatherMent(){
        //현재 상황에 따라 추천
        cur_temperature = Integer.parseInt(bundle.getString("temperature"));
        String ment = "오늘 날씨에는 ";
        String [][] arr = {
                {"민소매","반팔","반바지","치마"},
                {"반팔", "얇은 셔츠", "반바지", "면바지"},
                {"얇은 가디건", "긴팔티", "면바지", "청바지"},
                {"얇은 니트", "가디건", "맨투맨", "얇은 자켓", "면바지", "청바지"},
                {"자켓", "가디건", "야상", "맨투맨", "니트", "스타킹", "청바지", "면바지"},
                {"자켓", "트렌치코트", "야상", "니트", "스타킹", "청바지", "면바지"},
                {"코트", "히트텍", "니트", "청바지", "레깅스"},
                {"패딩", "두꺼운 코트", "목도리", "기모제품"}
        };

        int level;
        if(cur_temperature>= 28) level = 0;
        else if(cur_temperature >= 23) level = 1;
        else if(cur_temperature >= 20) level = 2;
        else if(cur_temperature >= 17) level = 3;
        else if(cur_temperature >= 12) level = 4;
        else if(cur_temperature >= 9) level = 5;
        else if(cur_temperature >= 5) level = 6;
        else level = 7;

        for(int i = 0; i < arr[level].length; i++){
            ment += arr[level][i]+ (i != arr[level].length-1 ? ", " : " 코디가 좋아요!");
            if(arr[level].length/2-1 == i) ment += "\n";
        }
        current_weather_ment.setText(ment);
        return;
    }

    public void setCurrentTempDiffMent(){
        //현재 일교차에 따른 추천 멘트
        String ment;

        if(temp_diff>=20) ment = "일교차가 매우 커요.\n외출을 삼가시는게 좋겠어요.";
        else if(temp_diff >= 10) ment = "일교차가 큰 편입니다.\n얇은 겉옷을 챙기는게 좋겠네요,";
        else if(temp_diff >= 5) ment = "일교차가 조금 있는 편이에요.\n온도에 민감하시면 얇은 겉옷을 챙기시는게 좋겠네요.";
        else ment = "일교차가 완만해요.\n편안한 외출 되시길 바랍니다.";

        current_temp_diff_ment.setText(ment);


    }

    public void recommendColorMatching(){
        int colorWhite = R.color.colorWhite;
        int colorBlack = R.color.colorBlack;
        int colorSky = R.color.colorSky;
        int colorRealBlue = R.color.colorRealBlue;
        int colorBeige = R.color.colorBeige;
        int colorCream = R.color.colorCream;
        int colorRed = R.color.colorRed;
        int colorPink = R.color.colorPink;
        int colorNavy = R.color.colorNavy;
        int colorKhaki = R.color.colorKhaki;
        int colorGray = R.color.colorGray;
        int colorBlue = R.color.colorBlue;
        int colorGreen = R.color.colorGreen;
        int colorYellow = R.color.colorYellow;

        ImageView [][] imgViewArr = {{color1_up,color1_down},{color2_up,color2_down},{color3_up,color3_down},{color4_up,color4_down},{color5_up,color5_down}};

        //a.setBackgroundColor(ContextCompat.getColor(rootView.getContext(),R.color.colorGray));

        //추천 색 조합.
        int [][] colorMatching = {{colorWhite, colorSky}, {colorWhite,colorRealBlue},{colorWhite,colorBeige},{colorWhite,colorBlack}, {colorCream, colorSky}, {colorCream, colorBeige}, {colorCream, colorRed}, {colorCream, colorBlack}
        ,{colorPink, colorSky}, {colorNavy, colorRed},{colorNavy, colorBeige}, {colorNavy, colorKhaki}, {colorBlack, colorWhite}, {colorGray, colorBlack}, {colorBlue, colorRealBlue}, {colorGreen, colorSky}, {colorYellow, colorRealBlue}
                ,{colorYellow, colorBlack}};


        int recommendCount = 0;
        HashSet<Integer> storeIdx = new HashSet<Integer>();

        while (recommendCount < 5) {
                int randomIdx = (int) (Math.random() * colorMatching.length);
                if(storeIdx.contains(randomIdx)) continue;
                else{
                    storeIdx.add(randomIdx);
                    imgViewArr[recommendCount][0].setBackgroundColor(ContextCompat.getColor(rootView.getContext(), colorMatching[randomIdx][0]));
                    imgViewArr[recommendCount][1].setBackgroundColor(ContextCompat.getColor(rootView.getContext(), colorMatching[randomIdx][1]));
                    recommendCount++;
                }
        }
        return;
    }

}