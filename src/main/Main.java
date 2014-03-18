package main;

/**
 * Created by Martin Sicho on 11.3.14.
 */
public class Main {

    public static void main(String[] args) {
        CommandLineObjects parameters = new CommandLineObjects(ArgumentBuilder.build(args));
    }
}
