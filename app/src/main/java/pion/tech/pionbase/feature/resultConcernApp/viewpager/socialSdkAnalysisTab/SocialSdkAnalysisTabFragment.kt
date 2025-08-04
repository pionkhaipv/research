package pion.tech.pionbase.feature.resultConcernApp.viewpager.socialSdkAnalysisTab

import android.view.View
import com.piontech.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import pion.tech.pionbase.app.CommonViewModel
import pion.tech.pionbase.databinding.FragmentSocialSdkAnalysisTabBinding

@AndroidEntryPoint
class SocialSdkAnalysisTabFragment :
    BaseFragment<FragmentSocialSdkAnalysisTabBinding, SocialSdkAnalysisTabViewModel, CommonViewModel>(
        FragmentSocialSdkAnalysisTabBinding::inflate,
        SocialSdkAnalysisTabViewModel::class.java,
        CommonViewModel::class.java,
    ) {
    override fun init(view: View) {
        initView()
    }

    override fun subscribeObserver(view: View) {
        // Empty for now - will be implemented later
    }
}
