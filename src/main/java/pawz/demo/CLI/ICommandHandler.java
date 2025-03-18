package pawz.demo.CLI;

import java.util.Map;

public interface ICommandHandler {
    String getLongHelpString();
    String getShortHelpString();
    String getDescription();
    boolean match(String[] args, Map<String, String> kwargs);

    void execute(String[] args, Map<String, String> kwargs) throws NotEnoughArgumentsException;
}
