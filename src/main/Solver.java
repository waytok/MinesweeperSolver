import java.util.*;

public class Solver {
    int height;
    int width;
    int mines;
    int closed;
    int minesFlagged;
    boolean mineOpened;
    int[][] inputField;
    double[][] probabilityField;
    Cell[][] solvedField;
    List<Cell> cellsToAnalyze;

    Solver(int width, int height, int mines, int[][] inputField) {
        this.height = height;
        this.width = width;
        this.mines = mines;
        this.minesFlagged = 0;
        this.mineOpened = false;
        this.closed = width * height;
        this.inputField = inputField;
        this.cellsToAnalyze = new LinkedList<>();
        probabilityField = new double[width][height];
        solvedField = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) solvedField[i][j] = new Cell(i, j, -1);
        }
    }

    public void fill(){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (inputField[i][j] == 9)
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            if ((k != 0 || l != 0) && i + k >= 0 && i + k < width && j + l >= 0 &&
                                    j + l < height && inputField[i + k][j + l] != 9) inputField[i + k][j + l]++;
                        }
                    }
            }
        }
     }

    // Check for non-opened or flagged cells around input cells.
    public void checkAround(Cell cell) {
        cell.flaggedAround = 0;
        cell.closedAround = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i != 0 || j != 0) && cell.x + i >= 0 && cell.x + i < width &&
                        cell.y + j >= 0 && cell.y + j < height)
                    if (solvedField[cell.x + i][cell.y + j].flagged) cell.flaggedAround += 1;
                    else if (!solvedField[cell.x + i][cell.y + j].opened) cell.closedAround += 1;
            }
        }
    }

    // Open the cell or area around equal to zero cells, if solver tries to open cell with mine mineOpened equals true.
    public void open(Cell cell) {
        if (!cell.opened) {
            cell.opened = true;
            if (inputField[cell.x][cell.y] > 0 && inputField[cell.x][cell.y] < 9) cellsToAnalyze.add(cell);
            if (inputField[cell.x][cell.y] == 9) {
                mineOpened = true;
            } else if (inputField[cell.x][cell.y] == 0) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((i != 0 || j != 0) && cell.x + i >= 0 && cell.x + i < width &&
                                cell.y + j >= 0 && cell.y + j < height) {
                            open(solvedField[cell.x + i][cell.y + j]);
                            closed--;
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
        minesFlagged += 1;
        closed--;
    }

    // Count the probability that mine is in a cell based on data from cells around.
    public void countProbability(Cell cell) {
        probabilityField[cell.x][cell.y] = 0;
        if (solvedField[cell.x][cell.y].closedAround == 8 || solvedField[cell.x][cell.y].opened ||
                solvedField[cell.x][cell.y].flagged) probabilityField[cell.x][cell.y] = 100;
        else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if ((i != 0 || j != 0) && cell.x + i >= 0 && cell.x + i < width && cell.y + j >= 0 &&
                            cell.y + j < height && !solvedField[cell.x][cell.y].opened &&
                            !solvedField[cell.x][cell.y].flagged && solvedField[cell.x + i][cell.y + j].opened)
                        probabilityField[cell.x][cell.y] = 1.0 - (1 - probabilityField[cell.x][cell.y]) *
                                (1 - ((double) (solvedField[cell.x + i][cell.y + j].minesAround -
                                        solvedField[cell.x + i][cell.y + j].flaggedAround)
                                        / solvedField[cell.x + i][cell.y + j].closedAround));
                }

            }
            probabilityField[cell.x][cell.y] = (double) (mines - minesFlagged) / closed;
        }
    }

    // Find the minimum probability for each closed cell next to non-zero or flagged cells by iterating through
    // an array of all cells.
    public Cell minProbability() {
        double min = 0;
        int xMin = 0;
        int yMin = 0;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (!solvedField[i][j].opened && !solvedField[i][j].flagged) {
                    countProbability(solvedField[i][j]);
                    if (min == 0 || (probabilityField[i][j] < min && probabilityField[i][j] != 0)) {
                        min = probabilityField[i][j];
                        xMin = i;
                        yMin = j;
                    }
                }
        return solvedField[xMin][yMin];
    }

    // Iterate through cellsToAnalyze and open or flag cells whenever possible.
    public int iterating() {
        int countChanges = 0;
        List<Cell> cellsToOpen = new ArrayList();
        Iterator<Cell> iterator = cellsToAnalyze.iterator();
        while (iterator.hasNext()) {
            Cell element = iterator.next();
            checkAround(solvedField[element.x][element.y]);
            if (solvedField[element.x][element.y].minesAround - solvedField[element.x][element.y].flaggedAround ==
                    solvedField[element.x][element.y].closedAround ||
                    solvedField[element.x][element.y].minesAround == solvedField[element.x][element.y].flaggedAround) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((i != 0 || j != 0) && element.x + i >= 0 && element.x + i < width && element.y + j >= 0 &&
                                element.y + j < height && !solvedField[element.x + i][element.y + j].flagged &&
                                !solvedField[element.x + i][element.y + j].opened) {
                            if (solvedField[element.x][element.y].minesAround -
                                    solvedField[element.x][element.y].flaggedAround ==
                                    solvedField[element.x][element.y].closedAround) {
                                flag(solvedField[element.x + i][element.y + j]);
                                countChanges++;
                            }
                            if (solvedField[element.x][element.y].minesAround ==
                                    solvedField[element.x][element.y].flaggedAround) {
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

    // Open cells until all mines are flagged or the cell with a mine is not opened.
    public void solve(int randX, int randY) {
        fill();
        open(solvedField[randX][randY]);
        int changes = iterating();
        while (changes != 0  && mines > minesFlagged && !mineOpened) {
            changes = iterating();
            while (changes == 0 && !mineOpened) {
                open(minProbability());
                changes = iterating();
            }
        }
    }
}