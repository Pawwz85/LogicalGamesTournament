package pawz.Tournament.Replika;

import pawz.DerivedImplementations.DeriveByteUtils;
import pawz.DerivedImplementations.PlainByteEncoder;
import pawz.DerivedImplementations.PlainCollectionByteEncoder;
import pawz.Puzzle;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.ByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ReplicaSnapshot<Move extends ByteEncodable, State extends ByteEncodable> implements ByteEncodable {

    public final Collection<PuzzleSolutionTicketDTO<Move, State>> tickets;
    public final Collection<Puzzle<Move, State>> puzzles;

    private final ByteEncoder<Collection<PuzzleSolutionTicketDTO<Move, State>>> ticketsByteEncoder;
    private final ByteEncoder<Collection<Puzzle<Move, State>>> puzzlesByteEncoder;


    public ReplicaSnapshot(Collection<PuzzleSolutionTicketDTO<Move, State>> tickets, Collection<Puzzle<Move, State>> puzzles) {
        this.tickets = tickets;
        this.puzzles = puzzles;
        DeriveByteUtils<PuzzleSolutionTicketDTO<Move, State>> ticketDTODeriveByteUtils = new DeriveByteUtils<>();
        DeriveByteUtils<Puzzle<Move, State>> puzzleDeriveByteUtils = new DeriveByteUtils<>();

        ticketsByteEncoder = ticketDTODeriveByteUtils.collectionByteEncoder(new PlainByteEncoder<>());
        puzzlesByteEncoder = puzzleDeriveByteUtils.collectionByteEncoder(new PlainByteEncoder<>());
    }

    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);
        byte[] ticketsBytes = ticketsByteEncoder.toBytes(this.tickets);
        byte[] puzzlesBytes = puzzlesByteEncoder.toBytes(this.puzzles);

        try{
            stream.writeInt(ticketsBytes.length);
            byteArrayOutputStream.write(ticketsBytes);
            stream.writeInt(puzzlesBytes.length);
            byteArrayOutputStream.write(puzzlesBytes);
        } catch (IOException e){
            throw new RuntimeException();
        }

        return byteArrayOutputStream.toByteArray();
    }
}
