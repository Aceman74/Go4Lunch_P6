package com.aceman.go4lunch.api;


import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.data.nearby_search.Nearby;
import com.aceman.go4lunch.data.photo.PlacePhoto;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Lionel JOFFRAY - on 13/03/2019.
 * <p>
 * This Class contain all CALL with API key for NYT <br>
 * Using <b>Retrofit</> <br>
 */
public interface PlacesCall {
    String API_KEY = BuildConfig.google_maps_key;

    @GET("nearbysearch/json?&key=" + API_KEY)
    Observable<Nearby> getLocationInfo(@Query("location") String location, @Query("type") String type, @Query("radius") int radius);
    // location=(latlong); type=(restaurant);

    @GET("details/json?&key=" + API_KEY)
    Observable<PlacesDetails> getRestaurantsDetails(@Query("placeid") String id);
    // location=(latlong); type=(restaurant);

    @GET("photo?maxwidth=200&maxheight=200&key=" + API_KEY)
    Observable<PlacePhoto> getRestaurantPhoto(@Query("photoreference") String reference);

}

