package main;

import BackupManagment.*;

/**
 * This is the main class.
 *
 * <br/>
 * Created by Martin Sicho on 11.3.14.
 */
public class Main {

    public static void main(String[] args) {
        BackupInstanceFramework framework = new BackupInstanceFramework(ArgumentBuilder.build(args));
        BackupManager manager = new BackupManager();

        if (framework.wantsCreateNewBackup()) {
            manager.registerNewBackup(framework);
        }

        if (framework.wantsList()) {
            // call the view and list all scheduled backups
        }
    }
}
