/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aceman.go4lunch.utils;

/**
 * Created by Lionel JOFFRAY - on 12/06/2019.
 * <p>
 * Class with a BaseView for presenter.
 */
public abstract class BasePresenter {

    BaseView mView = null;

    public void attachView(BaseView v) {
        mView = v;
    }

    public BaseView getView() {
        return mView;
    }

}
