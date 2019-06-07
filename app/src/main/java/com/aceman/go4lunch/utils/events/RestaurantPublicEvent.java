package com.aceman.go4lunch.utils.events;

import com.aceman.go4lunch.models.RestaurantPublic;

/**
 * Created by Lionel JOFFRAY - on 03/06/2019.
 */
public class RestaurantPublicEvent {


    public RestaurantPublic mRestaurantPublic;

    public RestaurantPublicEvent(RestaurantPublic restaurantPublic) {
        mRestaurantPublic = restaurantPublic;
    }
}
