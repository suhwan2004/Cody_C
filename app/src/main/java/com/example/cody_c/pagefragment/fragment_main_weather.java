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

    private TextView dateNow;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton searchButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton delButton;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private ImageView weathericon;

    private TextView region;                //??????
    private TextView current_temp;          //????????????
    private TextView current_location;      //?????? ??????
    private TextView current_bodily_temp;   //?????? ??????
    private TextView current_rain;          //?????????
    private TextView current_desc;          //??????
    private TextView current_temp_min_max; //???????????? ~ ????????????
    private TextView current_pressure;
    private TextView current_humidity;
    private TextView current_sunrise;
    private TextView current_sunset;

    private ArrayList<HourlyItem> hourlyItemList;
    private ArrayList<DailyItem> dailyItemList;
    Bundle bundle;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_weather, container, false);
        bundle = getArguments();
        initView(inflater, container, savedInstanceState);
        displayWeather(rootView.getContext());

        closeProgressDialog();
        return rootView;
    }


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
                /* ???????????? ??? ????????? ?????? */
                displayWeather(rootView.getContext());
                /* ???????????? ?????? */
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

        find_weather();
        find_future_weather();
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

    public void find_weather(){
        //??????
        current_temp.setText(bundle.getString("temperature")+getString(R.string.temperature_unit));

        //????????????
        current_bodily_temp.setText(bundle.getString("bodily_temperature")+getString(R.string.temperature_unit));

        //??????~ ????????????
        current_temp_min_max.setText(bundle.getString("temp_diff_str"));

        //??????
        current_pressure.setText(bundle.getString("pressure")+getString(R.string.pressure_unit));

        //??????
        current_humidity.setText(bundle.getString("humidity")+getString(R.string.percent_unit));

        //??????
        current_sunrise.setText(bundle.getString("sunrise"));

        //??????
        current_sunset.setText(bundle.getString("sunset"));

        //?????????
        current_rain.setText(bundle.getString("curRain"));

        //??????
        current_desc.setText(bundle.getString("description"));

        //?????? ?????????
        weathericon.setImageResource(bundle.getInt("iconResID"));
    }

    public void find_future_weather() {
        //?????? ??????

        hourlyItemList = bundle.getParcelableArrayList("hourlyItemList");
        dailyItemList = bundle.getParcelableArrayList("dailyItemList");

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

}