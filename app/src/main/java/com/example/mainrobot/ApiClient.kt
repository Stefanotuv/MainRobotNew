package com.example.mainrobot
import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ApiClient {
    private val TAG = "ApiClient"

//    fun sendRequest(url: String, method: String, key: String? = null, value: String? = null, callback: (String) -> Unit) {
//        if (method == "GET") {
//            GetRequestTask(callback).execute(url)
//        } else {
//            val data = JSONObject()
//            if (key != null && value != null) {
//                data.put(key, value)
//            }
//
//            PostRequestTask(callback).execute(url, data.toString())
//        }
//    }

    fun sendRequest(url: String, method: String, json: JSONObject? = null, callback: (String) -> Unit) {
        if (method == "GET") {
            GetRequestTask(callback).execute(url)
        } else {
            val data = json?.toString() ?: ""

            PostRequestTask(callback).execute(url, data)
        }
    }


    private inner class GetRequestTask(private val callback: (String) -> Unit) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String): String {
            val urlString = urls[0]
            val url = addProtocolToUrl(urlString)

            val connection: HttpURLConnection
            val response: String

            try {
                connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                val inputStream = BufferedInputStream(connection.inputStream)
                response = convertStreamToString(inputStream)
            } catch (e: Exception) {
                Log.e(TAG, "Error in GET request: ${e.message}")
                return ""
            }

            return response
        }

        override fun onPostExecute(result: String) {
            callback.invoke(result)
        }
    }

    private inner class PostRequestTask(private val callback: (String) -> Unit) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val urlString = params[0]
            val data = params[1]
            val url = addProtocolToUrl(urlString)

            val connection: HttpURLConnection
            val response: String

            try {
                val url = URL(url)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val outputStream = BufferedOutputStream(connection.outputStream)
                val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                writer.write(data)
                writer.flush()
                writer.close()

                val inputStream = BufferedInputStream(connection.inputStream)
                response = convertStreamToString(inputStream)
            } catch (e: Exception) {
                Log.e(TAG, "Error in POST request: ${e.message}")
                return ""
            }

            return response
        }

        override fun onPostExecute(result: String) {
            callback.invoke(result)
        }
    }

    private fun addProtocolToUrl(urlString: String): String {
        var formattedUrl = urlString
        if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
            formattedUrl = "http://$formattedUrl" // Add the protocol if missing
        }
        return formattedUrl
    }

    private fun convertStreamToString(inputStream: BufferedInputStream): String {
        return inputStream.bufferedReader().use { it.readText() }
    }
}
