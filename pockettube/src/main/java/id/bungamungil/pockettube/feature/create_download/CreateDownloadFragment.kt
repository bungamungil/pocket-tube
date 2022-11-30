package id.bungamungil.pockettube.feature.create_download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.bungamungil.pockettube.databinding.FragmentCreateDownloadBinding

class CreateDownloadFragment : Fragment() {

    private var _binding: FragmentCreateDownloadBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

}