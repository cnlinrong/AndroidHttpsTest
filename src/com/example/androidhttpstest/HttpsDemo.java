package com.example.androidhttpstest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class HttpsDemo {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, CertificateException,
			KeyStoreException, KeyManagementException, UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(new FileInputStream("D:/keystore/yayun_test.p12"), "ce205key".toCharArray());
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, "ce205key".toCharArray());
//		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//		X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(new FileInputStream("D:/keystore/server.crt"));
//		KeyStore trustKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//		trustKeyStore.load(null);
//		trustKeyStore.setCertificateEntry(certificate.getSubjectX500Principal().getName(), certificate);
		KeyStore trustKeyStore = KeyStore.getInstance("JKS");
		trustKeyStore.load(new FileInputStream("D:/keystore/lawapp_server_keystore.jks"), "123456".toCharArray());
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(trustKeyStore);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		URL url = new URL("https://192.168.0.205:8031/law/if/user/update");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (connection instanceof HttpsURLConnection) {
			((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
			((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
				
				@Override
				public boolean verify(String hostname, SSLSession session) {
					System.out.println(hostname);
					return true;
				}
				
			});
		}
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		StringBuffer params = new StringBuffer();
		params.append("id=").append("142").append("&relname=").append("张三").append("&email=").append("1243@qq.com");
		connection.getOutputStream().write(params.toString().getBytes());
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = "";
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		if (connection != null) {
			connection.disconnect();
		}
		if (br != null) {
			br.close();
		}
		if (is != null) {
			is.close();
		}
	}

}
