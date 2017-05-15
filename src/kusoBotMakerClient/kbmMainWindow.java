package kusoBotMakerClient;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import kusoBotMaker.BotAccount;
import kusoBotMaker.DataContainer;
import kusoBotMaker.KbmConnectClient;
import kusoBotMaker.KbmUtil;
import kusoBotMaker.Ttwitter_OAuth;
import kusoBotMaker.enumBotAcountStatus;
import kusoBotMaker.enumSocketMode;
import twitter4j.PagableResponseList;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;

public class kbmMainWindow {

	public void closeSocket() {
		kbmConnectClient.closeSocket();
	}

	public class KbmConnectClientWindow extends KbmConnectClient {
		public void execContainer(DataContainer container) {
			super.execContainer(container);
			refreshBotAcountStatus(container);
		}

	}

	public kbmMainWindow() {
		super();
		mapBotAccountOnWindow = new HashMap<Long, BotAccountOnWindow>();
		kbmConnectClient = new KbmConnectClientWindow();
		if (clientOnry) {
			kbmConnectClient.setDaemon(true);
			kbmConnectClient.setName("KBMclient");
			kbmConnectClient.start();
		}
	}
	void refreshBotAcountStatusAll()
	{
		for (BotAccountOnWindow botAccountOnWindow  : mapBotAccountOnWindow.values()) {
			botAccountOnWindow.refreshBotAcountStatus();
		}
	}

	private void refreshBotAcountStatus(kusoBotMaker.DataContainer dtCon) {

		BotAccountOnWindow botAccountOnWindow = mapBotAccountOnWindow.get(dtCon.getBotAccountID());
		if (botAccountOnWindow == null) {
			return;
		}
		botAccountOnWindow.setBotAcountStatus(dtCon.getBotAcountStatus());
		botAccountOnWindow.refreshBotAcountStatus();

	}

	public class BotAccountOnWindow extends BotAccount {

		@Override
		public boolean deleteBotDB() {
			// TODO 自動生成されたメソッド・スタブ
			if (clientOnry) {
				ChangeBotStatus(enumSocketMode.BOT_DEL);
			}
			return super.deleteBotDB();
		}

		class BotAccountWindowUserStreamAdapter extends BotAccountUserStreamAdapter {

			BotAccountWindowUserStreamAdapter(Configuration conf) {
				super(conf);
				// TODO 自動生成されたコンストラクター・スタブ
			}

			@Override
			public void onStatus(Status status) {
				// テーブルのアイテムをアップデート
				if (!clientOnry) {
					super.onStatus(status);
				}
				if (user == null /*
									 * || status.getUser().getId() !=
									 * user.getId()
									 */) {
					return;
				}
				checkAsyncExec(new Runnable() {
					@Override
					public void run() {
						if (display == null || tableItem.isDisposed() || display.isDisposed()) {
							return;
						}
						tableItem.setText(2, "[" + status.getUser().getName() + "] \n" + status.getText());
						tableItem.setImage(2, KbmImgeUtil.getImage(status.getUser().getProfileImageURLHttps()));
						KbmUtil.setUser(status.getUser());

					}
				});

			}

			// 有効なのは自分だけ
			@Override
			public void onUserProfileUpdate(User updatedUser) {
				// TODO 自動生成されたメソッド・スタブ
				if (!clientOnry) {
					super.onUserProfileUpdate(updatedUser);
					refreshBotAcountStatus();
				}
				KbmUtil.setUser(updatedUser);
				System.out.println(updatedUser.getName() + "がプロフィール変更");
				kbmConnectClient.sendContainer(new DataContainer(enumSocketMode.BOT_STATUS, updatedUser.getId()));
			}

			// 自分がフォローした時と自分がフォローされた時
			@Override
			public void onFollow(User source, User followedUser) {
				// TODO 自動生成されたメソッド・スタブ
				if (!clientOnry) {
					super.onFollow(source, followedUser);
				}
				KbmUtil.setUser(source);
				if (user != null && user.getId() == followedUser.getId()) {

				}
				System.out.println(source.getName() + "が" + followedUser.getName() + "をフォロー");
			}

