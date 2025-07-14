package pion.tech.pionbase.feature.popup

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pion.tech.pionbase.app.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentPopupStatisticsBinding
import pion.tech.pionbase.feature.popup.adapter.PopupDetectionAdapter
import timber.log.Timber

@AndroidEntryPoint
class PopupStatisticsFragment :
    BaseFragment<FragmentPopupStatisticsBinding, PopupStatisticsViewModel, CommonViewModel>(
        FragmentPopupStatisticsBinding::inflate,
        PopupStatisticsViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    private val popupAdapter = PopupDetectionAdapter()

    override fun init(view: View) {
        setupRecyclerView()
        observeViewModel()
    }

    override fun subscribeObserver(view: View) {
        // Observer sẽ được setup trong observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewDetections.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = popupAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Observe detections và submit vào adapter
            viewModel.detections.collect { detections ->
                popupAdapter.submitList(detections)
                Timber.d("Updated popup detections list: ${detections.size} items")
            }
        }

        lifecycleScope.launch {
            // Observe total count
            viewModel.totalCount.collect { total ->
                binding.textTotalDetections.text = "Tổng popup phát hiện: $total"
            }
        }

        lifecycleScope.launch {
            // Observe today detections
            viewModel.todayDetections.collect { today ->
                binding.textTodayDetections.text = "Hôm nay: $today"
            }
        }

        lifecycleScope.launch {
            // Observe top app
            viewModel.topAppWithMostPopups.collect { topApp ->
                if (topApp != null) {
                    binding.textTopApp.text = "App nhiều popup nhất: ${topApp.first} (${topApp.second})"
                } else {
                    binding.textTopApp.text = "Chưa có dữ liệu popup"
                }
            }
        }

        lifecycleScope.launch {
            // Observe loading state
            viewModel.isLoading.collect { isLoading ->
                // Có thể thêm progress bar ở đây nếu cần
                Timber.d("Loading state: $isLoading")
            }
        }

        lifecycleScope.launch {
            // Observe error messages
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    // Hiển thị error message (có thể dùng Snackbar)
                    Timber.e("Error: $it")
                    // viewModel.clearError() // Clear error sau khi hiển thị
                }
            }
        }
    }
}
