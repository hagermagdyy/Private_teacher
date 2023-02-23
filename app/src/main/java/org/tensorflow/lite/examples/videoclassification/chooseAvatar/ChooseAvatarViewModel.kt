package org.tensorflow.lite.examples.videoclassification.chooseAvatar

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageButton
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import org.tensorflow.lite.examples.videoclassification.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChooseAvatarViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _navigation = Channel<Navigation>(Channel.RENDEZVOUS)
    val navigation: Flow<Navigation> get() = _navigation.receiveAsFlow()

    private var sharedPreference: SharedPreferences =
        application.getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

    fun navigateTo(nav: Navigation) {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch {
            _navigation.send(nav)
        }
    }


    @SuppressLint("CommitPrefEdits")
    fun setPic(
        view: ImageButton,
        id: Int
    ) {
        val resource = when (id) {
            1 -> R.drawable.girl
            2 -> R.drawable.boy
            3 -> R.drawable.girl_ponytail
            4 -> R.drawable.boy_glasses
            5 -> R.drawable.girl_black
            6 -> R.drawable.boy_black
            else -> {
                R.drawable.boy
            }
        }

        ImageViewCompat.setImageTintList(
            view,
            ColorStateList.valueOf(Color.parseColor("#43673AB7"))
        )

        sharedPreference.edit().putInt("UserImage", resource)

    }

    fun next() {
        navigateTo(Navigation.Next)
    }

    enum class Navigation {
        Next,
    }
}