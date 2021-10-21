package jp.sanmarc.tinyinstaller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
	private final static String TAG = "TinyInstaller";

	TextView textView1;
	Button button1;

	File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView1 = findViewById(R.id.textView1);

//		// 絶対パス: /storage/emulated/0/Android/data/com.example.tinyinstaller/files/Download/fermata0.apk
//		// になっちまう。そこにshellからコピーして chmod 666 してインストールさせた
		//File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) , "fermata183.apk");
		File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) , "fermata-1.8.4-auto-universal-arm64.apk");

//		// getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) で  /storage/emulated/0/Download になる
//		// →これAPI29(Android10)で廃止された (実際はAPI28で使えなくなった？？) とりあえずAPI27(Android8.1)でビルドした
//		// それでもだめ。 manifest に android:requestLegacyExternalStorage="true" と書いておけば暫定的に通ったらしいがもうだめらしい
//		file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) , "mobile-release.apk");

		String path = String.format("絶対パス: %s", file.getAbsolutePath());
		Log.d(TAG, String.format("絶対パス: %s", path));
		if (file.exists()) {
			Log.d(TAG, "ファイルがある");
			textView1.setText(String.format("%s ファイルあった", path));
		} else {
			Log.d(TAG, "ファイルがないです");
			textView1.setText("ファイルがないです");
		}

		button1 = findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "button クリック");
				if (file != null) {
					if (file.exists()) {
						Log.d(TAG, "ファイルがあるのでインストールさせる");
						installAPK(file);
					} else {
						Log.d(TAG, "ファイルがないです");
					}
				} else {
					Log.d(TAG, "File is null");
				}
			}
		});

	}

	private void installAPK(File file) {

		Intent intent;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
			intent.setData(getUri(file));
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
		} else {
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndTypeAndNormalize(Uri.fromFile(file), "application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
		intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, "com.android.vending");
		getApplicationContext().startActivity(intent);
	}


	public Uri getUri(File file) {
		return FileProvider.getUriForFile(
				getApplicationContext(),
				"jp.sanmarc.tinyinstaller.provider",
				file
		);
	}

}