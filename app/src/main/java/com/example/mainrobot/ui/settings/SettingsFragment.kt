package com.example.mainrobot.ui.settings

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mainrobot.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var webView: WebView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        val front_camera = "http://192.168.2.186"
        val back_camera = "http://192.168.2.235"

        var videoUrl = front_camera
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        val textView: TextView = binding.textSettings
//        slideshowViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        val toggle: Switch = binding.switchButtonCamera
        webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36"
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(videoUrl)

        toggle.setOnClickListener{
            if (videoUrl==front_camera){
                videoUrl = back_camera
                webView.loadUrl(videoUrl)
            }

            else if(videoUrl==back_camera) {
                videoUrl = front_camera
                webView.loadUrl(videoUrl)

            }
            else {

            }
            webView.loadUrl(videoUrl)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}