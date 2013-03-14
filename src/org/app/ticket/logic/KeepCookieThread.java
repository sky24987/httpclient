 package org.app.ticket.logic;
 
 import org.app.ticket.core.ClientCore;
 import org.app.ticket.msg.ResManager;
 import org.app.ticket.util.StringUtil;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public class KeepCookieThread extends Thread
 {
   private static final Logger logger = LoggerFactory.getLogger(KeepCookieThread.class);
 
   public void run()
   {
     try
     {
       while (true) {
         logger.debug(new StringBuilder().append("cookie Every time start time interval for ").append(Integer.parseInt(StringUtil.isEmptyString(ResManager.getByKey("keepcookietime")) ? "20" : ResManager.getByKey("keepcookietime"))).append("min").toString());
         sleep(60000 * Integer.parseInt(StringUtil.isEmptyString(ResManager.getByKey("sleeptime")) ? "20" : ResManager.getByKey("keepcookietime")));
         ClientCore.getCookie();
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 }

