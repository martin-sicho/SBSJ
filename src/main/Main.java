package main;

/**
 * This is the main class.
 *
 * <br/>
 * Created by Martin Sicho on 11.3.14.
 */
public class Main {

    public static void main(String[] args) {
        BackupJob parameters = new BackupJob(ArgumentBuilder.build(args));
    }
}
