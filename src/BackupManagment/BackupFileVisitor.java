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
    private int mLevel;
    private FileTime mDirLastModified;

    BackupFileVisitor(BackupInstance instance) {
        mBackupInstance = instance;
        mLevel = instance.getInputPath().getNameCount();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        FileTime last_modified = attrs.lastModifiedTime();
        mDirLastModified = last_modified;
        if (!isBackupCreated(dir)) {
            backupDirectory(dir, last_modified);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        FileTime last_modified = attrs.lastModifiedTime();
        if (mBackupInstance.indexed(file) && mBackupInstance.getLastBackupTime(file).compareTo(last_modified) != 0) {
                backupFile(file, last_modified);
         } else {
            mBackupInstance.indexBackup(file,last_modified);
            backupFile(file, last_modified);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        setBackupDirLastModified(dir, mDirLastModified);
        return CONTINUE;
    }

    // internal private methods
    private boolean isBackupCreated(Path path) {
        Path backup_dest = getBackupDestination(path);
        return Files.exists(backup_dest);
    }

    private void backupDirectory(Path dir, FileTime last_modified) throws IOException{
        Path backup_dir = getBackupDestination(dir);
        Files.createDirectories(backup_dir);
        setBackupDirLastModified(backup_dir, last_modified);
        mBackupInstance.indexBackup(dir, last_modified);
    }

    private void backupFile(Path file, FileTime last_modified) throws IOException{
        Path backup_file = getBackupDestination(file);
        Files.copy(file, backup_file, StandardCopyOption.REPLACE_EXISTING);
        mBackupInstance.indexBackup(file, last_modified);
    }

    private void setBackupDirLastModified(Path backup_dir, FileTime last_modified) throws IOException{
        Files.setLastModifiedTime(backup_dir, last_modified);
    }

    private Path getBackupDestination(Path path) {
        return mBackupInstance.getOutputPath().resolve(path.subpath(mLevel - 1,path.getNameCount()));
    }

}
