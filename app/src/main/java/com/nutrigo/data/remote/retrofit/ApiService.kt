package com.nutrigo.data.remote.retrofit

import com.nutrigo.data.remote.response.ProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("product/{code}")
    fun getProduct(
        @Path("code") code: String,
        @Query("fields") fields: String = "nutriments"

    ): Call<ProductResponse>
}