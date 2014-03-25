package BackupManagment;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Implementation of the <code>{@link java.nio.file.FileVisitor FileVisitor}</code> interface.
 * Takes care of removing the files that were deleted in the original directory and are therefore no longer needed.
 * <br/>
 * This delete functionality is deliberately seperated from
 * the <code>{@link BackupManagment.BackupFileVisitor BackupFileVisitor}</code> in order to allow for easy
 * implementation of the case where the user wishes to keep all the files ever created in the original directory.
 *
 * <br/>
 * Created by Martin Sicho on 22.3.14.
 */
class DeleteFileVisitor implements java.nio.file.FileVisitor<Path> {
    private BackupInstance mBackupInstance;

    /**
     * The {@link BackupManagment.DeleteFileVisitor DeleteFileVisitor} constructor.
     * It takes an instance of {@link BackupManagment.BackupInstance BackupInstance}
     * to perform the delete actions.
     *
     * @param instance instance of {@link BackupManagment.BackupInstance BackupInstance}
     * to perform the backup actions
     */
    DeleteFileVisitor(BackupInstance instance) {
        mBackupInstance = instance;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//        if (Files.exists(mBackupInstance.getDirOriginal()) && !Files.isDirectory(mBackupInstance.getDirOriginal())) {
//            return CONTINUE;
//        }
        if (Files.notExists(mBackupInstance.getOriginalDestination(file))
                && mBackupInstance.isIndexed(mBackupInstance.getOriginalDestination(file))
                ) {
            Files.deleteIfExists(file);
            mBackupInstance.removeBackupFromIndex(mBackupInstance.getOriginalDestination(file));
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
            if (!Files.exists(mBackupInstance.getOriginalDestination(dir))
                    && mBackupInstance.isIndexed(mBackupInstance.getOriginalDestination(dir))
                    ) {
                Files.deleteIfExists(dir);
                mBackupInstance.removeBackupFromIndex(mBackupInstance.getOriginalDestination(dir));
            }
        } catch (DirectoryNotEmptyException exp) {
            System.out.println(exp.getMessage());
        }
        return CONTINUE;
    }
}
