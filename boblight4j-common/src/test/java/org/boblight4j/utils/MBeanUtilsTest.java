package org.boblight4j.utils;

import java.lang.management.ManagementFactory;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ManagementFactory.class })
@PowerMockIgnore(value = { "javax.management.*" })
public class MBeanUtilsTest {

	@Mock
	private static MBeanServer mBeanServer;

	@BeforeClass
	public static void setUp() {
		mBeanServer = Mockito.mock(MBeanServer.class);

		PowerMockito.mockStatic(ManagementFactory.class);
		Mockito.when(ManagementFactory.getPlatformMBeanServer()).thenReturn(
				mBeanServer);
	}

	@Test
	public void testRegisterBean() throws Exception {
		Object bean = Mockito.mock(DynamicMBean.class);
		String beanName = "beanName:type=type";

		ObjectName objectName = Mockito.mock(ObjectName.class);
		PowerMockito.whenNew(ObjectName.class).withParameterTypes(String.class)
				.withArguments(beanName).thenReturn(objectName);

		MBeanUtils.registerBean(beanName, bean);

		Mockito.verify(mBeanServer).getObjectInstance(objectName);

	}

}
