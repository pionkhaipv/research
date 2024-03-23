package pion.tech.pionbase.framework.presentation.home

import android.util.Log
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.framework.presentation.common.BaseFragment
import pion.tech.pionbase.util.PrefUtil
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject
    lateinit var prefUtil: PrefUtil
    private lateinit var viewModel :HomeViewModel



    override fun init(view: View) {
        viewModel = HomeViewModel(prefUtil , this)
        changeEvent()
    }

    override fun subscribeObserver(view: View) {
        viewModel.observerPremium()
    }




    private fun changeEvent() {
        binding.btnChange.setOnClickListener {
            viewModel.prefUtil.IS_PREMIUM = !viewModel.prefUtil.IS_PREMIUM
            viewModel.isPremium.postValue(viewModel.prefUtil.IS_PREMIUM)
        }
    }
}