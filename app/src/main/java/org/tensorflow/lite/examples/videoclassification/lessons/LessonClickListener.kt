package org.tensorflow.lite.examples.videoclassification.lessons

import androidx.constraintlayout.widget.ConstraintLayout

interface LessonClickListener {
    fun onLessonClick(item: LessonsModel, position:Int, selected:Boolean, view: ConstraintLayout)
}