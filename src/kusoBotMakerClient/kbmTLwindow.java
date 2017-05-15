package kusoBotMakerClient;

import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import kusoBotMaker.KbmUtil;
import kusoBotMakerClient.kbmMainWindow.BotAccountOnWindow;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserStreamAdapter;

public class kbmTLwindow {
	private static final String STATUS = "status";
	private static final String USER = "user";

	public class BotAccountUserStreamAdapter extends UserStreamAdapter {
		@Override
		public void onStatus(Status status) {
			// TODO 自動生成されたメソッド・スタブ
			super.onStatus(status);
			addStatus(status, true);
			if (shell.isDisposed()) {
				System.out.println(status.getText());
				botAccountOnWindow.twitterStream.removeListener(this);
			}
		}
	}

	BotAccountOnWindow botAccountOnWindow;
	private Table table;
	private TableColumn tableColumnUser;
	private TableColumn tableColumnTime;
	private TableColumn tableColumnTweets;
	private Shell shell;
	private Display display;

	/**
	 * Open the window.
	 *
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell parentshell, BotAccountOnWindow botAccountOnWindow) {
		display = Display.getDefault();
		shell = new Shell(parentshell, SWT.SHELL_TRIM | SWT.MODELESS);
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent paramControlEvent) {
				tableReSaize();
			}
		});
		this.botAccountOnWindow = botAccountOnWindow;
		shell.setSize(700, 800);
		shell.setText("タイムライン：" + botAccountOnWindow.user.getName());
		shell.setLayout(new FormLayout());

		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(table, 291, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(table, 18);
		fd_composite.right = new FormAttachment(table, 0, SWT.RIGHT);
		fd_composite.left = new FormAttachment(table, 0, SWT.LEFT);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);

		FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(100, -10);
		fd_table.top = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(100, -10);
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableColumnUser = new TableColumn(table, SWT.NONE);
		tableColumnUser.setWidth(130);
		tableColumnUser.setText("ユーザー");

		tableColumnTime = new TableColumn(table, SWT.NONE);
		tableColumnTime.setWidth(118);
		tableColumnTime.setText("時間");

		tableColumnTweets = new TableColumn(table, SWT.NONE);
		tableColumnTweets.setWidth(300);
		tableColumnTweets.setText("ツイート");

		Menu menu = new Menu(table);
		table.setMenu(menu);

		MenuItem mnUnFollow = new MenuItem(menu, SWT.NONE);
		mnUnFollow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				unFollow();
			}
		});
		mnUnFollow.setText("フォロー解除");

		MenuItem mntmBlock = new MenuItem(menu, SWT.NONE);
		mntmBlock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				block();
			}
		});
		mntmBlock.setText("ブロック");

		getTl();
		botAccountOnWindow.twitterStream.addListener(new BotAccountUserStreamAdapter());
		botAccountOnWindow.twitterStream.user();

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	void addStatus(Status status, Boolean Insert) {
		checkAsyncExec(new Runnable() {
			public void run() {
				if (table.isDisposed() || display.isDisposed()) {
					return;
				}
				User user = status.getUser();
				KbmUtil.setUser(user);
				TableItem tableItem;
				if (Insert) {
					tableItem = new TableItem(table, 0, 0);
				} else {
					tableItem = new TableItem(table, 0);
				}
				tableItem.setImage(0, KbmImgeUtil.getImage(user.getBiggerProfileImageURLHttps()));
				tableItem.setText(0, user.getName());
				tableItem.setText(1, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(status.getCreatedAt()));
				tableItem.setData(USER, user);
				tableItem.setData(STATUS, status);

				if (status.isRetweet()) {
					Status rtstatus = status.getRetweetedStatus();
					User rtuser = rtstatus.getUser();
					KbmUtil.setUser(rtuser);
					tableItem.setImage(2, KbmImgeUtil.getImage(rtuser.getBiggerProfileImageURLHttps()));
					tableItem.setText(2, "RT:[" + rtuser.getName() + "]\n" + rtstatus.getText());

				} else {
					tableItem.setText(2, status.getText());
				}
			}
		});
	}

	private User getUser() {
		int index = table.getSelectionIndex();
		if (index == -1) {
			return null;
		}
		return (User) table.getItem(index).getData(USER);
	}

	private void unFollow() {
		User user = getUser();
		if (user == null) {
			return;
		}
		try {
			botAccountOnWindow.twitter.destroyFriendship(user.getId());
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			botAccountOnWindow.onTwitterException(e);
		}
	}

	private void block() {
		User user = getUser();
		if (user == null) {
			return;
		}
		try {
			botAccountOnWindow.twitter.createBlock(user.getId());
			//KbmUtil.BlockAll(user.getId());
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			botAccountOnWindow.onTwitterException(e);
		}
	}

	private void getTl() {

		try {
			RateLimitStatus ratelimit = botAccountOnWindow.twitter.getRateLimitStatus().get("/statuses/home_timeline");
			if (ratelimit.getRemaining() < 2) {
				return;
			}
			/*
			 * for (Map.Entry<String, RateLimitStatus> ite :
			 * botAccountOnWindow.twitter.getRateLimitStatus().entrySet()) {
			 * System.out.println(ite.getKey() + "	:	" +
			 * ite.getValue().getRemaining() + "回	＆	" +
			 * ite.getValue().getSecondsUntilReset() + " 秒"); }
			 */
			checkAsyncExec(new Runnable() {
				public void run() {
					// table.clearAll();
				}
			});
			for (twitter4j.Status status : botAccountOnWindow.twitter.getHomeTimeline()) {
				addStatus(status, false);
			}
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		tableReSaize();

	}

	private void tableReSaize() {
		checkAsyncExec(new Runnable() {
			public void run() {
				if (table.isDisposed() || display.isDisposed()) {
					return;
				}
				// int tableWidth = shell.getSize().x - fd_table.left.offset +
				// fd_table.right.offset;
				int tableWidth = table.getSize().x;
				if (table.getVerticalBar().isEnabled()) {
					tableWidth = tableWidth - table.getVerticalBar().getSize().x;
				}
				tableColumnTweets.setWidth(tableWidth - tableColumnTime.getWidth() - tableColumnUser.getWidth());
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
