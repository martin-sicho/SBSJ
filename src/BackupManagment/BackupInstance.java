package backupmanagment;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * Holds every information about a scheduled backup.
 * The <code>{@link backupmanagment.BackupManager BackupManager}</code> class
 * manages, creates and serializes instances of this class.
 *
 * <br/>
 * Created by Martin Sicho on 19.3.14.
 */
class BackupInstance implements java.io.Serializable  {
    private String mName;
    private String mDirOriginal;
    private String mDirBackup;
    private boolean mShallow;
    private boolean mKeepAll;
    private Map<String,Long> mBackupIndex;

    private Date mLastSynchronization;

    /**
     * The class constructor. Takes one instance of
     * <code>{@link backupmanagment.BackupInstanceFramework BackupInstanceFramework}</code>
     * that serves as a template for the serializable
     * <code>{@link backupmanagment.BackupInstance BackupInstance}</code> object.
     *
     * @param framework instance of
     * <code>{@link backupmanagment.BackupInstanceFramework BackupInstanceFramework}</code>
     */
    BackupInstance(BackupInstanceFramework framework) {
        mName = framework.getBackupName();
        mDirOriginal = framework.getDirOriginal().toString();
        mDirBackup = framework.getDirBackup().toString();
        mShallow = framework.wantsShallow();
        mKeepAll = framework.wantsKeepAll();
        mBackupIndex = new HashMap<>();
        mLastSynchronization = new Date();
        synchronize();
    }

    /**
     * This method is used to synchronize the backup directory with the original.
     * It controls the flow according to rules set by
     * {@link backupmanagment.BackupInstanceFramework BackupInstanceFramework}
     * instance that was passed to the class <code>{@link #BackupInstance(BackupInstanceFramework) constructor}</code>.
     */
    void synchronize() {
        try {
            Files.createDirectories(getDirBackup());
        } catch (IOException exp) {
            System.err.format("An I/O exeption while creating the backup directory: %s%n%s%n", mDirBackup, exp.getLocalizedMessage());
            System.exit(1);
        }
        try {
            Files.walkFileTree(getDirOriginal(), new BackupFileVisitor(this));
            if (!mKeepAll) {
                removeDeleted();
            }
            System.out.println(mName + ": Synchronization OK.");
            mLastSynchronization = new Date();
        } catch (IOException exp) {
            System.err.println(mName + ": Synchronization FAILED.");
            System.err.println(exp.getMessage());
        }
    }

    /**
     * This method returns path to the original file or directory.
     *
     * @return {@link java.nio.file.Path Path} to the original.
     */
    Path getDirOriginal() {
        return Paths.get(mDirOriginal);
    }

    /**
     * This method returns the path to the directory
     * where the backed up data are to be created.
     *
     * @return {@link java.nio.file.Path Path} to the backup directory.
     */
    Path getDirBackup() {
        return Paths.get(mDirBackup);
    }

    boolean isShallow() {
        return mShallow;
    }

    /**
     * This method, together with {@link #getOriginalDestination(java.nio.file.Path)},
     * provides the program with the ability
     * to rebuild the directory tree of the original location in the backup directory.
     * <br/>
     * It takes an instance of {@link java.nio.file.Path} that is located within the original directory
     * and transforms it into a {@link java.nio.file.Path} pointing to the same location in the backup directory.
     *
     * @param original_path a {@link java.nio.file.Path} located somewhere in the original directory
     * @return a corresponding {@link java.nio.file.Path} in the backup directory
     */
    Path getBackupDestination(Path original_path) {
        int end = original_path.getNameCount();
        int start = end - Math.abs(end - getDirOriginal().getNameCount());
        if (start == end) {
            return getDirBackup();
        } else {
            return getDirBackup().resolve(original_path.subpath(start, end));
        }
    }

    /**
     * This method, together with {@link #getBackupDestination(java.nio.file.Path)}
     * , provides the program with the ability
     * to rebuild the directory tree of the original location in the backup directory.
     * <br/>
     * It takes an instance of {@link java.nio.file.Path} that is located within the backup directory
     * and transforms it into a {@link java.nio.file.Path} pointing to the same location in the original directory.
     *
     * @param backup_path a path located somewhere in the backup directory
     * @return a corresponding path to the original
     */
    Path getOriginalDestination(Path backup_path) {
        int end = backup_path.getNameCount();
        int start = end - Math.abs(end - getDirBackup().getNameCount());
        if (start == end) {
            return getDirOriginal();
        } else {
            return getDirOriginal().resolve(backup_path.subpath(start, end));
        }
    }

