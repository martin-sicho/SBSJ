package main;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import static enums.ProgramParameters.*;

/**
 * This class is where all the argument definition and customization is located.
 * The command line arguments are parsed here and return as a Namescpace object
 * via the <code>{@link #build(String[]) build()}</code> method.
 *
 * <br/>
 * Created by Martin Sicho on 18.3.14.
 */
class ArgumentBuilder {
    private static ArgumentParser mParser;
    private static String mDescription = "This is a very simple backup application. " +
            "It will look for changes in one directory and transfer them to another.";

    /**
     * Empty private constructor - prevents the class from being instantiated.
     */
    private ArgumentBuilder() {
        // no action
    }

    /**
     * Customizes and parses the command line arguments.
     *
     * @param args command line arguments
     *             as a <code>{@link java.lang.String String}</code> array
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
        mParser = ArgumentParsers.newArgumentParser(PROGRAM_NAME.toString())
                .description(mDescription);
        mParser.addArgument(ORIGINAL.toString())
                .metavar(ORIGINAL_METAVAR.toString())
                .nargs("?")
                .type(String.class)
                .help("The file/directory you want to to get a backup of.");
        mParser.addArgument(BACKUP.toString())
                .metavar(BACKUP_METAVAR.toString())
                .nargs("?")
                .type(String.class)
                .help("The directory where you want the backup to be created. " +
                        "If it doesn't exist, it is created.\n" +
                        "Note that this is always a directory to which all files " +
                        "from the " + ORIGINAL_METAVAR + " are copied. " +
                        "It is never created as a single file. " +
                        "Even if your " + ORIGINAL_METAVAR + " is a single file.");
        mParser.addArgument("-n", "--" + NAME)
                .metavar(NAME_METAVAR.toString())
                .nargs("?")
                .setDefault("")
                .help("use this optional argument to specify a name for your backup " +
                        "- otherwise the backup will be named like the " + ORIGINAL_METAVAR);
        mParser.addArgument("-del", "--" + DELETE)
                .metavar(NAME_METAVAR.toString())
                .nargs("?")
                .setDefault("")
                .help("use this optional argument to stop tracking directory for changes - " +
                        "this will entirely remove the backup from the utility!");

        // options
        mParser.addArgument("-ka", "--" + KEEP_ALL)
                .action(Arguments.storeTrue())
                .nargs("?")
                .setDefault(false)
                .help("this option can be specified to tell the utility to keep all files and directories " +
                        "that were ever created in the " + ORIGINAL_METAVAR + " backed up forever in the " + BACKUP_METAVAR + " .");
        mParser.addArgument("-s", "--" + SYNCHRONIZE)
                .action(Arguments.storeTrue())
                .nargs("?")
                .setDefault(false)
                .help("use this option to synchronize all scheduled backups. If you add --" + NAME + " " + NAME_METAVAR +
                        ", only the specified backup will be synchronized.");
        mParser.addArgument("-sh", "--" + SHALLOW)
                .action(Arguments.storeTrue())
                .nargs("?")
                .setDefault(false)
                .help("use this option to only make a shallow copy (subdirectories will not be backed)");
        mParser.addArgument("-ls", "--" + LIST_BACKUPS)
                .action(Arguments.storeTrue())
                .nargs("?")
                .setDefault(false)
                .help("use this option to list all scheduled backups. You can use the --" + NAME + " argument " +
                        "to select only a single backup.");
    }
}
