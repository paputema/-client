package kusoBotMakerClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import kusoBotMaker.ConsumerKeys;
import kusoBotMaker.Ttwitter_OAuth;
import twitter4j.TwitterException;


public class kbmOAuthWindow {
	private Text textField_PINcode;
	private Ttwitter_OAuth twitter_OAuth;
	private Text textConsumerKey;
	private Text textConsumerSecret;
	private Shell shell;
	private Browser browser;
	private Label lblConsumerkey;
	private Label lblConsumerSecret;
	private Button btnDefaultConsumer;
	private Group grpConsumer;

	/**
	 * @return
	 * @wbp.parser.entryPoint
	 */
	public Ttwitter_OAuth open() {
		try {
			//kbmOAuthWindow window = new kbmOAuthWindow();
			shell = new Shell(SWT.SHELL_TRIM | SWT.PRIMARY_MODAL);
			shell.setSize(658, 695);
			//window.open(shell);
			shell.setLayout(new FormLayout());

			browser = new Browser(shell, SWT.NONE);
			FormData fd_browser = new FormData();
			fd_browser.bottom = new FormAttachment(100, -10);
			fd_browser.right = new FormAttachment(100, -10);
			fd_browser.top = new FormAttachment(0, 216);
			fd_browser.left = new FormAttachment(0, 10);
			browser.setLayoutData(fd_browser);

			Button btnNewButton = new Button(shell, SWT.NONE);
			FormData fd_btnNewButton = new FormData();
			fd_btnNewButton.left = new FormAttachment(browser, 0, SWT.LEFT);
			fd_btnNewButton.right = new FormAttachment(0, 142);
			btnNewButton.setLayoutData(fd_btnNewButton);
			btnNewButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Open_OAuth();
				}
			});
			btnNewButton.setText("認証画面表示");

			Label lblNewLabel = new Label(shell, SWT.NONE);
			FormData fd_lblNewLabel = new FormData();
			fd_lblNewLabel.top = new FormAttachment(0, 181);
			fd_lblNewLabel.left = new FormAttachment(browser, 0, SWT.LEFT);
			lblNewLabel.setLayoutData(fd_lblNewLabel);
			lblNewLabel.setText("PINコード：");

			Button btnNewButton_1 = new Button(shell, SWT.NONE);
			FormData fd_btnNewButton_1 = new FormData();
			fd_btnNewButton_1.top = new FormAttachment(lblNewLabel, -5, SWT.TOP);
			btnNewButton_1.setLayoutData(fd_btnNewButton_1);
			btnNewButton_1.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (Comp_OAuth() && shell.isEnabled())
						shell.close();
					{
					}
				}
			});
			btnNewButton_1.setText("PINコード送信");

			textField_PINcode = new Text(shell, SWT.BORDER);
			fd_btnNewButton_1.left = new FormAttachment(textField_PINcode, 6);
			fd_btnNewButton.bottom = new FormAttachment(textField_PINcode, -6);
			FormData fd_textField_PINcode = new FormData();
			fd_textField_PINcode.left = new FormAttachment(lblNewLabel, 4);
			fd_textField_PINcode.right = new FormAttachment(lblNewLabel, 77, SWT.RIGHT);
			fd_textField_PINcode.top = new FormAttachment(0, 178);
			textField_PINcode.setLayoutData(fd_textField_PINcode);
			textField_PINcode.setText("");

			btnDefaultConsumer = new Button(shell, SWT.CHECK);
			btnDefaultConsumer.setTouchEnabled(true);
			btnDefaultConsumer.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

						textConsumerKey.setEnabled(!btnDefaultConsumer.getSelection());
						textConsumerSecret.setEnabled(!btnDefaultConsumer.getSelection());

				}
			});
			FormData fd_btnDefaultConsumer = new FormData();
			fd_btnDefaultConsumer.top = new FormAttachment(0, 10);
			fd_btnDefaultConsumer.left = new FormAttachment(browser, 0, SWT.LEFT);
			btnDefaultConsumer.setLayoutData(fd_btnDefaultConsumer);
			btnDefaultConsumer.setText("デフォルトのConsumerKeyを使う");

			grpConsumer = new Group(shell, SWT.NONE);
			grpConsumer.setText("Consumer取得");
			FormData fd_grpConsumer = new FormData();
			fd_grpConsumer.right = new FormAttachment(0, 461);
			fd_grpConsumer.bottom = new FormAttachment(btnNewButton, -6);
			fd_grpConsumer.top = new FormAttachment(btnDefaultConsumer, 6);
			fd_grpConsumer.left = new FormAttachment(0, 10);
			grpConsumer.setLayoutData(fd_grpConsumer);

			lblConsumerkey = new Label(grpConsumer, SWT.NONE);
			lblConsumerkey.setBounds(10, 56, 75, 15);
			lblConsumerkey.setText("ConsumerKey ");

			textConsumerKey = new Text(grpConsumer, SWT.BORDER);
			textConsumerKey.setBounds(101, 53, 205, 21);

			lblConsumerSecret = new Label(grpConsumer, SWT.NONE);
			lblConsumerSecret.setBounds(10, 81, 91, 15);
			lblConsumerSecret.setText("Consumer Secret ");

			textConsumerSecret = new Text(grpConsumer, SWT.BORDER);
			textConsumerSecret.setBounds(101, 78, 205, 21);

			Button btnNewButton_2 = new Button(grpConsumer, SWT.NONE);
			btnNewButton_2.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					browser.setUrl("https://apps.twitter.com/app/new");
					browser.update();
				}
			});
			btnNewButton_2.setBounds(10, 25, 75, 25);
			btnNewButton_2.setText("Twitter Apps");
		} catch (Exception e) {
			e.printStackTrace();
		}
		shell.open();
		shell.layout();

		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		if (twitter_OAuth != null) {
			return twitter_OAuth;
		}
		return null;
	}

	public void Open_OAuth() {
		try {
			if (btnDefaultConsumer.getSelection()) {
				twitter_OAuth = new Ttwitter_OAuth(ConsumerKeys.CONSUMERKEY, ConsumerKeys.CONSUMERSECRET);
			} else {
				twitter_OAuth = new Ttwitter_OAuth(textConsumerKey.getText(), textConsumerSecret.getText());
			}
			browser.setUrl(twitter_OAuth.open_requestToken_str());
			browser.update();
		} catch (TwitterException e) {
			// TODO: handle exception
			KbmImgeUtil.openMessageBox("CONSUMERKEYまたはCONSUMERSECRETが不正","失敗しました",shell);
		}
	}


	public boolean Comp_OAuth() {
		if (textField_PINcode.getText() == null || textField_PINcode.getText().equals("")) {
			return false;
		}
		Shell shell = new Shell();
		MessageBox box1 = new MessageBox(shell, SWT.OK);
		if (twitter_OAuth.pin(textField_PINcode.getText())) {
			box1.setMessage("認証に成功");
			box1.open();
			return true;
		} else {
			box1.setMessage("認証に失敗");
			box1.open();
			return false;
		}
	}
}
