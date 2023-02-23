package org.tensorflow.lite.examples.videoclassification.chooseAvatar

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import org.tensorflow.lite.examples.videoclassification.chooseAvatar.adapter.AvatarAdapter
import org.tensorflow.lite.examples.videoclassification.extensions.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.databinding.ChooseAvatarViewBinding

@AndroidEntryPoint
class ChooseAvatarView : Fragment(), OnAvatarClickListener {

    private val viewModel: ChooseAvatarViewModel by viewModels()
    private val adapter by lazy { AvatarAdapter(this) }
    private lateinit var sharedPreference: SharedPreferences
    private var image : Int = 0
    lateinit var mp:MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ChooseAvatarViewBinding.inflate(inflater, container, false)


        mp = MediaPlayer.create(requireContext(), R.raw.choose_your_character)
        mp.start()

        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ChooseAvatarView.viewModel
            adapter = this@ChooseAvatarView.adapter
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNavigation()

        adapter.submitList(
            listOf(
                AvatarModel(
                    id = 1,
                    resourceId = R.drawable.boy,
                    name = "woman",
                    isSelected = false
                ),
                AvatarModel(
                    id = 1,
                    resourceId = R.drawable.girl,
                    name = "man",
                    isSelected = false
                ),
                AvatarModel(
                    id = 1,
                    resourceId = R.drawable.boy_black,
                    name = "womanPonytail",
                    isSelected = false
                ),
                AvatarModel(
                    id = 1,
                    resourceId = R.drawable.girl_black,
                    name = "manBeard",
                    isSelected = false
                ),
                AvatarModel(
                    id = 1,
                    resourceId = R.drawable.boy_glasses,
                    name = "womanHair",
                    isSelected = false
                ),
                AvatarModel(
                    id = 1,
                    resourceId = R.drawable.girl_ponytail,
                    name = "manMask",
                    isSelected = false
                ),
            )
        )
    }

    private fun collectNavigation() {
        lifecycleScope.launchWhenStarted {
            viewModel.navigation.collectLatest { n ->
                when (n) {
                    ChooseAvatarViewModel.Navigation.Next -> navToRegister()
                    //LoginViewModel.Navigation.ForgetPassword -> navToForgetPassword()
                }
            }
        }
    }

    private fun navToRegister() {
        mp.stop()
        navigate(ChooseAvatarViewDirections.actionChooseAvatarView2ToHomeFragment())
    }

    override fun onImaeClick(position : Int , selected: Boolean, view: ConstraintLayout, item: AvatarModel) {
        if (selected) {
            view.setBackgroundResource(R.drawable.bg_capsule_unselected)
            adapter.setSelection(false)
        } else {
            view.setBackgroundResource(R.drawable.bg_capsule_selected)
            adapter.setSelection(true)
            sharedPreference.edit().putInt("userImage", item.resourceId).apply()
        }
    }

}