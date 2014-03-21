package BackupManagment;

import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import enums.ProgramParameters;

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
    private String mBackupName;

    public BackupInstanceFramework(Namespace args) {
        if (args != null) {
            System.out.println(args);
             if (args.get(ProgramParameters.INPUT.get()) == null
                     && args.get(ProgramParameters.OUTPUT.get()) != null
                    || args.get(ProgramParameters.INPUT.get()) != null
                     && args.get(ProgramParameters.OUTPUT.get()) == null
                     ) {
                    System.out.println("You have to specify both " +
                            ProgramParameters.INPUT_METAVAR.get() + " and "
                            + ProgramParameters.OUTPUT_METAVAR + "!");
                    System.exit(-1);
            }
            else  if (args.get(ProgramParameters.INPUT.get()) != null
                     && args.get(ProgramParameters.OUTPUT.get()) != null
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
            mShallow = args.getBoolean(ProgramParameters.SHALLOW.get());
            mList = args.getBoolean("list_backups");
            mBackupName = args.getString(ProgramParameters.NAME.get());

        } else {
            System.exit(0);
        }
    }

    private void parseLocations(Namespace args) {
        try {
            mDirInput = Paths.get(args.getString(ProgramParameters.INPUT.get())).toRealPath();
        } catch (IOException exp) {
            System.out.println("The specified "
                    + ProgramParameters.INPUT_METAVAR.get() + " path: "
                    + args.getString(ProgramParameters.INPUT.get()) + " doesn't exist");
            System.exit(-1);
        }
        mDirOutput = Paths.get(args.getString(ProgramParameters.OUTPUT.get()));
        if (mDirOutput.toAbsolutePath().equals(mDirInput.toAbsolutePath())) {
            System.out.println("The " + ProgramParameters.INPUT_METAVAR.get()
                    + " and " + ProgramParameters.OUTPUT_METAVAR.get() + " paths are equal. "
                    + "You have to put your backup into a directory that is different from the "
                    + ProgramParameters.OUTPUT_METAVAR.get() + " directory!");
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
        return mBackupName;
    }
}
