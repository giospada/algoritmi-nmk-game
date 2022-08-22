package test;

public class ValueOrReference {
    // create main function
    public static void iChangeValue(TestClass something) {
        something.setNum(something.num + 1);
    }

    public static void main(String[] args) {
        TestClass something = new TestClass(1);
        System.out.println("The number is " + something.num); // 1
        iChangeValue(something);
        System.out.println("The after change number is " + something.num); // 2
    }
}
