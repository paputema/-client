package kusoBotMakerClient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import kusoBotMaker.Access_db;
import kusoBotMaker.BotAccount;

public class kbmLogWindow {
	private static Table table;
	private TableColumn tableColumn;
	private TableColumn tableColumn_1;
	private TableColumn tableColumn_2;
	private Display display;
	private void setTable(long User_ID)
	{
		String sql = "SELECT * FROM log where User_ID = ? order by TIME DESC LIMIT 100;";

		try {
			Connection con = Access_db.Connect_db();
			java.sql.PreparedStatement pstat = con.prepareStatement(sql);
			pstat.setLong(1, User_ID);
			ResultSet reSet = pstat.executeQuery();
			while(reSet.next())
			{
				TableItem tableItem= new TableItem(table, SWT.NONE);
				tableItem.setText(0, reSet.getTimestamp("TIME").toString());
				tableItem.setText(1,reSet.getString("Code"));
				tableItem.setText(2,reSet.getString("Data"));

			}
			con.close();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}


	/*public static void main(String[] args) {
		try {
			kbmLogWindow window = new kbmLogWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open(BotAccount account) {
		display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(813, 615);
		if(account.user.getName() == null)
		{
			shell.setText(account.user.getName());
		}else
		{
			shell.setText(account.User_ID +"");
		}
		shell.setLayout(new FormLayout());

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(100, -10);
		fd_table.right = new FormAttachment(100, -13);
		fd_table.top = new FormAttachment(0, 10);
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(150);
		tableColumn.setText("日付");

		tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("コード");

		tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(shell.getSize().x - (tableColumn.getWidth() + tableColumn_1.getWidth()) );
		tableColumn_2.setText("ログ");

		setTable(account.User_ID);
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				resizeTable();
			}
		});
		shell.open();
		shell.layout();
		resizeTable();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	private void resizeTable()
	{
		checkAsyncExec(new Runnable() {
			public void run() {
				Table table = kbmLogWindow.table;
				if(table != null)
				{
					int tablesize = table.getSize().x;
					tablesize = tablesize - table.getBorderWidth() - (table.getGridLineWidth() * (table.getColumnCount() - 1));

					tablesize = tablesize - table.getColumn(0).getWidth() - table.getColumn(1).getWidth() ;

					if(table.getVerticalBar().isVisible())
					{
						tablesize = tablesize - table.getVerticalBar().getSize().x;
					}
					table.getColumn(2).setWidth(tablesize);
				}
			}
		});
	}

	private boolean checkAsyncExec(Runnable r) {
		if (!display.isDisposed()) {
			display.asyncExec(r);
			return true;
		} else {
			return false;
		}
	}
}