			// 自分が誰かのフォローを解除した時だけ(またはブロックを受けた)
			@Override
			public void onUnfollow(User source, User unfollowedUser) {
				// TODO 自動生成されたメソッド・スタブ
				super.onUnfollow(source, unfollowedUser);
				KbmUtil.setUser(source);
				if (user != null && user.getId() == unfollowedUser.getId()) {

				}
				System.out.println(source.getName() + "が" + unfollowedUser.getName() + "をフォロー解除");
			}

		}

		class GetfriendsLIST extends Thread {
			@Override
			public void run() {
				RateLimitStatus ratelimit = null;
				try {
					long parm = -1;
					PagableResponseList<User> friendsList = null;
					do {
						// if(count % 1 == 0)
						{
							ratelimit = twitter.getRateLimitStatus().get("/friends/list");
							if (ratelimit.getRemaining() <= 2) {
								long sleep = ((long) ratelimit.getSecondsUntilReset() * 1000);
								if (sleep <= 0) {
									sleep = 900 * 1000;
								}
								System.out.println("スリープ:" + sleep / 1000 + "秒");
								Thread.sleep(sleep);
							}
						}
						friendsList = twitter.getFriendsList(User_ID, parm);
						for (User user : friendsList) {
							KbmUtil.setUser(user);
						}
						parm = friendsList.getNextCursor();
					} while (friendsList.hasNext());
				} catch (TwitterException | InterruptedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
					System.out.println("リストエラー");
				}
			}
		}

		class GetFollowerLIST extends Thread {
			@Override
			public void run() {
				RateLimitStatus ratelimit = null;

				try {
					long parm = -1;
					PagableResponseList<User> followersList = null;
					do {
						// if(count % 1 == 0)
						{
							ratelimit = twitter.getRateLimitStatus().get("/followers/list");
							if (ratelimit.getRemaining() <= 2) {
								long sleep = ((long) ratelimit.getSecondsUntilReset() * 1000);
								if (sleep <= 0) {
									sleep = 900 * 1000;
								}
								System.out.println("スリープ:" + sleep / 1000 + "秒");
								Thread.sleep(sleep);
							}
						}
						followersList = twitter.getFollowersList(User_ID, parm);
						for (User user : followersList) {
							KbmUtil.setUser(user);
						}
						parm = followersList.getNextCursor();
					} while (followersList.hasNext());
				} catch (TwitterException | InterruptedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
					System.out.println("リストエラー");
				}
			}
		}

		@Override
		protected void onTwitterException(TwitterException e) {
			if (!clientOnry) {
				super.onTwitterException(e);
			}
			switch (e.getErrorCode()) {
			case 88:
			case -1:
				break;
			default:
				refreshBotAcountStatus();
				break;

			}
		}

		/*
		 * class StartBotThread extends BotAccount.StartBotThread {
		 *
		 * @Override public void run() { super.run(); refreshBotAcountStatus();
		 * } }
		 *
		 * class StopBotThread extends BotAccount.StopBotThread {
		 *
		 * @Override public void run() { super.run(); refreshBotAcountStatus();
		 * } }
		 */
		GetFollowerLIST folist;
		GetfriendsLIST frlist;

		@Override
		public Boolean startBot() {
			// TODO 自動生成されたメソッド・スタブ
			if (!clientOnry) {
				Enable = super.startBot();
			} else {
				// startBotTwitterStream();
				Enable = true;
				setBotEnable();
				ChangeBotStatus(enumSocketMode.BOT_START);
			}
			refreshBotAcountStatus();
			return Enable;
		}

		@Override
		public Boolean stopBot() {
			// TODO 自動生成されたメソッド・スタブ
			if (!clientOnry) {
				Enable = super.stopBot();
			} else {
				// stopBotTwitterStream();
				Enable = false;
				setBotEnable();
				ChangeBotStatus(enumSocketMode.BOT_STOP);
			}
			refreshBotAcountStatus();
			return Enable;
		}

