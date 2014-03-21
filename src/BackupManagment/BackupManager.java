package BackupManagment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
//import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import enums.ProgramPaths;

/**
 * Takes care of loading instances of
 * <code>{@link BackupManagment.BackupInstance BackupInstance}</code>
 * and registers new ones.
 *
 * <br/>
 * Created by Martin Sicho on 19.3.14.
 */
public class BackupManager {
    private Map<String,BackupInstance> mBackupList;
    //private Iterator mBackupListIterator;

    public BackupManager() {
        mBackupList = new HashMap<>();
        //mBackupListIterator = mBackupList.keySet().iterator();
    }

    public void registerNewBackup(BackupInstanceFramework framework) {
        if (!framework.getBackupName().equals("") && !mBackupList.containsKey(framework.getBackupName())) {
            mBackupList.put(framework.getBackupName(), new BackupInstance(framework));
        } else {
            //TODO: nastavit genericke jmeno
        }

        serialzeBackupList();
    }

    private void serialzeBackupList() {
        for (String key : mBackupList.keySet()) {
            try (FileOutputStream fileOut = new FileOutputStream(ProgramPaths.BACKUPS_DIR.get() + key);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)
            ) {
                out.writeObject(mBackupList.get(key));
            }
            catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }

}
