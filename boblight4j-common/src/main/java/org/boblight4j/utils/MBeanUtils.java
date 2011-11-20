package org.boblight4j.utils;

import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public final class MBeanUtils {

	private static final Logger LOG = Logger.getLogger(MBeanUtils.class);

	public static void registerBean(final String name, final Object bean) {
		final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName objName;
		try
		{
			objName = new ObjectName(name);

			try
			{
				mbs.getObjectInstance(objName);
				mbs.unregisterMBean(objName);
			}
			catch (final InstanceNotFoundException e)
			{
			}

			mbs.registerMBean(bean, objName);
		}
		catch (final Exception e)
		{
			LOG.error("", e);
		}
	}

	private MBeanUtils() {
	}
}
