package com.compose.admobads

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import android.os.Bundle
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ads.mediation.admob.AdMobAdapter
import androidx.compose.runtime.DisposableEffect
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

@Composable
fun BannerAdView(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/9214589741",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val isConnected = isInternetAvailable(context)

    // No internet = don't show anything
    if (!isConnected) return

    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    ) {
        // Ad View
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    this.adUnitId = adUnitId

                    // Get adaptive banner size for full width
                    val display = (context as? android.app.Activity)?.windowManager?.defaultDisplay
                    val outMetrics = android.util.DisplayMetrics()
                    display?.getMetrics(outMetrics)
                    val widthPixels = outMetrics.widthPixels.toFloat()
                    val density = outMetrics.density
                    val adWidth = (widthPixels / density).toInt()
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth))

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoading = false
                            onAdLoaded()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            isLoading = false
                            onAdFailed(adError)
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }
            }
        )

        // Show shimmer while loading
        if (isLoading) {
            BannerShimmerEffect()
        }
    }
}

@Composable
fun CollapsibleBannerAdView(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/9214589741",
    collapsiblePosition: String = "bottom",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val isConnected = isInternetAvailable(context)

    // No internet = don't show anything
    if (!isConnected) return

    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Shimmer while loading
        if (isLoading) {
            BannerShimmerEffect()
        }

        // Ad View
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    this.adUnitId = adUnitId

                    val display = (context as? android.app.Activity)?.windowManager?.defaultDisplay
                    val outMetrics = android.util.DisplayMetrics()
                    display?.getMetrics(outMetrics)
                    val widthPixels = outMetrics.widthPixels.toFloat()
                    val density = outMetrics.density
                    val adWidth = (widthPixels / density).toInt()
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth))

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoading = false
                            onAdLoaded()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            isLoading = false
                            onAdFailed(adError)
                        }
                    }

                    // Add collapsible extras
                    val extras = Bundle()
                    extras.putString("collapsible", collapsiblePosition)

                    val adRequest = AdRequest.Builder()
                        .addNetworkExtrasBundle(
                            AdMobAdapter::class.java,
                            extras
                        )
                        .build()

                    loadAd(adRequest)
                }
            }
        )
    }
}

@Composable
fun CenterBannerAdView(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val isConnected = isInternetAvailable(context)

    // No internet = don't show anything
    if (!isConnected) return

    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Shimmer while loading
        if (isLoading) {
            MediumRectangleShimmerEffect()
        }

        // Ad View - 300x250 Medium Rectangle
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    this.adUnitId = adUnitId

                    // Set fixed 300x250 size (Medium Rectangle)
                    setAdSize(AdSize.MEDIUM_RECTANGLE)

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoading = false
                            onAdLoaded()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            isLoading = false
                            onAdFailed(adError)
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@Composable
fun TopCollapsibleBanner(
    adUnitId: String = "ca-app-pub-3940256099942544/9214589741",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Shimmer while loading
        if (isLoading) {
            TopBannerShimmer()
        }

        // AdMob Banner View
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    this.adUnitId = adUnitId

                    // Get adaptive banner size
                    val display = (context as? android.app.Activity)?.windowManager?.defaultDisplay
                    val outMetrics = android.util.DisplayMetrics()
                    display?.getMetrics(outMetrics)
                    val widthPixels = outMetrics.widthPixels.toFloat()
                    val density = outMetrics.density
                    val adWidth = (widthPixels / density).toInt()
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth))

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoading = false
                            onAdLoaded()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            isLoading = false
                            onAdFailed(adError)
                        }
                    }

                    // Add collapsible extras for top position
                    val extras = Bundle()
                    extras.putString("collapsible", "top")

                    val adRequest = AdRequest.Builder()
                        .addNetworkExtrasBundle(
                            AdMobAdapter::class.java,
                            extras
                        )
                        .build()

                    loadAd(adRequest)
                }
            }
        )
    }
}

