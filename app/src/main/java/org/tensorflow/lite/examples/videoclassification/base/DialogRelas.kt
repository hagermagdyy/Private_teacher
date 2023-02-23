package org.tensorflow.lite.examples.videoclassification.base

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import org.tensorflow.lite.examples.videoclassification.ErrorMessage
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.Status
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.flow.Flow

abstract class DialogRelas: DialogFragment() {

    private var errorMessage: ErrorMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val width = (this.resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    protected inline fun <T> Flow<T>.collectWhenStarted(
        crossinline collector: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            this@collectWhenStarted.collect {
                collector(it)
            }
        }
    }

    protected fun showErrorMessage(error: Status.Error) {
        errorMessage?.showError(getMessage(error) ?: return)
    }

    protected fun showErrorMessage(message: String) {
        errorMessage?.showError(message)
    }

    protected fun showSuccessMessage(message: String) {
        errorMessage?.showSuccessMessage(message)
    }

    protected fun showSuccessMessage(message: Int) {
        errorMessage?.showSuccessMessage(message)
    }

    private fun getMessage(error: Status.Error): String {
        return when (error) {
            is Status.Error.Message -> error.message ?: getString(R.string.error)
            is Status.Error.Exceptions -> errorMessage.toString()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        errorMessage = context as? ErrorMessage
    }

    override fun onDetach() {
        super.onDetach()
        errorMessage = null
    }
}