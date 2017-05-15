package kusoBotMakerClient;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import kusoBotMaker.BotAccount;
import kusoBotMaker.PostTweet;

public class KbmTweetListWindow {
	private static String getena(boolean bool) {
		if (bool) {
			return "する";
		}
		return "しない";

	}

	public static void setTableitemPt(TableItem tableitem, PostTweet pt) {
		// checkAsyncExec(new Runnable() {
		// public void run() {
		tableitem.setData(pt);
		tableitem.setText(0, pt.getSearch_str());
		tableitem.setText(1, "" + pt.getPriority());
		tableitem.setText(2, pt.getPost_str());
		tableitem.setText(3, pt.getMode_name());
		tableitem.setText(4, getena(pt.isNormalpost()));
		tableitem.setText(5, getena(pt.isTw4Me()));
		tableitem.setText(6, getena(pt.isTw4Tl()));
		tableitem.setText(7, getena(pt.isAir()));
		tableitem.setText(8, getena(pt.isRT()));
		tableitem.setText(9, getena(pt.isFav()));
		tableitem.setText(10, getena(pt.isFollow()));
		if (pt.lastuse != null) {
			tableitem.setText(11, pt.lastuse.toString());
		}
		//
		// }
		// });
	}

	private BotAccount botAccount;
	private Display display;

	/*
	 * public static void main(String[] args) { try { KbmTweetConfWindow window
	 * = new KbmTweetConfWindow(null); window.open(); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */

	Shell shell = new Shell(SWT.SHELL_TRIM);

	private Table table;

	private Text textSearch;

	public KbmTweetListWindow() {
		// TODO 自動生成されたメソッド・スタブ
		super();
		return;
	}

