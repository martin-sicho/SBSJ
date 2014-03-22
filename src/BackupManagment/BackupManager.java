package BackupManagment;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.HashMap;
import static enums.ProgramPaths.*;

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

    public BackupManager() {
        mBackupList = new HashMap<>();
        deserializeBackupList();
    }

    public void registerNewBackup(BackupInstanceFramework framework) {
        deserializeBackupList();

        if (!framework.getBackupName().equals("") && !mBackupList.containsKey(framework.getBackupName())) {
            mBackupList.put(framework.getBackupName(), new BackupInstance(framework));
        } else {
            //TODO: nastavit genericke jmeno
        }

        serializeBackupList(); // mozna by bylo dobre vytvorit nekdy metodu na serializaci jedine BackupInstance
    }

    private void serializeBackupList() {
        for (String key : mBackupList.keySet()) {
            try (
                    FileOutputStream fileOut = new FileOutputStream(BACKUPS_DIR.get() + key);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)
            ) {
                out.writeObject(mBackupList.get(key));
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }

    private void deserializeBackupList() {
        DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

            public boolean accept(Path path) throws IOException {
                try {
                    return !Files.isDirectory(path);
                } catch (Exception exp) {
                    System.err.println(exp);
                    return false;
                }
            }

        };

        try (
                DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BACKUPS_DIR.get()), filter)
        ) {
            for (Path file: stream) {
                String path = BACKUPS_DIR.get() + file.getFileName().toString();
                String key = file.getFileName().toString();
                try (
                        FileInputStream fileIn = new FileInputStream(path);
                        ObjectInputStream in = new ObjectInputStream(fileIn)
                ) {
                    try {
                        mBackupList.put(key, (BackupInstance) in.readObject());
                    } catch (ClassNotFoundException exp) {
                        exp.printStackTrace();
                    }
                } catch (IOException exp) {
                    exp.printStackTrace();
                }
            }
        } catch (IOException | DirectoryIteratorException exp) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            exp.printStackTrace();
        }
    }
}
