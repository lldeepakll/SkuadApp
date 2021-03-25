package com.application.efx.networking

import com.application.skuadassignment.common.RestaurantModel
import io.reactivex.Single
import retrofit2.http.*

//by : Deepak Kumar

interface ApiService {

    @GET("place/nearbysearch/json")
    fun getResturants(@Query("location") location : String,
                       @Query("radius") radius : String,
    @Query("type") type : String,
    @Query("key") key : String) : Single<RestaurantModel>

}