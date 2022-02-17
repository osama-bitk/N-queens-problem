import java.util.*;

public class Queens
{
    private static int boardSize = 10;

    // creates a valid genotype with random values
    public static Integer [] randomGenotype() 
    {
        Integer [] genotype = new Integer [boardSize];

        for (int i = 0; i < genotype.length; i++) {
            genotype[i] = generateRandomRange(1, 10);
        }

        return genotype;
    }
    
    // swaps 2 genes in the genotype
    // the swap happens with probability p, so if p = 0.8
    // then 8 out of 10 times this method is called, a swap happens
    public static Integer[] swapMutate(Integer[] genotype, double p) 
    {
        double n = generateRandomRange(0, 100) / 100d;
        if (n <= p)  {

            int first = generateRandomRange(0, genotype.length - 1);
            int second = generateRandomExcept(0, genotype.length - 1, first);

            Integer temp = genotype[first];
            genotype[first] = genotype[second];
            genotype[second] = temp;
        }

        return genotype;
    }
    
    // creates 2 child genotypes using the 'cut-and-crossfill' method
    public static Integer[][] cutAndCrossfill(Integer[] parent0, Integer[] parent1)
    {
        Integer[][] children = new Integer[2][boardSize];

        children[0] = new Integer[parent1.length];
        children[1] = new Integer[parent0.length];

        int crossover = parent0.length / 2 - 1;

        for (int i = 0; i <= crossover; i++) {
            children[0][i] = parent0[i];
            children[1][i] = parent1[i];
        }

        crossfill(parent1, children[0], crossover);
        crossfill(parent0, children[1], crossover);
        
        return children;
    }
    
    // calculates the fitness of an individual
    public static int measureFitness(Integer[] genotype)  
    {
        /*  The initial fitness is the maximum pairs of queens
         * that can be in check (all possible pairs in check).
         * This is the maximum fitness value.
         * Deduct 1 from this value for every pair of queens
         * found to be in check.
         * The lower the score, the lower the fitness.
         */
        int fitness = (int) (0.5 * boardSize * (boardSize - 1));
        
        boolean[][][][] total = new boolean[boardSize][boardSize][boardSize][boardSize];
        boolean[][] board = ArrayIntoBoard(genotype, boardSize, boardSize);

        int count = 0;
        for (int i = 0; i < genotype.length; i++) {
            count += countQueenPairs(board, i, genotype[i] - 1, total);
        }

        return fitness - count;
    }
    
    // HELPER METHODS
    private static int generateRandomRange(int start, int end) 
    {
        Random random = new Random();
        int n = random.nextInt(end - start + 1);
        return n  + start;
    }

    private static int generateRandomExcept(int start, int end, int except)  {
        do  {
            int n = generateRandomRange(start, end);
            if (n != except)  {
                return n;
            }
        } while (true);
    }

    private static void crossfill(Integer[] parent, Integer[] child, int crossover)  {
        boolean[] used = new boolean[parent.length];

        for (int i = 0; i < parent.length; i++) {
            for (int j = 0; j <= crossover; j++) {
                if (child[j] == parent[i]) {
                    used[i] = true;
                }
            }
        }

        int k = crossover + 1;
        int z = crossover + 1;
        while (k < child.length)  {
            if (z >= used.length)  {
                z = 0;
            }

            if (used[z])  {
                z++;
                continue;
            }

            used[z] = true;
            child[k++] = parent[z++];
        }
    }


    private static boolean[][] ArrayIntoBoard(Integer[] array, int rows, int columns)  {
        boolean[][] board = new boolean[rows][columns];

        for (int i = 0; i < array.length; i++) {
            board[i][array[i] - 1] = true;
        }

        return board;
    }

    private static int countQueenPairs(boolean[][] board, int row, int column, boolean[][][][] totalUsed)  {
        boolean[][] used = new boolean[board.length][board[0].length];

        int count = 0;

        for (int j = column + 1; j < board[row].length; j++) {
            if (board[row][j] && !used[row][j] &&
                    !totalUsed[row][column][row][j] && !totalUsed[row][j][row][column])  {
                used[row][j] = true;
                count++;
            }
        }

        for (int i = row + 1; i < board.length; i++) {
            if (board[i][column] && !used[i][column] &&
                    !totalUsed[row][column][i][column] && !totalUsed[i][column][row][column])  {
                used[i][column] = true;
                count++;
            }
        }

        int i = row + 1;
        int j = column + 1;
        while (i < board.length && j < board[i].length)  {
            if (board[i][j] && !used[i][j] &&
                    !totalUsed[row][column][i][j] && !totalUsed[i][j][row][column])  {
                used[i][j] = true;
                count++;
            }

            i++;
            j++;
        }

        i = row + 1;
        j = column - 1;
        while (i >= 0 && j >= 0 && i < board.length)  {
            if (board[i][j] && !used[i][j] &&
                    !totalUsed[row][column][i][j] && !totalUsed[i][j][row][column])  {
                used[i][j] = true;
                count++;
            }

            i++;
            j--;
        }

        totalUsed[row][column] = used;

        return count;
    }
}