package com.compose.admobads

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.ump.UserMessagingPlatform
import com.google.android.ump.ConsentInformation

@Composable
fun ConsentScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val consentInformation = UserMessagingPlatform.getConsentInformation(context)
    var consentStatus by remember { mutableStateOf("Unknown") }
    var canRequestAds by remember { mutableStateOf(false) }
    var isFormAvailable by remember { mutableStateOf(false) }
    var privacyOptionsRequired by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    consentStatus = when (consentInformation.consentStatus) {
        ConsentInformation.ConsentStatus.UNKNOWN -> "Unknown"
        ConsentInformation.ConsentStatus.NOT_REQUIRED -> "Not Required"
        ConsentInformation.ConsentStatus.REQUIRED -> "Required"
        ConsentInformation.ConsentStatus.OBTAINED -> "Obtained"
        else -> "Unknown (${consentInformation.consentStatus})"
    }
    canRequestAds = consentInformation.canRequestAds()
    isFormAvailable = consentInformation.isConsentFormAvailable
    privacyOptionsRequired = consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Consent Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Manage your ad consent preferences",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (canRequestAds) Color(0xFFE8F5E9) else Color(0xFFFFF9C4)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(text = "Consent Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Status: $consentStatus", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF424242))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Can Request Ads: ${if (canRequestAds) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF424242))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Form Available: ${if (isFormAvailable) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF424242))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Privacy Options Required: ${if (privacyOptionsRequired) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF424242))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(text = "Error: $error", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F), modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                activity?.let {
                    errorMessage = null
                    if (privacyOptionsRequired) {
                        UserMessagingPlatform.showPrivacyOptionsForm(it) { formError ->
                            if (formError != null) {
                                errorMessage = "Privacy Options Error: ${formError.message}"
                            } else {
                                canRequestAds = consentInformation.canRequestAds()
                                consentStatus = when (consentInformation.consentStatus) {
                                    ConsentInformation.ConsentStatus.UNKNOWN -> "Unknown"
                                    ConsentInformation.ConsentStatus.NOT_REQUIRED -> "Not Required"
                                    ConsentInformation.ConsentStatus.REQUIRED -> "Required"
                                    ConsentInformation.ConsentStatus.OBTAINED -> "Obtained"
                                    else -> "Unknown (${consentInformation.consentStatus})"
                                }
                            }
                        }
                    } else {
                        errorMessage = "Privacy options form not available in your region"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(56.dp),
            enabled = privacyOptionsRequired || isFormAvailable,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), disabledContainerColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (privacyOptionsRequired) "Show Privacy Options" else "Privacy Options (Not Available)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = null
                consentInformation.reset()
                consentStatus = when (consentInformation.consentStatus) {
                    ConsentInformation.ConsentStatus.UNKNOWN -> "Unknown"
                    ConsentInformation.ConsentStatus.NOT_REQUIRED -> "Not Required"
                    ConsentInformation.ConsentStatus.REQUIRED -> "Required"
                    ConsentInformation.ConsentStatus.OBTAINED -> "Obtained"
                    else -> "Unknown (${consentInformation.consentStatus})"
                }
                canRequestAds = consentInformation.canRequestAds()
                isFormAvailable = consentInformation.isConsentFormAvailable
                privacyOptionsRequired = consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
                errorMessage = "Consent reset. Restart app to see consent form again."
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Reset Consent (Testing Only)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "About Consent", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "- Consent forms comply with GDPR\n- Shows automatically in EEA region\n- Required for personalized ads\n- Reset for testing purposes only",
                    style = MaterialTheme.typography.bodySmall, color = Color(0xFF424242)
                )
            }
        }
    }
}
