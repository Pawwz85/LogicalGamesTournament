package pawz.P2PClient.ResultParsers;

import java.util.Arrays;

public class ReplicaChecksums {
    public final byte [] puzzleRepositoryChecksums;
    public final  byte [] ticketRepositoryChecksums;

    public ReplicaChecksums(byte[] puzzleRepositoryChecksums, byte[] ticketRepositoryChecksums) {
        this.puzzleRepositoryChecksums = puzzleRepositoryChecksums;
        this.ticketRepositoryChecksums = ticketRepositoryChecksums;
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(puzzleRepositoryChecksums) ^ Arrays.hashCode(ticketRepositoryChecksums);
    }

}
