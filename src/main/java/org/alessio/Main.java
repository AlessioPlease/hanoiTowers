package org.alessio;

import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class Main {
	static Instant startTime;

	public static void main(String[] args) {
		int numDisks = getInput();
		boolean recursiveMode = true;
		boolean performanceMode = true;

		TowerOfHanoi towerOfHanoi = new TowerOfHanoi(numDisks, recursiveMode, !performanceMode);
		BackgroundThread statusThread = new BackgroundThread(towerOfHanoi, performanceMode);
		startTime = Instant.now();
		towerOfHanoi.solve();

		Instant finishTime = Instant.now();
		statusThread.shutdown();

		Duration duration = Duration.between(startTime, finishTime);
		printTime(duration, "Time elapsed:");
	}

	private static int getInput() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the number of disks: ");
		int numDisks = scanner.nextInt();
		scanner.close();
		return numDisks;
	}

	public static void printTime(Duration duration, String message) {
		String days = duration.toDays() > 0 ? (" " + duration.toDays() + (duration.toDays() > 1 ? " days," : " day,")) : "";
		String hours = duration.toHours() % 24 > 0 ? (" " + duration.toHours() % 24 + (duration.toHours() % 24 > 1 ? " hours," : " hour,")) : "";
		String minutes = duration.toMinutes() % 60 > 0 ? (" " + duration.toMinutes() % 60 + (duration.toMinutes() % 60 > 1 ? " minutes," : " minute,")) : "";
		String seconds = duration.toSeconds() % 60 > 0 ? (" " + duration.toSeconds() % 60 + (duration.toSeconds() % 60 > 1 ? " seconds," : " second,")) : "";
		String milliseconds = duration.toMillis() % 1000 > 0 ? (" " + duration.toMillis() % 1000 + (duration.toMillis() % 1000 > 1 ? " milliseconds" : " millisecond")) : "";

		System.out.println(message + days + hours + minutes + seconds + milliseconds);
	}

	public static Instant getStartTime() {
		return startTime;
	}
}