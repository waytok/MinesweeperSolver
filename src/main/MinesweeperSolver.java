import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File("input/" + inputFileName +".txt"));
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
        Field field = new Field(sizeX, sizeY, mines);
        field.inputField(input);
        Solver solver = new Solver(sizeX, sizeY, mines);
        solver.solve(field);
        Field solvedField = solver.solve(field);
        List<String> solved = new ArrayList();
        StringBuilder current = new StringBuilder();
        solved.add(sizeX + " " + sizeY + " " + mines);
        for (int i = 0; i < sizeY; i++) {
            current.append(solvedField.get(0, i));
            for (int j = 1; j < sizeX; j++) {
                current.append(" ").append(solvedField.get(j, i));
            }
            solved.add(current.toString());
            current = new StringBuilder();
        }
        if (input.equals(solved)) System.out.println("solved");
    }
}