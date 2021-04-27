package vico.com;

import org.bouncycastle.crypto.tls.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
 
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
 
public class TLSSocketConnectionFactory extends SSLSocketFactory {
 
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
 
    @Override
    public Socket createSocket(Socket socket, final String host, int port,
                               boolean arg3) throws IOException {
        if (socket == null) {
            socket = new Socket();
        }
        if (!socket.isConnected()) {
            socket.connect(new InetSocketAddress(host, port));
        }
 
        final TlsClientProtocol tlsClientProtocol = new     TlsClientProtocol(socket.getInputStream(), socket.getOutputStream(), new     SecureRandom());
 
        return _createSSLSocket(host, tlsClientProtocol);
    }
 
    @Override public String[] getDefaultCipherSuites() { return null; }
    @Override public String[] getSupportedCipherSuites() { return null; }
    @Override public Socket createSocket(String host, int port) throws IOException, UnknownHostException { return null; }
    @Override public Socket createSocket(InetAddress host, int port) throws IOException { return null; }
    @Override public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException { return null; }
    @Override public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException { return null; }
 
    private SSLSocket _createSSLSocket(final String host, final TlsClientProtocol tlsClientProtocol) {
        return new SSLSocket() {
            private java.security.cert.Certificate[] peertCerts;
 
            @Override public InputStream getInputStream() throws IOException { return tlsClientProtocol.getInputStream(); }
            @Override public OutputStream getOutputStream() throws IOException { return tlsClientProtocol.getOutputStream(); }
            @Override public synchronized void close() throws IOException { tlsClientProtocol.close(); }
            @Override public void addHandshakeCompletedListener( HandshakeCompletedListener arg0) { }
            @Override public boolean getEnableSessionCreation() { return false; }
            @Override public String[] getEnabledCipherSuites() { return null; }
            @Override public String[] getEnabledProtocols() { return null; }
            @Override public boolean getNeedClientAuth() { return false; }
 
            @Override
            public SSLSession getSession() {
                return new SSLSession() {
 
                    /*原本这些方法都是直接throw UnsupportedOperationException 导致看不到真实异常*/
                    @Override
                    public int getApplicationBufferSize() {
                        return 0;
                    }
                    @Override public String getCipherSuite() { return null; }
                    @Override public long getCreationTime() { return 0; }
                    @Override public byte[] getId() { return null; }
                    @Override public long getLastAccessedTime() { return 0; }
                    @Override public java.security.cert.Certificate[] getLocalCertificates() { return null; }
                    @Override public Principal getLocalPrincipal() { return null; }
                    @Override public int getPacketBufferSize() { return 0; }
                    @Override public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException { return null; }
                    @Override public java.security.cert.Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException { return peertCerts; }
                    @Override public String getPeerHost() { return null; }
                    @Override public int getPeerPort() { return 0; }
                    @Override public Principal getPeerPrincipal() throws SSLPeerUnverifiedException { return null; }
                    @Override public String getProtocol() { return null; }
                    @Override public SSLSessionContext getSessionContext() { return null; }
                    @Override public Object getValue(String arg0) { return null; }
                    @Override public String[] getValueNames() { return null; }
                    @Override public void invalidate() { return; }
                    @Override public boolean isValid() { return true; }
                    @Override public void putValue(String arg0, Object arg1) { return; }
                    @Override
                    public void removeValue(String arg0) {
                        return;
                    }
                };
            }
 
            @Override public String[] getSupportedProtocols() { return null; }
            @Override public boolean getUseClientMode() { return false; }
            @Override public boolean getWantClientAuth() { return false; }
            @Override public void removeHandshakeCompletedListener(HandshakeCompletedListener arg0) { }
            @Override public void setEnableSessionCreation(boolean arg0) { }
            @Override public void setEnabledCipherSuites(String[] arg0) { }
            @Override public void setEnabledProtocols(String[] arg0) { }
            @Override public void setNeedClientAuth(boolean arg0) { }
            @Override public void setUseClientMode(boolean arg0) { }
            @Override public void setWantClientAuth(boolean arg0) { }
            @Override public String[] getSupportedCipherSuites() { return null; }
 
            @Override
            public void startHandshake() throws IOException {
                tlsClientProtocol.connect(new DefaultTlsClient() {
 
                    @SuppressWarnings("unchecked")
                    @Override
                    public Hashtable<Integer, byte[]> getClientExtensions() throws IOException {
                        Hashtable<Integer, byte[]> clientExtensions = super.getClientExtensions();
                        if (clientExtensions == null) {
                            clientExtensions = new Hashtable<Integer, byte[]>();
                        }
 
                        //Add host_name
                        byte[] host_name = host.getBytes();
 
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        final DataOutputStream dos = new DataOutputStream(baos);
                        dos.writeShort(host_name.length + 3);
                        dos.writeByte(0);
                        dos.writeShort(host_name.length);
                        dos.write(host_name);
                        dos.close();
                        clientExtensions.put(ExtensionType.server_name, baos.toByteArray());
                        return clientExtensions;
                    }
 
                    @Override
                    public TlsAuthentication getAuthentication() throws IOException {
                        return new TlsAuthentication() {
 
                            @Override
                            public void notifyServerCertificate(Certificate serverCertificate) throws IOException {
                                try {
                                    KeyStore ks = _loadKeyStore();
 
                                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                                    List<java.security.cert.Certificate> certs = new LinkedList<java.security.cert.Certificate>();
                                    boolean trustedCertificate = false;
                                    for ( org.bouncycastle.asn1.x509.Certificate c : serverCertificate.getCertificateList()) {
                                        java.security.cert.Certificate cert = cf.generateCertificate(new ByteArrayInputStream(c.getEncoded()));
                                        certs.add(cert);
 
                                        String alias = ks.getCertificateAlias(cert);
                                        if(alias != null) {
                                            if (cert instanceof java.security.cert.X509Certificate) {
                                                try {
                                                    ( (java.security.cert.X509Certificate) cert).checkValidity();
                                                    trustedCertificate = true;
                                                } catch(CertificateExpiredException cee) {
                                                    // Accept all the certs!
                                                }
                                            }
                                        } else {
                                            // Accept all the certs!
                                        }
 
                                    }
                                    if (!trustedCertificate) {
                                        // Accept all the certs!
                                    }
                                    peertCerts = certs.toArray(new java.security.cert.Certificate[0]);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    throw new IOException(ex);
                                }
                            }
 
                            @Override
                            public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException {
                                return null;
                            }
 
                            private KeyStore _loadKeyStore() throws Exception {
                                FileInputStream trustStoreFis = null;
                                try {
                                    KeyStore localKeyStore = null;
 
                                    String trustStoreType = System.getProperty("javax.net.ssl.trustStoreType")!=null?System.getProperty("javax.net.ssl.trustStoreType"):KeyStore.getDefaultType();
                                    String trustStoreProvider = System.getProperty("javax.net.ssl.trustStoreProvider")!=null?System.getProperty("javax.net.ssl.trustStoreProvider"):"";
 
                                    if (trustStoreType.length() != 0) {
                                        if (trustStoreProvider.length() == 0) {
                                            localKeyStore = KeyStore.getInstance(trustStoreType);
                                        } else {
                                            localKeyStore = KeyStore.getInstance(trustStoreType, trustStoreProvider);
                                        }
 
                                        char[] keyStorePass = null;
                                        String str5 = System.getProperty("javax.net.ssl.trustStorePassword")!=null?System.getProperty("javax.net.ssl.trustStorePassword"):"";
 
                                        if (str5.length() != 0) {
                                            keyStorePass = str5.toCharArray();
                                        }
 
                                        localKeyStore.load(trustStoreFis, keyStorePass);
 
                                        if (keyStorePass != null) {
                                            for (int i = 0; i < keyStorePass.length; i++) {
                                                keyStorePass[i] = 0;
                                            }
                                        }
                                    }
                                    return localKeyStore;
                                } finally {
                                    if (trustStoreFis != null) {
                                        trustStoreFis.close();
                                    }
                                }
                            }
                        };
                    }
 
                });
            } // startHandshake
        };
    }
}