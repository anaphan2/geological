package examplegroup.examplemod

import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Constants {
    const val MOD_ID = "examplemod"
    const val MOD_NAME = "ExampleMod"

    @JvmStatic // needed so Mixins can access
    val LOG: Logger = LoggerFactory.getLogger(MOD_NAME)

    fun id(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, name)
    }
}