package pawz.demo;

import pawz.demo.CLI.StringTableFormatter;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketService;
import pawz.Tournament.Interfaces.IServiceSession;
import pawz.Tournament.TimeFormatter;

public class TicketDashboard {
    private final IPuzzleSolutionTicketService<SudokuMove, SudokuState> puzzleSolutionTicketService;
    private final StringTableFormatter tableFormatter;
    private final long tournamentStartedTimestamp;

    public TicketDashboard(IPuzzleSolutionTicketService<SudokuMove, SudokuState> puzzleSolutionTicketService, long tournamentStartedTimestamp) {
        this.puzzleSolutionTicketService = puzzleSolutionTicketService;
        this.tournamentStartedTimestamp = tournamentStartedTimestamp;

        int[] tableWidths = {10, 10, 25, 25};

        tableFormatter = new StringTableFormatter(tableWidths);
    }


    private String ticketPhaseToString(byte ticketPhase){
         switch (ticketPhase){
            case 0 : return "Not Solved";
            case 1 : return "Solution Declared";
            case 2 : return "Solution Committed";
            case 3 : return "Solution Rejected";
            case 4 : return "Solution Verified";
            default : return "Error";
        }
    }

    private void displayLabels(){
        StringTableFormatter.RowBuilder builder = tableFormatter.getRowBuilder();
        builder.putString("Ticket ID").putString("Owner ID").putString("Phase").putString("Time Since Start").display();
    }

    private void displayTicket(PuzzleSolutionTicketDTO<SudokuMove, SudokuState> dto){
          int ticketId = dto.ticketID;
          int ownerID = dto.playerID;
          String phase = ticketPhaseToString(dto.phase);
          String solvingTime = (dto.phase == 0)? "----":TimeFormatter.formatTime(dto.epochTimestamp - tournamentStartedTimestamp);

        StringTableFormatter.RowBuilder builder = tableFormatter.getRowBuilder();
          builder.putInt(ticketId).putInt(ownerID).putString(phase).putString(solvingTime).display();
    }

    public void displayTickets(IServiceSession session){
        puzzleSolutionTicketService.getAllOwnedTickets(session);
        displayLabels();

        for(var ticket: puzzleSolutionTicketService.getAllTicketsRecords()){
            if(ticket.playerID == session.getSessionId())
                displayTicket(ticket);
        }

    }

}
