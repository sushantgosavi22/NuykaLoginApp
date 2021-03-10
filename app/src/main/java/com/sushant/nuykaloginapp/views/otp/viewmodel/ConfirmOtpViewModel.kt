package com.sushant.nuykaloginapp.views.login.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sushant.nuykaloginapp.R
import com.sushant.nuykaloginapp.contants.Constants.VALID_OTP
import com.sushant.nuykaloginapp.model.OtpModel
import com.sushant.nuykaloginapp.views.base.BaseViewModel
import java.util.concurrent.TimeUnit


class ConfirmOtpViewModel(application: Application) : BaseViewModel(application) {
    private var mForceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var verificationId: String? = null
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var otpModel: OtpModel = OtpModel()
    var mSaveUserCallBack = MutableLiveData<Resource<String>>()

    fun getSaveFeedCallBack(): MutableLiveData<Resource<String>> {
        return mSaveUserCallBack;
    }

    fun sendVerificationCode(number: String, phoneAuthOptions: PhoneAuthOptions.Builder) {
        mSaveUserCallBack.value = Resource.showLoading()
        mSaveUserCallBack.value = Resource.registerReceiver()
        val mPhoneAuthOptions = if (mForceResendingToken != null) {
            mForceResendingToken?.let {
                phoneAuthOptions.setForceResendingToken(mForceResendingToken)
            } ?: phoneAuthOptions
        } else {
            phoneAuthOptions
        }
        val options =
                mPhoneAuthOptions
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setCallbacks(mCallBack)
                        .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun getFirebaseAuth(): FirebaseAuth = mAuth

    fun validateCode(code: String): Boolean = (code.trim().isEmpty() || code.length < VALID_OTP)

    fun verifyCode(code: String) {
        val error = {
            mSaveUserCallBack.value =
                    Resource.error(Throwable(getApplication<Application>().getString(R.string.verification_code_empty)))
        }
        verificationId?.let {
            if (it.isNotEmpty()) {
                val credential = PhoneAuthProvider.getCredential(it, code)
                signInWithCredential(credential)
            } else {
                error.invoke()
            }
        } ?: error.invoke()
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mSaveUserCallBack.value = Resource.showLoading()
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    mSaveUserCallBack.value = Resource.hideLoading()
                    if (task.isSuccessful) {
                        mSaveUserCallBack.value = Resource.signIn()
                    } else {
                        mSaveUserCallBack.value =
                                Resource.error(Throwable(getApplication<Application>().getString(R.string.sign_in_fail)))
                    }
                }
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(s, forceResendingToken)
                    verificationId = s
                    mForceResendingToken = forceResendingToken
                    mSaveUserCallBack.value = Resource.hideLoading()
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    mSaveUserCallBack.value = Resource.hideLoading()
                    val code = phoneAuthCredential.smsCode
                    if (code != null) {
                        otpModel.setOtp(code)
                        mSaveUserCallBack.value = Resource.success(code)
                    } else {
                        mSaveUserCallBack.value =
                                Resource.error(Throwable(application.getString(R.string.sms_code_null)))
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    mSaveUserCallBack.value = Resource.hideLoading()
                    mSaveUserCallBack.value = Resource.error(e)
                }
            }

    data class Resource<out T>(val status: Status, val data: T?, val exception: Throwable?) {
        enum class Status {
            REGISTER_OPP_RECEIVER,
            SHOW_LOADING,
            HIDE_LOADING,
            SUCCESS,
            SIGN_IN,
            ERROR,
        }

        companion object {
            fun <T> success(data: T?): Resource<T> {
                return Resource(Status.SUCCESS, data, null)
            }

            fun <T> signIn(): Resource<T> {
                return Resource(Status.SIGN_IN, null, null)
            }

            fun <T> error(exception: Throwable): Resource<T> {
                return Resource(Status.ERROR, null, exception)
            }

            fun <T> showLoading(): Resource<T> {
                return Resource(Status.SHOW_LOADING, null, null)
            }

            fun <T> registerReceiver(): Resource<T> {
                return Resource(Status.REGISTER_OPP_RECEIVER, null, null)
            }

            fun <T> hideLoading(): Resource<T> {
                return Resource(Status.HIDE_LOADING, null, null)
            }
        }
    }
}