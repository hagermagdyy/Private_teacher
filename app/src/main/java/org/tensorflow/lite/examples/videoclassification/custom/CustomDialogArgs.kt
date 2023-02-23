package org.tensorflow.lite.examples.videoclassification.custom

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CustomDialogArgs(val data: Data) : Parcelable {

    @Parcelize
    data class Data(
        @DrawableRes val icon: Int? = null,
        val header: String? = null,
        val message: String,
        val positiveText: String,
        val negativeText: String? = null
    ) : Parcelable

    open fun onPositive() = Unit
    open fun onNegative() = Unit
}

private const val ARGS_KEY = "CustomDialogView:args"

internal fun CustomDialogView.setArgs(args: CustomDialogArgs){
    arguments = Bundle()
        .apply { putParcelable(ARGS_KEY, args) }
}

val CustomDialogView.args: CustomDialogArgs
    get() = requireArguments().getParcelable(ARGS_KEY)!!


