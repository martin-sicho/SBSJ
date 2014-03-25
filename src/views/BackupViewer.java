package views;

import java.util.Date;

/**
 * This interface connects the {@link backupmanagment.BackupManager} instances
 * with the user. Any class that implements this interface can be provided
 * with information about scheduled backups via
 * the {@link backupmanagment.BackupManager#updateView(BackupViewer) updateView(BackupViewer)} method.
 *
 * <br/>
 * Created by Martin Sicho on 25.3.14.
 */
public interface BackupViewer {
    void printHeader();
    void listBackup(String name, Date date, boolean shallow);
    String getName();
    void setName(String name);
}
