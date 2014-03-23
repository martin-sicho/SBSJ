package BackupManagment;

import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private DateFormat mDateFormatter = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss_SSS");
    private Map<String,BackupInstance> mBackupList;

    public BackupManager() {
        Path backup_dir = Paths.get(BACKUPS_DIR.get());
        if (Files.notExists(backup_dir)) {
            try {
                Files.createDirectories(backup_dir);
            } catch (IOException exp) {
                System.err.println("Failed to create directory for scheduled backups:");
                System.err.println(exp.getMessage());
            }
        }
        mBackupList = new HashMap<>();
        deserializeBackupList();
    }

    public void registerNewBackup(BackupInstanceFramework framework) {
        if (framework.getBackupName().equals("") && !backupExists(framework.getBackupName())) {
            Date timestamp = new Date();
            framework.setName(framework.getDirOriginal().getFileName().toString() + "_" + mDateFormatter.format(timestamp));
            mBackupList.put(framework.getBackupName(), new BackupInstance(framework));
        }
        else if (backupExists(framework.getBackupName())) {
            System.out.println("Backup " + framework.getBackupName() + " already exists. It will only be synchronized.");
            mBackupList.get(framework.getBackupName()).synchronize();
        }
        else {
            mBackupList.put(framework.getBackupName(), new BackupInstance(framework));
        }

        serializeBackupList();
    }

    public boolean backupExists(String name) {
        return (mBackupList.containsKey(name));
    }

    public void synchronize() {
        for (String key : mBackupList.keySet()) {
            mBackupList.get(key).synchronize();
        }
    }

    public void synchronize(String key) {
        mBackupList.get(key).synchronize();
    }

    // internal private methods

    private void serializeBackupList() {
        for (String key : mBackupList.keySet()) {
            try (
                    FileOutputStream fileOut = new FileOutputStream(BACKUPS_DIR.get() + key);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)
            ) {
                out.writeObject(mBackupList.get(key));
            } catch (IOException exp) {
                System.err.format("The backup (name: %s) could not be saved: %n%s", key, exp.getLocalizedMessage());
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
