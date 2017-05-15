package kusoBotMakerClient;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.TouchListener;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import kusoBotMaker.KbmUtil;
import kusoBotMakerClient.kbmMainWindow.BotAccountOnWindow;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

public class KbmMyTweetWindow {
protected static final String USER = "user";
protected static final String STATUS = "status";
BotAccountOnWindow botAccount;
private Table table;
private Display display;
private TableColumn tableColumnTweet;
private TableColumn tableColumnFav;
private TableColumn tableColumnRT;
private TableColumn tableColumnAtCreate;
private Spinner spinnerRT;
private Spinner spinnerFAV;
private Button button;
private Shell shell;
private ScheduledFuture<?>  getMyTweetFuture;
private ScheduledExecutorService scheduledexec;

private ProgressBar progressBar;
	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell parentshell,BotAccountOnWindow botAccount) {
		if(botAccount == null)
		{
			return;
		}
		display = Display.getDefault();
		this.botAccount = botAccount;
		shell = new Shell(parentshell,SWT.SHELL_TRIM | SWT.MODELESS);
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				getMyTweetFuture.cancel(true);
				scheduledexec.shutdown();
				scheduledexec.shutdownNow();
			}
		});
		scheduledexec = Executors.newSingleThreadScheduledExecutor();
		shell.addTouchListener(new TouchListener() {
			public void touch(TouchEvent arg0) {
				tableReSaize();
			}
		});
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				tableReSaize();
			}
		});
		shell.setSize(636, 642);
		shell.setText("過去のツイート:" + botAccount.user.getName());
		shell.setLayout(new FormLayout());

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(100, -10);
		fd_table.left = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(100, -10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableColumnAtCreate = new TableColumn(table, SWT.NONE);
		tableColumnAtCreate.setWidth(100);
		tableColumnAtCreate.setText("日時");

		tableColumnRT = new TableColumn(table, SWT.NONE);
		tableColumnRT.setWidth(100);
		tableColumnRT.setText("RT");

		tableColumnFav = new TableColumn(table, SWT.NONE);
		tableColumnFav.setWidth(100);
		tableColumnFav.setText("いいね");

		tableColumnTweet = new TableColumn(table, SWT.NONE);
		tableColumnTweet.setWidth(100);
		tableColumnTweet.setText("ツイート");

		Group group = new Group(shell, SWT.NONE);
		fd_table.top = new FormAttachment(group, 3);

		Menu menu = new Menu(table);
		table.setMenu(menu);

		MenuItem mntmRt = new MenuItem(menu, SWT.NONE);
		mntmRt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getRt(table.getSelection());
			}
		});
		mntmRt.setText("RTしたユーザーを取得");
		group.setText("絞込");
		FormData fd_group = new FormData();
		fd_group.left = new FormAttachment(0, 10);
		fd_group.right = new FormAttachment(100, -10);
		fd_group.bottom = new FormAttachment(0, 99);
		fd_group.top = new FormAttachment(0, 10);
		group.setLayoutData(fd_group);

		button = new Button(group, SWT.CHECK);
		button.setBounds(10, 24, 119, 16);
		button.setText("自分のつぶやきのみ");

		spinnerRT = new Spinner(group, SWT.BORDER);
		spinnerRT.setMaximum(1000);
		spinnerRT.setBounds(212, 22, 44, 22);

		spinnerFAV = new Spinner(group, SWT.BORDER);
		spinnerFAV.setMaximum(1000);
		spinnerFAV.setBounds(212, 52, 44, 22);

		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(151, 24, 55, 15);
		lblNewLabel.setText("RT");

		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(151, 55, 55, 15);
		lblNewLabel_1.setText("FAV");

		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(getMyTweetFuture != null && !getMyTweetFuture.isDone())
				{
					if(!getMyTweetFuture.cancel(true))
					{
						getMyTweetFuture.cancel(false);
					}
				}
				ThreadGetMyTweet tgmt = new ThreadGetMyTweet(botAccount,table,button.getSelection(), spinnerRT.getSelection(), spinnerFAV.getSelection());
				getMyTweetFuture = scheduledexec.schedule(tgmt,0,TimeUnit.MINUTES);
			}
		});
		btnNewButton.setBounds(515, 52, 75, 25);
		btnNewButton.setText("取得");



		progressBar = new ProgressBar(group, SWT.NONE);
		progressBar.setBounds(420, 23, 170, 17);
		progressBar.setToolTipText("レートリミットスリープ中");
		progressBar.setVisible(false);

		shell.open();
		shell.layout();

		ThreadGetMyTweet tgmt = new ThreadGetMyTweet(botAccount,table);
		getMyTweetFuture = scheduledexec.schedule(tgmt,0,TimeUnit.MINUTES);


		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
		protected void finalize() throws Throwable {
			// TODO 自動生成されたメソッド・スタブ
			super.finalize();
			getMyTweetFuture.cancel(true);
			scheduledexec.shutdown();
			scheduledexec.shutdownNow();
		}
	private boolean checkAsyncExec(Runnable r) {
		if (display != null && !display.isDisposed()) {
			display.asyncExec(r);
			return true;
		} else {
			return false;
		}
	}

	class ThreadGetMyTweet extends Thread
	{
		public ThreadGetMyTweet(BotAccountOnWindow botAccount, Table table) {
			this(botAccount,table,false,0,0);
		}
		public ThreadGetMyTweet(BotAccountOnWindow botAccount, Table table, boolean myTweetOnly, int rtcount,
				int favcount) {
			super();
			this.botAccount = botAccount;
			this.table = table;
			this.myTweetOnly = myTweetOnly;
			this.rtcount = rtcount;
			this.favcount = favcount;
			setName("ツイート取得:" + botAccount.user.getName());
			setDaemon(true);
		}
		BotAccountOnWindow botAccount;
		Table table;
		boolean myTweetOnly;
		int rtcount ;
		int favcount;

		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			super.run();
			checkAsyncExec(new Runnable() {
				public void run() {
					table.removeAll();
				}
			});
			Paging paging = new Paging(1,3200);
			try {
				ResponseList<Status> statusList;
				do {
					KbmMyTweetWindow.this.sleep("/statuses/user_timeline");
					statusList = botAccount.twitter.getUserTimeline(paging);
					for (Status status : statusList) {
						if(!(myTweetOnly && status.isRetweet()) && status.getRetweetCount() >= rtcount && status.getFavoriteCount() >= favcount)
						{
							checkAsyncExec(new Runnable() {
								public void run() {
									addStatus(status, false);
								}
							});
						}
					}
					/*Map<String, RateLimitStatus> map = botAccount.twitter.getRateLimitStatus();
				for (Entry<String, RateLimitStatus> set : map.entrySet()) {
					System.out.println(set.getKey() + ":" + set.getValue().getRemaining());
				}*/
					paging.setPage(paging.getPage() + 1);
				} while (statusList.size() > 0);
			} catch (TwitterException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			tableReSaize();
		}
	}


	private void sleep(String api) throws InterruptedException
	{
		RateLimitStatus ratelimit;
		int sleeptime = 0;
		try {
			ratelimit = botAccount.twitter.getRateLimitStatus().get(api);

			if (ratelimit.getRemaining() < 1) {
				sleeptime = ratelimit.getSecondsUntilReset();
				if (sleeptime < 0) {
					sleeptime = 900;
				}
				System.out.println(api + ";" + ratelimit.getRemaining() + "回／"+ ratelimit.getLimit() + "回;" + ratelimit.getSecondsUntilReset()+ "秒");
			}
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
			ratelimit = e.getRateLimitStatus();
			System.out.println(ratelimit.toString() + ";" + ratelimit.getRemaining() + "回／"+ ratelimit.getLimit() + "回;" + ratelimit.getSecondsUntilReset()+ "秒");
			sleeptime = ratelimit.getSecondsUntilReset() ;
			if (sleeptime < 0) {
				sleeptime = 900;
			}
		}
		if(sleeptime > 0)
		{
			sleep(sleeptime);
		}
	}

	private void sleep(int sleepSeconds)
	{

		if(progressBar != null && !progressBar.isDisposed())
		{
			checkAsyncExec(new Runnable() {
				public void run() {
					progressBar.setVisible(true);
					progressBar.setMaximum(sleepSeconds);
				}
			});
		}

		for(int i = 0; i < sleepSeconds; i++)
		{
			int progsel = i;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			checkAsyncExec(new Runnable() {
				public void run() {
					if(progressBar != null && !progressBar.isDisposed())
					{
						progressBar.setSelection(progsel);
					}
				}
			});
		}

		if(progressBar != null && !progressBar.isDisposed())
		{
			checkAsyncExec(new Runnable() {
				public void run() {
					progressBar.setVisible(false);
				}
			});
		}


	}



	void addStatus(Status status,Boolean Insert)
	{
		checkAsyncExec(new Runnable() {
			public void run() {
				if (table.isDisposed() || display.isDisposed()) {
					return;
				}
				User user = status.getUser();
				KbmUtil.setUser(user);
				TableItem tableItem;
				if(Insert)
				{
					tableItem =new TableItem(table,  SWT.NONE,0);
				}
				else
				{
					tableItem =new TableItem(table,  SWT.NONE);
				}
				tableItem.setText(0,"" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(status.getCreatedAt()));
				tableItem.setText(1,"" + status.getRetweetCount());
				tableItem.setText(2,"" + status.getFavoriteCount());
				tableItem.setData(USER, user);
				tableItem.setData(STATUS, status);
				if(status.isRetweet())
				{
					Status rtstatus = status.getRetweetedStatus();
					User rtuser = rtstatus.getUser();
					KbmUtil.setUser(rtuser);
					tableItem.setImage(3,KbmImgeUtil.getImage(rtuser.getProfileImageURLHttps()));
					tableItem.setText(3,"RT:[" + rtuser.getName() + "]\n" + rtstatus.getText());
				}else
				{
					tableItem.setText(3,status.getText());
					//System.out.println( status.getText());
				}
				if(table.getItemCount() == 1)
				{
					tableReSaize();
				}
			}
		});
	}


	private void getRt(TableItem[] items) {
		// TODO 自動生成されたメソッド・スタブ
		for (TableItem tableItem : items) {
			try {
				KbmMyTweetWindow.this.sleep("/statuses/retweeters/ids");
			} catch (InterruptedException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			// sleep("/statuses/retweets/:id");
			Status get_status = (Status) tableItem.getData(STATUS);
			Status status ;
			if (get_status.isRetweet()) {
				status = get_status.getRetweetedStatus();
			}else
			{
				status = get_status;
			}
			if (status.getRetweetCount() > 0) {

				/*
				 * IDs ids = botAccount.twitter.getRetweeterIds(status.getId(),
				 * -1L); for (long id : ids.getIDs()) { System.out.println("id:"
				 * + id); }
				 */
				ResponseList<Status> statusList;
				try {
					statusList = botAccount.twitter.getRetweets(status.getId());


					checkAsyncExec(new Runnable() {
						public void run() {
							//new ThreadGetRT(status2,shell,botAccount).start();
							new KbmAfterRtWindow().open(shell, botAccount, status ,statusList);
						}
					});

				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}
	}

	private void tableReSaize()
	{
		checkAsyncExec(new Runnable() {
			public void run() {
				if (table.isDisposed() || display.isDisposed()) {
					return;
				}
				//int tableWidth = shell.getSize().x - fd_table.left.offset + fd_table.right.offset;
				int tableWidth = table.getSize().x;
				if(table.getVerticalBar().isEnabled())
				{
					tableWidth = tableWidth - table.getVerticalBar().getSize().x;
				}
				for (int i = 0; i < table.getColumnCount() - 1; i++) {

					table.getColumn(i).pack();
					tableWidth = tableWidth - table.getColumn(i).getWidth();
				}
				tableColumnTweet.setWidth(tableWidth -4);
			}
		});
	}
}
