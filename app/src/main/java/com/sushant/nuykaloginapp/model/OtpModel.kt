package com.sushant.nuykaloginapp.model

class OtpModel {
    private var otp: String = ""
    var digitOne: String? = null
    var digitTwo: String? = null
    var digitThree: String? = null
    var digitFour: String? = null
    var digitFive: String? = null
    var digitSix: String? = null

    public fun setOtp(otp: String) {
        if (otp.isNotEmpty()) {
            this.otp = otp.replace("[^0-9]", "")
            digitOne = if (otp.isNotEmpty()) otp[0].toString() else null
            digitTwo = if (otp.length > 1) otp[1].toString() else null
            digitThree = if (otp.length > 2) otp[2].toString() else null
            digitFour = if (otp.length > 3) otp[3].toString() else null
            digitFive = if (otp.length > 4) otp[4].toString() else null
            digitSix = if (otp.length > 5) otp[5].toString() else null
        }
    }

    fun getOtp(): String {
        val builder = StringBuilder()
        builder.append(digitOne ?: "")
        builder.append(digitTwo ?: "")
        builder.append(digitThree ?: "")
        builder.append(digitFour ?: "")
        builder.append(digitFive ?: "")
        builder.append(digitSix ?: "")
        return builder.toString()
    }

    fun clare(){
        otp=""
        digitOne=""
        digitTwo=""
        digitThree=""
        digitFour=""
        digitFive=""
        digitSix=""
    }
}