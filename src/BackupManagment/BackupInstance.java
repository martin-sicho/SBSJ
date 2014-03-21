package BackupManagment;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    private Map<String,Path> mInputIndex;
    private Map<String,Path> mOutputIndex;

    BackupInstance(BackupInstanceFramework framework) {
        mName = framework.getBackupName();
        mDirInput = framework.getDirInput().toString();
        mDirOutput = framework.getDirOutput().toString();
        mShallow = framework.wantsShallow();
        mInputIndex = new HashMap<>();
        mOutputIndex = new HashMap<>();

    }

    private Path getInputPath() {
        return Paths.get(mDirInput);
    }

    private Path getOutputPath() {
        return Paths.get(mDirOutput);
    }

    public void indexInput() {

    }

    public void indexOutput() {

    }
}
