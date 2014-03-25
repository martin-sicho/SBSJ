package main;

import BackupManagment.*;

/**
 * This is the main class.
 *
 * <br/>
 * Created by Martin Sicho on 11.3.14.
 */
public class Main {

    /**
     * This is the main method.
     * @param args command line arguments
     *             as a <code>{@link java.lang.String String}</code> array.
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            BackupInstanceFramework framework = new BackupInstanceFramework(ArgumentBuilder.build(args));
            BackupManager manager = new BackupManager();

            if (framework.wantsCreateNewBackup()) {
                manager.registerNewBackup(framework);
            }

            if (framework.wantsSynchronization()) {
                if (framework.wantsSynchronization() && !framework.getBackupName().equals("")) {
                    manager.synchronize(framework.getBackupName());
                } else {
                    manager.synchronize();
                }
            }

            if (framework.wantsList()) {
                // call the view and list all scheduled backups
                // TODO: implement the view
            }
        } else {
            System.out.println("Run the utility with -h or --help option to toString usage details.");
        }
    }
}
