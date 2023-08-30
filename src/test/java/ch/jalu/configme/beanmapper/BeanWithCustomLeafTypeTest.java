package ch.jalu.configme.beanmapper;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.beanmapper.leafvaluehandler.MapperLeafType;
import ch.jalu.configme.beanmapper.leafvaluehandler.LeafValueHandlerImpl;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.typeresolver.TypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests that the bean mapper can be extended to support custom types. Bean properties with fields of a
 * {@link CustomInteger custom type} are used, which are handled by a custom mapper leaf type.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/182">Issue #182</a>
 */
class BeanWithCustomLeafTypeTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadMap() throws IOException {
        // given
        String yaml = "rangeByName:"
            + "\n  growth:"
            + "\n    min: 5"
            + "\n    max: 10"
            + "\n  depth:"
            + "\n    min: 1"
            + "\n    max: 10"
            + "\n  speed:"
            + "\n    min: 2"
            + "\n    max: 7";
        Path tempFile = TestUtils.createTemporaryFile(tempDir);
        Files.write(tempFile, yaml.getBytes());

        // when
        SettingsManager settingsManager = SettingsManagerBuilder.withYamlFile(tempFile)
            .configurationData(MyTestSettings.class)
            .create();

        // then
        RangeCollection ranges = settingsManager.getProperty(MyTestSettings.RANGES);
        assertThat(ranges.getRangeByName().keySet(), contains("growth", "depth", "speed"));
        assertThat(ranges.getRangeByName().get("growth"), equalTo(new Range(5, 10)));
        assertThat(ranges.getRangeByName().get("depth"), equalTo(new Range(1, 10)));
        assertThat(ranges.getRangeByName().get("speed"), equalTo(new Range(2, 7)));
    }

    /**
     * Settings holder class with a bean property defined to use a custom bean mapper.
     */
    public static final class MyTestSettings implements SettingsHolder {

        public static final Property<RangeCollection> RANGES =
            new BeanProperty<>(RangeCollection.class, "", new RangeCollection(), new MapperWithCustomIntSupport());

        private MyTestSettings() {
        }
    }

    /**
     * Mapper extension with a custom leaf type so that {@link CustomInteger} is supported.
     */
    public static final class MapperWithCustomIntSupport extends MapperImpl {

        MapperWithCustomIntSupport() {
            super(new BeanDescriptionFactoryImpl(),
                  LeafValueHandlerImpl.builder().addDefaults().addType(new CustomIntegerLeafType()).build());
        }
    }

    /**
     * Provides {@link CustomInteger} when reading from and writing to a property resource.
     */
    public static final class CustomIntegerLeafType implements MapperLeafType {

        @Override
        public @Nullable Object convert(@Nullable Object value, @NotNull TypeInfo targetType,
                                        @NotNull ConvertErrorRecorder errorRecorder) {
            if (targetType.isAssignableFrom(CustomInteger.class) && value instanceof Number) {
                return new CustomInteger(((Number) value).intValue(), false);
            }
            return null;
        }

        @Override
        public @Nullable Object toExportValueIfApplicable(@Nullable Object value) {
            if (value instanceof CustomInteger) {
                return ((CustomInteger) value).value;
            }
            return null;
        }
    }

    /**
     * Custom integer - dummy class that wraps an integer.
     */
    public static class CustomInteger {

        private final int value;

        CustomInteger(int value, boolean otherParam) {
            // 'otherParam' is just here to show that it is a custom initialization and not a constructor the mapper
            // could somehow pick up automatically.
            this.value = value;
        }

        @Override
        public boolean equals(Object that) {
            return this == that
                || (that instanceof CustomInteger && this.value == ((CustomInteger) that).value);
        }
    }

    /**
     * Range collection: bean type as used in the bean property of {@link MyTestSettings}.
     */
    public static class RangeCollection {

        private Map<String, Range> rangeByName;

        public Map<String, Range> getRangeByName() {
            return rangeByName;
        }

        public void setRangeByName(Map<String, Range> rangeByName) {
            this.rangeByName = rangeByName;
        }
    }

    /**
     * Bean type which represents a range using the custom integer type. Used in {@link RangeCollection}.
     */
    public static class Range {

        private CustomInteger min;
        private CustomInteger max;

        public Range() {
        }

        public Range(int min, int max) {
            this.min = new CustomInteger(min, false);
            this.max = new CustomInteger(max, false);
        }

        public CustomInteger getMin() {
            return min;
        }

        public void setMin(CustomInteger min) {
            this.min = min;
        }

        public CustomInteger getMax() {
            return max;
        }

        public void setMax(CustomInteger max) {
            this.max = max;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Range range = (Range) o;
            return Objects.equals(min, range.min)
                && Objects.equals(max, range.max);
        }

        @Override
        public int hashCode() {
            return Objects.hash(min, max);
        }
    }
}
