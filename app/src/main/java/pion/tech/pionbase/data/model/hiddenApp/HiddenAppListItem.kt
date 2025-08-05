package pion.tech.pionbase.data.model.hiddenApp

sealed class HiddenAppListItem {
    data class Header(
        val title: String,
        val type: AppType,
    ) : HiddenAppListItem()

    data class AppItem(
        val app: HiddenAppUIModel,
    ) : HiddenAppListItem()
}

enum class AppType {
    USER_INSTALLED,
    SYSTEM,
}
