package com.aymen.metastore.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelRunTracker  @Inject constructor() {
    var firstViewModelName: String? = null
    var isFirstViewModelRun = true
}