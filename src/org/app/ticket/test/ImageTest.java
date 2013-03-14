 package org.app.ticket.test;
 
 import java.security.KeyManagementException;
 import java.security.NoSuchAlgorithmException;
 import org.app.ticket.core.ClientCore;
 
 public class ImageTest
 {
   public static void main(String[] args)
     throws KeyManagementException, NoSuchAlgorithmException
   {
     org.app.ticket.constants.Constants.BIGIPSERVEROTSWEB_VALUE = "2631139594.62495.0000";
     org.app.ticket.constants.Constants.JSESSIONID_VALUE = "E326226420AA495EB8A51924FA4C66CF";
 
     ClientCore.getPassCode("http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=randp", System.getProperty("user.dir") + "\\image\\" + "passcode-submit.jpg");
   }
 }

