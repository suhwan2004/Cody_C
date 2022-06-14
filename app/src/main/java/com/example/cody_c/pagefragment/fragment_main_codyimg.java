package com.example.cody_c.pagefragment;

import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cody_c.MainActivity;
import com.example.cody_c.R;
import com.example.cody_c.util.PreferenceManager;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_main_codyimg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_main_codyimg extends Fragment{
    private ImageView currentCodyImg;
    private ImageView currentBookMark;
    private View rootView;
    private int curRecommendedImgDrawble;
    private Fragment codyLibraryFragment;
    private Set<String> bookMarkSet;
    private int curTemp;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_main_codyimg() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_maincodyimg.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_main_codyimg newInstance(String param1, String param2) {
        fragment_main_codyimg fragment = new fragment_main_codyimg();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        codyLibraryFragment = new fragment_cody_lib();
        rootView = inflater.inflate(R.layout.fragment_main_codyimg, container, false);

        currentCodyImg = (ImageView) rootView.findViewById(R.id.main_cody_image);
        currentBookMark = (ImageView) rootView.findViewById(R.id.main_cody_bookmark);

        setCodyImg();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        currentCodyImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curTemp > 27) ft.replace(R.id.frameLayout, new fragment_cody_lib_27up()).addToBackStack(null).commit();
                else if(curTemp > 23) ft.replace(R.id.frameLayout,new fragment_cody_lib_23up()).addToBackStack(null).commit();
                else if(curTemp > 19) ft.replace(R.id.frameLayout,new fragment_cody_lib_20up()).addToBackStack(null).commit();
                else if(curTemp > 16) ft.replace(R.id.frameLayout,new fragment_cody_lib_17up()).addToBackStack(null).commit();
                else if(curTemp > 11) ft.replace(R.id.frameLayout,new fragment_cody_lib_12up()).addToBackStack(null).commit();
                else if(curTemp > 9) ft.replace(R.id.frameLayout,new fragment_cody_lib10up()).addToBackStack(null).commit();
                else if(curTemp > 5) ft.replace(R.id.frameLayout,new fragment_cody_lib_6up()).addToBackStack(null).commit();
                else ft.replace(R.id.frameLayout,new fragment_cody_lib_5down()).addToBackStack(null).commit();
            }
        });

        currentBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookMarkSet.contains(String.valueOf(curRecommendedImgDrawble))){
                    Log.e("북마크 된 채로 누름", "삭제");
                    currentBookMark.setImageResource(R.drawable.bookmark_empty);
                    bookMarkSet.remove(String.valueOf(curRecommendedImgDrawble));
                }else{
                    Log.e("북마크 안된 채로 누름", "추가");
                    currentBookMark.setImageResource(R.drawable.bookmark);
                    bookMarkSet.add(String.valueOf(curRecommendedImgDrawble));
                }
                PreferenceManager.setStringSet(getContext(), "bookMark", bookMarkSet);
            }
        });

        return rootView;
    }

    public void setCodyImg(){
        //현재 온도에 맞는 코디들 중에서 하나를 랜덤으로 고름.
        /*
            ~-5 : cody22
            6 : cody 19, 21
            10 : cody1, 4, 20
            12 : cody2, 14
            17 : cody3, 5, 6
            20 : cody7, 8, 9
            23 : cody11, 12, 15
            27~ : cody10, 13, 17
         */

        curTemp = PreferenceManager.getInt(getContext(), "temp");

        ArrayList<Integer> recommendedCodyArr;
        if(curTemp > 27) recommendedCodyArr = new ArrayList<Integer>(Arrays.asList(R.drawable.cody10,R.drawable.cody13,R.drawable.cody17));
        else if(curTemp > 23) recommendedCodyArr = new ArrayList<Integer>(Arrays.asList(R.drawable.cody11,R.drawable.cody12,R.drawable.cody15));
        else if(curTemp > 19) recommendedCodyArr = new ArrayList<Integer>(Arrays.asList(R.drawable.cody7, R.drawable.cody8, R.drawable.cody9));
        else if(curTemp > 16) recommendedCodyArr = new ArrayList<Integer>(Arrays.asList(R.drawable.cody3,R.drawable.cody5,R.drawable.cody6));
        else if(curTemp > 11) recommendedCodyArr = new ArrayList<Integer>(Arrays.asList(R.drawable.cody2,R.drawable.cody14));
        else if(curTemp > 9) recommendedCodyArr = new ArrayList<Integer>(Arrays.asList(R.drawable.cody1, R.drawable.cody4, R.drawable.cody20));
        else if(curTemp > 5) recommendedCodyArr = new ArrayList<Integer>(Arrays.asList(R.drawable.cody19, R.drawable.cody21));
        else recommendedCodyArr = new ArrayList<Integer>(Arrays.asList(R.drawable.cody22));

        Random rand = new Random();
        curRecommendedImgDrawble = recommendedCodyArr.get(rand.nextInt(recommendedCodyArr.size())); //현재 페이지에서 추천된 코디의 경로
        currentCodyImg.setImageResource(curRecommendedImgDrawble);//이미지를 삽입

        //북마크 여부 확인
        bookMarkSet = PreferenceManager.getStringSet(getContext(),"bookMark");
        //bookMarkSet에 curRecommendedImgDrawble(현재 추천된 코디의 경로)를 통해 현재 코디가 bookmark 되어 있는지 여부 판단.
        currentBookMark.setImageResource(bookMarkSet.contains(String.valueOf(curRecommendedImgDrawble)) ? R.drawable.bookmark : R.drawable.bookmark_empty);
    }

}