package org.tensorflow.lite.examples.videoclassification.lessons

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import org.tensorflow.lite.examples.videoclassification.extensions.back
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.videoclassification.databinding.LessonDetailsViewBinding


@AndroidEntryPoint
class LessonDetailsView : Fragment() {

    private lateinit var sharedPreference: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LessonDetailsViewBinding.inflate(inflater, container, false)

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        binding.toolbar.setNavigationOnClickListener { back() }

        binding.title = sharedPreference.getString("title", "")

        // binding.videoView.setVideoURI(Uri.parse(sharedPreference.getString("VideoUrl", "")));

        val mediaController = MediaController(requireContext())

        mediaController.setAnchorView(binding.videoView)
        mediaController.setMediaPlayer(binding.videoView)

        binding.videoView.setMediaController(mediaController)
        //  binding.videoView.start()

        val rawId = when (sharedPreference.getInt("lesson", 1)) {
            1 -> resources.getIdentifier("running", "raw", requireContext().packageName)
            2 -> resources.getIdentifier("chest_ball", "raw", requireContext().packageName)
            3 -> resources.getIdentifier("balance", "raw", requireContext().packageName)
            4 -> resources.getIdentifier("dribble", "raw", requireContext().packageName)
            5 -> resources.getIdentifier("roll", "raw", requireContext().packageName)
            else -> resources.getIdentifier("lesson1", "raw", requireContext().packageName)
        }

        val path = "android.resource://" + requireContext().packageName.toString() + "/" + rawId

        binding.videoView.setVideoURI(Uri.parse(path));

        binding.videoView.start()

        binding.takeVideo.setOnClickListener {
            binding.videoView.stopPlayback()
            val myIntent = Intent(requireContext(), EvaluationActivity::class.java)
            requireContext().startActivity(myIntent)
        }
//        binding.videoView.start()

        return binding.root
    }
}