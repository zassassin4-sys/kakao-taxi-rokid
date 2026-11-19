package com.example.kakaotaxi_hud

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 100, 60, 60)
        }

        statusText = TextView(this).apply {
            textSize = 16f
            setPadding(0, 0, 0, 40)
        }

        val btnPermission = Button(this).apply {
            text = "1. 알림 접근 권한 설정 열기"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }

        val btnTestHud = Button(this).apply {
            text = "2. 배차 완료 HUD 테스트 발송"
            setOnClickListener {
                val testSample = KakaoTaxiParser.parse(
                    title = "카카오 T 배차 완료",
                    text = "123가 4567 (소나타) 차량이 배차되었습니다. 3분 후 도착 예정"
                )
                if (testSample != null) {
                    val intent = Intent("com.anezium.rokid.nexus.ACTION_SHOW_HUD").apply {
                        putExtra("plugin_id", "kakao_taxi_plate")
                        putExtra("title", "🚕 ${testSample.status} | ${testSample.plateNumber}")
                        putExtra("body", testSample.detail)
                    }
                    sendBroadcast(intent)
                    Toast.makeText(this@MainActivity, "테스트 전송됨: ${testSample.plateNumber}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        layout.addView(statusText)
        layout.addView(btnPermission)
        layout.addView(btnTestHud)
        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        val isGranted = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
        statusText.text = if (isGranted) {
            "✅ 알림 접근 권한이 활성화되어 있습니다.\n카카오 T 알림 발생 시 자동으로 HUD에 전송됩니다."
        } else {
            "⚠️ 알림 접근 권한이 꺼져 있습니다.\n아래 버튼을 눌러 권한을 허용해 주세요."
        }
    }
}
