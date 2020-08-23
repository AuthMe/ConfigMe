package ch.jalu.configme.beanmapper

import ch.jalu.configme.beanmapper.command.Command
import ch.jalu.configme.beanmapper.command.KotlinCommandConfig
import ch.jalu.configme.beanmapper.command.ExecutionDetails
import ch.jalu.configme.beanmapper.command.Executor
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexCommand
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexCommandConfig
import ch.jalu.configme.beanmapper.command.optionalproperties.ComplexOptionalTypeConfig
import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandler
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactory
import ch.jalu.configme.beanmapper.typeissues.GenericCollection
import ch.jalu.configme.beanmapper.typeissues.MapWithNonStringKeys
import ch.jalu.configme.beanmapper.typeissues.UnsupportedCollection
import ch.jalu.configme.beanmapper.typeissues.UntypedCollection
import ch.jalu.configme.beanmapper.typeissues.UntypedMap
import ch.jalu.configme.beanmapper.worldgroup.GameMode
import ch.jalu.configme.beanmapper.worldgroup.Group
import ch.jalu.configme.beanmapper.worldgroup.WorldGroupConfig
import ch.jalu.configme.exception.ConfigMeException
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder
import ch.jalu.configme.resource.PropertyReader
import ch.jalu.configme.resource.YamlFileReader
import ch.jalu.configme.samples.TestEnum
import ch.jalu.configme.utils.TypeInformation
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.jupiter.api.Test
import java.util.LinkedHashMap
import java.util.NavigableMap
import java.util.Objects
import java.util.Optional
import java.util.TreeMap
import ch.jalu.configme.TestUtils.getJarPath
import ch.jalu.configme.TestUtils.verifyException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.Mockito.mock
import ch.jalu.configme.beanmapper.KotlinMapperImplTest

/**
 * Test for [KotlinMapperImpl].
 */
internal class KotlinMapperImplTest {

    @Test
    fun `unmappable mandatory field should return null`() {
        // given
        val reader: PropertyReader = createReaderFromFile("/beanmapper/commands_invalid_2.yml")
        val mapper = KotlinMapperImpl()
        val errorRecorder = ConvertErrorRecorder()

        // when
        val result = mapper.convertToBean(reader.getObject("commandconfig"), KotlinCommandConfig::class.java, errorRecorder)

        // then
        assertThat(result, nullValue())
    }

    @Test
    fun shouldReturnNullForMissingSection() {
        // given
        val reader: PropertyReader = createReaderFromFile("/empty_file.yml")
        val mapper = KotlinMapperImpl()
        val errorRecorder = ConvertErrorRecorder()

        // when
        val result = mapper.convertToBean(reader.getObject("commands"), KotlinCommandConfig::class.java, errorRecorder)

        // then
        assertThat(result, nullValue())
    }

    @Test
    fun shouldHandleEmptyOptionalFields() {
        // given
        val reader: PropertyReader = createReaderFromFile("/beanmapper/commands.yml")
        val mapper = KotlinMapperImpl()
        val errorRecorder = ConvertErrorRecorder()
        return // TODO: Doesn't work yet
        // when
        val result = mapper.convertToBean(
                reader.getObject("commandconfig"),
                ComplexCommandConfig::class.java,
                errorRecorder
        )

        // then
        assertThat(errorRecorder.isFullyValid(), equalTo(false)) // e.g. save.arguments are missing
        assertThat(result, not(nullValue()))
        assertThat(result.getCommands().keys, contains("save", "refresh", "open"))
        assertAllOptionalFieldsEmpty(result.getCommands().get("save"))
        assertAllOptionalFieldsEmpty(result.getCommands().get("refresh"))
        assertAllOptionalFieldsEmpty(result.getCommands().get("open"))
    }

    companion object {
        private fun assertAllOptionalFieldsEmpty(complexCommand: ComplexCommand?) {
            assertThat(complexCommand, not(nullValue()))
            if (complexCommand == null) return
            assertAreAllEmpty(
                    complexCommand.getNameStartsWith(),
                    complexCommand.getNameHasLength(),
                    complexCommand.getDoubleOptional(),
                    complexCommand.getTestEnumProperty())
        }

        private fun assertAreAllEmpty(vararg optionals: Optional<*>) {
            for (o in optionals) {
//                assertThat(o, equalTo(Optional.empty<>()))
            }
        }

        private fun createReaderFromFile(file: String): PropertyReader {
            return YamlFileReader(getJarPath(file))
        }

        private fun createContextWithType(clazz: Class<*>): MappingContext {
            val type = TypeInformation(clazz)
            val root: MappingContextImpl = MappingContextImpl.createRoot(type, ConvertErrorRecorder())
            return root.createChild("path.in.test", type)
        }
    }
}
