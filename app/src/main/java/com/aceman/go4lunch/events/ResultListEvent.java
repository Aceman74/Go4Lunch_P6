package com.aceman.go4lunch.events;

import com.aceman.go4lunch.data.nearby_search.Result;

import java.util.List;

/**
 * Created by Lionel JOFFRAY - on 17/05/2019.
 */
public class ResultListEvent {

    public List<Result> mResults;

    public ResultListEvent(List<Result> results) {
        mResults = results;
    }
}
