package com.aceman.go4lunch.utils;

import com.aceman.go4lunch.data.nearby_search.Result;

/**
 * Created by Lionel JOFFRAY - on 15/05/2019.
 */
public interface ProgressBarCallback {

    void onProgressCallback();
    void onFinishCallback();
}
