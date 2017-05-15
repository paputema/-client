package kusoBotMakerClient;

import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import kusoBotMakerClient.kbmMainWindow.BotAccountOnWindow;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;

public class KbmAfterRtComposite extends Composite {

	private BotAccountOnWindow botAccount;
	private Table table;
	private User rtuser;
	private long rtId;
	private Label lblNewLabel;
	private TableColumn tblclmnNewCreate;
	private TableColumn tblclmnNeTweet;
	private Display display;
	private ProgressBar progressBar;
	private Status rtStatus;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public KbmAfterRtComposite(Composite parent, int style ,BotAccountOnWindow botAccount,User rtuser,Status rtStatus) {
		super(parent, style);
		this.botAccount = botAccount;
		this.rtuser = rtuser;
		this.rtId = rtStatus.getId();
		this.rtStatus = rtStatus;
		display = Display.getDefault();

		setLayout(new FormLayout());


		lblNewLabel = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		fd_lblNewLabel.bottom = new FormAttachment(0, 74);
		fd_lblNewLabel.right = new FormAttachment(0, 74);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setImage(KbmImgeUtil.getImage(rtuser.getBiggerProfileImageURL()));


		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(0, 80);
		fd_table.bottom = new FormAttachment(100, -10);
		fd_table.right = new FormAttachment(100, -10);
		fd_table.left = new FormAttachment(0, 10);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tblclmnNeTweet = new TableColumn(table, SWT.NONE);
		tblclmnNeTweet.setWidth(250);
		tblclmnNeTweet.setText("ツイート");

		tblclmnNewCreate = new TableColumn(table, SWT.NONE);
		tblclmnNewCreate.setText("日時");
		tblclmnNewCreate.setToolTipText("");
		tblclmnNewCreate.setWidth(100);

		progressBar = new ProgressBar(this, SWT.NONE);
		FormData fd_progressBar = new FormData();
		fd_progressBar.bottom = new FormAttachment(lblNewLabel, 0, SWT.BOTTOM);
		fd_progressBar.right = new FormAttachment(table, 0, SWT.RIGHT);
		progressBar.setLayoutData(fd_progressBar);
		progressBar.setVisible(false);

		new ThreadGetTweet().start();

	}

	class ThreadGetTweet extends Thread{

		ThreadGetTweet()
		{
			super();
			setName("ThreadGetTweet");
			setDaemon(true);
		}
		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			super.run();
			setTweet();
		}
	}

	private void setTweet() {
		Paging pagingNewer = new Paging(1, 200);
		// paging.setSinceId(rtId);
		pagingNewer.setSinceId(rtId);
		ResponseList<Status> afterRtStatusList = null;
		ResponseList<Status> i_afterRtStatusList;
		do {
			i_afterRtStatusList = getUserTimeline(pagingNewer);
			if (afterRtStatusList == null) {
				afterRtStatusList = i_afterRtStatusList;
			} else {
				afterRtStatusList.addAll(i_afterRtStatusList);
			}
			pagingNewer.setPage(pagingNewer.getPage() + 1);
		} while (i_afterRtStatusList.size() > 0);

		int max = afterRtStatusList.size();
		int since = max -10;
		if (since < 0)
		{
			since = 0;
		}
		for (Status afterstatus : afterRtStatusList.subList(since,max)) {
			setTweet(afterstatus);
		}

		ResponseList<Status> beforeRtStatusList;
		Paging pagingOlder = new Paging(1, 10);
		pagingOlder.setMaxId(rtId);
		beforeRtStatusList =getUserTimeline( pagingOlder);

		for (Status afterstatus : beforeRtStatusList) {
			setTweet(afterstatus);
		}
	}


	ResponseList<Status> getUserTimeline (Paging paging)
	{
		try {
			ResponseList<Status> rt = botAccount.twitter.getUserTimeline(rtuser.getId(), paging);
			return rt;
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			//e.printStackTrace();
			switch (e.getErrorCode()) {
			case 88:
				RateLimitStatus ratelimit = e.getRateLimitStatus();
				int sleeptime = ratelimit.getSecondsUntilReset() ;
				System.out.println(ratelimit.toString() + ";" + ratelimit.getRemaining() + "回／"+ ratelimit.getLimit() + "回;" + ratelimit.getSecondsUntilReset()+ "秒");
				sleep(sleeptime);
			default:
				return getUserTimeline(paging);
			}
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



	void setTweet(Status i_afterstatus)
	{
		checkAsyncExec(new Runnable() {
			public void run() {
				Status afterstatus;
				if(table == null || table.isDisposed())
				{
					return;
				}
				TableItem tableItem = new TableItem(table, SWT.NONE);
				if (i_afterstatus.isRetweet()) {
					afterstatus = i_afterstatus.getRetweetedStatus();
					tableItem.setImage(KbmImgeUtil.getImage(afterstatus.getUser().getProfileImageURLHttps()));
				}
				else
				{
					afterstatus = i_afterstatus;
				}
				tableItem.setText(afterstatus.getText());
				tableItem.setText(1,
						"" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(afterstatus.getCreatedAt()));
				if(rtStatus.getRetweetedStatus().getId() == afterstatus.getId() || (afterstatus.isRetweet() && rtStatus.getRetweetedStatus().getId() == afterstatus.getRetweetedStatus().getId()))
				{
					org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(display, 255, 0, 0);
					tableItem.setBackground(color);
				}
			}
		});
	}

/*
	private void sleep(String api) throws InterruptedException
	{
			RateLimitStatus ratelimit;
			try {
				ratelimit = botAccount.twitter.getRateLimitStatus().get(api);

			if (ratelimit.getRemaining() < 1) {
				long sleeptime = ratelimit.getSecondsUntilReset() * 1000;
				if (sleeptime < 0) {
					sleeptime = 900 * 1000;
				}
				System.out.println(api + ";" + ratelimit.getRemaining() + "回／"+ ratelimit.getLimit() + "回;" + ratelimit.getSecondsUntilReset()+ "秒");
				Thread.sleep(sleeptime);
			}
			} catch (TwitterException e) {
				// TODO 自動生成された catch ブロック
				//e.printStackTrace();
				ratelimit = e.getRateLimitStatus();
				System.out.println(ratelimit.toString() + ";" + ratelimit.getRemaining() + "回／"+ ratelimit.getLimit() + "回;" + ratelimit.getSecondsUntilReset()+ "秒");
				long sleeptime = ratelimit.getSecondsUntilReset() * 1000;
				if (sleeptime < 0) {
					sleeptime = 900 * 1000;
				}
				Thread.sleep(sleeptime);
			}
	}
	*/
	private boolean checkAsyncExec(Runnable r) {
		if (display != null && !display.isDisposed()) {
			display.asyncExec(r);
			return true;
		} else {
			return false;
		}
	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
