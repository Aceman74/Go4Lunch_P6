package com.aceman.go4lunch.login;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aceman.go4lunch.R;
import com.aceman.go4lunch.base.BaseActivity;

import butterknife.BindView;
import okhttp3.Cache;

public class MainActivity extends BaseActivity implements LoginContract.LoginViewInterface {

    private static final int RC_SIGN_IN = 111;
    public static Cache mCache;
    // 1 - Get Coordinator Layout
    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.main_activity_button_login)
    Button mBtnLogin;
    @BindView(R.id.main_background)
    ImageView mBackground;
    private LoginPresenter mPresenter;
    Context mContext;

    @Override
    protected void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new LoginPresenter(this);
        mPresenter.configureCache();
        mPresenter.askPermission(mContext,this);
        mPresenter.alreadyLogged(this);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCurrentUserLogged()) {
                    mPresenter.startCoreActivity(mContext);
                } else {
                    mPresenter.startSignInActivity(mContext, RC_SIGN_IN, MainActivity.this);
                }
                /**
                 * << OR >>
                mPresenter.onClickLoginButton(RC_SIGN_IN,MainActivity.this);
                 */
            }
        });
    }

    @Override
    public int getFragmentLayout() {    //  Base activity
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.updateUIWhenResuming(mContext,mBtnLogin);

    }

}


