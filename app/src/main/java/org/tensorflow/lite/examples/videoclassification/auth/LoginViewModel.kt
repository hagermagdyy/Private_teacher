package org.tensorflow.lite.examples.videoclassification.auth

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import org.tensorflow.lite.examples.videoclassification.util.isPassword
import org.tensorflow.lite.examples.videoclassification.util.isPhoneNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    //  private val deviceToken by lazy { pref.getString(KeyString.DeviceToken) ?: "invalid token" }


    private val _navigation = Channel<Navigation>(Channel.RENDEZVOUS)
    val navigation: Flow<Navigation> get() = _navigation.receiveAsFlow()

    private var sharedPreference: SharedPreferences =
        application.getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

    // input data from UI
    val phone = MutableLiveData("")
    val password = MutableLiveData("")

    val loginState = MutableStateFlow(false)

    val enabledLoginBtn: LiveData<Boolean> =
        phone.asFlow().combine(password.asFlow()) { phone, password ->
            phone.isPhoneNumber() && password.isPassword()
        }.asLiveData()


    @SuppressLint("CommitPrefEdits")
    fun login(
        phone: String,
        password: String,
        nav: Navigation
    ) {
        if (phone == "01025054388" && password == "12345678") {
//            navigateTo(nav)
            viewModelScope.launch {
                loginState.value = true
                sharedPreference.edit().putBoolean("loggedIn", true)
            }
        }
    }


    private fun navigateTo(nav: Navigation) {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch {
            _navigation.send(nav)
        }
    }

    enum class Navigation {
        ChooseAvatar,
    }
}