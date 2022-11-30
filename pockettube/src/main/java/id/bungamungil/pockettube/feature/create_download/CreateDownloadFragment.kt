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
import com.google.android.material.snackbar.Snackbar
import com.yausername.youtubedl_android.BuildConfig
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.mapper.VideoInfo
import id.bungamungil.pockettube.databinding.FragmentCreateDownloadBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


class CreateDownloadFragment : Fragment(), TextView.OnEditorActionListener {

    private var _binding: FragmentCreateDownloadBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputUrl.setOnEditorActionListener(this)
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
            .subscribe(this::videoInfoRetrieved) { reason ->
                val message = "Failed to retrieve video info from $url"
                if (BuildConfig.DEBUG) {
                    Log.e("video-info", message, reason)
                }
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun videoInfoRetrieved(videoInfo: VideoInfo) {
        Log.i("video-info", videoInfo.toString())
    }

}