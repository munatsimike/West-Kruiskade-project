package nl.project.westkruiskade.ui.diffUtil

import androidx.recyclerview.widget.DiffUtil
import nl.project.westkruiskade.model.Exhibit

class MyDiffUtil(
): DiffUtil.ItemCallback<Exhibit> (){
    override fun areItemsTheSame(oldItem: Exhibit, newItem: Exhibit): Boolean {
      return  oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Exhibit, newItem: Exhibit): Boolean {
        return  oldItem.artistName == newItem.artistName
    }
}

