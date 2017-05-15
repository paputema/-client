package kusoBotMakerClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import kusoBotMaker.BotAccount;
import kusoBotMaker.KbmUtil;
import kusoBotMakerClient.kbmMainWindow.BotAccountOnWindow;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.TwitterException;
import twitter4j.User;

public class KbmFollowerConfigWindow {
	public KbmFollowerConfigWindow() {
		super();
		mapLangTabitem = new HashMap<String, TabItem>();

	}

	private static final String ID = "ID";
	private Table tableNickname;
	private Text textNickname;
	BotAccountOnWindow botAccount;
	Shell shell;
	private Table tableフレンド;
	private Table tableフォロワー;
	private Table table方思われ;
	private Table table片思い;
	private TabFolder tabFolder;
	private TabItem tabItemNickname;
	private TabItem tabItemFriends;
	private TabItem tabItemFollower;
	private TabItem tabItem方思われ;
	private TabItem tabItem片思い;
	private Table selectTable;
	private Map<String, TabItem> mapLangTabitem;
	private ProgressBar progressBarBlock;
	Map<Long, String> friendsMap;
	private ProgressBar progressBar;
	private Display display;
	private Label lbcout;
	private Browser browser;
	private static final String URL = "URL";
	private static final String URL_TWITTER_OFFICIAL = "https://twitter.com/intent/user?user_id=";

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	/*
	 * public static void main(String[] args) { try { kbmFollowerConfig window =
	 * new kbmFollowerConfig(); window.open(); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */

	void seNickname(User user, long id, String nickname, TableItem tableItem) {
		tableItem.setText(user.getName());
		tableItem.setText(1, nickname);
		tableItem.setData(URL, URL_TWITTER_OFFICIAL + id);
		tableItem.setData(ID, id);
		tableItem.setImage(KbmImgeUtil.getImage(user.getProfileImageURLHttps()));
	}

	class Block extends Thread {
		public Block(List<Long> blocklist, Shell shell) {
			super();
			this.max = blocklist.size();
			this.blocklist = blocklist;
			//this.shell = shell;
		}

		int max;
		List<Long> blocklist;
		//Shell shell;

		public void block() {
			updateprog(true);
			KbmUtil.BlockAll(blocklist);
			int errorCount = 0;
			updateprog(0, max);
			for (Long id : blocklist) {
				updateprog();
				try {
					botAccount.twitter.createBlock(id);
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					errorCount++;
				}
				updateprog();
			}
			if(errorCount > 0)
			{
				 openMessageBox(errorCount);
			}
			updateprog(false);

		}

		public void updateprog(int min, int max) {
			checkAsyncExec(new Runnable() {
				public void run() {
					if (display.isDisposed()) {
						return;
					}
					progressBarBlock.setVisible(true);
					progressBarBlock.setMinimum(min);
					progressBarBlock.setMaximum(max);
					progressBarBlock.setSelection(0);
				}
			});
		}

		public void openMessageBox(int errorCount) {
			checkAsyncExec(new Runnable() {
				public void run() {
					if (display.isDisposed() || shell == null || shell.isDisposed()) {
						return;
					}
					KbmImgeUtil.openMessageBox(errorCount + "件失敗しました", "ブロック結果",shell);
				}
			});
		}
		public void updateprog() {
			checkAsyncExec(new Runnable() {
				public void run() {
					if (display.isDisposed() || shell == null || shell.isDisposed()) {
						return;
					}
					progressBarBlock.setSelection(progressBarBlock.getSelection() + 1);
					progressBarBlock.update();
				}
			});
		}

		public void updateprog(boolean b) {
			checkAsyncExec(new Runnable() {
				public void run() {
					if (display.isDisposed() || shell == null || shell.isDisposed()) {
						return;
					}
					progressBarBlock.setVisible(b);
					progressBarBlock.update();
				}
			});
		}

		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			block();
		}

