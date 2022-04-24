package nl.project.westkruiskade.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import nl.project_.west_kruiskade.databinding.ItemLoadingStateBinding

class LoaderStateAdapter(
    private val adapter: Adapter,
) :
    LoadStateAdapter<LoaderStateAdapter.LoaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        LoaderViewHolder(
            ItemLoadingStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) { adapter.retry() }

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) =
        holder.bindState(loadState)

    class LoaderViewHolder(
        private val binding: ItemLoadingStateBinding,
        private val retryCallback: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener { retryCallback() }
        }

        fun bindState(loadState: LoadState) {
            with(binding) {
                if (loadState is LoadState.Error) {
                    tvErrorMessage.text = loadState.error.localizedMessage
                }
                progressBar.isVisible = loadState is LoadState.Loading
                tvErrorMessage.isVisible = loadState !is LoadState.Loading
                btnRetry.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}