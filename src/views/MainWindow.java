package views;

import backupmanagment.BackupManager;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is the {@link javax.swing.JFrame} that
 * wraps all components of the application's main window.
 * It uses an instance of {@link javax.swing.JTable} to display the backup watchlist.
 * There are also a few buttons to also allow the user to interact
 * with the {@link backupmanagment.BackupManager} instance.
 *
 * <br/>
 * Created by Martin Sicho on 26.5.2014.
 */
public class MainWindow extends JFrame {
    // containers
    /**
     * This is the root component.
     */
    private JPanel pContainer;
    /**
     * This {@link javax.swing.JPanel} holds the text at the top of the {@link views.MainWindow}.
     */
    private JPanel pIntro;
    /**
     * This is the {@link javax.swing.JScrollPane} that wraps the table.
     */
    private JScrollPane scrlPane;
    /**
     * This {@link javax.swing.JPanel} holds the buttons at the bottom of the {@link views.MainWindow}.
     */
    private JPanel pButtons;

    // components
    /**
     * The interactive list of backups.
     */
    private JTable tbTable;
    /**
     * The Synchonize Selected button.
     */
    private JButton btSyncSele;
    /**
     * The Synchonize All button.
     */
    private JButton btSyncAll;
    /**
     * The Create New Backup button.
     */
    private JButton btCreateNew;
    /**
     * The {@link javax.swing.JLabel} with the introductory text.
     */
    private JLabel lbIntro;
    /**
     * The Delete Selected button.
     */
    private JButton btDelSelected;

    // members
    /**
     * This is the table model. It is responsible for the data displayed inside the {@link #tbTable}.
     */
    private BackupTableModel mTableModel;
    /**
     * The {@link backupmanagment.BackupManager}.
     */
    private BackupManager mBackupManager;
    /**
     * The row of the table that the mouse cursor is currently hovering over.
     */
    private int mMousedOverRow;
    /**
     * The column of the table that the mouse cursor is currently hovering over.
     */
    private int mMousedOverColumn;

    /**
     * This constructor first registers an instance of {@link backupmanagment.BackupManager}
     * and then uses its information to build and display the GUI to the user.
     */
    public MainWindow() {
        mBackupManager = new BackupManager();

        setTitle("Simple Backup System in Java");
        getContentPane().add($$$getRootComponent$$$());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setUpTable();

        attachListeners();

        setMinimumSize(new Dimension(600, 200));
        pack();
        setLocationByPlatform(true);
    }

    /**
     * Displays the {@link javax.swing.JFrame} to the user.
     */
    public void showGUI() {
        setVisible(true);
    }

    /**
     * Requests update of the {@link views.BackupTableModel}
     * (for example when the user clicks the Delete Backup button)
     * and updates the displayed table.
     */
    public void updateTable() {
        mTableModel.update();
        tbTable.revalidate();
        scrlPane.repaint();
    }

    // private methods

    /**
     * Just a wrapper method to make the table nice and cozy.
     * It associates the {@link views.BackupTableModel},
     * {@link views.BackupTableDateRenderer}, {@link views.BackupTableSyncButtonRenderer},
     * and {@link BackupTableSyncButtonEditor} with the table and sets some dimension constraints.
     */
    private void setUpTable() {
        tbTable.setRowHeight(25);
        mTableModel = new BackupTableModel(mBackupManager);
        tbTable.setModel(mTableModel);
        tbTable.setDefaultRenderer(Date.class, new BackupTableDateRenderer());
        TableColumn col = tbTable.getColumn("Synchronize");
        col.setCellEditor(new BackupTableSyncButtonEditor());
        col.setCellRenderer(new BackupTableSyncButtonRenderer());
        col.setWidth(100);
        col = tbTable.getColumn("Selected");
        col.setMaxWidth(60);
        col = tbTable.getColumn("Shallow");
        col.setMaxWidth(60);
        col = tbTable.getColumn("Name");
        col.setPreferredWidth(50);
    }

