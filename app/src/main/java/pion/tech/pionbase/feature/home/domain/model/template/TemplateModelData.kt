package pion.tech.pionbase.feature.home.domain.model.template

import com.google.gson.annotations.SerializedName

data class TemplateModelData(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("is_pro")
    val isPro: Boolean? = null,
    @SerializedName("custom_fields")
    val customField: TemplateData
)


