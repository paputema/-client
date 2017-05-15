package kusoBotMakerClient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class KbmImgeUtil {
	static private ImageRegistry imagereg;
	public KbmImgeUtil() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	static public Image getImage(String url){
		if(url == null)
		{
			return null;
		}
		if(imagereg == null)
		{
			imagereg  = new ImageRegistry();
		}
		Image rt = imagereg.get(url);
		if (rt == null) {
			try {
				rt = ImageDescriptor.createFromURL(new URL(url)).createImage();
				imagereg.put(url, rt);
			} catch (MalformedURLException e) {
				// TODO 自動生成された catch ブロック
				// URLでなくってファイルパスだった
				rt = ImageDescriptor.createFromFile(null, url).createImage();
				imagereg.put(url, rt);
			}
		}
		return rt;
	}
	static public Image getImageRe(String url){
		//imagereg.remove(url);
		return getImage(url);
	}
	static public Image getImageResize64(String url,Device device){
		return getImageResize(url,device,64,64);
	}

	static public Image getImageResize(String url,Device device,int x, int y){
		Image rt = imagereg.get(url + "_Resize" + x + "x" + y);
		if (rt == null)
		{
			rt = getImage(url);
			rt = new Image(device, rt.getImageData().scaledTo(x,y));
			imagereg.put(url + "_Resize" + x + "x" + y, rt);
		}
		return rt;
	}
	@Override
	protected void finalize() throws Throwable {
		// TODO 自動生成されたメソッド・スタブ
		imagereg.dispose();
		super.finalize();
	}
	public static String downloadIcon(String url, String filepath) {
		String sdf = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

		ImageData data = getImage(url).getImageData();
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { data };

		File file = new File("boticons" + "/" + filepath); // 保存先
		file.mkdirs();

		String path = file.getAbsolutePath() + "/" + sdf + "_" + "icon.jpg";
		loader.save(path, SWT.IMAGE_JPEG);

		return path;
	}
	static public int openMessageBox(String mes, String txt, Shell shell) {
		if(shell == null || shell.isDisposed())
		{
			return 0;
		}
		MessageBox msg;
		msg = new MessageBox(shell);
		msg.setMessage(mes);
		msg.setText(txt);
		return msg.open();
	}
	static public int  openMessageBox(String mes, String txt) {
		Display display = Display.getDefault();
		// TODO 自動生成されたメソッド・スタブ
		Shell shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setLayout(new GridLayout(1, false));

		shell.pack();

		shell.open();
		return openMessageBox(mes,txt,shell);
	}
}
