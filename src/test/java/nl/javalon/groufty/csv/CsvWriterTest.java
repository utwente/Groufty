package nl.javalon.groufty.csv;

import org.junit.Assert;
import org.junit.Test;

public class CsvWriterTest {

	@Test
	public void escapeFileNameTest() {
		Assert.assertEquals(CsvWriter.escapeFileName("abcd"), "abcd");
		Assert.assertEquals(CsvWriter.escapeFileName("a/b/c/d"), "abcd");
		Assert.assertEquals(CsvWriter.escapeFileName("a\\b\\c\\d"), "abcd");
		Assert.assertEquals(CsvWriter.escapeFileName("a;b:c;d"), "abcd");
		Assert.assertEquals(CsvWriter.escapeFileName("@bcd"), "bcd");
		Assert.assertEquals(CsvWriter.escapeFileName("a^@d"), "ad");
		Assert.assertEquals(CsvWriter.escapeFileName("øøøø"), "");
		Assert.assertEquals(CsvWriter.escapeFileName("ØøaØbØcØd"), "abcd");
		Assert.assertEquals(CsvWriter.escapeFileName("a b c d-,.()?;#$_"), "a b c d-,.()$_");

	}
}
