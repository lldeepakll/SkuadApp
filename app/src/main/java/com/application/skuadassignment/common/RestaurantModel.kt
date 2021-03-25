package com.application.skuadassignment.common

class RestaurantModel {

    var results : ArrayList<Restaurant> = ArrayList()

    class Restaurant{

        var name : String ? = null
        var vicinity : String ? = null
        var rating : Float ? = null

        var opening_hours : OpeningHour ?= null

    }

    class OpeningHour{

        var open_now : Boolean ? = null

    }

}