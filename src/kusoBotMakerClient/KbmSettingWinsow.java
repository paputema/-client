package kusoBotMakerClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import kusoBotMakerClient.kbmMainWindow.BotAccountOnWindow;
import twitter4j.TwitterException;
import twitter4j.User;

public class KbmSettingWinsow {
	public KbmSettingWinsow(BotAccountOnWindow object) throws TwitterException {
		super();
		this.botAccount = object;
		if(object.user == null)
		{
			user = object.twitter.verifyCredentials();
		}else{
			user = object.user;
		}
	}
	User user;
	BotAccountOnWindow botAccount;
	private Spinner spinPauseTime;
	private Spinner spinNormalPostInterval;
	private Button btnRt;
	private Text textConsumers;


	/**
	 * Open the window.
	 * @throws TwitterException
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell parentshell) throws TwitterException {
		Display display = Display.getDefault();
		Shell shell = new Shell(parentshell,SWT.SHELL_TRIM | SWT.MODELESS);
		shell.setSize(450, 300);
		shell.setText(user.getName());
		shell.setLayout(new FormLayout());

		Label lblNewLabel = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.bottom = new FormAttachment(0, 110);
		fd_lblNewLabel.right = new FormAttachment(0, 110);
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("");
		lblNewLabel.setImage(KbmImgeUtil.getImage(user.getBiggerProfileImageURLHttps()));

		Button btnNewButton = new Button(shell, SWT.NONE);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.right = new FormAttachment(0, 155);
		fd_btnNewButton.top = new FormAttachment(0, 226);
		fd_btnNewButton.left = new FormAttachment(0, 10);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new kbmLogWindow().open(botAccount);
			}
		});
		btnNewButton.setText("ログ");

		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		FormData fd_btnNewButton_1 = new FormData();
		fd_btnNewButton_1.right = new FormAttachment(0, 155);
		fd_btnNewButton_1.top = new FormAttachment(0, 195);
		fd_btnNewButton_1.left = new FormAttachment(0, 10);
		btnNewButton_1.setLayoutData(fd_btnNewButton_1);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new KbmFollowerConfigWindow().open(botAccount);
			}
		});
		btnNewButton_1.setText("フォロー・フォロワー設定");

		Button btnOpenModeConf = new Button(shell, SWT.NONE);
		FormData fd_btnOpenModeConf = new FormData();
		fd_btnOpenModeConf.right = new FormAttachment(0, 155);
		fd_btnOpenModeConf.top = new FormAttachment(0, 164);
		fd_btnOpenModeConf.left = new FormAttachment(0, 10);
		btnOpenModeConf.setLayoutData(fd_btnOpenModeConf);
		btnOpenModeConf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KbmModeWindow kmw = new KbmModeWindow();
				kmw.open(shell,botAccount,user.getId());
			}
		});
		btnOpenModeConf.setText("モード設定");

		Button btnOpenTweetconf = new Button(shell, SWT.NONE);
		FormData fd_btnOpenTweetconf = new FormData();
		fd_btnOpenTweetconf.right = new FormAttachment(0, 155);
		fd_btnOpenTweetconf.top = new FormAttachment(0, 133);
		fd_btnOpenTweetconf.left = new FormAttachment(0, 10);
		btnOpenTweetconf.setLayoutData(fd_btnOpenTweetconf);
		btnOpenTweetconf.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KbmTweetListWindow ktc = new KbmTweetListWindow(botAccount);
				ktc.open();
			}
		});
		btnOpenTweetconf.setText("ツイート設定");

		Group group = new Group(shell, SWT.NONE);
		FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(lblNewLabel, 46, SWT.BOTTOM);
		fd_group.top = new FormAttachment(0, 10);
		fd_group.left = new FormAttachment(btnOpenTweetconf, 6);
		fd_group.right = new FormAttachment(100, -10);
		group.setLayoutData(fd_group);

		Label label = new Label(group, SWT.NONE);
		label.setBounds(10, 25, 108, 15);
		label.setText("通常投稿周期（分）");

		spinNormalPostInterval = new Spinner(group, SWT.BORDER);
		spinNormalPostInterval.setBounds(134, 22, 61, 22);
		spinNormalPostInterval.setMaximum(1440);
		spinNormalPostInterval.setSelection(botAccount.normalPostInterval.intValue());

		spinPauseTime = new Spinner(group, SWT.BORDER);
		spinPauseTime.setBounds(134, 50, 61, 22);
		spinPauseTime.setMaximum(1440);
		spinPauseTime.setSelection(botAccount.pauseTime.intValue());

		Label label_1 = new Label(group, SWT.NONE);
		label_1.setBounds(10, 53, 120, 15);
		label_1.setText("規制時休止時間（分）");

		Button btnNewButton_2 = new Button(group, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getSetting();
				botAccount.upDateBot();
				botAccount.stopBot();
				botAccount.startBot();
			}
		});
		btnNewButton_2.setBounds(134, 111, 75, 25);
		btnNewButton_2.setText("保存");

		btnRt = new Button(group, SWT.CHECK);
		btnRt.setBounds(10, 74, 92, 16);
		btnRt.setText("RTに反応");
		btnRt.setSelection(botAccount.replyRt);

		textConsumers = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		FormData fd_textConsumers = new FormData();
		fd_textConsumers.right = new FormAttachment(group, 0, SWT.RIGHT);
		fd_textConsumers.bottom = new FormAttachment(btnNewButton, 0, SWT.BOTTOM);
		fd_textConsumers.top = new FormAttachment(group, 8);
		fd_textConsumers.left = new FormAttachment(btnNewButton, 6);
		textConsumers.setLayoutData(fd_textConsumers);
		textConsumers.setText("consumerKey = " + botAccount.consumerKey  + "\r\n"
							+	"consumerSecret = "  + botAccount.consumerSecret + "\r\n"
							+	"Access_Token = " + botAccount.Access_Token + "\r\n"
							+	"Access_Token_Secret = " + botAccount.Access_Token_Secret + "\r\n" );
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	void getSetting()
	{
		botAccount.normalPostInterval = (long) spinNormalPostInterval.getSelection();
		botAccount.pauseTime = (long) spinPauseTime.getSelection();
		botAccount.replyRt = btnRt.getSelection();
	}
}
