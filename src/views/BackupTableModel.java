package views;

import backupmanagment.BackupManager;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.Vector;

/**
 * <br />
 * Created by Martin Sicho on 30.5.2014.
 */
class BackupTableModel extends AbstractTableModel implements TableModelListener, BackupViewer {

    private String[] mTableHeader = {
            "Selected"
            , "Synchronize"
            , "Name"
            , "Original Directory"
            , "Backup Directory"
            , "Shallow"
            , "Last Synchronized"
    };
    private Vector<Vector> mTableData = new Vector<>();
    private BackupManager mBackupManager;

    BackupTableModel(BackupManager manager) {
        mBackupManager = manager;
        update();
        addTableModelListener(this);
    }

    public void addRow(String name, String original, String backup, boolean shallow, Date date) {
        Vector<Object> row = new Vector<>();
        row.add(false);
        row.add(name);
        row.add(name);
        row.add(original);
        row.add(backup);
        row.add(shallow);
        row.add(date);
        mTableData.add(row);
    }

    public BackupManager getBackupManager() {
        return mBackupManager;
    }

    public void setBackupManager(BackupManager mBackupManager) {
        this.mBackupManager = mBackupManager;
    }

    public void update() {
        clear();
        mBackupManager.updateView(this);
    }

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
        update();
    }

    @Override
    public void showBackupInfo(String name, String original, String backup, boolean shallow, Date date) {
        addRow(name, original, backup, shallow, date);
    }

    @Override
    public String getBackupName() {
        return null;
    }

    @Override
    public void setBackupName(String name) {
        // no action
    }
}
