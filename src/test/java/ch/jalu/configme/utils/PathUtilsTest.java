package ch.jalu.configme.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link PathUtils}.
 */
class PathUtilsTest {

    @Test
    void shouldConcatPaths() {
        // given / when / then
        assertThat(PathUtils.concat("db", "driver"), equalTo("db.driver"));
        assertThat(PathUtils.concat("", "title"), equalTo("title"));
    }

    @Test
    void shouldConcatPathsAndBeAwareOfSpecialSuffixes() {
        // given / when / then
        assertThat(PathUtils.concatSpecifierAware("db", "driver"), equalTo("db.driver"));
        assertThat(PathUtils.concatSpecifierAware("", "title"), equalTo("title"));

        assertThat(PathUtils.concatSpecifierAware("db.protocols", "[0]"), equalTo("db.protocols[0]"));
        assertThat(PathUtils.concatSpecifierAware("db.alias", "[k=bool]"), equalTo("db.alias[k=bool]"));
        assertThat(PathUtils.concatSpecifierAware("db.version", "$opt"), equalTo("db.version$opt"));

        assertThat(PathUtils.concatSpecifierAware("", "[0]"), equalTo("[0]"));
        assertThat(PathUtils.concatSpecifierAware("", "[k=bool]"), equalTo("[k=bool]"));
        assertThat(PathUtils.concatSpecifierAware("", "$opt"), equalTo("$opt"));
    }

    @Test
    void shouldCreateIndexSpecifier() {
        // given / when / then
        assertThat(PathUtils.pathSpecifierForIndex(0), equalTo("[0]"));
        assertThat(PathUtils.pathSpecifierForIndex(4), equalTo("[4]"));
    }

    @Test
    void shouldCreateKeySpecifier() {
        // given
        Map<Object, Boolean> map = new LinkedHashMap<>();
        map.put("name", true);
        map.put(1337, false);

        List<Map.Entry<Object, Boolean>> mapEntries = new ArrayList<>(map.entrySet());

        // when / then
        assertThat(PathUtils.pathSpecifierForMapKey(mapEntries.get(0)), equalTo("[k=name]"));
        assertThat(PathUtils.pathSpecifierForMapKey(mapEntries.get(1)), equalTo("[k=1337]"));
        assertThat(PathUtils.pathSpecifierForMapKey("toast"), equalTo("[k=toast]"));
    }

    @Test
    void shouldDetermineIfIsSpecifierSuffix() {
        // given / when / then
        assertThat(PathUtils.isSpecifierSuffix("[3]"), equalTo(true));
        assertThat(PathUtils.isSpecifierSuffix("[k=groot]"), equalTo(true));
        assertThat(PathUtils.isSpecifierSuffix("[other]"), equalTo(true));

        assertThat(PathUtils.isSpecifierSuffix("$opt"), equalTo(true));
        assertThat(PathUtils.isSpecifierSuffix("$alt"), equalTo(true));

        assertThat(PathUtils.isSpecifierSuffix("version"), equalTo(false));
        assertThat(PathUtils.isSpecifierSuffix(""), equalTo(false));
        assertThat(PathUtils.isSpecifierSuffix("{year}"), equalTo(false));
    }
}
