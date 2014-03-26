package backupmanagment;

import views.BackupViewer;

import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static enums.ProgramPaths.*;

/**
 * Takes care of loading instances of
 * <code>{@link backupmanagment.BackupInstance BackupInstance}</code>
 * and registers new ones.
 *
 * <br/>
 * Created by Martin Sicho on 19.3.14.
 */
public class BackupManager {
    private DateFormat mDateFormatter = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss_SSS");
    private Map<String,BackupInstance> mBackupList;

    /**
     * The {@link backupmanagment.BackupManager} constructor.
     * It creates the directory into which the scheduled backups are serialized
     * or it loads backups that are already scheduled.
     *
     */
    public BackupManager() {
        Path backup_dir = Paths.get(BACKUPS_DIR.toString());
        if (Files.notExists(backup_dir)) {
            try {
                Files.createDirectories(backup_dir);
            } catch (IOException exp) {
                System.err.println("Failed to create directory for scheduled backups:");
                System.err.println(exp.getMessage());
                System.exit(1);
            }
        }
        mBackupList = new HashMap<>();
        deserializeBackupList();
    }

    /**
     * This method is used to schedule a new backup
     * according to rules specified by the {@link backupmanagment.BackupInstanceFramework}
     * and implements all the logic to be able to do so.
     *
     * @param framework instance of {@link backupmanagment.BackupInstanceFramework}
     */
    public void registerNewBackup(BackupInstanceFramework framework) {
        if (framework.getBackupName().equals("") && !backupExists(framework.getBackupName())) {
            Date timestamp = new Date();
            framework.setName(framework.getDirOriginal().getFileName().toString() + "_" + mDateFormatter.format(timestamp));
            mBackupList.put(framework.getBackupName(), new BackupInstance(framework));
            serializeBackup(framework.getBackupName());
            System.out.println("Backup " + framework.getBackupName() + " was created successfully.");
        }
        else if (backupExists(framework.getBackupName())) {
            System.out.println("Backup " + framework.getBackupName() + " already exists. It will only be synchronized.");
            mBackupList.get(framework.getBackupName()).synchronize();
            serializeBackup(framework.getBackupName());
        }
        else {
            mBackupList.put(framework.getBackupName(), new BackupInstance(framework));
            serializeBackup(framework.getBackupName());
        }
    }

    /**
     * Check whether a backup with the specified name already exists or not.
     *
     * @param name name to be checked
     * @return returns <code>true</code>, if it exists, <code>false</code> if it doesn't
     */
    public boolean backupExists(String name) {
        return (mBackupList.containsKey(name));
    }

    /**
     * Synchronizes all scheduled backups
     */
    public void synchronize() {
        for (String key : mBackupList.keySet()) {
            mBackupList.get(key).synchronize();
        }
        serializeBackupList();
    }

    /**
     * Synchronizes only a specified backup.
     *
     * @param name name of the backup to be updated
     */
    public void synchronize(String name) {
        if (backupExists(name)) {
            mBackupList.get(name).synchronize();
            serializeBackup(name);
        } else {
            System.out.println("Synchronization canceled: Backup " + name + " not found.");
        }
    }

    /**
     * This method is used to provide information about backups to a view
     * that implements the {@link views.BackupViewer} interface.
     *
     * @param view instance of a class that implemets the {@link views.BackupViewer} interface.
     */
    public void updateView(BackupViewer view) {
        if (view.getName() == null || view.getName().equals("")) {
            List<String> keys = new ArrayList<>();
            for (String key : mBackupList.keySet()) {
                keys.add(key);
            }
            Collections.sort(keys);
            for (String item : keys) {
                BackupInstance backup_instance = mBackupList.get(item);
                view.listBackup(item
                        , backup_instance.getLastSyncDate()
                        , backup_instance.isShallow()
                        , backup_instance.getDirOriginal().toString()
                        , backup_instance.getDirBackup().toString()
                );
            }
        } else {
            String name = view.getName();
            BackupInstance backup_instance = mBackupList.get(name);
            view.listBackup(name
                    , backup_instance.getLastSyncDate()
                    , backup_instance.isShallow()
                    , backup_instance.getDirOriginal().toString()
                    , backup_instance.getDirBackup().toString()
            );
        }
    }

    // internal private methods

    private void serializeBackup(String key) {
        try (
                FileOutputStream fileOut = new FileOutputStream(BACKUPS_DIR.toString() + key);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)
        ) {
            out.writeObject(mBackupList.get(key));
        } catch (IOException exp) {
            System.err.format("The backup (name: %s) could not be saved: %n%s", key, exp.getLocalizedMessage());
        }
    }

    private void serializeBackupList() {
        for (String key : mBackupList.keySet()) {
            try (
                    FileOutputStream fileOut = new FileOutputStream(BACKUPS_DIR.toString() + key);
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
                    System.err.println(exp.getMessage());
                    return false;
                }
            }

        };

        try (
                DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BACKUPS_DIR.toString()), filter)
        ) {
            for (Path file: stream) {
                String path = BACKUPS_DIR.toString() + file.getFileName().toString();
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