		public void ChangeBotStatus(enumSocketMode enumSocketMode) {
			kbmConnectClient.sendContainer(new DataContainer(enumSocketMode, this.User_ID));
		}

		TableItem tableItem;
		String consumerKey;
		String consumerSecret;
		String Access_Token;
		String Access_Token_Secret;

		// やり直し
		BotAccountOnWindow RetryBotAccountOnWindow() {
			this.tableItem.dispose();
			return new BotAccountOnWindow(User_ID, consumerKey, consumerSecret, Access_Token, Access_Token_Secret,
					Enable, normalPostInterval, pauseTime, replyRt);
		}

		// トリームの取得を開始
		BotAccountOnWindow(long User_ID, String consumerKey, String consumerSecret, String Access_Token,
				String Access_Token_Secret, boolean Enable, long normalPostInterval, long pauseTime, boolean replyRt) {
			super(User_ID, consumerKey, consumerSecret, Access_Token, Access_Token_Secret, Enable, normalPostInterval,
					pauseTime, replyRt);
			this.consumerKey = consumerKey;
			this.consumerSecret = consumerSecret;
			this.Access_Token = Access_Token;
			this.Access_Token_Secret = Access_Token_Secret;

			try {
				this.user = this.twitter.verifyCredentials();
				if (this.tableItem == null) {
					this.tableItem = new TableItem(table, SWT.RIGHT);
				}
				tableItem.setData(this);
				tableItem.setText(user.getName());
				tableItem.setImage(KbmImgeUtil.getImage((this.user.getProfileImageURLHttps())));
				if (!clientOnry) {
					if (this.Enable) {
						this.startBot();
					} else {
						this.stopBot();
					}
				}
				if (user != null) {
					folist = new GetFollowerLIST();
					frlist = new GetfriendsLIST();
					folist.setDaemon(true);
					frlist.setDaemon(true);
					folist.start();
					frlist.start();
					refreshBotAcountStatusClientOnry();
				}
			} catch (IllegalStateException | TwitterException e) {
				this.tableItem = new TableItem(table, SWT.RIGHT);
				tableItem.setText(User_ID + "");
				tableItem.setText(1, "起動失敗");
				tableItem.setText(2, "しばらくしたら「開始」を選択してください");
				tableItem.setData(this);
				this.setBotAcountStatus(enumBotAcountStatus.BOTPAUSE);
			}
			mapBotAccountOnWindow.put(User_ID, this);

		}

		void refreshBotAcountStatusClientOnry() {
			if (clientOnry) {
				kbmConnectClient.sendContainer(new DataContainer(enumSocketMode.BOT_STATUS,
						((BotAccountOnWindow) tableItem.getData()).User_ID));
			}
		}

		void refreshBotAcountStatus() {
			checkAsyncExec(new Runnable() {
				@Override
				public void run() {
					if (display.isDisposed() || tableItem.isDisposed() || imageIsDisposed()) {
						return;
					}
					try {
						tableItem.setText(1, getbotAcountStatus());
						user = twitter.verifyCredentials();
						tableItem.setText(user.getName());
						tableItem.setImage(KbmImgeUtil.getImageRe((user.getProfileImageURLHttps())));
					} catch (TwitterException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						return;
					}
				}
			});
		}

		@Override
		public void exec_twitterstream(Configuration conf) {
			// super.exec_twitterstream(conf);
			// TwitterStreamを生成
			TwitterStreamFactory factory = new TwitterStreamFactory(conf);
			twitterStream = factory.getInstance();
			// イベントを受け取るリスナーオブジェクトを設定
			twitterStream.addListener(new BotAccountWindowUserStreamAdapter(conf));
		}

	}

	static Display display;
	static Table table;

	static void close() {
		for (int i = 0; i < table.getItemCount(); i++) {
			BotAccount bot = (BotAccountOnWindow) table.getItem(i).getData();
			if (bot != null) {
				bot.closeBot(clientOnry);
			}
		}
		// db.Close_db();
	}

	// アカウントが既にテーブルにないか確認
	static boolean equals(long userID, BotAccountOnWindow botB) {
		return (botB != null && userID == botB.User_ID);
	}

