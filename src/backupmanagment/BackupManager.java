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
    private List<String> mFailedToLoad = new ArrayList<>();

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
        String name = framework.getBackupName();
        if (name.equals("") && !backupExists(name)) {
            Date timestamp = new Date();
            framework.setName(framework.getDirOriginal().getFileName().toString() + "_" + mDateFormatter.format(timestamp));
            mBackupList.put(name, new BackupInstance(framework));
            serializeBackup(name);
            System.out.println("Backup " + name + " was created successfully.");
        }
        else if (backupExists(name)) {
            System.out.println("Backup " + name + " already exists. It will only be synchronized.");
            mBackupList.get(name).synchronize();
            serializeBackup(name);
        }
        else if (mFailedToLoad.contains(name)) {
            System.out.println("Backup with the name " + name + " already exists and previously failed to load.");
            System.out.println("Remove the backup first (" + Paths.get(BACKUPS_DIR.toString(), name).toAbsolutePath()
                    + ") and run the utility again to replace it: ");
        }
        else {
            mBackupList.put(name, new BackupInstance(framework));
            serializeBackup(name);
        }
    }

    /**
     * Check whether a backup with the specified name exists in the
     * {@link backupmanagment.BackupManager} or not.
     *
     * @param name name to be checked
     * @return returns <code>true</code>, if it exists, <code>false</code> if it doesn't
     */
    public boolean backupExists(String name) {
        return mBackupList.containsKey(name);
    }

    /**
     * Check whether a backup file with the specified name exists or not.
     *
     * @param name name of the backup file
     * @return @return returns <code>true</code>, if it exists, <code>false</code> if it doesn't
     */
    public boolean backupFileExists(String name) {
        return Files.exists(Paths.get(BACKUPS_DIR.toString(), name));
    }

    /**
     * Synchronizes all scheduled backups
     */
    public void synchronize() {
        if (mBackupList.isEmpty()) System.out.println("No backups scheduled. Nothing to synchronize...");
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
                        , ((Boolean) backup_instance.isShallow()).toString()
                        , backup_instance.getDirOriginal().toString()
                        , backup_instance.getDirBackup().toString()
                );
            }
        } else {
            String name = view.getName();
            BackupInstance backup_instance = mBackupList.get(name);
            view.listBackup(name
                    , backup_instance.getLastSyncDate()
                    , ((Boolean) backup_instance.isShallow()).toString()
                    , backup_instance.getDirOriginal().toString()
                    , backup_instance.getDirBackup().toString()
            );
        }
    }

    /**
     * Attempts to delete a backup.
     *
     * @param name backup name
     */
    public void deleteBackup(String name) {
        if (backupFileExists(name)) {
            try {
                Files.delete(Paths.get(BACKUPS_DIR.toString(), name));
                System.out.println("Backup deleted: " + name);
            } catch (IOException exp) {
                System.err.println("IO Exception occured.");
                System.err.println("FAILED to delete backup: " + name);
            }
            mBackupList.remove(name);
        } else {
            System.out.println("Backup " + name + " doesn't exist. Nothing to delete...");
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
            serializeBackup(key);
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
                    } catch (InvalidClassException exp) {
                        System.err.println("Could not load following backup (it was probably created " +
                                "by an earlier version of the utility or the file is corrupted): " + file.getFileName());
                        mFailedToLoad.add(key);
                    } catch (ClassNotFoundException exp) {
                        exp.printStackTrace();
                    }
                } catch (IOException exp) {
                    System.err.println("An IO Exception occured. Could not load following backup: " + file.getFileName());
                }
            }
        } catch (IOException | DirectoryIteratorException exp) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            exp.printStackTrace();
        }
    }
}
