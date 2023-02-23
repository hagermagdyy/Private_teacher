package org.tensorflow.lite.examples.videoclassification

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.tensorflow.lite.examples.videoclassification.databinding.ProfileViewBinding

class ProfileView :Fragment() {

    private lateinit var sharedPreference: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ProfileViewBinding
            .inflate(inflater, container, false)

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        binding.userImage.setImageResource(sharedPreference.getInt("userImage", 0))

        binding.inputName.setText(sharedPreference.getString("userName", ""))
        binding.inputAge.setText(sharedPreference.getString("userAge", ""))
        binding.inputTeacher.setText(sharedPreference.getString("userTeacher", ""))
        binding.inputClass.setText(sharedPreference.getString("userClass", ""))

        return binding.root
    }
}