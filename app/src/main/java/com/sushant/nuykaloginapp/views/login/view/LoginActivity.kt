package com.sushant.nuykaloginapp.views.login.view

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.sushant.nuykaloginapp.R
import com.sushant.nuykaloginapp.contants.Constants
import com.sushant.nuykaloginapp.contants.Constants.appendPlus
import com.sushant.nuykaloginapp.databinding.ActivityLoginBinding
import com.sushant.nuykaloginapp.views.base.BaseActivity
import com.sushant.nuykaloginapp.views.dashboard.view.DashboardActivity
import com.sushant.nuykaloginapp.views.login.viewmodel.LoginViewModel
import com.sushant.nuykaloginapp.views.otp.view.ConfirmOtpActivity
import java.util.*


class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        setUpViews()
    }

    private fun setUpViews() {
        binding.btnRequestVerification.setOnClickListener { onRequestVerification() }
        binding.edtCountryCode.setText(loginViewModel.getDefaultCountryCode())
        binding.edtPhoneNumber.addTextChangedListener(PhoneNumberWatcher(binding.edtPhoneNumber))
    }

    private fun onRequestVerification(){
        val code: String = binding.edtCountryCode.text.toString().appendPlus()
        val number: String = binding.edtPhoneNumber.text.toString().trim()
        if (validate(number)) {
            startActivity(Intent(this, ConfirmOtpActivity::class.java).apply {
                this.putExtra(Constants.PHONE_NUMBER, code.plus(number))
            })
        }
    }
    private fun validate(number: String): Boolean {
        return if (loginViewModel.validateNumber(number)) {
            binding.edtPhoneNumber.error = getString(R.string.valid_no_required)
            binding.edtPhoneNumber.requestFocus()
            false
        } else {
            true
        }
    }


    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, DashboardActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    class PhoneNumberWatcher(private val et: EditText) : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            et.removeTextChangedListener(this)
            val text = PhoneNumberUtils.formatNumber(s.toString(),Locale.getDefault().country)
            text?.let {
                et.setText(text)
                et.setSelection(text.length)
            }
            et.addTextChangedListener(this)
        }
    }
}