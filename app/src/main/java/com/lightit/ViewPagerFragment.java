package com.lightit;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {


    public ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ViewPager pager = rootView.findViewById(R.id.viewPager);
        PagerAdapter adapter = new ViewPagerAdapter(getActivity());
        pager.setAdapter(adapter);

        TabLayout tabLayout=rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager,true);

        return rootView;
    }

}
