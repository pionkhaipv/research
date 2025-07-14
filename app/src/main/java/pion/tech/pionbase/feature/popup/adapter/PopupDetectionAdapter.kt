package pion.tech.pionbase.feature.popup.adapter

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pion.tech.pionbase.R
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

                // Load app icon
                loadAppIcon(detection.appPackage)

                root.setOnClickListener {
                    onItemClickListener?.invoke(detection)
                }

                buttonDelete.setOnClickListener {
                    onDeleteClickListener?.invoke(detection)
                }
            }
        }

        private fun loadAppIcon(packageName: String) {
            try {
                val packageManager = binding.root.context.packageManager
                val appIcon = packageManager.getApplicationIcon(packageName)
                binding.imageAppIcon.setImageDrawable(appIcon)

                // Remove background when showing actual app icon
                binding.imageAppIcon.background = null
                binding.imageAppIcon.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            } catch (e: PackageManager.NameNotFoundException) {
                // App not found, show default warning icon
                binding.imageAppIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context, R.drawable.ic_warning),
                )
                // Restore background for warning icon
                binding.imageAppIcon.background =
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.circle_background,
                    )
                binding.imageAppIcon.scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
            } catch (e: Exception) {
                // Any other error, show default warning icon
                binding.imageAppIcon.setImageDrawable(
                    ContextCompat.getDrawable(binding.root.context, R.drawable.ic_warning),
                )
                binding.imageAppIcon.background =
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.circle_background,
                    )
                binding.imageAppIcon.scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
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
