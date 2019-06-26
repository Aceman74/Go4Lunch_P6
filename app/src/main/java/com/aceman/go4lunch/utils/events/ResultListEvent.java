package com.aceman.go4lunch.utils.events;

import com.aceman.go4lunch.data.places.nearby_search.Result;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 17/05/2019.
 * Event to call for a Result list.
 *
 * @see com.aceman.go4lunch.fragments.listView.ListViewFragment
 * @see com.aceman.go4lunch.fragments.maps.MapsFragment
 */
public class ResultListEvent {

    public List<Result> mResults;

    public ResultListEvent(List<Result> results) {
        mResults = results;
    }
}
