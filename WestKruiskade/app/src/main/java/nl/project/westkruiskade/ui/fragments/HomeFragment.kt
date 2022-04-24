package nl.project.westkruiskade.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import nl.project.westkruiskade.data.api.Resource
import nl.project.westkruiskade.util.Coroutines
import nl.project.westkruiskade.util.handleApiError
import nl.project.westkruiskade.util.linearLayoutManager
import nl.project.westkruiskade.util.showLongToast
import nl.project_.west_kruiskade.R
import nl.project_.west_kruiskade.databinding.HomeLayoutBinding

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeLayoutBinding>(HomeLayoutBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshListener(binding.recycleView.swipeRefreshLayout)
        displayRecyclerItems()
        handleApiCallError()
        binding.artImageView.setOnClickListener(this)
    }

    //on home page image click navigate to art landing page
    override fun onClick(view: View?) {
        when (view?.id) {
            binding.artImageView.id -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragment3ToArtLandingPageFragment())
            }
        }
    }

    private fun displayRecyclerItems() {
        initRecycleView(
            binding.recycleView.rvExhibits,
            requireContext().linearLayoutManager(LinearLayoutManager.HORIZONTAL)
        )
        // get exhibits from local database after a successful api call
        artViewModel.fetchExhibits.observe(viewLifecycleOwner, {
            if (it)
                displayExhibits(artViewModel.fetchDataFromLocalDB())
        })
    }

    private fun handleApiCallError() {
        artViewModel.apiErrorResponse.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Failure -> {
                    //Attempt to fetch and display data from local db
                    displayExhibits(artViewModel.fetchDataFromLocalDB())
                    // handle error and retry api call
                    handleApiError(it) { artViewModel.updateLocalDatabase() }
                }
                else -> {}
            }
        })
    }
}