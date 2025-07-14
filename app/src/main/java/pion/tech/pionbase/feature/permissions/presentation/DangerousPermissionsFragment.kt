package pion.tech.pionbase.feature.permissions.presentation

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pion.tech.pionbase.app.presentation.CommonViewModel
import pion.tech.pionbase.databinding.FragmentDangerousPermissionsBinding
import pion.tech.pionbase.feature.permissions.presentation.adapter.PermissionsAdapter
import pion.tech.pionbase.feature.permissions.presentation.model.DangerLevel

@AndroidEntryPoint
class DangerousPermissionsFragment :
    BaseFragment<FragmentDangerousPermissionsBinding, DangerousPermissionsViewModel, CommonViewModel>(
        FragmentDangerousPermissionsBinding::inflate,
        DangerousPermissionsViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    private val adapter = PermissionsAdapter()

    override fun init(view: View) {
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClickListeners()
    }

    override fun subscribeObserver(view: View) {
        // Collect permissions list
        lifecycleScope.launch {
            viewModel.permissions.collect { permissions ->
                adapter.submitList(permissions)
                updateStats(permissions)

                // Show/hide empty state
                binding.tvEmptyState.visibility = if (permissions.isEmpty()) View.VISIBLE else View.GONE
                binding.rvPermissions.visibility = if (permissions.isEmpty()) View.GONE else View.VISIBLE
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
        binding.rvPermissions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DangerousPermissionsFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshPermissions()
        }
    }

    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            viewModel.refreshPermissions()
        }
    }

    private fun updateStats(permissions: List<pion.tech.pionbase.feature.permissions.presentation.model.PermissionUIModel>) {
        val totalPermissions = permissions.size
        val criticalCount = permissions.count { it.dangerLevel == DangerLevel.CRITICAL }

        binding.tvTotalPermissions.text = totalPermissions.toString()
        binding.tvCriticalCount.text = criticalCount.toString()
    }
}
