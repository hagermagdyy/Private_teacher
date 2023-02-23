package org.tensorflow.lite.examples.videoclassification.lessons

import androidx.recyclerview.widget.DiffUtil

data class LessonsModel(
    val lessonId : Int,
    val lessonName : String,
    val lessonUrl: String,
    val colorString: String,
    val textColor : String
){
    companion object DIFFUtil : DiffUtil.ItemCallback<LessonsModel>() {
        override fun areContentsTheSame(
            oldItem: LessonsModel,
            newItem: LessonsModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: LessonsModel,
            newItem: LessonsModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
