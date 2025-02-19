package pawz.Tournament.DTO;

import pawz.Tournament.*;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PuzzleSolutionTicketDTO<Move extends ByteEncodable, State extends ByteEncodable> implements ByteEncodable{
    public int playerID;
    public int ticketID;
    public State initialState;

    public long epochTimestamp;

    public byte[] declaredSolutionHash;

    public List<Move> solution;

    public byte phase;

    public Optional<PuzzleSolutionTicket<Move, State>> toTicket(GameDefinition<Move, State> gameDefinition) {
        PuzzleSolutionTicket<Move, State> result = new PuzzleSolutionTicket<>(playerID, ticketID, initialState, gameDefinition);

        try {
            if (phase > 0)
                result.declareSolution(declaredSolutionHash, epochTimestamp);

            if (phase > 1)
                result.commitSolution(solution);

            if( phase > 2)
                result.verifySolution();
        } catch (WrongStateException e) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    @Override
    public byte[] toBytes() {
        /*
            The layout of the solution byte buffer:

            ###
            Header

            playerID - 4 Bytes, written in BigEndian
            tickedID - 4 Bytes, written in BigEndian
            epochTimestamp - 8 Bytes, written in BigEndian
            phase - 1 Byte
            hashSize - 4 Bytes, written in BigEndian
            declaredSolutionHash - hashSize Bytes, written in BigEndian
            solutionSize - 4 Bytes, written in BigEndian


            ###
            Body: solutionSize blocks of

            moveSize - 4 Bytes, written in BigEndian
            move     - moveSize bytes

        */

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            outputStream.writeInt(playerID);
            outputStream.writeInt(ticketID);
            outputStream.writeLong(epochTimestamp);
            outputStream.writeByte(phase);
            outputStream.writeInt(declaredSolutionHash.length);
            outputStream.write(declaredSolutionHash);

            byte[] initialStateBytes = initialState.toBytes();
            outputStream.writeInt(initialStateBytes.length);
            outputStream.write(initialStateBytes);

            outputStream.writeInt(solution.size());

            for (Move m: solution) {
                byte[] moveBytes = m.toBytes();
                outputStream.writeInt(moveBytes.length);
                byteArrayOutputStream.write(moveBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    void loadFromBytes(byte[] b, ByteDecoder<Move> moveDecoder, ByteDecoder<State> stateByteDecoder) throws IOException{

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
        DataInputStream inputStream = new DataInputStream(byteArrayInputStream);

        playerID = inputStream.readInt();
        ticketID = inputStream.readInt();
        epochTimestamp = inputStream.readLong();
        phase = inputStream.readByte();
        int hashSize = inputStream.readInt();
        declaredSolutionHash = inputStream.readNBytes(hashSize);

        int initialStateByteSize = inputStream.readInt();
        initialState = stateByteDecoder.fromBytes(inputStream.readNBytes(initialStateByteSize));

        int solutionSize = inputStream.readInt();
        solution = new ArrayList<>(solutionSize);
        for(int i = 0; i < solutionSize; ++i){
            int moveSize = inputStream.readInt();
            byte[] moveBytes = byteArrayInputStream.readNBytes(moveSize);
            solution.add(moveDecoder.fromBytes(moveBytes));
        }

    }
}
