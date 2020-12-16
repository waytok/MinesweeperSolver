import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SolverTests {
    @Test
    void Solver() {
        String inputFileName = "input/example1.txt";
        List<String> input = new ArrayList<>();
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
            for (String j : input.get(i).split(" ")) {
                inputField[index][i - 1] = Integer.parseInt(j);
                index++;
            }
        }

        Solver solver = new Solver(sizeX, sizeY, mines, inputField);
        solver.solve();
        int[][] solved = new int[sizeX][sizeY];
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) solved[j][i] = solver.solvedField[j][i].minesAround;
        }
        assertArrayEquals(inputField, solved);
    }

}