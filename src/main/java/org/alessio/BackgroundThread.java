package org.alessio;

import java.util.*;
import java.time.*;

public class BackgroundThread {

	private final TowerOfHanoi towerOfHanoi;
	private final boolean startThread;
	private Timer statusTimer;
	private final int updateInterval = 10000;

	private final Thread t = new Thread(() -> {
		statusTimer = new Timer();
		statusTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				update();
			}
		}, 1000, updateInterval);
	});

	BackgroundThread(TowerOfHanoi towerOfHanoi, boolean startThread) {
		this.towerOfHanoi = towerOfHanoi;
		this.startThread = startThread;
		if (startThread) {
			t.start();
		}
	}

	private void update() {
		Instant start = Main.getStartTime() != null ? Main.getStartTime() : Instant.now();
		Instant now = Instant.now();
		Duration duration = Duration.between(start, now);

		printStatus(towerOfHanoi.getRods(), duration);
	}

	private void printStatus(Stack<Integer>[] rods, Duration duration) {

		// Print current moves and total moves until completion
		long moves = (long) Math.pow(2, towerOfHanoi.getNumDisks()) - 1;
		double progressPercentage = (double) towerOfHanoi.getMoves() / moves * 100;
		System.out.println("\n" + towerOfHanoi.getMoves() + " moves of " + moves + " (" + String.format("%.4f", progressPercentage) + "%)");

		// Print current state of rods
		if (rods != null && !rods[0].isEmpty() && !rods[1].isEmpty() && !rods[2].isEmpty()) {
			System.out.println("Current state of rods: " + rods[0].firstElement() + " " + rods[1].firstElement() + " " + rods[2].firstElement());
		} else {
			System.out.println("Rods are empty");
		}
		// Print time elapsed and estimated time remaining
		Duration etaDuration = Duration.ofMillis((long) (duration.toMillis() / progressPercentage * (100 - progressPercentage)));
		Main.printTime(duration, "Current time elapsed:");
		Main.printTime(etaDuration, "ETA:");
	}

	public void shutdown() {
		if (startThread) {
			statusTimer.cancel();
			statusTimer.purge();
			t.interrupt();
		}
	}
}
