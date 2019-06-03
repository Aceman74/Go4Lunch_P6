package com.aceman.go4lunch.events;

import com.aceman.go4lunch.data.nearby_search.Result;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 03/06/2019.
 */
public class PlacesDetailEvent {


    public Result mDetail;
    public String mUrl;

    public PlacesDetailEvent(Result detail, String url) {
        mDetail = detail;
        mUrl = url;
    }
}
