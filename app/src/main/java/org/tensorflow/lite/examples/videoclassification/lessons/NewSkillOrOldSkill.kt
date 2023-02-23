package org.tensorflow.lite.examples.videoclassification.lessons

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.databinding.NewOrOldSkillBinding
import org.tensorflow.lite.examples.videoclassification.extensions.back
import org.tensorflow.lite.examples.videoclassification.extensions.navigate

class NewSkillOrOldSkill : Fragment() {

    private lateinit var sharedPreference: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = NewOrOldSkillBinding
            .inflate(inflater, container, false)

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)


        val mp: MediaPlayer = MediaPlayer.create(requireContext(), R.raw.newskill)
        mp.start()

        binding.toolbar.setNavigationOnClickListener {
            mp.stop()
            back()
        }

        binding.buttonNewSkill.setOnClickListener {

            mp.stop()

            navigate(NewSkillOrOldSkillDirections.newSkillOrOldSkillToNewSkill())
        }

        binding.buttonOldSkill.setOnClickListener {

            mp.stop()

            navigate(NewSkillOrOldSkillDirections.newSkillOrOldSkillToOldSkills())
        }


        return binding.root
    }
}