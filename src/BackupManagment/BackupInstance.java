package BackupManagment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            System.out.println(mName + ": Sync OK.");
        } catch (IOException exp) {
            exp.printStackTrace();
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

    // internal private methods

    private void removeDeleted() throws IOException{
        Files.walkFileTree(getDirBackup(), new DeleteFileVisitor(this));
    }

}
