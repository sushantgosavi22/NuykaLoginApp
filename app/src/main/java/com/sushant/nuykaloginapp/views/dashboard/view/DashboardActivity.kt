package com.sushant.nuykaloginapp.views.dashboard.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.sushant.nuykaloginapp.R
import com.sushant.nuykaloginapp.databinding.ActivityDashboardBinding
import com.sushant.nuykaloginapp.views.base.BaseActivity
import com.sushant.nuykaloginapp.views.login.view.LoginActivity

class DashboardActivity : BaseActivity() {

    lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}