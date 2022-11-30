package id.bungamungil.pockettube

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.yausername.aria2c.Aria2c
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class PocketTubeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Completable.fromAction(this::initLibraries)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    // init libraries starting
                }

                override fun onComplete() {
                    // init libraries completed
                }

                override fun onError(e: Throwable) {
                    // init libraries failed
                    whenInitLibrariesError(e)
                }
            })
    }

    private fun initLibraries() {
        YoutubeDL.getInstance().init(this)
        FFmpeg.getInstance().init(this)
        Aria2c.getInstance().init(this)
    }

    private fun whenInitLibrariesError(reason: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e("init-libraries", "Failed to initialize libraries", reason)
        }
        Toast.makeText(
            this,
            "Initialization failed: " + reason.localizedMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

}