    /**
     * Indexes a path. Only paths to the original are indexed (they can be converted
     * back to the backup location with the {@link #getBackupDestination(java.nio.file.Path)} method).
     *
     * @param original_path instance of {@link java.nio.file.Path} pointing to file/directory to be indexed.
     * @param last_modified instance of {@link java.nio.file.attribute.FileTime}
     *                      specifying the time of last modification of the original file/directory
     */
    void addBackupToIndex(Path original_path, FileTime last_modified) {
        mBackupIndex.put(original_path.toString(), last_modified.toMillis());
    }

    /**
     * The opposite of {@link #addBackupToIndex(java.nio.file.Path, java.nio.file.attribute.FileTime)}.
     * Removes the path from the index.
     *
     * @param original_path instance of {@link java.nio.file.Path} that points to the original
     *                      to be removed from index.
     */
    void removeBackupFromIndex(Path original_path) {
        mBackupIndex.remove(original_path.toString());
    }

    /**
     * Checks if the input path is indexed.
     *
     * @param path the path to be checked.
     * @return returns <code>true</code>, if it is indexed, <code>false</code> if it isn't
     */
    boolean isIndexed(Path path) {
        return mBackupIndex.containsKey(path.toString());
    }

    /**
     * Fetches the last modified time known when the file/directory was last indexed.
     *
     * @param path input path
     * @return instance of {@link java.nio.file.attribute.FileTime}
     *         specifying the time of last modification of the original file/directory
     *         when it was last indexed.
     */
    FileTime getLastBackupTime(Path path) {
        return FileTime.fromMillis(mBackupIndex.get(path.toString()));
    }

    /**
     * Checks if a backup of an original file/directory exists.
     *
     * @param path the path to be checked.
     * @return returns <code>true</code>, if backup exists, <code>false</code> if it doesn't
     */
    boolean isBackupCreated(Path path) {
        Path backup_dest = getBackupDestination(path);
        return Files.exists(backup_dest);
    }

    /**
     * Creates a backup of a directory.
     *
     * @param dir {@link java.nio.file.Path} to the directory that is being backed.
     * @param last_modified known last modified time in the form of
     *                      {@link java.nio.file.attribute.FileTime} instance
     */
    void backupDirectory(Path dir, FileTime last_modified) {
        Path backup_dir = getBackupDestination(dir);
        try {
            Files.createDirectories(backup_dir);
        } catch (FileAlreadyExistsException exp) {
            System.err.format("Cannot create directory %s. File already exists: %s%n", backup_dir , exp.getFile());
            System.exit(1);
        } catch (SecurityException exp) {
            System.err.format("A security exception while creating directory: %s$n%s%n", backup_dir, exp.getLocalizedMessage());
            System.exit(1);
        } catch (IOException exp) {
            System.err.format("An I/O exception while creating directory: %s%n%s%n", backup_dir, exp.getLocalizedMessage());
            System.exit(1);
        }
        setBackupDirLastModified(backup_dir, last_modified);
        addBackupToIndex(dir, last_modified);
    }

    /**
     * Creates a backup of a file.
     *
     * @param file {@link java.nio.file.Path} to the file that is being backed.
     * @param last_modified known last modified time in the form of
     *                      {@link java.nio.file.attribute.FileTime} instance
     */
    void backupFile(Path file, FileTime last_modified) {
        Path backup_file = getBackupDestination(file);
        try {
            if (Files.isDirectory(backup_file)) {
                backup_file = backup_file.resolve(file.getFileName());
            }
            Files.copy(file, backup_file, StandardCopyOption.REPLACE_EXISTING);
        } catch (SecurityException exp) {
            System.err.format("A security exception while copying file: %s$n%s%n", backup_file, exp.getLocalizedMessage());
            System.exit(1);
        } catch (IOException exp) {
            System.err.format("An I/O exception while copying file: %s%n%s%n", backup_file, exp.getLocalizedMessage());
            System.exit(1);
        }
        addBackupToIndex(file, last_modified);
    }

    /**
     * Set last modified time for a directory. This method is used by {@link backupmanagment.BackupFileVisitor}
     * to set correct last modified times for directories in the backup location.
     * <br/>
     * NOTE: this feature is pretty much useless at the moment because
     * directories are created top to bottom and that means only one level directories will be affected.
     * A second file tree walk would be needed to eliminate this I suppose...
     *
     * @param backup_dir location in the backup directory
     * @param last_modified last modified time of the original directory (located somewhere in the backup path)
     */
    void setBackupDirLastModified(Path backup_dir, FileTime last_modified) {
        try {
            Files.setLastModifiedTime(backup_dir, last_modified);
        } catch (IOException | SecurityException exp) {
            System.err.format("Error setting last modified: %s%n", exp.getLocalizedMessage());
        }
    }

    // internal private methods

    private void removeDeleted() throws IOException{
        Files.walkFileTree(getDirBackup(), new DeleteFileVisitor(this));
    }

    // getters

    public Date getLastSyncDate() {
        return mLastSynchronization;
    }

}
