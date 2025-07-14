package pion.tech.pionbase.feature.popup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pion.tech.pionbase.app.data.model.PopupDetectionEntity
import pion.tech.pionbase.databinding.ItemPopupDetectionBinding
import java.text.SimpleDateFormat
import java.util.*

class PopupDetectionAdapter : ListAdapter<PopupDetectionEntity, PopupDetectionAdapter.ViewHolder>(DiffCallback()) {
    private var onItemClickListener: ((PopupDetectionEntity) -> Unit)? = null
    private var onDeleteClickListener: ((PopupDetectionEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (PopupDetectionEntity) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (PopupDetectionEntity) -> Unit) {
        onDeleteClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemPopupDetectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPopupDetectionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        fun bind(detection: PopupDetectionEntity) {
            binding.apply {
                textAppName.text = detection.appName
                textPackageName.text = detection.appPackage
                textPopupType.text = detection.popupType
                textDetectionTime.text = dateFormat.format(Date(detection.detectionTime))

                root.setOnClickListener {
                    onItemClickListener?.invoke(detection)
                }

                buttonDelete.setOnClickListener {
                    onDeleteClickListener?.invoke(detection)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PopupDetectionEntity>() {
        override fun areItemsTheSame(
            oldItem: PopupDetectionEntity,
            newItem: PopupDetectionEntity,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: PopupDetectionEntity,
            newItem: PopupDetectionEntity,
        ): Boolean = oldItem == newItem
    }
}
