package org.alessio;

import java.util.*;
import java.time.*;

public class BackgroundThread {

	Timer statusTimer;
	private final int updateInterval = 10000;

	private final Thread t = new Thread(() -> {
		statusTimer = new Timer();
		statusTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				update();
			}
		}, 1000, updateInterval);
	});

	BackgroundThread() {
		t.start();
	}

	private void update() {
		Instant start = Main.getStartTime() != null ? Main.getStartTime() : Instant.now();
		Instant now = Instant.now();
		Duration timeElapsed = Duration.between(start, now);

		printStatus(TowerOfHanoi.getRods(), timeElapsed);
	}

	private void printStatus(Stack<Integer>[] rods, Duration timeElapsed) {

		System.out.println("Current state of rods: " + rods);
		long days = timeElapsed.toDays();
		long hours = timeElapsed.toHours() % 24;
		long minutes = timeElapsed.toMinutes() % 60;
		long seconds = timeElapsed.toSeconds() % 60;
		long milliseconds = timeElapsed.toMillis() % 1000;

		System.out.printf("Execution time: %d days, %d hours, %d minutes, %d seconds, %d milliseconds%n",
				days, hours, minutes, seconds, milliseconds);
	}

	public void shutdown() {
		statusTimer.cancel();
		statusTimer.purge();
		t.interrupt();
	}
}
