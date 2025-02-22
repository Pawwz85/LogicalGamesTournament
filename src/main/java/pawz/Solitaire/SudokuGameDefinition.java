package pawz.Solitaire;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.GameDefinition;

public class SudokuGameDefinition implements GameDefinition<SudokuMove, SudokuState> {
    @Override
    public boolean isAcceptable(@NotNull SudokuState s) {
        boolean[] temp = new boolean[10];
        int[]  sectionMask = new int[9];
        int[]  squareMask = {0, 1, 2, 9, 10, 11, 18, 19, 20};
        int[] fields = s.getFields();

        for(int sectionId = 0; sectionId <27; ++sectionId){
            temp[9] = false;
            for(int i = 0; i < 9; ++i){
                temp[i] = false;

                switch (sectionId){
                    case 0, 1, 2, 3, 4, 5, 6,7, 8:
                        sectionMask[i] = 9*sectionId + i;
                        break;
                    case 9, 10, 11, 12, 13, 14, 15, 16, 17:
                        sectionMask[i] = (sectionId - 9) + 9*i;
                        break;
                    case 18, 19, 20, 21, 22, 23, 24, 25, 26:
                        int x = sectionId%3;
                        int y = (sectionId - 18)/3;
                        sectionMask[i] = squareMask[i] + 3*x + 9*y;
                }
            }


            for(int i = 0; i < 9; ++i){
                if (temp[fields[sectionMask[i]]] || fields[sectionMask[i]] == 0)
                    return false;
                temp[fields[sectionMask[i]]] = true;
            }
        }
        return true;
    }

    @Override
    public boolean isMoveLegal(@NotNull SudokuState s, @NotNull SudokuMove m) {
        int[] fields = s.getFields();

        if(m.squareId < 0 || m.squareId >= fields.length)
            return false;

        if (s.isProtected(m.squareId))
            return  false;

        return m.value >= 1 && m.value < 10;    }

    @NotNull
    @Override
    public SudokuState makeMove(@NotNull SudokuState s, @NotNull SudokuMove m) {
        int[] fields = s.getFields();
        fields[m.squareId] = m.value;
        return new SudokuState(fields, s.getProtectedFields());
    }
}
