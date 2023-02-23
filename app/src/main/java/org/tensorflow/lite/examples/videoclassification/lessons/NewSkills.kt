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
class NewSkills : Fragment(), LessonClickListener {

    private lateinit var sharedPreference: SharedPreferences
    private val adapter by lazy { LessonsAdapter(this) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LessonsViewBinding.inflate(inflater, container, false)

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        binding.toolbar.title = "المهارات الجديدة"


        binding.apply {
            adapter = this@NewSkills.adapter
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.submitList(
            listOf(
                LessonsModel(
                    lessonId = 1,
                    lessonName = "التمرين الأول",
                    lessonUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1",
                    colorString = "#A5DCD6",
                    textColor = "#009688"
                ),
                LessonsModel(
                    lessonId = 1,
                    lessonName = "التمرين الثاني",
                    lessonUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1",
                    colorString = "#A5DCD6",
                    textColor = "#009688"
                ),
                LessonsModel(
                    lessonId = 1,
                    lessonName = "التمرين الثالث",
                    lessonUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1",
                    colorString = "#A5DCD6",
                    textColor = "#009688"
                ),
                LessonsModel(
                    lessonId = 1,
                    lessonName = "التمرين الرابع",
                    lessonUrl = "https://www.youtube.com/watch?v=lqH005ntlyY",
                    colorString = "#A5DCD6",
                    textColor = "#009688"
                ),
                LessonsModel(
                    lessonId = 1,
                    lessonName = "التمرين الخامس",
                    lessonUrl = "https://www.youtube.com/watch?v=lqH005ntlyY",
                    colorString = "#A5DCD6",
                    textColor = "#009688"
                ),
            )
        )
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