package ausfin.api;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {
    public static String camelCase(String input) {
        String joinedCapitalised = Arrays.stream(input.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase()).collect(Collectors.joining());

        return joinedCapitalised.substring(0, 1).toLowerCase() + joinedCapitalised.substring(1);
    }
}
