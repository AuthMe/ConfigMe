package ch.jalu.configme.beanmapper.command

import java.util.Map

/**
 * Command configuration.
 */
data class KotlinCommandConfig(val commands: Map<String, Command>, val duration: Int)
