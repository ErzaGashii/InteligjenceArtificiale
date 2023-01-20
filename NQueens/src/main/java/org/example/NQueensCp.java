package org.example;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.DecisionBuilder;
import com.google.ortools.constraintsolver.IntVar;
import com.google.ortools.constraintsolver.Solver;

/** N-Queens Problem. */
public final class NQueensCp {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();
        // Instantiate the solver.
        Solver solver = new Solver("N-Queens");

        int boardSize = 8;
        IntVar[] queens = new IntVar[boardSize];
        for (int i = 0; i < boardSize; ++i) {
            queens[i] = solver.makeIntVar(0, boardSize - 1, "x" + i);
        }

        // Define constraints.
        // All rows must be different.
        solver.addConstraint(solver.makeAllDifferent(queens));

        // All columns must be different because the indices of queens are all different.
        // No two queens can be on the same diagonal.
        IntVar[] diag1 = new IntVar[boardSize];
        IntVar[] diag2 = new IntVar[boardSize];
        for (int i = 0; i < boardSize; ++i) {
            diag1[i] = solver.makeSum(queens[i], i).var();
            diag2[i] = solver.makeSum(queens[i], -i).var();
        }
        solver.addConstraint(solver.makeAllDifferent(diag1));
        solver.addConstraint(solver.makeAllDifferent(diag2));

        // Create the decision builder to search for solutions.
        final DecisionBuilder db =
                solver.makePhase(queens, Solver.CHOOSE_FIRST_UNBOUND, Solver.ASSIGN_MIN_VALUE);

        boolean[][] blocksArray = new boolean[8][8];
        blocksArray[0][0] = true;
        blocksArray[7][0] = true;

        int solutionCount = 0;
        solver.newSearch(db);
        while (solver.nextSolution()) {
            System.out.println("Solution " + solutionCount);
            boolean skipSolution = false;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (blocksArray[i][j] && queens[j].value() == i) {
                        skipSolution = true;
                    }
                }
            }
            if (skipSolution) {
                continue;
            }
            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    if (queens[j].value() == i) {
                        System.out.print("Q");
                    } else {
                        System.out.print("_");
                    }
                    if (j != boardSize - 1) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
            solutionCount++;
        }
        solver.endSearch();

        // Statistics.
        System.out.println("Statistics");
        System.out.println("  failures: " + solver.failures());
        System.out.println("  branches: " + solver.branches());
        System.out.println("  wall time: " + solver.wallTime() + "ms");
        System.out.println("  Solutions found: " + solutionCount);
    }

    private NQueensCp() {}
}