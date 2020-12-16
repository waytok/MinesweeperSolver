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
                mineOpened = true;
            } else if (inputField[cell.x][cell.y] == 0) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((i != 0 || j != 0) && cell.x + i >= 0 && cell.x + i < width && cell.y + j >= 0 && cell.y + j < height) {
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

    public void countProbability(Cell cell) {
        probabilityField[cell.x][cell.y] = 0;
        if (solvedField[cell.x][cell.y].closedAround == 8 || solvedField[cell.x][cell.y].opened || solvedField[cell.x][cell.y].flagged)
            probabilityField[cell.x][cell.y] = 100;
        else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if ((i != 0 || j != 0) && cell.x + i >= 0 && cell.x + i < width && cell.y + j >= 0 && cell.y + j < height
                            && !solvedField[cell.x][cell.y].opened && !solvedField[cell.x][cell.y].flagged
                            && solvedField[cell.x + i][cell.y + j].opened)
                        probabilityField[cell.x][cell.y] = 1 - (1 - probabilityField[cell.x][cell.y]) * (1 -
                                ((solvedField[cell.x + i][cell.y + j].minesAround - solvedField[cell.x + i][cell.y + j].flaggedAround)
                                        / solvedField[cell.x + i][cell.y + j].closedAround));
                }

            }
            probabilityField[cell.x][cell.y] = (mines - minesFlagged) / closed;
        }
    }

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
        do {
            randX = rand.nextInt(width);
            randY = rand.nextInt(height);
        } while (inputField[randX][randY] != 0);
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