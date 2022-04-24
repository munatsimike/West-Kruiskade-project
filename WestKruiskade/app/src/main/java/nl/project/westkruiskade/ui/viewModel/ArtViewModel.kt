package nl.project.westkruiskade.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nl.project.westkruiskade.data.api.Resource
import nl.project.westkruiskade.data.repository.ExhibitRepositoryImp
import nl.project.westkruiskade.model.Exhibit
import nl.project.westkruiskade.util.Coroutines
import javax.inject.Inject

@HiltViewModel
class ArtViewModel @Inject constructor(
    private var exhibitRepository: ExhibitRepositoryImp
) : BaseViewModel() {

    private val _fetchExhibits: MutableLiveData<Boolean> = MutableLiveData()
    val fetchExhibits: LiveData<Boolean> get() = _fetchExhibits

    private val _apiErrorResponse: MutableLiveData<Resource<List<Exhibit>>> = MutableLiveData()
    val apiErrorResponse: LiveData<Resource<List<Exhibit>>> get() = _apiErrorResponse

    init {
        updateLocalDatabase()
    }

    private suspend fun fetchExhibitsFromApi(): LiveData<Resource<List<Exhibit>>> {
        val result = MutableLiveData<Resource<List<Exhibit>>>()
        result.value = exhibitRepository.fetchExhibitsFromApi()
        return result
    }

    private suspend fun updateLocalDB(exhibits: List<Exhibit>) {
        exhibitRepository.updateLocalDB(exhibits)
    }

     fun updateLocalDatabase() {
        viewModelScope.launch {
            fetchExhibitsFromApi().observeForever {
                when (it) {
                    is Resource.Success -> {
                        Coroutines.io {
                            updateLocalDB(it.value)
                        }
                        Coroutines.main {
                            _fetchExhibits.value = true
                        }
                    }
                    else -> {
                        _apiErrorResponse.value = it
                    }
                }
            }
        }
    }

    fun fetchDataFromLocalDB() = getItemsFromLocalDB({ exhibitRepository.fetchExhibitsFromLocalDB() })
}