package nl.project.westkruiskade.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import nl.project.westkruiskade.MainActivity
import nl.project.westkruiskade.data.api.Resource
import nl.project.westkruiskade.model.Exhibit
import nl.project.westkruiskade.model.SliderTransformer
import nl.project.westkruiskade.ui.adapters.Adapter
import nl.project.westkruiskade.ui.adapters.LoaderStateAdapter
import nl.project.westkruiskade.ui.viewModel.ArtViewModel
import nl.project.westkruiskade.ui.viewModel.PromotionsViewModel
import nl.project.westkruiskade.ui.viewModel.SearchViewModel
import nl.project.westkruiskade.util.Coroutines.myViewLifeCycleOwner
import nl.project.westkruiskade.util.MyClickListener
import nl.project.westkruiskade.util.enums.ViewType
import nl.project.westkruiskade.util.handleApiError
import nl.project.westkruiskade.util.linearLayoutManager
import nl.project_.west_kruiskade.NavGraphDirections
import nl.project_.west_kruiskade.R

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (layoutInflater: LayoutInflater) -> VB
) : Fragment(), View.OnClickListener, MyClickListener {
    protected val artViewModel: ArtViewModel by viewModels()
    protected val searchViewModel: SearchViewModel by viewModels()
    val promotionsViewModel: PromotionsViewModel by viewModels()

    private lateinit var searchView: SearchView
    private lateinit var mainActivity: MainActivity
    protected lateinit var myAdapter: Adapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var _binding: VB? = null

    val binding: VB
        get() = _binding as VB

    override fun onPrepareOptionsMenu(menu: Menu) {
        val mSearchMenuItem = menu.findItem(R.id.search_result_fragment)
        searchView = mSearchMenuItem.actionView as SearchView
        searchView.setBackgroundColor(Color.TRANSPARENT)
        searchView.queryHint = getString(R.string.search_view_hint)

        // hide search recyclerview when search view is closed
        searchView.setOnQueryTextFocusChangeListener { _, b ->
            if (!b) {
                mainActivity.searchResultViewVisibility(false)
            }
        }

        setSearchViewListener()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = bindingInflater.invoke(inflater)
        mainActivity = requireActivity() as MainActivity
        if (_binding == null) {
            throw IllegalArgumentException("Binding cannot be null")
        }
        return binding.root
    }

    // on exhibit click navigate to detail page
    override fun onExhibitClick(exhibit: Exhibit) {
        findNavController().navigate(
            NavGraphDirections.actionGlobalArtDetailPageFragment(
                exhibit
            )
        )

        if (mainActivity.isSearchRvVisible()) {
            mainActivity.searchResultViewVisibility(false)
        }
    }

    protected fun displayExhibits(
        data: Flow<PagingData<Exhibit>>,
        adapter: Adapter = myAdapter
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.submitData(PagingData.empty())
            data.collectLatest {
                // delay(1000)
                adapter.submitData(it)
                cancel()
            }
        }
    }

    // initialize recycler view
    protected fun initRecycleView(
        recyclerView: RecyclerView,
        layout: RecyclerView.LayoutManager,
        viewType: ViewType = ViewType.LIST_ITEM
    ) {
        myAdapter = Adapter(this, recyclerView)
        myAdapter.setRecycleViewData(viewType)
        recyclerView.apply {
            layoutManager = layout
            adapter = myAdapter.withLoadStateFooter(
                footer = LoaderStateAdapter(myAdapter)
            )
        }
        initialLoadingSState()
    }

    fun swipeRefreshListener(swipeRefreshLayout: SwipeRefreshLayout) {
        viewLifecycleOwner.lifecycleScope.launch {
            swipeRefreshLayout.setOnRefreshListener {
                artViewModel.updateLocalDatabase()
                myAdapter.refresh()
                if (swipeRefreshLayout.isRefreshing) {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    protected fun initViewPager(_myAdapter: Adapter) {
        val viewPager = _myAdapter.viewGroup as ViewPager2
        viewPager.apply {
            adapter = _myAdapter.withLoadStateFooter(
                LoaderStateAdapter(_myAdapter)
            )
            offscreenPageLimit = 4
            setPageTransformer(SliderTransformer(offscreenPageLimit))
        }
        autoScrollViewPagerItems(viewPager)
    }

    private fun autoScrollViewPagerItems(viewPager2: ViewPager2) {
        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                for (i in 0..8) {
                    // transition delay time
                    delay(10000)
                    if (i == 0) {
                        viewPager2.setCurrentItem(i, false)
                    } else {
                        viewPager2.setCurrentItem(i, true)
                    }
                }
            }
        }
    }

    private fun setSearchViewListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(searchQuery: String): Boolean {
                if (searchQuery.trim().isNotEmpty() && searchQuery.trim()
                        .all { it.isLetterOrDigit() }
                ) {
                    if (!mainActivity.isSearchRvVisible()) {
                        mainActivity.searchResultViewVisibility(true)
                        initRecycleView(
                            mainActivity.getSearchRecycleView(),
                            requireContext().linearLayoutManager(LinearLayoutManager.VERTICAL),
                            ViewType.SEARCH_RESULT
                        )

                    }
                    searchResultHeader(searchQuery)
                    // display search results
                    displayExhibits(
                        searchViewModel.searchQuery(searchQuery),
                    )

                } else {
                    mainActivity.searchResultViewVisibility(false)
                }
                return true
            }
        })
    }

    fun searchResultHeader(searchQuery: String) {
        myAdapter.addLoadStateListener {
            mainActivity.searchResultHeaderTxt(myAdapter.itemCount, searchQuery)
        }
    }

    @SuppressLint("InflateParams")
    private fun initialLoadingSState() {
        viewLifecycleOwner.lifecycleScope.launch {
            myAdapter.loadStateFlow.collectLatest {
                if (it.prepend is LoadState.NotLoading && it.prepend.endOfPaginationReached) {
                    delay(1200)
                    mainActivity.hideShowProgressBar(false)
                }
            }
        }
    }
}
