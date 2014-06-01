package views;

import backupmanagment.BackupManager;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * This class extends the {@link javax.swing.table.AbstractTableModel}, which
 * implements most of the methods required by {@link javax.swing.table.TableModel}
 * interface and serves as a base class to implement customized tables.
 * <p>
 * This class also implements the {@link views.BackupViewer} interface
 * to receive updates from the {@link backupmanagment.BackupManager}
 * and the {@link javax.swing.event.TableModelListener} interface,
 * which defines the {@link javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
 * tableChanged(TableModelEvent e)}
 * method that is executed every time the table model changes.
 * </p>
 *
 * <br />
 * Created by Martin Sicho on 30.5.2014.
 */
class BackupTableModel extends AbstractTableModel implements TableModelListener, BackupViewer {

    /**
     * The table header as an array of {@link java.lang.String}.
     */
    private String[] mTableHeader = {
            "Synchronize"
            , "Selected"
            , "Name"
            , "Original Directory"
            , "Backup Directory"
            , "Shallow"
            , "Last Synchronized"
    };
    /**
     * The data saved in the table cells represented by
     * a two dimensional {@link java.util.Vector}.
     */
    private Vector<Vector> mTableData = new Vector<>();
    /**
     * A {@link java.util.Set} that holds items that are currently selected.
     */
    Set<String> mSelectedBackups = new HashSet<>();
    /**
     * Instance of {@link backupmanagment.BackupManager} that is
     * shared between the model and the {@link views.MainWindow}.
     */
    private BackupManager mBackupManager;

    /**
     * Class constructor. Registers the passed in {@link backupmanagment.BackupManager}
     * , updates itself and registers itself to listen for {@link javax.swing.event.TableModelEvent} events.
     *
     * @param manager instance of {@link backupmanagment.BackupManager} that is
     * shared between the model and the {@link views.MainWindow}
     */
    BackupTableModel(BackupManager manager) {
        mBackupManager = manager;
        update();
        addTableModelListener(this);
    }

    /**
     * This method is used to add new items into the table.
     *
     * @param selected defines whether the item is selected
     * @param name value of the Name column
     * @param original value of the Original Directory column
     * @param backup value of the backup Directory column
     * @param shallow defines whether the backup is defined as shallow
     * @param date value of the Last Synchronized column
     */
    public void addRow(boolean selected, String name, String original, String backup, boolean shallow, Date date) {
        Vector<Object> row = new Vector<>();
        row.add(name);
        row.add(selected);
        row.add(name);
        row.add(original);
        row.add(backup);
        row.add(shallow);
        row.add(date);
        mTableData.add(row);
    }

    /**
     * Returns the associated {@link backupmanagment.BackupManager} instance.
     *
     * @return returned {@link backupmanagment.BackupManager} instance.
     */
    public BackupManager getBackupManager() {
        return mBackupManager;
    }

    /**
     * Associates a {@link backupmanagment.BackupManager} instance with this {@link views.BackupTableModel}.
     */
    public void setBackupManager(BackupManager mBackupManager) {
        this.mBackupManager = mBackupManager;
    }

    /**
     * Returns a {@link java.util.Set} of names of the selected backups.
     *
     * @return a {@link java.util.Set} of names of the selected backups
     */
    public Set<String> getSelectedBackups() {
        return mSelectedBackups;
    }

    /**
     * Marks a backup with this name as not selected.
     *
     * @param name name of the backup
     */
    public void removeFromSelectedBackups(String name) {
        mSelectedBackups.remove(name);
    }

    /**
     * Rebuilds this {@link views.BackupTableModel}.
     */
    public void update() {
        clear();
        mBackupManager.updateView(this);
    }

    /**
     * Clears this {@link views.BackupTableModel}.
     */
    public void clear() {
        mTableData = new Vector<>();
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        if (mTableData.isEmpty()) {
            return 0;
        }
        else {
            return mTableData.size();
        }
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return mTableHeader.length;
    }

    /**
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     * returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public String getColumnName(int column) {
        return mTableHeader[column];
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return mTableData.get(rowIndex).get(columnIndex);
    }

    /**
     * This empty implementation is provided so users don't have to implement
     * this method if their data model is not editable.
     *
     * @param value      value to assign to cell
     * @param rowIndex    row of cell
     * @param columnIndex column of cell
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        mTableData.get(rowIndex).set(columnIndex, value);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Returns false.  This is the default implementation for all cells.
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex < 2;
    }

    /**
     * Update the model when the table is edited.
     * <br />
     * Additionally this fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     *
     * @param e instance of {@link javax.swing.event.TableModelEvent}
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        saveSelectedBackups();
        update();
    }

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
    @Override
    public void showBackupInfo(String name, String original, String backup, boolean shallow, Date date) {
        boolean selected = false;
        if (mSelectedBackups.contains(name)) {
            selected = true;
        }
        addRow(selected, name, original, backup, shallow, date);
    }

    @Override
    public String getBackupName() {
        return null;
    }

    @Override
    public void setBackupName(String name) {
        // no action
    }

    /**
     * This method saves the names of currently selected backups.
     */
    private void saveSelectedBackups() {
        for (Enumeration<Vector> e = mTableData.elements(); e.hasMoreElements();) {
            Vector<Object> row = e.nextElement();
            if ((Boolean) row.get(1)) {
                mSelectedBackups.add((String) row.get(2));
            }
            else {
                mSelectedBackups.remove((String) row.get(2));
            }
        }
    }
}