	static void equalsBotInTable(long userID, Table table) {
		for (TableItem iterable_element : table.getItems()) {
			if (equals(userID, (BotAccountOnWindow) iterable_element.getData())) {
				((BotAccountOnWindow) iterable_element.getData()).stopBot();
				iterable_element.dispose();
			}
		}

	}

	/**
	 * @author paputema
	 * @throws TwitterException
	 *
	 */
	public static BotAccountOnWindow getBotAccountByTable() throws TwitterException {

		BotAccountOnWindow bat = (BotAccountOnWindow) table.getItem(table.getSelectionIndex()).getData();
		if (bat.user == null) {
			bat.user = bat.twitter.verifyCredentials();
		}
		return bat;
	}

	public static void BotAccountStop() {
		if (table.getSelectionIndex() == -1) {
			return;
		}
		try {
			getBotAccountByTable().stopBot();
		} catch (TwitterException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
			KbmImgeUtil.openMessageBox("BOTを停止できませんでした", "BOTを停止できませんでした", shell);
		}
	}

	public static void BotAccountStart() {
		if (table.getSelectionIndex() == -1) {
			return;
		}
		try {
			getBotAccountByTable().startBot();
		} catch (TwitterException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
			KbmImgeUtil.openMessageBox("BOTを開始できませんでした", "BOTを開始できませんでした", shell);
		}
	}

	public static void BotAccountrefresh() {
		if (table.getSelectionIndex() == -1) {
			return;
		}
		try {
			getBotAccountByTable().refreshBotAcountStatusClientOnry();

			getBotAccountByTable().refreshBotAcountStatus();
		} catch (TwitterException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
			KbmImgeUtil.openMessageBox("BOTの情報を再取得きませんでした", "BOTの情報を再取得きませんでした", shell);
		}
	}

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	static Shell shell;
	public static boolean clientOnry = false;

	private static void readClientOnry() {
		Properties properties = KbmUtil.properties;

		clientOnry = false;

		// 値の取得
		clientOnry = (properties.getProperty("CLIENTONLY").contentEquals("0")) ? false : true;
	}

	Map<Long, BotAccountOnWindow> mapBotAccountOnWindow;
	KbmConnectClientWindow kbmConnectClient;

	public static void main(String[] args) {
		readClientOnry();
		kbmMainWindow kbmwin = new kbmMainWindow();

		display = Display.getDefault();
		shell = new Shell();
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				resizeTable();
			}

