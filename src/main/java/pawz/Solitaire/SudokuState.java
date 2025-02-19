package pawz.Solitaire;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SudokuState implements ByteEncodable {


    // 0 stands for "empty" cells
    private final int[] fields;


    public SudokuState(){
        this.fields = new int[81];
    }

    public SudokuState(@NotNull int[] fields){
        assert fields.length == 81;
        this.fields = fields;
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

}
