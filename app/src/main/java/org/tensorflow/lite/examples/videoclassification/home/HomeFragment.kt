package org.tensorflow.lite.examples.videoclassification.home

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.tensorflow.lite.examples.videoclassification.extensions.back
import org.tensorflow.lite.examples.videoclassification.extensions.navigate
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.databinding.HomeViewBinding


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var sharedPreference: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = HomeViewBinding
            .inflate(inflater, container, false)

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        val image = sharedPreference.getInt("userImage", 0)

        binding.woman.setImageResource(image)

        val mp: MediaPlayer = MediaPlayer.create(requireContext(), R.raw.intro)
        mp.start()

        binding.toolbar.setNavigationOnClickListener {
            mp.stop()
            back()
        }

        binding.addInfo.setOnClickListener {
            mp.stop()
            navigate(HomeFragmentDirections.homeFragmentToAddInfoView())
        }

        return binding.root
    }

}