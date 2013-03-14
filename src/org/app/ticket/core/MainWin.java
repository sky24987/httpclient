package org.app.ticket.core;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import org.app.ticket.autoimg.OCR;
import org.app.ticket.bean.OrderRequest;
import org.app.ticket.bean.TrainQueryInfo;
import org.app.ticket.bean.UserInfo;
import org.app.ticket.constants.Constants;
import org.app.ticket.logic.KeepCookieThread;
import org.app.ticket.logic.LoginThread;
import org.app.ticket.logic.SubmitThread;
import org.app.ticket.logic.TicketThread;
import org.app.ticket.msg.ResManager;
import org.app.ticket.util.DateUtil;
import org.app.ticket.util.StringUtil;
import org.app.ticket.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWin extends JFrame
{
  private static final Logger logger = LoggerFactory.getLogger(MainWin.class);
  private JFrame frame;
  public JTextField username;
  public JPasswordField password;
  public JLabel code;
  private JButton loginBtn;
  public JTextField authcode;
  public JCheckBox loginAuto;
  private JTextField jSession;
  private JTextField bigWeb;
  private JButton impSession;
  private JTextField linkman1_name;
  private JTextField linkman1_cardNo;
  private JTextField linkman1_mobile;
  private JTextField linkman2_name;
  private JTextField linkman2_cardNo;
  private JTextField linkman2_mobile;
  private JTextField linkman3_name;
  private JTextField linkman3_cardNo;
  private JTextField linkman3_mobile;
  private JCheckBox boxkTwoSeat;
  private JCheckBox hardSleePer;
  public JCheckBox isAutoCode;
  private JFormattedTextField txtStartDate;
  private JTextField formCode;
  private JTextField toCode;
  private JButton startButton;
  public JTextArea messageOut;
  private List<TrainQueryInfo> trainQueryInfo;
  private List<UserInfo> userInfoList;
  private OrderRequest req;
  public static String path;
  private MainWin mainWin = null;
  public String loginUrl;
  public String submitUrl;
  public static boolean isLogin = false;

//  private static String tessPath = null;

  public boolean isRunThread = false;

  public boolean isStopRun = false;

  public void initLayout()
  {
    this.frame = new JFrame(ResManager.getString("RobotTicket.main.msg"));
    ImageIcon ico = ResManager.createImageIcon("logo.jpg");
    this.frame.setIconImage(ico.getImage());
    this.frame.setBounds(50, 50, 670, 640);
    this.frame.setResizable(false);
    this.frame.setDefaultCloseOperation(3);
    this.frame.setLocationRelativeTo(null);
    ToolTipManager.sharedInstance().setInitialDelay(0);
    this.frame.getContentPane().setLayout(null);

    this.frame.addWindowListener(new WindowListener()
    {
      public void windowOpened(WindowEvent e)
      {
      }

      public void windowIconified(WindowEvent e)
      {
      }

      public void windowDeiconified(WindowEvent e)
      {
      }

      public void windowDeactivated(WindowEvent e)
      {
      }

      public void windowClosing(WindowEvent e)
      {
        try {
          ToolUtil.getUserInfo(MainWin.path, "UI.dat", new Object[] { MainWin.this.username, MainWin.this.password, MainWin.this.formCode, MainWin.this.toCode,
        		  MainWin.this.linkman1_cardNo,MainWin.this.linkman1_mobile, MainWin.this.linkman1_name,
        		  MainWin.this.linkman2_cardNo,MainWin.this.linkman2_mobile, MainWin.this.linkman2_name,
        		  MainWin.this.linkman3_cardNo,MainWin.this.linkman3_mobile, MainWin.this.linkman3_name});
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }

      public void windowClosed(WindowEvent e)
      {
      }

      public void windowActivated(WindowEvent e)
      {
      }
    });
    JPanel panel_o = new JPanel();
    panel_o.setBounds(10, 12, 650, 54);
    this.frame.getContentPane().add(panel_o);
    panel_o.setLayout(null);
    panel_o.setBorder(new TitledBorder(ResManager.getString("RobotTicket.panel.userinfo")));

    JLabel label_o = new JLabel(ResManager.getString("RobotTicket.label.user_name"));
    label_o.setBounds(10, 26, 40, 15);
    panel_o.add(label_o);
    label_o.setHorizontalAlignment(4);

    this.username = new JTextField();
    this.username.setName("username");
    this.username.setToolTipText(ResManager.getString("RobotTicket.label.user_name"));
    this.username.setBounds(60, 23, 100, 21);
    panel_o.add(this.username);
    this.username.setColumns(10);

    JLabel label_o1 = new JLabel(ResManager.getString("RobotTicket.label.password"));
    label_o1.setBounds(170, 26, 40, 15);
    panel_o.add(label_o1);
    label_o1.setHorizontalAlignment(4);

    this.password = new JPasswordField();
    this.password.setName("password");
    this.password.setToolTipText(ResManager.getString("RobotTicket.label.password"));
    this.password.setBounds(220, 23, 100, 21);
    panel_o.add(this.password);
    this.password.setColumns(10);

    this.code = new JLabel();
    this.code.setBounds(340, 20, 60, 20);
    this.code.setToolTipText("点我刷新验证码！");
    panel_o.add(this.code);
    this.code.setHorizontalAlignment(4);
    this.code.addMouseListener(new codeClick());

    this.authcode = new JTextField();
    this.authcode.setToolTipText(ResManager.getString("RobotTicket.label.codename"));
    this.authcode.setBounds(410, 23, 40, 21);
    panel_o.add(this.authcode);
    this.authcode.setColumns(10);

    this.loginAuto = new JCheckBox(ResManager.getString("RobotTicket.label.isAutoCode"));
    this.loginAuto.setBounds(430, 26, 110, 15);
    panel_o.add(this.loginAuto);
    this.loginAuto.setHorizontalAlignment(4);
    this.loginAuto.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if(loginAuto.isSelected()){
				
				getCodeText();
			}else{
				
				
			}
			
		}
	});
    
    
    

    this.loginBtn = new JButton();
    this.loginBtn.setText(ResManager.getString("RobotTicket.btn.login"));
    this.loginBtn.setBounds(560, 18, 65, 28);
    panel_o.add(this.loginBtn);
    this.loginBtn.addActionListener(new LoginBtn());

    JPanel panel = new JPanel();
    panel.setBounds(10, 70, 650, 54);
    this.frame.getContentPane().add(panel);
    panel.setLayout(null);
    panel.setBorder(new TitledBorder(ResManager.getString("RobotTicket.panel.session")));

    JLabel label = new JLabel(ResManager.getString("RobotTicket.label.jsession"));
    label.setBounds(10, 26, 60, 15);
    panel.add(label);
    label.setHorizontalAlignment(4);

    this.jSession = new JTextField();
    this.jSession.setToolTipText(ResManager.getString("RobotTicket.label.jsession"));
    this.jSession.setBounds(78, 23, 200, 21);
    panel.add(this.jSession);
    this.jSession.setColumns(10);

    JLabel label_1 = new JLabel(ResManager.getString("RobotTicket.label.BIGipServerotsweb"));
    label_1.setBounds(278, 26, 120, 15);
    panel.add(label_1);
    label_1.setHorizontalAlignment(4);

    this.bigWeb = new JTextField();
    this.bigWeb.setToolTipText(ResManager.getString("RobotTicket.label.BIGipServerotsweb"));
    this.bigWeb.setBounds(408, 23, 130, 21);
    panel.add(this.bigWeb);
    this.bigWeb.setColumns(10);

    this.impSession = new JButton();
    this.impSession.setText(ResManager.getString("RobotTicket.btn.import"));
    this.impSession.setBounds(560, 18, 65, 28);
    panel.add(this.impSession);
    this.impSession.addActionListener(new ImpSession());

    JPanel panel2 = new JPanel();
    panel2.setBounds(10, 140, 650, 210);
    this.frame.getContentPane().add(panel2);
    panel2.setLayout(null);
    panel2.setBorder(new TitledBorder(ResManager.getString("RobotTicket.panel.linkmaninfo")));

    JPanel panel3 = new JPanel();
    panel3.setBounds(20, 20, 610, 54);
    panel2.add(panel3);
    panel3.setLayout(null);
    panel3.setBorder(new TitledBorder(ResManager.getString("RobotTicket.panel.linkman1")));

    JLabel label_2 = new JLabel(ResManager.getString("RobotTicket.label.username"));
    label_2.setBounds(55, 26, 30, 15);
    panel3.add(label_2);
    label_2.setHorizontalAlignment(4);

    this.linkman1_name = new JTextField();
    this.linkman1_name.setName("linkman1_name");
    this.linkman1_name.setToolTipText(ResManager.getString("RobotTicket.label.username"));
    this.linkman1_name.setBounds(95, 23, 40, 21);
    panel3.add(this.linkman1_name);
    this.linkman1_name.setColumns(10);

    JLabel label_3 = new JLabel(ResManager.getString("RobotTicket.label.cardno"));
    label_3.setBounds(155, 26, 50, 15);
    panel3.add(label_3);
    label_3.setHorizontalAlignment(4);

    this.linkman1_cardNo = new JTextField();
    this.linkman1_cardNo.setName("linkman1_cardNo");
    this.linkman1_cardNo.setToolTipText(ResManager.getString("RobotTicket.label.cardno"));
    this.linkman1_cardNo.setBounds(215, 23, 150, 21);
    panel3.add(this.linkman1_cardNo);
    this.linkman1_cardNo.setColumns(10);

    JLabel label_4 = new JLabel(ResManager.getString("RobotTicket.label.mobilephone"));
    label_4.setBounds(375, 26, 40, 15);
    panel3.add(label_4);
    label_4.setHorizontalAlignment(4);

    this.linkman1_mobile = new JTextField();
    this.linkman1_mobile.setName("linkman1_mobile");
    this.linkman1_mobile.setToolTipText(ResManager.getString("RobotTicket.label.mobilephone"));
    this.linkman1_mobile.setBounds(425, 23, 100, 21);
    panel3.add(this.linkman1_mobile);
    this.linkman1_mobile.setColumns(10);

    JPanel panel4 = new JPanel();
    panel4.setBounds(20, 80, 610, 54);
    panel2.add(panel4);
    panel4.setLayout(null);
    panel4.setBorder(new TitledBorder(ResManager.getString("RobotTicket.panel.linkman2")));

    JLabel label_5 = new JLabel(ResManager.getString("RobotTicket.label.username"));
    label_5.setBounds(55, 26, 30, 15);
    panel4.add(label_5);
    label_5.setHorizontalAlignment(4);

    this.linkman2_name = new JTextField();
    this.linkman2_name.setName("linkman2_name");
    this.linkman2_name.setToolTipText(ResManager.getString("RobotTicket.label.username"));
    this.linkman2_name.setBounds(95, 23, 40, 21);
    panel4.add(this.linkman2_name);
    this.linkman2_name.setColumns(10);

    JLabel label_6 = new JLabel(ResManager.getString("RobotTicket.label.cardno"));
    label_6.setBounds(155, 26, 50, 15);
    panel4.add(label_6);
    label_6.setHorizontalAlignment(4);

    this.linkman2_cardNo = new JTextField();
    this.linkman2_cardNo.setName("linkman2_cardNo");
    this.linkman2_cardNo.setToolTipText(ResManager.getString("RobotTicket.label.cardno"));
    this.linkman2_cardNo.setBounds(215, 23, 150, 21);
    panel4.add(this.linkman2_cardNo);
    this.linkman2_cardNo.setColumns(10);

    JLabel label_7 = new JLabel(ResManager.getString("RobotTicket.label.mobilephone"));
    label_7.setBounds(375, 26, 40, 15);
    panel4.add(label_7);
    label_7.setHorizontalAlignment(4);

    this.linkman2_mobile = new JTextField();
    this.linkman2_mobile.setName("linkman2_mobile");
    this.linkman2_mobile.setToolTipText(ResManager.getString("RobotTicket.label.mobilephone"));
    this.linkman2_mobile.setBounds(425, 23, 100, 21);
    panel4.add(this.linkman2_mobile);
    this.linkman2_mobile.setColumns(10);

    JPanel panel5 = new JPanel();
    panel5.setBounds(20, 140, 610, 54);
    panel2.add(panel5);
    panel5.setLayout(null);
    panel5.setBorder(new TitledBorder(ResManager.getString("RobotTicket.panel.linkman3")));

    JLabel label_8 = new JLabel(ResManager.getString("RobotTicket.label.username"));
    label_8.setBounds(55, 26, 30, 15);
    panel5.add(label_8);
    label_8.setHorizontalAlignment(4);

    this.linkman3_name = new JTextField();
    this.linkman3_name.setName("linkman3_name");
    this.linkman3_name.setToolTipText(ResManager.getString("RobotTicket.label.username"));
    this.linkman3_name.setBounds(95, 23, 40, 21);
    panel5.add(this.linkman3_name);
    this.linkman3_name.setColumns(10);

    JLabel label_9 = new JLabel(ResManager.getString("RobotTicket.label.cardno"));
    label_9.setBounds(155, 26, 50, 15);
    panel5.add(label_9);
    label_9.setHorizontalAlignment(4);

    this.linkman3_cardNo = new JTextField();
    this.linkman3_cardNo.setName("linkman3_cardNo");
    this.linkman3_cardNo.setToolTipText(ResManager.getString("RobotTicket.label.cardno"));
    this.linkman3_cardNo.setBounds(215, 23, 150, 21);
    panel5.add(this.linkman3_cardNo);
    this.linkman3_cardNo.setColumns(10);

    JLabel label_10 = new JLabel(ResManager.getString("RobotTicket.label.mobilephone"));
    label_10.setBounds(375, 26, 40, 15);
    panel5.add(label_10);
    label_10.setHorizontalAlignment(4);

    this.linkman3_mobile = new JTextField();
    this.linkman3_mobile.setName("linkman3_mobile");
    this.linkman3_mobile.setToolTipText(ResManager.getString("RobotTicket.label.mobilephone"));
    this.linkman3_mobile.setBounds(425, 23, 100, 21);
    panel5.add(this.linkman3_mobile);
    this.linkman3_mobile.setColumns(10);

    JPanel panel6 = new JPanel();
    panel6.setBounds(10, 350, 650, 108);
    this.frame.getContentPane().add(panel6);
    panel6.setLayout(null);
    panel6.setBorder(new TitledBorder(ResManager.getString("RobotTicket.panel.configuration")));

    this.boxkTwoSeat = new JCheckBox(ResManager.getString("RobotTicket.label.boxkTwoSeat"));
    this.boxkTwoSeat.setBounds(5, 26, 100, 15);
    panel6.add(this.boxkTwoSeat);
    this.boxkTwoSeat.setHorizontalAlignment(4);

    this.hardSleePer = new JCheckBox(ResManager.getString("RobotTicket.label.hardSleePer"));
    this.hardSleePer.setBounds(140, 26, 100, 15);
    panel6.add(this.hardSleePer);
    this.hardSleePer.setHorizontalAlignment(4);

    this.isAutoCode = new JCheckBox(ResManager.getString("RobotTicket.label.isAutoCode"));
    this.isAutoCode.setBounds(420, 26, 120, 15);
    panel6.add(this.isAutoCode);
    this.isAutoCode.setHorizontalAlignment(4);

    JLabel label_15 = new JLabel(ResManager.getString("RobotTicket.label.txtStartDate"));
    label_15.setBounds(30, 70, 60, 13);
    panel6.add(label_15);
    label_15.setHorizontalAlignment(4);

    MaskFormatter mf = null;
    try {
      mf = new MaskFormatter("####-##-##");
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    this.txtStartDate = new JFormattedTextField(mf);
    this.txtStartDate.setToolTipText(ResManager.getString("RobotTicket.label.txtStartDate"));
    this.txtStartDate.setBounds(100, 68, 84, 21);
    panel6.add(this.txtStartDate);
    this.txtStartDate.setColumns(10);

    JLabel label_16 = new JLabel(ResManager.getString("RobotTicket.label.formCode"));
    label_16.setBounds(200, 70, 40, 17);
    panel6.add(label_16);
    label_16.setHorizontalAlignment(4);

    this.formCode = new JTextField();
    this.formCode.setToolTipText(ResManager.getString("RobotTicket.label.formCode"));
    this.formCode.setName("formCode");
    this.formCode.setBounds(250, 70, 60, 21);
    panel6.add(this.formCode);
    this.formCode.setColumns(10);

    JLabel label_17 = new JLabel(ResManager.getString("RobotTicket.label.toCode"));
    label_17.setBounds(320, 68, 60, 17);
    panel6.add(label_17);
    label_17.setHorizontalAlignment(4);

    this.toCode = new JTextField();
    this.toCode.setToolTipText(ResManager.getString("RobotTicket.label.toCode"));
    this.toCode.setName("toCode");
    this.toCode.setBounds(390, 68, 60, 21);
    panel6.add(this.toCode);
    this.toCode.setColumns(10);

    this.startButton = new JButton();
    this.startButton.setText(ResManager.getString("RobotTicket.btn.start"));
    this.startButton.setBounds(540, 64, 70, 28);
    panel6.add(this.startButton);
    this.startButton.addActionListener(new StartButton());

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(15, 460, 640, 145);
    scrollPane.setViewportBorder(new BevelBorder(1, null, null, null, null));
    scrollPane.setHorizontalScrollBarPolicy(31);
    this.frame.getContentPane().add(scrollPane);

    this.messageOut = new JTextArea();
    scrollPane.setViewportView(this.messageOut);
    this.messageOut.setText(ResManager.getString("RobotTicket.textarea.messageOut"));
    this.messageOut.setEditable(false);
    this.messageOut.setLineWrap(true);
  }

  public static void main(String[] arg0)
  {
  //  tessPath = arg0[0];

    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          MainWin window = new MainWin();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    try
    {
      File file = new File(path + "config.properties");

      if (!file.exists()) {
        return;
      }
      ResManager.initProperties(path + "config.properties");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public MainWin()
  {
    initLayout();
    try
    {
      ClientCore.getCookie();
    } catch (Exception e1) {
      e1.printStackTrace();
    }

    initLoginImage();
    this.mainWin = this;
    try {
      ToolUtil.setUserInfo(path, "UI.dat", new Object[] { this.username, this.password, this.linkman1_cardNo, this.linkman1_name, this.linkman1_mobile, this.linkman2_cardNo, this.linkman2_name, this.linkman2_mobile, this.linkman3_cardNo, this.linkman3_name, this.linkman3_mobile, this.formCode, this.toCode });
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("初始化界面赋值失败！");
    }
  }

  public void initLoginImage()
  {
    try {
      this.loginUrl = (path + "image" + File.separator);
      File file = new File(this.loginUrl);
      if (!file.exists()) {
        file.mkdirs();
      }

      if ((StringUtil.isEmptyString(Constants.JSESSIONID_VALUE)) && (StringUtil.isEmptyString(Constants.BIGIPSERVEROTSWEB_VALUE))) {
        ClientCore.getCookie();
      }
      this.loginUrl += "passcode-login.jpeg";
      this.submitUrl = (path + "image" + File.separator + "passcode-submit.jpeg");
      ClientCore.getPassCode("http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=sjrand", this.loginUrl);
      this.code.setIcon(ToolUtil.getImageIcon(this.loginUrl));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  
  //设置code
  public void getCodeText(){
    String valCode;
	try {
		 valCode = new OCR().recognizeText(path, new File(this.mainWin.loginUrl), "jpeg");
		 valCode = valCode.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
		 valCode = valCode.replaceAll(",", "");
		 valCode = valCode.replace("§", "");
		 valCode = valCode.replace("'","");
		 authcode.setText(valCode);
		 logger.debug("验证码识别："+valCode);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
  }
  
  
  public void showMsg(String msg)
  {
    JOptionPane.showMessageDialog(this.frame, msg);
  }

  public List<UserInfo> getUserInfo()
  {
    List list = new ArrayList();
    if ((StringUtil.isEmptyString(this.linkman1_cardNo.getText().trim())) || (!StringUtil.isEmptyString(this.linkman1_name.getText().trim()))) {
      if (!StringUtil.isEmptyString(this.linkman1_mobile.getText().trim())) {
        UserInfo userInfo1 = new UserInfo(this.linkman1_cardNo.getText().trim(), this.linkman1_name.getText().trim(), this.linkman1_mobile.getText().trim());
        list.add(userInfo1);
      } else {
        UserInfo userInfo1 = new UserInfo(this.linkman1_cardNo.getText().trim(), this.linkman1_name.getText().trim());
        list.add(userInfo1);
      }
    }
    if ((!StringUtil.isEmptyString(this.linkman2_cardNo.getText().trim())) || (!StringUtil.isEmptyString(this.linkman2_name.getText().trim()))) {
      if (!StringUtil.isEmptyString(this.linkman2_mobile.getText().trim())) {
        UserInfo userInfo2 = new UserInfo(this.linkman2_cardNo.getText().trim(), this.linkman2_name.getText().trim(), this.linkman2_mobile.getText().trim());
        list.add(userInfo2);
      } else {
        UserInfo userInfo2 = new UserInfo(this.linkman2_cardNo.getText().trim(), this.linkman2_name.getText().trim());
        list.add(userInfo2);
      }
    }
    if ((!StringUtil.isEmptyString(this.linkman3_cardNo.getText().trim())) || (!StringUtil.isEmptyString(this.linkman3_name.getText().trim())))
      if (!StringUtil.isEmptyString(this.linkman3_name.getText().trim())) {
        UserInfo userInfo3 = new UserInfo(this.linkman3_cardNo.getText().trim(), this.linkman3_name.getText().trim(), this.linkman3_mobile.getText().trim());
        list.add(userInfo3);
      } else {
        UserInfo userInfo3 = new UserInfo(this.linkman3_cardNo.getText().trim(), this.linkman3_name.getText().trim());
        list.add(userInfo3);
      }
    try
    {
      this.userInfoList = getUserInfo(list);
    } catch (IOException e) {
      e.printStackTrace();
      showMsg("配置文件中联系人解析错误,自动为您选择界面上的用户!");
      this.userInfoList = list;
      logger.error("解析配置中联系人错误,原因为:[" + e.getMessage() + "]");
    }
    return list;
  }

  private List<UserInfo> getUserInfo(List<UserInfo> list)
    throws IOException
  {
    String userString = ResManager.getByKey("userinfo");
    if (!StringUtil.isEmptyString(userString)) {
      String[] userList = userString.split("\\|");
      if (userList.length > 0) {
        for (int i = 0; i < userList.length; i++) {
          String[] user = userList[i].split("\\,");
          UserInfo userInfo = new UserInfo();
          if ((!StringUtil.isEmptyString(user[0])) && (!StringUtil.isEmptyString(user[1]))) {
            String username = new String(user[0].getBytes("ISO-8859-1"), "gbk");
            logger.debug("-------------------username = " + username);
            userInfo.setName(username);
            userInfo.setCardID(user[1]);
          }
          try {
            if (!StringUtil.isEmptyString(user[2]))
              userInfo.setPhone(user[2]);
          }
          catch (Exception e) {
            logger.error("数组越界!");
            userInfo.setPhone(null);
          }
          logger.debug("user:" + userInfo.toString());
          list.add(userInfo);
        }
      }
    }
    return list;
  }

  private OrderRequest getOrderRequest()
  {
    this.req = new OrderRequest();
    this.req.setFrom(this.formCode.getText().trim());
    this.req.setTo(this.toCode.getText().trim());
    this.req.setTrain_date(this.txtStartDate.getText().trim());
    this.req.setQuery_date(DateUtil.getCurDate());
    return this.req;
  }

  public boolean isBoxkTwoSeat()
  {
    return this.boxkTwoSeat.isSelected();
  }

  public boolean isHardSleePer()
  {
    return this.hardSleePer.isSelected();
  }

  public JButton getStartButton() {
    return this.startButton;
  }

  static
  {
    path = System.getProperty("java.class.path");
    int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
    int lastIndex = path.lastIndexOf(File.separator) + 1;
    path = path.substring(firstIndex, lastIndex);
  }

  class StartButton
    implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      JButton btn = (JButton)e.getSource();
      if (ResManager.getString("RobotTicket.btn.start").equals(btn.getText())) {
        List list = MainWin.this.getUserInfo();

        if (list.size() == 0) {
          MainWin.this.showMsg("请至少输入1位联系人信息!");
          return;
        }

        if (!MainWin.isLogin) {
          MainWin.this.showMsg("请登录!");
          return;
        }

        List msglist = ToolUtil.validateWidget(new Object[] { MainWin.this.username, MainWin.this.password, MainWin.this.authcode });
        if (msglist.size() > 0) {
          String msg = "";
          for (int i = 0; i < msglist.size(); i++) {
            msg = new StringBuilder().append(msg).append(i == msglist.size() - 1 ? (String)msglist.get(i) : new StringBuilder().append((String)msglist.get(i)).append(",").toString()).toString();
          }
          MainWin.this.showMsg(new StringBuilder().append(msg).append("不能为空！").toString());
          return;
        }

        MainWin.this.getOrderRequest();
        if (MainWin.this.isRunThread) {
          MainWin.this.showMsg("订票线程已启动!");
          return;
        }
        if ((MainWin.this.loginAuto.isSelected()) && (MainWin.this.isAutoCode.isSelected()))
          new SubmitThread(MainWin.this.userInfoList, MainWin.this.req, MainWin.this.mainWin).start();
        else {
          new TicketThread(MainWin.this.userInfoList, MainWin.this.req, MainWin.this.mainWin).start();
        }
      }
      if (ResManager.getString("RobotTicket.btn.stop").equals(btn.getText()))
        MainWin.this.isStopRun = true;
    }
  }

  class ImpSession
    implements ActionListener
  {
    ImpSession()
    {
    }

    public void actionPerformed(ActionEvent e)
    {
      JButton btn = (JButton)e.getSource();
      if (ResManager.getString("RobotTicket.btn.import").equals(btn.getText())) {
        Constants.BIGIPSERVEROTSWEB_VALUE = MainWin.this.bigWeb.getText().trim();
        Constants.JSESSIONID_VALUE = MainWin.this.jSession.getText().trim();
        if ((StringUtil.isEmptyString(MainWin.this.toCode.getText().trim())) && (StringUtil.isEmptyString(MainWin.this.formCode.getText().trim())) && ("-  -".equals(MainWin.this.txtStartDate.getText().trim()))) {
          MainWin.this.showMsg("请输入发站&到站&乘车日期验证session是否为登录过！");
        } else {
          OrderRequest req = new OrderRequest();
          req.setFrom(MainWin.this.formCode.getText().trim());
          req.setTo(MainWin.this.toCode.getText().trim());
          req.setTrain_date(MainWin.this.txtStartDate.getText().trim());
          try {
        	  ClientCore.queryTrain(req);
         //   MainWin.access$1702(MainWin.this, ClientCore.queryTrain(req));
         //   MainWin.this.
            if (MainWin.this.trainQueryInfo.size() > 0) {
              MainWin.this.isLogin = true;
              MainWin.this.showMsg("导入session成功!");
              MainWin.this.messageOut.setText(MainWin.this.messageOut.getText() + "本次一共为您筛选到" + MainWin.this.trainQueryInfo.size() + "趟列车信息\n");

              new KeepCookieThread().start();
            } else {
              MainWin.this.showMsg("导入session失败,请仔细检查session!");
            }
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }

  class LoginBtn
    implements ActionListener
  {
    LoginBtn()
    {
    }

    public void actionPerformed(ActionEvent e)
    {
      JButton btn = (JButton)e.getSource();
      if (ResManager.getString("RobotTicket.btn.login").equals(btn.getText())) {
        List list = null;
        if (MainWin.this.loginAuto.isSelected())
          list = ToolUtil.validateWidget(new Object[] { MainWin.this.username, MainWin.this.password });
        else {
          list = ToolUtil.validateWidget(new Object[] { MainWin.this.username, MainWin.this.password, MainWin.this.authcode });
        }
        if (list.size() > 0) {
          String msg = "";
          for (int i = 0; i < list.size(); i++) {
            msg = new StringBuilder().append(msg).append(i == list.size() - 1 ? (String)list.get(i) : new StringBuilder().append((String)list.get(i)).append(",").toString()).toString();
          }
          MainWin.this.showMsg(new StringBuilder().append(msg).append("不能为空！").toString());
          return;
        }

        new LoginThread(MainWin.this.mainWin, MainWin.path).start();
      }
    }
  }

  class codeClick
    implements MouseListener
  {

    public void mouseClicked(MouseEvent e)
    {
      MainWin.this.initLoginImage();
      getCodeText();
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }
  }
}