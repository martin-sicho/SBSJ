package views;

import backupmanagment.BackupManager;
import views.tablerenderers.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Set;

/**
 * This class wraps all components of the aplication main window.
 * <p/>
 * <br/>
 * Created by Martin Sicho on 26.5.2014.
 */
public class MainWindow extends JFrame {
    // containers
    private JPanel pContainer;
    private JPanel pIntro;
    private JScrollPane scrlPane;
    private JPanel pButtons;

    // components
    private JTable tbTable;
    private JButton btSyncSele;
    private JButton btSyncAll;
    private JButton btCreateNew;
    private JLabel lbIntro;
    private JButton btDelSelected;

    // members
    BackupTableModel mTableModel;
    BackupManager mBackupManager;
    int mMousedOverRow;
    int mMousedOverColumn;

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

    public void showGUI() {
        setVisible(true);
    }

    public void updateTable() {
        mTableModel.update();
        tbTable.revalidate();
        scrlPane.repaint();
    }

    // private methods

    private void setUpTable() {
        tbTable.setRowHeight(25);
        mTableModel = new BackupTableModel(mBackupManager);
        tbTable.setModel(mTableModel);
        tbTable.setDefaultRenderer(Date.class, new BackupTableDateRenderer());
        TableColumn col = tbTable.getColumn("Synchronize");
        col.setCellEditor(new BackupTableButtonEditor());
        col.setCellRenderer(new BackupTableSyncButtonRenderer());
        col.setWidth(100);
        col = tbTable.getColumn("Selected");
        col.setMaxWidth(60);
        col = tbTable.getColumn("Shallow");
        col.setMaxWidth(60);
        col = tbTable.getColumn("Name");
        col.setPreferredWidth(50);
    }

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
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                Set<String> backup_names = mTableModel.getSelectedBackups();
                for (String name : backup_names) {
                    mBackupManager.deleteBackup(name);
                    mTableModel.removeFromSelectedBackups(name);
                }
                updateTable();
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
