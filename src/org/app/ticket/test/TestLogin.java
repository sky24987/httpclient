 package org.app.ticket.test;
 
 import java.io.File;
 import java.io.PrintStream;
 import java.util.Random;
 import org.app.ticket.autoimg.OCR;
 import org.app.ticket.bean.LoginDomain;
 import org.app.ticket.core.ClientCore;
 
 public class TestLogin
 {
   public static void main(String[] args)
     throws Exception
   {
     try
     {
       boolean islogin = false;
       int sum = 0;
       ClientCore.getCookie();
       while (!islogin)
       {
         String loginRand = ClientCore.loginAysnSuggest();
         if (loginRand.contains("loginRand")) {
           String[] l = loginRand.split(",");
           String[] t = l[0].split(":");
           loginRand = t[1].substring(1, t[1].length() - 1);
         }
 
         String path = System.getProperty("user.dir") + "\\image\\";
 
         String url = "http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=sjrand&";
         double f = 1.000000016862384E-016D;
         Random random = new Random();
         f = random.nextDouble();
         url = url + f;
 
         if (!new File(path).exists()) {
           new File(path).mkdir();
         }
         System.out.println("url = " + url);
         ClientCore.getPassCode(url, path + "passCode.jpg");
 
         System.out.println("-----------loginRand=" + loginRand);
 
         String valCode = new OCR().recognizeText(null, new File(path + "passCode.jpg"), "jpg");
         valCode = valCode.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
 
         System.out.println("-------------valCode" + valCode);
 
         LoginDomain login = null;
         if (sum == 0)
           login = new LoginDomain(loginRand, "hncdyj", valCode, "Y", "N", "yangjie123");
         else {
           login = new LoginDomain(loginRand, "hncdyj", valCode, "focus", "Y", "N", "yangjie123");
         }
 
         String loginStr = ClientCore.Login(login);
         if (loginStr.contains("您最后一次登录时间为")) {
           islogin = true;
           break;
         }
         sum++;
         System.out.println("第" + sum + "次登录失败！");
       }
       System.out.println("在第" + sum + "次终于登录成功了！");
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 }