	public KbmTweetListWindow(BotAccount botAccount) {
		super();
		this.botAccount = botAccount;
	}
	/**
	 * Open the window.
	 *
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				// resizeTable();
			}
		});
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
			}
		});
		shell.setSize(1500, 600);
		shell.setMinimumSize(new Point(700, 450));
		shell.setText(botAccount.user.getName());
		shell.setLayout(new FormLayout());
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				// resizeTable();
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				openkbmTweetEditWindow(true);
			}
		});
		FormData fd_table = new FormData();
		fd_table.right = new FormAttachment(100, -10);
		fd_table.top = new FormAttachment(0, 88);
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tColumn_1 = new TableColumn(table, SWT.NONE);
		tColumn_1.setWidth(154);
		tColumn_1.setText("検索条件");

		TableColumn tColumn_13 = new TableColumn(table, SWT.NONE);
		tColumn_13.setWidth(64);
		tColumn_13.setText("優先順位");

		TableColumn tColumn_2 = new TableColumn(table, SWT.NONE);
		tColumn_2.setWidth(145);
		tColumn_2.setText("ツイート内容");

		TableColumn tColumn_3 = new TableColumn(table, SWT.NONE);
		tColumn_3.setWidth(134);
		tColumn_3.setText("モード");

		TableColumn tColumn_4 = new TableColumn(table, SWT.NONE);
		tColumn_4.setWidth(85);
		tColumn_4.setText("ノーマルポスト");

		TableColumn tColumn_7 = new TableColumn(table, SWT.NONE);
		tColumn_7.setWidth(100);
		tColumn_7.setText("対メンション");

		TableColumn tColumn_8 = new TableColumn(table, SWT.NONE);
		tColumn_8.setWidth(100);
		tColumn_8.setText("対タイムライン");

		Button btn編集 = new Button(shell, SWT.NONE);
		btn編集.setText("編集");
		btn編集.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openkbmTweetEditWindow(true);
			}
		});
		fd_table.bottom = new FormAttachment(100, -41);

		TableColumn tColumn_9 = new TableColumn(table, SWT.NONE);
		tColumn_9.setWidth(100);
		tColumn_9.setText("エアリプ");

		TableColumn tColumn_10 = new TableColumn(table, SWT.NONE);
		tColumn_10.setWidth(100);
		tColumn_10.setText("リツイート");

		TableColumn tColumn_11 = new TableColumn(table, SWT.NONE);
		tColumn_11.setWidth(100);
		tColumn_11.setText("いいね");

		TableColumn tColumn_12 = new TableColumn(table, SWT.NONE);
		tColumn_12.setWidth(100);
		tColumn_12.setText("フォロー");

		FormData fd_btn編集 = new FormData();
		fd_btn編集.right = new FormAttachment(0, 85);
		fd_btn編集.top = new FormAttachment(100, -35);
		fd_btn編集.bottom = new FormAttachment(100, -10);
		fd_btn編集.left = new FormAttachment(0, 10);
		btn編集.setLayoutData(fd_btn編集);
		btn編集.setText("編集");

		Button btn削除 = new Button(shell, SWT.NONE);
		btn削除.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				if (index == -1) {
					return;
				}
				try {
					((PostTweet) table.getItem(index).getData()).deletePost();
				} catch (SQLException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
				settableitem();
				table.setSelection(index);
			}
		});
		FormData fd_btn削除 = new FormData();
		fd_btn削除.right = new FormAttachment(btn編集, 81, SWT.RIGHT);
		fd_btn削除.left = new FormAttachment(btn編集, 6);
		fd_btn削除.top = new FormAttachment(btn編集, 0, SWT.TOP);
		btn削除.setLayoutData(fd_btn削除);
		btn削除.setText("削除");

		Button btnNewButton_2 = new Button(shell, SWT.NONE);
		FormData fd_btnNewButton_2 = new FormData();
		fd_btnNewButton_2.top = new FormAttachment(table, 6);
		fd_btnNewButton_2.right = new FormAttachment(btn削除, 81, SWT.RIGHT);
		fd_btnNewButton_2.left = new FormAttachment(btn削除, 6);
		btnNewButton_2.setLayoutData(fd_btnNewButton_2);
		btnNewButton_2.setText("新規");
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openkbmTweetEditWindow(false);
			}
		});

		Label lblNewLabel = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.bottom = new FormAttachment(table, -6);

		TableColumn tableColumnLastUse = new TableColumn(table, SWT.NONE);
		tableColumnLastUse.setWidth(100);
		tableColumnLastUse.setText("最終使用時間");
		fd_lblNewLabel.top = new FormAttachment(0, 6);
		fd_lblNewLabel.right = new FormAttachment(btn編集, 0, SWT.RIGHT);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setImage(KbmImgeUtil.getImage(botAccount.user.getBiggerProfileImageURLHttps()));

		textSearch = new Text(shell, SWT.BORDER);
		FormData fd_textSearch = new FormData();
		fd_textSearch.right = new FormAttachment(lblNewLabel, 543, SWT.RIGHT);
		fd_textSearch.bottom = new FormAttachment(table, -6);
		fd_textSearch.left = new FormAttachment(lblNewLabel, 87);
		textSearch.setLayoutData(fd_textSearch);

		Button buttonSearch = new Button(shell, SWT.NONE);
		buttonSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				settableitemSearch();
			}
		});
		FormData fd_buttonSearch = new FormData();
		fd_buttonSearch.top = new FormAttachment(textSearch, -2, SWT.TOP);
		fd_buttonSearch.right = new FormAttachment(textSearch, 99, SWT.RIGHT);
		fd_buttonSearch.left = new FormAttachment(textSearch, 6);
		buttonSearch.setLayoutData(fd_buttonSearch);
		buttonSearch.setText("絞込");

		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				settableitem();
			}
		});
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(btn編集, -25);
		fd_button.bottom = new FormAttachment(btn編集, 0, SWT.BOTTOM);
		fd_button.right = new FormAttachment(btnNewButton_2, 81, SWT.RIGHT);
		fd_button.left = new FormAttachment(btnNewButton_2, 6);
		button.setLayoutData(fd_button);
		button.setText("再読込");

		settableitem();
		shell.open();
		shell.layout();
		resizeTable();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	private void openkbmTweetEditWindow(Boolean isupdate) {
		kbmTweetEditWindow kte = new kbmTweetEditWindow();
		if (isupdate) {
			int select = table.getSelectionIndex();
			if (select == -1) {
				return;
			}
			kte.setKbmTweetEditWindow(table, table.getItem(select));
		} else {
			kte.setKbmTweetEditWindow(table, botAccount.User_ID);
		}
		kte.open();
	}

	private void resizeTable() {

		if (table != null) {
			for (TableColumn tableColumn : table.getColumns()) {
				tableColumn.pack();
			}
		}

	}

	private void settableitem() {
		if (table.isDisposed() || shell.isDisposed()) {
			return;
		}
		try {

			table.removeAll();
			table.clearAll();

			List<PostTweet> listPostTweet = PostTweet.GetPost(botAccount.User_ID);
			for (int i = 0; i < listPostTweet.size(); i++) {
				PostTweet pt = listPostTweet.get(i);
				TableItem tableitem = new TableItem(table, SWT.MULTI);
				setTableitemPt(tableitem, pt);

			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		resizeTable();
	}

	private void settableitemSearch() {
		if (table.isDisposed() || shell.isDisposed()) {
			return;
		}
		try {

			table.removeAll();
			table.clearAll();

			List<PostTweet> listPostTweet = PostTweet.GetPost(botAccount.User_ID, textSearch.getText());
			for (int i = 0; i < listPostTweet.size(); i++) {
				PostTweet pt = listPostTweet.get(i);
				TableItem tableitem = new TableItem(table, SWT.MULTI);
				setTableitemPt(tableitem, pt);

			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		resizeTable();
	}

}
