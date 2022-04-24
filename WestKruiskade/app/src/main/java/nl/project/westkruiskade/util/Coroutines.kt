package nl.project.westkruiskade.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object Coroutines{
    // use main thread
    fun main (job: suspend (()->Unit)) {
        CoroutineScope(Dispatchers.Main).launch {
            job()
        }
    }

    // use io thread
    fun io (job: suspend (()->Unit)) {
        CoroutineScope(Dispatchers.IO).launch {
            job()
        }
    }

    fun Fragment.myViewLifeCycleOwner (job: suspend () -> Unit){
        viewLifecycleOwner.lifecycleScope.launch {
           job()
        }
    }

}