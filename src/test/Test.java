package test;

import lava.Main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Test {
	public static void main(String[] args) throws Exception{
		Main.main(new String[]{"D:\\xpq\\workspace\\lava\\src\\test\\test.lava"});

		//genLink();

		//System.out.println((""+new Object()));


		//System.out.println((System.currentTimeMillis()-151852227922l)/(1000*60*60*24*360));
	}

	public static void  genLink() throws NoSuchAlgorithmException {
		long time=System.currentTimeMillis();
		String sign=MD51("time="+time+"97B32EA2FAF55CDC");
		//String url="http://114.215.202.134:19007//payment/v1/dayincome/2017-09-04?time="+time+"&sign="+sign;
		String url="https://ebank.payworth.net/payment/v1/dayincome/2017-09-25?time="+time+"&sign="+sign;
		System.out.println(url);
	}

	public final static String MD51(String pwd) throws NoSuchAlgorithmException {
		char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

			byte[] btInput = pwd.getBytes();

			MessageDigest mdInst = MessageDigest.getInstance("MD5");

			mdInst.update(btInput);


			byte[] md = mdInst.digest();


			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) { // i = 0
				byte byte0 = md[i]; //95
				str[k++] = md5String[byte0 >>> 4 & 0xf]; // 5
				str[k++] = md5String[byte0 & 0xf]; // F
			}

			return new String(str);
	}

}
