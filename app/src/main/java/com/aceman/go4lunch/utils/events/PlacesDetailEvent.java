package com.aceman.go4lunch.utils.events;

import com.aceman.go4lunch.data.places.nearby_search.Result;

/**
 * Created by Lionel JOFFRAY - on 03/06/2019.
 * Event for the Result list.
 *
 * @see com.aceman.go4lunch.adapter.ListViewAdapter
 * @see com.aceman.go4lunch.activities.placesDetail.PlacesDetailActivity
 */
public class PlacesDetailEvent {


    public Result mDetail;
    public String mUrl;

    public PlacesDetailEvent(Result detail, String url) {
        mDetail = detail;
        mUrl = url;
    }
}
