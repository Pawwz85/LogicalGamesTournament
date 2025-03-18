package pawz.demo;

public class SudokuPuzzleUIView {

    public char emptySymbol = '#';
    public char verticalDelimiter = '|';
    public char horizontalDelimiter = '-';
    public int leftPaddingSpaces = 20;


    private void renderPadding(){
        for(int i = 0; i< leftPaddingSpaces; ++i)
            System.out.print(" ");
    }

    private void renderHorizontalLine(){
        renderPadding();
        for(int i = 0; i< 13; ++i){
            System.out.print(horizontalDelimiter);
            System.out.print(" ");
        }
        System.out.print("\n");
    }

    private void renderSudokuLine(SudokuState state, int lineNumber, SudokuPainter.ColorMask mask){
        renderPadding();
        int fieldValue;
        String fieldDisplay;
        for(int i = 0; i < 3; ++i){
            System.out.print(verticalDelimiter);
            System.out.print(" ");
            for(int j = 0; j < 3; ++j) {
                fieldValue = state.getFields()[lineNumber*9 + 3 * i + j];

                if(fieldValue != 0)
                    SudokuPainter.printColored(mask,lineNumber*9 + 3 * i + j, String.valueOf(fieldValue));
                else
                    System.out.print(emptySymbol);

                System.out.print(" ");
            }
        }
        System.out.print(verticalDelimiter);
        System.out.print("\n");
    }

    public void render(SudokuState state, String additionalText){

        /*
            - - - - - - - - - - - - -
            | 3 # 5 | 6 4 1 | 9 2 # |
            | # # # | 2 3 # | 3 1 4 |
            | 1 2 4 | 5 # # | # # 8 |
            - - - - - - - - - - - - -
            | 3 # 5 | 6 4 1 | 9 2 # |
            | # # # | 2 3 # | 3 1 4 |
            | 1 2 4 | 5 # # | # # 8 |
            - - - - - - - - - - - - -
            | 3 # 5 | 6 4 1 | 9 2 # |
            | # # # | 2 3 # | 3 1 4 |
            | 1 2 4 | 5 # # | # # 8 |
            - - - - - - - - - - - - -
         */

        SudokuPainter.ColorMask mask = SudokuPainter.determineColors(state);
        for(int lineNumber = 0; lineNumber < 9; ++lineNumber)
        {
            if(lineNumber % 3 == 0)
                renderHorizontalLine();
            renderSudokuLine(state, lineNumber, mask);
        }
        renderHorizontalLine();
        System.out.println(additionalText);
    }
}
