package com.sushant.nuykaloginapp.views.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sushant.nuykaloginapp.R
import com.sushant.nuykaloginapp.contants.Constants.SPLASH_TIME
import com.sushant.nuykaloginapp.views.base.BaseActivity
import com.sushant.nuykaloginapp.views.login.view.LoginActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME)
    }
}