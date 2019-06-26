package com.aceman.go4lunch.utils.events;

/**
 * Created by Lionel JOFFRAY - on 07/06/2019.
 * Event to call for a search name with autocomplete.
 *
 * @see com.aceman.go4lunch.activities.core.CoreActivity
 * @see com.aceman.go4lunch.fragments.maps.MapsFragment
 */
public class SearchRefreshEvent {

    public String mSearchID;


    public SearchRefreshEvent(String searchID) {
        mSearchID = searchID;
    }
}
