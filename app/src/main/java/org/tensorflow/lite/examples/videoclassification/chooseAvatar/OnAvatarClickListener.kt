package org.tensorflow.lite.examples.videoclassification.chooseAvatar

import androidx.constraintlayout.widget.ConstraintLayout

interface OnAvatarClickListener {
    fun onImaeClick(position : Int , selected:Boolean, view: ConstraintLayout, item:AvatarModel)
}