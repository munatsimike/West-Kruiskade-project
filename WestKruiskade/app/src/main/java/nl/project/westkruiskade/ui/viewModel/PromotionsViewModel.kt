package nl.project.westkruiskade.ui.viewModel

import dagger.hilt.android.lifecycle.HiltViewModel
import nl.project.westkruiskade.data.repository.PromotionsRepository
import javax.inject.Inject

@HiltViewModel
class PromotionsViewModel @Inject constructor(
    private var promotions: PromotionsRepository
) : BaseViewModel() {
    fun promotions() = getItemsFromLocalDB({ promotions.fetchPromotions() })
}
