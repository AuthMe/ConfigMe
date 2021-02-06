package ch.jalu.configme.beanmapper;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.TestUtils;
import ch.jalu.configme.beanmapper.leafvaluehandler.AbstractLeafValueHandler;
import ch.jalu.configme.beanmapper.leafvaluehandler.CombiningLeafValueHandler;
import ch.jalu.configme.beanmapper.leafvaluehandler.StandardLeafValueHandlers;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;
import ch.jalu.configme.properties.BeanProperty;
import ch.jalu.configme.properties.Property;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests bean properties with BigInteger field, which are handled by a custom type handler.
 *
 * @see <a href="https://github.com/AuthMe/ConfigMe/issues/182">Issue #182</a>
 */
class BeanWithCustomBigIntegerTypeHandlerTest {

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

    public static final class MyTestSettings implements SettingsHolder {

        public static final Property<RangeCollection> RANGES =
            new BeanProperty<>(RangeCollection.class, "", new RangeCollection(), new MapperWithBigIntSupport());

        private MyTestSettings() {
        }
    }

    public static final class MapperWithBigIntSupport extends MapperImpl {

        MapperWithBigIntSupport() {
            super(new BeanDescriptionFactoryImpl(),
                new CombiningLeafValueHandler(StandardLeafValueHandlers.getDefaultLeafValueHandler(),
                    new BigIntegerLeafValueHandler()));
        }
    }

    public static final class BigIntegerLeafValueHandler extends AbstractLeafValueHandler {

        @Override
        protected Object convert(Class<?> clazz, Object value) {
            if (clazz.equals(BigInteger.class) && value instanceof Number) {
                return BigInteger.valueOf(((Number) value).longValue());
            }
            return null;
        }

        @Nullable
        @Override
        public Object toExportValue(@Nullable Object value) {
            if (value instanceof BigInteger) {
                return ((BigInteger) value).longValue();
            }
            return null;
        }
    }

    public static class RangeCollection {

        private Map<String, Range> rangeByName;

        public Map<String, Range> getRangeByName() {
            return rangeByName;
        }

        public void setRangeByName(Map<String, Range> rangeByName) {
            this.rangeByName = rangeByName;
        }
    }

    public static class Range {

        private BigInteger min;
        private BigInteger max;

        public Range() {
        }

        public Range(int min, int max) {
            this.min = BigInteger.valueOf(min);
            this.max = BigInteger.valueOf(max);
        }

        public BigInteger getMin() {
            return min;
        }

        public void setMin(BigInteger min) {
            this.min = min;
        }

        public BigInteger getMax() {
            return max;
        }

        public void setMax(BigInteger max) {
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
