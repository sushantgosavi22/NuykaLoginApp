package com.sushant.nuykaloginapp.views.otp.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.auth.PhoneAuthOptions
import com.sushant.nuykaloginapp.R
import com.sushant.nuykaloginapp.contants.Constants
import com.sushant.nuykaloginapp.contants.Constants.showToast
import com.sushant.nuykaloginapp.databinding.ActivityConfirmOtpBinding
import com.sushant.nuykaloginapp.receiver.SmsReceiver
import com.sushant.nuykaloginapp.views.base.BaseActivity
import com.sushant.nuykaloginapp.views.dashboard.view.DashboardActivity
import com.sushant.nuykaloginapp.views.login.view.LoginActivity
import com.sushant.nuykaloginapp.views.login.viewmodel.ConfirmOtpViewModel


class ConfirmOtpActivity : BaseActivity(), SmsReceiver.OnSmsReceivedListener {

    private lateinit var confirmOtpViewModel: ConfirmOtpViewModel
    lateinit var binding: ActivityConfirmOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_otp)
        confirmOtpViewModel = ViewModelProviders.of(this).get(ConfirmOtpViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = confirmOtpViewModel
        setUpViews()
    }

    private fun setUpViews() {
        binding.btnVerify.setOnClickListener { onVerify() }
        binding.ivTroubleshoot.setOnClickListener { showDialog() }
        binding.tvTimeCounter.setOnClickListener {
            startCounter()
            sendVerificationCode()
            binding.tvTimeCounter.isEnabled = false
        }
        confirmOtpViewModel.getSaveFeedCallBack().observe(this, Observer { consumeResponse(it) })
        sendVerificationCode()
    }

    private fun startCounter() {
        confirmOtpViewModel.otpModel.clare()
        binding.tvDetails.text = getString(R.string.new_code_sent)
        binding.tvDetails.setTextColor(Color.BLUE)
        confirmOtpViewModel.otpModel.apply {
            binding.edtOtp1.setText(Constants.EMPTY_STRING)
            binding.edtOtp2.setText(Constants.EMPTY_STRING)
            binding.edtOtp3.setText(Constants.EMPTY_STRING)
            binding.edtOtp4.setText(Constants.EMPTY_STRING)
            binding.edtOtp5.setText(Constants.EMPTY_STRING)
            binding.edtOtp6.setText(Constants.EMPTY_STRING)
        }
        val min = Constants.NUMBER_OF_MIN * Constants.MINUTE_VALUE * Constants.THOUSAND
        object : CountDownTimer(min, Constants.THOUSAND) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = ((millisUntilFinished / (Constants.THOUSAND * Constants.MINUTE_VALUE)) % Constants.MINUTE_VALUE)
                val mSec: Long = millisUntilFinished / Constants.THOUSAND % Constants.MINUTE_VALUE
                binding.tvTimeCounter.setText("".plus(minutes).plus(" : ").plus(mSec))
            }

            override fun onFinish() {
                binding.tvTimeCounter.text = getString(R.string.resend_code)
                binding.tvDetails.text = getString(R.string.one_last_step)
                binding.tvDetails.setTextColor(Color.BLACK)
                binding.tvTimeCounter.isEnabled = true
            }
        }.start()

    }

    private fun sendVerificationCode() {
        intent.getStringExtra(Constants.PHONE_NUMBER)?.let {
            val builder = PhoneAuthOptions
                    .newBuilder(confirmOtpViewModel.getFirebaseAuth())
                    .setActivity(this)
            confirmOtpViewModel.sendVerificationCode(it, builder)
        }
    }

    /**
     * method to handle response
     */
    private fun consumeResponse(response: ConfirmOtpViewModel.Resource<String>?) {
        when (response?.status) {
            ConfirmOtpViewModel.Resource.Status.SHOW_LOADING -> {
                showProgressBar()
            }
            ConfirmOtpViewModel.Resource.Status.HIDE_LOADING -> {
                hideProgressBar()
            }
            ConfirmOtpViewModel.Resource.Status.REGISTER_OPP_RECEIVER -> {
                registerReceiver()
            }
            ConfirmOtpViewModel.Resource.Status.SIGN_IN -> {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            ConfirmOtpViewModel.Resource.Status.SUCCESS -> {
                binding.tvDetails.text = getString(R.string.one_last_step)
                binding.tvDetails.setTextColor(Color.BLACK)
                onReceiveOtp(response.data.toString())
                confirmOtpViewModel.verifyCode(response.data.toString())
            }
            ConfirmOtpViewModel.Resource.Status.ERROR -> {
                binding.ivTroubleshoot.visibility = View.VISIBLE
                showToast(this, response.exception?.message)
                if (response.exception?.message.equals(getString(R.string.sign_in_fail))) {
                    binding.tvDetails.text = getString(R.string.invalid_code)
                    binding.tvDetails.setTextColor(Color.RED)
                }
            }
            else -> {
                hideProgressBar()
            }
        }
    }

    private fun onVerify() {
        val code: String = confirmOtpViewModel.otpModel.getOtp()
        if (confirmOtpViewModel.validateCode(code)) {
            binding.tvDetails.text = getString(R.string.enter_valid_otp_number)
            binding.tvDetails.setTextColor(Color.RED)
            binding.tvDetails.requestFocus()
            return
        } else {
            binding.tvDetails.text = getString(R.string.one_last_step)
            binding.tvDetails.setTextColor(Color.BLACK)
            confirmOtpViewModel.verifyCode(code)
        }
    }


    private fun registerReceiver() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            val filter = IntentFilter()
            filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            mSmsReceiver.setOnSmsReceivedListener(this)
            registerReceiver(mSmsReceiver, filter)
        }
        task.addOnFailureListener {
            consumeResponse(ConfirmOtpViewModel.Resource.error(it))
        }
    }

    private val mSmsReceiver: SmsReceiver by lazy { SmsReceiver() }

    override fun onReceiveOtp(otp: String) {
        confirmOtpViewModel.otpModel.setOtp(otp)
        confirmOtpViewModel.otpModel.apply {
            binding.edtOtp1.setText(digitOne.toString())
            binding.edtOtp2.setText(digitTwo.toString())
            binding.edtOtp3.setText(digitThree.toString())
            binding.edtOtp4.setText(digitFour.toString())
            binding.edtOtp5.setText(digitFive.toString())
            binding.edtOtp6.setText(digitSix.toString())
        }
    }

    /**
     * IF we want to show time out message.
     */
    override fun onTimeout() {
        //consumeResponse(ConfirmOtpViewModel.Resource.error(Throwable(getString(R.string.timeout_otp_detect))))
    }

    private fun showDialog() {
        val colors = arrayOf(getString(R.string.change_number), getString(R.string.support))
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(true)

        builder.setTitle(getString(R.string.trouble))
        builder.setItems(colors) { dialog, which ->
            if (which == 0) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                dialog.dismiss()
            }
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create().apply {
            setOnShowListener { getButton(Dialog.BUTTON_NEGATIVE)?.setTextColor(Color.WHITE) }
        }
        dialog.show()
    }

}