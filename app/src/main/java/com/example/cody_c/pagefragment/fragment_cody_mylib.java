package com.example.cody_c.pagefragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cody_c.R;
import com.example.cody_c.util.PreferenceManager;

import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_cody_mylib#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_cody_mylib extends Fragment {
    private View rootView;
    private Set<String> bookMarkSet;
    private LinearLayout myCodyLib;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_cody_mylib() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_cody_mylib.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_cody_mylib newInstance(String param1, String param2) {
        fragment_cody_mylib fragment = new fragment_cody_mylib();
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


    /*
    현재, 이 페이지의 경우 최대 9개 까지만 북마크가 되도록 구동됨.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_cody_mylib, container, false);
        myCodyLib = rootView.findViewById(R.id.mycodylib);
        bookMarkSet = PreferenceManager.getStringSet(getContext(),"bookMark");

        String[] bookMarkArr = bookMarkSet.toArray(new String[0]); //북마크 hashSet을 String[]로 변환
        int [] myLibCody = {R.id.book_cody1,R.id.book_cody2, R.id.book_cody3, R.id.book_cody4, R.id.book_cody5, R.id.book_cody6, R.id.book_cody7, R.id.book_cody8, R.id.book_cody9};
        int [] myLibBookMark = {R.id.bookmark_cody1,R.id.bookmark_cody2,R.id.bookmark_cody3,R.id.bookmark_cody4,R.id.bookmark_cody5,R.id.bookmark_cody6,R.id.bookmark_cody7,R.id.bookmark_cody8,R.id.bookmark_cody9};
        int curIdx = 0;

        if(bookMarkSet.size() > 0 || curIdx < 9){
            while(curIdx <= bookMarkArr.length-1){
                ImageView curCodyImg = (ImageView) rootView.findViewById(myLibCody[curIdx]);
                ImageView curBookMarkImg = (ImageView) rootView.findViewById(myLibBookMark[curIdx]);
                int curBookMarkedCodyImg = Integer.parseInt(bookMarkArr[curIdx++]);

                curCodyImg.setImageResource(curBookMarkedCodyImg);
                curBookMarkImg.setImageResource(R.drawable.bookmark);

                curCodyImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bookMarkSet.contains(String.valueOf(curBookMarkedCodyImg))){
                            curBookMarkImg.setImageResource(R.drawable.bookmark_empty);
                            bookMarkSet.remove(String.valueOf(curBookMarkedCodyImg));
                        }else{
                            curBookMarkImg.setImageResource(R.drawable.bookmark);
                            bookMarkSet.add(String.valueOf(curBookMarkedCodyImg));
                        }
                        PreferenceManager.setStringSet(getContext(), "bookMark", bookMarkSet);
                    }
                });
            }
        }
        return rootView;
    }
}