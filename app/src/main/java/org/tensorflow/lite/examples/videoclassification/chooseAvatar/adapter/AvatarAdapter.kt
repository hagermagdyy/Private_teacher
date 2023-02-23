package org.tensorflow.lite.examples.videoclassification.chooseAvatar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.chooseAvatar.AvatarModel
import org.tensorflow.lite.examples.videoclassification.chooseAvatar.OnAvatarClickListener
import org.tensorflow.lite.examples.videoclassification.databinding.AvatarItemRawBinding


class AvatarAdapter(private val clickListener: OnAvatarClickListener) :

    ListAdapter<AvatarModel, AvatarAdapter.AllProjectsViewHolder>(AvatarModel.DIFFUtil) {

     var selected: Boolean = false

    override fun onBindViewHolder(holder: AllProjectsViewHolder, position: Int) {
        if(selected)
            holder.selectedBg()
        else
            holder.defaultBg()

        holder.bind(position, getItem(position) ?: return)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllProjectsViewHolder {
        return AllProjectsViewHolder.create(parent, clickListener, selected)
    }

    class AllProjectsViewHolder(
        private val binding: AvatarItemRawBinding,
        private val clickListener: OnAvatarClickListener,
        private var selected: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            @JvmStatic
            fun create(
                parent: ViewGroup,
                clickListener: OnAvatarClickListener,
                selected: Boolean

            ): AllProjectsViewHolder {
                return AllProjectsViewHolder(
                    binding = AvatarItemRawBinding.inflate(
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
            binding.view.setBackgroundResource(R.drawable.bg_capsule_unselected)
        }

        fun selectedBg() {
            binding.view.setBackgroundResource(R.drawable.bg_capsule_selected)
        }


        fun bind(position: Int, item: AvatarModel) {
            binding.item = item
            binding.woman.setImageResource(item.resourceId)
            binding.constraintView = binding.view
            binding.selected = selected
            binding.position = position
            binding.view.setBackgroundResource(R.drawable.bg_capsule_unselected)
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }
    }

    fun setSelection(selectedValue: Boolean){
        selected = selectedValue
    }


}

