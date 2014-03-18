package main;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * This class is where all the argument customization and argument parsing is happening.
 * Parsed arguments can be obtained as a Namescpace object
 * via the <code>{@link #build(String[])}  build}</code> method.
 *
 * Created by Martin Sicho on 18.3.14.
 */
public class ArgumentBuilder {
    private static ArgumentParser mParser;
    private static String mDescription = "This is a very simple backup application.";

    /**
     * Private constructor - prevents the class from being instantiated.
     */
    private ArgumentBuilder() {
        // no action
    }

    /**
     * Customizes and parses the command line arguments.
     *
     * @param args command line arguments as <code>{@link java.lang.String String[]}</code>
     * @return parsed arguments as
     * a <code>{@link net.sourceforge.argparse4j.inf.Namespace Namespace}</code> object
     */
    public static Namespace build(String[] args) {
        buildArguments();

        Namespace res = null;
        try {
            res = mParser.parseArgs(args);
        } catch (ArgumentParserException e) {
            mParser.handleError(e);
        }
        return res;
    }

    private static void buildArguments() {
        mParser = ArgumentParsers.newArgumentParser("SBSJ")
                .description(mDescription);
        mParser.addArgument("dir_backed")
                .metavar("SOURCE")
                .type(String.class)
                .nargs(1)
                .help("The file/directory you want to get a backup of.");
        mParser.addArgument("dir_backup")
                .metavar("DESTINATION")
                .type(String.class)
                .nargs(1)
                .help("The file/directory where you want the backup copy to be created.");
    }
}
