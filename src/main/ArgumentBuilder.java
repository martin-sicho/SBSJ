package main;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import enums.ProgramParameters;

/**
 * This class is where all the argument customization and argument parsing is happening.
 * Parsed arguments can be obtained as a Namescpace object
 * via the <code>{@link #build(String[])}  build}</code> method.
 *
 * <br/>
 * Created by Martin Sicho on 18.3.14.
 */
class ArgumentBuilder {
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
        mParser = ArgumentParsers.newArgumentParser(ProgramParameters.PROGRAM_NAME.get())
                .description(mDescription);
        mParser.addArgument(ProgramParameters.INPUT.get())
                .metavar(ProgramParameters.INPUT_METAVAR.get())
                .nargs("?")
                .type(String.class)
                .help("The file/directory you want to get a backup of.");
        mParser.addArgument(ProgramParameters.OUTPUT.get())
                .metavar(ProgramParameters.OUTPUT_METAVAR.get())
                .nargs("?")
                .type(String.class)
                .help("The file/directory where you want the backup copy to be created.");
        mParser.addArgument("-n", "--" + ProgramParameters.NAME.get())
                .metavar(ProgramParameters.NAME_METAVAR.get())
                .nargs("?")
                .setDefault("")
                .help("use this optional argument to specify a name for your backup " +
                        "- otherwise a backup with generic name will be created");

        // options
        mParser.addArgument("-sh", "--" + ProgramParameters.SHALLOW.get())
                .action(Arguments.storeTrue())
                .nargs("?")
                .setDefault(false)
                .help("use this option to only make a shallow copy (subdirectories will not be backed)");
        mParser.addArgument("-ls", "--" + ProgramParameters.LIST_BACKUPS.get())
                .action(Arguments.storeTrue())
                .nargs("?")
                .setDefault(false)
                .help("use this option to list all created backups");
    }
}
