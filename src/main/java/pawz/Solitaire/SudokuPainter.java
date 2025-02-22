package pawz.Solitaire;

import org.jetbrains.annotations.NotNull;

public class SudokuPainter {


    public  static record ColorMask(@NotNull int[] colors){};

    private static final int protectedField = 0x1;
    private static final int conflictedField = 0x2;

    private static void findConflicts(int[] colorBuffer, int[] fields, int[] regionMask){
        int[] counter = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int j : regionMask) counter[fields[j]]++;

        for(int value = 1; value <= 9; value++) if (counter[value] > 1)
            for (int j: regionMask) if (fields[j] == value)
                colorBuffer[j] |= conflictedField;
    }

    private static int[] makeColumnRegion(int offset){
        int[] result = new int[9];
        for(int i = 0; i <9; ++i)
            result[i] = 9*i + offset;
        return result;
    }

    private static int[] makeRowRegion(int offset){
        int[] result = new int[9];
        for(int i = 0; i <9; ++i)
            result[i] = i + 9*offset;
        return result;
    }

    private static int[] makeBoxRegion(int offset){
        int[] result = {0, 1, 2, 9, 10, 11, 18, 19, 20};
        int x = offset%3;
        int y = offset/3;
        for(int i = 0; i<9; ++i)
            result[i] = result[i] + 3*x + 27*y;
        return result;
    }

    private static void findConflicts (int[] colorBuffer, SudokuState s){
        int[] fields = s.getFields();
        for(int i = 0; i<9; ++i){
            findConflicts(colorBuffer, fields, makeRowRegion(i));
            findConflicts(colorBuffer, fields, makeColumnRegion(i));
            findConflicts(colorBuffer, fields, makeBoxRegion(i));
        }

    }

    public static ColorMask determineColors(SudokuState s){
        int[] result = new int[81];
        for(int i = 0; i < 81; ++i)
            result[i] = (s.isProtected(i))? protectedField : 0x0;

        findConflicts(result, s);
        return new ColorMask(result);
    }

    private static String getAnsiCode(int color){
        int fg = ((color & protectedField) != 0)? 35: 39;
        int bg = ((color & conflictedField) != 0)? 101 : 40;
        return String.format("[%d;%dm", fg, bg);
    }

    public static void printColored(ColorMask mask, int offset, String value){
        System.out.printf("\033%s%s\033[39;40m", getAnsiCode(mask.colors[offset]), value);
    }

}
