package com.compose.admobads

import android.app.Activity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
fun InterstitialAdScreen(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/1033173712",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {},
    onAdDismissed: () -> Unit = {}
) {
    val context = LocalContext.current
    val isConnected = isInternetAvailable(context)

    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var adStatus by remember { mutableStateOf("No ad loaded") }

    fun loadInterstitialAd() {
        if (!isEnabled || !isConnected) return

        isLoading = true
        showLoadingDialog = true
        adStatus = "Loading ad..."
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    showLoadingDialog = false
                    adStatus = "Ad failed to load: ${loadAdError.message}"
                    onAdFailed(loadAdError)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    adStatus = "Ad loaded successfully!"
                    onAdLoaded()

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            showLoadingDialog = false
                            interstitialAd = null
                            adStatus = "Ad dismissed. Click reload to load new ad"
                            onAdDismissed()
                        }

                        override fun onAdShowedFullScreenContent() {
                            showLoadingDialog = false
                            adStatus = "Ad showing"
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                            showLoadingDialog = false
                            interstitialAd = null
                            adStatus = "Ad failed to show: ${adError.message}"
                        }
                    }

                    // Show ad automatically when loaded
                    ad.show(context as Activity)
                }
            }
        )
    }

    DisposableEffect(Unit) {
        if (isEnabled && isConnected) {
            loadInterstitialAd()
        }
        onDispose { }
    }

    // If not enabled or no internet, don't show anything
    if (!isEnabled || !isConnected) return

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Text(
            text = "Interstitial Ad Screen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Interstitial ads are full-screen ads that cover the interface of their host app.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Status: $adStatus",
            style = MaterialTheme.typography.bodyMedium,
            color = if (adStatus.contains("success"))
                MaterialTheme.colorScheme.primary
            else if (adStatus.contains("failed"))
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (interstitialAd != null) {
                    showLoadingDialog = true
                    interstitialAd?.show(context as Activity)
                } else {
                    adStatus = "Ad not ready. Loading..."
                    loadInterstitialAd()
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Loading..." else "Show Interstitial Ad")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { loadInterstitialAd() },
            enabled = !isLoading
        ) {
            Text("Reload Ad")
        }
        }

        // Fullscreen Loading Overlay
        if (showLoadingDialog) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                FullScreenLoadingDialog()
            }
        }
    }
}

@Composable
fun FullScreenLoadingDialog() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val dotAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dotAlpha2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dotAlpha3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Box(
        modifier = Modifier
            .width(screenWidth)
            .height(screenHeight)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 40.dp, horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .background(
                            color = Color(0xFF1976D2).copy(alpha = 0.08f),
                            shape = RoundedCornerShape(50.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = Color(0xFF1976D2),
                        strokeWidth = 4.dp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Loading Advertisement",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(dotAlpha1)
                            .background(
                                Color(0xFF1976D2),
                                RoundedCornerShape(5.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(dotAlpha2)
                            .background(
                                Color(0xFF1976D2),
                                RoundedCornerShape(5.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(dotAlpha3)
                            .background(
                                Color(0xFF1976D2),
                                RoundedCornerShape(5.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Your ad will appear in just a moment.\nThank you for your patience.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF616161),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = Color(0xFF1976D2),
                    trackColor = Color(0xFFE3F2FD)
                )
            }
    }
}
