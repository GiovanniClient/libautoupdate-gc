package moe.nea.libautoupdate.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class StringSplitIteratorTest {
	@Test
	void testOverlappingNeedles() {
		Assertions.assertIterableEquals(
				Arrays.asList("abc", "1def"),
				StringUtils.splitIterator("abc111def", "11")
		);
	}

	@Test
	void testNoNeedle() {
		Assertions.assertIterableEquals(
				Arrays.asList("abc1def"),
				StringUtils.splitIterator("abc1def", "11")
		);
	}
}