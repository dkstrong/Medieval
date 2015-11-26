package asf.medieval.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;

/**
 * Created by daniel on 11/26/15.
 */
public class FileWatcher implements Runnable, Disposable{


	private ScheduledExecutorService exec;
	private ScheduledFuture<?> future;
	private WatchService watcher;

	private FileChangeListener listener;

	public FileWatcher(FileChangeListener listener) {
		this.listener = listener;
		exec = Executors.newSingleThreadScheduledExecutor();
		try {
			watcher = FileSystems.getDefault().newWatchService();

		} catch (IOException e) {
			UtLog.warning("error starting file watcher", e);
		}
	}

	public void addWatch(FileHandle fh)
	{
		try {
			Path fhPath = Paths.get(fh.file().getAbsolutePath());
			fhPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			UtLog.warning("error adding watch", e);
		}

		if(future == null)
			future = exec.scheduleWithFixedDelay(this, 1000, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		try {
			WatchKey key = watcher.take();
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}

				final WatchEvent<Path> pathEvent = (WatchEvent<Path>)event;
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.onFileChanged(pathEvent);
					}
				});
			}

			boolean valid = key.reset();
			if(!valid){
				UtLog.warning("file watch key is no longer valid");
			}
		} catch (InterruptedException e) {
			// not a problem, the file watcher ended or was disposed etc
		} catch(ClosedWatchServiceException e1){
			// not a problem, the file watcher ended or was disposed etc
		}
	}

	@Override
	public void dispose() {
		try {
			watcher.close();
		} catch (IOException e) {
			UtLog.warning("exception thrown when disposing watcher service", e);
		}

		exec.shutdown();
		exec = null;
	}

	public interface FileChangeListener{
		public void onFileChanged(WatchEvent<Path> event);
	}
}
