package com.aceman.go4lunch.navigation.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.PlacesApi;
import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.data.nearby_search.Nearby;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;


public class MapsFragment extends Fragment implements GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    GoogleMap mMaps;
    SupportMapFragment mMapsFragment;
    PlacesClient mPlacesClient;
    Marker mMarker;
    Disposable disposable;
    String mLocation;
    String mType;
    Double mLatitude;
    Double mLongitude;
    int mRadius = 1500;
    List<Result> mResults;
    List<PlacesDetails> mPlacesDetails;
    String placeID;
    LatLng mLatLng;
    private String API_KEY = BuildConfig.google_maps_key;


    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.location_fragment, container, false);
        initializeMapsAndPlaces();
        mResults = new ArrayList<>();
        return view;
    }

    public void initializeMapsAndPlaces() {

        mMapsFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_branch_map);
        mMapsFragment.getMapAsync(this);
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), API_KEY);
            mPlacesClient = Places.createClient(getContext());
        }
    }

    public void autocompleteIntent() {

        // Set the fields to specify which types of place data to
// return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMaps = googleMap;
        getLocationPermission();
        mMaps.getUiSettings().setMapToolbarEnabled(false);
        mMaps.getUiSettings().setMyLocationButtonEnabled(true);

        mMaps.setOnMyLocationClickListener(this);
        LatLng paris = new LatLng(48.8534100, 2.3488000);
        mMaps.addMarker(new MarkerOptions().position(paris).title("Paris")).showInfoWindow();
        mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(paris, 10));
        mMaps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mLatitude = latLng.latitude;
                mLongitude = latLng.longitude;
                mLocation = mLatitude + " " + mLongitude;
                mType = "restaurant";
                executeHttpRequestWithRetrofit();
                  mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
            }
        });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMaps.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void executeHttpRequestWithRetrofit() {

        this.disposable = PlacesApi.getInstance().getLocationInfo(mLocation, mType, mRadius).subscribeWith(new DisposableObserver<Nearby>() {
            @Override
            public void onNext(Nearby details) {
                Timber.tag("PLACES_Next").i("On Next");
                Timber.tag("PLACES_OBSERVABLE").i("from: " + mLocation + " type: " + mType);
                updateData(details);
            }

            @Override
            public void onError(Throwable e) {
                Timber.tag("PLACES_Error").e("On Error%s", Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Timber.tag("PLACES_Complete").i("On Complete !!");
                detailsHttpRequestWithRetrofit();
            }
        });
    }

    private void detailsHttpRequestWithRetrofit() {
        for (int i = 0; i < mResults.size(); i++) {
            placeID = mResults.get(i).getPlaceId();

            this.disposable = PlacesApi.getInstance().getRestaurantsDetails(placeID).subscribeWith(new DisposableObserver<PlacesDetails>() {
                @Override
                public void onNext(PlacesDetails details) {
                    Timber.tag("PLACES_Next").i("On Next");
                    Timber.tag("PLACES_OBSERVABLE").i("from: " + mLocation + " type: " + mType);
                    updateMap(details,placeID);
                    Timber.tag("Details").i(placeID);
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

    }

    private void updateData(Nearby details) {
        mResults.clear();
        mResults.addAll(details.getResults());
    }

    private void updateMap(final PlacesDetails details, String placeID) {
        mLatLng = new LatLng(details.getResult().getGeometry().getLocation().getLat(),details.getResult().getGeometry().getLocation().getLng());
        mMaps.addMarker(new MarkerOptions().position(mLatLng).title(details.getResult().getName()));
        mMaps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapsFragment.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapsFragment.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapsFragment.onDestroy();
        disposeWhenDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapsFragment.onLowMemory();
    }
}
