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
    /**
     * This method is used by the {@link backupmanagment.BackupManager}
     * to show backup information in the view.
     *
     * @param name backup name
     * @param original original directory
     * @param backup backup directory
     * @param shallow specifies whether the backup is shallow or not
     * @param date when the last synchronization took place
     */
    public void showBackupInfo(String name, String original, String backup, boolean shallow, Date date);
    public String getBackupName();
    public void setBackupName(String name);
}
