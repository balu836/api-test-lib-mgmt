//import com.google.common.base.Splitter;
//import com.google.common.collect.Iterables;
//import net.andreinc.mockneat.abstraction.MockUnit;
//import net.andreinc.mockneat.abstraction.MockUnitLocalDate;
//import net.andreinc.mockneat.abstraction.MockUnitString;
//import net.andreinc.mockneat.types.enums.StringType;
//import uk.gov.dwp.utils.ConvertUtils;
//
//import java.io.IOException;
//
//import static net.andreinc.mockneat.unit.text.Strings.strings;
//import static net.andreinc.mockneat.unit.types.Ints.ints;
//import static org.apache.commons.lang3.RandomStringUtils.random;
//import static uk.gov.dwp.data.DateTimeConstants.DATE_FORMAT;
//import static uk.gov.dwp.utils.dates.DatesUtil.futureDateAvoidingWeekendsAndBankHolidays;
//import static uk.gov.dwp.utils.dates.DatesUtil.now;
//
//public class CustomGenerator {
//
//    public static <T> MockUnit<T> randomValueFromList(T[] list) {
//        return () -> () -> (T) list[ints().range(0, list.length).get()];
//    }
//
//    public static MockUnit<Boolean> alwaysTrue() {
//        return () -> () -> true;
//    }
//
//    public static MockUnit<Boolean> alwaysFalse() {
//        return () -> () -> false;
//    }
//
//    public static MockUnitString formattedString() {
//        return getRequiredString(100);
//    }
//
//    public static MockUnitLocalDate getCustomBusinessDate(int daysToAdd) throws IOException {
//        String date = futureDateAvoidingWeekendsAndBankHolidays(now(DATE_FORMAT), daysToAdd, DATE_FORMAT);
//        return () -> () -> ConvertUtils.stringToLocalDate(date);
//    }
//
//    public static MockUnitString formattedString(int count) {
//        return getRequiredString(count);
//    }
//
//    private static MockUnitString getRequiredString(int size) {
//        String text = strings().size(size).type(StringType.ALPHA_NUMERIC).get();
//        StringBuilder formattedText = new StringBuilder();
//        int wordSize = 0;
//        String[] splitText = Iterables.toArray(Splitter.fixedLength(25).split(text), String.class);
//        for (String s : splitText) {
//            formattedText.insert(0, s + " ");
//            if (wordSize < 2) {
//                wordSize++;
//            } else {
//                wordSize = 1;
//            }
//        }
//        return () -> () -> formattedText.substring(0, size);
//    }
//
//    public static String getRandomNino() {
//        return "TR" + random(6, false, true) + random(1, "ABCD");
//    }
//}
