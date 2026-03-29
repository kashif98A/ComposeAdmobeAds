package com.compose.admobads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

@Composable
fun FullscreenNativeAdScreen(
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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadNativeAd() {
        isLoading = true
        errorMessage = null

        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd?.destroy()
                nativeAd = ad
                isLoading = false
                errorMessage = null
                onAdLoaded()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoading = false
                    errorMessage = "Failed to load: ${adError.message}"
                    onAdFailed(adError)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setRequestMultipleImages(true)
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    DisposableEffect(Unit) {
        loadNativeAd()
        onDispose { nativeAd?.destroy() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            FullscreenNativeAdShimmer()
        }

        AnimatedVisibility(
            visible = !isLoading && nativeAd != null,
            enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                initialOffsetY = { it / 10 },
                animationSpec = tween(400)
            ),
            exit = fadeOut()
        ) {
            nativeAd?.let { ad ->
                FullscreenNativeAdContent(
                    ad = ad,
                    onRetry = { loadNativeAd() }
                )
            }
        }

        AnimatedVisibility(
            visible = !isLoading && nativeAd == null && errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FullscreenNativeAdError(
                errorMessage = errorMessage,
                onRetry = { loadNativeAd() }
            )
        }
    }
}

@Composable
fun FullscreenNativeAdError(errorMessage: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unable to load ad",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))
        errorMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

@Composable
fun FullscreenNativeAdContent(ad: NativeAd, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                ad.images.firstOrNull()?.let { image ->
                    Image(
                        painter = rememberAsyncImagePainter(image.uri),
                        contentDescription = "Ad Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }

                    IconButton(
                        onClick = onRetry,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Ad",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp)
                        .offset(y = 40.dp)
                ) {
                    Card(
                        modifier = Modifier.size(90.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        ad.icon?.let { icon ->
                            Image(
                                painter = rememberAsyncImagePainter(icon.uri),
                                contentDescription = "App Icon",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } ?: Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "App", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 50.dp)
            ) {
                Text(
                    text = ad.headline ?: "Amazing App",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = ad.advertiser ?: ad.store ?: "Google Play",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ad.starRating?.let { rating ->
                                Text(
                                    text = String.format("%.1f", rating),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(18.dp)
                                )
                            } ?: Text(
                                text = "4.5",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = "Rating", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9E9E9E))
                    }

                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE0E0E0)))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "10M+", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                        Text(text = "Downloads", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9E9E9E))
                    }

                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE0E0E0)))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = ad.price ?: "Free", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        Text(text = "Price", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9E9E9E))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                ) {
                    Text(
                        text = (ad.callToAction ?: "INSTALL").uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "About this app",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = ad.body ?: "Download this amazing app and enjoy all the features. Get started today and experience the best mobile experience!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF424242),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Contains ads", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9E9E9E))
                    Text(text = " \u2022 ", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9E9E9E))
                    Text(text = "In-app purchases", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9E9E9E))
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FullscreenNativeAdShimmer() {
    val shimmerColors = listOf(Color(0xFFE8E8E8), Color(0xFFF8F8F8), Color(0xFFE8E8E8))
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f, targetValue = 1200f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "shimmer"
    )
    val brush = Brush.linearGradient(colors = shimmerColors, start = Offset(translateAnim - 400f, 0f), end = Offset(translateAnim, 0f))

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            Box(modifier = Modifier.fillMaxSize().background(brush))
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.width(40.dp).height(22.dp).background(Color(0xFFD0D0D0), RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.size(36.dp).background(Color(0xFFD0D0D0), CircleShape))
            }
            Box(
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp).offset(y = 40.dp)
                    .size(90.dp).background(brush, RoundedCornerShape(20.dp)).border(4.dp, Color.White, RoundedCornerShape(20.dp))
            )
        }

        Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 50.dp)) {
            Box(modifier = Modifier.fillMaxWidth(0.75f).height(28.dp).background(brush, RoundedCornerShape(6.dp)))
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.width(120.dp).height(18.dp).background(brush, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                repeat(3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.width(50.dp).height(22.dp).background(brush, RoundedCornerShape(4.dp)))
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(60.dp).height(14.dp).background(brush, RoundedCornerShape(4.dp)))
                    }
                    if (it < 2) {
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE0E0E0)))
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Box(modifier = Modifier.fillMaxWidth().height(54.dp).background(brush, RoundedCornerShape(14.dp)))
            Spacer(modifier = Modifier.height(28.dp))
            Box(modifier = Modifier.width(130.dp).height(20.dp).background(brush, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(brush, RoundedCornerShape(12.dp)))
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Box(modifier = Modifier.width(180.dp).height(14.dp).background(brush, RoundedCornerShape(4.dp)))
            }
        }
    }
}
