package org.alessio;

import java.util.Stack;

/**
 * Performance-oriented copy of {@link TowerOfHanoi}.
 * <ul>
 *   <li>Rods are plain {@code int} arrays with a height counter, instead of
 *   {@link Stack} (which is synchronized on every push/pop).</li>
 *   <li>The iterative solver needs no helper stack and allocates nothing:
 *   it alternates moving the smallest disk around the rods in a fixed
 *   direction with the only legal move that doesn't involve it.</li>
 *   <li>The move counter is kept in a local variable and published to a
 *   volatile field every {@link #PUBLISH_INTERVAL} moves, so the status
 *   thread sees a correct value without slowing down every move.</li>
 * </ul>
 */
public class FastTowerOfHanoi {
	// Must be a power of two
	private static final long PUBLISH_INTERVAL = 1 << 16;

	private final int[][] rods;
	private final int[] heights;
	private final int numDisks;
	private final boolean recursiveMode;
	private final boolean printUpdates;
	// Written only by the solving thread, read by the status thread
	private volatile long moves;
	// Moves counter used by the recursive solver
	private long recursiveMoves;

	/**
	 * Constructs a FastTowerOfHanoi object with the specified number of disks.
	 *
	 * @param numDisks the number of disks
	 * @param recursiveMode whether to use the recursive or the iterative solver
	 * @param printUpdates whether to print the rods after each move
	 */
	public FastTowerOfHanoi(int numDisks, boolean recursiveMode, boolean printUpdates) {
		this.numDisks = numDisks;
		this.recursiveMode = recursiveMode;
		this.printUpdates = printUpdates;
		this.moves = 0;

		rods = new int[3][Math.max(numDisks, 0)];
		heights = new int[3];
		for (int i = numDisks; i > 0; i--) {
			rods[0][heights[0]++] = i;
		}
	}

	/**
	 * Starts the Tower of Hanoi puzzle.
	 */
	public void solve() {
		moves = 0;
		if (printUpdates) printRods();
		if (numDisks < 1) return;
		if (recursiveMode) {
			recursiveMoves = 0;
			moveDisks(numDisks, 0, 2, 1);
			moves = recursiveMoves;
		} else {
			moveDisksIteratively();
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
			moveDisk(fromRod, toRod);
			return;
		}
		moveDisks(n - 1, fromRod, auxRod, toRod);
		moveDisk(fromRod, toRod);
		moveDisks(n - 1, auxRod, toRod, fromRod);
	}

	private void moveDisk(int fromRod, int toRod) {
		int disk = rods[fromRod][--heights[fromRod]];
		assert heights[toRod] == 0 || rods[toRod][heights[toRod] - 1] > disk : "illegal move";
		rods[toRod][heights[toRod]++] = disk;
		if ((++recursiveMoves & (PUBLISH_INTERVAL - 1)) == 0) moves = recursiveMoves;
		if (printUpdates) printRods();
	}

	/**
	 * Iterative method to move all the disks from rod 0 to rod 2.
	 * Odd moves always move the smallest disk one rod forward (0 -> 1 -> 2 -> 0
	 * with an even number of disks, 0 -> 2 -> 1 -> 0 with an odd number).
	 * Even moves make the only legal move between the other two rods.
	 */
	private void moveDisksIteratively() {
		final int[][] r = rods;
		final int[] h = heights;
		final int n = numDisks;
		final int step = (n % 2 == 0) ? 1 : 2;
		int smallest = 0;
		long m = 0;

		while (true) {
			// Move the smallest disk (always disk 1)
			int next = smallest + step;
			if (next >= 3) next -= 3;
			h[smallest]--;
			r[next][h[next]++] = 1;
			smallest = next;
			m++;
			if (printUpdates) {
				moves = m;
				printRods();
			}
			if (h[2] == n) break;

			// Move between the other two rods, in the only legal direction
			int a = smallest + 1;
			if (a == 3) a = 0;
			int b = a + 1;
			if (b == 3) b = 0;
			int topA = h[a] == 0 ? Integer.MAX_VALUE : r[a][h[a] - 1];
			int topB = h[b] == 0 ? Integer.MAX_VALUE : r[b][h[b] - 1];
			if (topA < topB) {
				h[a]--;
				r[b][h[b]++] = topA;
			} else {
				h[b]--;
				r[a][h[a]++] = topB;
			}
			m++;

			if (printUpdates) {
				moves = m;
				printRods();
			} else if ((m & (PUBLISH_INTERVAL - 1)) == 0) {
				moves = m;
			}
		}
		moves = m;
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
				if (heights[j] > numDisks - 1 - i) {
					int diskSize = rods[j][numDisks - 1 - i];
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

	/**
	 * Returns the rods as they are after {@link #getMoves()} moves.
	 * The state is computed from the move count rather than read from the
	 * arrays, so it is consistent even while another thread is solving.
	 */
	public Stack<Integer>[] getRods() {
		return rodsAfter(numDisks, moves);
	}

	/**
	 * Computes the rods after the given number of moves of the optimal solution
	 * (moving all disks from rod 0 to rod 2). Disk d has moved
	 * round(moves / 2^d) times, always cycling in the same direction:
	 * 0 -> 2 -> 1 -> 0 if (numDisks - d) is even, 0 -> 1 -> 2 -> 0 otherwise.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	static Stack<Integer>[] rodsAfter(int numDisks, long moves) {
		Stack<Integer>[] result = new Stack[3];
		for (int i = 0; i < 3; i++) {
			result[i] = new Stack<>();
		}
		for (int d = numDisks; d > 0; d--) {
			long diskMoves = d < 64 ? (moves >>> d) + ((moves >>> (d - 1)) & 1) : 0;
			int step = (numDisks - d) % 2 == 0 ? 2 : 1;
			int rod = (int) (diskMoves % 3 * step % 3);
			result[rod].push(d);
		}
		return result;
	}

	public int getNumDisks() {
		return numDisks;
	}

	public long getMoves() {
		return moves;
	}
}
