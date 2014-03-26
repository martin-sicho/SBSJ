package backupmanagment;

import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static enums.ProgramParameters.*;

/**
 * This class holds everything the
 * <code>{@link backupmanagment.BackupInstance BackupInstance}</code> constructor
 * needs to create a new instance for the
 * <code>{@link backupmanagment.BackupManager BackupManager}</code>.
 *
 * <br/>
 * Created by Martin Sicho on 18.3.14.
 */
public class BackupInstanceFramework {
    private Path mDirOriginal;
    private Path mDirBackup;
    private boolean mCreateNewBackup;
    private boolean mList;
    private boolean mShallow;
    private boolean mSync;
    private boolean mKeepAll;
    private String mName;

    /**
     * The {@link backupmanagment.BackupInstanceFramework} constructor.
     * It takes an instance of the {@link net.sourceforge.argparse4j.inf.Namespace}
     * class implemented by the {@link net.sourceforge.argparse4j argparse4j} library
     * and uses it to create the {@link backupmanagment.BackupInstanceFramework} for the creation
     * of a {@link backupmanagment.BackupInstance} object.
     *
     * @param args an instance of the {@link net.sourceforge.argparse4j.inf.Namespace} class
     *             that represents the command line arguments passed by the user
     */
    public BackupInstanceFramework(Namespace args) {
        //System.out.println(args);
        if (args == null) System.exit(0);
        if (args.get(ORIGINAL.toString()) == null
                && args.get(BACKUP.toString()) != null
                || args.get(ORIGINAL.toString()) != null
                && args.get(BACKUP.toString()) == null
                ) {
            System.out.println("You have to specify both "
                    + ORIGINAL_METAVAR + " and "
                    + BACKUP_METAVAR + "!");
            System.exit(-1);
        }
        else  if (args.get(ORIGINAL.toString()) != null
                && args.get(BACKUP.toString()) != null
                ) {
            parseLocations(args);
            mCreateNewBackup = true;
        }

        //other arguments/options
        mShallow = args.getBoolean(SHALLOW.toString());
        mList = args.getBoolean("list_backups");
        mName = args.getString(NAME.toString());
        mSync = args.getBoolean(SYNCHRONIZE.toString());
        mKeepAll = args.getBoolean(KEEP_ALL.toString());
    }

    // internal private methods

    private void parseLocations(Namespace args) {
        // load the original path and test if the original path is valid and exists
        try {
            mDirOriginal = Paths.get(args.getString(ORIGINAL.toString())).toRealPath().normalize();
        } catch (InvalidPathException exp) {
            System.out.println("The specified "
                    + ORIGINAL_METAVAR + " path: "
                    + args.getString(ORIGINAL.toString()) + " is invalid.");
            System.exit(-1);
        } catch (IOException exp) {
            System.out.println("The specified "
                    + ORIGINAL_METAVAR + " path: "
                    + args.getString(ORIGINAL.toString()) + " doesn't exist.");
            System.exit(-1);
        }

        // load the backup path and test if it is valid
        try {
            mDirBackup = Paths.get(args.getString(BACKUP.toString())).toAbsolutePath().normalize();
        } catch (InvalidPathException exp) {
            System.out.println("The specified "
                    + BACKUP_METAVAR + " path: "
                    + args.getString(BACKUP.toString()) + " is invalid.");
            System.exit(-1);
        }

        // test paths for equality
        if (mDirBackup.equals(mDirOriginal)) {
            System.out.println("The " + ORIGINAL_METAVAR
                    + " and " + BACKUP_METAVAR + " paths are equal. "
                    + "You have to put your backup into a directory that is different from the "
                    + BACKUP_METAVAR + " directory!");
            System.exit(-1);
        }

        // test for possible infinite loop
        if (mDirBackup.getRoot().equals(mDirOriginal.getRoot())) {
            Path pp = mDirBackup.relativize(mDirOriginal);
            boolean backup_is_subpath = true;
            for (int i = 0; i < pp.getNameCount(); i++) {
                if (!pp.getName(i).toString().equals("..")) {
                    backup_is_subpath = false;
                    break;
                }
            }
            if (backup_is_subpath) {
                System.out.println(BACKUP_METAVAR + " can't be a subpath of " + ORIGINAL_METAVAR + "!!!");
                System.exit(-1);
            }
        }

        // warn if the backup is inefective (the backup location contains the original file)
        if (mDirOriginal.getParent().equals(mDirBackup) && !Files.isDirectory(mDirOriginal)) {
            System.out.println("WARNING: Backup inefective - the backup location contains the original file!");
        }
    }

    // getters

    public Path getDirOriginal() {
        return mDirOriginal;
    }

    public Path getDirBackup() {
        return mDirBackup;
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

    public boolean wantsKeepAll() {
        return mKeepAll;
    }

    public boolean wantsSynchronization() {
        return mSync;
    }

    public String getBackupName() {
        return mName;
    }

    // setters

    public void setName(String mName) {
        this.mName = mName;
    }
}
