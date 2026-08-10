package com.anaphan.geological

import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Constants {
    const val MOD_ID = "geological"
    const val MOD_NAME = "Geological"

    @JvmStatic // needed so Mixins can access
    val LOG: Logger = LoggerFactory.getLogger(MOD_NAME)

    fun id(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, name)
    }
}