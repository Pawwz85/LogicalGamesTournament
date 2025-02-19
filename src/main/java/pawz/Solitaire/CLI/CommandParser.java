package pawz.Solitaire.CLI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommandParser {

    private List<String> tokenize(String string){
        List<String> strings = new ArrayList<>();
        StringBuilder currentString = new StringBuilder();

        boolean inQuotes = false;

        for (int i = 0; i < string.length(); ++i){
            char c = string.charAt(i);
            switch (c){
                case '"':
                    inQuotes = !inQuotes;
                    break;
                case ' ', '\t', '\n':
                    if (!inQuotes) {
                        strings.add(currentString.toString());
                        currentString.delete(0, currentString.length());
                    } else
                        currentString.append(c);
                    break;
                default:
                    currentString.append(c);
            }
        }

        if (!currentString.isEmpty())
            strings.add(currentString.toString());

        return strings;
    }

    private ImmutablePair<List<String>, Map<String, String>> parseArgs(List<String> tokens){
        List<String> args = new ArrayList<>();
        Map<String, String> kwargs = new HashMap<>();

        for(int i = 0; i < tokens.size(); ++i){
            String s = tokens.get(i);

            if(s.startsWith("--")){
                if(i + 1 < tokens.size()){
                    kwargs.put(s.substring(2), tokens.get(i+1));
                    ++i;
                }
            } else if (s.startsWith("-")) {
                kwargs.put(s.substring(1), "1");
            } else {
                args.add(s);
            }
        }

        return new ImmutablePair<>(args, kwargs);
    }

    private ImmutablePair<String[], Map<String, String>> condense(ImmutablePair<List<String>, Map<String, String>> looseResult){
        List<String> list = looseResult.first();
        String[] result = list.toArray(new String[0]);
        return new ImmutablePair<>(result, looseResult.second());
    }

    public ImmutablePair<String[], Map<String, String>> parseLine(String command){
        List<String> tokens = tokenize(command);
        ImmutablePair<List<String>, Map<String, String>> intermediateResult = parseArgs(tokens);
        return condense(intermediateResult);
    }

}
