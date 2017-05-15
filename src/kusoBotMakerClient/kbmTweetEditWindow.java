package kusoBotMakerClient;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import kusoBotMaker.AccountMode;
import kusoBotMaker.PostTweet;
import kusoBotMaker.SongTitleData;
import kusoBotMaker.Songs;

public class kbmTweetEditWindow {
	boolean isNew;
	TableItem tableItem;
	Table table;

	public void setKbmTweetEditWindow(Table table,TableItem tableItem) {
		this.table =table;
		this.tableItem = tableItem;
		this.postTweet = (PostTweet) tableItem.getData();
		isNew = false;
		//setControl();
	}


	public void setKbmTweetEditWindow(Table table,long userID) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.table =table;
		this.postTweet = new PostTweet(userID);
		this.postTweet.User_ID = userID;
		isNew = true;
		//setControl();
	}

	private void setControl()
	{
		txtBoxSearchStr.setText(postTweet.getSearch_str());;
		txtBoxPost.setText(postTweet.getPost_str());
		combo.setText(postTweet.getMode_name());
		cbtNormalPost.setSelection(postTweet.isNormalpost());
		cbTtw4me.setSelection(postTweet.isTw4Me());
		cbtTw4tl.setSelection(postTweet.isTw4Tl());
		cbtTw4Other.setSelection(postTweet.isTw4Other());
		cbtAir.setSelection(postTweet.isAir());
		cbtRt.setSelection(postTweet.isRT());
		cbtFav.setSelection(postTweet.isFav());
		cbtFollow.setSelection(postTweet.isFollow());
		textLimit.setText(String.valueOf(postTweet.getLoopLimit()));
		textPriority.setText(String.valueOf(postTweet.getPriority()));
		textDelay.setText(String.valueOf(postTweet.getDelay()));
		cbtSong.setSelection(postTweet.isSong());
		if(postTweet.isSong())
		{
			comboSongTitle.setText(Songs.GetSongTitle(postTweet.getSong_ID()));
		}



	}

	private void getControl()
	{
		postTweet.setSearch_str(txtBoxSearchStr.getText());
		postTweet.setPost_str(txtBoxPost.getText());
		if (combo.getSelectionIndex() != -1) {
			postTweet.setMode_name(combo.getItem(combo.getSelectionIndex()));
		} else {
			postTweet.setMode_name(combo.getText());
		}
		postTweet.setTw4Me(cbTtw4me.getSelection());
		postTweet.setTw4Tl(cbtTw4tl.getSelection());
		postTweet.setTw4Other(cbtTw4Other.getSelection());
		postTweet.setAir(cbtAir.getSelection());
		postTweet.setRT(cbtRt.getSelection());
		postTweet.setFav(cbtFav.getSelection());
		postTweet.setFollow(cbtFollow.getSelection());
		postTweet.setNormalpost(cbtNormalPost.getSelection());

		postTweet.setLoopLimit(Long.valueOf(textLimit.getText()));

		postTweet.setPriority(Long.valueOf(textPriority.getText()));
		postTweet.setDelay(Long.valueOf(textDelay.getText()));

		postTweet.setSong(cbtSong.getSelection());
		if(postTweet.isSong())
		{
			String i_Title = comboSongTitle.getText();

			Object object = comboSongTitle.getData(i_Title);
			long i_songIS = (object != null) ? (long) object : -1;
			postTweet.setSong_ID(i_songIS);
		}else
		{
			postTweet.setSong_ID(-1);
		}

	}


	private PostTweet postTweet;
	private Text txtBoxSearchStr;
	private Text txtBoxPost;
	private Label lblNewLabel_1;
	private Combo combo;
	private Button cbtNormalPost;
	private Button cbTtw4me;
	private Button cbtTw4tl;
	private Button cbtAir;
	private Button cbtRt;
	private Button cbtFav;
	private Button cbtFollow;
	private Text textPriority;
	private Text textLimit;
	private Label lblNewLabel_4;
	private Text textDelay;
	private Text text;
	private Text text_1;
	private Button cbtTw4Other;
	private Combo comboSongTitle;
	private Button cbtSong;
	private Text text置換タグ;





	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		//Shell shell = new Shell(parentshell,SWT.SHELL_TRIM|SWT.SYSTEM_MODAL);
		Shell shell = new Shell(SWT.SHELL_TRIM);
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
			}
		});
		shell.setMinimumSize(new Point(700, 450));
		shell.setSize(1086, 656);
		shell.setText("ツイート設定");
		shell.setLayout(new FormLayout());

		Label lblNewLabel = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 5);
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("検索条件");

		txtBoxSearchStr = new Text(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		txtBoxSearchStr.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Matching ();
			}
		});
		FormData fd_txtBoxSearchStr = new FormData();
		fd_txtBoxSearchStr.top = new FormAttachment(lblNewLabel, 6);
		fd_txtBoxSearchStr.left = new FormAttachment(0, 5);
		txtBoxSearchStr.setLayoutData(fd_txtBoxSearchStr);

		lblNewLabel_1 = new Label(shell, SWT.NONE);
		fd_txtBoxSearchStr.bottom = new FormAttachment(100, -470);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(txtBoxSearchStr, 6);
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("ツイート内容");

		txtBoxPost = new Text(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		FormData fd_txtBoxPost = new FormData();
		fd_txtBoxPost.top = new FormAttachment(lblNewLabel_1, 6);
		fd_txtBoxPost.right = new FormAttachment(txtBoxSearchStr, 0, SWT.RIGHT);
		fd_txtBoxPost.left = new FormAttachment(0, 5);
		txtBoxPost.setLayoutData(fd_txtBoxPost);

		Composite composite = new Composite(shell, SWT.NONE);
		fd_txtBoxPost.bottom = new FormAttachment(100, -389);
		composite.setLayout(new FormLayout());
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(txtBoxPost, 6);
		fd_composite.right = new FormAttachment(txtBoxSearchStr, 0, SWT.RIGHT);
		fd_composite.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_composite.bottom = new FormAttachment(100, -10);
		composite.setLayoutData(fd_composite);

		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.top = new FormAttachment(0, 10);
		fd_lblNewLabel_2.left = new FormAttachment(0, 10);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("モード");

		combo = new Combo(composite, SWT.BORDER);
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(lblNewLabel_2, 134, SWT.RIGHT);
		fd_combo.top = new FormAttachment(lblNewLabel_2, 0, SWT.TOP);
		fd_combo.left = new FormAttachment(lblNewLabel_2, 3);
		combo.setLayoutData(fd_combo);

		List<AccountMode> amlist = AccountMode.getSetAccountModes(this.postTweet.User_ID);
		int size = amlist.size();
		if(size > 0)
		{
			for (int i = 0; i < size; i++) {
				combo.add(amlist.get(i).mode_name);
			}
		}


		cbtNormalPost = new Button(composite, SWT.CHECK);
		cbtNormalPost.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData fd_cbtNormalPost = new FormData();
		fd_cbtNormalPost.top = new FormAttachment(combo, 6);
		fd_cbtNormalPost.left = new FormAttachment(0, 10);
		cbtNormalPost.setLayoutData(fd_cbtNormalPost);
		cbtNormalPost.setText("ノーマルポスト");

		cbTtw4me = new Button(composite, SWT.CHECK);
		cbTtw4me.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		FormData fd_cbTtw4me = new FormData();
		fd_cbTtw4me.top = new FormAttachment(cbtNormalPost, 6);
		fd_cbTtw4me.left = new FormAttachment(lblNewLabel_2, 0, SWT.LEFT);
		cbTtw4me.setLayoutData(fd_cbTtw4me);
		cbTtw4me.setText("対メンション：自分宛てのツイートに対してリプライを反す");

		cbtTw4tl = new Button(composite, SWT.CHECK);
		FormData fd_cbtTw4tl = new FormData();
		fd_cbtTw4tl.top = new FormAttachment(cbTtw4me, 6);
		fd_cbtTw4tl.left = new FormAttachment(0, 10);
		cbtTw4tl.setLayoutData(fd_cbtTw4tl);
		cbtTw4tl.setText("対TL：TL上に流れているツイートへリプライを反す");

		cbtAir = new Button(composite, SWT.CHECK);
		FormData fd_cbtAir = new FormData();
		fd_cbtAir.right = new FormAttachment(100, -259);
		cbtAir.setLayoutData(fd_cbtAir);
		cbtAir.setText("エアリプ：リプライ時に＠をつけない");

		cbtRt = new Button(composite, SWT.CHECK);
		fd_cbtAir.bottom = new FormAttachment(cbtRt, -6);
		FormData fd_cbtRt = new FormData();
		fd_cbtRt.left = new FormAttachment(0, 10);
		cbtRt.setLayoutData(fd_cbtRt);
		cbtRt.setText("リツイート");

		cbtFav = new Button(composite, SWT.CHECK);
		fd_cbtRt.bottom = new FormAttachment(cbtFav, -6);
		FormData fd_cbtFav = new FormData();
		fd_cbtFav.left = new FormAttachment(lblNewLabel_2, 0, SWT.LEFT);
		cbtFav.setLayoutData(fd_cbtFav);
		cbtFav.setText("いいね：お気に入りにする");

		cbtFollow = new Button(composite, SWT.CHECK);
		fd_cbtFav.bottom = new FormAttachment(cbtFollow, -6);
		FormData fd_cbtTw4Other = new FormData();
		fd_cbtTw4Other.left = new FormAttachment(lblNewLabel_2, 0, SWT.LEFT);
		cbtFollow.setLayoutData(fd_cbtTw4Other);
		cbtFollow.setText("フォロー");

		Button btnSave = new Button(composite, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getControl();
				} catch (NumberFormatException e2) {
					// TODO 自動生成された catch ブロック
					e2.printStackTrace();
					MessageBox mbox = new MessageBox(shell);
					mbox.setText("入力値が不正です");
					mbox.setMessage("入力値が不正です");
					mbox.open();
					return;
				}
				{
					try {
						if (isNew)
						{
							postTweet.insertDb();
							TableItem tableItem = new TableItem(table, SWT.MULTI);
							KbmTweetListWindow.setTableitemPt(tableItem ,postTweet);
						}
						else
						{
							postTweet.updateDb();
							KbmTweetListWindow.setTableitemPt(tableItem ,postTweet);
						}
					} catch (SQLException e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
					}
				}
			}
		});
		FormData fd_btnSave = new FormData();
		fd_btnSave.left = new FormAttachment(0, 247);
		btnSave.setLayoutData(fd_btnSave);
		if(isNew)
		{
			btnSave.setText("新規保存");
		}else{
			btnSave.setText("上書き");
		}


		Button btnCancel = new Button(composite, SWT.NONE);
		fd_btnSave.top = new FormAttachment(btnCancel, 0, SWT.TOP);
		fd_btnSave.right = new FormAttachment(btnCancel, -6);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		fd_btnCancel.left = new FormAttachment(0, 347);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("キャンセル");

		textPriority = new Text(composite, SWT.BORDER);
		FormData fd_textPriority = new FormData();
		fd_textPriority.right = new FormAttachment(100, -259);
		textPriority.setLayoutData(fd_textPriority);

		Label label = new Label(composite, SWT.NONE);
		fd_textPriority.top = new FormAttachment(label, -3, SWT.TOP);
		fd_cbtTw4Other.bottom = new FormAttachment(label, -6);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 232);
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		label.setText("優先度");

		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.right = new FormAttachment(cbtNormalPost, 0, SWT.RIGHT);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("リプライ回数上限");

		textLimit = new Text(composite, SWT.BORDER);
		fd_textPriority.left = new FormAttachment(textLimit, 0, SWT.LEFT);
		fd_lblNewLabel_3.top = new FormAttachment(textLimit, 3, SWT.TOP);
		FormData fd_textLimit = new FormData();
		fd_textLimit.bottom = new FormAttachment(100, -96);
		fd_textLimit.right = new FormAttachment(100, -259);
		fd_textLimit.left = new FormAttachment(0, 122);
		textLimit.setLayoutData(fd_textLimit);

		lblNewLabel_4 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.bottom = new FormAttachment(100, -72);
		fd_lblNewLabel_4.left = new FormAttachment(lblNewLabel_2, 0, SWT.LEFT);
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		lblNewLabel_4.setText("リプライ遅延（秒）");

		textDelay = new Text(composite, SWT.BORDER);
		FormData fd_textDelay = new FormData();
		fd_textDelay.top = new FormAttachment(lblNewLabel_4, -3, SWT.TOP);
		fd_textDelay.left = new FormAttachment(lblNewLabel_4, 18);
		fd_textDelay.right = new FormAttachment(cbtAir, 0, SWT.RIGHT);
		textDelay.setLayoutData(fd_textDelay);

		cbtTw4Other = new Button(composite, SWT.CHECK);
		cbtTw4Other.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		FormData fd_cbtTw4Other1 = new FormData();
		fd_cbtTw4Other1.top = new FormAttachment(cbtTw4tl, 6);
		fd_cbtTw4Other1.left = new FormAttachment(lblNewLabel_2, 0, SWT.LEFT);
		cbtTw4Other.setLayoutData(fd_cbtTw4Other1);
		cbtTw4Other.setText("対他のユーザーへのリプライ");

		cbtSong = new Button(composite, SWT.CHECK);
		FormData fd_cbtSong = new FormData();
		fd_cbtSong.left = new FormAttachment(lblNewLabel_2, 0, SWT.LEFT);
		cbtSong.setLayoutData(fd_cbtSong);
		cbtSong.setText("歌/掛け合い");

		comboSongTitle = new Combo(composite, SWT.READ_ONLY);
		fd_cbtSong.top = new FormAttachment(comboSongTitle, 2, SWT.TOP);
		FormData fd_comboSongTitle = new FormData();
		fd_comboSongTitle.top = new FormAttachment(textDelay, 6);
		fd_comboSongTitle.left = new FormAttachment(textPriority, 0, SWT.LEFT);
		comboSongTitle.setLayoutData(fd_comboSongTitle);
		comboSongTitle.select(1);
		comboSongTitle.setText("");

		Group group = new Group(shell, SWT.NONE);
		fd_txtBoxSearchStr.right = new FormAttachment(group, -6);
		group.setText("正規表現確認");
		FormData fd_group = new FormData();
		fd_group.top = new FormAttachment(0, 10);
		fd_group.left = new FormAttachment(0, 462);
		fd_group.bottom = new FormAttachment(0, 551);
		fd_group.right = new FormAttachment(100, -10);
		group.setLayoutData(fd_group);

		text = new Text(group, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Matching ();
			}
		});
		text.setBounds(26, 22, 329, 79);

		text_1 = new Text(group, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		text_1.setBounds(26, 148, 329, 193);

		text置換タグ = new Text(group, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		text置換タグ.setText("#user_name# \tリプライ先ユーザー名/愛称\r\n#reply_name# \tリプライ先のリプライ先のユーザー名/愛称\r\n#reply_at# \t\tリプライ先のリプライ先の @スクリーンネーム\r\n\r\n#next# \t\t\tここで区切って連続ツイート\r\n#stop# \t\t\t停止ツイートしない\r\n#group_n# \t\tnは数字正規表現でヒットしたグループと置き換え、#group_0#は全部#group_1#以降から1個め、ヒットしない分は消える");
		text置換タグ.setBounds(26, 347, 329, 184);


		try {
			for(SongTitleData songTitleData : Songs.GetSongTitle())
			{
				comboSongTitle.add(songTitleData.song_Title);
				comboSongTitle.setData(songTitleData.song_Title, songTitleData.song_ID);
			}
		} catch (SQLException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

		setControl();

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}


	private void Matching ()
	{
		String result = "";
		try {
			Pattern pattern = Pattern.compile(txtBoxSearchStr.getText());
			Matcher Matcher = pattern.matcher(text.getText());
			if(!Matcher.find())
			{
				result = "一致なし";
				text_1.setText(result);
				return;
			}
			int g = Matcher.groupCount();
			for (int i = 0; i <= g; i++) {
				result += "#group_" + i + "#:" + Matcher.group(i) + "\n";
			}
			text_1.setText(result);
		} catch (Exception e) {
			// TODO: handle exception
			text_1.setText("正規表現マッチング失敗");
			return;
		}
	}
}
