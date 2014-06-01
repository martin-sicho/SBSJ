package views;

import backupmanagment.BackupInstanceFramework;
import backupmanagment.BackupManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <br />
 * Created by Martin Sicho on 31.5.2014.
 */
class CreateNewBackupWindow extends JFrame implements Runnable {
    // containers
    private JPanel pContainer;

    // components
    private JTextField tfBackupName;
    private JButton btOriginalDest;
    private JTextField tfOriginalDest;
    private JButton btBackupDest;
    private JTextField tfBackupDest;
    private JCheckBox cbShallow;
    private JCheckBox cbKeepAll;
    private JButton btCreateBackup;


    // members
    private BackupManager mBackupManager;
    private MainWindow mParentWindow;
    private BackupInstanceFramework mFramework;

    CreateNewBackupWindow(BackupManager manager, MainWindow parent) {
        setTitle("Create New Backup");
        getContentPane().add($$$getRootComponent$$$());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
        setLocationByPlatform(true);

        mBackupManager = manager;
        mParentWindow = parent;
        mFramework = new BackupInstanceFramework();

        attachVerifiers();
        attachListeners();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        setVisible(true);
    }

    // private methods

    private void attachVerifiers() {
        tfBackupName.setInputVerifier(new InputVerifier() {
            public boolean verify(JComponent comp) {
                JTextField field = (JTextField) comp;
                boolean backup_exists = mBackupManager.backupExists(field.getText());
                if (backup_exists) {
                    JOptionPane.showMessageDialog(
                            CreateNewBackupWindow.this
                            , "A backup with this name already exists. Please, use a different name."
                    );
                }
                return !backup_exists;
            }
        });

        tfOriginalDest.setInputVerifier(new InputVerifier() {
            public boolean verify(JComponent comp) {
                JTextField field = (JTextField) comp;
                try {
                    Paths.get(field.getText()).toRealPath().normalize();
                } catch (InvalidPathException exp) {
                    String msg = "The specified path: " + field.getText() + " is invalid.";
                    System.out.println(msg);
                    JOptionPane.showMessageDialog(CreateNewBackupWindow.this, msg);
                    field.setText("");
                    return false;
                } catch (IOException e) {
                    String msg = "The specified path: " + field.getText() + " doesn't exist.";
                    System.out.println(msg);
                    JOptionPane.showMessageDialog(CreateNewBackupWindow.this, msg);
                    field.setText("");
                    return false;
                }
                return true;
            }
        });

        tfBackupDest.setInputVerifier(new InputVerifier() {
            public boolean verify(JComponent comp) {
                JTextField field = (JTextField) comp;
                try {
                    Paths.get(field.getText()).toAbsolutePath().normalize();
                } catch (InvalidPathException exp) {
                    String msg = "The specified path: " + field.getText() + " is invalid.";
                    System.out.println(msg);
                    JOptionPane.showMessageDialog(CreateNewBackupWindow.this, msg);
                    field.setText("");
                    return false;
                }
                return true;
            }
        });
    }

