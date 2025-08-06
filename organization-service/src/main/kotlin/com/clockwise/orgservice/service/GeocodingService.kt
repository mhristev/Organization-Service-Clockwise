package com.clockwise.orgservice.service

interface GeocodingService {
    suspend fun getCoordinates(location: String?): Pair<Double?, Double?>
}
