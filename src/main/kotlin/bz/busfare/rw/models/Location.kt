package bz.busfare.rw.models

import kotlin.math.*

sealed class Location(lat: Double, lon: Double) : LatLng(lat, lon) {

    data object SARTENEJA : Location(18.35416317226022, -88.14326985560284)
    data object DANGRIGA : Location(16.96119696086447, -88.22101175814998)
    data object INDEPENDENCE : Location(16.535297751343315, -88.4235380481256)
    data object PUNTA_GORDA : Location(16.101615258446266, -88.80166814717033)
    data object PLACENCIA : Location(16.51723726403817, -88.36778542054674)
    data object DUMP : Location(16.22748609210673, -88.93543518276314) //TODO: Unsure
    data object SAN_FELIPE : Location(17.875790012549214, -88.77115496882273)
    data object SANTA_ELENA_BORDER : Location(18.485727974984478, -88.39923300734948) //TODO: Unsure
    data object COROZAL : Location(18.392946664855796, -88.3882605842278)
    data object GUINEA_GRASS : Location(17.968886390198247, -88.59792985973769) //TODO: Unsure
    data object BURREL_BOOM_JUNCTION : Location(17.57111482625365, -88.42377024198221) //TODO:  Unsure
    data object PALLOTI_JUNCTION : Location(17.50547102994152, -88.19637085450118) //TODO: Unsure
    data object BELMOPAN : Location(17.25038008829335, -88.77492098246258)
    data object SAN_IGNACIO : Location(17.159253465007172, -89.06938364368425)
    data object BENQUE_VIEJO : Location(17.078365907265745, -89.13715537384982)
    data object BELIZE_CITY : Location(17.49536557776557, -88.19316242696863)
    data object ORANGE_WALK : Location(18.080414769060454, -88.5644522730042)

    val earthRadiusKm: Double = 6372.8

    /**
     * Haversine formula. Giving great-circle distances between two points on a sphere from their longitudes and latitudes.
     * It is a special case of a more general formula in spherical trigonometry, the law of haversines, relating the
     * sides and angles of spherical "triangles".
     *
     * https://rosettacode.org/wiki/Haversine_formula#Java
     *
     * @return Distance in kilometers
     */
    fun haversine(lat: Double, lon: Double, lat_2: Double, lon_2: Double): Double {
        val dLat = Math.toRadians(lat_2 - lat)
        val dLon = Math.toRadians(lon_2 - lon)
        val originLat = Math.toRadians(lat)
        val destinationLat = Math.toRadians(lat_2)

        val a =
            sin(dLat / 2).pow(2.toDouble()) + sin(dLon / 2).pow(2.toDouble()) * cos(originLat) * cos(
                destinationLat
            )
        val c = 2 * asin(sqrt(a))
        return earthRadiusKm * c
    }

    fun haversine(latLng: LatLng, latLng2: LatLng): Double =
        haversine(latLng.lat, latLng.lon, latLng2.lat, latLng2.lon)

}