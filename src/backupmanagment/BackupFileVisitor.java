package backupmanagment;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.FileVisitResult.*;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * Implementation of the <code>{@link java.nio.file.FileVisitor FileVisitor}</code> interface.
 * Takes care of copying and modifying the files in the backup directory according to
 * their state in the original directory.
 *
 * <br/>
 * Created by Martin Sicho on 22.3.14.
 */
class BackupFileVisitor implements java.nio.file.FileVisitor<Path> {
    private BackupInstance mBackupInstance;
    private FileTime mDirLastModified;

    /**
     * The {@link backupmanagment.BackupFileVisitor BackupFileVisitor} constructor.
     * It takes an instance of {@link backupmanagment.BackupInstance BackupInstance}
     * to perform the backup actions.
     *
     * @param instance instance of {@link backupmanagment.BackupInstance BackupInstance}
     * to perform the backup actions
     */
    BackupFileVisitor(BackupInstance instance) {
        mBackupInstance = instance;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        FileTime last_modified = attrs.lastModifiedTime();
        mDirLastModified = last_modified;
        if (dir.equals(mBackupInstance.dirOriginal())) {
            return CONTINUE;
        }
        if (mBackupInstance.isShallow()) {
            return SKIP_SUBTREE;
        }
        if (!mBackupInstance.isBackupCreated(dir)) {
            mBackupInstance.backupDirectory(dir, last_modified);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        FileTime last_modified = attrs.lastModifiedTime();
        if (mBackupInstance.isPathIndexed(file) && mBackupInstance.retrieveLastBackupTime(file).compareTo(last_modified) != 0) {
            mBackupInstance.backupFile(file, last_modified);
         } else {
            mBackupInstance.addBackupToIndex(file, last_modified);
            mBackupInstance.backupFile(file, last_modified);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (dir.equals(mBackupInstance.dirOriginal())) {
            return CONTINUE;
        }
        mBackupInstance.rewriteLastModified(mBackupInstance.retrieveBackupPath(dir), mDirLastModified);
        return CONTINUE;
    }

}
