package com.example.cody_c;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.cody_c.pagefragment.fragment_citysearch;
import com.example.cody_c.pagefragment.fragment_clothstyle;
import com.example.cody_c.pagefragment.fragment_main;
import com.example.cody_c.pagefragment.fragment_main_codyimg;
import com.example.cody_c.pagefragment.fragment_main_codyset;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private Fragment mainFragment, clothStyleFragment, citySearchFragment, mainCodyImgFragment, mainCodySetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        BottomNavigationView bottomView = findViewById(R.id.bottomNavigationView);
        bottomView.setOnNavigationItemSelectedListener(listener);

        createFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, mainFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.item_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, mainFragment).commit();
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
    };

    private void createFragment() {
        mainFragment = new fragment_main();
        clothStyleFragment = new fragment_clothstyle();
        citySearchFragment = new fragment_citysearch();
        mainCodyImgFragment = new fragment_main_codyimg();
        mainCodySetFragment = new fragment_main_codyset();
    }
}