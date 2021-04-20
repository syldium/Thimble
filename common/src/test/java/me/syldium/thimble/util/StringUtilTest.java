package me.syldium.thimble.util;

import me.syldium.thimble.common.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilTest {

    @Test
    public void split_placeholder() {
        final String str1 = "thimble";
        final String str2 = "lb";
        final String str3 = "wins";
        final String str4 = "2";
        final char delim = '_';
        final List<String> actual = StringUtil.split(str1 + delim + str2 + delim + str3 + delim + str4, delim);
        assertEquals(List.of(str1, str2, str3, str4), actual);
    }

    @Test
    public void split_commandWithTrailingSpace() {
        final String str1 = "arena";
        final String str2 = "setMin";
        final String str3 = "4";
        final char delim = ' ';
        final List<String> actual = StringUtil.split(str1 + delim + str2 + delim + str3 + delim, delim);
        assertEquals(List.of(str1, str2, str3, ""), actual);
    }

    @Test
    public void split_emptyString() {
        final List<String> actual = StringUtil.split("", ' ');
        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    public void split_sameResultAsRegex() {
        final String str = "th arena setName demo ";
        assertArrayEquals(str.split(" ", -1), StringUtil.split(str, ' ').toArray(String[]::new));
    }

    @Test
    public void firstToken_empty() {
        assertEquals("", StringUtil.firstToken("", ' '));
    }

    @Test
    public void firstToken_withoutDelimiter() {
        assertEquals("thimble", StringUtil.firstToken("thimble", ' '));
    }

    @Test
    public void firstToken() {
        assertEquals("th", StringUtil.firstToken("th setSpawn test", ' '));
    }
}
