package org.tensorflow.lite.examples.videoclassification.chooseAvatar

import androidx.recyclerview.widget.DiffUtil

data class AvatarModel(
    val id : Int,
    val resourceId : Int,
    val name: String,
    val isSelected : Boolean
){
    companion object DIFFUtil : DiffUtil.ItemCallback<AvatarModel>() {
        override fun areContentsTheSame(
            oldItem: AvatarModel,
            newItem: AvatarModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: AvatarModel,
            newItem: AvatarModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
