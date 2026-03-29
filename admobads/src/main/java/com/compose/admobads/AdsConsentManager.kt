package com.compose.admobads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Consent state for ads
 */
data class ConsentState(
    val canRequestAds: Boolean = false,
    val isConsentFormAvailable: Boolean = false,
    val isPrivacyOptionsRequired: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * Manages user consent for ads using the User Messaging Platform (UMP).
 */
class AdsConsentManager private constructor(context: Context) {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)
    private val canRequestAdsFlag = AtomicBoolean(false)
    private val isProcessingConsent = AtomicBoolean(false)

    companion object {
        private const val TAG = "AdsConsentManager"

        @Volatile
        private var instance: AdsConsentManager? = null

        fun getInstance(context: Context): AdsConsentManager {
            return instance ?: synchronized(this) {
                instance ?: AdsConsentManager(context.applicationContext).also { instance = it }
            }
        }

        /**
         * Gets the user's consent result for ads.
         */
        fun getConsentResult(context: Context): Boolean {
            val consentString = context.getSharedPreferences(
                "${context.packageName}_preferences", 0
            ).getString("IABTCF_PurposeConsents", "") ?: ""
            Log.d(TAG, "getConsentResult: Consent string = $consentString")
            return consentString.isEmpty() || consentString.firstOrNull() == '1'
        }
    }

    /**
     * Requests user consent for ads using the User Messaging Platform.
     */
    fun requestUMP(
        activity: Activity,
        onResult: (canShowAds: Boolean) -> Unit
    ) {
        requestUMP(
            activity = activity,
            enableDebug = false,
            testDevice = "",
            resetData = false,
            onResult = onResult
        )
    }

    /**
     * Requests user consent for ads using the User Messaging Platform with additional parameters.
     */
    fun requestUMP(
        activity: Activity,
        enableDebug: Boolean = false,
        testDevice: String = "",
        resetData: Boolean = false,
        onResult: (canShowAds: Boolean) -> Unit
    ) {
        // Prevent concurrent consent requests
        if (isProcessingConsent.getAndSet(true)) {
            Log.d(TAG, "requestUMP: Consent request already in progress, ignoring")
            return
        }

        Log.d(TAG, "requestUMP: Starting consent request, canRequestAds=${canRequestAdsFlag.get()}")

        // Reset consent data if requested
        if (resetData) {
            Log.d(TAG, "requestUMP: Resetting consent data")
            consentInformation.reset()
        }

        // Check if ads can already be requested
        if (consentInformation.canRequestAds() && canRequestAdsFlag.get()) {
            Log.d(TAG, "requestUMP: Ads can already be requested, skipping consent form")
            isProcessingConsent.set(false)
            onResult(getConsentResult(activity))
            return
        }

        val paramsBuilder = ConsentRequestParameters.Builder()

        // Set debug settings if debugging is enabled
        if (enableDebug) {
            Log.d(TAG, "requestUMP: Enabling debug mode with test device ID: $testDevice")
            paramsBuilder.setConsentDebugSettings(
                ConsentDebugSettings.Builder(activity)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(testDevice)
                    .build()
            )
        }

        val params = paramsBuilder.setTagForUnderAgeOfConsent(false).build()

        // Request consent information update
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                Log.d(TAG, "requestUMP: Consent info updated successfully")
                // Load and show the consent form if required
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    isProcessingConsent.set(false)
                    if (formError != null) {
                        Log.e(TAG, "requestUMP: Error loading consent form: ${formError.message}")
                    } else {
                        Log.d(TAG, "requestUMP: Consent form loaded and shown or not required")
                    }

                    // Update canRequestAds and notify listener
                    if (!canRequestAdsFlag.getAndSet(consentInformation.canRequestAds())) {
                        Log.d(TAG, "requestUMP: Notifying listener, canRequestAds=${consentInformation.canRequestAds()}")
                        onResult(getConsentResult(activity))
                    }
                }
            },
            { requestConsentError ->
                isProcessingConsent.set(false)
                Log.e(TAG, "requestUMP: Consent info update failed: ${requestConsentError.message}")
                // Update canRequestAds and notify listener
                if (!canRequestAdsFlag.getAndSet(consentInformation.canRequestAds())) {
                    Log.d(TAG, "requestUMP: Notifying listener after error, canRequestAds=${consentInformation.canRequestAds()}")
                    onResult(getConsentResult(activity))
                }
            }
        )
    }

    /**
     * Shows the privacy options form for the user to manage consent settings.
     */
    fun showPrivacyOption(
        activity: Activity,
        onResult: (canShowAds: Boolean) -> Unit
    ) {
        Log.d(TAG, "showPrivacyOption: Showing privacy options form")
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.e(TAG, "showPrivacyOption: Error showing privacy form: ${formError.message}")
            } else {
                Log.d(TAG, "showPrivacyOption: Privacy options form shown")
            }
            onResult(getConsentResult(activity))
        }
    }

    /**
     * Checks if ads can be requested based on the user's consent.
     */
    fun canRequestAds(): Boolean {
        val canRequest = consentInformation.canRequestAds()
        Log.d(TAG, "canRequestAds: Returning $canRequest")
        return canRequest
    }

    /**
     * Checks if privacy options are required.
     */
    fun isPrivacyOptionsRequired(): Boolean {
        val required = consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        Log.d(TAG, "isPrivacyOptionsRequired: Returning $required")
        return required
    }

    /**
     * Reset consent information
     */
    fun resetConsent() {
        consentInformation.reset()
        canRequestAdsFlag.set(false)
    }
}

/**
 * Composable function to handle ads consent
 */
@Composable
fun rememberAdsConsentState(
    enableDebug: Boolean = false,
    testDevice: String = "",
    resetData: Boolean = false
): ConsentState {
    val context = LocalContext.current
    val activity = context as? Activity
    var consentState by remember { mutableStateOf(ConsentState()) }

    LaunchedEffect(Unit) {
        if (activity == null) {
            consentState = consentState.copy(
                isLoading = false,
                error = "Activity context required"
            )
            return@LaunchedEffect
        }

        val consentManager = AdsConsentManager.getInstance(context)

        consentManager.requestUMP(
            activity = activity,
            enableDebug = enableDebug,
            testDevice = testDevice,
            resetData = resetData
        ) { canShowAds ->
            consentState = ConsentState(
                canRequestAds = canShowAds,
                isConsentFormAvailable = consentManager.canRequestAds(),
                isPrivacyOptionsRequired = consentManager.isPrivacyOptionsRequired(),
                isLoading = false,
                error = null
            )
        }
    }

    return consentState
}

/**
 * Composable to request consent and show ads only when consent is granted
 */
@Composable
fun AdsConsentWrapper(
    enableDebug: Boolean = false,
    testDevice: String = "",
    onConsentResult: (canShowAds: Boolean) -> Unit = {},
    content: @Composable (canShowAds: Boolean) -> Unit
) {
    val consentState = rememberAdsConsentState(
        enableDebug = enableDebug,
        testDevice = testDevice
    )

    LaunchedEffect(consentState.canRequestAds, consentState.isLoading) {
        if (!consentState.isLoading) {
            onConsentResult(consentState.canRequestAds)
        }
    }

    content(consentState.canRequestAds && !consentState.isLoading)
}

/**
 * Composable button to show privacy options
 */
@Composable
fun PrivacyOptionsButton(
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    androidx.compose.material3.TextButton(
        onClick = {
            activity?.let {
                AdsConsentManager.getInstance(context).showPrivacyOption(it) { _ ->
                    onClick()
                }
            }
        }
    ) {
        content()
    }
}