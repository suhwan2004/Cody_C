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
    private Fragment mainCodyImg, mainCodyImg1;
    final int NUM_PAGES = 2;

    private GpsTracker gpsTracker;

    private View rootView;

    private TextView region;                //??????
    private TextView current_temp;          //????????????
    private TextView current_location;      //?????? ??????
    private TextView current_bodily_temp;   //?????? ??????
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

    private int temp_diff; // ?????????(?????????)
    private int cur_temperature;
    private String dailyLow;
    private String dailyHigh;
    private String temp_f;

    private String temp_extra;
    private String description;
    private ProgressDialog progressDialog;
    private String weather_Id;

    private String temp_diff_str;
    private String rain_1h; //1?????? ?????? ?????? ??????
    private String rain_3h; //3?????? ?????? ????????????
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

        //??? ?????? ?????? ??? bundle ?????????
        if(PreferenceManager.getBoolean(rootView.getContext(), "weatherInfoExist") == false) {
            bundle = new Bundle();
        }

        initView(inflater, container, savedInstanceState);
        displayWeather(rootView.getContext());
        recommendColorMatching();

        //??? ?????? ?????????
        //1. SharedPreference ??? key : "weatherInfoExist"??? ?????? true??? ????????????, ?????? ?????? ????????? ???????????? ??????
        //2. SharedPreference ??? key : "lastRequestTime"??? ?????? ??? request??? ?????? ???????????? ??????
        if(PreferenceManager.getBoolean(rootView.getContext(), "weatherInfoExist") == false) {
            PreferenceManager.setBoolean(rootView.getContext(), "weatherInfoExist", true);
            PreferenceManager.setLong(rootView.getContext(), "lastRequestTime", curTime);
        }
        mainCodyImg = new fragment_main_codyimg();
        mainCodyImg1 = new fragment_main_codyimg1();
        detailWeatherFragment = new fragment_main_weather();
        detailWeatherFragment.setArguments(bundle);

        ViewPager2 viewPager2 = rootView.findViewById(R.id.viewpager);
        viewPager2.setAdapter(new viewPagerAdapter(this)); // ????????? this??? ??????????????? ???????????? ?????? ?????? ???????????????(HomeFragment)??? ????????? ????????????.
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
            else if(position == 1) return mainCodyImg1;
            else return mainCodyImg;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }


    private void initView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        region=(TextView)rootView.findViewById(R.id.main_region_text); //??????
        current_temp = (TextView)rootView.findViewById(R.id.main_temp_now); //?????? ??????
        current_location = (TextView)rootView.findViewById(R.id.main_region_text); //?????? ??????
        current_bodily_temp=(TextView)rootView.findViewById(R.id.main_bodily_temp); //?????? ??????
        current_temp_diff = (TextView)rootView.findViewById(R.id.main_temp_diff); //?????????
        current_weather_ment = (TextView)rootView.findViewById(R.id.main_weather_ment); // ????????? ?????? ??????
        current_temp_diff_ment = (TextView)rootView.findViewById(R.id.main_temp_diff_ment); // ???????????? ?????? ??????

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
        1. ??? ?????? ?????? ????????? request?????? ???????????? or ????????? request ????????? ???????????? ?????? ??????
        2. ????????? request????????? 1????????? ?????? ??????

        ?????? ????????? ?????? ???, request?????? ?????? ?????? ????????? ????????? ????????? ????????? request ?????? ??????.
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

            Toast.makeText(getContext(),"????????? ?????????????????????.",Toast.LENGTH_SHORT);
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
        progressDialog.setMessage("??????????????????..");
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
        String url="http://api.openweathermap.org/data/2.5/weather?appid=bb5a2651f01077f8fcfec1ba21425991&units=metric&id=1835848&lang=kr"; //??????????????? ?????? ????????? ???????????? ??????.
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

                        //??????
                        temperature = main_object.getString("temp");
                        temperature = String.valueOf(Math.round(Double.valueOf(temperature)));
                        temp_extra = temperature;
                        cur_temperature = Integer.parseInt(temperature);

                        //????????? bundle??? ??????.
                        bundle.putString("temperature", temperature);

                        current_temp.setText(temperature + getString(R.string.temperature_unit));

                        //????????????
                        bodily_temperature = main_object.getString("feels_like");
                        bodily_temperature = String.valueOf(Math.round(Double.valueOf(bodily_temperature)));
                        bundle.putString("bodily_temperature", bodily_temperature);
                        current_bodily_temp.setText(bodily_temperature + getString(R.string.temperature_unit));

                        //????????????
                        String temp_max = main_object.getString("temp_max");
                        temp_max = String.valueOf(Math.round(Double.valueOf(temp_max)));

                        //????????????
                        String temp_min = main_object.getString("temp_min");
                        temp_min = String.valueOf(Math.round(Double.valueOf(temp_min)));

                        //?????? ~ ?????? ?????? ??????
                        temp_diff_str = temp_min + " ~ " + temp_max + getString(R.string.temperature_unit);
                        bundle.putString("temp_diff_str", temp_diff_str);
                        //?????? ??????
                        JSONObject weather = weather_object.getJSONObject(0);
                        description = weather.getString("description");

                        //?????? ?????? ??????
                        weather_Id = weather.getString("id");
                        setCurrentWeatherMent();

                        //??????
                        pressure = main_object.getString("pressure");
                        bundle.putString("pressure", pressure);

                        //??????
                        humidity = main_object.getString("humidity");
                        bundle.putString("humidity", humidity);

                        //??????
                        sunrise = sys_object.getString("sunrise");
                        long timestamp = Long.parseLong(sunrise);
                        Date date = new java.util.Date(timestamp * 1000L);
                        SimpleDateFormat sdf = new java.text.SimpleDateFormat("a hh:mm");
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                        sunrise = sdf.format(date);

                        //??????
                        sunset = sys_object.getString("sunset");
                        timestamp = Long.parseLong(sunset);
                        date = new java.util.Date(timestamp * 1000L);
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                        sunset = sdf.format(date);

                        //?????????
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

                        //??????
                        description = weather.getString("description");
                        bundle.putString("description", description);

                        //?????? ?????????
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

            //????????????
            temp_extra = bundle.getString("temperature");
            current_temp.setText(bundle.getString("temperature") + getString(R.string.temperature_unit));

            //????????????
            current_bodily_temp.setText(bundle.getString("bodily_temperature") + getString(R.string.temperature_unit));
        }

    }

    public void find_future_weather(float latitude, float longitude, boolean isRequiredRequest) {
        //?????? ????????? ???????????? ??? ??????.
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
                            //??????
                            String dt = rec.getString("dt");
                            long timestamp = Long.parseLong(dt);
                            Date date = new java.util.Date(timestamp * 1000L);
                            SimpleDateFormat sdf = new java.text.SimpleDateFormat("a h" + "???");
                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                            dt = sdf.format(date);

                            //??????
                            temp_f = rec.getString("temp");
                            temp_f = String.valueOf(Math.round(Double.valueOf(temp_f)));

                            JSONArray weather_object = rec.getJSONArray("weather");
                            JSONObject weather = weather_object.getJSONObject(0);
                            String icon = weather.getString("icon");
                            int resID = getResId("icon_" + icon, R.drawable.class);

                            if (i == 0)
                                hourlyItemList.add(new HourlyItem("??????", resID, temp_f + getString(R.string.temperature_unit)));
                            else
                                hourlyItemList.add(new HourlyItem(dt, resID, temp_f + getString(R.string.temperature_unit)));
                        }

                        for (int i = 1; i < daily_object.length(); i++) {
                            JSONObject rec = daily_object.getJSONObject(i);
                            JSONObject get_temp = rec.getJSONObject("temp");

                            //??????
                            String dt = rec.getString("dt");
                            long timestamp = Long.parseLong(dt);
                            Date date = new java.util.Date(timestamp * 1000L);
                            SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE", Locale.KOREAN);
                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
                            dt = sdf.format(date);

                            //????????????
                            dailyLow = get_temp.getString("min");
                            dailyLow = String.valueOf(Math.round(Double.valueOf(dailyLow)));

                            //????????????
                            dailyHigh = get_temp.getString("max");
                            dailyHigh = String.valueOf(Math.round(Double.valueOf(dailyHigh)));

                            //?????????
                            JSONArray weather_object = rec.getJSONArray("weather");
                            JSONObject weather = weather_object.getJSONObject(0);
                            String icon = weather.getString("icon");
                            int resID = getResId("icon_" + icon, R.drawable.class);
                            dailyItemList.add(new DailyItem(dt, dailyLow + getString(R.string.temperature_unit), dailyHigh + getString(R.string.temperature_unit), resID));
                        }

                        bundle.putString("dailyHigh", dailyHigh);
                        bundle.putString("dailyLow", dailyLow);

                        //?????????(??????)
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
            //?????????(??????)
            dailyHigh = bundle.getString("dailyHigh");
            dailyLow = bundle.getString("dailyLow");
            temp_diff = bundle.getInt("temp_diff");

            current_temp_diff.setText(dailyLow + " ~ " + dailyHigh + getString(R.string.temperature_unit));

            setCurrentTempDiffMent();
            progressDialog.dismiss();

        }

    }

    public void setCurrentWeatherMent(){
        //?????? ????????? ?????? ??????
        cur_temperature = Integer.parseInt(bundle.getString("temperature"));
        String ment = "?????? ???????????? ";
        String [][] arr = {
                {"?????????","??????","?????????","??????"},
                {"??????", "?????? ??????", "?????????", "?????????"},
                {"?????? ?????????", "?????????", "?????????", "?????????"},
                {"?????? ??????", "?????????", "?????????", "?????? ??????", "?????????", "?????????"},
                {"??????", "?????????", "??????", "?????????", "??????", "?????????", "?????????", "?????????"},
                {"??????", "???????????????", "??????", "??????", "?????????", "?????????", "?????????"},
                {"??????", "?????????", "??????", "?????????", "?????????"},
                {"??????", "????????? ??????", "?????????", "????????????"}
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
            ment += arr[level][i]+ (i != arr[level].length-1 ? ", " : " ????????? ?????????!");
            if(arr[level].length/2-1 == i) ment += "\n";
        }
        current_weather_ment.setText(ment);
        return;
    }

    public void setCurrentTempDiffMent(){
        //?????? ???????????? ?????? ?????? ??????
        String ment;

        if(temp_diff>=20) ment = "???????????? ?????? ??????.\n????????? ??????????????? ????????????.";
        else if(temp_diff >= 10) ment = "???????????? ??? ????????????.\n?????? ????????? ???????????? ????????????,";
        else if(temp_diff >= 5) ment = "???????????? ?????? ?????? ????????????.\n????????? ??????????????? ?????? ????????? ??????????????? ????????????.";
        else ment = "???????????? ????????????.\n????????? ?????? ????????? ????????????.";

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

        //?????? ??? ??????.
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