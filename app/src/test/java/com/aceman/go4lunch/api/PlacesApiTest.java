package com.aceman.go4lunch.api;

import com.aceman.go4lunch.data.places.details.PlacesDetails;
import com.aceman.go4lunch.data.places.nearby_search.Nearby;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Lionel JOFFRAY - on 27/05/2019.
 * <p>
 * This class test the API Requests.
 */
public class PlacesApiTest {
    private Retrofit mRetrofit;
    private PlacesCall mPlacesCall;
    private String mType = "restaurant";
    private String mLocation;
    private int mRadius = 500;
    private String statusOk = "OK";
    private String statusZero = "ZERO_RESULTS";
    private String mID = "ChIJZ-mNLJBBjEcRXtzyxVZt-vg"; // "Villa Cecile" place

    @Before
    public void setUp() {
        mRetrofit = PlacesApi.getInstance().mRetrofit;
        mPlacesCall = mRetrofit.create(PlacesCall.class);
    }


    /**
     * Test the Retrofit base call on the URL
     */
    @Test
    public void settingRetrofitTest() {

        assertEquals("https://maps.googleapis.com/maps/api/place/", mRetrofit.baseUrl().toString());
        assertNotNull(mRetrofit);
        assertTrue(mRetrofit.baseUrl().isHttps());

    }

    /**
     * Test a basic nearby call.
     */
    @Test
    public void getLocationInfo() {
        mLocation = "46.36430767482289 6.321261227130891"; // Villa cecile (only one)
        Nearby near = mPlacesCall.getLocationInfo(mLocation, mType, mRadius).blockingFirst();
        assertNotNull(near);    //  Check for responses
        assertTrue(near.getResults().size() > 0);  //  Check for results

        assertEquals(near.getStatus(), statusOk);  //  Test status response

        String itemType = "Villa Cecile";
        assertEquals(near.getResults().get(0).getName(), itemType);   //  Test if restaurant is Villa Cecile
    }

    /**
     * Test an empty location (middle water).
     */
    @Test
    public void getLocationInfoEmptyPlaceCase() {
        mLocation = "46.37839986823946 6.318713799118996"; // empty place
        Nearby near = mPlacesCall.getLocationInfo(mLocation, mType, mRadius).delaySubscription(5, TimeUnit.SECONDS).blockingFirst();
        assertNotNull(near);    //  Check for responses
        assertEquals(0, near.getResults().size());  //  Check for results

        assertEquals(near.getStatus(), statusZero);  //  Test status response
    }

    /**
     * Test a detail request on a restaurant ID.
     */
    @Test
    public void getRestaurantsDetails() {
        PlacesDetails near = mPlacesCall.getRestaurantsDetails(mID).blockingFirst();
        assertNotNull(near);    //  Check for responses
        assertFalse(near.getResult().getName().isEmpty());  //  Check for results

        assertEquals(near.getStatus(), statusOk);  //  Test status response

        String phone = "04 50 72 27 40";
        assertEquals(near.getResult().getFormattedPhoneNumber(), phone);   //  Test if restaurant is Villa Cecile
    }
}