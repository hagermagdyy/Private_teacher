package org.tensorflow.lite.examples.videoclassification.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import org.tensorflow.lite.examples.videoclassification.ErrorMessage

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.databinding.LoginViewBinding
import org.tensorflow.lite.examples.videoclassification.extensions.navigate

@AndroidEntryPoint
class LoginView : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var errorMessage: ErrorMessage? = null
    private lateinit var sharedPreference: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LoginViewBinding
            .inflate(inflater, container, false)
        sharedPreference =
            requireContext().getSharedPreferences("Parcelable", Context.MODE_PRIVATE)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@LoginView.viewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectNavigation()
        collectLoginState()
    }

    private fun collectNavigation() {
        lifecycleScope.launchWhenStarted {
            viewModel.navigation.collectLatest { n ->
                when (n) {
                    //  LoginViewModel.Navigation.ChooseAvatar -> navToRegister()
                    //LoginViewModel.Navigation.ForgetPassword -> navToForgetPassword()
                }
            }
        }
    }

    private fun collectLoginState() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collectLatest {
                if (it) {
                    sharedPreference.edit().putBoolean("loggedIn", true).apply()

                    errorMessage?.showSuccessMessage("Login Successfully")
                    navigate(LoginViewDirections.actionLoginViewToChooseAvatarView())
                }
            }
        }
    }

//    private fun navToRegister() {
//        navigate(LoginViewDirections.actionLoginViewToChooseAvatar())
//    }

}