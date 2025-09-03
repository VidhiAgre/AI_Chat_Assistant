package com.example.aichatassistant

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.aichatassistant.ui.theme.AIChatAssistantTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()


    private val speechRecognitionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val speechResult: ArrayList<String>? =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            if (!speechResult.isNullOrEmpty()) {
                chatViewModel.onSpeechResult(speechResult[0])
            }
        } else {

            Toast.makeText(this, "Speech recognition failed or cancelled.", Toast.LENGTH_SHORT).show()
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {

                startSpeechToTextInternal()
            } else {

                Toast.makeText(this, "Microphone permission denied. Cannot use voice input.", Toast.LENGTH_LONG).show()
            }
        }

    private fun launchSpeechToTextWithPermissionCheck() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {

                startSpeechToTextInternal()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {

                Toast.makeText(this, "Microphone access is required for voice input. Please grant the permission.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {

                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startSpeechToTextInternal() {
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()) // Uses device default language
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }

        try {
            speechRecognitionLauncher.launch(speechIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Speech recognition is not available on this device.", Toast.LENGTH_LONG).show()
            // Attempt to direct user to install the Google app
            val appPackageName = "com.google.android.googlequicksearchbox"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIChatAssistantTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChatPage(
                        modifier = Modifier.padding(innerPadding),
                        chatViewModel = chatViewModel,
                        // MODIFIED: Call the function that includes the permission check
                        onVoiceInput = { launchSpeechToTextWithPermissionCheck() }
                    )
                }
            }
        }
    }
}