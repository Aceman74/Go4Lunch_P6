package com.aceman.go4lunch.fragments.maps;

import com.aceman.go4lunch.data.places.details.PlacesDetails;
import com.aceman.go4lunch.data.places.nearby_search.Nearby;
import com.aceman.go4lunch.data.places.nearby_search.Result;
import com.aceman.go4lunch.utils.BaseView;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 * <p>
 * Maps contracts.
 */
public interface MapsContract {

    interface MapsPresenterInterface {

        void executeHttpRequestWithRetrofit(final String mLocation, final String mType, int mRadius, List<Result> mResults);

        void detailsHttpRequestWithRetrofit(List<Result> mResults);

        void getSearchRestaurant(String mSearchID);

    }

    interface MapsViewInterface extends BaseView {
        void refreshMapBtn();

        void getLastLocation();

        void initializeMapsAndPlaces();

        void initLocationOnLaunch();

        void updateLocation();

        void markerSetPreviousPos();

        void getCurrentMapInfo();

        void onMarkerClickListener();

        void customInfoWindows();

        void updateData(Nearby details);

        void noResultFound();

        void updateResultList(Result result, PlacesDetails details);

        void setDistanceAndRating(Result result);

        void updateMap(final PlacesDetails details);

        void disposeWhenDestroy();

        void clearMapMarkers();

        void postEventBusAfterRequest();

        void zoomOnMapLocation();

        void addDetail(PlacesDetails details);

        String isEatingHereHourCheck(int i);

        void getLocationPermission();
    }
}
