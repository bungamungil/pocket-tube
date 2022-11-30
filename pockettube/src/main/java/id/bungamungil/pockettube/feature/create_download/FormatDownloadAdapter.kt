package id.bungamungil.pockettube.feature.create_download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yausername.youtubedl_android.mapper.VideoFormat
import id.bungamungil.pockettube.databinding.ItemFormatDownloadBinding
import id.bungamungil.pockettube.util.iconTrack
import id.bungamungil.pockettube.util.labelSize
import id.bungamungil.pockettube.util.labelTrackTitle
import id.bungamungil.pockettube.util.onlyVisibleWhenHasText

class FormatDownloadAdapter(private val videoFormats: List<VideoFormat>) :
    RecyclerView.Adapter<FormatDownloadAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFormatDownloadBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFormatDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = videoFormats[position]
        Glide.with(holder.binding.iconFormatDownload)
            .load(item.iconTrack())
            .into(holder.binding.iconFormatDownload)
        holder.binding.labelTrackTitle.text = item.labelTrackTitle(holder.binding.root.context)
        holder.binding.labelTrackNote.text = item.format
        holder.binding.labelTrackExtension.onlyVisibleWhenHasText(item.ext)
        holder.binding.labelTrackSize.onlyVisibleWhenHasText(item.labelSize())
    }

    override fun getItemCount(): Int {
        return videoFormats.count()
    }

}