@Composable
fun InlineMediumNativeAd(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/2247696110",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val isConnected = isInternetAvailable(context)

    // No internet = don't show anything
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        if (isLoading) {
            InlineMediumNativeAdShimmer()
        } else {
            nativeAd?.let { ad ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "\uD83D\uDCF1", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Content
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Headline + Ad Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ad.headline ?: "Ad Title",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .border(0.5.dp, Color(0xFFBDBDBD), RoundedCornerShape(3.dp))
                                    .background(Color(0xFFFFF9C4), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Ad",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF424242)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Rating
                        ad.starRating?.let { rating ->
                            Text(
                                text = "\u2605".repeat(rating.toInt()) + "\u2606".repeat(5 - rating.toInt()),
                                color = Color(0xFF00BCD4),
                                fontSize = 14.sp
                            )
                        } ?: Text(
                            text = "\u2605\u2605\u2605\u2605\u2605",
                            color = Color(0xFF00BCD4),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Body
                        Text(
                            text = ad.body ?: "Description of the ad",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // CTA Button
                    Button(
                        onClick = { },
                        modifier = Modifier.height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = ad.callToAction ?: "Install",
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Rectangular Banner Ad (300x250 Medium Rectangle)
 * Perfect for in-feed placements and content breaks.
 */
@Composable
fun RectangularBannerAd(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val isConnected = isInternetAvailable(context)

    if (!isConnected) return

    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            MediumRectangleShimmerEffect()
        }

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    this.adUnitId = adUnitId
                    setAdSize(AdSize.MEDIUM_RECTANGLE)

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoading = false
                            onAdLoaded()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            isLoading = false
                            onAdFailed(adError)
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

/**
 * Bottom Collapsible Banner Ad.
 * Anchored at the bottom of the screen, collapses after user interaction.
 */
@Composable
fun BottomCollapsibleBanner(
    adUnitId: String = "ca-app-pub-3940256099942544/9214589741",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val isConnected = isInternetAvailable(context)

    if (!isConnected) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        if (isLoading) {
            BannerShimmerEffect()
        }

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    this.adUnitId = adUnitId

                    val display = (context as? android.app.Activity)?.windowManager?.defaultDisplay
                    val outMetrics = android.util.DisplayMetrics()
                    display?.getMetrics(outMetrics)
                    val widthPixels = outMetrics.widthPixels.toFloat()
                    val density = outMetrics.density
                    val adWidth = (widthPixels / density).toInt()
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth))

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoading = false
                            onAdLoaded()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            isLoading = false
                            onAdFailed(adError)
                        }
                    }

                    val extras = Bundle()
                    extras.putString("collapsible", "bottom")

                    val adRequest = AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                        .build()

                    loadAd(adRequest)
                }
            }
        )
    }
}

/**
 * Large Banner Ad (320x100).
 * Twice the height of a standard banner.
 */
@Composable
fun LargeBannerAd(
    isEnabled: Boolean = true,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    onAdLoaded: () -> Unit = {},
    onAdFailed: (LoadAdError) -> Unit = {}
) {
    if (!isEnabled) return

    val context = androidx.compose.ui.platform.LocalContext.current
    val isConnected = isInternetAvailable(context)

    if (!isConnected) return

    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            BannerShimmerEffect()
        }

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    this.adUnitId = adUnitId
                    setAdSize(AdSize.LARGE_BANNER)

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isLoading = false
                            onAdLoaded()
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            isLoading = false
                            onAdFailed(adError)
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

// Shimmer Effects

@Composable
fun BannerShimmerEffect() {
    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 400f, 0f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFFF0F0F0))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .height(40.dp)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .height(32.dp)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun MediumRectangleShimmerEffect() {
    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 400f, 0f)
    )

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(250.dp)
            .background(Color(0xFFF0F0F0))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(brush, shape = RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .background(brush, shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .background(brush, shape = RoundedCornerShape(4.dp))
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(brush, shape = RoundedCornerShape(6.dp))
            )
        }
    }
}

@Composable
fun TopBannerShimmer() {
    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 400f, 0f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFFF0F0F0))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .height(50.dp)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .height(40.dp)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun InlineMediumNativeAdShimmer() {
    val shimmerColors = listOf(
        Color(0xFFE0E0E0),
        Color(0xFFF5F5F5),
        Color(0xFFE0E0E0)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 400f, 0f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(brush, RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .background(brush, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(14.dp)
                    .background(brush, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(brush, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .width(70.dp)
                .height(40.dp)
                .background(brush, RoundedCornerShape(6.dp))
        )
    }
}
