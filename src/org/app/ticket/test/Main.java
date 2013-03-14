 package org.app.ticket.test;
 
 import java.util.List;

import org.app.ticket.bean.OrderRequest;
import org.app.ticket.bean.TrainQueryInfo;
import org.app.ticket.constants.Constants;
import org.app.ticket.core.ClientCore;
import org.app.ticket.util.DateUtil;
 
 public class Main
 {
   public static void main(String[] arg0)
     throws Exception
   {
     Constants.BIGIPSERVEROTSWEB_VALUE = "2396258570.62495.0000";
     Constants.JSESSIONID_VALUE = "321D7DED6E4A80B7E60BE04BB2B1B81E";
     OrderRequest req = new OrderRequest();
     req.setFrom("深圳");
     req.setTo("常德");
     req.setTrain_date("2012-12-30");
     req.setQuery_date(DateUtil.getCurDate());
     List<TrainQueryInfo> trainQueryInfoList = ClientCore.queryTrain(req);
     for (TrainQueryInfo s : trainQueryInfoList) {
       System.out.println(s);
     }
 
     ClientCore.submitOrderRequest((TrainQueryInfo)trainQueryInfoList.get(1), req);
 
     System.out.println("NEW = " + Constants.TOKEN + "\n" + Constants.LEFTTICKETSTR);
   }
 }

