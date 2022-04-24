package nl.project.westkruiskade.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import nl.project.westkruiskade.util.removeFragment
import nl.project_.west_kruiskade.R

class PermissionFragment : Fragment(R.layout.permissions_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        removeFragment(this)
        if (allPermissionsGranted(requireContext())) {
            navigateToBarCodeScanner(findNavController())
        } else {
            askCameraPermission()
        }
    }

    private fun askCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CODE_PERMISSIONS = 10

        fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                context, it
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun navigateToBarCodeScanner(nav: NavController) {
            nav.navigate(PermissionFragmentDirections.actionPermissionFragment2ToScanBarcodeFragment())
        }
    }
}