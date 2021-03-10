package com.sushant.nuykaloginapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.sushant.nuykaloginapp.contants.Constants

class SmsReceiver() : BroadcastReceiver() {
    private var mOnSmsReceivedListener : OnSmsReceivedListener? =null
    fun setOnSmsReceivedListener( mOnSmsReceivedListener : OnSmsReceivedListener){
        this.mOnSmsReceivedListener =mOnSmsReceivedListener
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status?
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message  = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String?
                    message?.let {
                        mOnSmsReceivedListener?.onReceiveOtp(getOtp(message))
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    mOnSmsReceivedListener?.onTimeout()
                }
            }
        }
    }

    fun getOtp(sms: String): String {
        var isDigitFound = false
        val stringArr = sms.split("").toTypedArray()
        val builder = StringBuilder()
        for (string in stringArr) {
            if (string.matches(Regex("[0-9]+"))) {
                builder.append(string)
                isDigitFound = true
            } else {
                if (isDigitFound) {
                    return builder.toString()
                }
            }
        }
        return ""
    }

    public interface OnSmsReceivedListener{
        fun onReceiveOtp(otp : String)
        fun onTimeout()
    }
}