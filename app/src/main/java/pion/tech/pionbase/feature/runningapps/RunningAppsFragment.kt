package pion.tech.pionbase.feature.runningapps

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.piontech.core.base.BaseFragment
import com.piontech.core.utils.collectFlowOnView
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentRunningAppsBinding
import pion.tech.pionbase.feature.runningapps.adapter.RunningAppsAdapter
import pion.tech.pionbase.util.displayToast
import pion.tech.pionbase.util.handleUiState

@AndroidEntryPoint
class RunningAppsFragment :
    BaseFragment<FragmentRunningAppsBinding, RunningAppsViewModel, CommonViewModel>(
        FragmentRunningAppsBinding::inflate,
        RunningAppsViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    lateinit var adapter: RunningAppsAdapter

    override fun init(view: View) {
        logger.logScreen("running_apps_show")
        initView()
        setupToolbar()
        setupSwipeRefresh()
        setupClickListeners()
        onBackEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.runningAppsUiState.collectFlowOnView(viewLifecycleOwner) {
            it.handleUiState(
                onLoading = {
                    showHideLoading(true)
                    binding.swipeRefresh.isRefreshing = true
                    binding.rvRunningApps.isVisible = false
                    binding.tvEmptyState.isVisible = false
                },
                onSuccess = { apps ->
                    showHideLoading(false)
                    binding.swipeRefresh.isRefreshing = false

                    if (apps.isEmpty()) {
                        binding.rvRunningApps.isVisible = false
                        binding.tvEmptyState.isVisible = true
                        binding.tvEmptyState.text = "No running apps found"
                    } else {
                        binding.rvRunningApps.isVisible = true
                        binding.tvEmptyState.isVisible = false
                        adapter.submitList(apps)
                    }

                    updateAppCount(apps.size)
                },
                onError = {
                    showHideLoading(false)
                    binding.swipeRefresh.isRefreshing = false
                    binding.rvRunningApps.isVisible = false
                    binding.tvEmptyState.isVisible = true
                    binding.tvEmptyState.text = "Failed to load running apps"
                    displayToast("Failed to load running apps")
                },
            )
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
