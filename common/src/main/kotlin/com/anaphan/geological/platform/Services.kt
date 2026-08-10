package com.anaphan.geological.platform

import com.anaphan.geological.Constants
import com.anaphan.geological.platform.services.IPlatformHelper
import java.util.ServiceLoader
import kotlin.jvm.java


object Services {

    val PLATFORM = load(IPlatformHelper::class.java)

    fun <T> load(clazz: Class<T>): T {
        val loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow {
                IllegalStateException("Failed to load service for ${clazz.name}")
            }
        Constants.LOG.info("Loaded {} for service {}", loadedService, clazz)
        return loadedService
    }
}