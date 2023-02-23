package org.tensorflow.lite.examples.videoclassification.lessons

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.extensions.navigate

import org.tensorflow.lite.examples.videoclassification.lessons.adapter.LessonsAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.videoclassification.databinding.LessonsViewBinding

@AndroidEntryPoint
class LessonsView : Fragment(), LessonClickListener {

    private lateinit var sharedPreference: SharedPreferences
    private val adapter by lazy { LessonsAdapter(this) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LessonsViewBinding.inflate(inflater, container, false)

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        binding.apply {
            adapter = this@LessonsView.adapter

        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

           }


    override fun onLessonClick(
        item: LessonsModel,
        position: Int,
        selected: Boolean,
        view: ConstraintLayout
    ) {
        sharedPreference.edit().putString("VideoUrl", item.lessonUrl).apply()
        sharedPreference.edit().putString("title", item.lessonName).apply()
        view.setBackgroundColor(Color.parseColor("#4CAF50"))
        navigate(destination = R.id.lessonDetailsView)
    }
}