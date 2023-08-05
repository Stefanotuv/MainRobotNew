package com.example.mainrobot.ui.configurations

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mainrobot.databinding.FragmentConfigurationsBinding
import org.json.JSONObject
import com.example.mainrobot.ApiClient

class ConfigurationsFragment : Fragment() {

    private lateinit var binding: FragmentConfigurationsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentConfigurationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frontCameraIpEditText = binding.frontCameraIpEditText
        val backCameraIpEditText = binding.backCameraIpEditText
        val speedSeekBar = binding.speedSeekBar
        val wifiSsidEditText = binding.wifiSsidEditText
        val wifiPasswordEditText = binding.wifiPasswordEditText
        val showPasswordCheckBox = binding.showPasswordCheckBox
        val apModeSwitch = binding.apModeSwitch
        val saveButton = binding.saveButton
        val resetButton = binding.resetButton

        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (sharedPreferences != null){
            // the file exist so we can load the preferences
            val jsonString = sharedPreferences?.getString("jsonString", null)
            val jsonObject = JSONObject(jsonString)
            val frontCameraIp = jsonObject.getString("front_camera_ip")
            val apWifi = jsonObject.getString("ap_wifi")
            val backCameraIp = jsonObject.getString("back_camera_ip")
            val ssid = jsonObject.getString("ssid")
            val password = jsonObject.getString("password")

            wifiSsidEditText.text = Editable.Factory.getInstance().newEditable(ssid)
            wifiPasswordEditText.text = Editable.Factory.getInstance().newEditable(password)
            frontCameraIpEditText.text = Editable.Factory.getInstance().newEditable(frontCameraIp)
            backCameraIpEditText.text = Editable.Factory.getInstance().newEditable(backCameraIp)
        }

        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show password
                wifiPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                // Hide password
                wifiPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        apModeSwitch.setOnClickListener{
            if(apModeSwitch.isEnabled) {
                // AP is selected

            }
            else if(!apModeSwitch.isEnabled) {
                // Wifi is selected

            }
        }


        saveButton.setOnClickListener {
            // Read the input values
            val frontCameraIp = frontCameraIpEditText.text.toString()
            val backCameraIp = backCameraIpEditText.text.toString()
            val speed = speedSeekBar.progress
            val wifiSsid = wifiSsidEditText.text.toString()
            val wifiPassword = wifiPasswordEditText.text.toString()
            val isShowPasswordChecked = showPasswordCheckBox.isChecked
            var isApModeSelected = "AP"
            if (apModeSwitch.isChecked) {
                isApModeSelected = "wifi"
            }
            else if(!apModeSwitch.isChecked) {
                isApModeSelected = "ap"
            }
            // Create a JSON object with the input data
            val inputData = JSONObject().apply {
                put("front_camera_ip", frontCameraIp)
                put("back_camera_ip", backCameraIp)
                put("speed", speed)
                put("ssid", wifiSsid)
                put("password", wifiPassword)
                put("ap_wifi", isApModeSelected)
            }

            // Send the JSON data through the API
            val apiClient = ApiClient()
            val address_robotcar = "http://192.168.2.45/api/config"
            apiClient.sendRequest(address_robotcar, "POST", inputData) { response ->
                // Handle the API response here
                Log.d("ConfigurationsFragment", "API Response: $response")
            }

            // Show a toast message to indicate the save operation
            Toast.makeText(requireContext(), "Configuration saved on Server", Toast.LENGTH_SHORT).show()

            // TODO: add code to update the file as well and add the toast however do the
            // local file if the file has been written correctly on the server

        }

        // TODO: Implement the logic for the resetButton click event
    }

    // ...
}