    /**
     * Just a wrapper method that associates the table and every button
     * with the respective listeners.
     */
    private void attachListeners() {
        // table

        tbTable.addMouseMotionListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been moved over the area of the table.
             *
             * @param e the resulting event
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                JTable aTable = (JTable) e.getSource();
                mMousedOverRow = aTable.rowAtPoint(e.getPoint());
                mMousedOverColumn = aTable.columnAtPoint(e.getPoint());
                TableColumn col = aTable.getColumnModel().getColumn(mMousedOverColumn);
                String id = (String) col.getIdentifier();
                BackupTableSyncButtonRenderer renderer;
                if (id.equals("Synchronize")) {
                    renderer = (BackupTableSyncButtonRenderer) aTable.getCellRenderer(mMousedOverRow, mMousedOverColumn);
                    renderer.setCoords(mMousedOverRow, mMousedOverColumn);
                } else {
                    col = aTable.getColumn("Synchronize");
                    renderer = (BackupTableSyncButtonRenderer) col.getCellRenderer();
                    renderer.setCoords(-1, -1);
                    renderer.setSelected(false);
                }
                renderer.repaint();
                aTable.repaint();
            }
        });

        tbTable.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been moved outside the area of the table.
             *
             * @param e the captured event
             */
            @Override
            public void mouseExited(MouseEvent e) {
                JTable aTable = (JTable) e.getSource();
                TableColumn col = aTable.getColumn("Synchronize");
                BackupTableSyncButtonRenderer renderer = (BackupTableSyncButtonRenderer) col.getCellRenderer();
                renderer.setCoords(-1, -1);
                renderer.setSelected(false);
                renderer.repaint();
                aTable.repaint();
            }
        });

        // buttons

        btSyncSele.addMouseListener(new MouseAdapter() {
            /**
             * When the user clicks this button, the backups currently selected in the table are synchronized.
             *
             * @param e the resulting event
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                Set<String> backup_names = mTableModel.getSelectedBackups();
                for (final String name : backup_names) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mBackupManager.synchronize(name);
                            updateTable();
                        }
                    }).start();
                }
            }
        });

        btSyncAll.addMouseListener(new MouseAdapter() {
            /**
             * When the user clicks this button, all backups are synchronized.
             *
             * @param e the resulting event
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mBackupManager.synchronize();
                        updateTable();
                    }
                }).start();
            }
        });

        btCreateNew.addMouseListener(new MouseAdapter() {
            /**
             * Displays a window where user can specify the details of new backups and add them to the watchlist.
             *
             * @param e the resulting event
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(new CreateNewBackupWindow(mBackupManager, MainWindow.this)).start();
            }
        });

        btDelSelected.addMouseListener(new MouseAdapter() {
            /**
             * This method is invoked when the user decides to delete selected backups.
             *
             * @param e the resulting event
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                int selection = JOptionPane.showConfirmDialog(MainWindow.this, "Do you want to remove all selected backups?");
                switch (selection) {
                    case JOptionPane.YES_OPTION:
                        Set<String> removed_names = new HashSet<>();
                        for (String name : mTableModel.getSelectedBackups()) {
                            mBackupManager.deleteBackup(name);
                            removed_names.add(name);
                        }
                        for (String name : removed_names) {
                            mTableModel.removeFromSelectedBackups(name);
                        }
                        updateTable();
                        break;
                }
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
        pContainer.setLayout(new BorderLayout(0, 0));
        pContainer.setPreferredSize(new Dimension(650, 300));
        pButtons = new JPanel();
        pButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pButtons.setAlignmentX(0.5f);
        pContainer.add(pButtons, BorderLayout.SOUTH);
        btDelSelected = new JButton();
        btDelSelected.setActionCommand("btDelSelected");
        btDelSelected.setText("Delete Selected");
        btDelSelected.setToolTipText("Delete selected backups. Only removes backups from the application. All files are preserved.");
        pButtons.add(btDelSelected);
        btSyncSele = new JButton();
        btSyncSele.setActionCommand("btSyncSele");
        btSyncSele.setText("Synchronize Seleceted");
        btSyncSele.setToolTipText("Synchronize selected backups.");
        pButtons.add(btSyncSele);
        btSyncAll = new JButton();
        btSyncAll.setActionCommand("btSyncAll");
        btSyncAll.setText("Synchronize All");
        btSyncAll.setToolTipText("Synchronize all backups.");
        pButtons.add(btSyncAll);
        btCreateNew = new JButton();
        btCreateNew.setActionCommand("btCreateNew");
        btCreateNew.setText("Create New Backup");
        btCreateNew.setToolTipText("Create new backup.");
        pButtons.add(btCreateNew);
        scrlPane = new JScrollPane();
        pContainer.add(scrlPane, BorderLayout.CENTER);
        tbTable = new JTable();
        scrlPane.setViewportView(tbTable);
        pIntro = new JPanel();
        pIntro.setLayout(new BorderLayout(0, 0));
        pContainer.add(pIntro, BorderLayout.NORTH);
        pIntro.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 3, 5, 3), null));
        lbIntro = new JLabel();
        lbIntro.setText("<html>\n<p>\nThis tiny application provides its user with the ability to make directory or file backups with a simple click of a button.\n</p>\n<p>\nBelow is a table with a summary of all currently scheduled backups. You can manage them directly from here or via the command line interface (use the <code>-h</code> or <code>--help</code> option for usage details).\n</p>\n</html>");
        pIntro.add(lbIntro, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pContainer;
    }
}
