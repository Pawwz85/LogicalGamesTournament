package pawz.Tournament.Replika;

import org.bouncycastle.crypto.digests.MD5Digest;
import org.jetbrains.annotations.NotNull;
import pawz.Puzzle;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Exceptions.RepositoryException;
import pawz.Tournament.Exceptions.SynchronisationError;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.Interfaces.IPuzzleService;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketService;
import pawz.Tournament.PuzzleSolutionTicket;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReplikaSynchronisationService<Move extends ByteEncodable, State extends ByteEncodable> {
    private final LocalPuzzleRepository<Move, State> puzzleRepository;
    private final LocalSolutionTicketRepository<Move, State> ticketRepository;

    private final GameDefinition<Move, State> gameDefinition;

    public ReplikaSynchronisationService(LocalPuzzleRepository<Move, State> puzzleRepository, LocalSolutionTicketRepository<Move, State> ticketRepository, GameDefinition<Move, State> gameDefinition) {
        this.puzzleRepository = puzzleRepository;
        this.ticketRepository = ticketRepository;
        this.gameDefinition = gameDefinition;
    }


    public void forcefullySetTicketRepository(Collection<PuzzleSolutionTicketDTO<Move, State>> ticketRecords) throws RepositoryException {
        ticketRepository.clear();
        for(var record: ticketRecords){
            ticketRepository.update(new PuzzleSolutionTicket<>(record, gameDefinition));
        }
    }

    public void forcefullySetPuzzleRepository(Collection<Puzzle<Move, State>> puzzles) throws RepositoryException {
        puzzleRepository.clear();

        for(var record: puzzles)
            puzzleRepository.update(record);
    }

    public void syncTicketRepository(IPuzzleSolutionTicketService<Move, State> trustedService) throws SynchronisationError {
        try {
            forcefullySetTicketRepository(trustedService.getAllTicketsRecords());
        } catch (RepositoryException e) {
            /*
                If we ever run into this scenario:
                1. The repository was out of sync to begin with
                2. The repository was cleaned out
                3. There was an error during repository update

                Which means that repository is in inconsistent state. Since, we would throw a synchronisation
                error and let the caller decide what to do with this mess.
             */
            throw new SynchronisationError();
        }
    }

    public void syncPuzzleRepository(IPuzzleService<Move, State> trustedService) throws SynchronisationError{
        try{
            forcefullySetPuzzleRepository(trustedService.getAllPuzzles());
        } catch (RepositoryException e){
            throw new SynchronisationError();
        }
    }

    public byte[] calculatePuzzleRepositoryChecksum(){
        MD5Digest digest = new MD5Digest();

        List<byte[]> checksums = puzzleRepository.getAll()
                .stream()
                .map( p -> hashBytes(p.toBytes()))
                .collect(Collectors.toList());

        return xorChecksums(checksums);
    }

    public byte[] calculateTicketRepositoryChecksum(){
        MD5Digest digest = new MD5Digest();

        List<byte[]> checksums = ticketRepository.getAllTickets()
                .stream()
                .map( ticket -> hashBytes(ticket.toDto().toBytes()))
                .collect(Collectors.toList());

        return xorChecksums(checksums);
    }

    private static byte[] hashBytes(byte[] input) {
        MD5Digest digest = new MD5Digest();
        digest.update(input, 0, input.length);
        byte[] hash = new byte[digest.getDigestSize()]; // 16 bytes
        digest.doFinal(hash, 0);
        return hash;
    }

    private static byte[] xorChecksums(List<byte[]> checksums) {
        if (checksums.isEmpty()) {
            return new byte[16]; // Return zeroed MD5-sized array if empty
        }

        byte[] result = new byte[16]; // MD5 produces 16-byte hashes
        for (byte[] checksum : checksums) {
            for (int i = 0; i < 16; ++i) {
                result[i] ^= checksum[i];
            }
        }
        return result;
    }

    public ReplicaSnapshot<Move, State> getSnapshot(){
        Collection<PuzzleSolutionTicketDTO<Move, State>> ticketRecords =
                ticketRepository.getAllTickets().stream().map(PuzzleSolutionTicket::toDto)
                        .collect(Collectors.toList());
        return new ReplicaSnapshot<>(
                ticketRecords,
                this.puzzleRepository.getAll()
        );
    }

}
