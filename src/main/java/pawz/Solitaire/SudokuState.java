package pawz.Solitaire;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SudokuState implements ByteEncodable {


    // 0 stands for "empty" cells
    private final int[] fields;

    public boolean[] getProtectedFields() {
        return protectedFields.clone();
    }

    private final boolean[] protectedFields;


    public SudokuState(){
        this.fields = new int[81];
        this.protectedFields = new boolean[81];
    }

    public SudokuState(@NotNull int[] fields){
        assert fields.length == 81;
        this.fields = fields;
        this.protectedFields = new boolean[81];

        for(int i = 0; i<81; ++i)
            protectedFields[i] = fields[i] != 0;

    }

    public SudokuState(@NotNull int[] fields, @NotNull boolean[] protectedFields){
        assert fields.length == 81 && protectedFields.length == 81;
        this.fields = fields;
        this.protectedFields = protectedFields;
    }


    @Override
    public byte[] toBytes() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        try {
            for (int field : fields) stream.writeInt(field);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public int[] getFields() {
        return fields.clone();
    }
    public boolean isProtected(int sq){
        return sq >= 0 && sq < 81 && protectedFields[sq];
    };
}
