package BackupManagment;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Holds everything there needs to be known about a scheduled backup.
 * The <code>{@link BackupManagment.BackupManager BackupManager}</code> class
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
    private Map<String,Long> mBackupIndex;

    BackupInstance(BackupInstanceFramework framework) {
        mName = framework.getBackupName();
        mDirOriginal = framework.getDirOriginal().toString();
        mDirBackup = framework.getDirBackup().toString();
        mShallow = framework.wantsShallow();
        mBackupIndex = new HashMap<>();
        synchronize();
    }

    void synchronize() {
        try {
            Files.walkFileTree(getDirOriginal(), new BackupFileVisitor(this, mShallow));
            removeDeleted();
            System.out.println(mName + ": Synchronization OK.");
        } catch (IOException exp) {
            System.err.println(mName + ": Synchronization FAILED.");
            System.err.println(exp.getMessage());
        }
    }

    Path getDirOriginal() {
        return Paths.get(mDirOriginal);
    }

    Path getDirBackup() {
        return Paths.get(mDirBackup);
    }

    Path getBackupDestination(Path original_path) {
        int end = original_path.getNameCount();
        int start = end - Math.abs(end - getDirOriginal().getNameCount());
        return getDirBackup().resolve(original_path.subpath(start, end));
    }

    Path getOriginalDestination(Path backup_path) {
        int end = backup_path.getNameCount();
        int start = end - Math.abs(end - getDirBackup().getNameCount());
        return getDirOriginal().resolve(backup_path.subpath(start, end));
    }

    void addBackupToIndex(Path input_path, FileTime last_modified) {
        mBackupIndex.put(input_path.toString(), last_modified.toMillis());
    }

    void removeBackupFromIndex(Path input_path) {
        mBackupIndex.remove(input_path.toString());
    }

    boolean isIndexed(Path path) {
        return mBackupIndex.containsKey(path.toString());
    }

    FileTime getLastBackupTime(Path path) {
        return FileTime.fromMillis(mBackupIndex.get(path.toString()));
    }

    boolean isBackupCreated(Path path) {
        Path backup_dest = getBackupDestination(path);
        return Files.exists(backup_dest);
    }

    void backupDirectory(Path dir, FileTime last_modified) {
        Path backup_dir = getBackupDestination(dir);
        try {
            Files.createDirectories(backup_dir);
        } catch (FileAlreadyExistsException exp) {
            System.err.format("Cannot create directory %s. File already exists: %s%n", backup_dir , exp.getFile());
            System.exit(1);
        } catch (SecurityException exp) {
            System.err.format("A security exception while creating directory: %s$n%s%n", backup_dir, exp.getMessage());
            System.exit(1);
        } catch (IOException exp) {
            System.err.format("An I/O exeption while creating directory: %s%n%s%n", backup_dir, exp.getMessage());
            System.exit(1);
        }
        setBackupDirLastModified(backup_dir, last_modified);
        addBackupToIndex(dir, last_modified);
    }

    void backupFile(Path file, FileTime last_modified) {
        Path backup_file = getBackupDestination(file);
        try {
            Files.copy(file, backup_file, StandardCopyOption.REPLACE_EXISTING);
        } catch (SecurityException exp) {
            System.err.format("A security exception while copying file: %s$n%s%n", backup_file, exp.getMessage());
            System.exit(1);
        } catch (IOException exp) {
            System.err.format("An I/O exeption while copying file: %s%n%s%n", backup_file, exp.getMessage());
            System.exit(1);
        }
        addBackupToIndex(file, last_modified);
    }

    void setBackupDirLastModified(Path backup_dir, FileTime last_modified) {
        try {
            Files.setLastModifiedTime(backup_dir, last_modified);
        } catch (IOException | SecurityException exp) {
            System.err.format("Error setting last modified: %s%n", exp.getMessage());
        }
    }

    // internal private methods

    private void removeDeleted() throws IOException{
        Files.walkFileTree(getDirBackup(), new DeleteFileVisitor(this));
    }

}
