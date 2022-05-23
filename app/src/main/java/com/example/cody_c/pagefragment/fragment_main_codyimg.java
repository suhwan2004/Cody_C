package com.example.cody_c.pagefragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cody_c.MainActivity;
import com.example.cody_c.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_main_codyimg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_main_codyimg extends Fragment{
    private ImageView currentCodyImg;
    private View rootView;
    private Fragment codyLibraryFragment;
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

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        currentCodyImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ft.replace(R.id.frameLayout,codyLibraryFragment).addToBackStack(null).commit();
            }
        });

        return rootView;
    }

}