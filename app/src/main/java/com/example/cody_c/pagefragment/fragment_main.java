package com.example.cody_c.pagefragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cody_c.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_main extends Fragment {
    private Fragment mainCodyImg, mainCodySet;
    final int NUM_PAGES = 2;

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
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        mainCodyImg = new fragment_main_codyimg();
        mainCodySet = new fragment_main_codyset();

        ViewPager2 viewPager2 = view.findViewById(R.id.viewpager);
        viewPager2.setAdapter(new viewPagerAdapter(this)); // 여기서 this로 뷰페이저가 포함되어 있는 현재 프래그먼트(HomeFragment)를 인수로 넣어준다.
        viewPager2.setCurrentItem(0);
        return view;
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
}