			@Override
			public void controlMoved(ControlEvent arg0) {
				resizeTable();
			}
		});

		shell.setSize(664, 500);
		shell.setMinimumSize(new Point(700, 500));
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				close();
				// display.dispose();
			}
		});
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
			}
		});
		shell.setText("クソbotメーカー");
		shell.setLayout(new FormLayout());

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BotAccountStart();
			}
		});
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.left = new FormAttachment(0, 10);
		fd_btnNewButton.top = new FormAttachment(100, -35);
		fd_btnNewButton.bottom = new FormAttachment(100, -10);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("開始");

		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		fd_btnNewButton.right = new FormAttachment(btnNewButton_1, -6);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BotAccountStop();
			}
		});
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.top = new FormAttachment(100, -35);
		fd_btnNewButton_1.bottom = new FormAttachment(100, -10);
		fd_btnNewButton_1.right = new FormAttachment(0, 130);
		fd_btnNewButton_1.left = new FormAttachment(0, 73);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
		btnNewButton_1.setText("停止");

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(btnNewButton, -6);
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.left = new FormAttachment(0, 10);
		composite.setLayoutData(fd_composite);

		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		table.setBackground(display.getSystemColor( SWT.COLOR_WHITE));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tableColumnAccount = new TableColumn(table, SWT.CENTER);
		tableColumnAccount.setWidth(260);
		tableColumnAccount.setText("アカウント");

		TableColumn tableColumnStatus = new TableColumn(table, SWT.CENTER);
		tableColumnStatus.setWidth(100);
		tableColumnStatus.setText("状態");

		TableColumn tableColumnLastStatus = new TableColumn(table, SWT.LEFT);
		tableColumnLastStatus.setWidth(258);
		tableColumnLastStatus.setText("取得ツイート");

		Menu menu = new Menu(table);
		table.setMenu(menu);

		MenuItem menuItem_5 = new MenuItem(menu, SWT.NONE);
		menuItem_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (TableItem tableItem : table.getSelection()) {
					new KbmMyTweetWindow().open(shell, (BotAccountOnWindow) tableItem.getData());
				}
			}
		});
		menuItem_5.setText("過去のツイート確認");

		MenuItem menuItem_6 = new MenuItem(menu, SWT.NONE);
		menuItem_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				for (TableItem tableItem : table.getSelection()) {
					new KbmReplyTweetWindow().open(shell, (BotAccountOnWindow) tableItem.getData());
				}
			}
		});
		menuItem_6.setText("メンション確認");

		MenuItem menuItem_4 = new MenuItem(menu, SWT.NONE);
		menuItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					new kbmTLwindow().open(shell, getBotAccountByTable());
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		});
		menuItem_4.setText("タイムライン確認");

		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BotAccountrefresh();
			}
		});

		menuItem.setText("ステータス更新");

		MenuItem menuItem_1 = new MenuItem(menu, SWT.NONE);
		menuItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BotAccountStart();
			}
		});
		menuItem_1.setText("開始");

		MenuItem menuItem_2 = new MenuItem(menu, SWT.NONE);
		menuItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BotAccountStop();
			}
		});
		menuItem_2.setText("停止");

		MenuItem menuItem_3 = new MenuItem(menu, SWT.NONE);
		menuItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				try {

					Desktop desktop = Desktop.getDesktop();
					String url = "https://twitter.com/intent/user?user_id="
							+ ((BotAccountOnWindow) table.getItem(index).getData()).twitter.getId();

					desktop.browse(new URI(url));
				} catch (IllegalStateException | TwitterException | IOException | URISyntaxException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
			}
		});
		menuItem_3.setText("ツイッター公式");

		Button btnNewButton_3 = new Button(shell, SWT.NONE);
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (table.getSelectionIndex() != -1) {
						KbmSettingWinsow ksw = new KbmSettingWinsow(
								(BotAccountOnWindow) table.getItem(table.getSelectionIndex()).getData());
						ksw.open(shell);
					}
				} catch (TwitterException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
					KbmImgeUtil.openMessageBox("ユーザー情報の取得に失敗", "ユーザー情報の取得に失敗", shell);
					return;
				}

			}
		});
		FormData fd_btnNewButton_3 = new FormData();
		fd_btnNewButton_3.top = new FormAttachment(100, -35);
		fd_btnNewButton_3.bottom = new FormAttachment(100, -10);
		btnNewButton_3.setLayoutData(fd_btnNewButton_3);
		btnNewButton_3.setText("BOT設定");

		Button btnNewButton_2 = new Button(shell, SWT.NONE);
		fd_btnNewButton_3.right = new FormAttachment(btnNewButton_2, -6);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				kbmOAuthWindow gi = new kbmOAuthWindow();
				Ttwitter_OAuth to = gi.open();
				if (to != null) {
					kbmwin.addBotAcount(to);
				}
			}
		});
		FormData fd_btnNewButton_2 = new FormData();
		fd_btnNewButton_2.top = new FormAttachment(100, -35);
		fd_btnNewButton_2.bottom = new FormAttachment(100, -10);
		btnNewButton_2.setLayoutData(fd_btnNewButton_2);
		btnNewButton_2.setText("BOT追加");

		KbmUtil.init();// 一回呼び出して初期化してやらんと落ちる。初期化中に競合でも起こすの？
		kbmwin.addBotAcount(BotAccount.GetAccessToken());

		shell.pack();

		Button btnBotDelete = new Button(shell, SWT.NONE);
		fd_btnNewButton_2.right = new FormAttachment(btnBotDelete, -183);
		btnBotDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				int index = table.getSelectionIndex();
				if (index != -1) {
					BotAccountOnWindow botAccount = getBotAccountOnWindow(index);
					botAccount.stopBot();
					botAccount.deleteBotDB();
					botAccount.tableItem.dispose();
				}
			}
		});
		FormData fd_btnBotDelete = new FormData();
		fd_btnBotDelete.right = new FormAttachment(100, -10);
		fd_btnBotDelete.bottom = new FormAttachment(100, -10);
		btnBotDelete.setLayoutData(fd_btnBotDelete);
		btnBotDelete.setText("BOT削除");

		Menu menu_1 = new Menu(shell);
		shell.setMenu(menu_1);

		MenuItem mntmKbm = new MenuItem(menu_1, SWT.NONE);
		mntmKbm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				new kbmAboutWindow().open();
			}
		});
		mntmKbm.setText("KBMについて");

		Button btnNewButtonSong = new Button(shell, SWT.NONE);
		btnNewButtonSong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				kbmSongList ksl = new kbmSongList();
				ksl.open();
			}
		});
		FormData fd_btnNewButtonSong = new FormData();
		fd_btnNewButtonSong.bottom = new FormAttachment(btnNewButton, 0, SWT.BOTTOM);
		fd_btnNewButtonSong.right = new FormAttachment(btnNewButton_3, -6);
		btnNewButtonSong.setLayoutData(fd_btnNewButtonSong);
		btnNewButtonSong.setText("歌/掛け合い");

		shell.open();
		shell.layout();
		// ExecutorService executorserveUpdateStatuServer =
		// Executors.newFixedThreadPool(1);
		resizeTable();


		kbmwin.refreshBotAcountStatusAll();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// close();
		// executorserveUpdateStatuServer.shutdownNow();
		// display.dispose();

		kbmwin.closeSocket();

	}

	private static BotAccountOnWindow getBotAccountOnWindow(int index) {
		BotAccountOnWindow rt = (BotAccountOnWindow) table.getItem(index).getData();
		return rt;

	}

	private static boolean imageIsDisposed() {
		for (TableItem item : table.getItems()) {
			if (imageIsDisposed(item)) {
				return true;
			}
		}
		return false;
	}

	private static boolean imageIsDisposed(TableItem item) {

		Image image = item.getImage();
		return ((image != null) && image.isDisposed());
		// TODO 自動生成されたメソッド・スタブ
	}

	void addBotAcount(Ttwitter_OAuth at) {
		if (at != null) {
			equalsBotInTable(at.accessToken.getUserId(), table);
			BotAccountOnWindow botAccountOnWindow = new BotAccountOnWindow(at.accessToken.getUserId(), at.consumerKey,
					at.consumerSecret, at.accessToken.getToken(), at.accessToken.getTokenSecret(), true, 60, 120,
					false);
			botAccountOnWindow.ChangeBotStatus(enumSocketMode.BOT_ADD);
		}

	}

	void addBotAcount(ResultSet rs) {

		try {
			while (rs.next()) {
				new BotAccountOnWindow(rs.getLong("User_ID"), rs.getString("Consumer_Key"),
						rs.getString("Consumer_Secret"), rs.getString("Access_Token"),
						rs.getString("Access_Token_Secret"), rs.getBoolean("bot_enable"),
						rs.getLong("normal_post_interval"), rs.getLong("pause_time"), rs.getBoolean("replytoRT"));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
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

	private static void resizeTable() {
		checkAsyncExec(new Runnable() {
			@Override
			public void run() {
				Table table = kbmMainWindow.table;
				if (table != null) {
					int tablesize = table.getSize().x;
					tablesize = tablesize - table.getBorderWidth()
							- (table.getGridLineWidth() * table.getColumnCount());
					if (table.getVerticalBar().isVisible()) {
						tablesize = tablesize - table.getVerticalBar().getSize().x;
					}
					tablesize = tablesize - table.getColumn(0).getWidth();
					tablesize = tablesize - table.getColumn(1).getWidth();
					table.getColumn(2).setWidth(tablesize);
				}
			}
		});
	}
}
