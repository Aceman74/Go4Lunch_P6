package com.aceman.go4lunch.fragments.mapsFragment;

import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.data.nearby_search.Nearby;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.utils.BaseView;
import com.aceman.go4lunch.utils.events.UserListEvent;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public interface MapsContract {

    interface MapsPresenterInterface {

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

        void getLocationPermission();

        void updateData(Nearby details);

        void noResultFound();

        void executeHttpRequestWithRetrofit();

        void detailsHttpRequestWithRetrofit();

        void updateResultList(Result result, PlacesDetails details);

        void setDistanceAndRating(Result result);

        void updateMap(final PlacesDetails details);

        void disposeWhenDestroy();


    }
}
