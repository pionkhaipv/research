package pion.tech.pionbase.data.remote

import pion.tech.pionbase.app.data.model.ApiObjectResponseData
import pion.tech.pionbase.data.model.appCategory.AppCategoryDto
import pion.tech.pionbase.data.model.template.TemplateModelDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("api/v5.0/public/categories?app_id=56ba3e1f-27a4-4acd-b420-1b33600ac495")
    suspend fun getAppCategory(): ApiObjectResponseData<List<AppCategoryDto>>

    @GET("api/v5.0/public/items/get-all?region_code=%7Bregion_code%7D")
    suspend fun getAllTemplate(
        @Query("category_id") categoryId: String,
    ): ApiObjectResponseData<List<TemplateModelDto>>
}
