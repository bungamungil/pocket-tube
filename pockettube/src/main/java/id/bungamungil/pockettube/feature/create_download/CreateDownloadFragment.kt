package id.bungamungil.pockettube.feature.create_download

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yausername.youtubedl_android.BuildConfig
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.mapper.VideoInfo
import id.bungamungil.pockettube.MainView
import id.bungamungil.pockettube.R
import id.bungamungil.pockettube.databinding.FragmentCreateDownloadBinding
import id.bungamungil.pockettube.feature.read_qrcode.QrCodeScannerFragment
import id.bungamungil.pockettube.util.loadImageFromRemote
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


class CreateDownloadFragment : Fragment(), TextView.OnEditorActionListener {

    private var _binding: FragmentCreateDownloadBinding? = null

    private val binding get() = _binding!!

    private val mainView get() = activity as? MainView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(QrCodeScannerFragment.SCAN_QR_REQUEST) { _, bundle ->
            val result = bundle.getString(QrCodeScannerFragment.SCAN_QR_RESULT)!!
            binding.inputUrl.setText(result)
            inputUrlCompleted(result)
        }
        binding.inputUrl.setOnEditorActionListener(this)
        binding.buttonScanQr.setOnClickListener {
            findNavController().navigate(R.id.action_CreateDownload_to_ScanQr)
        }
    }

    override fun onPause() {
        super.onPause()
        mainView?.apply {
            hideDownloadButton()
        }
    }

    override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (textView == binding.inputUrl) {
            when (actionId) {
                EditorInfo.IME_ACTION_DONE,
                EditorInfo.IME_ACTION_NEXT,
                EditorInfo.IME_ACTION_PREVIOUS -> {
                    inputUrlCompleted(textView.text.toString())
                }
            }
        }
        return false
    }

    private fun inputUrlCompleted(url: String) {
        Single.fromCallable { YoutubeDL.getInstance().getInfo(url) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                binding.widgetProgressFetchVideoInfo.visibility = View.VISIBLE
                binding.widgetDownloadOptions.visibility = View.GONE
            }
            .subscribe(this::videoInfoRetrieved) { reason ->
                this.videoInfoFailedToRetrieve(reason, url)
            }
    }

    private fun videoInfoRetrieved(videoInfo: VideoInfo) {
        binding.widgetProgressFetchVideoInfo.visibility = View.GONE
        mainView?.showDownloadButton()
        if (videoInfo.requestedFormats != null) {
            binding.widgetDownloadOptions.adapter = FormatDownloadAdapter(videoInfo.requestedFormats)
            binding.widgetDownloadOptions.layoutManager = LinearLayoutManager(context)
            binding.widgetDownloadOptions.visibility = View.VISIBLE
        }
        binding.layoutVideoInfo.visibility = View.VISIBLE
        binding.imageVideoThumbnail.loadImageFromRemote(videoInfo.thumbnail)
        binding.labelVideoTitle.text = videoInfo.title
    }

    private fun videoInfoFailedToRetrieve(reason: Throwable, url: String) {
        binding.widgetProgressFetchVideoInfo.visibility = View.GONE
        val message = "Failed to retrieve video info from $url"
        if (BuildConfig.DEBUG) {
            Log.e("video-info", message, reason)
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}