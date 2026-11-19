package com.example.kakaotaxi_hud

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class KakaoTaxiNotificationService : NotificationListenerService() {

    companion object {
        private const val TAG = "KakaoTaxiHUD"
        private const val KAKAO_TAXI_PACKAGE = "com.kakao.taxi"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName != KAKAO_TAXI_PACKAGE) return

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()

        val parsedData = KakaoTaxiParser.parse(title, text) ?: return
        sendToRokidNexus(parsedData)
    }

    private fun sendToRokidNexus(data: TaxiHudData) {
        val hudTitle = "🚕 ${data.status} | ${data.plateNumber}"
        val hudBody = data.detail

        Log.i(TAG, "HUD 전송: $hudTitle - $hudBody")

        val intent = Intent("com.anezium.rokid.nexus.ACTION_SHOW_HUD").apply {
            putExtra("plugin_id", "kakao_taxi_plate")
            putExtra("title", hudTitle)
            putExtra("body", hudBody)
            putExtra("plate", data.plateNumber)
        }
        sendBroadcast(intent)
    }
}
