package com.example.mainrobot.ui.home

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mainrobot.databinding.FragmentHomeBinding
import com.example.mainrobot.RobocarViewModel
import com.example.mainrobot.ApiClient
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var robocarViewModel: RobocarViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val apiClient = ApiClient()
    private val addressRobotCar = "http://192.168.2.45/api"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val addressEditText: EditText = binding.inputEditText
        val saveConnect: Button = binding.saveConnect

        robocarViewModel = ViewModelProvider(requireActivity()).get(RobocarViewModel::class.java)

        saveConnect.setOnClickListener {
            val address = addressEditText.text.toString()
            robocarViewModel.setRobocarAddress(address)
            Log.d("HomeFragment", "Robocar address saved: $address")
            Toast.makeText(requireContext(), "Robocar address saved", Toast.LENGTH_SHORT).show()

            // check if the file exist first to load the default values

            if (saveConnect.text == "connect"){
                apiClient.sendRequest(address, "GET") { response ->
                    // Handle the API response here
                    Log.d("RobotCarFragment", "API Response: $response")
                    activity?.runOnUiThread {
                        if (response != "") { // answer is received
                            saveConnect.text = "disconnect"

                            // Save the JSON string to SharedPreferences
                            val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            //val jsonString = "{\"front_camera_ip\": \"192.168.2.186\", \"ap_wifi\": \"wifi\", \"back_camera_ip\": \"192.168.2.235\", \"ssid\": \"micasa\", \"password\": \"\"}"
                            val jsonString = response
                            sharedPreferences?.edit()?.putString("jsonString", jsonString)?.apply()

                        } else {
                            // Handle unsuccessful response if needed
                        }
                    }
                }
            }

            else if (saveConnect.text == "disconnect"){
                saveConnect.text = "connect"
                val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val jsonString = sharedPreferences?.getString("jsonString", null)

                // If jsonString is not null, you can parse it to a JSON object
                val jsonObject = JSONObject(jsonString)
                val frontCameraIp = jsonObject.getString("front_camera_ip")
                val apWifi = jsonObject.getString("ap_wifi")
                val backCameraIp = jsonObject.getString("back_camera_ip")
                val ssid = jsonObject.getString("ssid")
                val password = jsonObject.getString("password")
                Log.d("Fileread", "From File: $jsonObject")
            }


            else {

            }



        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
