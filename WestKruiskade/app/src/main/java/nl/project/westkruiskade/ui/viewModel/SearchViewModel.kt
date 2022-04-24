package nl.project.westkruiskade.ui.viewModel

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.project.westkruiskade.data.repository.SearchResultRepository
import nl.project.westkruiskade.model.Exhibit
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private var searchRepository: SearchResultRepository
) : BaseViewModel() {

    fun searchQuery(searchQuery: String) =
        getItemsFromLocalDB({ searchRepository.fetchSearchResultsFromLocalDB(searchQuery) })

    fun searchBarcode(barcode: String): LiveData<Exhibit> =
        searchRepository.fetchExhibitById(barcode)
}
