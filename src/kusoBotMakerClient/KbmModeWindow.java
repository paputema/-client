package kusoBotMakerClient;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import kusoBotMaker.AccountMode;
import kusoBotMaker.BotAccount;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class KbmModeWindow {
	private Text textBotName;
	private Text textLocation;
	private Text texDdescription;
	private Text textModeName;
		private Long userID;
	private User user;

	public void settable() {
		table.removeAll();
		java.util.List<AccountMode> acmode = AccountMode.getSetAccountModes(userID);
		for (int i = 0; i < acmode.size(); i++) {
			acmode.get(i).getIconDb();
			TableItem item = new TableItem(table, SWT.LEFT);
			item.setData(acmode.get(i));
			item.setImage(0, KbmImgeUtil.getImageResize64(acmode.get(i).profile_iconp_path, display));
			item.setText(0, acmode.get(i).mode_type);
			item.setText(1, acmode.get(i).mode_name);
			item.setText(2, acmode.get(i).user_name);
			item.setText(3, acmode.get(i).user_description);
			item.setText(4, acmode.get(i).user_location);
			// item.setText(4,acmode.get(i).user_url);
		}
		checkEnable() ;
	}

	public void setEdits() {
		int i = table.getSelectionIndex();
		if (i == -1) {
			return;
		}
		AccountMode acmode = (AccountMode) table.getItem(i).getData();
		// acmode.id
		textModeName.setText(acmode.mode_name);
		comboModeType.setText(acmode.mode_type);
		labelIconPath.setText(acmode.profile_iconp_path);
		labelIconPath.setImage(KbmImgeUtil.getImageResize64(acmode.profile_iconp_path,display));
		texDdescription.setText(acmode.user_description);
		// acmode.User_ID
		textLocation.setText(acmode.user_location);
		textBotName.setText(acmode.user_name);
		textURL.setText(acmode.user_url);
	}

	public void setEditsFromTwitter() {
		table.setSelection(-1);
		checkEnable();


		// acmode.id
		textModeName.setText("通常");
		comboModeType.setText("通常");


		labelIconPath.setText(KbmImgeUtil.downloadIcon (user.getOriginalProfileImageURLHttps() ,user.getScreenName()));
		labelIconPath.setImage(KbmImgeUtil.getImageResize64(labelIconPath.getText(),display));

		set_notnull(texDdescription,user.getDescription());
		set_notnull(textLocation,user.getLocation());
		set_notnull(textBotName,user.getName());
		set_notnull(textURL,user.getURL());
	}

	public void set_notnull (Text obj, String str) {
		if(str != null)
		{
			obj.setText(str);
		}
	}









	/**
	 * Open the window.
	 *
	 * @wbp.parser.entryPoint
	 */
	static Display display = Display.getDefault();
	private Table table;
	private Combo comboModeType;
	private Label labelIconPath;
	private Text textURL;
	private Button btnDelete;
	private Button btnUpdate;

	private void resizeTable()
	{
		checkAsyncExec(new Runnable() {
			public void run() {
				Table is_table = table;
				if(is_table != null)
				{
					int tablesize = is_table.getSize().x;
					tablesize = tablesize - is_table.getBorderWidth() - (is_table.getGridLineWidth() * (is_table.getColumnCount() - 1));

					tablesize = tablesize - is_table.getColumn(0).getWidth() - is_table.getColumn(1).getWidth() - is_table.getColumn(2).getWidth()  - is_table.getColumn(4).getWidth();

					if(is_table.getVerticalBar().isVisible())
					{
						tablesize = tablesize - is_table.getVerticalBar().getSize().x;
					}
					is_table.getColumn(3).setWidth(tablesize);
				}
			}
		});
	}
	/**
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell parentshell, BotAccount botAccount, Long userID) {
		Twitter twitter = botAccount.twitter;
		this.userID = userID;
		try {
			this.user = twitter.verifyCredentials();
		} catch (TwitterException e2) {
			// TODO 自動生成された catch ブロック
			e2.printStackTrace();
		}
		Shell shell = new Shell(parentshell, SWT.SHELL_TRIM | SWT.PRIMARY_MODAL);
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				resizeTable();
			}
		});
		shell.setSize(817, 662);
		shell.setText("モード設定");
		shell.setLayout(new FormLayout());

		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new FormLayout());
		FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(100, -10);
		fd_group.right = new FormAttachment(100, -10);
		fd_group.left = new FormAttachment(0, 10);
		group.setLayoutData(fd_group);
		group.setText("追加・編集");

		textBotName = new Text(group, SWT.BORDER);
		FormData fd_textBotName = new FormData();
		fd_textBotName.right = new FormAttachment(0, 499);
		fd_textBotName.top = new FormAttachment(0);
		fd_textBotName.left = new FormAttachment(0, 341);
		textBotName.setLayoutData(fd_textBotName);

		Label lblNewLabel = new Label(group, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(textBotName, 3, SWT.TOP);
		fd_lblNewLabel.right = new FormAttachment(textBotName, -6);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("BOT名称");

		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("現在地");

		textLocation = new Text(group, SWT.BORDER);
		fd_lblNewLabel_1.top = new FormAttachment(textLocation, 3, SWT.TOP);
		FormData fd_textLocation = new FormData();
		fd_textLocation.right = new FormAttachment(0, 481);
		fd_textLocation.top = new FormAttachment(0, 160);
		fd_textLocation.left = new FormAttachment(0, 341);
		textLocation.setLayoutData(fd_textLocation);

		texDdescription = new Text(group, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_texDdescription = new FormData();
		fd_texDdescription.bottom = new FormAttachment(0, 157);
		fd_texDdescription.left = new FormAttachment(textBotName, 0, SWT.LEFT);
		fd_texDdescription.right = new FormAttachment(0, 584);
		fd_texDdescription.top = new FormAttachment(0, 109);
		texDdescription.setLayoutData(fd_texDdescription);

		Label label = new Label(group, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		FormData fd_label = new FormData();
		fd_label.left = new FormAttachment(0, 286);
		fd_label.right = new FormAttachment(texDdescription, -6);
		label.setLayoutData(fd_label);
		label.setText("自己紹介");

		comboModeType = new Combo(group, SWT.BORDER);
		FormData fd_comboModeType = new FormData();
		comboModeType.setLayoutData(fd_comboModeType);
		comboModeType.setTouchEnabled(true);
		comboModeType.setListVisible(true);


		comboModeType.setItems(new String[] { "通常", "停止" });
		List<String> modetypelist = AccountMode.getSetModeType(userID);
		for (String i : modetypelist) {
			if(!i.equals("通常") && !i.equals("停止") )
			{
				comboModeType.add(i);
			}
		}
		comboModeType.setToolTipText("通常：BOTが動いている時\n停止：規制による一時停止やアプリを終了した時に切り替えられるモード\n日付「MMDD」の形式で入力：その日付限定で切り替えられるモード（4月12日になら[0412]、[__15]で毎月15日なども可）");

		/*List<AccountMode> amlist = AccountMode.getSetAccountModes(userID);
		int size = amlist.size();
		if(size > 0)
		{
			for (int i = 0; i < size; i++) {
				comboModeType.add(amlist.get(i).mode_type);
			}
		}*/


		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		fd_comboModeType.left = new FormAttachment(lblNewLabel_2, 6);
		fd_comboModeType.top = new FormAttachment(lblNewLabel_2, -3, SWT.TOP);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.top = new FormAttachment(0, 32);
		fd_lblNewLabel_2.left = new FormAttachment(0, 7);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("モード種別");

		Label lblNewLabel_3 = new Label(group, SWT.NONE);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.right = new FormAttachment(0, 62);
		fd_lblNewLabel_3.top = new FormAttachment(0, 3);
		fd_lblNewLabel_3.left = new FormAttachment(0, 7);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("モード名称");

		textModeName = new Text(group, SWT.BORDER);
		FormData fd_textModeName = new FormData();
		fd_textModeName.top = new FormAttachment(textBotName, 0, SWT.TOP);
		fd_textModeName.left = new FormAttachment(comboModeType, 0, SWT.LEFT);
		fd_textModeName.right = new FormAttachment(0, 141);
		textModeName.setLayoutData(fd_textModeName);


		Label label_1 = new Label(group, SWT.NONE);
		fd_label.top = new FormAttachment(label_1, 73);
		fd_comboModeType.right = new FormAttachment(label_1, -129);
		label_1.setAlignment(SWT.RIGHT);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(lblNewLabel, 6);
		fd_label_1.left = new FormAttachment(lblNewLabel_2, 226);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("アイコン");

		Composite compositeEditIcon = new Composite(group, SWT.NONE);
		fd_label_1.right = new FormAttachment(compositeEditIcon, -6);
		FormData fd_compositeEditIcon = new FormData();
		fd_compositeEditIcon.bottom = new FormAttachment(texDdescription, -18);
		fd_compositeEditIcon.top = new FormAttachment(textBotName, 6);
		fd_compositeEditIcon.left = new FormAttachment(0, 341);
		compositeEditIcon.setLayoutData(fd_compositeEditIcon);
		compositeEditIcon.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Composite composite_2 = new Composite(group, SWT.NONE);
		FormData fd_composite_2 = new FormData();
		fd_composite_2.bottom = new FormAttachment(100, -3);
		fd_composite_2.right = new FormAttachment(100, -3);
		fd_composite_2.top = new FormAttachment(0, 15);
		composite_2.setLayoutData(fd_composite_2);
		GridLayout gl_composite_2 = new GridLayout(1, false);
		gl_composite_2.marginRight = 1;
		gl_composite_2.marginLeft = 1;
		gl_composite_2.marginTop = 5;
		gl_composite_2.verticalSpacing = 10;
		gl_composite_2.marginWidth = 3;
		gl_composite_2.marginHeight = 3;
		composite_2.setLayout(gl_composite_2);

		Button btnAddMode = new Button(composite_2, SWT.NONE);
		btnAddMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					AccountMode am = new  AccountMode(twitter.getId(), textModeName.getText(), comboModeType.getText(), textBotName.getText(), textURL.getText(), textLocation.getText(), texDdescription.getText(), labelIconPath.getText());
					if (am.insertDb() == 0 || am.updateIconDb () == 0)
					{
						KbmImgeUtil.openMessageBox("モードの追加に失敗しました","モードの追加",shell);
					}else{
						KbmImgeUtil.openMessageBox("モードの追加に成功しました","モードの追加",shell);
					}
				} catch (IllegalStateException | TwitterException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
					KbmImgeUtil.openMessageBox("モードの追加に失敗しました","モードの追加",shell);
					return;
				} catch (SQLException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
					KbmImgeUtil.openMessageBox("モードの追加に失敗しました","モードの追加",shell);
					return;
				}
				settable();
			}
		});
		GridData gd_btnAddMode = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnAddMode.heightHint = 45;
		btnAddMode.setLayoutData(gd_btnAddMode);

		btnAddMode.setText("追加");

		btnUpdate = new Button(composite_2, SWT.NONE);
		btnUpdate.setEnabled(false);
		btnUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {

					long id = ((AccountMode)table.getItem(table.getSelectionIndex()).getData()).id;
					if(id == -1)
					{
						return;
					}
					AccountMode am = new  AccountMode(id ,twitter.getId(), textModeName.getText(), comboModeType.getText(), textBotName.getText(), textURL.getText(), textLocation.getText(), texDdescription.getText(), labelIconPath.getText());
					if(am.updateDb() == 0 || am.updateIconDb () == 0)
					{
						KbmImgeUtil.openMessageBox("モードの更新に失敗しました","モードの追加",shell);
					}else{
						KbmImgeUtil.openMessageBox("モードの更新に成功しました","モードの追加",shell);
				}
					settable();
				} catch (IllegalStateException | SQLException | TwitterException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
					KbmImgeUtil.openMessageBox("モードの更新に失敗しました","モードの追加",shell);
					return;
				};
			}
		});
		GridData gd_btnUpdate = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnUpdate.heightHint = 45;
		btnUpdate.setLayoutData(gd_btnUpdate);
		btnUpdate.setText("上書き");

		btnDelete = new Button(composite_2, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long id = ((AccountMode)table.getItem(table.getSelectionIndex()).getData()).id;
				if(id == -1)
				{
					return;
				}
				try {
					AccountMode.deleteMode(id);
				} catch (SQLException | TwitterException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
					KbmImgeUtil.openMessageBox("モードの削除に失敗しました", "削除失敗", shell);
				}
				settable();

			}
		});
		btnDelete.setEnabled(false);
		GridData gd_btnDelete = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnDelete.heightHint = 45;
		gd_btnDelete.widthHint = 80;
		btnDelete.setLayoutData(gd_btnDelete);
		btnDelete.setText("削除");
		new Label(composite_2, SWT.NONE);

		Button btnNewButton_2 = new Button(group, SWT.NONE);
		fd_compositeEditIcon.right = new FormAttachment(btnNewButton_2, -6);
		FormData fd_btnNewButton_2 = new FormData();
		fd_btnNewButton_2.top = new FormAttachment(textBotName, 9);
		fd_btnNewButton_2.right = new FormAttachment(textLocation, 0, SWT.RIGHT);
		btnNewButton_2.setLayoutData(fd_btnNewButton_2);

		labelIconPath = new Label(compositeEditIcon, SWT.NONE);
		labelIconPath.setImage(null);
		labelIconPath.setBounds(0, 0, 64, 64);
		labelIconPath.setText("アイコン");
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog openDialog = new FileDialog(shell, SWT.OPEN);
				String openFile = openDialog.open();

				if(openFile == null)
				{
					return;
				}
				Image icon = KbmImgeUtil.getImage(openFile);
				icon = new Image(Display.getDefault(),
				icon.getImageData().scaledTo(labelIconPath.getSize().x, labelIconPath.getSize().y));
				labelIconPath.setData(openFile);
				labelIconPath.setText(openFile);
				labelIconPath.setImage(icon);
			}
		});
		btnNewButton_2.setText("アイコン選択");

		Composite compositeOldIcon = new Composite(shell, SWT.NONE);
		FormData fd_compositeOldIcon = new FormData();
		fd_compositeOldIcon.top = new FormAttachment(0, 10);
		fd_compositeOldIcon.left = new FormAttachment(0, 10);
		compositeOldIcon.setLayoutData(fd_compositeOldIcon);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean bool = (table.getSelectionIndex() != -1);
				{
					btnDelete.setEnabled(bool);
					btnUpdate.setEnabled(bool);
				}
				setEdits();
			}
		});
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(compositeOldIcon, 6);
		fd_table.bottom = new FormAttachment(group, -6);
		fd_table.left = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(100, -10);

		textURL = new Text(group, SWT.BORDER);
		FormData fd_textURL = new FormData();
		fd_textURL.right = new FormAttachment(0, 481);
		fd_textURL.top = new FormAttachment(0, 187);
		fd_textURL.left = new FormAttachment(0, 341);
		textURL.setLayoutData(fd_textURL);

		Label lblUrl = new Label(group, SWT.NONE);
		lblUrl.setAlignment(SWT.RIGHT);
		FormData fd_lblUrl = new FormData();
		fd_lblUrl.right = new FormAttachment(textURL, -6);
		fd_lblUrl.bottom = new FormAttachment(composite_2, 0, SWT.BOTTOM);
		lblUrl.setLayoutData(fd_lblUrl);
		lblUrl.setText("URL");
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("モード種別");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("モード名称");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(130);
		tblclmnNewColumn_2.setText("BOT名称");

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setMoveable(true);
		tblclmnNewColumn.setWidth(330);
		tblclmnNewColumn.setText("自己紹介");

		TableColumn tblclmnBot = new TableColumn(table, SWT.NONE);
		tblclmnBot.setWidth(100);
		tblclmnBot.setText("現在地");

		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setEditsFromTwitter();
			}
		});
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(0, 10);
		fd_button.right = new FormAttachment(100, -10);
		button.setLayoutData(fd_button);
		button.setText("現在の設定を取り込む");

		Button button_1 = new Button(shell, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				if (index == -1)
				{
					KbmImgeUtil.openMessageBox("モードを選択してください", "モードが選択されていません", shell);
					return;
				}
			((AccountMode)table.getItem(index).getData()).updateTwitterProfile(botAccount);
			((AccountMode)table.getItem(index).getData()).updateAccountmode(twitter);
			}
		});
		FormData fd_button_1 = new FormData();
		fd_button_1.right = new FormAttachment(button, 0, SWT.RIGHT);
		fd_button_1.top = new FormAttachment(button, 6);
		fd_button_1.left = new FormAttachment(button, 0, SWT.LEFT);
		button_1.setLayoutData(fd_button_1);
		button_1.setText("モード変更");

		settable();

		shell.open();
		shell.layout();
		resizeTable();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void checkEnable() {
		boolean bool = (table.getSelectionIndex() != -1);
		{
			btnDelete.setEnabled(bool);
			btnUpdate.setEnabled(bool);
		}
	}


	private static boolean checkAsyncExec(Runnable r) {
		if (!display.isDisposed()) {
			display.asyncExec(r);
			return true;
		} else {
			return false;
		}
	}

}