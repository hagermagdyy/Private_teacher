package org.tensorflow.lite.examples.videoclassification.addInfo

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.tensorflow.lite.examples.videoclassification.extensions.back
import org.tensorflow.lite.examples.videoclassification.extensions.navigate
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.databinding.AddInfoViewBinding

@AndroidEntryPoint
class AddInfoView : Fragment() {

    private lateinit var sharedPreference: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AddInfoViewBinding
            .inflate(inflater, container, false)

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        binding.userImage.setImageResource(sharedPreference.getInt("userImage", 0))

        val mp: MediaPlayer = MediaPlayer.create(requireContext(), R.raw.fill_data)
        mp.start()

        binding.toolbar.setNavigationOnClickListener {
            mp.stop()
            back()
        }

        binding.saveData.setOnClickListener {



            mp.stop()

            sharedPreference.edit().putString("userName", binding.inputName.text.toString().trim()).apply()
            sharedPreference.edit().putString("userAge", binding.inputAge.text.toString().trim()).apply()
            sharedPreference.edit().putString("userTeacher", binding.inputTeacher.text.toString().trim()).apply()
            sharedPreference.edit().putString("userClass", binding.inputClass.text.toString().trim()).apply()
            sharedPreference.edit().putString("teacherNumber", binding.inputTeacherPhone.text.toString().trim()).apply()
            sharedPreference.edit().putString("teacherEmail", binding.inputTeacherEmail.text.toString().trim()).apply()

            findNavController().setGraph(R.navigation.app_graph)

//            if (binding.inputName.text.toString() == "" ||
//                binding.inputAge.text.toString() == "" ||
//                binding.inputTeacher.text.toString() == "" ||
//                binding.inputClass.text.toString() == "" ||
//                binding.inputTeacherPhone.text.toString() == "" ||
//                    binding.inputTeacherEmail.text.toString() == ""){
//
//                AlertDialog.Builder(context)
//                    .setTitle("البيانات غير مكتملة")
//                    .setMessage("الرجاء من التأكد بأن البيانات مكتملة") // Specifying a listener allows you to take an action before dismissing the dialog.
//                    // The dialog is automatically dismissed when a dialog button is clicked.
//                    .setPositiveButton(
//                        R.string.yes,
//                        DialogInterface.OnClickListener { dialog, which ->
//                            // Continue with delete operation
//                        }) // A null listener allows the button to dismiss the dialog and take no further action.
//                    .show()
//            }else {
//
//            }

        }

        return binding.root
    }


}