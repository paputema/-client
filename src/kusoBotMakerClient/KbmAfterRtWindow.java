package kusoBotMakerClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import kusoBotMakerClient.kbmMainWindow.BotAccountOnWindow;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;

public class KbmAfterRtWindow {



	private Shell shell;
	private TabFolder tabFolder;
	BotAccountOnWindow botAccount;
	private Display display;
	private Text text;

	/**
	 * Open the window.
	 * @param status
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell parentshell,BotAccountOnWindow botAccount,Status status, ResponseList<Status> statusList) {
		display = Display.getDefault();
		shell = new Shell(parentshell,SWT.SHELL_TRIM | SWT.MODELESS);
		shell.setSize(635, 693);
		shell.setText("SWT Application");
		shell.setLayout(new FormLayout());

		this.botAccount = botAccount;
		tabFolder = new TabFolder(shell, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(100, -10);
		fd_tabFolder.left = new FormAttachment(0, 10);
		fd_tabFolder.right = new FormAttachment(100, -10);
		tabFolder.setLayoutData(fd_tabFolder);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		fd_tabFolder.top = new FormAttachment(lblNewLabel, 6);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		fd_lblNewLabel.bottom = new FormAttachment(100, -570);
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(status.getText());
		lblNewLabel.setImage(KbmImgeUtil.getImage(status.getUser().getBiggerProfileImageURLHttps()));

		text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		fd_lblNewLabel.right = new FormAttachment(text, -6);
		text.setTouchEnabled(true);
		FormData fd_text = new FormData();
		fd_text.bottom = new FormAttachment(tabFolder, -6);
		fd_text.right = new FormAttachment(100, -10);
		fd_text.top = new FormAttachment(0, 10);
		fd_text.left = new FormAttachment(0, 90);
		text.setLayoutData(fd_text);
		text.setText( status.getUser().getName() + "\n" + status.getText());

		shell.open();
		shell.layout();

		ThreadGetAfRt afRt = new ThreadGetAfRt(statusList,botAccount,tabFolder);
		afRt.start();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private boolean checkAsyncExec(Runnable r) {
		if (!display.isDisposed()) {
			display.asyncExec(r);
			return true;
		} else {
			return false;
		}
	}

	class ThreadGetAfRt extends Thread {
		public ThreadGetAfRt(ResponseList<Status> statusList, BotAccountOnWindow botAccount, TabFolder tabFolder) {
			super();
			this.statusList = statusList;
			this.botAccount = botAccount;
			this.tabFolder = tabFolder;
			setDaemon(true);
			setName("RT取得");
		}

		ResponseList<Status> statusList;
		BotAccountOnWindow botAccount;
		 TabFolder tabFolder;
		@Override
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			super.run();
			for (Status rtStatus : statusList) {
				User rtuser = rtStatus.getUser();

				checkAsyncExec(new Runnable() {
					public void run() {
						TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
						KbmAfterRtComposite kbmAfterRtComposite = new KbmAfterRtComposite(tabFolder, SWT.NONE,
								botAccount, rtuser, rtStatus);
						tbtmNewItem.setControl(kbmAfterRtComposite);
						tbtmNewItem.setText(rtuser.getName());
					}
				});

			}
		}

	}
}
