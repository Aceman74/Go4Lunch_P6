package com.aceman.go4lunch.api;


import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.data.places.details.PlacesDetails;
import com.aceman.go4lunch.data.places.nearby_search.Nearby;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Lionel JOFFRAY - on 12/05/2019.
 * <p>
 * API Call with hidden API Key.
 */
public interface PlacesCall {
    String API_KEY = BuildConfig.google_maps_key;

    @GET("nearbysearch/json?&key=" + API_KEY)
    Observable<Nearby> getLocationInfo(@Query("location") String location, @Query("type") String type, @Query("radius") int radius);
    // location=(latlong); type=(restaurant);

    @GET("details/json?&key=" + API_KEY)
    Observable<PlacesDetails> getRestaurantsDetails(@Query("placeid") String id);
    // location=(latlong); type=(restaurant);

}

