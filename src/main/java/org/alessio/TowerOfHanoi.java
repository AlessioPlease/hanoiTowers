package org.alessio;

import java.util.Stack;

public class TowerOfHanoi {
	private Stack<Integer>[] rods;
	private int numDisks;
	private boolean recursiveMode;
	private final boolean printUpdates;
	private long moves;

	/**
	 * Constructs a TowerOfHanoi object with the specified number of disks.
	 *
	 * @param numDisks the number of disks
	 */
	public TowerOfHanoi(int numDisks, boolean recursiveMode, boolean printUpdates) {
		this.numDisks = numDisks;
		this.recursiveMode = recursiveMode;
		this.printUpdates = printUpdates;
		this.moves = 0;

		rods = new Stack[3];
		for (int i = 0; i < 3; i++) {
			rods[i] = new Stack<>();
		}
		for (int i = numDisks; i > 0; i--) {
			rods[0].push(i);
		}
	}

	/**
	 * Starts the Tower of Hanoi puzzle.
	 */
	public void solve() {
		moves = 0;
		if (printUpdates) printRods();
		if (recursiveMode) {
			moveDisks(numDisks, 0, 2, 1);
		} else {
			moveDisksIteratively(numDisks, 0, 2, 1);
		}
	}

	/**
	 * Recursive method to move the specified number of disks from one rod to another.
	 *
	 * @param n the number of disks to move
	 * @param fromRod the rod to move disks from
	 * @param toRod the rod to move disks to
	 * @param auxRod the auxiliary rod
	 */
	private void moveDisks(int n, int fromRod, int toRod, int auxRod) {
		if (n == 1) {
			rods[toRod].push(rods[fromRod].pop());
			moves++;
			if (printUpdates) printRods();
			return;
		}
		moveDisks(n - 1, fromRod, auxRod, toRod);
		rods[toRod].push(rods[fromRod].pop());
		moves++;
		if (printUpdates) printRods();
		moveDisks(n - 1, auxRod, toRod, fromRod);
	}

	/**
	 * Iterative method to move the specified number of disks from one rod to another.
	 *
	 * @param n the number of disks to move
	 * @param fromRod the rod to move disks from
	 * @param toRod the rod to move disks to
	 * @param auxRod the auxiliary rod
	 */
	private void moveDisksIteratively(int n, int fromRod, int toRod, int auxRod) {
		Stack<Move> stack = new Stack<>();
		stack.push(new Move(n, fromRod, toRod, auxRod));

		while (!stack.isEmpty()) {
			Move move = stack.pop();

			if (move.n == 1) {
				rods[move.toRod].push(rods[move.fromRod].pop());
				moves++;
				if (printUpdates) printRods();
			} else {
				stack.push(new Move(move.n - 1, move.auxRod, move.toRod, move.fromRod));
				stack.push(new Move(1, move.fromRod, move.toRod, move.auxRod));
				stack.push(new Move(move.n - 1, move.fromRod, move.auxRod, move.toRod));
			}
		}
	}

	/**
	 * Prints the current state of the rods to the console.
	 */
	private void printRods() {
		sleep();
		// Clear the terminal (OS dependent, doesn't work in IDEs)
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

	/**
	 * Pauses the execution for a short period, to allow the user
	 * to see the current state of the rods and their moves.
	 */
	private void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public Stack<Integer>[] getRods() {
		return rods;
	}

	public int getNumDisks() {
		return numDisks;
	}

	public long getMoves() {
		return moves;
	}
}