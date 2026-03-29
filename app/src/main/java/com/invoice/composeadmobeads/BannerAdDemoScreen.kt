package com.invoice.composeadmobeads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.compose.admobads.BannerAdView
import com.compose.admobads.CenterBannerAdView
import com.compose.admobads.CollapsibleBannerAdView
import com.compose.admobads.InlineMediumNativeAd

@Composable
fun BannerAdDemoScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            BannerAdView(
                isEnabled = true,
                adUnitId = "ca-app-pub-3940256099942544/9214589741",
                onAdLoaded = { },
                onAdFailed = { }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Banner Ad Screen",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = "Banner ads are rectangular ads that appear at the top or bottom of the screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text(text = "Banner Ads Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Banner ads are displayed at the top and bottom of the screen. They load automatically and stay visible while scrolling.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                CenterBannerAdView(
                    isEnabled = true,
                    adUnitId = "ca-app-pub-3940256099942544/6300978111",
                    onAdLoaded = { },
                    onAdFailed = { }
                )

                Spacer(modifier = Modifier.height(24.dp))

                InlineMediumNativeAd(
                    isEnabled = true,
                    adUnitId = "ca-app-pub-3940256099942544/2247696110",
                    onAdLoaded = { },
                    onAdFailed = { }
                )

                Spacer(modifier = Modifier.height(200.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            CollapsibleBannerAdView(
                isEnabled = true,
                adUnitId = "ca-app-pub-3940256099942544/9214589741",
                collapsiblePosition = "bottom",
                onAdLoaded = { },
                onAdFailed = { }
            )
        }
    }
}
