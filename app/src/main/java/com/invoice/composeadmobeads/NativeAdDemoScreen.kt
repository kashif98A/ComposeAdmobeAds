package com.invoice.composeadmobeads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.compose.admobads.LargeNativeAdView
import com.compose.admobads.MediumNativeAdView
import com.compose.admobads.SmallNativeAdView

@Composable
fun NativeAdDemoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Native Ad Screen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Native ads match the look and feel of your app.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Large Native Ad",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LargeNativeAdView(
            isEnabled = true,
            adUnitId = "ca-app-pub-3940256099942544/2247696110",
            onAdLoaded = { },
            onAdFailed = { }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Medium Native Ad",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        MediumNativeAdView(
            isEnabled = true,
            adUnitId = "ca-app-pub-3940256099942544/2247696110",
            onAdLoaded = { },
            onAdFailed = { }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Small Native Ad",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        SmallNativeAdView(
            isEnabled = true,
            adUnitId = "ca-app-pub-3940256099942544/2247696110",
            onAdLoaded = { },
            onAdFailed = { }
        )
    }
}
