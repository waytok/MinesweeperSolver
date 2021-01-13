public class Cell {
    int x;
    int y;
    int minesAround;
    boolean opened;
    boolean flagged;
    int closedAround;
    int flaggedAround;

    Cell(int x, int y, int minesAround) {
        this.x = x;
        this.y = y;
        this.minesAround = minesAround;
        this.opened = false;
        this.flagged = false;
    }
}