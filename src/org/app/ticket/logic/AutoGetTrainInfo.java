 package org.app.ticket.logic;
 
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 import javax.swing.JTextArea;
 import org.app.ticket.bean.TrainQueryInfo;
 import org.app.ticket.bean.UserInfo;
 import org.app.ticket.constants.Constants;
 import org.app.ticket.core.MainWin;
 import org.app.ticket.msg.ResManager;
 import org.app.ticket.util.StringUtil;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class AutoGetTrainInfo
 {
   private static final Logger logger = LoggerFactory.getLogger(AutoGetTrainInfo.class);
   private List<TrainQueryInfo> trainQueryInfoList;
   private List<UserInfo> userInfoList;
   private MainWin mainWin;
   private String[] specificTrainKeys;
   private String[] specificTrainSeat;
   private static Map seatMap = null;
 
   private Map<String, TrainQueryInfo> specificTrains = new HashMap();
 
   private Map<String, TrainQueryInfo> specificSeatTrains = new HashMap();
 
   public AutoGetTrainInfo(List<TrainQueryInfo> trainQueryInfoList, MainWin mainWin, List<UserInfo> userInfoList) {
     this.trainQueryInfoList = trainQueryInfoList;
     this.mainWin = mainWin;
     this.userInfoList = userInfoList;
 
     this.specificTrainKeys = getKeys();
 
     this.specificTrainSeat = getSeatKeys();
     trainQueryInfoClass();
   }
 
   public void trainQueryInfoClass()
   {
     for (int j = 0; (j < this.specificTrainKeys.length) && 
       (!StringUtil.isEmptyString(this.specificTrainKeys[j])); j++)
     {
       for (int i = this.trainQueryInfoList.size() - 1; i >= 0; i--) {
         if (!this.specificTrainKeys[j].equalsIgnoreCase(((TrainQueryInfo)this.trainQueryInfoList.get(i)).getTrainNo()))
           continue;
         this.specificTrains.put(((TrainQueryInfo)this.trainQueryInfoList.get(i)).getTrainNo(), this.trainQueryInfoList.get(i));
         this.trainQueryInfoList.remove(i);
       }
 
     }
 
     for (int i = this.trainQueryInfoList.size() - 1; i >= 0; i--)
     {
       if (!StringUtil.isEmptyString(((TrainQueryInfo)this.trainQueryInfoList.get(i)).getMmStr())) {
         this.specificSeatTrains.put(((TrainQueryInfo)this.trainQueryInfoList.get(i)).getTrainNo(), this.trainQueryInfoList.get(i));
         this.trainQueryInfoList.remove(i);
       }
 
     }
 
     String specificTrain = "";
     for (Map.Entry key : this.specificTrains.entrySet()) {
       specificTrain = specificTrain + ((TrainQueryInfo)key.getValue()).getTrainNo() + ",";
     }
 
     String specificSeatTrain = "";
     for (Map.Entry key : this.specificSeatTrains.entrySet()) {
       specificSeatTrain = specificSeatTrain + ((TrainQueryInfo)key.getValue()).getTrainNo() + ",";
     }
 
     if (!StringUtil.isEmptyString(specificTrain)) {
       this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "您指定的列车为:" + specificTrain.substring(0, specificTrain.length() - 1) + "\n");
       logger.debug("指定列车信息:" + specificTrain.substring(0, specificTrain.length() - 1));
     }
 
     if (!StringUtil.isEmptyString(specificSeatTrain)) {
       this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "指定之外的列车为:" + specificSeatTrain.substring(0, specificSeatTrain.length() - 1) + "\n");
       logger.debug("指定之外列车信息:" + specificSeatTrain.substring(0, specificSeatTrain.length() - 1));
     }
   }
 
   public TrainQueryInfo getSeattrainQueryInfo()
   {
     boolean isAssign = false;
     TrainQueryInfo returninfo = null;
 
     if ((this.specificTrainKeys.length > 0) && (!StringUtil.isEmptyString(this.specificTrainKeys[0]))) {
       for (int i = 0; i < this.specificTrainKeys.length; i++) {
         TrainQueryInfo info = (TrainQueryInfo)this.specificTrains.get(this.specificTrainKeys[i]);
         if (info != null) {
           returninfo = getSeattrainQueryInfo(info);
         }
         if (returninfo != null) {
           return returninfo;
         }
       }
 
     }
 
     if (!isAssign) {
       for (Map.Entry map : this.specificSeatTrains.entrySet()) {
         TrainQueryInfo info = getSeattrainQueryInfo((TrainQueryInfo)map.getValue());
         if (info != null) {
           returninfo = info;
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "最终车次为:" + info.getTrainNo() + "\n");
           return returninfo;
         }
 
       }
 
       for (Map.Entry map : this.specificTrains.entrySet()) {
         TrainQueryInfo info = (TrainQueryInfo)map.getValue();
         String seat = Constants.NONE_SEAT;
         boolean isTicket = checkSeatIsTicket(((TrainQueryInfo)map.getValue()).getNone_seat());
         if (!isTicket) {
           returninfo = info;
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "指定列车和未指定列车均无有座位票!\n");
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动指定车次为:" + info.getTrainNo() + " ----- 自动选择席别为:" + seatMap.get(seat) + "\n");
           return returninfo;
         }
 
       }
 
       for (Map.Entry map : this.specificSeatTrains.entrySet()) {
         TrainQueryInfo info = (TrainQueryInfo)map.getValue();
         String seat = Constants.NONE_SEAT;
         boolean isTicket = checkSeatIsTicket(((TrainQueryInfo)map.getValue()).getNone_seat());
         if (!isTicket) {
           returninfo = info;
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "指定列车和未指定列车均无有座位票!\n");
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动未指定车次为:" + info.getTrainNo() + " ----- 自动选择席别为:" + seatMap.get(seat) + "\n");
           return returninfo;
         }
       }
     }
     return null;
   }
 
   public TrainQueryInfo getSeattrainQueryInfo(TrainQueryInfo info)
   {
     String seat = "";
 
     if ((this.specificTrainSeat.length > 0) && (!StringUtil.isEmptyString(this.specificTrainSeat[0]))) {
       for (int i = 0; i < this.specificTrainSeat.length; i++) {
         seat = this.specificTrainSeat[i];
         try
         {
           String seatCount = getSeatCount(seat, info);
           if (checkSeatIsTicket(seatCount)) {
             continue;
           }
           if (Integer.parseInt(seatCount) >= this.userInfoList.size()) {
             setUserSest(this.userInfoList, this.specificTrainSeat[i]);
             this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "您指定的座位席别为:" + seatMap.get(seat) + "\n");
             return info;
           }
         } catch (NumberFormatException ex) {
           setUserSest(this.userInfoList, this.specificTrainSeat[i]);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "您指定的座位席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
 
       }
 
     }
 
     if ((this.mainWin.isBoxkTwoSeat()) && 
       (!"--".equals(info.getTwo_seat())) && (!"无".equals(info.getTwo_seat()))) {
       try {
         if (Integer.parseInt(info.getTwo_seat()) >= this.userInfoList.size()) {
           seat = Constants.TWO_SEAT;
           setUserSest(this.userInfoList, Constants.TWO_SEAT);
           logger.debug("动车优先车次为:" + info.getTrainCode());
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.TWO_SEAT;
         setUserSest(this.userInfoList, Constants.TWO_SEAT);
         logger.debug("动车优先车次为:" + info.getTrainCode());
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
 
     }
 
     if ((this.mainWin.isHardSleePer()) && 
       (!"--".equals(info.getHard_sleeper())) && (!"无".equals(info.getHard_sleeper()))) {
       try {
         if (Integer.parseInt(info.getHard_sleeper()) >= this.userInfoList.size()) {
           seat = Constants.HARD_SLEEPER;
           setUserSest(this.userInfoList, Constants.HARD_SLEEPER);
           logger.debug("卧铺优先车次为:" + info.getTrainCode());
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.HARD_SLEEPER;
         setUserSest(this.userInfoList, Constants.HARD_SLEEPER);
         logger.debug("卧铺优先车次为:" + info.getTrainCode());
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
 
     }
 
     if ((!"--".equals(info.getTwo_seat())) && (!"无".equals(info.getTwo_seat()))) {
       try {
         if (Integer.parseInt(info.getTwo_seat()) >= this.userInfoList.size()) {
           seat = Constants.TWO_SEAT;
           setUserSest(this.userInfoList, Constants.TWO_SEAT);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.TWO_SEAT;
         setUserSest(this.userInfoList, Constants.TWO_SEAT);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
 
     if ((!"--".equals(info.getOne_seat())) && (!"无".equals(info.getOne_seat()))) {
       try {
         if (Integer.parseInt(info.getOne_seat()) >= this.userInfoList.size()) {
           seat = Constants.ONE_SEAT;
           setUserSest(this.userInfoList, Constants.ONE_SEAT);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.ONE_SEAT;
         setUserSest(this.userInfoList, Constants.ONE_SEAT);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
 
     if ((!"--".equals(info.getHard_sleeper())) && (!"无".equals(info.getHard_sleeper()))) {
       try {
         if (Integer.parseInt(info.getHard_sleeper()) >= this.userInfoList.size()) {
           seat = Constants.HARD_SLEEPER;
           setUserSest(this.userInfoList, Constants.HARD_SLEEPER);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.HARD_SLEEPER;
         setUserSest(this.userInfoList, Constants.HARD_SLEEPER);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
 
     if ((!"--".equals(info.getSoft_sleeper())) && (!"无".equals(info.getSoft_sleeper()))) {
       try {
         if (Integer.parseInt(info.getSoft_sleeper()) >= this.userInfoList.size()) {
           seat = Constants.SOFT_SLEEPER;
           setUserSest(this.userInfoList, Constants.SOFT_SLEEPER);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.SOFT_SLEEPER;
         setUserSest(this.userInfoList, Constants.SOFT_SLEEPER);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
 
     if ((!"--".equals(info.getSoft_seat())) && (!"无".equals(info.getSoft_seat()))) {
       try {
         if (Integer.parseInt(info.getSoft_seat()) >= this.userInfoList.size()) {
           seat = Constants.SOFT_SEAT;
           setUserSest(this.userInfoList, Constants.SOFT_SEAT);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.SOFT_SEAT;
         setUserSest(this.userInfoList, Constants.SOFT_SEAT);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
 
     if ((!"--".equals(info.getHard_seat())) && (!"无".equals(info.getHard_seat()))) {
       try {
         if (Integer.parseInt(info.getHard_seat()) >= this.userInfoList.size()) {
           seat = Constants.HARD_SEAT;
           setUserSest(this.userInfoList, Constants.HARD_SEAT);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.HARD_SEAT;
         setUserSest(this.userInfoList, Constants.HARD_SEAT);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
 
     if ((!"--".equals(info.getVag_sleeper())) && (!"无".equals(info.getVag_sleeper()))) {
       try {
         if (Integer.parseInt(info.getVag_sleeper()) >= this.userInfoList.size()) {
           seat = Constants.VAG_SLEEPER;
           setUserSest(this.userInfoList, Constants.VAG_SLEEPER);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.VAG_SLEEPER;
         setUserSest(this.userInfoList, Constants.VAG_SLEEPER);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
 
     if ((!"--".equals(info.getBest_seat())) && (!"无".equals(info.getBest_seat()))) {
       try {
         if (Integer.parseInt(info.getBest_seat()) >= this.userInfoList.size()) {
           seat = Constants.BEST_SEAT;
           setUserSest(this.userInfoList, Constants.BEST_SEAT);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.BEST_SEAT;
         setUserSest(this.userInfoList, Constants.BEST_SEAT);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
 
     if ((!"--".equals(info.getBuss_seat())) && (!"无".equals(info.getBuss_seat()))) {
       try {
         if (Integer.parseInt(info.getBuss_seat()) >= this.userInfoList.size()) {
           seat = Constants.BUSS_SEAT;
           setUserSest(this.userInfoList, Constants.BUSS_SEAT);
           this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
           return info;
         }
       } catch (NumberFormatException ex) {
         seat = Constants.BUSS_SEAT;
         setUserSest(this.userInfoList, Constants.BUSS_SEAT);
         this.mainWin.messageOut.setText(this.mainWin.messageOut.getText() + "自动选择的席别为:" + seatMap.get(seat) + "\n");
         return info;
       }
     }
     return null;
   }
 
   private String[] getKeys()
   {
     return ResManager.getByKey("traincode").split(",");
   }
 
   private String[] getSeatKeys()
   {
     return ResManager.getByKey("settype").split(",");
   }
 
   private void setUserSest(List<UserInfo> userInfoList, String seat)
   {
     for (UserInfo info : userInfoList)
       info.setSeatType(seat);
   }
 
   private boolean checkSeatIsTicket(String seat)
   {
     return (StringUtil.isEqualString("--", seat)) || (StringUtil.isEqualString("无", seat));
   }
 
   private String getSeatCount(String seat, TrainQueryInfo info)
   {
     if (StringUtil.isEqualString("P", seat))
       return info.getBuss_seat();
     if (StringUtil.isEqualString("M", seat))
       return info.getOne_seat();
     if (StringUtil.isEqualString("O", seat))
       return info.getTwo_seat();
     if (StringUtil.isEqualString("6", seat))
       return info.getVag_sleeper();
     if (StringUtil.isEqualString("4", seat))
       return info.getSoft_sleeper();
     if (StringUtil.isEqualString("3", seat))
       return info.getHard_sleeper();
     if (StringUtil.isEqualString("2", seat))
       return info.getSoft_seat();
     if (StringUtil.isEqualString("1", seat))
       return info.getHard_seat();
     if (StringUtil.isEqualString("-1", seat)) {
       return info.getNone_seat();
     }
     return info.getOther_seat();
   }
 
   static
   {
     seatMap = new HashMap();
     seatMap.put("9", "商务座");
     seatMap.put("P", "特等座");
     seatMap.put("M", "一等座");
     seatMap.put("O", "二等座");
     seatMap.put("6", "高级软卧");
     seatMap.put("4", "软卧");
     seatMap.put("3", "硬卧");
     seatMap.put("2", "软座");
     seatMap.put("1", "硬座");
     seatMap.put("-1", "无座");
   }
 }

