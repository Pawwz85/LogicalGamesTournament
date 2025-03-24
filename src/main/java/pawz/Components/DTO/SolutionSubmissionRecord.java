package pawz.Components.DTO;

import org.jetbrains.annotations.Nullable;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.PuzzleSolutionTicketPhase;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SolutionSubmissionRecord<Move extends ByteEncodable, State extends ByteEncodable> {

    private final PuzzleSolutionTicketDTO<Move, State> dto;

    public final int playerId;

    public final int ticketId;

    public final PuzzleSolutionTicketPhase phase;

    private final @Nullable Long solvingTimeMs;
    
    public final List<Move> solution;


    public SolutionSubmissionRecord(PuzzleSolutionTicketDTO<Move, State> dto, @Nullable Long solvingTimeMs) {
        this.dto = dto;
        this.playerId = dto.playerID;
        this.ticketId = dto.ticketID;
        this.phase = phaseFromByte(dto.phase);
        this.solvingTimeMs = solvingTimeMs;
        this.solution = dto.solution;
    }


    private static PuzzleSolutionTicketPhase phaseFromByte(byte phase){
        switch (phase){
            case 0 : return PuzzleSolutionTicketPhase.NotSolved;
            case 1 : return  PuzzleSolutionTicketPhase.SolutionDeclared;
            case 2 : return  PuzzleSolutionTicketPhase.SolutionSubmitted;
            case 3 : return  PuzzleSolutionTicketPhase.SolutionRejected;
            case 4 : return PuzzleSolutionTicketPhase.SolutionVerified;
            default : return  PuzzleSolutionTicketPhase.SolutionRejected;
        }
    }

    public Optional<Long> getSolvingTime(){
        return Optional.ofNullable(this.solvingTimeMs);
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(dto.toBytes()) ^ ((solvingTimeMs != null)? solvingTimeMs.hashCode() : 0);
    }

}
