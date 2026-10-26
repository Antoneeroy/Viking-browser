package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("vidking_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getThemeModeInternal())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _adBlockerEnabled = MutableStateFlow(prefs.getBoolean(KEY_AD_BLOCKER, true))
    val adBlockerEnabled: StateFlow<Boolean> = _adBlockerEnabled.asStateFlow()

    private val _blockedAdsCount = MutableStateFlow(prefs.getInt(KEY_BLOCKED_COUNT, 248))
    val blockedAdsCount: StateFlow<Int> = _blockedAdsCount.asStateFlow()

    private val _vipUnlocked = MutableStateFlow(prefs.getBoolean(KEY_VIP_UNLOCKED, true)) // Default active VIP tier for full power
    val vipUnlocked: StateFlow<Boolean> = _vipUnlocked.asStateFlow()

    private val _vipCoins = MutableStateFlow(prefs.getInt(KEY_VIP_COINS, 650))
    val vipCoins: StateFlow<Int> = _vipCoins.asStateFlow()

    private val _checkInStreak = MutableStateFlow(prefs.getInt(KEY_CHECK_IN_STREAK, 3))
    val checkInStreak: StateFlow<Int> = _checkInStreak.asStateFlow()

    private val _hasCheckedInToday = MutableStateFlow(prefs.getBoolean(KEY_CHECKED_TODAY, false))
    val hasCheckedInToday: StateFlow<Boolean> = _hasCheckedInToday.asStateFlow()

    private val _vaultPin = MutableStateFlow(prefs.getString(KEY_VAULT_PIN, "1234") ?: "1234")
    val vaultPin: StateFlow<String> = _vaultPin.asStateFlow()

    private val _stealthCalculatorMode = MutableStateFlow(prefs.getBoolean(KEY_STEALTH_MODE, true))
    val stealthCalculatorMode: StateFlow<Boolean> = _stealthCalculatorMode.asStateFlow()

    private val _speedLimitEnabled = MutableStateFlow(prefs.getBoolean(KEY_SPEED_LIMIT_ENABLED, false))
    val speedLimitEnabled: StateFlow<Boolean> = _speedLimitEnabled.asStateFlow()

    private val _speedLimitMb = MutableStateFlow(prefs.getInt(KEY_SPEED_LIMIT_MB, 5))
    val speedLimitMb: StateFlow<Int> = _speedLimitMb.asStateFlow()

    private val _maxConcurrentDownloads = MutableStateFlow(prefs.getInt(KEY_MAX_CONCURRENT, 4))
    val maxConcurrentDownloads: StateFlow<Int> = _maxConcurrentDownloads.asStateFlow()

    private val _wifiOnly = MutableStateFlow(prefs.getBoolean(KEY_WIFI_ONLY, false))
    val wifiOnly: StateFlow<Boolean> = _wifiOnly.asStateFlow()

    private val _nightOwlSchedule = MutableStateFlow(prefs.getBoolean(KEY_NIGHT_OWL, false))
    val nightOwlSchedule: StateFlow<Boolean> = _nightOwlSchedule.asStateFlow()

    private val _searchEngine = MutableStateFlow(prefs.getString(KEY_SEARCH_ENGINE, "Google") ?: "Google")
    val searchEngine: StateFlow<String> = _searchEngine.asStateFlow()

    private val _debugOverlayEnabled = MutableStateFlow(prefs.getBoolean(KEY_DEBUG_OVERLAY, false))
    val debugOverlayEnabled: StateFlow<Boolean> = _debugOverlayEnabled.asStateFlow()

    private val _adultSitesAllowed = MutableStateFlow(prefs.getBoolean(KEY_ADULT_SITES_ALLOWED, false))
    val adultSitesAllowed: StateFlow<Boolean> = _adultSitesAllowed.asStateFlow()

    private fun getThemeModeInternal(): AppThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name)
        return try {
            AppThemeMode.valueOf(name ?: AppThemeMode.DARK.name)
        } catch (_: Exception) {
            AppThemeMode.DARK
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setAdBlockerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AD_BLOCKER, enabled).apply()
        _adBlockerEnabled.value = enabled
    }

    fun incrementBlockedAds(count: Int = 1) {
        val newCount = _blockedAdsCount.value + count
        prefs.edit().putInt(KEY_BLOCKED_COUNT, newCount).apply()
        _blockedAdsCount.value = newCount
    }

    fun setVaultPin(pin: String) {
        prefs.edit().putString(KEY_VAULT_PIN, pin).apply()
        _vaultPin.value = pin
    }

    fun setStealthCalculatorMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_STEALTH_MODE, enabled).apply()
        _stealthCalculatorMode.value = enabled
    }

    fun setSpeedLimitEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SPEED_LIMIT_ENABLED, enabled).apply()
        _speedLimitEnabled.value = enabled
    }

    fun setSpeedLimitMb(mb: Int) {
        prefs.edit().putInt(KEY_SPEED_LIMIT_MB, mb).apply()
        _speedLimitMb.value = mb
    }

    fun setMaxConcurrentDownloads(count: Int) {
        prefs.edit().putInt(KEY_MAX_CONCURRENT, count).apply()
        _maxConcurrentDownloads.value = count
    }

    fun setWifiOnly(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WIFI_ONLY, enabled).apply()
        _wifiOnly.value = enabled
    }

    fun setNightOwlSchedule(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NIGHT_OWL, enabled).apply()
        _nightOwlSchedule.value = enabled
    }

    fun setSearchEngine(engine: String) {
        prefs.edit().putString(KEY_SEARCH_ENGINE, engine).apply()
        _searchEngine.value = engine
    }

    fun setDebugOverlayEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEBUG_OVERLAY, enabled).apply()
        _debugOverlayEnabled.value = enabled
    }

    fun claimDailyCheckIn(): Boolean {
        if (_hasCheckedInToday.value) return false
        val newStreak = _checkInStreak.value + 1
        val newCoins = _vipCoins.value + (50 * newStreak)
        prefs.edit()
            .putBoolean(KEY_CHECKED_TODAY, true)
            .putInt(KEY_CHECK_IN_STREAK, newStreak)
            .putInt(KEY_VIP_COINS, newCoins)
            .apply()
        _hasCheckedInToday.value = true
        _checkInStreak.value = newStreak
        _vipCoins.value = newCoins
        return true
    }

    fun redeemPromoCode(code: String): Boolean {
        val clean = code.trim().uppercase()
        if (clean == "VIPKING" || clean == "FAST2026" || clean == "PRODOWNLOADER") {
            val newCoins = _vipCoins.value + 500
            prefs.edit().putBoolean(KEY_VIP_UNLOCKED, true).putInt(KEY_VIP_COINS, newCoins).apply()
            _vipUnlocked.value = true
            _vipCoins.value = newCoins
            return true
        }
        return false
    }

    fun setAdultSitesAllowed(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ADULT_SITES_ALLOWED, enabled).apply()
        _adultSitesAllowed.value = enabled
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_AD_BLOCKER = "ad_blocker"
        private const val KEY_BLOCKED_COUNT = "blocked_count"
        private const val KEY_VIP_UNLOCKED = "vip_unlocked"
        private const val KEY_VIP_COINS = "vip_coins"
        private const val KEY_CHECK_IN_STREAK = "check_in_streak"
        private const val KEY_CHECKED_TODAY = "checked_today"
        private const val KEY_VAULT_PIN = "vault_pin"
        private const val KEY_STEALTH_MODE = "stealth_calculator_mode"
        private const val KEY_SPEED_LIMIT_ENABLED = "speed_limit_enabled"
        private const val KEY_SPEED_LIMIT_MB = "speed_limit_mb"
        private const val KEY_MAX_CONCURRENT = "max_concurrent"
        private const val KEY_WIFI_ONLY = "wifi_only"
        private const val KEY_NIGHT_OWL = "night_owl"
        private const val KEY_SEARCH_ENGINE = "search_engine"
        private const val KEY_DEBUG_OVERLAY = "debug_overlay"
        private const val KEY_ADULT_SITES_ALLOWED = "adult_sites_allowed"
    }
}
