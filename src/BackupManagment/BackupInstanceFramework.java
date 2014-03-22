package BackupManagment;

import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static enums.ProgramParameters.*;

/**
 * This class holds everything the
 * <code>{@link BackupManagment.BackupInstance BackupInstance}</code> constructor
 * needs to create a new instance for the
 * <code>{@link BackupManagment.BackupManager BackupManager}</code>.
 *
 * <br/>
 * Created by Martin Sicho on 18.3.14.
 */
public class BackupInstanceFramework {
    private Path mDirInput;
    private Path mDirOutput;
    private boolean mCreateNewBackup;
    private boolean mList;
    private boolean mShallow;
    private String mName;

    public BackupInstanceFramework(Namespace args) {
        if (args != null) {
            System.out.println(args);
             if (args.get(INPUT.get()) == null
                     && args.get(OUTPUT.get()) != null
                    || args.get(INPUT.get()) != null
                     && args.get(OUTPUT.get()) == null
                     ) {
                    System.out.println("You have to specify both "
                            + INPUT_METAVAR.get() + " and "
                            + OUTPUT_METAVAR + "!");
                    System.exit(-1);
            }
            else  if (args.get(INPUT.get()) != null
                     && args.get(OUTPUT.get()) != null
                     ) {
                parseLocations(args);
                mCreateNewBackup = true;
            }
            else {
                System.out.println("Unknown combination of arguments:");
                System.out.println(args.getAttrs().keySet());
                System.exit(-1);
            }

            //other arguments/options
            mShallow = args.getBoolean(SHALLOW.get());
            mList = args.getBoolean("list_backups");
            mName = args.getString(NAME.get());

        } else {
            System.exit(0);
        }
    }

    private void parseLocations(Namespace args) {
        try {
            mDirInput = Paths.get(args.getString(INPUT.get())).toRealPath();
        } catch (IOException exp) {
            System.out.println("The specified "
                    + INPUT_METAVAR.get() + " path: "
                    + args.getString(INPUT.get()) + " doesn't exist");
            System.exit(-1);
        }
        mDirOutput = Paths.get(args.getString(OUTPUT.get())).toAbsolutePath();
        if (mDirOutput.equals(mDirInput)) {
            System.out.println("The " + INPUT_METAVAR.get()
                    + " and " + OUTPUT_METAVAR.get() + " paths are equal. "
                    + "You have to put your backup into a directory that is different from the "
                    + OUTPUT_METAVAR.get() + " directory!");
            System.exit(-1);
        }
    }

    // getters

    public Path getDirInput() {
        return mDirInput;
    }

    public Path getDirOutput() {
        return mDirOutput;
    }

    public boolean wantsCreateNewBackup() {
        return mCreateNewBackup;
    }

    public boolean wantsList() {
        return mList;
    }

    public boolean wantsShallow() {
        return mShallow;
    }

    public String getBackupName() {
        return mName;
    }
}
