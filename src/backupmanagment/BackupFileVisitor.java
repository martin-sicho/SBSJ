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

    /**
     * Invoked for a directory before entries in the directory are visited.
     * <p/>
     * <p> If this method returns {@link java.nio.file.FileVisitResult#CONTINUE CONTINUE},
     * then entries in the directory are visited. If this method returns {@link
     * java.nio.file.FileVisitResult#SKIP_SUBTREE SKIP_SUBTREE} or {@link
     * java.nio.file.FileVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} then entries in the
     * directory (and any descendants) will not be visited.
     *
     * @param dir   a reference to the directory
     * @param attrs the directory's basic attributes
     * @return the visit result
     * @throws java.io.IOException if an I/O error occurs
     */
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

    /**
     * Invoked for a file in a directory.
     *
     * @param file  a reference to the file
     * @param attrs the file's basic attributes
     * @return the visit result
     * @throws java.io.IOException if an I/O error occurs
     */
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

    /**
     * Invoked for a file that could not be visited. This method is invoked
     * if the file's attributes could not be read, the file is a directory
     * that could not be opened, and other reasons.
     *
     * @param file a reference to the file
     * @param exc  the I/O exception that prevented the file from being visited
     * @return the visit result
     * @throws java.io.IOException if an I/O error occurs
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
    }

    /**
     * Invoked for a directory after entries in the directory, and all of their
     * descendants, have been visited. This method is also invoked when iteration
     * of the directory completes prematurely (by a {@link #visitFile visitFile}
     * method returning {@link java.nio.file.FileVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS},
     * or an I/O error when iterating over the directory).
     *
     * @param dir a reference to the directory
     * @param exc {@code null} if the iteration of the directory completes without
     *            an error; otherwise the I/O exception that caused the iteration
     *            of the directory to complete prematurely
     * @return the visit result
     * @throws java.io.IOException if an I/O error occurs
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (dir.equals(mBackupInstance.dirOriginal())) {
            return CONTINUE;
        }
        mBackupInstance.rewriteLastModified(mBackupInstance.retrieveBackupPath(dir), mDirLastModified);
        return CONTINUE;
    }

}
