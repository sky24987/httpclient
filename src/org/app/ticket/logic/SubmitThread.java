 package org.app.ticket.logic;
 
 import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import org.app.ticket.autoimg.OCR;
import org.app.ticket.bean.LoginDomain;
import org.app.ticket.bean.OrderRequest;
import org.app.ticket.bean.TrainQueryInfo;
import org.app.ticket.bean.UserInfo;
import org.app.ticket.core.ClientCore;
import org.app.ticket.core.MainWin;
import org.app.ticket.msg.ResManager;
import org.app.ticket.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
 public class SubmitThread extends Thread
 {
   private static final Logger logger = LoggerFactory.getLogger(SubmitThread.class);
   private MainWin mainWin;
   private boolean islogin = false;
   private String tessPath;
   private List<TrainQueryInfo> trainQueryInfoList;
   private List<UserInfo> userInfos;
   private OrderRequest req;
   private TrainQueryInfo trainQueryInfo;
   private static boolean isSuccess = false;
 
   private int sum = 0;
 
   public SubmitThread()
   {
   }
 
   public SubmitThread(List<UserInfo> userInfos, OrderRequest req, MainWin mainWin) {
     this.userInfos = userInfos;
     this.req = req;
     this.mainWin = mainWin;
   }
 
   public void run()
   {
     while (!isSuccess) {
       this.mainWin.getStartButton().setText(ResManager.getString("RobotTicket.btn.stop"));
       try {
         int sum = 0;
         LoginDomain login = null;
         if (!this.mainWin.loginAuto.isSelected()) {
           String loginRand = getLoginRand();
           login = new LoginDomain(loginRand, this.mainWin.username.getText(), this.mainWin.authcode.getText(), "Y", "N", this.mainWin.password.getText());
           String loginStr = ClientCore.Login(login);
           if (loginStr.contains("您最后一次登录时间为")) {
             this.islogin = true;
             MainWin.isLogin = true;
             this.mainWin.showMsg("登录成功！");
           } else {
             this.mainWin.showMsg("登录失败,请仔细坚持验证码！");
           }
         } else {
           ClientCore.getCookie();
           while (!this.islogin) {
             String url = "http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=sjrand&";
             double f = 1.000000016862384E-016D;
             Random random = new Random();
             f = random.nextDouble();
             url = url + f;
             System.out.println("url = " + url);
             ClientCore.getPassCode(url, this.mainWin.loginUrl);
 
             String loginRand = getLoginRand();
 
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
             if (loginStr.contains("您最后一次登录时间为")) {
               this.islogin = true;
               MainWin.isLogin = true;
               break;
             }
 
             logger.debug("第" + sum + "次登录失败！");
             this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "第" + sum + "次登录失败！\n");
           }
         }
         logger.debug("在第" + sum + "次终于登录成功了！");
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "在第" + sum + "次终于登录成功了！\n");
         this.mainWin.showMsg("登录成功！");
         while (true)
         {
           if (MainWin.isLogin)
           {
             this.trainQueryInfoList = ClientCore.queryTrain(this.req);
             this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "火车信息\n");
             if (this.trainQueryInfoList.size() > 0) {
               for (TrainQueryInfo t : this.trainQueryInfoList) {
                 this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + t.toString() + "\n");
               }
             }
 
             this.trainQueryInfo = new AutoGetTrainInfo(this.trainQueryInfoList, this.mainWin, this.userInfos).getSeattrainQueryInfo();
 
             ClientCore.submitOrderRequest(this.trainQueryInfo, this.req);
 
             String path = this.mainWin.submitUrl;
             ClientCore.getPassCode("http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=randp", path);
 
             String valCode = new OCR().recognizeText(this.tessPath, new File(path), "jpg");
             valCode = valCode.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
             System.out.println("-------------valCode = " + valCode);
 
             String msg = ClientCore.confirmSingleForQueueOrder(this.trainQueryInfo, this.req, this.userInfos, valCode, null);
 
             logger.debug("最后输出消息:" + valCode + "----------" + msg);
             if (msg.contains("验证码")) {
               this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "验证码错误！\n");
             }
             if (msg.contains("Y")) {
               isSuccess = true;
               this.mainWin.showMsg("订票成功!");
             }
             continue;
           }
         }
       } catch (Exception e) {
         e.printStackTrace();
       }
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

