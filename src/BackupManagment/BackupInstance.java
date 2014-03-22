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
    private String mDirInput;
    private String mDirOutput;
    private boolean mShallow;
    private Map<String,Long> mBackupIndex;

    BackupInstance(BackupInstanceFramework framework) {
        mName = framework.getBackupName();
        mDirInput = framework.getDirInput().toString();
        mDirOutput = framework.getDirOutput().toString();
        mShallow = framework.wantsShallow();
        //mInputIndex = new HashMap<>();
        mBackupIndex = new HashMap<>();
        synchronize();
    }

    Path getInputPath() {
        return Paths.get(mDirInput);
    }

    Path getOutputPath() {
        return Paths.get(mDirOutput);
    }

    void indexBackup(Path input_dir, FileTime last_modified) {
        mBackupIndex.put(input_dir.toString(), last_modified.toMillis());
    }

    boolean indexed(Path dir) {
        return mBackupIndex.containsKey(dir.toString());
    }

    FileTime getLastBackupTime(Path path) {
        return FileTime.fromMillis(mBackupIndex.get(path.toString()));
    }

    void synchronize() {
        try {
            Files.walkFileTree(getInputPath(), new BackupFileVisitor(this));
            System.out.println(mName + ": Sync OK.");
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }
}
