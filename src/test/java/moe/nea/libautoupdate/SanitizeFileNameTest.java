package moe.nea.libautoupdate.util.file;

import moe.nea.libautoupdate.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SanitizeFileNameTest {
	@Test
	void testSpace() {
		Assertions.assertEquals(
				"filename 1.2.0.jar",
				FileUtils.sanitizeFileName("filename 1.2.0.jar")
		);
	}

	@Test
	void testTraversal() {
		assertDoesNotContain(".", FileUtils.sanitizeFileName("."));
		Assertions.assertEquals(
				".",
				FileUtils.urlDecode(FileUtils.sanitizeFileName("."))
		);

		assertDoesNotContain("..", FileUtils.sanitizeFileName(".."));
		Assertions.assertEquals(
				"..",
				FileUtils.urlDecode(FileUtils.sanitizeFileName(".."))
		);

		Assertions.assertEquals("...", FileUtils.sanitizeFileName("..."));
	}

	static void assertDoesNotContain(String search, String subject) {
		Assertions.assertFalse(
				subject.contains(search),
				() -> "'" + subject + "' should not contain '"
						+ search + "' but does."
		);
	}

	@Test
	void testSlashes() {
		assertDoesNotContain("a/b", FileUtils.sanitizeFileName("a/b"));
		Assertions.assertEquals(
				"a/b",
				FileUtils.urlDecode(FileUtils.sanitizeFileName("a/b"))
		);

		assertDoesNotContain(
				"a\\b", FileUtils.sanitizeFileName("a\\b")
		);
		Assertions.assertEquals(
				"a\\b",
				FileUtils.urlDecode(FileUtils.sanitizeFileName("a\\b"))
		);

		assertDoesNotContain("/", FileUtils.sanitizeFileName("/"));
		Assertions.assertEquals(
				"/",
				FileUtils.urlDecode(FileUtils.sanitizeFileName("/"))
		);

		assertDoesNotContain("\\", FileUtils.sanitizeFileName("\\"));
		Assertions.assertEquals(
				"\\",
				FileUtils.urlDecode(FileUtils.sanitizeFileName("\\"))
		);
	}

	@Test
	void testPlus() {
		Assertions.assertEquals(
				"filename+1.2.0.jar",
				FileUtils.sanitizeFileName("filename+1.2.0.jar")
		);
	}

	@Test
	void testDot() {
		Assertions.assertEquals(
				"filename.jar",
				FileUtils.sanitizeFileName("filename.jar")
		);
	}
}