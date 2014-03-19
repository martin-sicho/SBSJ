package main;

import BackupManagment.BackupJob;

/**
 * This is the main class.
 *
 * <br/>
 * Created by Martin Sicho on 11.3.14.
 */
public class Main {

    public static void main(String[] args) {
        BackupJob job = new BackupJob(ArgumentBuilder.build(args));

        if (job.wantsCreateNewBackup()) {
            // create new backup
        }

        if (job.wantsList()) {
            // call the view and list all scheduled backups
        }
    }
}
