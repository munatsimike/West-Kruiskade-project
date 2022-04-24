package nl.project.westkruiskade.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import nl.project.westkruiskade.model.Exhibit

abstract class BaseViewModel(
) : ViewModel() {

    protected fun getItemsFromLocalDB(
        exhibits: () -> PagingSource<Int, Exhibit>,
        pageSize: Int = PAGE_SIZE,
        maxSize: Int = MAX_SIZE
    ): Flow<PagingData<Exhibit>> = Pager(config = PagingConfig(
        pageSize = pageSize,
        enablePlaceholders = false,
        maxSize = maxSize
    ),
        pagingSourceFactory = { exhibits.invoke() }).flow.cachedIn(viewModelScope)

    companion object{
        const val PAGE_SIZE = 15
        const val MAX_SIZE = 45
    }
}