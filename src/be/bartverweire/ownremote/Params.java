package be.bartverweire.ownremote;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;

public class Params {
	
	private static 	Params	instance;
	
	private static final String PREFS_NAME 	= "OWNRemote";
	
	public 	static final String	PREFS_PORT			= "port";
	public 	static final String	PREFS_URL			= "url";
	
	public int			port;
	public String		url;
	
	private	Context		context;
	
	private Params(Context context) {
		this.context = context;
	};
	
	public static Params getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Params not initialized");
		}
		
		return instance;
	}
	
	public static void initPrefs(Context context) {
		if (instance == null) {
			instance = new Params(context);
			
			SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			Resources res = context.getResources();
			
			instance.port 			= prefs.getInt(PREFS_PORT, res.getInteger(R.integer.default_port));
			instance.url			= prefs.getString(PREFS_URL, context.getText(R.string.default_url).toString());
		}
	}
	
	public void savePrefs() {
		if (instance == null) {
			return;
		}
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		
		editor.putInt(PREFS_PORT, 		instance.port);
		editor.putString(PREFS_URL, 	instance.url);
		
		editor.commit();
	}
	
	public static void cleanup() {
		instance = null;
	}
	
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
