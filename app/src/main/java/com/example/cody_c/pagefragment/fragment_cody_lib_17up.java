package com.example.cody_c.pagefragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cody_c.R;
import com.example.cody_c.util.PreferenceManager;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_cody_lib_17up#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_cody_lib_17up extends Fragment {
    private View rootView;
    private Set<String> bookMarkSet;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_cody_lib_17up() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_cody_lib_17up.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_cody_lib_17up newInstance(String param1, String param2) {
        fragment_cody_lib_17up fragment = new fragment_cody_lib_17up();
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
        rootView = inflater.inflate(R.layout.fragment_cody_lib_17up, container, false);

        //SharedPreference 내에 HashSet<String> 타입으로 존재하는 북마크 받아옥
        bookMarkSet = PreferenceManager.getStringSet(getContext(),"bookMark");

        int [] myLibCody = {R.id.book_lib17_cody1,R.id.book_lib17_cody2, R.id.book_lib17_cody3, R.id.book_lib17_cody4, R.id.book_lib17_cody5, R.id.book_lib17_cody6, R.id.book_lib17_cody7, R.id.book_lib17_cody8, R.id.book_lib17_cody9};
        int [] myLibBookMark = {R.id.bookmark_lib17_cody1,R.id.bookmark_lib17_cody2,R.id.bookmark_lib17_cody3,R.id.bookmark_lib17_cody4,R.id.bookmark_lib17_cody5,R.id.bookmark_lib17_cody6,R.id.bookmark_lib17_cody7,R.id.bookmark_lib17_cody8,R.id.bookmark_lib17_cody9};
        int [] codyLibArr = {R.drawable.cody3, R.drawable.cody5, R.drawable.cody6, R.drawable.cody18};
        int curIdx = 0;

        while(curIdx <= codyLibArr.length-1){
            ImageView curCodyImg = (ImageView) rootView.findViewById(myLibCody[curIdx]);
            ImageView curBookMarkImg = (ImageView) rootView.findViewById(myLibBookMark[curIdx]);

            //이미지가 존재할 때만 북마크와 eventListener를 생성
            if(curCodyImg.getBackground() != null){
                String curCodyImg_background = String.valueOf(codyLibArr[curIdx]);
                if(bookMarkSet.contains(curCodyImg_background)) curBookMarkImg.setImageResource(R.drawable.bookmark);
                else curBookMarkImg.setImageResource(R.drawable.bookmark_empty);

                curCodyImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bookMarkSet.contains(curCodyImg_background)){
                            curBookMarkImg.setImageResource(R.drawable.bookmark_empty);
                            bookMarkSet.remove(curCodyImg_background);
                        }else{
                            curBookMarkImg.setImageResource(R.drawable.bookmark);
                            bookMarkSet.add(curCodyImg_background);
                        }
                        PreferenceManager.setStringSet(getContext(), "bookMark", bookMarkSet);
                    }
                });
            }
            curIdx++;
        }

        return rootView;
    }
}