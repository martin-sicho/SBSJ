package main;

import backupmanagment.*;
import views.CommandLineViewer;

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
            CommandLineViewer viewer = new CommandLineViewer();

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
                viewer.setName(framework.getBackupName());
                viewer.printHeader();
                manager.updateView(viewer);
            }
        } else {
            System.out.println("Run the utility with -h or --help option to get usage details.");
        }
    }
}
