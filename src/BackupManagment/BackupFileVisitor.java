package BackupManagment;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.FileVisitResult.*;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * <br/>
 * Created by Martin Sicho on 22.3.14.
 */
class BackupFileVisitor implements java.nio.file.FileVisitor<Path> {
    private BackupInstance mBackupInstance;
    private FileTime mDirLastModified;

    BackupFileVisitor(BackupInstance instance) {
        mBackupInstance = instance;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        FileTime last_modified = attrs.lastModifiedTime();
        mDirLastModified = last_modified;
        if (dir.equals(mBackupInstance.getDirOriginal())) {
            Files.createDirectories(mBackupInstance.getDirBackup());
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
        if (mBackupInstance.isIndexed(file) && mBackupInstance.getLastBackupTime(file).compareTo(last_modified) != 0) {
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
        if (dir.equals(mBackupInstance.getDirOriginal())) {
            return CONTINUE;
        }
        mBackupInstance.setBackupDirLastModified(mBackupInstance.getBackupDestination(dir), mDirLastModified);
        return CONTINUE;
    }

}
