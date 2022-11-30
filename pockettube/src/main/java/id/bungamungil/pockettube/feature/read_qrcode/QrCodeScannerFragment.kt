package id.bungamungil.pockettube.feature.read_qrcode

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.common.Barcode
import id.bungamungil.pockettube.databinding.FragmentQrCodeScannerBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrCodeScannerFragment : Fragment(), ScannerRunnable.OnScannerRunnableError,
    ReadQrCodeAnalyzer.OnQrCodeAnalyzed {

    companion object {
        const val SCAN_QR_REQUEST = "SCAN_QR_REQUEST"
        const val SCAN_QR_RESULT = "SCAN_QR_RESULT"
    }

    private var _binding: FragmentQrCodeScannerBinding? = null

    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            performCamera()
        } else {
            showRequestPermissionAlertDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        checkCameraPermission(view.context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    private fun checkCameraPermission(context: Context) {
        when {
            isCameraPermissionGranted(context) -> {
                performCamera()
            }
            shouldShowRequestPermissionForCamera() -> {
                showRequestPermissionAlertDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun isCameraPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRequestPermissionForCamera(): Boolean {
        return shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
    }

    private fun performCamera() {
        val ctx = context ?: return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener(ScannerRunnable(
            cameraProviderFuture.get(),
            binding.previewView,
            cameraExecutor,
            ReadQrCodeAnalyzer(this),
            CameraSelector.DEFAULT_BACK_CAMERA,
            this,
            this
        ), ContextCompat.getMainExecutor(ctx))
    }

    private fun showRequestPermissionAlertDialog() {
        val ctx = context ?: return
        MaterialAlertDialogBuilder(ctx)
            .setTitle("Permission required")
            .setMessage("This application needs to access the camera to process barcodes")
            .setPositiveButton("OK") { _, _ ->
                // Keep asking for permission until granted
                checkCameraPermission(ctx)
            }
            .setCancelable(false)
            .create()
            .apply {
                setCanceledOnTouchOutside(false)
                show()
            }
    }

    override fun onQrCodeAnalyzed(qrCodes: List<Barcode>) {
        val qrCodeValue = qrCodes.lastOrNull()?.rawValue ?: return
        val bundle = Bundle().apply {
            putString(SCAN_QR_RESULT, qrCodeValue)
        }
        setFragmentResult(SCAN_QR_REQUEST, bundle)
        findNavController().navigateUp()
    }

    override fun onScannerRunnableError(reason: Throwable) {
        // scanner / camera error
    }

}