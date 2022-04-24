package nl.project.westkruiskade.util

import android.app.Activity
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import nl.project.westkruiskade.data.api.Resource
import nl.project.westkruiskade.ui.fragments.HomeFragment
import nl.project_.west_kruiskade.R
import java.time.Duration

fun Activity.openFragment(fragmentId: Int) {
    val pendingIntent = NavDeepLinkBuilder(this.applicationContext)
        .setGraph(R.navigation.nav_graph)
        .setDestination(fragmentId)
        .createPendingIntent()
    pendingIntent.send()
}

fun Context.showLongToast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.alertDialogueBox(
    title: String,
    msg: String,
    negativeBtn: String = "",
    positiveBtn: String = getString(R.string.ok),
    positiveAction: (() -> Unit)? = null,
    negativeAction: (() -> Unit)? = null

) {
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setMessage(msg)
        .setPositiveButton(positiveBtn) { _, _ ->
            positiveAction?.invoke()
        }
        .setNegativeButton(negativeBtn) { dialog, _ ->
            dialog.dismiss()
            negativeAction?.invoke()
        }

    val alert = dialogBuilder.create()
    alert.setTitle(title)
    alert.show()
}


fun View.snackbarLong(message: String, position: Int = Gravity.TOP): Snackbar {
    val snackBarView = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    val view = snackBarView.view
    val params = view.layoutParams as FrameLayout.LayoutParams

    // set text size
    val snackbarTextView = view.findViewById<View>(R.id.snackbar_text) as TextView
    snackbarTextView.setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        resources.getDimension(R.dimen.size18sp)
    )

    // set display position
    params.gravity = position
    view.layoutParams = params

    // set background
    view.background = ContextCompat.getDrawable(
        context,
        R.drawable.blue_background_rectangle
    ) // for custom background
    snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
    return snackBarView
}

// show or hide view e.g progress bar
fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)

    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

// handle errors in wrapped in the resource class
fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null // try if operation fails
) {
    when {
        failure.isNetworkError -> requireView().snackbar(
            getString(R.string.no_internet_error_msg)
        ) {
            retry?.invoke()
        }
        else -> {
            requireView().snackbar(failure.errorBody?.string().toString())
        }
    }
}

fun getPopupWindow(view: View) = PopupWindow(
    view,
    LinearLayout.LayoutParams.WRAP_CONTENT,
    LinearLayout.LayoutParams.WRAP_CONTENT
)

fun Context.getInflater(layout: Int): View {
    val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return inflater.inflate(layout, null)
}

fun redirectToPreviousFragment(fragment: Fragment) {
    findNavController(fragment).popBackStack()
}

fun Context.linearLayoutManager(orientation: Int): LinearLayoutManager {
    return LinearLayoutManager(this, orientation, false)
}

fun staggeredGridLayoutManager(orientation: Int, spanCount: Int = 2): StaggeredGridLayoutManager {
    val staggeredGridLayoutManager =
        StaggeredGridLayoutManager(spanCount, orientation)
    staggeredGridLayoutManager.gapStrategy =
        StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
    return staggeredGridLayoutManager
}

fun ConstraintLayout.layoutTransition(duration: Long) {
    val transition = AutoTransition()
    transition.duration = duration
    TransitionManager.beginDelayedTransition(
        this, transition
    )
}

fun Fragment.removeFragment(fragment: Fragment) {
    parentFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
}

