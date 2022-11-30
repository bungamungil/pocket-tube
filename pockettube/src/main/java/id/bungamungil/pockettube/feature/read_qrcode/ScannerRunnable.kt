package id.bungamungil.pockettube.feature.read_qrcode

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService

class ScannerRunnable(
    private val cameraProvider: ProcessCameraProvider,
    private val previewView: PreviewView,
    private val executor: ExecutorService,
    private val analyzer: Analyzer,
    private val selectedCamera: CameraSelector,
    private val lifecycleOwner: LifecycleOwner,
    private val onScannerRunnableError: OnScannerRunnableError
) : Runnable {

    override fun run() {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { it.setAnalyzer(executor, analyzer) }
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, selectedCamera, preview, imageAnalyzer)
        } catch (reason: Throwable) {
            onScannerRunnableError.onScannerRunnableError(reason)
        }
    }

    interface OnScannerRunnableError {
        fun onScannerRunnableError(reason: Throwable)
    }

}