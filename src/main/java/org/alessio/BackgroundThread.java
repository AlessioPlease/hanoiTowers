package org.alessio;

import java.util.*;
import java.time.*;
import java.util.function.*;

public class BackgroundThread {

	private final Supplier<Stack<Integer>[]> rods;
	private final IntSupplier numDisks;
	private final LongSupplier moves;
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
		this(towerOfHanoi::getRods, towerOfHanoi::getNumDisks, towerOfHanoi::getMoves, startThread);
	}

	BackgroundThread(FastTowerOfHanoi towerOfHanoi, boolean startThread) {
		this(towerOfHanoi::getRods, towerOfHanoi::getNumDisks, towerOfHanoi::getMoves, startThread);
	}

	private BackgroundThread(Supplier<Stack<Integer>[]> rods, IntSupplier numDisks, LongSupplier moves, boolean startThread) {
		this.rods = rods;
		this.numDisks = numDisks;
		this.moves = moves;
		this.startThread = startThread;
		if (startThread) {
			t.start();
		}
	}

	private void update() {
		Instant start = Main.getStartTime() != null ? Main.getStartTime() : Instant.now();
		Instant now = Instant.now();
		Duration duration = Duration.between(start, now);

		printStatus(rods.get(), numDisks.getAsInt(), moves.getAsLong(), duration);
	}

	private void printStatus(Stack<Integer>[] rods, int numDisks, long currentMoves, Duration duration) {
		// Print current moves and total moves until completion
		long totalMoves = (long) Math.pow(2, numDisks) - 1;
		double progressPercentage = (double) currentMoves / totalMoves * 100;
		System.out.println("\n" + currentMoves + " moves of " + totalMoves + " (" + String.format("%.4f", progressPercentage) + "%)");

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
