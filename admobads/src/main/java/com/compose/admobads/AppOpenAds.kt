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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

@Composable
fun AppOpenAdScreen(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/9257395921",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {},
    onAdDismissed: () -> Unit = {}
) {
    val context = LocalContext.current
    val isConnected = isInternetAvailable(context)

    var appOpenAd by remember { mutableStateOf<AppOpenAd?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var adStatus by remember { mutableStateOf("No ad loaded") }

    fun loadAppOpenAd() {
        if (!isEnabled || !isConnected) return

        isLoading = true
        adStatus = "Loading ad..."
        val adRequest = AdRequest.Builder().build()

        AppOpenAd.load(
            context,
            adUnitId,
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    appOpenAd = null
                    isLoading = false
                    adStatus = "Ad failed to load: ${loadAdError.message}"
                    onAdFailed(loadAdError)
                }

                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoading = false
                    adStatus = "Ad loaded successfully!"
                    onAdLoaded()

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            appOpenAd = null
                            adStatus = "Ad dismissed. Click reload to load new ad"
                            onAdDismissed()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            appOpenAd = null
                            adStatus = "Ad failed to show: ${adError.message}"
                        }

                        override fun onAdShowedFullScreenContent() {
                            adStatus = "Ad showing"
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
            loadAppOpenAd()
        }
        onDispose { }
    }

    // If not enabled or no internet, don't show anything
    if (!isEnabled || !isConnected) return

    // Fullscreen Welcome Back Dialog
    if (isLoading) {
        WelcomeBackDialog()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "App Open Ad Screen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "App Open ads are full-screen ads that appear when users open or return to your app.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Status indicator
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    when {
                        adStatus.contains("success") -> Color(0xFFE8F5E9)
                        adStatus.contains("failed") -> Color(0xFFFFEBEE)
                        isLoading -> Color(0xFFFFF9C4)
                        else -> Color(0xFFF5F5F5)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFFFBC02D)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = adStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = when {
                        adStatus.contains("success") -> Color(0xFF2E7D32)
                        adStatus.contains("failed") -> Color(0xFFC62828)
                        else -> Color(0xFF424242)
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (appOpenAd != null) {
                    appOpenAd?.show(context as Activity)
                } else {
                    adStatus = "Ad not ready. Loading..."
                    loadAppOpenAd()
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = if (isLoading) "Loading..." else "Show App Open Ad",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { loadAppOpenAd() },
            enabled = !isLoading,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Reload Ad",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "App Open Ad Info",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "- Appears when app launches\n- Full-screen format\n- Can be shown when app returns from background\n- Best for monetization on app start",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF424242)
                )
            }
        }
    }
}

@Composable
fun WelcomeBackDialog() {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .background(
                            Color(0xFF2196F3).copy(alpha = 0.1f),
                            RoundedCornerShape(50.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\uD83D\uDC4B",
                        fontSize = 48.sp,
                        modifier = Modifier.alpha(alpha)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF212121),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please wait while we prepare",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "content for you",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFF2196F3),
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
