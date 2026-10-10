package com.examprep.android.ads

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import timber.log.Timber

// ─────────────────────────────────────────────────────────────────────────────
// AD UNIT IDs
//
// TEST IDs: These are Google's official test ad unit IDs.
// They display real test ads and will NOT generate real revenue.
// Replace with your REAL ad unit IDs from https://apps.admob.com before release.
// ─────────────────────────────────────────────────────────────────────────────
object AdUnitIds {
    // TODO: Replace with real Ad Unit IDs from AdMob console before release
    const val BANNER_DASHBOARD    = "ca-app-pub-3940256099942544/6300978111" // Test banner
    const val BANNER_QUIZ_END     = "ca-app-pub-3940256099942544/6300978111" // Test banner
    const val INTERSTITIAL_QUIZ   = "ca-app-pub-3940256099942544/1033173712" // Test interstitial
    const val INTERSTITIAL_MOCK   = "ca-app-pub-3940256099942544/1033173712" // Test interstitial
}

// ─────────────────────────────────────────────────────────────────────────────
// BANNER AD COMPOSABLE
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A Compose-compatible banner ad view.
 *
 * Architecture notes:
 * - Uses AndroidView to bridge the legacy AdView into Compose
 * - Respects Compose lifecycle — ad is destroyed when composable leaves composition
 * - Only shown to FREE tier users; premium users see no ads
 *
 * @param adUnitId  The AdMob ad unit ID (use [AdUnitIds] constants)
 * @param modifier  Standard Compose modifier
 */
@Composable
fun BannerAdView(
    adUnitId: String = AdUnitIds.BANNER_DASHBOARD,
    isPersonalized: Boolean = false,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
) {
    val adView = remember { mutableStateOf<AdView?>(null) }

    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                val extras = android.os.Bundle().apply {
                    if (!isPersonalized) putString("npa", "1")
                }
                val request = AdRequest.Builder()
                    .addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java, extras)
                    .build()
                loadAd(request)
                adView.value = this
            }
        },
        modifier = modifier,
        onRelease = { view ->
            view.destroy()
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            adView.value?.destroy()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// INTERSTITIAL AD MANAGER
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Manages loading and displaying interstitial ads.
 *
 * Usage:
 *   val adManager = remember { InterstitialAdManager(context) }
 *   LaunchedEffect(Unit) { adManager.load("ca-app-pub-XXXX") }
 *   adManager.show(activity)  // Call at natural breakpoint (e.g., after quiz result)
 *
 * Design decision: Pre-loads the ad so it's ready instantly when needed.
 * Never show an interstitial mid-session — only at natural content boundaries.
 */
class InterstitialAdManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun load(adUnitId: String = AdUnitIds.INTERSTITIAL_QUIZ, isPersonalized: Boolean = false) {
        if (isLoading || interstitialAd != null) return
        isLoading = true

        val extras = android.os.Bundle().apply {
            if (!isPersonalized) putString("npa", "1")
        }
        val request = AdRequest.Builder()
            .addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter::class.java, extras)
            .build()

        InterstitialAd.load(
            context,
            adUnitId,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Timber.d("InterstitialAd loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Timber.w("InterstitialAd failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows the interstitial ad if ready. After the ad closes, pre-loads the next one.
     *
     * @param activity  The Activity context required by the interstitial SDK.
     * @param onAdClosed  Called when the ad closes (or if no ad was ready).
     */
    fun show(
        activity: android.app.Activity,
        adUnitId: String = AdUnitIds.INTERSTITIAL_QUIZ,
        onAdClosed: () -> Unit = {}
    ) {
        val ad = interstitialAd
        if (ad == null) {
            Timber.d("InterstitialAd not ready — proceeding without ad")
            onAdClosed()
            load(adUnitId) // Pre-load for next time
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onAdClosed()
                load(adUnitId) // Pre-load for next time
            }

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                interstitialAd = null
                onAdClosed()
                Timber.w("InterstitialAd failed to show: ${error.message}")
            }
        }

        ad.show(activity)
    }

    fun isReady(): Boolean = interstitialAd != null

    fun destroy() {
        interstitialAd = null
    }
}
