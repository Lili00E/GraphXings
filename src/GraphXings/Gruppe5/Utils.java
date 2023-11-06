package GraphXings.Gruppe5;

public class Utils {
    public static void announceTimedFunction(String name, long start, long end) {
        System.out.println(name + " took " + (end - start) + " Millisek.");
    }
}
