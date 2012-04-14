package org.boblight4j.server.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Vector;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.Light;
import org.boblight4j.server.ClientsHandler;

public class ConfigUpdater extends AbstractConfigUpdater implements Runnable {

	private boolean stop;

	public ConfigUpdater(final File file, final ClientsHandler clients,
			final Config config, final Vector<Device> devices,
			final Vector<Light> lights) {
		super(file, clients, config, devices, lights);
	}

	@Override
	public void run() {
		final Path myDir = Paths.get(this.watchFile.getParentFile()
				.getAbsolutePath());
		WatchService watcher;
		try {
			watcher = myDir.getFileSystem().newWatchService();
			myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		while (!this.stop) {
			try {

				final WatchKey watckKey = watcher.take();

				boolean updateConfig = false;
				final List<WatchEvent<?>> events = watckKey.pollEvents();
				for (final WatchEvent<?> event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						final Path context = (Path) event.context();
						final File file = context.toFile();
						if (this.watchFile.getName().equals(file.getName())) {
							this.updateConfig();
							System.out.println("Config modified");
						}
					} else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
					} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						final Path context = (Path) event.context();
						final File file = context.toFile();
						if (this.watchFile.getName().equals(file.getName())) {
							updateConfig = true;
						}

					}
				}

				if (updateConfig) {
					this.updateConfig();
					System.out.println("Config modified");
				}

				watckKey.reset();

			} catch (final Exception e) {
				e.printStackTrace();
				System.out.println("Error: " + e.toString());
			}

			synchronized (this) {

				try {
					watcher.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				this.notifyAll();
			}
		}
	}

	public void startThread() {
		new Thread(this, "ConfigUpdater").start();
	}

}
