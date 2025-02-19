package pawz.Tournament;

public class TimeFormatter {


    private static String padNumber(String s, int targetLength){
        return "0".repeat(Math.max(0, targetLength - s.length())) +
                s;
    }

    public static String formatTime(long deltaTime){
        long millis = deltaTime%1000;
        deltaTime/= 1000;
        long seconds = deltaTime%60;
        deltaTime/=60;
        long minutes = deltaTime%60;
        long hours = deltaTime/60;

        boolean overMinute = false;
        StringBuilder stringBuilder = new StringBuilder();

        if(hours > 0){
            overMinute = true;
            stringBuilder.append(hours).append(":");
        }

        if(overMinute || minutes > 0){
            overMinute = true;
            stringBuilder.append(padNumber(String.valueOf(minutes), 2)).append(':');
        }

       if(overMinute)
           stringBuilder.append(padNumber(String.valueOf(seconds), 2));
       else
           stringBuilder.append(seconds);

       stringBuilder.append('.').append(millis/100);
        return stringBuilder.toString();
    }

}
