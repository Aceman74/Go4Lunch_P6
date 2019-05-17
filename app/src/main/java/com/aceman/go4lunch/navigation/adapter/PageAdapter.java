package com.aceman.go4lunch.navigation.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aceman.go4lunch.navigation.fragments.ListViewFragment;
import com.aceman.go4lunch.navigation.fragments.MapsFragment;
import com.aceman.go4lunch.navigation.fragments.WorkmatesFragment;


/**
 * Created by Lionel JOFFRAY - on 05/03/2019.
 * <b>Page Adapter</b> for TabView
 */
public class PageAdapter extends FragmentPagerAdapter {


    private final Context mContext;

    //Default Constructor
    public PageAdapter(FragmentManager mgr, Context context) {
        super(mgr);
        mContext = context;
    }

    @Override
    public int getCount() {
        return (3);
    }

    /**
     * Set what fragment to show
     *
     * @param position actual view
     * @return the fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //Page number 1
                return MapsFragment.newInstance();
            case 1: //Page number 2
                return ListViewFragment.newInstance();
            case 2: //Page number 3
                return WorkmatesFragment.newInstance();
            default:
                return null;
        }
    }
}

