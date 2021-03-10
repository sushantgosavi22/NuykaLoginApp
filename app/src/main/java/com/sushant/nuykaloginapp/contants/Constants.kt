package com.sushant.nuykaloginapp.contants

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber


object Constants {
    const val SPLASH_TIME : Long = 2000
    const val NUMBER_OF_MIN : Long = 3
    const val THOUSAND : Long = 1000
    const val MINUTE_VALUE : Long = 60
    const val VALID_PHONE_NUMBER : Int = 10
    const val VALID_OTP : Int = 6
    const val EMPTY_STRING : String = ""
    const val PHONE_NUMBER : String = "phoneNumber"

    fun showToast(context: Context?, message: String?) {
        context?.let {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    @Throws(Exception::class)
    fun parseContact(contact: String?, countryCode: String)  : String? {
        var phoneNumber: PhoneNumber? = null
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        var finalNumber: String? = null
        val isoCode = phoneNumberUtil.getRegionCodeForCountryCode(countryCode.toInt())
        var isValid = false
        var isMobile: PhoneNumberType? = null
        try {
            phoneNumber = phoneNumberUtil.parse(contact, isoCode)
            isValid = phoneNumberUtil.isValidNumber(phoneNumber)
            isMobile = phoneNumberUtil.getNumberType(phoneNumber)
        } catch (e: NumberParseException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        if (isValid && (PhoneNumberType.MOBILE == isMobile || PhoneNumberType.FIXED_LINE_OR_MOBILE == isMobile)) {
            finalNumber = phoneNumberUtil.format(
                phoneNumber,
                PhoneNumberFormat.E164
            ).substring(1)
        }
        return finalNumber
    }

    fun String.appendPlus() : String{
        return if (this.startsWith("+")) this else "+".plus(this)
    }
}