package com.invoice.composeadmobeads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.invoice.composeadmobeads.ui.theme.ComposeAdmobeAdsTheme

// Import library components
import com.compose.admobads.BannerAdView
import com.compose.admobads.CollapsibleBannerAdView
import com.compose.admobads.CenterBannerAdView
import com.compose.admobads.TopCollapsibleBanner
import com.compose.admobads.InlineMediumNativeAd
import com.compose.admobads.InterstitialAdScreen
import com.compose.admobads.LargeNativeAdView
import com.compose.admobads.MediumNativeAdView
import com.compose.admobads.SmallNativeAdView
import com.compose.admobads.AppOpenAdScreen
import com.compose.admobads.FullscreenNativeAdScreen
import com.compose.admobads.ConsentScreen

class MainActivity : ComponentActivity() {
    private lateinit var consentInformation: ConsentInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Consent Form
        initializeConsentForm()

        enableEdgeToEdge()
        setContent {
            ComposeAdmobeAdsTheme {
                AdMobApp()
            }
        }
    }

    private fun initializeConsentForm() {
        val debugSettings = ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("TEST-DEVICE-HASHED-ID")
            .build()

        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)

        Log.d("ConsentForm", "Requesting consent info update...")

        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                Log.d("ConsentForm", "Consent info updated. Status: ${consentInformation.consentStatus}")
                Log.d("ConsentForm", "Can request ads: ${consentInformation.canRequestAds()}")
                Log.d("ConsentForm", "Privacy options required: ${consentInformation.privacyOptionsRequirementStatus}")

                if (consentInformation.isConsentFormAvailable) {
                    Log.d("ConsentForm", "Consent form is available, loading...")

                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { formError ->
                        if (formError != null) {
                            Log.e("ConsentForm", "Form Error: ${formError.errorCode} - ${formError.message}")
                        } else {
                            Log.d("ConsentForm", "Consent form shown successfully or not required")
                        }

                        if (consentInformation.canRequestAds()) {
                            Log.d("ConsentForm", "Initializing Mobile Ads SDK...")
                            initializeMobileAdsSdk()
                        } else {
                            Log.w("ConsentForm", "Cannot request ads yet, consent not obtained")
                        }
                    }
                } else {
                    Log.d("ConsentForm", "Consent form not available")
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk()
                    }
                }
            },
            { requestConsentError ->
                Log.e("ConsentForm", "Request error: ${requestConsentError.errorCode} - ${requestConsentError.message}")
                initializeMobileAdsSdk()
            }
        )

        if (consentInformation.canRequestAds()) {
            Log.d("ConsentForm", "Consent already obtained, initializing ads immediately")
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        MobileAds.initialize(this) {}
    }
}

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "AdMob Demo")
    object Banner : Screen("banner", "Banner Ads")
    object CollapsibleBanner : Screen("collapsible_banner", "Collapsible Banner")
    object Interstitial : Screen("interstitial", "Interstitial Ads")
    object Native : Screen("native", "Native Ads")
    object AppOpen : Screen("appopen", "App Open Ads")
    object FullscreenNative : Screen("fullscreen_native", "Fullscreen Native Ad")
    object Consent : Screen("consent", "Consent Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdMobApp() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    val currentScreen = when (currentRoute) {
        Screen.Banner.route -> Screen.Banner
        Screen.CollapsibleBanner.route -> Screen.CollapsibleBanner
        Screen.Interstitial.route -> Screen.Interstitial
        Screen.Native.route -> Screen.Native
        Screen.AppOpen.route -> Screen.AppOpen
        Screen.FullscreenNative.route -> Screen.FullscreenNative
        Screen.Consent.route -> Screen.Consent
        else -> Screen.Home
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) },
                navigationIcon = {
                    if (currentRoute != Screen.Home.route) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Banner.route) {
                BannerAdDemoScreen()
            }
            composable(Screen.CollapsibleBanner.route) {
                CollapsibleBannerDemoScreen()
            }
            composable(Screen.Interstitial.route) {
                InterstitialAdScreen(
                    isEnabled = true,
                    adUnitId = "ca-app-pub-3940256099942544/1033173712",
                    onAdLoaded = { },
                    onAdFailed = { },
                    onAdDismissed = { }
                )
            }
            composable(Screen.Native.route) {
                NativeAdDemoScreen()
            }
            composable(Screen.AppOpen.route) {
                AppOpenAdScreen(
                    isEnabled = true,
                    adUnitId = "ca-app-pub-3940256099942544/9257395921",
                    onAdLoaded = { },
                    onAdFailed = { },
                    onAdDismissed = { }
                )
            }
            composable(Screen.FullscreenNative.route) {
                FullscreenNativeAdScreen(
                    isEnabled = true,
                    adUnitId = "ca-app-pub-3940256099942544/2247696110",
                    onAdLoaded = { },
                    onAdFailed = { }
                )
            }
            composable(Screen.Consent.route) {
                ConsentScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AdMob Integration",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Select an ad type to view",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navController.navigate(Screen.Banner.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(text = "Banner Ads", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.CollapsibleBanner.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Collapsible Banner", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.Interstitial.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(text = "Interstitial Ads", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.Native.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(text = "Native Ads", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.AppOpen.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text(text = "App Open Ads", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.FullscreenNative.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(text = "Fullscreen Native Ads", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.Consent.route) },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
        ) {
            Text(text = "Consent Settings", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Using Google AdMob SDK",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
