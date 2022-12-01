package id.bungamungil.pockettube

import com.yausername.youtubedl_android.mapper.VideoInfo

interface MainView {

    fun showDownloadButton(videoInfo: VideoInfo)

    fun hideDownloadButton()

}