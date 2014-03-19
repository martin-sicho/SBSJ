package BackupManagment;

import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.*;

/**
 * This class holds everything the BackupManager class
 * needs to perform action.
 *
 * <br/>
 * Created by Martin Sicho on 18.3.14.
 */
public class BackupJob {
    private Path mDirInput;
    private Path mDirOutput;
    private boolean mCreateNewBackup;
    private boolean mList;
    private boolean mShallow;
    private String mBackupName;

    public BackupJob(Namespace args) {
        if (args != null) {
            System.out.println(args);
            // mozna bych mohl predelat pomoci  Arrays.asList(...).contains(...)
             if (args.get("input") == null && args.get("output") != null
                    || args.get("input") != null && args.get("output") == null
                     ) {
                    System.out.println("You have to specify both SOURCE and DESTINATION!");
                    System.exit(-1);
            }
            else  if (args.get("input") != null && args.get("output") != null) {
                //System.out.println("vytvorim novou zalohu a pak vypisu");
                parseLocations(args);
                mCreateNewBackup = true;
            }
            else {
                System.out.println("Unknown combination of arguments:");
                System.out.println(args.getAttrs().keySet());
                System.exit(-1);
            }

            //other arguments
            mShallow = args.getBoolean("shallow");
            mList = args.getBoolean("list_backups");
            mBackupName = args.getString("name");

        } else {
            System.exit(0);
        }
    }

    private void parseLocations(Namespace args) {
        try {
            mDirInput = Paths.get(args.getString("input")).toRealPath();
        } catch (IOException exp) {
            System.out.println("The specified SOURCE path: " + args.getString("input") + " doesn't exist");
            System.exit(-1);
        }
        mDirOutput = Paths.get(args.getString("output"));
        if (mDirOutput.toAbsolutePath().equals(mDirInput.toAbsolutePath())) {
            System.out.println("The SOURCE and DESTINATION paths are equal. " +
                    "You have to put your backup into a different directory!");
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
