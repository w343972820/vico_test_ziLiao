package vico.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.alibaba.fastjson.JSONObject;

public class HelloTttpTest {
	//private static final Logger logger = LoggerFactory.getLogger(HelloTttpTest.class);
	
	public static void main(String[] args) throws Exception {
		//java.lang.System.setProperty("https.protocols", "TLSv1.2");
		HelloTttpTest test1 = new HelloTttpTest();
		//HttpClient client = new HttpClient(); 
		//HttpClients client= new H
		
		test1.loginTest("15800000001", "aaaa123456");
	}
	
	 public void loginTest(String phone,String password) throws Exception{      
	       	String uri="https://api.fexvip.co/api/sys/login";  
	        JSONObject json = new JSONObject();
	        json.put("phoneormail",phone); 
	        json.put("password",password);

	        String result = "";
	        result = Send(json.toJSONString(),uri);
	               
	        System.out.println(result);
	        //logger.debug("结束，返回:"+result);
	        
	        
	       /* System.out.println(result);
	        JSONObject results=JSONObject.parseObject(result);
	        String code = results.getString("code");
	        String token="";
	        String userid="";
	        if ("1000".contains(code)){
	            JSONObject dataJson = results.getJSONObject("data");
	            JSONObject userJson = dataJson.getJSONObject("user");
	            userid=userJson.getString("id");
	            token=dataJson.getString("token");
	        }
	        return token+","+userid;*/
	        //System.out.println(result);
	    }
	
	
	public static String Send(String signData,String signURL) {
		 
		StringBuffer sb = new StringBuffer();
		URL myurl = null;
		try {
			myurl = new URL(signURL);
		} catch (MalformedURLException e1) {
			System.out.println("myurl error:"+e1);
		}
 	 
		HttpsURLConnection con = null;
		try {
		    con = (HttpsURLConnection) myurl.openConnection();
		    con.setSSLSocketFactory(new TLSSocketConnectionFactory());
		    /*用于解决host name wrong问题，重写主机验证方法，如果请求正常可以去掉*/
		    con.setHostnameVerifier(new HostnameVerifier(){
				@Override
				public boolean verify(String hostname, SSLSession session) {
					// TODO Auto-generated method stub
					return true;
				}			
		    });
	
		    System.setProperty("sun.net.client.defaultConnectTimeout","300000");  
		    System.setProperty("sun.net.client.defaultReadTimeout", "300000");
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setInstanceFollowRedirects(true);
			con.setRequestProperty("Content-type", "text/xml;charset=utf-8"); 
			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"); 
			System.out.println("start connect");
			con.connect();
			System.out.println("end connect");
		} catch (IOException e1) {
			System.err.println("HttpsURLConnection error:"+e1);
		}
 
		OutputStream out = null;
		//OutputStreamWriter os = null;
		try { 
	
			out = con.getOutputStream();
			//os = new OutputStreamWriter(out);
			out.write(signData.getBytes("utf-8"));
			out.flush();
			out.close();
			//os.write(signData);
		
			//os.flush();
		} catch (IOException e1) {
			System.out.println("IOException:"+e1);
			 
		}catch(Exception ex){
			System.out.println("HTTPS Exception:"+ex);
			System.out.println(ex.getStackTrace());
		}
		 
		//接收响应 
		
		try {
			
	        System.out.println("responsecode="+con.getResponseCode());
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			sb.append("");
			String s = reader.readLine();
			while(s!=null){
				sb.append(s);
				s=reader.readLine();
			} 
			reader.close();
			 
			con.disconnect();
		} catch (IOException ioex) {
			System.err.println("https write file error:"+ioex);
		}
 
		return sb.toString();
		 
	}
	
	
	
	

	
	

}
