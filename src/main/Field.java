import java.util.List;

public class Field {
    int sizeX;
    int sizeY;
    int mines;
    int[][] field;

    Field(int sizeX, int sizeY, int mines) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.mines = mines;
        this.field = new int[sizeX][sizeY];
    }

    public void createField() {
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) this.field[j][i] = -1;
        }
    }

    public void inputField(List<String> input) {
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) this.field[j][i] = Integer.parseInt((input.get(i + 1).split(" "))[j]);
        }
    }

    public int get(int x, int y) {
        return field[x][y];
    }

    public void put(int x, int y, int value) {
        this.field[x][y] = value;
    }

    public int iteratingAround(int value, int x, int y){
        int counter = 0;
        if (x != 0) {
            if (this.field[x-1][y] == value) counter +=1;
            if (y != 0) if (this.field[x-1][y-1] == value) counter +=1;
            if (y != sizeY-1) if (this.field[x-1][y+1] == value) counter +=1;
        }
        if (x != sizeX-1) {
            if (this.field[x+1][y] == value) counter +=1;
            if (y != 0) if (this.field[x+1][y-1] == value) counter +=1;
            if (y != sizeY-1) if (this.field[x+1][y+1] == value) counter +=1;
        }
        if (y != 0) if (this.field[x][y-1] == value) counter +=1;
        if (y != sizeY-1) if (this.field[x][y+1] == value) counter +=1;
        return counter;
    }

    public int countUnknownAround(int x, int y) {
        return iteratingAround(-1, x, y);
    }

    public int countMinesAround(int x, int y) {
        return iteratingAround(9, x, y);
    }
}