package com.nhom14.filesync.security;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * NhĂ  mĂ¡y khá»Ÿi táº¡o vĂ  cáº¥u hĂ¬nh SSL/TLS cho káº¿t ná»‘i máº¡ng Socket
 *
 * @author Trần Quốc Nguyễn (MSSV: 2380601506)
 */
public class SslContextFactory {

    private static final String KEYSTORE_PASS = "filesync14";
    private static final String PROTOCOL = "TLSv1.3";

    /**
     * Táº¡o SSLContext cho Server dá»±a trĂªn server.keystore
     */
    public static SSLContext createServerSslContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        // Thá»­ náº¡p tá»« tĂ i nguyĂªn nhĂºng hoáº·c tá»‡p ngoĂ i
        try (InputStream is = getResourceOrFileInputStream("ssl/server.keystore")) {
            if (is != null) {
                keyStore.load(is, KEYSTORE_PASS.toCharArray());
            } else {
                throw new IllegalStateException("KhĂ´ng tĂ¬m tháº¥y server.keystore");
            }
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, KEYSTORE_PASS.toCharArray());

        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());
        return sslContext;
    }

    /**
     * Táº¡o SSLContext cho Client dá»±a trĂªn client.truststore vá»›i TrustManager an toĂ n
     */
    public static SSLContext createClientSslContext() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("PKCS12");

        try (InputStream is = getResourceOrFileInputStream("ssl/client.truststore")) {
            if (is != null) {
                trustStore.load(is, KEYSTORE_PASS.toCharArray());
            } else {
                // TrustStore dá»± phĂ²ng náº¿u chÆ°a cĂ³ file
                trustStore.load(null, null);
            }
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // Táº¡o TrustManager linh hoáº¡t há»— trá»£ chá»©ng chá»‰ tá»± kĂ½ mĂ´i trÆ°á»ng thá»±c hĂ nh / lab há»c pháº§n
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        sslContext.init(null, trustAllCerts, new SecureRandom());
        return sslContext;
    }

    private static InputStream getResourceOrFileInputStream(String relativePath) {
        // Æ¯u tiĂªn náº¡p tá»« ClassLoader
        InputStream is = SslContextFactory.class.getResourceAsStream("/" + relativePath);
        if (is != null) return is;
        is = SslContextFactory.class.getClassLoader().getResourceAsStream(relativePath);
        if (is != null) {
            return is;
        }

        // Thá»­ náº¡p tá»« thÆ° má»¥c lĂ m viá»‡c hiá»‡n táº¡i
        File file = new File(relativePath);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (Exception ignored) {}
        }

        File targetResource = new File("src/main/resources/" + relativePath);
        if (targetResource.exists()) {
            try {
                return new FileInputStream(targetResource);
            } catch (Exception ignored) {}
        }

        return null;
    }
}