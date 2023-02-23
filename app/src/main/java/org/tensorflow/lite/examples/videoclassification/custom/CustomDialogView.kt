package org.tensorflow.lite.examples.videoclassification.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import org.tensorflow.lite.examples.videoclassification.base.DialogRelas
import org.tensorflow.lite.examples.videoclassification.databinding.CustomDialogViewBinding

class CustomDialogView: DialogRelas() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = CustomDialogViewBinding
            .inflate(inflater, container, false)

        val args = this.args

        binding.image.isVisible = args.data.icon
            ?.let { binding.image.setImageResource(it); true }
            ?: false

        binding.dialogHeader.isVisible = args.data.header
            ?.let { binding.dialogHeader.text = it; true}
            ?: false

        binding.negativeBtn.isVisible = args.data.negativeText
            ?.let { binding.negativeBtn.text = it; true }
            ?: false

        binding.dialogDesc.text = args.data.message
        binding.positiveBtn.text = args.data.positiveText

        binding.positiveBtn.setOnClickListener {
            dismiss()
            args.onPositive()
        }

        binding.negativeBtn.setOnClickListener {
            dismiss()
            args.onNegative()
        }


        return binding.root
    }

    companion object {
        fun show(manager: FragmentManager, args: CustomDialogArgs){
            CustomDialogView()
                .apply { this.setArgs(args) }
                .show(manager, "aladdin_dialog")
        }
    }
}
