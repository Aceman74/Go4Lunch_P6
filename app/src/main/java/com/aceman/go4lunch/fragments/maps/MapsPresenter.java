package com.aceman.go4lunch.fragments.maps;

import android.util.Log;

import com.aceman.go4lunch.api.PlacesApi;
import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.data.nearby_search.Nearby;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.utils.BasePresenter;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

/**
 * Created by Lionel JOFFRAY - on 04/06/2019.
 */
public class MapsPresenter extends BasePresenter implements MapsContract.MapsPresenterInterface {


    @Override
    public void executeHttpRequestWithRetrofit(final String mLocation, final String mType, int mRadius, final List<Result> mResults) {

        Disposable newDisposable = PlacesApi.getInstance().getLocationInfo(mLocation, mType, mRadius).subscribeWith(new DisposableObserver<Nearby>() {
            @Override
            public void onNext(Nearby details) {
                Timber.tag("NEARBY_Next").i("On Next");
                Timber.tag("NEARBY_OBSERVABLE").i("from: " + mLocation + " type: " + mType);
                ((MapsContract.MapsViewInterface) getView()).clearMapMarkers();
                ((MapsContract.MapsViewInterface) getView()).updateData(details);
            }

            @Override
            public void onError(Throwable e) {
                Timber.tag("NEARBY_Error").e("On Error%s", Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Timber.tag("NEARBY_Complete").i("On Complete !!");
                detailsHttpRequestWithRetrofit(mResults);

            }
        });
    }

    @Override
    public void detailsHttpRequestWithRetrofit(List<Result> mResults) {

        for (final Result result : mResults) {


            Disposable disposable = PlacesApi.getInstance().getRestaurantsDetails(result.getPlaceId()).subscribeWith(new DisposableObserver<PlacesDetails>() {
                @Override
                public void onNext(PlacesDetails details) {
                    Timber.tag("PLACES_Next").i("On Next");
                    ((MapsContract.MapsViewInterface) getView()).updateMap(details);
                    ((MapsContract.MapsViewInterface) getView()).updateResultList(result, details);
                }

                @Override
                public void onError(Throwable e) {
                    Timber.tag("PLACES_Error").e("On Error%s", Log.getStackTraceString(e));
                }

                @Override
                public void onComplete() {
                    Timber.tag("PLACES_Complete").i("On Complete !!");
                }
            });
        }
        ((MapsContract.MapsViewInterface) getView()).noResultFound();

    }

    @Override
    public void getSearchRestaurant(String mSearchID) {


        Disposable mSearchDisposable = PlacesApi.getInstance().getRestaurantsDetails(mSearchID).subscribeWith(new DisposableObserver<PlacesDetails>() {
            @Override
            public void onNext(PlacesDetails details) {
                Timber.tag("PLACES_Next").i("On Next");
                ((MapsContract.MapsViewInterface) getView()).addDetail(details);
            }

            @Override
            public void onError(Throwable e) {
                Timber.tag("PLACES_Error").e("On Error%s", Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Timber.tag("PLACES_Complete").i("On Complete !!");
                ((MapsContract.MapsViewInterface) getView()).zoomOnMapLocation();
            }
        });

    }

}
