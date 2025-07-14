package pion.tech.pionbase.feature.runningapps.presentation

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pion.tech.pionbase.app.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentRunningAppsBinding
import pion.tech.pionbase.feature.runningapps.presentation.adapter.RunningAppsAdapter

@AndroidEntryPoint
class RunningAppsFragment :
    BaseFragment<FragmentRunningAppsBinding, RunningAppsViewModel, CommonViewModel>(
        FragmentRunningAppsBinding::inflate,
        RunningAppsViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    private val adapter = RunningAppsAdapter()

    override fun init(view: View) {
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClickListeners()
    }

    override fun subscribeObserver(view: View) {
        // Collect running apps list
        lifecycleScope.launch {
            viewModel.runningApps.collect { apps ->
                adapter.submitList(apps)
                updateAppCount(apps.size)

                // Show/hide empty state
                binding.tvEmptyState.visibility = if (apps.isEmpty()) View.VISIBLE else View.GONE
                binding.rvRunningApps.visibility = if (apps.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        // Collect loading state
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                showHideLoading(isLoading)
                binding.swipeRefresh.isRefreshing = isLoading
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding.rvRunningApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RunningAppsFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshApps()
        }
    }

    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            viewModel.refreshApps()
        }
    }

    private fun updateAppCount(count: Int) {
        binding.tvAppCount.text = "Total: $count apps running"
    }
}
