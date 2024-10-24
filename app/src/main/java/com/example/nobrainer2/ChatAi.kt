package com.example.nobrainer2

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ChatAi : AppCompatActivity() {
    private val client = OkHttpClient()
    private val TAG = "ChatAI"

    private lateinit var txtResponse: TextView
    private lateinit var idTVQuestion: TextView
    private lateinit var etQuestion: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_ai)

        // Initialize views
        etQuestion = findViewById(R.id.etQuestion)
        idTVQuestion = findViewById(R.id.idTVQuestion)
        txtResponse = findViewById(R.id.txtResponse)

        setupQuestionInput()
    }

    private fun setupQuestionInput() {
        etQuestion.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val question = etQuestion.text.toString().trim()
                handleQuestion(question)
                true
            } else {
                false
            }
        }
    }

    private fun handleQuestion(question: String) {
        if (question.isEmpty()) {
            showToast("Please enter a question")
            return
        }

        if (!isNetworkAvailable()) {
            showToast("No internet connection available")
            return
        }

        txtResponse.text = "Please wait..."
        getResponse(question) { response ->
            runOnUiThread {
                txtResponse.text = response
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getResponse(question: String, callback: (String) -> Unit) {
        idTVQuestion.text = question
        etQuestion.setText("")

        val apiKey = "SA0OUsnNqMijYiVAsQqdCOnCCtduMMWD6mMS0RaA"
        val url = "https://api.cohere.ai/v1/generate"

        val requestBody = """
            {
                "model": "command",
                "prompt": "$question",
                "max_tokens": 300,
                "temperature": 0.7,
                "k": 0,
                "stop_sequences": [],
                "return_likelihoods": "NONE"
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("content-type", "application/json")
            .addHeader("authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "API failed", e)
                showToast("Error: API call failed")
                callback("Error: Unable to connect to the server. Please check your internet connection and try again.")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body?.string()

                    if (!response.isSuccessful) {
                        handleErrorResponse(response.code, body, callback)
                        return
                    }

                    if (body == null) {
                        callback("Error: Empty response from server.")
                        return
                    }

                    handleSuccessResponse(body, callback)

                } catch (e: Exception) {
                    Log.e(TAG, "Error processing response", e)
                    callback("Error: Something went wrong. Please try again.")
                }
            }
        })
    }

    private fun handleErrorResponse(code: Int, errorBody: String?, callback: (String) -> Unit) {
        val errorMessage = when (code) {
            401 -> "Error: Invalid API key"
            429 -> "Error: Rate limit exceeded. Please try again later."
            500 -> "Error: Server error. Please try again later."
            else -> "Error: Something went wrong (Error code: $code)"
        }

        Log.e(TAG, "API Error: $code - $errorBody")
        callback(errorMessage)
    }

    private fun handleSuccessResponse(body: String, callback: (String) -> Unit) {
        try {
            val jsonObject = JSONObject(body)

            if (jsonObject.has("generations")) {
                val generations = jsonObject.getJSONArray("generations")
                if (generations.length() > 0) {
                    val textResult = generations.getJSONObject(0).getString("text")
                    callback(textResult.trim())
                } else {
                    callback("Error: No response generated.")
                }
            } else if (jsonObject.has("message")) {
                val errorMessage = jsonObject.getString("message")
                Log.e(TAG, "API Error: $errorMessage")
                callback("Error: $errorMessage")
            } else {
                Log.e(TAG, "Unexpected response format: $body")
                callback("Error: Unexpected response format.")
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Failed to parse JSON", e)
            callback("Error: Failed to parse response.")
        }
    }
}