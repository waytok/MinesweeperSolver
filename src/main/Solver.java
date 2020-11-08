import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Solver {
    int sizeX;
    int sizeY;
    int mines;
    int foundMines;
    Field probabilityField;
    Field supposedField;
    Set<Pair> known = new HashSet();
    Random randomizer = new Random();

    Solver(int sizeX, int sizeY, int mines){
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.probabilityField = new Field(this.sizeX, this.sizeY, mines);
        this.probabilityField.createField();
        this.foundMines = 0;
        this.mines = mines;
    }

    public Field solve(Field field) {
        while (foundMines<mines){
            int randX = randomizer.nextInt(field.sizeX);
            int randY = randomizer.nextInt(field.sizeY);
            if (known.contains(new Pair(randX, randY))) {
                randX = randomizer.nextInt(field.sizeX);
                randY = randomizer.nextInt(field.sizeY);
            }
            if (field.get(randX, randY) == 9) {
                System.out.println("mine was selected");
            } else {
                if (probabilityField.get(randX, randY) == -1)
                    probabilityField.put(randX, randY, countProbability(randX, randY, field));
                else probabilityField.put(randX, randY,
                        1 - (1-probabilityField.get(randX, randY)) * (1 - countProbability(randX, randY, field)));
                known.add(new Pair(randX, randY));
                foundMines+=probabilityField.iteratingAround(100, randX, randY);

            }
        }

        return supposedField;
    }

    public int countProbability(int x, int y, Field field){
        return 100*(field.get(x,y)-field.countMinesAround(x,y))/ probabilityField.countUnknownAround(x, y);
    }

    public void openArea(int x, int y) {

    }
}