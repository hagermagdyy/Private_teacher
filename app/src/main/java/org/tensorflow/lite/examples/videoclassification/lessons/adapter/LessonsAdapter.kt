package org.tensorflow.lite.examples.videoclassification.lessons.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.tensorflow.lite.examples.videoclassification.databinding.LessonItemViewBinding
import org.tensorflow.lite.examples.videoclassification.lessons.LessonClickListener
import org.tensorflow.lite.examples.videoclassification.lessons.LessonsModel


class LessonsAdapter (private val clickListener: LessonClickListener) :

    ListAdapter<LessonsModel, LessonsAdapter.LessonsViewHolder>(LessonsModel.DIFFUtil) {

    var selected: Boolean = false

    override fun onBindViewHolder(holder: LessonsViewHolder, position: Int) {
        if(selected)
            holder.selectedBg()
        else
            holder.defaultBg()

        holder.bind(position, getItem(position) ?: return)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LessonsViewHolder {
        return LessonsViewHolder.create(parent, clickListener, selected)
    }

    class LessonsViewHolder(
        private val binding: LessonItemViewBinding,
        private val clickListener: LessonClickListener,
        private var selected: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            @JvmStatic
            fun create(
                parent: ViewGroup,
                clickListener: LessonClickListener,
                selected: Boolean

            ): LessonsViewHolder {
                return LessonsViewHolder(
                    binding = LessonItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    clickListener = clickListener,
                    selected = selected
                )
            }
        }


        fun defaultBg() {
            binding.view.setBackgroundColor(Color.parseColor("#E1D4F8"))
        }

        fun selectedBg() {
            binding.view.setBackgroundColor(Color.parseColor("#4CAF50"))
        }


        fun bind(position: Int, item: LessonsModel) {
            binding.item = item
            binding.constraintView = binding.view
            binding.selected = selected
            binding.position = position
            binding.text.setTextColor(Color.parseColor(item.textColor))
            binding.view.setBackgroundColor(Color.parseColor(item.colorString))
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }
    }

    fun setSelection(selectedValue: Boolean){
        selected = selectedValue
    }

}