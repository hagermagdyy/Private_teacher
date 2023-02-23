package org.tensorflow.lite.examples.videoclassification

import androidx.annotation.StringRes

interface ErrorMessage {
    fun showError(message: String)
    fun showError(@StringRes message: Int)
    fun showSuccessMessage(message: String)
    fun showSuccessMessage(@StringRes message: Int)
}