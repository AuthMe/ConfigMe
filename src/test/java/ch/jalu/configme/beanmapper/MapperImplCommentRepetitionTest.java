package ch.jalu.configme.beanmapper;

import ch.jalu.configme.Comment;
import ch.jalu.configme.properties.convertresult.ValueWithComments;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Test for the handling of comments in {@link MapperImpl}.
 */
class MapperImplCommentRepetitionTest {

    private final MapperImpl mapper = new MapperImpl();

    @TempDir
    public Path temporaryFolder;

    @Test
    void shouldExportAsExpected() {
        // given
        Song song1 = new Song("Thank You", 2000);
        Song song2 = new Song("Smooth Operator", 1984);
        Song song3 = new Song("Rolling in the Deep", 2010);
        Song song4 = new Song("Bohemian Rhapsody", 1975);
        Song song5 = new Song("Shape of You", 2017);

        SongContainer songs = new SongContainer();
        songs.setSongs(Arrays.asList(song1, song2, song3, song4, song5));

        // when
        Map<String, Object> exportValue = (Map) mapper.toExportValue(songs);

        // then
        assertThat(exportValue.keySet(), contains("songs"));
        List<Map<String, Object>> songsAsExport = (List) exportValue.get("songs");

        assertThat(songsAsExport, hasSize(5));
        for (int i = 0; i < songsAsExport.size(); ++i) {
            Map<String, Object> songValues = songsAsExport.get(i);
            assertThat(songValues.keySet(), contains("title", "year"));

            // The comment on title only added for the first entry
            Object titleValue = songValues.get("title");
            if (i == 0) {
                assertThat(titleValue, instanceOf(ValueWithComments.class));
                assertThat(((ValueWithComments) titleValue).getComments(), contains("Song title"));
                assertThat(((ValueWithComments) titleValue).getValue(), equalTo(songs.getSongs().get(i).getTitle()));
            } else {
                assertThat(titleValue, equalTo(songs.getSongs().get(i).getTitle()));
            }

            // The comment on year is always added
            Object yearValue = songValues.get("year");
            assertThat(yearValue, instanceOf(ValueWithComments.class));
            assertThat(((ValueWithComments) yearValue).getComments(), contains("Release year"));
            assertThat(((ValueWithComments) yearValue).getValue(), equalTo(songs.getSongs().get(i).getYear()));
        }
    }

    public static final class SongContainer {

        private List<Song> songs = new ArrayList<>();

        public List<Song> getSongs() {
            return songs;
        }

        public void setSongs(List<Song> songs) {
            this.songs = songs;
        }
    }

    public static final class Song {

        @Comment("Song title")
        private String title;

        @Comment(value = "Release year", repeat = true)
        private int year;

        public Song() {
        }

        public Song(String title, int year) {
            this.title = title;
            this.year = year;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}
