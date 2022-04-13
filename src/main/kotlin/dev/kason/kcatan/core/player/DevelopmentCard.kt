package dev.kason.kcatan.core.player

enum class DevCard {
    KNIGHT,
    VICTORY,
    ROAD_BUILDING,
    YEAR_OF_PLENTY,
    MONOPOLY;

    fun formalName() = when (this) {
        KNIGHT -> "Knight"
        VICTORY -> "Victory"
        ROAD_BUILDING -> "Road Building"
        YEAR_OF_PLENTY -> "Year of Plenty"
        MONOPOLY -> "Monopoly"
    }

    val description: String
        get() = when (this) {
            KNIGHT -> "Move the robber and steal a resource from another player."
            VICTORY -> "Gain a victory point."
            ROAD_BUILDING -> "Build two roads."
            YEAR_OF_PLENTY -> "Draw two resources."
            MONOPOLY -> "Take all resources of a single type."
        }
}

fun executeKnight() {

}
