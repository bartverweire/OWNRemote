package be.bartverweire.ownremote.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

public class WebServer extends Thread {

	private final static String TAG = "WebServer";
	
	private int 	port;
	private Context context;
	
	public WebServer(Context context, int port) {
		super();
		this.context 	= context;
		this.port 		= port;
	}


	public void run() {
		super.run();
		
		AsyncHttpServer server = new AsyncHttpServer();

		server.get("/index.html", new HttpServerRequestCallback() {

			@Override
			public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
				Log.d(TAG, request.getPath());
				try {
					Uri uri = Uri.parse(request.getPath());
					response.sendFile(readFile(uri.getPathSegments()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.send("Error loading file");
				}
			}
		    
		});

		server.get("/build/.*", new HttpServerRequestCallback() {

			@Override
			public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
				try {
					Uri uri = Uri.parse(request.getPath());
					response.sendFile(readFile(uri.getPathSegments()));
				} catch (IOException e) {
					e.printStackTrace();
					response.send("Error loading file");
				} catch (Exception e) {
					e.printStackTrace();
					response.send("Error loading file");
				}
				Log.d(TAG, request.getPath());
			}
		    
		});

		server.listen(port);
	}
	
	private File readFile(List<String> pathSegments) throws IOException {
		if (pathSegments == null || pathSegments.size() == 0) {
			return null;
		}
		
		// get the path
		StringBuffer filePathBuffer = new StringBuffer(context.getFilesDir().toString());
		StringBuffer relativePathBuffer = new StringBuffer();
		int i = 0;
		for (String segment : pathSegments) {
			if (i++ > 0) {
				relativePathBuffer.append("/");
			}
			relativePathBuffer.append(segment);
		}
		filePathBuffer.append("/").append(relativePathBuffer);
		String filePath = filePathBuffer.toString();
		
		Log.d(TAG, "Requesting " + filePath);
		File file = new File(filePath);
		
		if (file.exists()) {
			Log.d(TAG, "File exists, simply returning");
			Log.d(TAG, "File is file ? " + file.isFile());
			Log.d(TAG, "File length: " + file.length());
			return file;
		}
		
		// create the necessary directories
		File parentDirectory = file.getParentFile();
		if (parentDirectory != null && !parentDirectory.exists()) {
			Log.d(TAG, "Creating directories " + parentDirectory.getAbsolutePath());
			parentDirectory.mkdirs();
		}
		
		String assetFilePath = relativePathBuffer.toString();
		Log.d(TAG, "Reading from asset " + assetFilePath);
		
		InputStream is = context.getAssets().open(assetFilePath);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		while (is.read(buffer) > 0) {
			fos.write(buffer);
		}
		fos.close();
		is.close();
		
		Log.d(TAG, "Asset written to file");
		return file;
	}
}
