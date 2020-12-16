import java.util.*;

public class Solver {
    int height;
    int width;
    int mines;
    int[][] inputField;
    double[][] probabilityField;
    Cell[][] solvedField;
    List<Cell> cellsToAnalyze;

    Solver(int width, int height, int mines, int[][] inputField) {
        this.height = height;
        this.width = width;
        this.mines = mines;
        this.inputField = inputField;
        this.cellsToAnalyze = new LinkedList<>();
        solvedField = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) solvedField[i][j] = new Cell(i, j, -1);
        }
    }

    public void checkAround(Cell cell) {
        cell.flaggedAround = 0;
        cell.closedAround = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i != 0 || j != 0) && cell.x + i >= 0 && cell.x + i < width && cell.y + j >= 0 && cell.y + j < height)
                    if (solvedField[cell.x + i][cell.y + j].flagged) cell.flaggedAround += 1;
                    else if (!solvedField[cell.x + i][cell.y + j].opened) cell.closedAround += 1;
            }
        }
    }

    public void open(Cell cell) {
        if (!cell.opened) {
            cell.opened = true;
            if (inputField[cell.x][cell.y] > 0 && inputField[cell.x][cell.y] < 9) cellsToAnalyze.add(cell);
            if (inputField[cell.x][cell.y] == 9) {
                cell.hasMine = true;
            } else if (inputField[cell.x][cell.y] == 0) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((i != 0 || j != 0) && cell.x + i >= 0 && cell.x + i < width && cell.y + j >= 0 && cell.y + j < height) {
                            open(solvedField[cell.x + i][cell.y + j]);
                        }
                    }
                }
            }
            cell.minesAround = inputField[cell.x][cell.y];
            checkAround(cell);
        }
    }

    public void flag(Cell cell) {
        cell.minesAround = 9;
        cell.flagged = true;
    }

/*    public void countProbability(Cell cell) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i != 0 || j != 0) && cell.x + i >= 0 && cell.x + i < width && cell.y + j >= 0 && cell.y + j < height && !solvedField[cell.x][cell.y].opened) {
                    probabilityField[cell.x + i][cell.y + j] = (cell.minesAround - cell.flaggedAround) / cell.closedAround;
                }
            }
        }
    }*/

    public int iterating() {
        int countChanges = 0;
        List<Cell> cellsToOpen = new ArrayList();
        Iterator<Cell> iterator = cellsToAnalyze.iterator();
        while (iterator.hasNext()) {
            Cell element = iterator.next();
            checkAround(solvedField[element.x][element.y]);
            if (solvedField[element.x][element.y].minesAround - solvedField[element.x][element.y].flaggedAround == solvedField[element.x][element.y].closedAround
                    || solvedField[element.x][element.y].minesAround == solvedField[element.x][element.y].flaggedAround) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((i != 0 || j != 0) && element.x + i >= 0 && element.x + i < width && element.y + j >= 0 &&
                                element.y + j < height && !solvedField[element.x + i][element.y + j].flagged && !solvedField[element.x + i][element.y + j].opened) {
                            if (solvedField[element.x][element.y].minesAround - solvedField[element.x][element.y].flaggedAround
                                    == solvedField[element.x][element.y].closedAround) {
                                flag(solvedField[element.x + i][element.y + j]);
                                countChanges++;
                            }
                            if (solvedField[element.x][element.y].minesAround == solvedField[element.x][element.y].flaggedAround) {
                                cellsToOpen.add(solvedField[element.x + i][element.y + j]);
                                countChanges++;
                            }
                        }
                    }
                }
                iterator.remove();
            }
        }
        for (Cell i : cellsToOpen) {
            open(i);
        }
        return countChanges;
    }

    public void solve() {
        Random rand = new Random();
        int randX;
        int randY;
        //To prevent the minesweeper solver from hitting a mine on the first move,
        // we will make it so that it does not run the solution from an inappropriate cell
        do  {
            randX = rand.nextInt(width);
            randY = rand.nextInt(height);
        } while (inputField[randX][randY] != 0);
        open(solvedField[randX][randY]);
        int changes = iterating();
        while (changes != 0) {
            changes = iterating();
        }
    }
}