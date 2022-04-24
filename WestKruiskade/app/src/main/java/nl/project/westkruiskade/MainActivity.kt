package nl.project.westkruiskade

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import nl.project.westkruiskade.ui.fragments.ArtLandingPageFragment
import nl.project.westkruiskade.ui.fragments.HomeFragment
import nl.project.westkruiskade.ui.fragments.PermissionFragment.Companion.REQUEST_CODE_PERMISSIONS
import nl.project.westkruiskade.ui.fragments.PermissionFragment.Companion.allPermissionsGranted
import nl.project.westkruiskade.ui.fragments.PermissionFragment.Companion.navigateToBarCodeScanner
import nl.project.westkruiskade.ui.fragments.ScanBarcodeFragment
import nl.project.westkruiskade.ui.fragments.ScanBarcodeFragmentDirections
import nl.project.westkruiskade.util.alertDialogueBox
import nl.project.westkruiskade.util.openFragment
import nl.project_.west_kruiskade.R
import nl.project_.west_kruiskade.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_navigation_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavComponents()
        hideShowActionBarBottomNav()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun initNavComponents() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        setSupportActionBar(binding.toolBar)
        setupActionBarWithNavController(navController)
    }

    // handle user permission request responses
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted(this)) {
            navigateToBarCodeScanner(navController)
        } else {
            alertDialogueBox(
                getString(R.string.permission_denied_title),
                getString(R.string.camera_permission_denied_msg),
                getString(R.string.deny_permission_btn),
                getString(R.string.grant_permission_btn)
            )

            // action for negative btn & action for positive btn
            { openFragment(R.id.homeFragment3) }
        }
    }

    fun isSearchRvVisible(): Boolean {
        return binding.searchRecyclerView.isVisible
    }

    fun getSearchRecycleView(): RecyclerView {
        return binding.searchRecyclerView;
    }

    fun isProgressBarVisible(): Boolean {
        return binding.progressBar.isVisible
    }

    fun hideShowProgressBar(boolean: Boolean) {
        binding.progressBar.isVisible = boolean

    }

    // display number of items found
    @SuppressLint("SetTextI18n")
    fun searchResultHeaderTxt(foundItemCount: Int, searchQuery: String) {
        if (foundItemCount == 1) {
            binding.searchResultsTxt.text = getString(R.string.found_result_singular)
        } else {
            binding.searchResultsTxt.text = getString(R.string.found_result_plural)
        }
        binding.tvSearchResultQuantity.text = "$foundItemCount"
        binding.tvSearchQuery.text = "\"$searchQuery\""
    }

    // set search recyclerview visibility
    fun searchResultViewVisibility(visibility: Boolean) {
        binding.searchRecyclerView.isVisible = visibility
        binding.headerContainer.isVisible = visibility
    }

    override fun onBackPressed() {
        val fragmentInstance = navHostFragment.childFragmentManager.primaryNavigationFragment
        if (fragmentInstance is ScanBarcodeFragment) {
            navController.navigate(ScanBarcodeFragmentDirections.actionScanBarcodeFragmentToHomeFragment3())
        } else {
            supportActionBar?.show()
            binding.bottomNavigationView.isVisible = true
            supportFragmentManager.popBackStack()
        }
    }


    private fun hideShowActionBarBottomNav() {
        val destinations = listOf(
            R.id.detailPageFragment,
            R.id.contactFormFragment2,
            R.id.scanBarcodeFragment,
            R.id.permissionFragment2
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destinations.contains(destination.id)) {
                supportActionBar?.hide()
                binding.bottomNavigationView.isVisible = false
            } else {
                supportActionBar?.show()
                binding.bottomNavigationView.isVisible = true
            }
        }
    }
}