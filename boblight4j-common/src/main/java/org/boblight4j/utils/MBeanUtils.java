package org.boblight4j.utils;

import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MBeanUtils {

	private static final Logger LOG = LoggerFactory.getLogger(MBeanUtils.class);

	private MBeanUtils() {
	}

	public static void registerBean(final String name, final Object bean) {
		final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName objName;
		try {
			objName = new ObjectName(name);

			try {
				mbs.getObjectInstance(objName);
				mbs.unregisterMBean(objName);
			} catch (final InstanceNotFoundException e) {
			}

			mbs.registerMBean(bean, objName);
		} catch (final Exception e) {
			LOG.error("Unable to register bean with name " + name);
		}
	}
}
