package pion.tech.pionbase.framework.presentation.home

import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.databinding.FragmentHomeBinding
import pion.tech.pionbase.framework.database.entities.DummyEntity
import pion.tech.pionbase.framework.presentation.common.BaseFragment

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate,
    HomeViewModel::class.java
) {
    var dummyEntity: DummyEntity? = null
    override fun init(view: View) {
        initView()
    }

    override fun subscribeObserver(view: View) {
    }

}