    private void attachListeners() {
        btOriginalDest.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the user clicks the button that opens the
             * File Selection Dialog to specify the path he/she wants to make a backup of.
             *
             * @param e event that occured when the user clicked the button
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = chooser.showOpenDialog(CreateNewBackupWindow.this);
                if (result == JFileChooser.CANCEL_OPTION) return;
                try {
                    File file = chooser.getSelectedFile();
                    tfOriginalDest.setText(file.getAbsolutePath());
                    tfOriginalDest.requestFocus();
                } catch (Exception exp) {
                    tfOriginalDest.setText("Could not load file: " + exp.getMessage());
                }
            }
        });

        btBackupDest.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the user clicks the button that opens the
             * File Selection Dialog to specify the directory into which he/she
             * wants to synchronize the original files.
             *
             * @param e event that occured when the user clicked the button
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(CreateNewBackupWindow.this);
                if (result == JFileChooser.CANCEL_OPTION) return;
                try {
                    File file = chooser.getSelectedFile();
                    tfBackupDest.setText(file.getAbsolutePath());
                    tfBackupDest.requestFocus();
                } catch (Exception exp) {
                    tfBackupDest.setText("Could not load file: " + exp.getMessage());
                }
            }
        });

        btCreateBackup.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the user decides to add a backup with currently specified parameters to the watchlist.
             *
             * @param e event that occured when the user clicked the button
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                Path original = Paths.get(tfOriginalDest.getText()).toAbsolutePath();
                Path backup = Paths.get(tfBackupDest.getText()).toAbsolutePath();
                boolean keep_all = cbKeepAll.isSelected();
                boolean shallow = cbShallow.isSelected();
                String name = tfBackupName.getText();

                // test if backup exists
                if (mBackupManager.backupExists(name)) {
                    String msg = "This backup already exists. It will only be synchronized.";
                    JOptionPane.showMessageDialog(CreateNewBackupWindow.this, msg);
                }

                // paths cannot be equal
                if (backup.equals(original)) {
                    String msg = "<html>The original and backup paths are equal. "
                            + "You have to put your backup<br /> into a directory that " +
                            "is different from the original directory!</html>";
                    System.out.println(msg);
                    JOptionPane.showMessageDialog(CreateNewBackupWindow.this, msg);
                    return;
                }

                // check for recursive loop
                if (backup.getRoot().equals(original.getRoot())) {
                    Path pp = backup.relativize(original);
                    boolean backup_is_subpath = true;
                    for (int i = 0; i < pp.getNameCount(); i++) {
                        if (!pp.getName(i).toString().equals("..")) {
                            backup_is_subpath = false;
                            break;
                        }
                    }
                    if (backup_is_subpath) {
                        String msg = "<html>Backup can't be a subpath of original!!! " +
                                "<br />You really don't want to see the things that would ensue.</html>";
                        System.out.println(msg);
                        JOptionPane.showMessageDialog(CreateNewBackupWindow.this, msg);
                        return;
                    }
                }

                // warn if the backup is inefective (the backup location contains the original file)
                if (original.getParent().equals(backup) && !Files.isDirectory(original)) {
                    String msg = "WARNING: Backup inefective - the backup location contains the original file!";
                    System.out.println(msg);
                    JOptionPane.showMessageDialog(CreateNewBackupWindow.this, msg);
                }

                // build the framework
                mFramework.setOptions(
                        original
                        , backup
                        , name
                        , null
                        , keep_all
                        , shallow
                        , true
                        , false
                        , true
                );

                // execute backup creation process
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mBackupManager.registerNewBackup(mFramework);
                        mParentWindow.updateTable();
                    }
                }).start();

                // close the window or not close the window, that is the question :]
//                CreateNewBackupWindow.this.dispose();
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        pContainer = new JPanel();
        pContainer.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        pContainer.setPreferredSize(new Dimension(500, 250));
        final JScrollPane scrollPane1 = new JScrollPane();
        pContainer.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Options"));
        cbShallow = new JCheckBox();
        cbShallow.setActionCommand("cbShallow");
        cbShallow.setText("Only make a shallow copy.");
        cbShallow.setToolTipText("Do not descent into subdirectories.");
        panel2.add(cbShallow, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cbKeepAll = new JCheckBox();
        cbKeepAll.setActionCommand("cbKeepAll");
        cbKeepAll.setText("Don't delete any files from the backup directory. ");
        cbKeepAll.setToolTipText("All files will remain. Even if thei're removed from the original.");
        panel2.add(cbKeepAll, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Paths"));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btOriginalDest = new JButton();
        btOriginalDest.setActionCommand("btOriginalDest");
        btOriginalDest.setLabel("Original Path");
        btOriginalDest.setText("Original Path");
        btOriginalDest.setToolTipText("Can be a directory or a file.");
        panel4.add(btOriginalDest, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(135, -1), null, null, 0, false));
        tfOriginalDest = new JTextField();
        tfOriginalDest.setText(".");
        tfOriginalDest.setToolTipText("You can use both relative and absolute paths. If left empty, current directory is used.");
        panel4.add(tfOriginalDest, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btBackupDest = new JButton();
        btBackupDest.setActionCommand("btbackupDest");
        btBackupDest.setText("Backup Directory");
        btBackupDest.setToolTipText("Always a directory. If it doesn't exist, it is created.");
        panel5.add(btBackupDest, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(135, -1), null, null, 0, false));
        tfBackupDest = new JTextField();
        tfBackupDest.setText(".");
        tfBackupDest.setToolTipText("You can use both relative and absolute paths. If left empty, current directory is used.");
        panel5.add(tfBackupDest, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Backup Name"));
        tfBackupName = new JTextField();
        tfBackupName.setText("Great New Backup");
        tfBackupName.setToolTipText("Must be unique for each backup.");
        panel6.add(tfBackupName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btCreateBackup = new JButton();
        btCreateBackup.setActionCommand("btCreateBackup");
        btCreateBackup.setText("Create Backup");
        btCreateBackup.setToolTipText("Create a backup with the specified parameters.");
        panel1.add(btCreateBackup, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pContainer;
    }
}
