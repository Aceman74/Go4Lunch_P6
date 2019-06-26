package com.aceman.go4lunch.utils.events;

import com.aceman.go4lunch.data.models.RestaurantPublic;

/**
 * Created by Lionel JOFFRAY - on 03/06/2019.
 * Event to call for a RestaurantPublic object.
 *
 * @see com.aceman.go4lunch.activities.placesDetail.PlacesDetailActivity
 * @see com.aceman.go4lunch.adapter.WorkersAdapter
 */
public class RestaurantPublicEvent {


    public RestaurantPublic mRestaurantPublic;

    public RestaurantPublicEvent(RestaurantPublic restaurantPublic) {
        mRestaurantPublic = restaurantPublic;
    }
}
