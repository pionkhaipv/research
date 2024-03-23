package pion.tech.pionbase.framework.presentation.home

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.framework.presentation.common.BaseFragment


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {


    val viewModel : HomeViewModel by viewModels()



    override fun init(view: View) {
        viewModel.isPremium.postValue(viewModel.prefUtil.IS_PREMIUM)
        changeEvent()
    }

    override fun subscribeObserver(view: View) {
        observerPremium()
    }

    private fun changeEvent() {
        binding.btnChange.setOnClickListener {
            viewModel.prefUtil.IS_PREMIUM = !viewModel.prefUtil.IS_PREMIUM
            viewModel.isPremium.postValue(viewModel.prefUtil.IS_PREMIUM)
        }
    }

    private fun observerPremium(){
        viewModel.isPremium.observe(viewLifecycleOwner){
            binding.viewModel = viewModel
        }
    }
}