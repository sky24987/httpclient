 package org.app.ticket.logic;
 
 import java.awt.Container;
 import java.awt.FlowLayout;
 import java.awt.GridLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.util.List;
 import javax.swing.JButton;
 import javax.swing.JCheckBox;
 import javax.swing.JDialog;
 import javax.swing.JLabel;
 import javax.swing.JPanel;
 import javax.swing.JTextArea;
 import javax.swing.JTextField;
 import org.app.ticket.bean.OrderRequest;
 import org.app.ticket.bean.TrainQueryInfo;
 import org.app.ticket.bean.UserInfo;
 import org.app.ticket.constants.Constants;
 import org.app.ticket.core.ClientCore;
 import org.app.ticket.core.MainWin;
 import org.app.ticket.msg.ResManager;
 import org.app.ticket.util.StringUtil;
 import org.app.ticket.util.ToolUtil;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class TicketThread extends Thread
 {
   private static final Logger logger = LoggerFactory.getLogger(TicketThread.class);
   private List<TrainQueryInfo> trainQueryInfoList;
   private List<UserInfo> userInfos;
   private OrderRequest req;
   private MainWin mainWin;
   private TrainQueryInfo trainQueryInfo;
   private boolean isSuccess = false;
 
   private int sum = 0;
 
   private int querySum = 0;
 
   public TicketThread()
   {
   }
 
   public TicketThread(List<UserInfo> userInfos, OrderRequest req, MainWin mainWin) {
     this.userInfos = userInfos;
     this.req = req;
     this.mainWin = mainWin;
   }
 
   public void run()
   {
     this.mainWin.getStartButton().setText(ResManager.getString("RobotTicket.btn.stop"));
     if (this.userInfos.size() > 5) {
       this.mainWin.showMsg("联系人不能大于5个!");
     }
     while (!this.isSuccess) {
       this.mainWin.isRunThread = true;
       if (this.mainWin.isStopRun) {
         this.mainWin.showMsg("停止线程成功!");
         this.mainWin.isStopRun = false;
         this.mainWin.isRunThread = false;
         break;
       }
       try
       {
         this.trainQueryInfoList = ClientCore.queryTrain(this.req);
         if (this.trainQueryInfoList.size() == 0) {
           this.mainWin.showMsg("请查看乘车日期是否输入正确!");
           this.mainWin.isRunThread = false;
           break;
         }
 
         this.trainQueryInfoList = ToolUtil.isSellPoint(this.trainQueryInfoList);
 
         if (this.trainQueryInfoList.size() == 0) {
           String wait = ResManager.getByKey("waitway");
           if (StringUtil.isEqualString("1", wait)) {
             if (this.querySum < 1) {
               this.mainWin.showMsg("您所要求预定的城市还未到放票时间点!");
             }
             this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "第" + ++this.querySum + "次查询时间点无票!\n");
 
             sleep(Integer.parseInt(StringUtil.isEmptyString(ResManager.getByKey("sleeptime")) ? "1000" : ResManager.getByKey("sleeptime")));
             continue;
           }
 
           this.mainWin.showMsg("您所要求预定的城市还未到放票时间点!");
           this.mainWin.isRunThread = false;
           break;
         }
 
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "火车信息\n");
         if (this.trainQueryInfoList.size() > 0) {
           for (TrainQueryInfo t : this.trainQueryInfoList) {
             this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + t.toString() + "\n");
           }
         }
 
         this.trainQueryInfo = new AutoGetTrainInfo(this.trainQueryInfoList, this.mainWin, this.userInfos).getSeattrainQueryInfo();
         if (this.trainQueryInfo == null) {
          // this.mainWin.showMsg("指定列车和非指定列车均无票,请通过其它途径购买或稍后在尝试!");
           //this.mainWin.isRunThread = false;
           //this.mainWin.getStartButton().setText(ResManager.getString("RobotTicket.btn.start"));
          // break;
        	 //没有票
        	 continue;
         }
 
         ClientCore.submitOrderRequest(this.trainQueryInfo, this.req);
         if (this.mainWin.isAutoCode.isSelected());
         final JDialog randcodeDialog = new JDialog(this.mainWin, "输入验证码", true);
         randcodeDialog.setSize(200, 150);
         randcodeDialog.setLocationRelativeTo(this.mainWin);
         randcodeDialog.setResizable(false);
 
         JLabel l_randcode = new JLabel("请输入验证码:", 0);
 
         final JTextField t_randcode = new JTextField(10);
         final JButton btn_randcode = new JButton("");
         String path = this.mainWin.submitUrl;
         ClientCore.getPassCode("http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=randp", path);
         btn_randcode.setIcon(ToolUtil.getImageIcon(path));
 
         btn_randcode.addActionListener(new ActionListener()
         {
           public void actionPerformed(ActionEvent e) {
             try {
               String path = TicketThread.this.mainWin.submitUrl;
               ClientCore.getPassCode("http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=randp", path);
               btn_randcode.setIcon(ToolUtil.getImageIcon(path));
               TicketThread.logger.debug("获取订单验证码----");
             } catch (Exception e1) {
               e1.printStackTrace();
             }
           }
         });
         JPanel p_randcode = new JPanel();
         p_randcode.setLayout(new FlowLayout());
         p_randcode.add(t_randcode);
         p_randcode.add(btn_randcode);
 
         JButton btn_confirm = new JButton("提交");
         JPanel p_confirm = new JPanel();
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "第" + ++this.sum + "次提交订单.\n");
 
         btn_confirm.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ev) {
             String msg = "";
             String randcode = t_randcode.getText();
             if ((randcode == null) || (randcode.trim().length() != 4)) {
               t_randcode.setText("");
               t_randcode.grabFocus();
             } else {
               try {
                 if (TicketThread.this.trainQueryInfo != null)
                 {
                   msg = ClientCore.confirmSingleForQueueOrder(TicketThread.this.trainQueryInfo, TicketThread.this.req, TicketThread.this.userInfos, randcode, "http://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=checkOrderInfo&rand=");
                   TicketThread.logger.debug("最后输出消息:" + randcode + "----------" + msg);
                   if (msg.contains("验证码")) {
                     TicketThread.this.mainWin.messageOut.setText(TicketThread.this.mainWin.messageOut.getText() + "验证码错误！\n");
                   }
                   else
                     msg = ClientCore.confirmSingleForQueueOrder(TicketThread.this.trainQueryInfo, TicketThread.this.req, TicketThread.this.userInfos, randcode, "http://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=confirmSingleForQueueOrder");
                 }
               }
               catch (Exception e) {
                 e.printStackTrace();
               }
               if (msg.contains("Y")) {
            	   isSuccess= true;
               //  TicketThread.access$502(TicketThread.this, true);
              //	 TicketThread.this.stop();
                 TicketThread.this.mainWin.isRunThread = false;
                 TicketThread.this.mainWin.showMsg("订票成功!");
 
                 TicketThread.this.mainWin.messageOut.setText(TicketThread.this.mainWin.messageOut.getText() + "Cookie:[" + "JSESSIONID" + "=" + Constants.JSESSIONID_VALUE + ";" + "BIGipServerotsweb" + "=" + Constants.BIGIPSERVEROTSWEB_VALUE + "]\n");
 
                 TicketThread.this.mainWin.getStartButton().setText(ResManager.getString("RobotTicket.btn.start"));
               }
               randcodeDialog.setVisible(false);
               randcodeDialog.dispose();
             }
           }
         });
         p_confirm.add(btn_confirm);
         Container container = randcodeDialog.getContentPane();
         container.setLayout(new GridLayout(3, 1));
         container.add(l_randcode);
         container.add(p_randcode);
         container.add(p_confirm);
         randcodeDialog.setVisible(true);
         logger.debug("线程休眠时间为:" + ResManager.getByKey("sleeptime"));
 
         sleep(Integer.parseInt(StringUtil.isEmptyString(ResManager.getByKey("sleeptime")) ? "1000" : ResManager.getByKey("sleeptime")));
       }
       catch (Exception e) {
         e.printStackTrace();
       }
     }
     this.mainWin.getStartButton().setText(ResManager.getString("RobotTicket.btn.start"));
   }
 }

