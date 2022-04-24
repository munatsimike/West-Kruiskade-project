package nl.project.westkruiskade.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.project.westkruiskade.MainActivity
import nl.project.westkruiskade.model.BarcodeAnalyzer
import nl.project.westkruiskade.ui.fragments.PermissionFragment.Companion.allPermissionsGranted
import nl.project.westkruiskade.util.snackbarLong
import nl.project_.west_kruiskade.NavGraphDirections
import nl.project_.west_kruiskade.R
import nl.project_.west_kruiskade.databinding.ScannerFragmentBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

typealias BarcodeListener = (barcode: String) -> Unit

@AndroidEntryPoint
class ScanBarcodeFragment : BaseFragment<ScannerFragmentBinding>(ScannerFragmentBinding::inflate) {
    private var processingBarcode = AtomicBoolean(false)
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        binding.closeBtn.setOnClickListener(this)
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted(requireContext())) {
            startCamera()
        } else {
            findNavController().navigate(ScanBarcodeFragmentDirections.actionScanBarcodeFragmentToPermissionFragment2())

        }
    }

    override fun onResume() {
        super.onResume()
        processingBarcode.set(false)
    }

    override fun onPause() {
        super.onPause()
        cameraExecutor.shutdown()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            if (view.id == binding.closeBtn.id)
                cameraProvider.unbindAll()
            findNavController().navigate(ScanBarcodeFragmentDirections.actionScanBarcodeFragmentToHomeFragment3())
        }
    }

    private fun startCamera() {
        // which will be used to bind the use cases to a lifecycle owner.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(
                    binding.previewView.surfaceProvider
                )
            }
            // Setup the ImageAnalyzer for the ImageAnalysis use case
            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                        if (processingBarcode.compareAndSet(false, true)) {
                            searchBarcode(barcode)
                        }
                    })
                }

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                processingBarcode.set(false)
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e("PreviewUseCase", "Binding failed! :(", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun searchBarcode(barcode: String) {
        searchViewModel.searchBarcode(barcode).observe(viewLifecycleOwner, { exhibit ->
            if (exhibit == null) {
                requireView().snackbarLong(getString(R.string.Invalid_QR_code)).show()
                startCamera()
                return@observe
            }
            findNavController().navigate(
                NavGraphDirections.actionGlobalArtDetailPageFragment(
                    exhibit
                )
            )
        })
    }
}


