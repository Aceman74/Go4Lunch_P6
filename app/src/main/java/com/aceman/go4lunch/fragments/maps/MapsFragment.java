package com.aceman.go4lunch.fragments.maps;


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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceman.go4lunch.BuildConfig;
import com.aceman.go4lunch.R;
import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.data.nearby_search.Nearby;
import com.aceman.go4lunch.data.nearby_search.Result;
import com.aceman.go4lunch.models.RestaurantPublic;
import com.aceman.go4lunch.utils.AnimationClass;
import com.aceman.go4lunch.utils.DateSetter;
import com.aceman.go4lunch.utils.HourSetter;
import com.aceman.go4lunch.utils.events.RefreshEvent;
import com.aceman.go4lunch.utils.events.ResultListEvent;
import com.aceman.go4lunch.utils.events.SearchRefreshEvent;
import com.aceman.go4lunch.utils.events.UserListEvent;
import com.arsy.maps_library.MapRadar;
import com.arsy.maps_library.MapRipple;
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
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static com.aceman.go4lunch.activities.core.CoreActivity.sFusedLocationProviderClient;


public class MapsFragment extends Fragment implements GoogleMap.OnMyLocationClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, MapsContract.MapsViewInterface {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    public static GoogleMap mMaps;
    public List<Result> mResults;
    public LatLng mSearchLatLng;
    public String mSearchName;
    public PlacesDetails mSearchDetails;
    public List<RestaurantPublic> mUserList = new ArrayList<>();
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
    int mRadius = 500;  //  >>> RADIUS <<<<
    String mSearchID;
    LatLng mLatLng;
    LatLng mLastLatLng;
    String mMarkerTitle;
    Double mRating;
    int mDistanceRounded;
    int getinfo = 0;
    GoogleApiClient mGoogleApiClient;
    Boolean initLocation = true;
    float distance = 0;
    @BindView(R.id.maps_btn)
    FloatingActionButton mRefreshBtn;
    MapsPresenter mPresenter;
    MapRipple mapRipple;
    MapRadar mapRadar;
    private String API_KEY = BuildConfig.google_maps_key;
    private String mPrevSearchID;


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
        mPresenter = new MapsPresenter();
        mPresenter.attachView(this);
        initializeMapsAndPlaces();
        mResults = new ArrayList<>();
        refreshMapBtn();
        return view;
    }

    @Subscribe(sticky = true)
    public void onUserListEvent(UserListEvent userlist) {
        mUserList = userlist.mUserList;
    }

    @Subscribe
    public void onSearchRefreshEvent(SearchRefreshEvent event) {
        mSearchID = event.mSearchID;
        mPresenter.getSearchRestaurant(event.mSearchID);
        getCurrentMapInfo();

    }

    @Override
    public void addDetail(PlacesDetails details) {
        mSearchLatLng = new LatLng(details.getResult().getGeometry().getLocation().getLat(), details.getResult().getGeometry().getLocation().getLng());
        mSearchName = details.getResult().getName();
        mSearchDetails = details;
    }

    @Override
    public void zoomOnMapLocation() {
        mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(mSearchLatLng, 14));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void refreshMapBtn() {
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshBtn.startAnimation(AnimationClass.refreshClick(getContext()));
                getCurrentMapInfo();
            }
        });
    }

    @Override
    public void getLastLocation() {
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
                                        mType = getString(R.string.restaurant);
                                        mCurrentLocation = location;
                                        mPreviousLocation = new Location("");
                                        mDistanceToLocation = new Location("");
                                        mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
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

    @Override
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
             * >> DISABLE DURING DEV FOR REQUEST QUOTAS <<
             if (distance > 1500) {
             getCurrentMapInfo();
             }
             */

        }
        if (mSearchID != null && !mSearchID.equals(mPrevSearchID)) {
            getCurrentMapInfo();
            mPrevSearchID = mSearchID;
            mSearchID = null;
        }
    }


    @Override
    public void updateLocation() {
        if (mCurrentLocation != null) {
            mCurrentLocation.setLongitude(mMaps.getCameraPosition().target.longitude);
            mCurrentLocation.setLatitude(mMaps.getCameraPosition().target.latitude);
        }
    }

    @Override
    public void initLocationOnLaunch() {
        if (initLocation) {
            markerSetPreviousPos();
            getCurrentMapInfo();
            initLocation = false;
        }
    }

    @Override
    public void markerSetPreviousPos() {
        if (mMarker == null) {
            mMarker = mMaps.addMarker(new MarkerOptions().title("PreviousLocation").visible(false).position(mLastLatLng));
        } else {
            mMarker.setPosition(mLatLng);
        }
        mPreviousLocation.setLongitude(mMarker.getPosition().longitude);
        mPreviousLocation.setLatitude(mMarker.getPosition().latitude);
    }

    @Override
    public void getCurrentMapInfo() {
        getinfo++;
        mMaps.clear();
        mLatLng = mMaps.getCameraPosition().target;
        mMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
        mLatitude = mLatLng.latitude;
        mLongitude = mLatLng.longitude;
        mLocation = mLatitude + " " + mLongitude;
        mType = getString(R.string.restaurant);
        mPresenter.executeHttpRequestWithRetrofit(mLocation, mType, mRadius, mResults);
        markerSetPreviousPos();
    }

    @Override
    public void onMarkerClickListener() {
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

    @Override
    public void customInfoWindows() {
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

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "You are here !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void getLocationPermission() {
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

    @Override
    public void updateData(Nearby details) {
        mResults.clear();
        mResults.addAll(details.getResults());
    }

    @Override
    public void noResultFound() {
        if (mResults.size() == 0) {
            Marker noResult = mMaps.addMarker(new MarkerOptions().title("No results found!").snippet("No restaurants here within 1Km").position(mLatLng));
            noResult.showInfoWindow();
        } else {
            postEventBusAfterRequest();
        }
    }

    @Override
    public void updateResultList(Result result, PlacesDetails details) {
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
    }

    @Override
    public void setDistanceAndRating(Result result) {
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

    @Override
    public void updateMap(final PlacesDetails details) {
        String date = DateSetter.getFormattedDate();
        mLatLng = new LatLng(details.getResult().getGeometry().getLocation().getLat(), details.getResult().getGeometry().getLocation().getLng());

        if (details.getResult().getName().equals(mSearchName)) {

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
        if (mUserList != null) {
            for (int i = 0; i < mUserList.size(); i++) {
                RestaurantPublic user = mUserList.get(i);
                if (user.getRestaurantID() != null && user.getRestaurantID().equals(details.getResult().getPlaceId())
                        && user.getDate() != null && user.getDate().equals(date)) {
                    mPosMarker.remove();
                    mPosMarker = mMaps.addMarker(new MarkerOptions()
                            .position(mLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_g))
                            .title(details.getResult().getName())
                            .snippet(isEatingHereHourCheck(i)));
                }
            }
        }
    }

    private String isEatingHereHourCheck(int i) {
        String date = DateSetter.getFormattedDate();
        int hour = HourSetter.getHour();
        if (mUserList.get(i).getDate().equals(date) && hour < 13) {
            return mUserList.get(i).getUsername() + " is eating here !";
        } else {
            return mUserList.get(i).getUsername() + " ate here today !";
        }
    }

    @Override
    public void disposeWhenDestroy() {
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
        postEventBusAfterRequest();
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

    @Override
    public void clearMapMarkers() {
        mMaps.clear();
    }

    @Override
    public void postEventBusAfterRequest() {
        EventBus.getDefault().post(new ResultListEvent(mResults));
        EventBus.getDefault().post(new RefreshEvent());
    }
}
