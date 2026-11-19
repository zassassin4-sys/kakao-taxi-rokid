package com.example.kakaotaxi_hud

data class TaxiHudData(
    val status: String,
    val plateNumber: String,
    val detail: String
)

object KakaoTaxiParser {

    private val PLATE_REGEX = Regex("""\b(\d{2,3}[가-힣]\s?\d{4})\b""")

    fun parse(title: String, text: String): TaxiHudData? {
        val fullText = "$title $text"

        val taxiKeywords = listOf("배차", "차량", "도착", "기사", "탑승", "하차", "운행", "호출")
        if (taxiKeywords.none { fullText.contains(it) }) return null

        val plateMatch = PLATE_REGEX.find(fullText)
        val plateNumber = plateMatch?.value ?: "차량 확인중"

        val status = when {
            fullText.contains("도착했습니다") || fullText.contains("곧 도착") -> "차량 도착"
            fullText.contains("배차") -> "배차 완료"
            fullText.contains("탑승") || fullText.contains("운행") -> "운행 중"
            fullText.contains("하차") || fullText.contains("완료") -> "운행 종료"
            else -> "택시 알림"
        }

        val cleanDetail = text.replace("\n", " ").take(45)

        return TaxiHudData(
            status = status,
            plateNumber = plateNumber,
            detail = cleanDetail
        )
    }
}
