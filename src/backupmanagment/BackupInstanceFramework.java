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
    /**
     * {@link java.nio.file.Path} to the original directory.
     */
    private Path mDirOriginal;
    /**
     * {@link java.nio.file.Path} to the backup directory.
     */
    private Path mDirBackup;
    /**
     * Specifies whether a new backup supposed to be created or not.
     */
    private boolean mCreateNewBackup;
    /**
     * Specifies whether the user wants to see the listing of backups or not.
     */
    private boolean mList;
    /**
     * Specifies whether the user wants the newly created backup to be shallow or not.
     */
    private boolean mShallow;
    /**
     * Specifies whether the user wants backup synchronization or not.
     */
    private boolean mSync;
    /**
     * Specifies whether the user wants to keep all backed files
     * in the backup directory synchronization or not.
     */
    private boolean mKeepAll;
    /**
     * The backup name.
     */
    private String mName;
    /**
     * The name of the backup that is to be deleted.
     */
    private String mDeleteName;

    public BackupInstanceFramework() {
        // no action
    }

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
        mDeleteName = args.getString(DELETE.toString());
    }

    /**
     * This method allows to set the {@link backupmanagment.BackupInstanceFramework}
     * properties manually. It is used by the GUI.
     *
     * @param original original path
     * @param backup backup directory
     * @param name backup name
     * @param delete_name name of the backup that should be deleted
     * @param keep_all the value of {@link backupmanagment.BackupInstanceFramework#mKeepAll}
     * @param shallow the value of {@link backupmanagment.BackupInstanceFramework#mShallow}
     * @param create the value of {@link backupmanagment.BackupInstanceFramework#mCreateNewBackup}
     * @param list the value of {@link backupmanagment.BackupInstanceFramework#mList}
     * @param sync the value of {@link backupmanagment.BackupInstanceFramework#mSync}
     */
    public void setOptions(
            Path original
            , Path backup
            , String name
            , String delete_name
            , boolean keep_all
            , boolean shallow
            , boolean create
            , boolean list
            , boolean sync
    ) {
        mDirOriginal = original;
        mDirBackup = backup;
        mName = name;
        mDeleteName = delete_name;
        mKeepAll = keep_all;
        mShallow = shallow;
        mCreateNewBackup = create;
        mList = list;
        mSync = sync;
    }

    // internal private methods

    /**
     * Checks whether the user input via command line is valid.
     * It also notifies the user if some problems occur.
     *
     * @param args the arguments passed from the command line in the
     *             form of {@link net.sourceforge.argparse4j.inf.Namespace} instance.
     */
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

    public String getDeleteName() {
        return mDeleteName;
    }

    // setters

    public void setName(String mName) {
        this.mName = mName;
    }
}
