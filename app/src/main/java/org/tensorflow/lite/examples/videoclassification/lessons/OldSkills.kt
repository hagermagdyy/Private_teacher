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
import org.tensorflow.lite.examples.videoclassification.extensions.back
import org.tensorflow.lite.examples.videoclassification.extensions.navigate
import org.tensorflow.lite.examples.videoclassification.lessons.adapter.LessonsAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.videoclassification.databinding.LessonsViewBinding

@AndroidEntryPoint
class OldSkills : Fragment(), LessonClickListener {

    private lateinit var sharedPreference: SharedPreferences
    private val adapter by lazy { LessonsAdapter(this) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LessonsViewBinding.inflate(inflater, container, false)

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        binding.toolbar.title = "المهارات السابقة"
        binding.toolbar.setNavigationOnClickListener { back() }

        binding.apply {
            adapter = this@OldSkills.adapter

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
                    colorString = "#E1D4F8",
                    textColor = "#9B68F6"
                ),
                LessonsModel(
                    lessonId = 2,
                    lessonName = "التمرين الثاني",
                    lessonUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1",
                    colorString = "#E1D4F8",
                    textColor = "#9B68F6"

                ),
                LessonsModel(
                    lessonId = 3,
                    lessonName = "التمرين الثالث",
                    lessonUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1",
                    colorString = "#E1D4F8",
                    textColor = "#9B68F6"

                ),
                LessonsModel(
                    lessonId = 4,
                    lessonName = "التمرين الرابع",
                    lessonUrl = "https://www.youtube.com/watch?v=lqH005ntlyY",
                    colorString = "#E1D4F8",
                    textColor = "#9B68F6"

                ),
                LessonsModel(
                    lessonId = 5,
                    lessonName = "التمرين الخامس",
                    lessonUrl = "https://www.youtube.com/watch?v=lqH005ntlyY",
                    colorString = "#E1D4F8",
                    textColor = "#9B68F6"
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
        sharedPreference.edit().putInt("lesson", item.lessonId).apply()
        view.setBackgroundColor(Color.parseColor("#4CAF50"))
        navigate(destination = R.id.lessonDetailsView)
    }
}