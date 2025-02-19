package pawz.Solitaire.CLI;

import java.util.ArrayList;
import java.util.List;

public class StringTableFormatter {

    private final int[] columnWidths;
    public int spacing = 2;

    public int leftPadding = 12;

    public StringTableFormatter(int[] columnWidths){
        this.columnWidths = columnWidths;
    }


    public class RowBuilder {
        private final int limit;
        private int index = 0;

        private final List<String> chunks = new ArrayList<>();

        int getTargetWidth(){
            return columnWidths[Math.min(index, limit)];
        }

        public RowBuilder() {
            this.limit = columnWidths.length - 1;
        }

        public RowBuilder putString(String s){
            chunks.add(s);
            ++index;
            return this;
        }

        public RowBuilder putInt(int i){
            chunks.add(String.valueOf(i));
            ++index;
            return this;
        }

        public RowBuilder putFloat(float f){
            chunks.add(String.valueOf(f));
            ++index;
            return this;
        }

        public RowBuilder putDouble(double d){
            chunks.add(String.valueOf(d));
            ++index;
            return this;
        }

        public RowBuilder putObject(Object o){
            chunks.add(o.toString());
            ++index;
            return this;

        }
        public void display(){
            renderRow(chunks);
        }

    }


    private void renderRow(List<String> chunks){

        System.out.print(" ".repeat(leftPadding));

        for(int i = 0; i< columnWidths.length && i < chunks.size(); ++i){
            int targetWidth = columnWidths[i];

            System.out.print(" ".repeat(Math.max(0, targetWidth-chunks.get(i).length())));
            System.out.print(chunks.get(i));

        }
        System.out.print('\n');
    }

    public RowBuilder getRowBuilder(){
        return new RowBuilder();
    }

}
