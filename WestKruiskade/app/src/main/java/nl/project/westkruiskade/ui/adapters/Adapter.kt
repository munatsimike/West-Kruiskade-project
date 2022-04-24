package nl.project.westkruiskade.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import coil.load
import nl.project.westkruiskade.util.enums.ViewType
import nl.project.westkruiskade.model.Exhibit
import nl.project.westkruiskade.ui.diffUtil.MyDiffUtil
import nl.project.westkruiskade.util.MyClickListener
import nl.project_.west_kruiskade.R
import nl.project_.west_kruiskade.databinding.ListItemLayoutBinding
import nl.project_.west_kruiskade.databinding.SearchResultListItemBinding

class Adapter(
    private var myClickListener: MyClickListener,
    var viewGroup: ViewGroup
) : PagingDataAdapter<Exhibit, Adapter.ArtViewHolder>(MyDiffUtil()) {
    private lateinit var listItemBinding: ListItemLayoutBinding
    private lateinit var searchResultBinding: SearchResultListItemBinding
    private var _viewType: ViewType = ViewType.LIST_ITEM

    inner class ArtViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private fun clickListerBinding(exhibit: Exhibit, myClickListener: MyClickListener) {
            if (ViewType.LIST_ITEM.ordinal == _viewType.ordinal) {
                listItemBinding.artImage.setOnClickListener { myClickListener.onExhibitClick(exhibit) }
            } else {
                searchResultBinding.result.setOnClickListener {
                    myClickListener.onExhibitClick(
                        exhibit
                    )
                }
            }
        }

        fun bind(exhibit: Exhibit) {
            if (ViewType.LIST_ITEM.ordinal == _viewType.ordinal) {
                this.setIsRecyclable(false)
                exhibit.apply {
                    listItemBinding.artImage.load(images[0].imageUri)
                    listItemBinding.artistName.text = artistName
                }
            } else {
                with(searchResultBinding) {
                    ivImage.load(exhibit.images[0].imageUri)
                    searchResultsTxt.text = exhibit.artistName
                    tvLocation.text = exhibit.location
                }
            }
            clickListerBinding(exhibit, myClickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val layoutMatchParent = ViewGroup.MarginLayoutParams.MATCH_PARENT
        val layoutWrapContent = ViewGroup.MarginLayoutParams.WRAP_CONTENT

        if (ViewType.LIST_ITEM.ordinal == _viewType.ordinal) {
            listItemBinding =
                ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            // set staggered layout attributes
            if (viewGroup is RecyclerView) {
                val layout = viewGroup as RecyclerView
                if (layout.layoutManager is StaggeredGridLayoutManager) {
                    setParentViewHolderAttribute(layoutMatchParent, layoutWrapContent)
                    setChildElementAttribute()
                }
            }

            // set viewpager2 layout attributes
            if (viewGroup is ViewPager2)
                setParentViewHolderAttribute(layoutMatchParent, layoutMatchParent)
            return ArtViewHolder(listItemBinding)
        }

        // layout for displaying search results
        searchResultBinding =
            SearchResultListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtViewHolder(searchResultBinding)
    }

    override fun onBindViewHolder(holder: Adapter.ArtViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    // set recycler view data
    fun setRecycleViewData(viewType: ViewType) {
        _viewType = viewType
    }

    override fun getItemViewType(position: Int): Int {
        return _viewType.ordinal
    }

    // change constraint layout for staggered layout and view pager
    private fun setParentViewHolderAttribute(width: Int, length: Int) {
        listItemBinding.layout.layoutParams = ViewGroup.LayoutParams(
            width, length
        )
    }

    // change card view with and height
    private fun setChildElementAttribute() {
        val constraintSet = ConstraintSet()
        constraintSet.apply {
            clone(listItemBinding.layout)
            constrainWidth(R.id.cardView20, ConstraintLayout.LayoutParams.MATCH_PARENT)
            constrainHeight(R.id.cardView20, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            applyTo(listItemBinding.layout)
        }
    }
}