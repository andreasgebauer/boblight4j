package org.boblight4j.server.config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PlainTextConfigFileReader.class })
public class PlainTextConfigFileReaderTest {

	private PlainTextConfigFileReader testable;

	private File configFile;

	@Before
	public void setUp() throws Exception {
		configFile = mock(File.class);
		testable = new PlainTextConfigFileReader(configFile);
	}

	@Test
	public void testLoadConfig() throws Exception {
		URL resource = PlainTextConfigFileReader.class
				.getResource("/boblight.10pc.conf");

		File configfile = new File(resource.toURI());
		testable = new PlainTextConfigFileReader(configfile);

		testable.loadConfig();

		List<ConfigGroup> expGrps = new ArrayList<ConfigGroup>();
		ConfigGroup expGrp = new ConfigGroup();
		expGrp.lines.add(new ConfigLine("name		red", 23));
		expGrp.lines.add(new ConfigLine("rgb		FF0000", 24));
		expGrps.add(expGrp);
		expGrp = new ConfigGroup();
		expGrp.lines.add(new ConfigLine("name		green", 27));
		expGrp.lines.add(new ConfigLine("rgb		00FF00", 28));
		expGrps.add(expGrp);
		expGrp = new ConfigGroup();
		expGrp.lines.add(new ConfigLine("name		blue", 31));
		expGrp.lines.add(new ConfigLine("rgb		0000FF", 32));
		expGrps.add(expGrp);

		assertReadGroupsContainExpectedGroup(expGrps, testable.colorLines);

		expGrps.clear();
		expGrp = new ConfigGroup();
		expGrp.lines.add(new ConfigLine("name		arduino", 6));
		expGrp.lines.add(new ConfigLine("output		/dev/ttyUSB0", 7));
		expGrp.lines.add(new ConfigLine("channels	60", 8));
		expGrp.lines.add(new ConfigLine("type		momo", 9));
		expGrps.add(expGrp);

		assertReadGroupsContainExpectedGroup(expGrps, testable.deviceLines);

		expGrps.clear();
		expGrp = new ConfigGroup();
		expGrp.lines.add(new ConfigLine("name		arduino", 6));
		expGrp.lines.add(new ConfigLine("output		/dev/ttyUSB0", 7));
		expGrps.add(expGrp);

		assertReadGroupsContainExpectedGroup(expGrps, testable.deviceLines);
	}

	private void assertReadGroupsContainExpectedGroup(
			List<ConfigGroup> expGrps, List<ConfigGroup> deviceLines) {
		assertEquals(expGrps.size(), deviceLines.size());
		for (ConfigGroup configGroup : expGrps) {
			for (ConfigLine configLine : configGroup.lines) {
				findInReadGroups(configLine, deviceLines);
			}
		}
	}

	private void findInReadGroups(ConfigLine configLine,
			List<ConfigGroup> deviceLines) {
		boolean found = false;
		outer: for (ConfigGroup readGrp : deviceLines) {
			for (ConfigLine readLine : readGrp.lines) {
				if (configLine.equals(readLine)) {
					found = true;
					break outer;
				}
			}
		}
		assertTrue("Did not find expected " + configLine, found);
	}
}
