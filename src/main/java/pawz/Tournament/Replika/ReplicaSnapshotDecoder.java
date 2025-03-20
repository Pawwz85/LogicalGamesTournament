package pawz.Tournament.Replika;

import pawz.DerivedImplementations.DeriveByteUtils;
import pawz.Puzzle;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTOByteDecoder;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.ByteEncoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collection;

public class ReplicaSnapshotDecoder<Move extends ByteEncodable, State extends ByteEncodable> implements ByteDecoder<ReplicaSnapshot<Move, State>> {

    private final ByteDecoder<PuzzleSolutionTicketDTO<Move, State>> ticketDTOByteDecoder;
    private final ByteDecoder<Puzzle<Move, State>> puzzleByteDecoder;

    public ReplicaSnapshotDecoder(ByteDecoder<PuzzleSolutionTicketDTO<Move, State>> ticketDTOByteDecoder, ByteDecoder<Puzzle<Move, State>> puzzleByteDecoder) {
        this.ticketDTOByteDecoder = ticketDTOByteDecoder;
        this.puzzleByteDecoder = puzzleByteDecoder;
    }

    @Override
    public ReplicaSnapshot<Move, State> fromBytes(byte[] bytes) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(byteArrayInputStream);

        DeriveByteUtils<PuzzleSolutionTicketDTO<Move, State>> ticketDTODeriveByteUtils = new DeriveByteUtils<>();
        DeriveByteUtils<Puzzle<Move, State>> puzzleDeriveByteUtils = new DeriveByteUtils<>();

        int ticketsLength = stream.readInt();
        byte[] ticketsBytes = byteArrayInputStream.readNBytes(ticketsLength);
        int puzzlesLength = stream.readInt();
        byte[] puzzlesBytes = byteArrayInputStream.readNBytes(puzzlesLength);

        Collection<PuzzleSolutionTicketDTO<Move, State>> tickets = ticketDTODeriveByteUtils.collectionByteDecoder(ticketDTOByteDecoder).fromBytes(ticketsBytes);
        Collection<Puzzle<Move, State>> puzzles = puzzleDeriveByteUtils.collectionByteDecoder(puzzleByteDecoder).fromBytes(puzzlesBytes);

        return new ReplicaSnapshot<>(tickets, puzzles);
    }
}
