package org.tensorflow.lite.examples.videoclassification.util

import android.util.Patterns


/**
 * Check is that phone number or not
 * phone must be start with 01 and number after that length must be 9
 */
fun String.isPhoneNumber(): Boolean{
    val regex = Regex("^0[1][0125]\\d{8}\$")
    return regex.matches(this)
}

fun String.isName(): Boolean{
    return this.length > 2
}

fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check password length is smaller than 6
 */
fun String.isPassword(): Boolean{
    return this.length >= 8
}