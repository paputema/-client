package kusoBotMakerClient;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import kusoBotMaker.SongTitleData;
import kusoBotMaker.Songs;
import kusoBotMakerClient.kbmMainWindow.BotAccountOnWindow;

public class kbmSongList {
	BotAccountOnWindow botAccountOnWindow;
	private Table table;
	private TableColumn tableColumnTitle;
	private Text textTitle;
	private Display display;
	Shell shell = new Shell(SWT.SHELL_TRIM);
	/**
	 * Open the window.
	 *
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display. getCurrent();

		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent paramControlEvent) {
				tableReSaize();
			}
		});
		shell.setSize(648, 468);
		shell.setText("歌リスト");
		shell.setLayout(new FormLayout());

		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(table, 291, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(table, 18);
		fd_composite.right = new FormAttachment(table, 0, SWT.RIGHT);
		fd_composite.left = new FormAttachment(table, 0, SWT.LEFT);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);

		FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(100, -69);
		fd_table.top = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(100, -10);
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableColumnTitle = new TableColumn(table, SWT.NONE);
		tableColumnTitle.setWidth(600);
		//tableColumnUser.setWidth(608);
		tableColumnTitle.setText("タイトル");

		Menu menu = new Menu(table);
		table.setMenu(menu);

		MenuItem mnEdit = new MenuItem(menu, SWT.NONE);
		mnEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				openkbmTweetEditWindow();
			}
		});
		mnEdit.setText("編集");

		MenuItem mnDelete = new MenuItem(menu, SWT.NONE);
		mnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				int select = table.getSelectionIndex();
				if (select == -1) {
					return;
				}

				SongTitleData s = (SongTitleData) table.getItem(select).getData();

				Songs.DeleteSongTitle(s.song_ID);
				RefreshTable();
			}
		});
		mnDelete.setText("削除");

		Button btnNewButtonAdd = new Button(shell, SWT.NONE);
		btnNewButtonAdd.addSelectionListener(new SelectionAdapter() {
			@Override

			public void widgetSelected(SelectionEvent arg0) {
				Songs.InsertSongTitle(textTitle.getText());
				RefreshTable();
			}
		});
		FormData fd_btnNewButtonAdd = new FormData();
		btnNewButtonAdd.setLayoutData(fd_btnNewButtonAdd);
		btnNewButtonAdd.setText("新規追加");

		Button btnNewButtonRefresh = new Button(shell, SWT.NONE);
		btnNewButtonRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				RefreshTable();
			}
		});
		fd_btnNewButtonAdd.top = new FormAttachment(btnNewButtonRefresh, 0, SWT.TOP);
		fd_btnNewButtonAdd.right = new FormAttachment(btnNewButtonRefresh, -198);
		FormData fd_btnNewButtonRefresh = new FormData();
		fd_btnNewButtonRefresh.bottom = new FormAttachment(100, -10);
		fd_btnNewButtonRefresh.right = new FormAttachment(table, 0, SWT.RIGHT);
		btnNewButtonRefresh.setLayoutData(fd_btnNewButtonRefresh);
		btnNewButtonRefresh.setText("リスト更新");

		textTitle = new Text(shell, SWT.BORDER);
		FormData fd_textTitle = new FormData();
		fd_textTitle.right = new FormAttachment(btnNewButtonAdd, -6);
		fd_textTitle.top = new FormAttachment(table, 34);
		fd_textTitle.bottom = new FormAttachment(100, -10);
		fd_textTitle.left = new FormAttachment(table, 0, SWT.LEFT);
		textTitle.setLayoutData(fd_textTitle);

		RefreshTable();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void openkbmTweetEditWindow() {
		kbmSongEdit kte = new kbmSongEdit();
		//if (isupdate) {
			int select = table.getSelectionIndex();
			if (select == -1) {
				return;
			}

			SongTitleData s = (SongTitleData) table.getItem(select).getData();


		/*
		} else {
			kte.setKbmTweetEditWindow(table,botAccount.User_ID);
		}
		*/
		kte.open(s);
	}


	void RefreshTable()
	{
		table.removeAll();

		try {
			List<SongTitleData> songtitle = Songs.GetSongTitle();
			for(SongTitleData s : songtitle){
				addSongTitle(s, false);
	        }
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
	void addSongTitle(SongTitleData s , boolean Insert) {
		checkAsyncExec(new Runnable() {
			public void run() {
				if (table.isDisposed()) {
					return;
				}

				TableItem tableItem;

				if(Insert)
				{
					tableItem = new TableItem(table, 0, 0);
				}else
				{
					tableItem = new TableItem(table, 0);
				}


				//tableItem.setImage(0, KbmImgeUtil.getImage(user.getBiggerProfileImageURLHttps()));
				tableItem.setText(0, s.song_Title);
				//tableItem.setText(1, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(status.getCreatedAt()));
				tableItem.setData(s);


				//tableItem.setData(STATUS, status);
				/*
				if (status.isRetweet()) {
					Status rtstatus = status.getRetweetedStatus();
					User rtuser = rtstatus.getUser();
					KbmUtil.setUser(rtuser);
					tableItem.setImage(2, KbmImgeUtil.getImage(rtuser.getBiggerProfileImageURLHttps()));
					tableItem.setText(2, "RT:[" + rtuser.getName() + "]\n" + rtstatus.getText());

				} else {
					tableItem.setText(2, status.getText());
				}*/
			}
		});
	}



	private void tableReSaize() {
		checkAsyncExec(new Runnable() {
			public void run() {
				if (table.isDisposed()) {
					return;
				}
				// int tableWidth = shell.getSize().x - fd_table.left.offset +
				// fd_table.right.offset;
				int tableWidth = table.getSize().x;
				if (table.getVerticalBar().isEnabled()) {
					tableWidth = tableWidth - table.getVerticalBar().getSize().x;
				}
				tableColumnTitle.setWidth(tableWidth );
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
