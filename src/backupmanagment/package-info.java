/**
 * This package contains what one might call the backend.
 * It includes the {@link backupmanagment.BackupManager} class
 * that manages instances of {@link backupmanagment.BackupInstance}.
 * Each {@link backupmanagment.BackupInstance} is then responsible for one single backup.
 * The {@link backupmanagment.BackupInstanceFramework} class is the general template used
 * by the {@link backupmanagment.BackupManager} to perform actions upon backups and create new ones.
 *
 * <br />
 * Created by Martin Sicho on 1.6.2014.
 */
package backupmanagment;