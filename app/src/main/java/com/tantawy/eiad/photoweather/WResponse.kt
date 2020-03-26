package com.tantawy.eiad.photoweather

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class WResponse {
    @SerializedName("base")
    @Expose
    var base: String? = null

    @SerializedName("main")
    @Expose
    var main: Main? = null

    @SerializedName("dt")
    @Expose
    var dt: Int? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("cod")
    @Expose
    var cod: Int? = null

}