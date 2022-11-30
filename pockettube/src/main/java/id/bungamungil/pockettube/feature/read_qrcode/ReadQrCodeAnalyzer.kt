package id.bungamungil.pockettube.feature.read_qrcode

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class ReadQrCodeAnalyzer(private val onQrCodeAnalyzed: OnQrCodeAnalyzed) :
    ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            val scanner = BarcodeScanning.getClient(options)
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    onQrCodeAnalyzed.onQrCodeAnalyzed(barcodes)
                }
                .addOnFailureListener { }
        }
        image.close()
    }

    interface OnQrCodeAnalyzed {
        fun onQrCodeAnalyzed(qrCodes: List<Barcode>)
    }

}