package com.aceman.go4lunch.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aceman.go4lunch.fragments.listView.ListViewFragment;
import com.aceman.go4lunch.fragments.maps.MapsFragment;
import com.aceman.go4lunch.fragments.workmates.WorkmatesFragment;


/**
 * Created by Lionel JOFFRAY - on 02/05/2019.
 * <p>
 * Page adapter for fragments.
 *
 * @see com.aceman.go4lunch.activities.core.CoreActivity
 */
public class PageAdapter extends FragmentPagerAdapter {
    private final Context mContext;

    public PageAdapter(FragmentManager mgr, Context context) {
        super(mgr);
        mContext = context;
    }

    /**
     * Page numbers.
     *
     * @return number of pages
     */
    @Override
    public int getCount() {
        return (3);
    }

    /**
     * Set the fragment to show.
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

