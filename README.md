# Compose AdMob Ads

[![](https://jitpack.io/v/kashif98A/ComposeAdmobeAds.svg)](https://jitpack.io/#kashif98A/ComposeAdmobeAds)

A Jetpack Compose library for easy Google AdMob integration with beautiful shimmer loading effects, GDPR consent management, and multiple ad formats.

## Features

- **Banner Ads** - Adaptive and collapsible banner ads
- **Interstitial Ads** - Full-screen ads with loading dialog
- **Native Ads** - Large, Medium, and Small native ad layouts
- **App Open Ads** - Ads shown on app launch/resume
- **Fullscreen Native Ads** - Premium native ad presentation
- **Consent Management** - GDPR-compliant UMP integration
- **Shimmer Effects** - Beautiful loading placeholders for all ad types
- **Network Awareness** - Graceful handling when offline

## Installation

### Step 1: Add JitPack repository

Add JitPack to your project-level `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add the dependency

Add to your app-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.kashif98A:ComposeAdmobeAds:1.0.0")
}
```

## Usage

### Banner Ad

```kotlin
BannerAdView(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { /* Ad loaded */ },
    onAdFailed = { error -> /* Handle error */ }
)
```

### Collapsible Banner Ad

```kotlin
CollapsibleBannerAdView(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    collapsiblePosition = "bottom", // or "top"
    onAdLoaded = { },
    onAdFailed = { }
)
```

### Top Collapsible Banner

```kotlin
TopCollapsibleBanner(
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { },
    onAdFailed = { }
)
```

### Medium Rectangle Banner

```kotlin
CenterBannerAdView(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { },
    onAdFailed = { }
)
```

### Interstitial Ad

```kotlin
InterstitialAdScreen(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { },
    onAdFailed = { error -> },
    onAdDismissed = { }
)
```

### Native Ads

```kotlin
// Large Native Ad
LargeNativeAdView(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { },
    onAdFailed = { }
)

// Medium Native Ad
MediumNativeAdView(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { },
    onAdFailed = { }
)

// Small Native Ad
SmallNativeAdView(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { },
    onAdFailed = { }
)
```

### App Open Ad

```kotlin
AppOpenAdScreen(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { },
    onAdFailed = { },
    onAdDismissed = { }
)
```

### Fullscreen Native Ad

```kotlin
FullscreenNativeAdScreen(
    isEnabled = true,
    adUnitId = "your-ad-unit-id",
    onAdLoaded = { },
    onAdFailed = { }
)
```

### Consent Management

```kotlin
// Using the consent wrapper
AdsConsentWrapper(
    enableDebug = true,
    testDevice = "YOUR-TEST-DEVICE-ID"
) { canShowAds ->
    if (canShowAds) {
        BannerAdView(adUnitId = "your-ad-unit-id")
    }
}

// Or use the consent state directly
val consentState = rememberAdsConsentState()

// Show consent screen
ConsentScreen()

// Privacy options button
PrivacyOptionsButton {
    // Handle click
}
```

### Network Utilities

```kotlin
// Check internet availability
val isConnected = isInternetAvailable(context)

// Observe network state
val networkState by rememberNetworkState()

// Network-aware content
NetworkAwareContent(
    showWhenOffline = { Text("No internet connection") }
) {
    // Your content when online
}
```

## Setup

### AndroidManifest.xml

Add your AdMob App ID to your app's `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
```

### Initialize Mobile Ads SDK

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
    }
}
```

## Requirements

- Min SDK: 24
- Compile SDK: 36
- Jetpack Compose
- Google Play Services Ads 23.6.0

## License

```
MIT License

Copyright (c) 2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software.
```
