package com.sushant.nuykaloginapp.views.login.viewmodel

import android.app.Application
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.sushant.nuykaloginapp.contants.Constants.VALID_PHONE_NUMBER
import com.sushant.nuykaloginapp.contants.Constants.appendPlus
import com.sushant.nuykaloginapp.views.base.BaseViewModel
import java.util.*

class LoginViewModel(application: Application) : BaseViewModel(application) {

    fun validateNumber(number: String) = (number.isEmpty() || number.length < VALID_PHONE_NUMBER)

    fun getDefaultCountryCode(): String = PhoneNumberUtil.getInstance().getCountryCodeForRegion(Locale.getDefault().country).toString().appendPlus()
}