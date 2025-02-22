package pawz.Solitaire;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SudokuLoader {

    private final static int[] easyPuzzle = {
            0, 0, 2,    0, 0, 0,    0, 0, 0,
            8, 4, 7,    2, 5, 0,    0, 0, 9,
            5, 9, 6,    8, 3, 1,    7, 4, 2,

            9, 6, 8,    7, 0, 0,    4, 2, 3,
            0, 0, 0,    6, 9, 4,    1, 0, 0,
            0, 0, 4,    3, 0, 8,    6, 9, 7,

            0, 0, 5,    0, 0, 0,    0, 0, 0,
            0, 0, 9,    1, 6, 3,    0, 0, 0,
            0, 0, 0,    0, 8, 0,    0, 0, 6
    };

    private final static int[] mediumPuzzle = {
            3, 0, 0,    8, 7, 0,    4, 0, 0,
            0, 7, 0,    2, 9, 6,    5, 0, 0,
            0, 0, 0,    1, 0, 3,    0, 2, 0,

            0, 0, 1,    9, 0, 4,    0, 0, 8,
            4, 9, 0,    3, 8, 0,    2, 0, 6,
            0, 0, 0,    6, 0, 0,    9, 5, 0,

            0, 0, 0,    0, 0, 2,    0, 3, 9,
            0, 0, 0,    7, 6, 9,    1, 0, 0,
            0, 1, 9,    4, 0, 0,    0, 0, 0
    };

    private final static int[] hardPuzzle = {
            2, 0, 0,    4, 5, 0,    0, 9, 0,
            0, 0, 7,    1, 0, 3,    4, 0, 5,
            5, 0, 4,    0, 0, 0,    1, 0, 2,

            3, 1, 5,    0, 0, 2,    0, 0, 0,
            0, 2, 0,    9, 0, 8,    0, 0, 3,
            0, 8, 0,    0, 0, 0,    6, 2, 4,

            0, 0, 0,    3, 6, 0,    0, 7, 9,
            6, 5, 3,    0, 9, 0,    2, 0, 0,
            9, 7, 0,    2, 1, 0,    3, 0, 0
    };

    public static Collection<SudokuState> createSudokuStates(){
        List<SudokuState> result = new ArrayList<>();
        result.add(new SudokuState(easyPuzzle));
        result.add(new SudokuState(mediumPuzzle));
        result.add(new SudokuState(hardPuzzle));
        return result;
    };
}
