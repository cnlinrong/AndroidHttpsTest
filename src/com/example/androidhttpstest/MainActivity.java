package com.example.androidhttpstest;

import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView response_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		response_tv = (TextView) findViewById(R.id.response);
	}

	public void request(View v) throws Exception {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			
			@Override
			public boolean verify(String hostname, SSLSession session) {
				Log.e("HTTPS-TEST", hostname);
				return true;
			}
			
		});
		
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(getAssets().open("client_side.p12"), "123456".toCharArray());
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, "123456".toCharArray());
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(getAssets().open("server_side.cer"));
		KeyStore trustKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustKeyStore.load(null);
		trustKeyStore.setCertificateEntry(certificate.getSubjectX500Principal().getName(), certificate);
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(trustKeyStore);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack(null, sslSocketFactory));
		String url = "https://192.168.0.109:8443/https-demo/";
//		String url = "https://localhost:8443/https-demo/";
		StringRequest request = new StringRequest(Method.POST, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				response_tv.setText(response);
				Log.e("HttpsTest", response);
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("HttpsTest-Error", error.toString());
			}

		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("id", "142");
				params.put("relname", "张三123");
//				params.put("myphone", "13000000000");
				params.put("email", "1243@qq.com");
				return params;
			}

		};
		requestQueue.add(request);
	}

}
