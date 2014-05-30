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
    public void showBackupInfo(String name, String original, String backup, boolean shallow, Date date);
    public String getBackupName();
    public void setBackupName(String name);
}
