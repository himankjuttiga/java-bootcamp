public class MethodsDemo {
    // Takes an int parameter; returns an int
    public static int square(int n) {
        return n * n;
    }

    // Overload: same method name, different parameter type
    public static double square(double n) {
        return n * n;
    }

    public static void main(String[] args) {
        int intResult = square(4);          // calls the int version
        double doubleResult = square(2.5);  // calls the double version — compiler picks by argument type

        System.out.println("square(4) = " + intResult);
        System.out.println("square(2.5) = " + doubleResult);
    }
}