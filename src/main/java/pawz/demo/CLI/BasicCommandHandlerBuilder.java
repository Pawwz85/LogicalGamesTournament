package pawz.demo.CLI;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class BasicCommandHandlerBuilder {

    private String longHelp = "";
    private String shortHelp = "";
    private String description = "";

    private Function<ImmutablePair<String[], Map<String, String>>, Boolean> matcher = null;

    private Function<ImmutablePair<String[], Map<String, String>>, Object> runner = null;

    private final static Function<ImmutablePair<String[], Map<String, String>>, Boolean> defaultMatcher = (mapImmutablePair -> Boolean.FALSE);
    private final static Function<ImmutablePair<String[], Map<String, String>>, Object> defaultRunner = (mapImmutablePair -> 0);


    public BasicCommandHandlerBuilder withShortHelp(String shortHelp){
        this.shortHelp = shortHelp;
        return this;
    }

    public BasicCommandHandlerBuilder withLongHelp(String longHelp){
        this.longHelp = longHelp;
        return this;
    }

    public BasicCommandHandlerBuilder withDescription(String description){
        this.description = description;
        return this;
    }

    public BasicCommandHandlerBuilder withMatcher(Function<ImmutablePair<String[], Map<String, String>>, Boolean> matcher){
        this.matcher = matcher;
        return this;
    }

    public BasicCommandHandlerBuilder withRunner(Function<ImmutablePair<String[], Map<String, String>>, Object> runner){
        this.runner = runner;
        return this;
    }


    public BasicCommandHandlerBuilder withKeywordMatcher(String keyword){
        return withMatcher(pair -> pair.first().length > 0 && Objects.equals(pair.first()[0], keyword));
    }

    public BasicCommandHandler build(){
        return new BasicCommandHandler(
                longHelp,
                shortHelp,
                description,
                matcher == null? defaultMatcher: matcher,
                runner == null? defaultRunner: runner);
    }

}
