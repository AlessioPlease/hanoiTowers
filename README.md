# Tower of Hanoi
This project implements the Tower of Hanoi puzzle in Java. The Tower of Hanoi is a classic problem in computer science and mathematics, involving moving a set of disks from one rod to another, following specific rules.

### Improved performance "mode"
To improve performance, you can change the performanceMode variable in the Main class. This mode will not print each move of the solution, it will instead provide updates at regular intervals about the progress of the solution.

### Fast implementation
`FastTowerOfHanoi` is a faster copy of `TowerOfHanoi`: the rods are plain `int` arrays instead of `Stack`, and the iterative solver needs no helper stack. It is used when the `fastMode` variable in the Main class is `true`; set it to `false` to run the original `TowerOfHanoi`.
