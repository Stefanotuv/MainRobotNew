package com.example.mainrobot.ui.robotcar

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mainrobot.ApiClient
import com.example.mainrobot.databinding.FragmentRobotcarBinding
import com.example.mainrobot.JoystickView
import com.example.mainrobot.RobocarViewModel
import org.json.JSONObject

class RobotCarFragment : Fragment(), JoystickView.JoystickListener {

    private lateinit var binding: FragmentRobotcarBinding
    private lateinit var webView: WebView
    private lateinit var joystickTopCoordinates: TextView
    private lateinit var joystickBottomCoordinates: TextView
    private lateinit var switchButton: Switch
    private var videoUrl = "http://192.168.2.186:81/stream" // Replace with your IP camera video URL

    private val apiClient = ApiClient()
    private val addressRobotCar = "http://192.168.2.45/api"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRobotcarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the WebView
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

        // Initialize the TextViews
        joystickTopCoordinates = binding.joystickTopCoordinates
        joystickBottomCoordinates = binding.joystickBottomCoordinates

        // Initialize the Switch
        switchButton = binding.switchButton
        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                videoUrl = "http://192.168.2.235:81/stream"
            } else {
                videoUrl = "http://192.168.2.186:81/stream"
            }
            Log.d("RobotCarFragment", "Switch value changed: $isChecked")
            webView.loadUrl(videoUrl) // Update the video URL in the WebView
        }


        // Initialize the JoystickViews
        binding.joystickTop.setJoystickListener(object : JoystickView.JoystickListener {
            override fun onJoystickMoved(x: Float, y: Float) {
                handleJoystickMoved(x, y, "top")
            }

            override fun onJoystickReleased() {
                handleJoystickReleased("top")
            }
        })

        binding.joystickBottom.setJoystickListener(object : JoystickView.JoystickListener {
            override fun onJoystickMoved(x: Float, y: Float) {
                handleJoystickMoved(x, y, "bottom")
            }

            override fun onJoystickReleased() {
                handleJoystickReleased("bottom")
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setJoystickPositions()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView.destroy()
    }

    override fun onJoystickMoved(x: Float, y: Float) {
        // Not used for individual joysticks
    }

    override fun onJoystickReleased() {
        // Not used for individual joysticks
    }

    private var lastPostTime: Long = 0
    private val postThrottleInterval: Long = 250 // Throttle interval in milliseconds
    private fun handleJoystickMoved(x: Float, y: Float, joypad: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPostTime >= postThrottleInterval) {
            val (scaledX, scaledY) = scaleCoordinate(x, y)

            if (joypad == "top") {
                joystickTopCoordinates.text = "X: ${String.format("%.2f", scaledX)}\nY: ${String.format("%.2f", scaledY)}"
            } else if (joypad == "bottom") {
                joystickBottomCoordinates.text = "X: ${String.format("%.2f", scaledX)}\nY: ${String.format("%.2f", scaledY)}"
            }

            val inputData = JSONObject().apply {
                put("scaledX", scaledX)
                put("scaledY", scaledY)
                put("joypad", joypad)

            }

            apiClient.sendRequest(addressRobotCar, "POST", inputData) { response ->
                // Handle the API response here
                Log.d("RobotCarFragment", "API Response: $response")
            }

            Log.d("RobotCarFragment", "Request POST sent: joypad=$joypad, scaledX=$scaledX, scaledY=$scaledY")

            lastPostTime = currentTime
        }
    }

    private fun handleJoystickReleased(joypad: String) {
        if (joypad == "top") {
            joystickTopCoordinates.text = "X: 0.00\nY: 0.00"
        } else if (joypad == "bottom") {
            joystickBottomCoordinates.text = "X: 0.00\nY: 0.00"
        }

        val inputData = JSONObject().apply {
            put("scaledX", 0)
            put("scaledY", 0)
            put("joypad", joypad)
        }

        apiClient.sendRequest(addressRobotCar, "POST", inputData) { response ->
            // Handle the API response here
            Log.d("RobotCarFragment", "API Response: $response")
        }

        Log.d("RobotCarFragment", "Request POST sent: joypad=$joypad, scaledX=0, scaledY=0")
    }



    private fun scaleCoordinate(x: Float, y: Float): Pair<Float, Float> {
        val angle = Math.atan2(y.toDouble(), x.toDouble()).toFloat()
        val radius = 1.0f
        val scaledX = radius * Math.cos(angle.toDouble()).toFloat()
        val scaledY = radius * Math.sin(angle.toDouble()).toFloat()
        return Pair(scaledX, -scaledY)
    }

    private fun setJoystickPositions() {
        val layoutParamsTop = binding.joystickTop.layoutParams as ViewGroup.LayoutParams
        val layoutParamsBottom = binding.joystickBottom.layoutParams as ViewGroup.LayoutParams

        val displayMetrics = resources.displayMetrics
        val joystickSize = (displayMetrics.widthPixels * 0.3).toInt()

        layoutParamsTop.width = joystickSize
        layoutParamsTop.height = joystickSize

        layoutParamsBottom.width = joystickSize
        layoutParamsBottom.height = joystickSize
    }
}
