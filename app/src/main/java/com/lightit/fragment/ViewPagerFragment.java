package com.lightit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lightit.R;
import com.lightit.adapter.ViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);

        ViewPager pager = rootView.findViewById(R.id.viewPager);
        PagerAdapter adapter = new ViewPagerAdapter(getActivity());
        pager.setAdapter(adapter);

        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager, true);

        return rootView;
    }

}
