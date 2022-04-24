package nl.project.westkruiskade.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import nl.project.westkruiskade.ui.adapters.Adapter
import nl.project.westkruiskade.util.layoutTransition
import nl.project.westkruiskade.util.staggeredGridLayoutManager
import nl.project_.west_kruiskade.databinding.ArtLandingPageLayoutBinding

@AndroidEntryPoint
class ArtLandingPageFragment :
    BaseFragment<ArtLandingPageLayoutBinding>(ArtLandingPageLayoutBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.artGalleryToggleBtn.setOnClickListener(this)
        val viewPagerAdapter = Adapter(this, binding.viewPager2)
        swipeRefreshListener(binding.recycleView.swipeRefreshLayout)
        initViewPager(viewPagerAdapter)
        // display viewpager exhibits
        displayExhibits(promotionsViewModel.promotions(), viewPagerAdapter)
        displayRecyclerViewItems()
    }

    private fun displayRecyclerViewItems() {
        initRecycleView(
            binding.recycleView.rvExhibits,
            staggeredGridLayoutManager(StaggeredGridLayoutManager.VERTICAL)
        )
        displayExhibits(artViewModel.fetchDataFromLocalDB())
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.artGalleryToggleBtn.id -> {
                if (getGuideLineHeight() == 0F) {
                    binding.artGalleryGuideLine.setGuidelinePercent(0.55F)
                } else {
                    binding.artGalleryGuideLine.setGuidelinePercent(0F)
                }
                binding.rvContainer.layoutTransition(500)
            }
        }
    }

    private fun getGuideLineHeight(): Float {
        return (binding.artGalleryGuideLine.layoutParams as ConstraintLayout.LayoutParams).guidePercent
    }
}