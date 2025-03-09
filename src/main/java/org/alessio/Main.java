package org.alessio;

import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class Main {
	static Instant startTime;

	public static void main(String[] args) {
		int numDisks = getInput();
		BackgroundThread statusThread = new BackgroundThread();
		TowerOfHanoi towerOfHanoi = new TowerOfHanoi(numDisks);
		startTime = Instant.now();
		towerOfHanoi.solve();

		Instant finishTime = Instant.now();
		statusThread.shutdown();

		printTimeElapsed(startTime, finishTime);
	}

	private static int getInput() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the number of disks: ");
		int numDisks = scanner.nextInt();
		scanner.close();
		return numDisks;
	}

	private static void printTimeElapsed(Instant startTime, Instant endTime) {
		Duration duration = Duration.between(startTime, endTime);
		String days = duration.toDays() > 0 ? (duration.toDays() + (duration.toDays() > 1 ? " days, " : " day, ")) : "";
		String hours = duration.toHours() % 24 > 0 ? (duration.toHours() + (duration.toHours() > 1 ? " hours, " : " hour, ")) : "";
		String minutes = duration.toMinutes() % 60 > 0 ? (duration.toMinutes() + (duration.toMinutes() > 1 ? " minutes, " : " minute, ")) : "";
		String seconds = duration.toSeconds() % 60 > 0 ? (duration.toSeconds() + (duration.toSeconds() > 1 ? " seconds, " : " second, ")) : "";
		String milliseconds = duration.toMillis() % 1000 > 0 ? (duration.toMillis() + (duration.toMillis() > 1 ? " milliseconds" : " millisecond")) : "";

		System.out.println("Execution time: " + days + " " + hours + " " + minutes + " " + seconds + " " + milliseconds);
	}

	public static Instant getStartTime() {
		return startTime;
	}
}