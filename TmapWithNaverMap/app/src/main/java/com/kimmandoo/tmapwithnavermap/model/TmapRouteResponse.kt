package com.kimmandoo.tmapwithnavermap.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmapRouteResponse(
    @SerialName("metaData")
    val metaData: MetaData
) {
    @Serializable
    data class MetaData(
        @SerialName("plan")
        val plan: Plan,
        @SerialName("requestParameters")
        val requestParameters: RequestParameters
    ) {
        @Serializable
        data class Plan(
            @SerialName("itineraries")
            val itineraries: List<Itinerary>
        ) {
            @Serializable
            data class Itinerary(
                @SerialName("fare")
                val fare: Fare,
                @SerialName("legs")
                val legs: List<Leg>,
                @SerialName("pathType")
                val pathType: Int,
                @SerialName("totalDistance")
                val totalDistance: Int,
                @SerialName("totalTime")
                val totalTime: Int,
                @SerialName("totalWalkDistance")
                val totalWalkDistance: Int,
                @SerialName("totalWalkTime")
                val totalWalkTime: Int,
                @SerialName("transferCount")
                val transferCount: Int
            ) {
                @Serializable
                data class Fare(
                    @SerialName("regular")
                    val regular: Regular
                ) {
                    @Serializable
                    data class Regular(
                        @SerialName("currency")
                        val currency: Currency,
                        @SerialName("totalFare")
                        val totalFare: Int
                    ) {
                        @Serializable
                        data class Currency(
                            @SerialName("currency")
                            val currency: String,
                            @SerialName("currencyCode")
                            val currencyCode: String,
                            @SerialName("symbol")
                            val symbol: String
                        )
                    }
                }

                @Serializable
                data class Leg(
                    @SerialName("distance")
                    val distance: Int,
                    @SerialName("end")
                    val end: End,
                    @SerialName("Lane")
                    val lane: List<Lane>? = null,
                    @SerialName("mode")
                    val mode: String,
                    @SerialName("passShape")
                    val passShape: PassShape? = null,
                    @SerialName("passStopList")
                    val passStopList: PassStopList? = null,
                    @SerialName("route")
                    val route: String? = null,
                    @SerialName("routeColor")
                    val routeColor: String? = null,
                    @SerialName("routeId")
                    val routeId: String? = null,
                    @SerialName("sectionTime")
                    val sectionTime: Int,
                    @SerialName("service")
                    val service: Int? = null,
                    @SerialName("start")
                    val start: Start,
                    @SerialName("steps")
                    val steps: List<Step>? = null,
                    @SerialName("type")
                    val type: Int? = null,
                ) {
                    @Serializable
                    data class End(
                        @SerialName("lat")
                        val lat: Double,
                        @SerialName("lon")
                        val lon: Double,
                        @SerialName("name")
                        val name: String
                    )

                    @Serializable
                    data class Lane(
                        @SerialName("route")
                        val route: String,
                        @SerialName("routeColor")
                        val routeColor: String,
                        @SerialName("routeId")
                        val routeId: String,
                        @SerialName("service")
                        val service: Int,
                        @SerialName("type")
                        val type: Int
                    )

                    @Serializable
                    data class PassShape(
                        @SerialName("linestring")
                        val linestring: String
                    )

                    @Serializable
                    data class PassStopList(
                        @SerialName("stationList")
                        val stationList: List<Station>
                    ) {
                        @Serializable
                        data class Station(
                            @SerialName("index")
                            val index: Int,
                            @SerialName("lat")
                            val lat: String,
                            @SerialName("lon")
                            val lon: String,
                            @SerialName("stationID")
                            val stationID: String,
                            @SerialName("stationName")
                            val stationName: String
                        )
                    }

                    @Serializable
                    data class Start(
                        @SerialName("lat")
                        val lat: Double,
                        @SerialName("lon")
                        val lon: Double,
                        @SerialName("name")
                        val name: String
                    )

                    @Serializable
                    data class Step(
                        @SerialName("description")
                        val description: String,
                        @SerialName("distance")
                        val distance: Int,
                        @SerialName("linestring")
                        val linestring: String,
                        @SerialName("streetName")
                        val streetName: String
                    )
                }
            }
        }

        @Serializable
        data class RequestParameters(
            @SerialName("airplaneCount")
            val airplaneCount: Int,
            @SerialName("busCount")
            val busCount: Int,
            @SerialName("endX")
            val endX: String,
            @SerialName("endY")
            val endY: String,
            @SerialName("expressbusCount")
            val expressbusCount: Int,
            @SerialName("ferryCount")
            val ferryCount: Int,
            @SerialName("locale")
            val locale: String,
            @SerialName("reqDttm")
            val reqDttm: String,
            @SerialName("startX")
            val startX: String,
            @SerialName("startY")
            val startY: String,
            @SerialName("subwayBusCount")
            val subwayBusCount: Int,
            @SerialName("subwayCount")
            val subwayCount: Int,
            @SerialName("trainCount")
            val trainCount: Int,
            @SerialName("wideareaRouteCount")
            val wideareaRouteCount: Int
        )
    }
}