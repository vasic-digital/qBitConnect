package com.shareconnect.qbitconnect.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private lateinit var securityAccessManager: SecurityAccessManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securityAccessManager = SecurityAccessManager.getInstance(this)

        // Check if security access is required BEFORE onboarding
        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
            return
        }

        // Check if onboarding is needed BEFORE setting up UI
        if (isOnboardingNeeded()) {
            // Finish this activity immediately - onboarding will be launched from App
            finish()
            return
        }

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()

        // Check security access when app comes back to foreground
        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
            return
        }
    }

    private fun isOnboardingNeeded(): Boolean {
        val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        return !prefs.getBoolean("onboarding_completed", false)
    }

    private fun isSecurityAccessRequired(): Boolean {
        return try {
            runBlocking {
                securityAccessManager.isAccessRequired()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking security access requirement", e)
            false // Default to no security if there's an error
        }
    }

    private fun launchSecurityAccess() {
        try {
            showSecurityAccessDialog()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error launching security access", e)
            // If security access fails, continue with normal flow
            setupMainUI()
        }
    }

    private fun showSecurityAccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Security Access Required")
        builder.setMessage("Please enter your PIN to access the application")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Unlock") { dialog, _ ->
            val pin = input.text.toString()
            authenticateWithPin(pin)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
            finish() // Close app if user cancels
        }

        builder.setCancelable(false)
        builder.show()
    }

    private fun authenticateWithPin(pin: String) {
        lifecycleScope.launch {
            try {
                val result = securityAccessManager.authenticate(AccessMethod.PIN, pin)
                when (result) {
                    is SecurityAccessManager.AuthenticationResult.Success -> {
                        // Authentication successful, continue with normal flow
                        setupMainUI()
                    }
                    else -> {
                        // Authentication failed, show error and retry
                        Toast.makeText(this@MainActivity, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                        showSecurityAccessDialog()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during PIN authentication", e)
                Toast.makeText(this@MainActivity, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show()
                showSecurityAccessDialog()
            }
        }
    }

    private fun setupMainUI() {
        setContent {
            App()
        }
    }
}