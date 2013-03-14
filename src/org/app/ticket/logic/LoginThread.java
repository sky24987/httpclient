 package org.app.ticket.logic;
 
 import java.io.File;
 import java.security.KeyManagementException;
 import java.security.NoSuchAlgorithmException;
 import java.util.Random;
 import javax.swing.JCheckBox;
 import javax.swing.JLabel;
 import javax.swing.JPasswordField;
 import javax.swing.JTextArea;
 import javax.swing.JTextField;
 import org.app.ticket.autoimg.OCR;
 import org.app.ticket.bean.LoginDomain;
 import org.app.ticket.constants.Constants;
 import org.app.ticket.core.ClientCore;
 import org.app.ticket.core.MainWin;
 import org.app.ticket.util.StringUtil;
 import org.app.ticket.util.ToolUtil;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class LoginThread extends Thread
 {
   private static final Logger logger = LoggerFactory.getLogger(LoginThread.class);
   private MainWin mainWin;
   private boolean islogin = false;
   private String tessPath;
 
   public LoginThread()
   {
   }
 
   public LoginThread(MainWin mainWin, String tessPath)
   {
     this.mainWin = mainWin;
     this.tessPath = tessPath;
   }
 
   public void run()
   {
     try {
       int sum = 0;
       LoginDomain login = null;
       if (!this.mainWin.loginAuto.isSelected()) {
         String loginRand = getLoginRand();
         while (StringUtil.isEmptyString(loginRand)) {
           loginRand = getLoginRand();
         }
         login = new LoginDomain(loginRand, this.mainWin.username.getText(), this.mainWin.authcode.getText(), "Y", "N", this.mainWin.password.getText());
         String loginStr = ClientCore.Login(login);
         if (loginStr.contains("欢迎您登录中国铁路客户服务中心网站")) {
           this.islogin = true;
           MainWin.isLogin = true;
           this.mainWin.showMsg("登录成功！");
 
           new KeepCookieThread().start();
         } else {
           this.mainWin.showMsg("登录失败,请仔细检查验证码！");
 
           this.mainWin.initLoginImage();
         }
       }
       else {
         if ((StringUtil.isEmptyString(Constants.JSESSIONID_VALUE)) && (StringUtil.isEmptyString(Constants.BIGIPSERVEROTSWEB_VALUE))) {
           ClientCore.getCookie();
         }
         while (!this.islogin) {
           String url = "http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=sjrand&";
           double f = 1.000000016862384E-016D;
           Random random = new Random();
           f = random.nextDouble();
           url = url + f;
           ClientCore.getPassCode(url, this.mainWin.loginUrl);
 
           String loginRand = getLoginRand();
 
           if (loginRand == null)
           {
             continue;
           }
           logger.debug("-----------loginRand=" + loginRand);
 
           this.mainWin.code.setIcon(ToolUtil.getImageIcon(this.mainWin.loginUrl));
 
           String valCode = new OCR().recognizeText(this.tessPath, new File(this.mainWin.loginUrl), "jpg");
           valCode = valCode.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
 
           logger.debug("-------------valCode" + valCode);
 
           this.mainWin.authcode.setText(valCode);
 
           logger.debug("userName = " + this.mainWin.username.getText() + "|password = " + this.mainWin.password.getText());
 
           if (sum == 0)
             login = new LoginDomain(loginRand, this.mainWin.username.getText(), valCode, "Y", "N", this.mainWin.password.getText());
           else {
             login = new LoginDomain(loginRand, this.mainWin.username.getText(), valCode, "focus", "Y", "N", this.mainWin.password.getText());
           }
 
           sum++;
           String loginStr = ClientCore.Login(login);
           if (loginStr.contains("欢迎您登录中国铁路客户服务中心网站")) {
             this.islogin = true;
             MainWin.isLogin = true;
             break;
           }
 
           logger.debug("第" + sum + "次登录失败！");
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "第" + sum + "次登录失败！\n");
         }
         logger.debug("在第" + sum + "次终于登录成功了！");
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "在第" + sum + "次终于登录成功了！\n");
         this.mainWin.showMsg("登录成功！");
 
         new KeepCookieThread().start();
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 
   private static String getLoginRand() throws KeyManagementException, NoSuchAlgorithmException
   {
     String loginRand = ClientCore.loginAysnSuggest();
     if (loginRand.contains("loginRand")) {
       String[] l = loginRand.split(",");
       String[] t = l[0].split(":");
       loginRand = t[1].substring(1, t[1].length() - 1);
     }
     return loginRand;
   }
 }
