package com.example.cody_c.pagefragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cody_c.R;
import com.example.cody_c.util.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_main_codyimg1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_main_codyimg1 extends Fragment {
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

    public fragment_main_codyimg1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_main_codyimg2.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_main_codyimg1 newInstance(String param1, String param2) {
        fragment_main_codyimg1 fragment = new fragment_main_codyimg1();
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

        codyLibraryFragment = new fragment_cody_lib();

        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_main_codyimg1, container, false);
        currentCodyImg = (ImageView) rootView.findViewById(R.id.main_cody_image1);
        currentBookMark = (ImageView) rootView.findViewById(R.id.main_cody_bookmark1);

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
                    Log.e("????????? ??? ?????? ??????", "??????");
                    currentBookMark.setImageResource(R.drawable.bookmark_empty);
                    bookMarkSet.remove(String.valueOf(curRecommendedImgDrawble));
                }else{
                    Log.e("????????? ?????? ?????? ??????", "??????");
                    currentBookMark.setImageResource(R.drawable.bookmark);
                    bookMarkSet.add(String.valueOf(curRecommendedImgDrawble));
                }
                PreferenceManager.setStringSet(getContext(), "bookMark", bookMarkSet);
            }
        });

        return rootView;
    }

    public void setCodyImg(){
        //?????? ????????? ?????? ????????? ????????? ????????? ???????????? ??????.
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
        curRecommendedImgDrawble = recommendedCodyArr.get(rand.nextInt(recommendedCodyArr.size())); //?????? ??????????????? ????????? ????????? ??????
        currentCodyImg.setImageResource(curRecommendedImgDrawble);//???????????? ??????

        //????????? ?????? ??????
        bookMarkSet = PreferenceManager.getStringSet(getContext(),"bookMark");
        //bookMarkSet??? curRecommendedImgDrawble(?????? ????????? ????????? ??????)??? ?????? ?????? ????????? bookmark ?????? ????????? ?????? ??????.
        currentBookMark.setImageResource(bookMarkSet.contains(String.valueOf(curRecommendedImgDrawble)) ? R.drawable.bookmark : R.drawable.bookmark_empty);
    }

}