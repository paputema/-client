package kusoBotMakerClient;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import kusoBotMaker.KbmUtil;
import kusoBotMaker.SongTitleData;
import kusoBotMaker.Songs;
import kusoBotMaker.songData;
import twitter4j.User;

public class kbmSongEdit {
	private Table table;
	private TableColumn tableColumnId;
	private Display display;
	private Text text;
	private long song_id;
	private ComboViewer comboViewer;
	private Combo comboAccount;
	private Spinner spinner;
	private Label lblNewLabel;
	private Label lblTextCount;
	Shell shell = new Shell(SWT.SHELL_TRIM);
	/**
	 * Open the window.
	 *
	 * @wbp.parser.entryPoint
	 */
	public void open(SongTitleData s) {
		display = Display.getDefault();


		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent paramControlEvent) {
				tableReSaize();
			}
		});
		shell.setSize(916, 750);
		shell.setText("タイトル:" + s.song_Title);
		song_id = s.song_ID;
		shell.setLayout(new FormLayout());

		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(table, 291, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(table, 18);
		fd_composite.right = new FormAttachment(table, 0, SWT.RIGHT);
		fd_composite.left = new FormAttachment(table, 0, SWT.LEFT);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);

		FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(100, -196);
		fd_table.top = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(100, -10);
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		Menu menu = new Menu(table);
		table.setMenu(menu);

		MenuItem mnEdit = new MenuItem(menu, SWT.NONE);
		mnEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {

			}
		});
		mnEdit.setText("編集");

		MenuItem mnDelete = new MenuItem(menu, SWT.NONE);
		mnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {

			}
		});
		mnDelete.setText("削除");

		Button btnNewButtonAdd = new Button(shell, SWT.NONE);
		btnNewButtonAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addSongTxt();
			}
		});

		FormData fd_btnNewButtonAdd = new FormData();
		fd_btnNewButtonAdd.left = new FormAttachment(0, 10);
		fd_btnNewButtonAdd.bottom = new FormAttachment(100, -10);

		tableColumnId = new TableColumn(table, SWT.NONE);
		tableColumnId.setWidth(20);
		// tableColumnUser.setWidth(608);
		tableColumnId.setText("＃");

		TableColumn tableColumnAccount = new TableColumn(table, SWT.NONE);
		tableColumnAccount.setWidth(100);
		tableColumnAccount.setText("アカウント");

		TableColumn tableColumnText = new TableColumn(table, SWT.NONE);
		tableColumnText.setWidth(100);
		tableColumnText.setText("歌詞");
		btnNewButtonAdd.setLayoutData(fd_btnNewButtonAdd);
		btnNewButtonAdd.setText("追加");

		Button btnNewButtonRefresh = new Button(shell, SWT.NONE);
		FormData fd_btnNewButtonRefresh = new FormData();
		fd_btnNewButtonRefresh.top = new FormAttachment(btnNewButtonAdd, 0, SWT.TOP);
		fd_btnNewButtonRefresh.left = new FormAttachment(btnNewButtonAdd, 6);
		btnNewButtonRefresh.setLayoutData(fd_btnNewButtonRefresh);
		btnNewButtonRefresh.setText("リスト更新");

		Composite composite = new Composite(shell, SWT.NONE);
		fd_composite.right = new FormAttachment(table, 0, SWT.RIGHT);
		fd_composite.bottom = new FormAttachment(btnNewButtonAdd, -6);
		fd_composite.top = new FormAttachment(table, 6);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("遅延(秒)");
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);

		lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 94, 75, 22);
		lblNewLabel.setText("遅延（秒）");

		text = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				lblTextCount.setText(text.getText().length() + "文字");
			}
		});

		lblTextCount = new Label(composite, SWT.NONE);
		lblTextCount.setBounds(159, 94, 153, 22);
		lblTextCount.setText(text.getText().length() + "文字");

		text.setBounds(10, 39, 860, 49);

		comboViewer = new ComboViewer(composite, SWT.NONE);
		comboAccount = comboViewer.getCombo();
		comboAccount.setBounds(10, 10, 272, 23);

		spinner = new Spinner(composite, SWT.BORDER);
		spinner.setBounds(93, 94, 60, 22);
		spinner.setMinimum(0);
		spinner.setSelection(10);

		RefreshTable();
		SetcomboAccount();
		shell.open();
		shell.layout();
		while (!shell.isDisposed() ) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	void addSongTxt() {
		String Item = comboAccount.getItem(comboAccount.getSelectionIndex());
		User user = (User) comboAccount.getData(Item);
		comboAccount.remove(Item);
		Songs.InsertSong(song_id, user.getId(), text.getText(), spinner.getSelection());
		comboAccount.setItem(0, Item);
		RefreshTable();
	}

	void SetcomboAccount() {
		for (User user : KbmUtil.getBotUser()) {
			if (user != null && user.getName() != null) {
				comboAccount.add(user.getName());
				// 名前被り在るかもだからあんまよくない
				comboAccount.setData(user.getName(), user);
			}

		}
	}

	void RefreshTable() {
		table.removeAll();

		try {
			List<songData> songdata = Songs.GetSong(song_id);
			for (songData s : songdata) {
				addSong(s, false);
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		tableReSaize();

	}

	void addSong(songData s, boolean Insert) {
		checkAsyncExec(new Runnable() {
			public void run() {
				if (display.isDisposed()) {
					return;
				}

				TableItem tableItem;

				if (Insert) {
					tableItem = new TableItem(table, 0, 0);
				} else {
					tableItem = new TableItem(table, 0);
				}

				tableItem.setText(0, "" + s.Song_Sequence);
				User user = KbmUtil.BotAccount(s.User_ID).user;
				if(user != null)
				{
					tableItem.setText(1, user.getScreenName());
					tableItem.setImage(1, KbmImgeUtil.getImage(user.getProfileImageURLHttps()));
					tableItem.setText(2, s.Post_str);
					tableItem.setText(3, "" + s.Delay);

					tableItem.setData(s);
				}

			}
		});
	}

	private void tableReSaize() {
		checkAsyncExec(new Runnable() {
			public void run() {
				if (display.isDisposed()) {
					return;
				}
				int tableWidth = table.getSize().x;
				if (table.getVerticalBar().isEnabled()) {
					tableWidth = tableWidth - table.getVerticalBar().getSize().x;
				}
				for (TableColumn tableColumn : table.getColumns()) {
					tableColumn.pack();
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
