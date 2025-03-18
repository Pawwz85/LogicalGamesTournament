package pawz.demo;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.GameDefinition;

public class SudokuGameDefinition implements GameDefinition<SudokuMove, SudokuState> {
    private static SudokuGameDefinition instance;

    private final ByteDecoder<SudokuMove> moveByteDecoder = new SudokuMoveDecoder();
    private final ByteDecoder<SudokuState> stateByteDecoder = new SudokuStateDecoder();

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
                    case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8:
                        sectionMask[i] = 9*sectionId + i;
                        break;
                    case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17:
                        sectionMask[i] = (sectionId - 9) + 9*i;
                        break;
                    case 18: case 19: case 20: case 21: case 22: case 23: case 24: case 25: case 26:
                        int x = sectionId%3;
                        int y = (sectionId - 18)/3;
                        sectionMask[i] = squareMask[i] + 3*x + 27*y;
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

    @Override
    public ByteDecoder<SudokuMove> moveByteDecoder() {
        return moveByteDecoder;
    }

    @Override
    public ByteDecoder<SudokuState> stateByteDecoder() {
        return stateByteDecoder;
    }

    public static SudokuGameDefinition getInstance(){
        if(instance == null)
            instance = new SudokuGameDefinition();

        return  instance;
    }

}
