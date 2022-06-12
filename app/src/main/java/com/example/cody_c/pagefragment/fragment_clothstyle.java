package com.example.cody_c.pagefragment;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.cody_c.adapter.ClothListAdapter;
import com.example.cody_c.data.ClothNode;
import com.example.cody_c.dto.CustomDTO;
import com.example.cody_c.R;
import com.example.cody_c.adapter.CustomAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_clothstyle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_clothstyle extends Fragment {
    ArrayList<ClothNode> codyDataList;
    private CustomAdapter adapter;
    private ListView listView;

    private Fragment page_5down, page_6up, page_10up, page_12up, page_17up, page_20up, page_23up, page_27up;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_clothstyle() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_2.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_clothstyle newInstance(String param1, String param2) {
        fragment_clothstyle fragment = new fragment_clothstyle();
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
        View rootView = inflater.inflate(R.layout.fragment_clothstyle, container, false);
        this.InitializeCodyData();
        this.InitializeCodyLibraryPages();
        listView = (ListView) rootView.findViewById(R.id.clothstyle_listView);
        final ClothListAdapter clothListAdapter = new ClothListAdapter(rootView.getContext(), codyDataList);

        listView.setAdapter(clothListAdapter);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction ft = fragmentManager.beginTransaction();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        ft.replace(R.id.frameLayout, page_5down).addToBackStack(null).commit();
                        return;
                    case 1:
                        ft.replace(R.id.frameLayout,page_6up).addToBackStack(null).commit();
                        return;
                    case 2:
                        ft.replace(R.id.frameLayout,page_10up).addToBackStack(null).commit();
                        return;
                    case 3:
                        ft.replace(R.id.frameLayout,page_12up).addToBackStack(null).commit();
                        return;
                    case 4:
                        ft.replace(R.id.frameLayout,page_17up).addToBackStack(null).commit();
                        return;
                    case 5:
                        ft.replace(R.id.frameLayout,page_20up).addToBackStack(null).commit();
                        return;
                    case 6:
                        ft.replace(R.id.frameLayout,page_23up).addToBackStack(null).commit();
                        return;
                    case 7:
                        ft.replace(R.id.frameLayout,page_27up).addToBackStack(null).commit();
                        return;
                }
            }
        });


        return rootView;
    }


    public void InitializeCodyData()
    {
        codyDataList = new ArrayList<ClothNode>();

        codyDataList.add(new ClothNode(R.drawable.cody1," ~ 5ºC"));
        codyDataList.add(new ClothNode(R.drawable.cody2,"6 ~ 10ºC"));
        codyDataList.add(new ClothNode(R.drawable.cody3,"10 ~ 12ºC"));
        codyDataList.add(new ClothNode(R.drawable.cody4,"12 ~ 17ºC"));
        codyDataList.add(new ClothNode(R.drawable.cody5,"17 ~ 20ºC"));
        codyDataList.add(new ClothNode(R.drawable.cody6,"20 ~ 23ºC"));
        codyDataList.add(new ClothNode(R.drawable.cody7,"23 ~ 27ºC"));
        codyDataList.add(new ClothNode(R.drawable.cody7,"27ºC ~"));
    }

    public void InitializeCodyLibraryPages(){
        page_5down = new fragment_cody_lib_5down();
        page_6up = new fragment_cody_lib_6up();
        page_10up = new fragment_cody_lib10up();
        page_12up = new fragment_cody_lib_12up();
        page_17up = new fragment_cody_lib_17up();
        page_20up = new fragment_cody_lib_20up();
        page_23up = new fragment_cody_lib_23up();
        page_27up = new fragment_cody_lib_27up();
    }
}