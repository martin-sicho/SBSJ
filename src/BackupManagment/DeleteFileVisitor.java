package BackupManagment;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * <br/>
 * Created by Martin Sicho on 22.3.14.
 */
class DeleteFileVisitor implements java.nio.file.FileVisitor<Path> {
    private BackupInstance mBackupInstance;

    DeleteFileVisitor(BackupInstance instance) {
        mBackupInstance = instance;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!Files.exists(mBackupInstance.getOriginalDestination(file))) {
            Files.deleteIfExists(file);
            mBackupInstance.removeBackupFromIndex(file);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (dir.equals(mBackupInstance.getDirBackup())) {
            return CONTINUE;
        }
        try {
            if (!Files.exists(mBackupInstance.getOriginalDestination(dir))) {
                Files.deleteIfExists(dir);
                mBackupInstance.removeBackupFromIndex(dir);
            }
        } catch (DirectoryNotEmptyException exp) {
            System.out.println(exp.getMessage());
        }
        return CONTINUE;
    }
}
