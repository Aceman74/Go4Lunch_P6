package com.aceman.go4lunch.api;

import com.aceman.go4lunch.data.details.PlacesDetails;
import com.aceman.go4lunch.data.nearby_search.Nearby;
import com.aceman.go4lunch.data.photo.PlacePhoto;
import com.aceman.go4lunch.navigation.activities.MainActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Lionel JOFFRAY - on 13/03/2019.
 * <p>
 * <b>NewsStream</> Class contain all observables for news Call an set Retrofit  <br>
 * Using <b>RX Java and Retrofit</>
 */
public class PlacesApi {

    private static PlacesApi INSTANCE = new PlacesApi();
    Retrofit mRetrofit;

    /**
     * NewStream private constructor for Retrofit
     */
    private PlacesApi() {

        /**
         * Interceptor for caching request for 2 minutes.
         */
        final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                //  Create an Interceptor to  cache all request and avoid Too many request error
                Response originalResponse = chain.proceed(chain.request());
                int maxAge = 120; // read from cache for 2 minutes
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
                /**
                 int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                 return originalResponse.newBuilder()
                 .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                 .build();
                 */
            }
        };
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Dispatcher dispatcherMaxR = new Dispatcher();
        dispatcherMaxR.setMaxRequests(2);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcherMaxR)
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(MainActivity.mCache)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .client(client)
                .build();
    }

    public static PlacesApi getInstance() {
        return INSTANCE;
    }

    /**
     * Observable for Top Stories
     *
     * @return List of article
     */
    public Observable<Nearby> getLocationInfo(String location, String type, int radius) {
        PlacesCall callInfo = mRetrofit.create(PlacesCall.class);
        return callInfo.getLocationInfo(location, type, radius)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())   //  Run call on another thread
                .observeOn(AndroidSchedulers.mainThread())  //  Observe on the Main thread
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<PlacesDetails> getRestaurantsDetails(String id) {
        PlacesCall callInfo = mRetrofit.create(PlacesCall.class);
        return callInfo.getRestaurantsDetails(id)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())   //  Run call on another thread
                .observeOn(AndroidSchedulers.mainThread())  //  Observe on the Main thread
                .timeout(10, TimeUnit.SECONDS);
    }

    public Observable<PlacePhoto> getRestaurantPhoto(String reference) {
        PlacesCall callInfo = mRetrofit.create(PlacesCall.class);
        return callInfo.getRestaurantPhoto(reference)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())   //  Run call on another thread
                .observeOn(AndroidSchedulers.mainThread())  //  Observe on the Main thread
                .timeout(10, TimeUnit.SECONDS);
    }
}
