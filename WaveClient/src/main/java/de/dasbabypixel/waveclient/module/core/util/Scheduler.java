package de.dasbabypixel.waveclient.module.core.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import de.dasbabypixel.waveclient.module.core.listener.BaseListener;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class Scheduler {

	private static final int ticksPerSecond = 20;
	private static final long millisecondsPerTick = TimeUnit.SECONDS.toMillis(1) / ticksPerSecond;
	public static final int NO_REPEAT = -1;
	public static final int NO_DELAY = 0;

	private final Listener listener = new Listener(this);

	private long lastTimeCalled = System.currentTimeMillis();
	private int currentTick = 0;

	private final Queue<Task> tasks = new ConcurrentLinkedQueue<>();

	private void processSync() {
		while (lastTimeCalled < System.currentTimeMillis()) {
			lastTimeCalled += millisecondsPerTick;
			currentTick++;
			for (Task task : tasks) {
				if (shouldExecuteTask(task)) {
					try {
						task.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private boolean shouldExecuteTask(Task task) {
		if (task.handle.isPaused()) {
			return false;
		}
		if (task.handle.isStopped()) {
			tasks.remove(task);
			return false;
		}
		if (task.delay > 0) {
			task.delay--;
			return false;
		}
		if (task.tickStarted == -1) {
			task.tickStarted = currentTick;
		}
		if (task.repeat == NO_REPEAT) {
			tasks.remove(task);
			return true;
		}
		if ((currentTick - task.tickStarted) % task.repeat == 0) {
			return true;
		}
		return false;
	}

	public void start() {
		listener.register();
	}

	public void stop() {
		listener.unregister();
	}

	public ScheduledRunnable schedule(Runnable run) {
		return schedule(run, NO_DELAY);
	}

	public ScheduledRunnable schedule(Runnable run, int delay) {
		return schedule(run, delay, NO_REPEAT);
	}

	public ScheduledRunnable schedule(Runnable run, int delay, int repeat) {
		return schedule(new ScheduledRunnableHandle(run), delay, repeat);
	}

	public ScheduledRunnable schedule(ScheduledRunnable run) {
		return schedule(run, NO_DELAY);
	}

	public ScheduledRunnable schedule(ScheduledRunnable run, int delay) {
		return schedule(run, delay, NO_REPEAT);
	}

	public ScheduledRunnable schedule(ScheduledRunnable run, int delay, int repeat) {
		schedule(Task.getTask(run, delay, repeat));
		return run;
	}

	private void schedule(Task task) {
		tasks.add(task);
	}

	private static class Listener extends BaseListener<ClientTickEvent> {
		public Listener(Scheduler scheduler) {
			super(ClientTickEvent.class, event -> {
				scheduler.processSync();
			});
		}
	}

	private static class ScheduledRunnableHandle extends ScheduledRunnable {
		private Runnable handle;

		public ScheduledRunnableHandle(Runnable handle) {
			this.handle = handle;
		}

		@Override
		public void run() {
			handle.run();
		}
	}

	public static abstract class ScheduledRunnable implements Runnable {

		private boolean cancelled;
		private boolean paused = false;

		public boolean isStopped() {
			return cancelled;
		}

		public void stop() {
			if (!cancelled) {
				cancelled = true;
			}
		}

		public boolean isPaused() {
			return paused;
		}

		public void pause() {
			paused = true;
		}

		public void resume() {
			paused = false;
		}
	}

	private static class Task implements Runnable {

		private static Task getTask(ScheduledRunnable runnable, int delay, int repeat) {
			return new Task(runnable, delay, repeat);
		}

		private int tickStarted = -1;
		private int delay;
		private int repeat;
		private ScheduledRunnable handle;

		public Task(ScheduledRunnable handle, int delay, int repeat) {
			this.handle = handle;
			this.delay = delay;
			this.repeat = repeat;
		}

		@Override
		public void run() {
			handle.run();
		}
	}
}
