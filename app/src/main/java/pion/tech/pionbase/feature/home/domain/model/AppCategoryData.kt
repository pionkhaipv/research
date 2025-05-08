package pion.tech.pionbase.feature.home.domain.model

import com.google.gson.annotations.SerializedName

data class AppCategoryData(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
)