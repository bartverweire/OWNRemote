package be.bartverweire.ownremote;

import be.bartverweire.ownremote.server.WebServer;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

	static final 	String 		TAG 	= "OWNRemote";
	
	private 		WebView 	webView;
    private			Params	 	params;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_main);
		webView = (WebView) findViewById(R.id.mainWebView);
        webView.setWebViewClient(new OWNRemoteWebViewClient(this));
        webView.setWebChromeClient(new OWNRemoteWebChromeClient());
        WebView.setWebContentsDebuggingEnabled(true);
        
        WebSettings webViewSettings = webView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webViewSettings.setAllowFileAccess(true);
        webViewSettings.setAllowContentAccess(true);
        webViewSettings.setAllowFileAccessFromFileURLs(true);
        webViewSettings.setAllowUniversalAccessFromFileURLs(true);
        
        Params.initPrefs(this);
        params = Params.getInstance();
        
        WebServer server = new WebServer(this, params.getPort());
        server.start();
        
        webView.loadUrl("file:///android_asset/index.html");
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	private void showMessage(CharSequence function, CharSequence text, boolean success) {
		//Context context = getApplicationContext();
		 
		int duration = success ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
		
	};
	
	private class OWNRemoteWebViewClient extends WebViewClient {
	    
		private Context context;
		public OWNRemoteWebViewClient(Context context) {
			super();
		
			this.context = context;
		}

		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        return false;
	    }

		/** 
		 * @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
		 */
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.i(TAG, "Error loading webview");
			showMessage("loadWebView", "Error loading web page", true);
		}

		/** 
		 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
		 */
		@Override
		public void onPageFinished(WebView webView, String url) {
			Log.i(TAG, "Finished loading webview");
			Params.initPrefs(this.context);
		}
	}
	
	private class OWNRemoteWebChromeClient extends WebChromeClient {

		/** 
		 * @see android.webkit.WebChromeClient#onConsoleMessage(android.webkit.ConsoleMessage)
		 */
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			try {
				Log.d("Javascript", consoleMessage.message() + " -- line "
	                    + consoleMessage.lineNumber() + " of "
	                    + Uri.parse(consoleMessage.sourceId()).getPath() );
			} catch (Exception e) {
				
			}
			
			return true;
		}

		/** 
		 * @see android.webkit.WebChromeClient#onConsoleMessage(java.lang.String, int, java.lang.String)
		 */
		@Override
		public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			Log.d("Javascript", message + " -- line "
                    + lineNumber + " of "
                    + Uri.parse(sourceID).getPath() );
		}
	}
	
}
