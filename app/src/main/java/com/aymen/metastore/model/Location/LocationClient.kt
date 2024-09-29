package com.aymen.metastore.model.Location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocatipnUpdates(interval : Long):Flow<Location>

    class LocationException(message : String):Exception()
}