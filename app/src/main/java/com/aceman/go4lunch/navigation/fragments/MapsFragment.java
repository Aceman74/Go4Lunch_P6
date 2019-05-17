package com.aceman.go4lunch.navigation.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.R;
import com.aceman.go4lunch.api.PlacesApi;
import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.data.nearby_search.Nearby;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.events.RefreshEvent;
import com.aceman.go4lunch.events.ResultListEvent;
import com.aceman.go4lunch.navigation.activities.CoreActivity;
import com.aceman.go4lunch.utils.ProgressBarCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.annotations.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static com.aceman.go4lunch.navigation.activities.CoreActivity.sFusedLocationProviderClient;


public class MapsFragment extends Fragment implements GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    public static GoogleMap mMaps;
    public List<Result> mResults;
    SupportMapFragment mMapsFragment;
    PlacesClient mPlacesClient;
    Marker mMarker;
    Marker mPosMarker;
    Marker mSearchMarker;
    Disposable disposable;
    String mLocation;
    Location mCurrentLocation;
    Location mPreviousLocation;
    Location mDistanceToLocation;
    String mType;
    Double mLatitude;
    Double mLongitude;
    float mDistanceTo;
    int mRadius = 1500;
    String placeID;
    LatLng mLatLng;
    LatLng mLastLatLng;
    String mMarkerTitle;
    Double mRating;
    int mDistanceRounded;
    int getinfo = 0;
    GoogleApiClient mGoogleApiClient;
    Boolean initLocation = true;
    ProgressBarCallback mProgressBarCallback;
    float distance = 0;
    @BindView(R.id.maps_btn)
    FloatingActionButton mRefreshBtn;
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
        ButterKnife.bind(this, view);
        initializeMapsAndPlaces();
        // currentLocationRequest();
        mResults = new ArrayList<>();
        mProgressBarCallback = (ProgressBarCallback) getActivity();
        refreshMapBtn();
        return view;
    }

    private void refreshMapBtn() {
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentMapInfo();
            }
        });
    }

    private void getLastLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getActivity()))
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Timber.e("Location permission not granted");
                                return;
                            }

                            Task task = sFusedLocationProviderClient.getLastLocation();

                            task.addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        mLastLatLng = new LatLng(mLatitude = location.getLatitude(), mLongitude = location.getLongitude());
                                        mLocation = mLatitude + " " + mLongitude;
                                        mType = "restaurant";
                                        mCurrentLocation = location;
                                        mPreviousLocation = new Location("");
                                        mDistanceToLocation = new Location("");
                                        mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 14));
                                    }
                                }
                            });
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Timber.e("onConnectionSuspended() ");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Timber.e("Get location failure : %s", connectionResult.getErrorMessage());
                        }
                    })
                    .build();
        }
        mGoogleApiClient.connect();
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMaps = googleMap;
        getLocationPermission();
        mMaps.getUiSettings().setMapToolbarEnabled(false);
        mMaps.getUiSettings().setMyLocationButtonEnabled(true);
        mMaps.setOnMyLocationClickListener(this);
        mMaps.setOnInfoWindowClickListener(this);
        mMaps.setOnCameraIdleListener(this);
        onMarkerClickListener();
        customInfoWindows();
        getLastLocation();
        // getCurrentMapInfo();
        /**
         mMaps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
        @Override public void onMapClick(LatLng latLng) {
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;
        mLocation = mLatitude + " " + mLongitude;
        mType = "restaurant";
        executeHttpRequestWithRetrofit();
        mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
        });
         */
    }

    @Override
    public void onCameraIdle() {
        updateLocation();
        if (mCurrentLocation != null && mPreviousLocation != null) {
            initLocationOnLaunch();
            Timber.tag("GET CURRENT LOCATION").i(mCurrentLocation.toString());
            Timber.tag("GET PREVIOUS LOCATION").i(mPreviousLocation.toString());
            distance = mCurrentLocation.distanceTo(mPreviousLocation);
            Timber.tag("GET DISTANCE ").i(String.valueOf(distance));
            /**
             LatLng latlngtest = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
             mMaps.addMarker(new MarkerOptions().position(latlngtest)
             .title("Centre"));
             */
            if (distance > 1500) {
                getCurrentMapInfo();
            }
        }
    }

    private void initLocationOnLaunch() {
        if (initLocation) {
            markerSetPreviousPos();
            getCurrentMapInfo();
            initLocation = false;
        }
    }

    private void updateLocation() {
        if (mCurrentLocation != null) {
            mCurrentLocation.setLongitude(mMaps.getCameraPosition().target.longitude);
            mCurrentLocation.setLatitude(mMaps.getCameraPosition().target.latitude);
        }
    }

    private void markerSetPreviousPos() {
        if (mMarker == null) {
            mMarker = mMaps.addMarker(new MarkerOptions().title("PreviousLocation").visible(false).position(mLastLatLng));
        } else {
            mMarker.setPosition(mLatLng);
        }
        mPreviousLocation.setLongitude(mMarker.getPosition().longitude);
        mPreviousLocation.setLatitude(mMarker.getPosition().latitude);
    }

    private void getCurrentMapInfo() {
        getinfo++;
        mMaps.clear();
        mLatLng = mMaps.getCameraPosition().target;
        mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 14));
        mLatitude = mLatLng.latitude;
        mLongitude = mLatLng.longitude;
        mLocation = mLatitude + " " + mLongitude;
        mType = "restaurant";
        executeHttpRequestWithRetrofit();
        markerSetPreviousPos();
    }

    private void onMarkerClickListener() {
        mMaps.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                getinfo = 0;
                mMarkerTitle = marker.getTitle();
                Timber.tag("TITLE").i(mMarkerTitle);


                return false;
            }
        });
    }

    private void customInfoWindows() {
        mMaps.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setGravity(Gravity.CENTER);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), "Info window clicked " + marker.getTitle(),
                Toast.LENGTH_SHORT).show();
        String markerName = marker.getTitle();
        for (int i = 0; i < mResults.size(); i++) {
            if (mResults.get(i).getMarker() == marker) {

            }
        }
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
                mMaps.clear();
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

    private void updateData(Nearby details) {
        mResults.clear();
        mResults.addAll(details.getResults());
        EventBus.getDefault().post(new ResultListEvent(mResults));
        noResultFound();
    }


    private void noResultFound() {
        if (mResults.size() == 0) {
            Marker noResult = mMaps.addMarker(new MarkerOptions().title("No results found!").snippet("No restaurants here within 1500m").position(mLatLng));
            noResult.showInfoWindow();
        }
    }

    private void detailsHttpRequestWithRetrofit() {

        for (final Result result : mResults) {


            this.disposable = PlacesApi.getInstance().getRestaurantsDetails(result.getPlaceId()).subscribeWith(new DisposableObserver<PlacesDetails>() {
                @Override
                public void onNext(PlacesDetails details) {
                    Timber.tag("PLACES_Next").i("On Next");
                    Timber.tag("PLACES_OBSERVABLE").i("from: " + mLocation + " type: " + mType);
                    updateMap(details);
                    updateResultList(result, details);
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

        EventBus.getDefault().post(new ResultListEvent(mResults));
        EventBus.getDefault().post(new RefreshEvent());
    }

    private void updateResultList(Result result, PlacesDetails details) {
        mDistanceToLocation.setLatitude(result.getGeometry().getLocation().getLat());
        mDistanceToLocation.setLongitude(result.getGeometry().getLocation().getLng());
        mDistanceTo = mCurrentLocation.distanceTo(mDistanceToLocation);
        result.setDistanceTO(mDistanceTo);
        result.setFormattedAddress(details.getResult().getFormattedAddress());
        result.setFormattedPhoneNumber(details.getResult().getFormattedPhoneNumber());
        result.setWebsite(details.getResult().getWebsite());
        result.setRating(details.getResult().getRating());
        result.setMarker(mMarker);
        setDistanceAndRating(result);
        Timber.tag("Maps Rating").i("%s %s", result.getName(), result.getRatingStars());
    }

    private void setDistanceAndRating(Result result) {
        mDistanceRounded = Math.round(result.getDistanceTo());
        mDistanceRounded = ((mDistanceRounded + 4) / 5) * 5;    //  Round distance to 5m
        result.setDistanceToInt(mDistanceRounded);
        mRating = result.getRating();
        if (mRating != null) {
            if (mRating >= 1 && mRating <= 2.4) {
                result.setRatingStars(1);
            }
            if (mRating >= 2.5 && mRating <= 3.9) {
                result.setRatingStars(2);
            }
            if (mRating >= 4 && mRating <= 5) {
                result.setRatingStars(3);
            }
        }
    }

    private void updateMap(final PlacesDetails details) {
        mLatLng = new LatLng(details.getResult().getGeometry().getLocation().getLat(), details.getResult().getGeometry().getLocation().getLng());

        if (details.getResult().getName().equals(CoreActivity.mSearchName)) {

            mSearchMarker = mMaps.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(details.getResult().getName())
                    .snippet(details.getResult().getFormattedAddress() + "\n" + details.getResult().getFormattedPhoneNumber()));
            mSearchMarker.showInfoWindow();
        } else {
            mPosMarker = mMaps.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_o))
                    .title(details.getResult().getName())
                    .snippet(details.getResult().getFormattedAddress() + "\n" + details.getResult().getFormattedPhoneNumber()));
        }
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
