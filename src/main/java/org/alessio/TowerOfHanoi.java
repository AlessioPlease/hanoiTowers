package org.alessio;

import java.util.Stack;

public class TowerOfHanoi {
	private int numDisks;
	private static Stack<Integer>[] rods;

	public TowerOfHanoi(int numDisks) {
		this.numDisks = numDisks;
		rods = new Stack[3];
		for (int i = 0; i < 3; i++) {
			rods[i] = new Stack<>();
		}
		for (int i = numDisks; i > 0; i--) {
			rods[0].push(i);
		}
	}

	public void solve() {
		//printRods();
		moveDisks(numDisks, 0, 2, 1);
		printRods(); // temporary fix to only print the final state
	}

	private void moveDisks(int n, int fromRod, int toRod, int auxRod) {
		if (n == 1) {
			rods[toRod].push(rods[fromRod].pop());
			//printRods();
			return;
		}
		moveDisks(n - 1, fromRod, auxRod, toRod);
		rods[toRod].push(rods[fromRod].pop());
		//printRods();
		moveDisks(n - 1, auxRod, toRod, fromRod);
	}

	private void printRods() {
		//sleep();
		// Clear the terminal
		System.out.print("\033[H\033[2J");
		System.out.flush();

		StringBuilder[] rodStrings = new StringBuilder[numDisks];
		for (int i = 0; i < numDisks; i++) {
			rodStrings[i] = new StringBuilder();
			for (int j = 0; j < 3; j++) {
				if (rods[j].size() > numDisks - 1 - i) {
					int diskSize = rods[j].get(numDisks - 1 - i);
					rodStrings[i].append(" ".repeat(numDisks - diskSize));
					rodStrings[i].append("O".repeat(diskSize * 2 - 1));
					rodStrings[i].append(" ".repeat(numDisks - diskSize));
				} else {
					rodStrings[i].append(" ".repeat(numDisks - 1)).append("|").append(" ".repeat(numDisks - 1));
				}
				if (j < 2) {
					rodStrings[i].append("   ");
				}
			}
		}
		for (StringBuilder rodString : rodStrings) {
			System.out.println(rodString.toString());
		}
		System.out.println();
	}

	private void sleep() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static Stack<Integer>[] getRods() {
		return rods;
	}
}