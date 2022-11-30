package id.bungamungil.pockettube.feature.create_download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yausername.youtubedl_android.mapper.VideoFormat
import id.bungamungil.pockettube.databinding.ItemFormatDownloadBinding

class FormatDownloadAdapter(private val videoFormats: List<VideoFormat>) :
    RecyclerView.Adapter<FormatDownloadAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFormatDownloadBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFormatDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.text.text = videoFormats[position].formatNote
    }

    override fun getItemCount(): Int {
        return videoFormats.count()
    }

}