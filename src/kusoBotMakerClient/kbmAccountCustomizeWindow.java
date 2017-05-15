package kusoBotMakerClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class kbmAccountCustomizeWindow {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			kbmAccountCustomizeWindow window = new kbmAccountCustomizeWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(815, 536);
		shell.setText("SWT Application");
		shell.setLayout(new FormLayout());

		Group group = new Group(shell, SWT.NONE);
		group.setText("時間設定");
		FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(0, 121);
		fd_group.top = new FormAttachment(0, 10);
		fd_group.left = new FormAttachment(0, 10);
		fd_group.right = new FormAttachment(0, 272);
		group.setLayoutData(fd_group);

		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(10, 29, 108, 15);
		lblNewLabel.setText("通常投稿周期（分）");

		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setBounds(10, 62, 120, 15);
		lblNewLabel_1.setText("規制時休止時間（分）");

		Spinner spinNormalPostInterval = new Spinner(group, SWT.BORDER);
		spinNormalPostInterval.setMaximum(1440);
		spinNormalPostInterval.setBounds(141, 26, 61, 22);

		Spinner spinPauseInterval = new Spinner(group, SWT.BORDER);
		spinPauseInterval.setMaximum(1440);
		spinPauseInterval.setBounds(141, 59, 61, 22);

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
