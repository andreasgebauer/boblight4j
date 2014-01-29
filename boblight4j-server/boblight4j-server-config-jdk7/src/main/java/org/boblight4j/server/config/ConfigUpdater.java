package org.boblight4j.server.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.boblight4j.server.ClientsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUpdater extends AbstractConfigUpdater implements Runnable {

	private static final Logger LOG = LoggerFactory
			.getLogger(ConfigUpdater.class);

	private boolean stop;

	public ConfigUpdater(final File file, final ConfigCreator configCreator,
			final ClientsHandler<?> clients, final AbstractConfig config) {
		super(file, configCreator, clients, config);
	}

	@Override
	public void run() {
		final Path myDir = Paths.get(this.watchFile.getParentFile()
				.getAbsolutePath());
		WatchService watcher;
		try
		{
			watcher = myDir.getFileSystem().newWatchService();
			myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);
		}
		catch (final IOException e1)
		{
			LOG.error("Error while setup.", e1);
			return;
		}

		while (!this.stop)
		{
			try
			{
				final WatchKey watchKey = watcher.take();

				boolean updateConfig = false;
				final List<WatchEvent<?>> events = watchKey.pollEvents();
				for (final WatchEvent<?> event : events)
				{
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
							|| event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
					{
						final Path context = (Path) event.context();
						final File file = context.toFile();
						if (this.watchFile.getName().equals(file.getName()))
						{
							updateConfig = true;
						}
					}
				}

				if (updateConfig)
				{
					this.updateConfig();
					System.out.println("Config modified");
				}

				watchKey.reset();

				synchronized (this)
				{
					this.notifyAll();
				}
			}
			catch (final InterruptedException e)
			{
				LOG.error("interrupted", e);
			}
			catch (final ClosedWatchServiceException e)
			{
				LOG.error("watch service closed", e);
			}

		}
	}

	public void startThread() {
		new Thread(this, "ConfigUpdater").start();
	}

}