		private boolean checkAsyncExec(Runnable runnable) {
			if (!display.isDisposed()) {
				;
				display.asyncExec(runnable);
				return true;
			} else {
				return false;
			}
		}
	}

	class GetfriendsList extends Thread {

		BotAccountOnWindow botAccount;

		public GetfriendsList(BotAccountOnWindow botAccount, Map<Long, String> friendsMap) {
			// TODO 自動生成されたコンストラクター・スタブ
			this.botAccount = botAccount;
			this.friendsMap = friendsMap;
		}

		private int getedId;
		int idSize;
		long newGetUserCount;
		private Map<Long, String> friendsMap;
		User it_user;

		public void updateprog(User user, long id, String nickname, Table table) {
			checkAsyncExec(new Runnable() {
				public void run() {
					if (display.isDisposed() || table.isDisposed() || tabFolder.isDisposed()) {
						return;
					}
					String i_nickname = nickname;
					if (i_nickname == null) {
						i_nickname = "";
					}
					progressBar.setMinimum(0);
					progressBar.setMaximum(idSize);
					progressBar.setSelection(getedId);
					lbcout.setText(getedId + "/" + idSize);
					TableItem tableItem = new TableItem(table, SWT.NONE);

					if (user != null) {
						seNickname(user, id, i_nickname, tableItem);
					} else {
						tableItem.setText(id + "");
						tableItem.setText(1, i_nickname);
						tableItem.setData(ID, id);
						tableItem.setData(URL, URL_TWITTER_OFFICIAL + id);
					}
				}
			});
		}

		public void run() {
			setNicknameList();
			getfriendsList();
		}

		public void setNicknameList() {
			getedId = 0;
			idSize = friendsMap.size();
			for (Map.Entry<Long, String> iterable_element : friendsMap.entrySet()) {
				User user = KbmUtil.getUser(iterable_element.getKey());
				getedId++;
				if (user == null) {
					try {
						user = botAccount.twitter.showUser(iterable_element.getKey());
						KbmUtil.setUser(user);
					} catch (TwitterException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
				updateprog(user, iterable_element.getKey(), iterable_element.getValue(), tableNickname);
			}
		}

		public void getfriendsList() {
			RateLimitStatus ratelimit = null;
			Set<Long> ffAllIdList = new HashSet<Long>();
			Set<Long> friendsIdList = new HashSet<Long>();
			Set<Long> followersIdList = new HashSet<Long>();
			try {
				for (Long id : botAccount.twitter.getFollowersIDs(-1L).getIDs()) {
					// if(friendsMap.get(id) == null)
					{
						followersIdList.add(id);
					}
				}
				for (Long id : botAccount.twitter.getFriendsIDs(-1L).getIDs()) {
					// if(friendsMap.get(id) == null)
					{
						friendsIdList.add(id);
					}
				}
				ffAllIdList.addAll(followersIdList);
				ffAllIdList.addAll(friendsIdList);
			} catch (TwitterException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			int count = 0;
			idSize = idSize + ffAllIdList.size();
			for (Long id : ffAllIdList) {
				if (display.isDisposed() || tableNickname.isDisposed() || shell.isDisposed()) {
					return;
				}
				try {
					User user = KbmUtil.getUser(id);
					if (user == null) {
						if (count % 10 == 0) {
							ratelimit = botAccount.twitter.getRateLimitStatus().get("/users/show/:id");
							System.out.println("取得前" + (long) ratelimit.getRemaining() + "/"
									+ (long) ratelimit.getSecondsUntilReset());
							if (ratelimit.getRemaining() <= 11) {
								long sleeptime = (long) ratelimit.getSecondsUntilReset() * 1000;
								if (sleeptime < 0) {
									sleeptime = 900 * 1000;
								}
								System.out.println("スリープ:" + sleeptime / 1000 + "秒");
								Thread.sleep(sleeptime);
							}
						}
						try {
							user = botAccount.twitter.showUser(id);
						} catch (TwitterException e) {
							// TODO: handle exception
							if(e.exceededRateLimitation())
							{
								long sleeptime = (long) e.getRateLimitStatus().getSecondsUntilReset() * 1000;
								System.out.println("スリープ:" + sleeptime / 1000 + "秒");
								Thread.sleep(sleeptime);
								user = botAccount.twitter.showUser(id);
							}else
							{
								e.printStackTrace();
								return;
							}
						}

						KbmUtil.setUser(user);
						count++;
					}
					getedId++;
					if (followersIdList.contains(id)) {
						updateprog(user, id, friendsMap.get(id), tableフォロワー);
						if (!friendsIdList.contains(id)) {
							updateprog(user, id, friendsMap.get(id), table方思われ);
						}
					}
					if (friendsIdList.contains(id)) {
						updateprog(user, id, friendsMap.get(id), tableフレンド);
						if (!followersIdList.contains(id)) {
							updateprog(user, id, friendsMap.get(id), table片思い);
						}
					}
					if (Matching(user.getName(), "(@[a-zA-Z0-9_]+)") || Matching(user.getDescription(), "(相互|フォロバ|100%|[Ff]ollow)"))
					{
						updateprog(user, id, friendsMap.get(id), tableスパム嫌疑);
					}
					if (user.getName().contains("BOT") || user.getName().contains("bot") || user.getName().contains("Bot")) {
						addtabString(user, id,"bot" );
					}

					addLangtab(user, id);
					addFriendstab(user, id);

				} catch (InterruptedException | TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}


		private boolean Matching (String strSearch , String strMatcher)
		{
			if(strMatcher == null || strSearch == null)
			{
				return false;
			}
			Pattern pattern = Pattern.compile(".*" + strMatcher + ".*");
			Matcher Matcher = pattern.matcher(strSearch);
			return Matcher.find();
		}

		private void addFriendstab(User user, long id) {
			checkAsyncExec(new Runnable() {
				public void run() {
					long l = 100000;
					for (Long friCount = (long) 1000; friCount < user.getFriendsCount(); friCount = friCount + l)
						if (user.getFriendsCount() >= friCount && user.getFriendsCount() <= friCount + l) {
							TabItem item = mapLangTabitem.get(friCount.toString());
							Table table;
							if (display.isDisposed() || tabFolder.isDisposed()) {
								return;
							}
							if (item == null) {

								item = new TabItem(tabFolder, 0);

								item.setText(friCount.toString());

								table = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
								table.setLinesVisible(true);
								table.setHeaderVisible(true);
								item.setControl(table);

								TableColumn tableColumn = new TableColumn(table, SWT.NONE);
								tableColumn.setWidth(200);
								tableColumn.setText("対象アカウント");

								TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
								tableColumn_2.setWidth(355);
								tableColumn_2.setText("愛称");

								mapTabTable.put(item, table);
								mapLangTabitem.put(friCount.toString(), item);
								table.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent arg0) {
										displayOfficialPage();
									}
								});
								setTableMenu(table);
							}
							table = mapTabTable.get(item);
							updateprog(user, id, friendsMap.get(id), table);
						}
				}
			});

		}





		private void addLangtab(User user, long id) {
			checkAsyncExec(new Runnable() {
				public void run() {
					TabItem item = mapLangTabitem.get(user.getLang());
					Table table;
					if (display.isDisposed() || tabFolder.isDisposed()) {
						return;
					}
					if (item == null) {

						item = addTab(user.getLang());
					}
					table = mapTabTable.get(item);
					updateprog(user, id, friendsMap.get(id), table);
				}
			});
		}
		private void addtabString(User user, long id, String Title) {

			checkAsyncExec(new Runnable() {
				public void run() {
					TabItem item = mapLangTabitem.get(Title);
					Table table;
					if (display.isDisposed() || tabFolder.isDisposed()) {
						return;
					}
					if (item == null) {

						item = addTab(Title);
					}
					table = mapTabTable.get(item);
					updateprog(user, id, friendsMap.get(id), table);
				}
			});
		}




	}
	private boolean checkAsyncExec(Runnable runnable) {
		if (!display.isDisposed()) {
			;
			display.asyncExec(runnable);
			return true;
		} else {
			return false;
		}
	}
	private List<Long> getCheckIdList() {
		List<Long> rt = new ArrayList<>();
		for (TableItem ti : selectTable.getItems()) {
			if (ti.getChecked()) {
				rt.add((Long) ti.getData(ID));
			}
		}
		return rt;
	}
	private void setAllCheck (boolean on)
	{
		for (TableItem ti : selectTable.getItems()) {

				ti.setChecked(on);

		}
	}

	private void follow() {
		int errorCount = 0;
		for (Long id : getCheckIdList()) {
			try {
				botAccount.twitter.createFriendship(id);
			} catch (TwitterException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				errorCount++;
			}
		}
		 if(errorCount > 0)
		{
			KbmImgeUtil.openMessageBox(errorCount + "件失敗しました", "フォロー結果", shell);
		}
	}

	private void followOut() {
		int errorCount = 0;
		for (Long id : getCheckIdList()) {
			try {
				botAccount.twitter.destroyFriendship(id);
			} catch (TwitterException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				errorCount++;
			}
		}
		 if(errorCount > 0)
		{
			KbmImgeUtil.openMessageBox(errorCount + "件失敗しました", "フォロー解除結果", shell);
		}
	}

	Map<TabItem, Table> mapTabTable;
	private Table tableスパム嫌疑;
	private TabItem tabItemスパム嫌疑;
	private Table tableBOT共;

	/**
	 * Open the window.
	 *
	 * @wbp.parser.entryPoint
	 */
	public void open(BotAccountOnWindow botAccount) {
		this.botAccount = botAccount;
		display = Display.getDefault();
		shell = new Shell();
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				resizeTable();
			}
		});
		shell.setSize(1028, 504);
		shell.setText("フォロー・フォロワー設定");
		shell.setLayout(new FormLayout());
		mapTabTable = new HashMap<TabItem, Table>();

		Group group = new Group(shell, SWT.NONE);
		group.setText("愛称変更");
		group.setLayout(new FormLayout());
		FormData fd_group = new FormData();
		fd_group.left = new FormAttachment(0, 10);
		group.setLayoutData(fd_group);

		textNickname = new Text(group, SWT.BORDER);
		FormData fd_textNickname = new FormData();
		fd_textNickname.right = new FormAttachment(0, 124);
		fd_textNickname.top = new FormAttachment(0, 10);
		fd_textNickname.left = new FormAttachment(0, 10);
		textNickname.setLayoutData(fd_textNickname);

		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = selectTable.getSelectionIndex();
				if (index == -1) {
					return;
				}
				long Friends_ID = (long) selectTable.getItem(index).getData(ID);
				String Nickname = textNickname.getText();
				KbmUtil.updateNickname(botAccount.User_ID, Friends_ID, Nickname);
				selectTable.getItem(index).setText(1, Nickname);
				for (TableItem tableitem : tableNickname.getItems()) {
					Long id = (Long) tableitem.getData(ID);
					if (id != null && id == Friends_ID) {
						tableitem.setText(1, Nickname);
						return;
					}
				}
				User user = KbmUtil.getUser(Friends_ID);
				TableItem newTableItem = new TableItem(tableNickname, SWT.NONE);
				seNickname(user, Friends_ID, Nickname, newTableItem);
			}
		});
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(textNickname, -2, SWT.TOP);
		fd_btnNewButton.right = new FormAttachment(textNickname, 78, SWT.RIGHT);
		fd_btnNewButton.left = new FormAttachment(textNickname, 6);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("設定");

		progressBar = new ProgressBar(shell, SWT.NONE);
		fd_group.top = new FormAttachment(progressBar, -51);
		fd_group.bottom = new FormAttachment(progressBar, 0, SWT.BOTTOM);
		FormData fd_progressBar = new FormData();
		fd_progressBar.bottom = new FormAttachment(100, -10);
		progressBar.setLayoutData(fd_progressBar);

		progressBarBlock = new ProgressBar(shell, SWT.NONE);
		FormData fd_progressBarBlock;
		fd_progressBarBlock = new FormData();
		fd_progressBarBlock.right = new FormAttachment(progressBar, 176, SWT.RIGHT);
		fd_progressBarBlock.bottom = new FormAttachment(group, 0, SWT.BOTTOM);
		fd_progressBarBlock.left = new FormAttachment(progressBar, 6);
		progressBarBlock.setLayoutData(fd_progressBarBlock);
		progressBarBlock.setVisible(false);

		lbcout = new Label(shell, SWT.NONE);
		FormData fd_lbcout = new FormData();
		fd_lbcout.bottom = new FormAttachment(progressBar, -6);
		fd_lbcout.top = new FormAttachment(group, 0, SWT.TOP);
		fd_lbcout.left = new FormAttachment(progressBar, 0, SWT.LEFT);
		lbcout.setLayoutData(fd_lbcout);
		lbcout.setText("New Label");

		browser = new Browser(shell, SWT.NONE);
		FormData fd_browser = new FormData();
		fd_browser.top = new FormAttachment(0, 34);
		fd_browser.right = new FormAttachment(100, -10);
		fd_browser.bottom = new FormAttachment(100, -112);
		browser.setLayoutData(fd_browser);

		Group group_1 = new Group(shell, SWT.NONE);
		fd_progressBar.left = new FormAttachment(group_1, 41);

		Button btnNewButtondelete = new Button(group, SWT.NONE);
		btnNewButtondelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int index = tableNickname.getSelectionIndex();
				if (index == -1) {
					return;
				}
				long Friends_ID = (long) tableNickname.getItem(index).getData(ID);
				String Nickname = textNickname.getText();
				KbmUtil.deleteNickname(botAccount.User_ID, Friends_ID, Nickname);
				tableNickname.getItem(index).dispose();
			}
		});
		FormData fd_btnNewButtondelete = new FormData();
		fd_btnNewButtondelete.top = new FormAttachment(textNickname, -2, SWT.TOP);
		fd_btnNewButtondelete.right = new FormAttachment(btnNewButton, 78, SWT.RIGHT);
		fd_btnNewButtondelete.left = new FormAttachment(btnNewButton, 6);
		btnNewButtondelete.setLayoutData(fd_btnNewButtondelete);
		btnNewButtondelete.setText("削除");
		group_1.setText("一括操作");
		FormData fd_group_1 = new FormData();
		fd_group_1.bottom = new FormAttachment(group, 0, SWT.BOTTOM);
		fd_group_1.left = new FormAttachment(group, 6);
		fd_group_1.top = new FormAttachment(100, -64);
		group_1.setLayoutData(fd_group_1);

		Button btnFollow = new Button(group_1, SWT.NONE);
		btnFollow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				follow();
			}
		});
		btnFollow.setBounds(10, 26, 75, 25);
		btnFollow.setText("フォロー");

		Button btnBreak = new Button(group_1, SWT.NONE);
		btnBreak.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				followOut();
			}
		});
		btnBreak.setBounds(91, 26, 75, 25);
		btnBreak.setText("フォロー解除");

		Button btnBlock = new Button(group_1, SWT.NONE);
		btnBlock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Block block = new Block(getCheckIdList(), shell);
				block.setName("ブロック");
				block.setDaemon(true);
				block.start();
			}
		});
		btnBlock.setBounds(172, 26, 75, 25);
		btnBlock.setText("ブロック");

		tabFolder = new TabFolder(shell, SWT.NONE);
		fd_browser.left = new FormAttachment(tabFolder, 6);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int index = tabFolder.getSelectionIndex();
				if (index == -1) {
					return;
				}
				selectTable = mapTabTable.get(tabFolder.getItem(index));
				resizeTable();
			}
		});
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.right = new FormAttachment(0, 587);
		fd_tabFolder.left = new FormAttachment(0, 10);
		fd_tabFolder.top = new FormAttachment(0, 10);
		fd_tabFolder.bottom = new FormAttachment(group, -6);
		tabFolder.setLayoutData(fd_tabFolder);

		TabItem tabItemBOT共 = new TabItem(tabFolder, 0);
		tabItemBOT共.setText("KBM登録アカ");



		tableBOT共 = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		tableBOT共.setLinesVisible(true);
		tableBOT共.setHeaderVisible(true);
		tabItemBOT共.setControl(tableBOT共);

		TableColumn tableColumn_7 = new TableColumn(tableBOT共, SWT.NONE);
		tableColumn_7.setWidth(200);
		tableColumn_7.setText("対象アカウント");

		TableColumn tableColumn_8 = new TableColumn(tableBOT共, SWT.NONE);
		tableColumn_8.setWidth(100);
		tableColumn_8.setText("愛称");

		TableColumn tableColumn_9 = new TableColumn(tableBOT共, SWT.NONE);
		tableColumn_9.setWidth(70);
		tableColumn_9.setText("フォロー");

		TableColumn tableColumn_10 = new TableColumn(tableBOT共, SWT.NONE);
		tableColumn_10.setWidth(70);
		tableColumn_10.setText("非フォロー");

		tabItemNickname = new TabItem(tabFolder, SWT.NONE);
		tabItemNickname.setText("愛称");

		tableNickname = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		tabItemNickname.setControl(tableNickname);
		tableNickname.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				displayOfficialPage();
			}
		});
		tableNickname.setHeaderVisible(true);
		tableNickname.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(tableNickname, SWT.NONE);
		tblclmnNewColumn.setWidth(200);
		tblclmnNewColumn.setText("対象アカウント");

		TableColumn tblclmnNewColumn_1 = new TableColumn(tableNickname, SWT.NONE);
		tblclmnNewColumn_1.setWidth(355);
		tblclmnNewColumn_1.setText("愛称");

		tabItemFriends = new TabItem(tabFolder, SWT.NONE);
		tabItemFriends.setText("フォロー");

		tableフレンド = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		tableフレンド.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				displayOfficialPage();
			}
		});
		tabItemFriends.setControl(tableフレンド);
		tableフレンド.setHeaderVisible(true);
		tableフレンド.setLinesVisible(true);

		TableColumn tblclmnFriendsAccount = new TableColumn(tableフレンド, SWT.NONE);
		tblclmnFriendsAccount.setWidth(200);
		tblclmnFriendsAccount.setText("対象アカウント");

		TableColumn tblclmnFriendsNickname = new TableColumn(tableフレンド, SWT.NONE);
		tblclmnFriendsNickname.setWidth(100);
		tblclmnFriendsNickname.setText("愛称");

		tabItemFollower = new TabItem(tabFolder, 0);
		tabItemFollower.setText("フォロワー");

		tableフォロワー = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		tableフォロワー.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				displayOfficialPage();
			}
		});
		tableフォロワー.setLinesVisible(true);
		tableフォロワー.setHeaderVisible(true);
		tabItemFollower.setControl(tableフォロワー);

		TableColumn tblclmnFollowerAccount = new TableColumn(tableフォロワー, SWT.NONE);
		tblclmnFollowerAccount.setWidth(200);
		tblclmnFollowerAccount.setText("対象アカウント");

		TableColumn tableColumn_1 = new TableColumn(tableフォロワー, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("愛称");

		tabItem方思われ = new TabItem(tabFolder, 0);
		tabItem方思われ.setText("方思われ");

		table方思われ = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		table方思われ.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				displayOfficialPage();
			}
		});
		table方思われ.setLinesVisible(true);
		table方思われ.setHeaderVisible(true);
		tabItem方思われ.setControl(table方思われ);

		TableColumn tableColumn = new TableColumn(table方思われ, SWT.NONE);
		tableColumn.setWidth(200);
		tableColumn.setText("対象アカウント");

		TableColumn tableColumn_2 = new TableColumn(table方思われ, SWT.NONE);
		tableColumn_2.setWidth(355);
		tableColumn_2.setText("愛称");

		tabItem片思い = new TabItem(tabFolder, 0);
		tabItem片思い.setText("片思い");

		table片思い = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		table片思い.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				displayOfficialPage();
			}
		});
		table片思い.setLinesVisible(true);
		table片思い.setHeaderVisible(true);
		tabItem片思い.setControl(table片思い);

		TableColumn tableColumn_3 = new TableColumn(table片思い, SWT.NONE);
		tableColumn_3.setWidth(200);
		tableColumn_3.setText("対象アカウント");

		TableColumn tableColumn_4 = new TableColumn(table片思い, SWT.NONE);
		tableColumn_4.setWidth(355);
		tableColumn_4.setText("愛称");

		shell.open();
		shell.layout();
		friendsMap = KbmUtil.getNicknameMap(botAccount.User_ID);

		GetfriendsList gfl = new GetfriendsList(botAccount, friendsMap);
		gfl.setName("フレンドリスト取得");
		gfl.setDaemon(true);
		gfl.start();

		tabItemスパム嫌疑 = new TabItem(tabFolder, 0);
		tabItemスパム嫌疑.setText("スパム嫌疑");

		tableスパム嫌疑 = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		tableスパム嫌疑.setLinesVisible(true);
		tableスパム嫌疑.setHeaderVisible(true);
		tabItemスパム嫌疑.setControl(tableスパム嫌疑);
		mapTabTable.put(tabItemBOT共, tableBOT共);
		tableスパム嫌疑.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				displayOfficialPage();
			}
		});

		selectTable = tableNickname;
		mapTabTable.put(tabItemNickname, tableNickname);
		selectTable = mapTabTable.get(tabFolder.getItem(tabFolder.getSelectionIndex()));






		TableColumn tableColumn_5 = new TableColumn(tableスパム嫌疑, SWT.NONE);
		tableColumn_5.setWidth(200);
		tableColumn_5.setText("対象アカウント");

		TableColumn tableColumn_6 = new TableColumn(tableスパム嫌疑, SWT.NONE);
		tableColumn_6.setWidth(355);
		tableColumn_6.setText("愛称");

		Label labelBlock = new Label(shell, SWT.NONE);
		fd_progressBarBlock.top = new FormAttachment(labelBlock, 6);
		labelBlock.setText("");
		FormData fd_labelBlock = new FormData();
		fd_labelBlock.top = new FormAttachment(browser, 54);
		fd_labelBlock.bottom = new FormAttachment(100, -33);
		fd_labelBlock.right = new FormAttachment(100, -188);
		fd_labelBlock.left = new FormAttachment(lbcout, 121);
		labelBlock.setLayoutData(fd_labelBlock);


		mapTabTable.put(tabItemFollower, tableフォロワー);
		mapTabTable.put(tabItemFriends, tableフレンド);
		mapTabTable.put(tabItem方思われ, table方思われ);
		mapTabTable.put(tabItem片思い, table片思い);
		mapTabTable.put(tabItemスパム嫌疑, tableスパム嫌疑);

		addTab("bot");
		for (Table table : mapTabTable.values()) {
			setTableMenu(table);
		}

		for (BotAccount accountOnWindow : KbmUtil.botAccounts.values()) {

					updateprog(accountOnWindow.user, accountOnWindow.User_ID, friendsMap.get(accountOnWindow.User_ID),
							tableBOT共);

		}


		resizeTable();




		// gfl.getfriendsList();
		while (!shell.isDisposed()) {
			if (display != null && !display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void updateprog(User user, long id, String nickname, Table table) {
		checkAsyncExec(new Runnable() {
			public void run() {
				if (display.isDisposed() || table.isDisposed() || tabFolder.isDisposed()) {
					return;
				}
				String i_nickname = nickname;
				if (i_nickname == null) {
					i_nickname = "";
				}
				TableItem tableItem = new TableItem(table, SWT.NONE);

				if (user != null) {
					seNickname(user, id, i_nickname, tableItem);
				} else {
					tableItem.setText(id + "");
					tableItem.setText(1, i_nickname);
				}
				Relationship re;
				try {
					re = (botAccount.twitter.showFriendship(botAccount.User_ID, id));
					tableItem.setText(2, re.isSourceFollowingTarget() ? "○" : "✕");
					tableItem.setText(3, re.isSourceFollowedByTarget() ? "○" : "✕");
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

				tableItem.setData(ID, id);
				tableItem.setData(URL, URL_TWITTER_OFFICIAL + id);
			}
		});

	}
	private void setTableMenu(Table table) {

			Menu menu = new Menu(table);
			table.setMenu(menu);

			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					setAllCheck(true);
				}
			});
			menuItem.setText("すべて選択");
	}

	void displayOfficialPage() {
		int selectTabIndex = tabFolder.getSelectionIndex();
		if(selectTabIndex == -1)
		{
			return;
		}
		TabItem tabItem = tabFolder.getItem(selectTabIndex) ;


		Table table = mapTabTable.get(tabItem);
		int index = table.getSelectionIndex();
		if (index == -1) {
			return;
		}

		String url = (String) table.getItem(index).getData("URL");
		browser.setUrl(url);
		browser.update();
	}

	void resizeTable() {
		if (mapTabTable == null) {
			return;
		}
		for (Table table : mapTabTable.values()) {
			if (table != null) {
				int tablesize = table.getSize().x;
				tablesize = tablesize - table.getBorderWidth() - table.getGridLineWidth() - 1;
				if (table.getVerticalBar().getVisible()) {
					tablesize = tablesize - table.getVerticalBar().getSize().x;
				}
				tablesize = tablesize - table.getColumn(0).getWidth();
				table.getColumn(1).pack();

			}
		}

	}
	private TabItem addTab(String Title) {
		TabItem item = new TabItem(tabFolder, 0);
		Title = (Title != null) ? Title : "無題";


		item.setText(Title);

		Table table = new Table(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		item.setControl(table);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(200);
		tableColumn.setText("対象アカウント");

		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(355);
		tableColumn_2.setText("愛称");

		setTableMenu(table);
		mapTabTable.put(item, table);
		mapLangTabitem.put(item.getText(), item);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				displayOfficialPage();
			}
		});

		return item;
	}
}
