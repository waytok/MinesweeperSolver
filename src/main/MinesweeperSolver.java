import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MinesweeperSolver {

    @Argument(metaVar = "file", usage = "Input file name")
    private String inputFileName;

    public MinesweeperSolver() {
    }

    public static void main(String[] args) {
        new MinesweeperSolver().launch(args);
    }

    private void launch(String[] args) {
        List<String> input = new ArrayList<>();
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
        }

        FileReader fileReader;
        try {
            fileReader = new FileReader(inputFileName);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            while (line != null) {
                input.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int sizeX = Integer.parseInt(input.get(0).split(" ")[0]);
        int sizeY = Integer.parseInt(input.get(0).split(" ")[1]);
        int mines = Integer.parseInt(input.get(0).split(" ")[2]);

        int[][] inputField = new int[sizeX][sizeY];
        for (int i = 1; i < input.size(); i++) {
            int index = 0;
            for (String j: input.get(i).split(" ")) {
                inputField[index][i-1] = Integer.parseInt(j);
                index++;
            }
        }

        Random rand = new Random();
        int randX = rand.nextInt();
        int randY = rand.nextInt();

        Solver solver = new Solver(sizeX, sizeY, mines, inputField);
        solver.solve(randX, randY);

        StringBuilder output = new StringBuilder();
        int[][] solved = new int[sizeX][sizeY];
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                solved[j][i] = solver.solvedField[j][i].minesAround;
                // "c" for closed, "f" for flagged mines.
                output.append(solver.solvedField[j][i].flagged ? "f " : solved[j][i] == -1 ? "c " : solved[j][i] + " ");
            }
            output.append("\n");
        }

        if (Arrays.deepEquals(solved, inputField)) System.out.println("Solved\n");
        else System.out.println("Not solved\n");
        System.out.println("Calculated field:");
        System.out.println(output);
    }
}