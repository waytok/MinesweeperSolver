public class Cell {
    int x;
    int y;
    int minesAround;
    boolean hasMine;
    boolean opened;
    boolean flagged;
    int closedAround;
    int flaggedAround;

    Cell(int x, int y, int minesAround) {
        this.x = x;
        this.y = y;
        this.minesAround = minesAround;
        this.hasMine = false;
        this.opened = false;
        this.flagged = false;
    }
}