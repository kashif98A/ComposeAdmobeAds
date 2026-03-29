package com.compose.admobads

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

// Large Native Ad
@Composable
fun LargeNativeAdView(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/2247696110",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = LocalContext.current
    val isConnected = isInternetAvailable(context)

    if (!isConnected) return

    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                isLoading = false
                onAdLoaded()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoading = false
                    onAdFailed(adError)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        onDispose {
            nativeAd?.destroy()
        }
    }

    if (isLoading) {
        LargeNativeAdShimmer()
    } else {
        nativeAd?.let { ad ->
            LargeNativeAdContent(ad)
        }
    }
}

// Medium Native Ad
@Composable
fun MediumNativeAdView(
    isEnabled: Boolean = true,
    adUnitId: String = "",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = LocalContext.current
    val isConnected = isInternetAvailable(context)

    if (!isConnected) return

    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                isLoading = false
                onAdLoaded()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoading = false
                    onAdFailed(adError)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        onDispose {
            nativeAd?.destroy()
        }
    }

    if (isLoading) {
        MediumNativeAdShimmer()
    } else {
        nativeAd?.let { ad ->
            MediumNativeAdContent(ad)
        }
    }
}

// Small Native Ad
@Composable
fun SmallNativeAdView(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/2247696110",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = LocalContext.current
    val isConnected = isInternetAvailable(context)

    if (!isConnected) return

    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                isLoading = false
                onAdLoaded()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoading = false
                    onAdFailed(adError)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        onDispose {
            nativeAd?.destroy()
        }
    }

    if (isLoading) {
        SmallNativeAdShimmer()
    } else {
        nativeAd?.let { ad ->
            SmallNativeAdContent(ad)
        }
    }
}

// Ad Content Composables
@Composable
fun LargeNativeAdContent(ad: NativeAd) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Ad",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFFFFBB00), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83D\uDCF1", style = MaterialTheme.typography.headlineLarge)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ad.headline ?: "App Name",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    ad.starRating?.let { rating ->
                        Text(
                            text = "\u2605".repeat(rating.toInt()) + "\u2606".repeat(5 - rating.toInt()),
                            color = Color(0xFFFFBB00),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = ad.body ?: "App description",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = ad.callToAction ?: "Install")
            }
        }
    }
}

@Composable
fun MediumNativeAdContent(ad: NativeAd) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83D\uDCF1", style = MaterialTheme.typography.headlineSmall)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ad.headline ?: "Test Ad : Google Ads",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(2.dp))
                                .background(Color.White, RoundedCornerShape(2.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Ad",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF424242),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    ad.starRating?.let { rating ->
                        Text(
                            text = "\u2605".repeat(rating.toInt()) + "\u2606".repeat(5 - rating.toInt()),
                            color = Color(0xFF00BCD4),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp
                        )
                    } ?: run {
                        Text(
                            text = "\u2605\u2605\u2605\u2605\u2605",
                            color = Color(0xFF00BCD4),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ad.body ?: "Download now for free",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                color = Color(0xFF757575),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text(
                    text = (ad.callToAction ?: "INSTALL").uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SmallNativeAdContent(ad: NativeAd) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83D\uDCF1", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ad.headline ?: "App Name",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(2.dp))
                            .background(Color.White, RoundedCornerShape(2.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Ad",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242),
                            fontSize = 10.sp
                        )
                    }
                }

                ad.starRating?.let { rating ->
                    Text(
                        text = "\u2605".repeat(rating.toInt()) + "\u2606".repeat(5 - rating.toInt()),
                        color = Color(0xFF00BCD4),
                        style = MaterialTheme.typography.labelSmall
                    )
                } ?: run {
                    Text(
                        text = "\u2605\u2605\u2605\u2605\u2605",
                        color = Color(0xFF00BCD4),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { }
            ) {
                Text(text = ad.callToAction ?: "Install", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// Shimmer Effects
@Composable
fun LargeNativeAdShimmer() {
    val shimmerColors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0))
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "shimmer"
    )
    val brush = Brush.linearGradient(colors = shimmerColors, start = Offset(translateAnim, 0f), end = Offset(translateAnim + 400f, 0f))

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAFA)).padding(16.dp)) {
            Box(modifier = Modifier.width(30.dp).height(16.dp).background(brush, shape = RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.width(80.dp).height(80.dp).background(brush, shape = RoundedCornerShape(8.dp)))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxWidth(0.8f).height(20.dp).background(brush, shape = RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.width(80.dp).height(12.dp).background(brush, shape = RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(14.dp).background(brush, shape = RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp).background(brush, shape = RoundedCornerShape(4.dp)))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(48.dp).background(brush, shape = RoundedCornerShape(8.dp)))
        }
    }
}

@Composable
fun MediumNativeAdShimmer() {
    val shimmerColors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0))
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "shimmer"
    )
    val brush = Brush.linearGradient(colors = shimmerColors, start = Offset(translateAnim, 0f), end = Offset(translateAnim + 400f, 0f))

    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAFA)).padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.width(50.dp).height(50.dp).background(brush, shape = RoundedCornerShape(8.dp)))
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp).background(brush, shape = RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(80.dp).height(12.dp).background(brush, shape = RoundedCornerShape(4.dp)))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp).background(brush, shape = RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(brush, shape = RoundedCornerShape(8.dp)))
        }
    }
}

@Composable
fun SmallNativeAdShimmer() {
    val shimmerColors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0))
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "shimmer"
    )
    val brush = Brush.linearGradient(colors = shimmerColors, start = Offset(translateAnim, 0f), end = Offset(translateAnim + 400f, 0f))

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAFA)).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(40.dp).height(40.dp).background(brush, shape = RoundedCornerShape(8.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).background(brush, shape = RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.width(80.dp).height(12.dp).background(brush, shape = RoundedCornerShape(4.dp)))
            }
            Box(modifier = Modifier.width(70.dp).height(36.dp).background(brush, shape = RoundedCornerShape(8.dp)))
        }
    }
}
