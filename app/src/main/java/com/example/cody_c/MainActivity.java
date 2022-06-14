package com.example.cody_c;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.cody_c.pagefragment.fragment_clothstyle;
import com.example.cody_c.pagefragment.fragment_cody_mylib;
import com.example.cody_c.pagefragment.fragment_main;
import com.example.cody_c.util.GpsTracker;
import com.example.cody_c.util.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    private Fragment mainFragment, clothStyleFragment, myCodyLibFragment;
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
                    case R.id.item_clothstyle:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, clothStyleFragment).addToBackStack(null).commit();
                        return true;
                    case R.id.item_mycodylib:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, myCodyLibFragment).addToBackStack(null).commit();
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
        clothStyleFragment = new fragment_clothstyle(); // 옷 스타일 페이지
        myCodyLibFragment = new fragment_cody_mylib(); // 도시 추천 페이지
    }

    private void showMainNoticeDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("앱 처음 실행 시 공지")
                .setMessage("온도를 누르면 상세한 날씨를 볼 수 있으며, 코디를 누르면 추천 코디로 이동합니다!")
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

    @Override
    protected void onPause(){
        super.onPause();
        PreferenceManager.setString(this, "flugin", "1");
    }

    protected void onResume() {
        super.onResume();
        PreferenceManager.onResume_pref(this);
    }

}
