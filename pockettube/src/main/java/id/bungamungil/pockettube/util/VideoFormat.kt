package id.bungamungil.pockettube.util

import android.content.Context
import com.yausername.youtubedl_android.mapper.VideoFormat
import id.bungamungil.pockettube.R

enum class VideoFormatType {
    VIDEO, AUDIO, MERGE
}

fun VideoFormat.formatType(): VideoFormatType {
    return when (this.width) {
        0 -> {
            VideoFormatType.AUDIO
        }
        else -> {
            VideoFormatType.VIDEO
        }
    }
}

fun VideoFormat.labelTrackTitle(context: Context): String {
    return when (this.formatType()) {
        VideoFormatType.AUDIO -> {
            "Audio Track"
        }
        VideoFormatType.VIDEO -> {
            "Video Track"
        }
        VideoFormatType.MERGE -> {
            "Video & Audio Track"
        }
    }
}

fun VideoFormat.iconTrack(): Int {
    return when (this.formatType()) {
        VideoFormatType.AUDIO -> {
            R.drawable.music
        }
        VideoFormatType.VIDEO -> {
            R.drawable.video
        }
        VideoFormatType.MERGE -> {
            R.drawable.download
        }
    }
}

fun VideoFormat.labelSize(): String? {
    if (this.fileSizeApproximate > 0) {
        return "${this.fileSizeApproximate}"
    } else if (this.fileSize > 0) {
        return "${this.fileSize}"
    }
    return null
}
