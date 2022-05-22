package com.example.cody_c;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.example.cody_c.adapter.DailyItemAdapter;
import com.example.cody_c.adapter.HourlyItemAdapter;
import com.example.cody_c.data.DailyItem;
import com.example.cody_c.data.HourlyItem;
import com.example.cody_c.pagefragment.fragment_citysearch;
import com.example.cody_c.pagefragment.fragment_clothstyle;
import com.example.cody_c.pagefragment.fragment_main;
import com.example.cody_c.pagefragment.fragment_main_weather;
import com.example.cody_c.util.GpsTracker;
import com.example.cody_c.util.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

public class MainActivity extends AppCompatActivity {
    private Fragment mainFragment, detailWeatherFragment, clothStyleFragment, citySearchFragment;
    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MainActivity latitude", String.valueOf(PreferenceManager.getFloat(this, "LATITUDE")));
        createFragment();

        //첫 화면은 메인화면으로 전시
        FragmentTransaction fT = getSupportFragmentManager().beginTransaction();
        fT.replace(R.id.frameLayout, mainFragment);
        fT.addToBackStack(null);
        fT.commit();

        //하단 BottomNavigationView 선언
        BottomNavigationView bottomView = findViewById(R.id.bottomNavigationView);

        //BottomNavigationView에 eventListener 선언 => 하단 특정 버튼 클릭시 원하는 페이지로 이동.
        bottomView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_home:
                        FragmentTransaction fT1 = getSupportFragmentManager().beginTransaction();
                        fT1.replace(R.id.frameLayout, mainFragment);
                        fT1.addToBackStack(null);
                        fT1.commit();
                        return true;
                    case R.id.item_DetailWeather:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, detailWeatherFragment).commit();
                        return true;
                    case R.id.item_clothstyle:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, clothStyleFragment).commit();
                        return true;
                    case R.id.item_citysearch:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, citySearchFragment).commit();
                        return true;
                }
                return false;
            }
        });


        if(PreferenceManager.getBoolean(this, "MAIN_NOTICE_DIALOG")!=true){
            showMainNoticeDialog();
        }

    }

    private void createFragment() {
        mainFragment = new fragment_main(); //메인 페이지
        detailWeatherFragment = new fragment_main_weather(); //날씨 상세 페이지
        clothStyleFragment = new fragment_clothstyle(); // 옷 스타일 페이지
        citySearchFragment = new fragment_citysearch(); // 도시 추천 페이지
    }

    private void showMainNoticeDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("앱 처음 실행 시 공지")
                .setMessage("설정창에 들어가서 날씨 정보를 받을 위치를 설정하세요!")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNegativeButton("다시 보지 않기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PreferenceManager.setBoolean(getApplicationContext(), "MAIN_NOTICE_DIALOG",true);
                    }
                })
                .show();
    }

    private void initSharedPreference(){
        Log.e("SEULGI SP", ""+PreferenceManager.getFloat(this, "LATITUDE"));
        Log.e("SEULGI SP", ""+PreferenceManager.getFloat(this, "LONGITUDE"));
        if(PreferenceManager.getFloat(this, "LATITUDE")==-1F){
            if(latitude!=0.0 && longitude!=0.0){
                PreferenceManager.setFloat(this,"LATITUDE",(float)latitude);
            }
            else {
                PreferenceManager.setFloat(this,"LATITUDE",37.5172f);
            }
        }
        if(PreferenceManager.getFloat(this, "LONGITUDE")==-1F){
            if(latitude!=0.0 && longitude!=0.0){
                PreferenceManager.setFloat(this,"LONGITUDE",(float)longitude);
            }
            else {
                PreferenceManager.setFloat(this,"LONGITUDE",127.0473f);
            }
        }
        if(PreferenceManager.getString(this, "CITY").equals("")){
            PreferenceManager.setString(getApplicationContext(),"CITY","서울특별시 강남구");
        }

        PreferenceManager.setInt(this, "REGION_NUMBER",1);
    }

}