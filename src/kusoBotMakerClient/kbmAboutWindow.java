package kusoBotMakerClient;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class kbmAboutWindow {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			kbmAboutWindow window = new kbmAboutWindow();
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
		shell.setSize(490, 187);
		shell.setText("クソbotメーカーについて");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Yu Gothic UI", 12, SWT.BOLD));
		lblNewLabel.setBounds(10, 10, 191, 21);
		lblNewLabel.setText("Version : 1.0.0");

		Link link = new Link(shell, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				Desktop desktop = Desktop.getDesktop();
				String url = "http://twpf.jp/Chupacabras_MON";
				try {
					desktop.browse(new URI(url));
				} catch (IOException | URISyntaxException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		});
		link.setFont(SWTResourceManager.getFont("Yu Gothic UI", 13, SWT.NORMAL));
		link.setBounds(10, 61, 257, 23);
		link.setText("<a href=\"http://twpf.jp/Chupacabras_MON\">http://twpf.jp/Chupacabras_MON</a>");

		Link link_1 = new Link(shell, SWT.NONE);
		link_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {
				Desktop desktop = Desktop.getDesktop();
				String url = "https://twitter.com/Chupacabras_MON";
				try {
					desktop.browse(new URI(url));
				} catch (IOException | URISyntaxException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		});
		link_1.setFont(SWTResourceManager.getFont("Yu Gothic UI", 13, SWT.NORMAL));
		link_1.setBounds(10, 114, 398, 23);
		link_1.setText("アプリの作者：<a href=\"https://twitter.com/Chupacabras_MON\">https://twitter.com/Chupacabras_MON</a>");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
