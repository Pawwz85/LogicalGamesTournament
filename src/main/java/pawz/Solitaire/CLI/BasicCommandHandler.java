package pawz.Solitaire.CLI;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class BasicCommandHandler implements ICommandHandler{

    private final String longHelp;
    private final String shortHelp;

    private final String description;

    private final Function<ImmutablePair<String[], Map<String, String>>, Boolean>  matcher;

    private final Function<ImmutablePair<String[], Map<String, String>>, Object> runner;

    public BasicCommandHandler(String longHelp, String shortHelp, String description, Function<ImmutablePair<String[], Map<String, String>>, Boolean> matcher, Function<ImmutablePair<String[], Map<String, String>>, Object> runner) {
        this.longHelp = longHelp;
        this.shortHelp = shortHelp;
        this.description = description;
        this.matcher = matcher;
        this.runner = runner;
    }

    @Override
    public String getLongHelpString() {
        return longHelp;
    }

    @Override
    public String getShortHelpString() {
        return shortHelp;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean match(String[] args, Map<String, String> kwargs) {
        ImmutablePair<String[], Map<String, String>> pair = new ImmutablePair<>(args, kwargs);
        return matcher.apply(pair);
    }

    @Override
    public void execute(String[] args, Map<String, String> kwargs) throws NotEnoughArgumentsException {
        ImmutablePair<String[], Map<String, String>> pair = new ImmutablePair<>(args, kwargs);

        if(kwargs.containsKey("desc")){
            System.out.println(description);
            return;
        }

        if(kwargs.containsKey("man")){
            System.out.println(longHelp);
            return;
        }

        if(kwargs.containsKey("help")){
            System.out.println(shortHelp);
            return;
        }

        runner.apply(pair);
    }
}
