 package org.app.ticket.util;
 
 import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.app.ticket.autoimg.ImageFilter;
import org.app.ticket.core.ClientCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
 public class ToolUtil
 {
   private static final Logger logger = LoggerFactory.getLogger(ToolUtil.class);
 
   public static ImageIcon getImageIcon(String path)
   {
     ImageIcon icon = new ImageIcon(path);
     icon.setImage(icon.getImage().getScaledInstance(icon.getIconWidth(), icon.getIconHeight(), 1));
     return icon;
   }
 
   public static List<String> validateWidget(Object[] o)
   {
     List msg = new ArrayList();
     if (o.length > 0) {
       for (Object s : o) {
         JTextComponent f = (JTextComponent)s;
         if (StringUtil.isEmptyString(f.getText().trim())) {
           msg.add(f.getToolTipText());
         }
         if ((f.getClass() != JFormattedTextField.class) || 
           (!StringUtil.isEqualString("-  -", f.getText().trim()))) continue;
         msg.add(f.getToolTipText());
       }
 
     }
 
     return msg;
   }
 
   public static void getUserInfo(String path, String fileName, Object[] o)
     throws Exception
   {
     Map userMap = new HashMap();
     if (o.length > 0) {
       for (Object s : o) {
         JTextComponent f = (JTextComponent)s;
         if (!StringUtil.isEmptyString(f.getText().trim())) {
           if (f.getClass() == JTextField.class) {
             JTextField jtf = (JTextField)f;
             logger.debug("[key = " + jtf.getName() + ",value = " + jtf.getText().trim() + "]");
             userMap.put(jtf.getName(), jtf.getText().trim());
           }
           if (f.getClass() == JPasswordField.class) {
             JPasswordField jpf = (JPasswordField)f;
             logger.debug("[key = " + jpf.getName() + ",value = " + jpf.getText().trim() + "]");
             userMap.put(jpf.getName(), jpf.getText().trim());
           }
         }
       }
     }
 
     writeFile(userMap, path, fileName);
   }
 
   private static void writeFile(Map<String, String> userMap, String path, String fileName)
     throws Exception
   {
     ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + fileName));
     out.writeObject(userMap);
     out.close();
   }
 
   public static void setUserInfo(String path, String fileName, Object[] o)
     throws FileNotFoundException, Exception
   {
     if (!new File(path + fileName).exists()) {
       return;
     }
     ObjectInputStream in = new ObjectInputStream(new FileInputStream(path + fileName));
     Map userMap = (Map)in.readObject();
     in.close();
     if (o.length > 0)
       for (Object s : o) {
         JTextComponent f = (JTextComponent)s;
         if (f.getClass() == JTextField.class) {
           JTextField jtf = (JTextField)f;
           jtf.setText(userMap.get(jtf.getName()) == null ? "" : (String)userMap.get(jtf.getName()));
         }
         if (f.getClass() == JPasswordField.class) {
           JPasswordField jpf = (JPasswordField)f;
           jpf.setText(userMap.get(jpf.getName()) == null ? "" : (String)userMap.get(jpf.getName()));
         }
       }
   }
 
   public static void filterImage(String uri) throws IOException
   {
     FileInputStream fin = new FileInputStream(uri);
     BufferedImage bi = ImageIO.read(fin);
     ImageFilter flt = new ImageFilter(bi);
     flt.changeGrey();
     flt.getMedian();
     flt.getGrey();
     flt.getBrighten();
     bi = flt.getProcessedImg();
 
     File file = new File(uri);
     ImageIO.write(bi, "jpg", file);
   }
 
   public static void getLoginImage(String i) throws KeyManagementException, NoSuchAlgorithmException {
     String url = "http://dynamic.12306.cn/otsweb/passCodeAction.do?rand=sjrand&";
     double f = 1.000000016862384E-016D;
     Random random = new Random();
     f = random.nextDouble();
     url = url + f;
     ClientCore.getPassCode(url, "F:\\image\\f_" + i + ".jpg");
   }
 
   public static List isSellPoint(Object obj)
     throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IntrospectionException
   {
     boolean isSell = false;
     List list = (List)obj;
     for (int i = list.size() - 1; i >= 0; i--)
     {
       BeanInfo bi = Introspector.getBeanInfo(list.get(i).getClass(), Object.class);
 
       PropertyDescriptor[] pds = bi.getPropertyDescriptors();
       for (PropertyDescriptor pd : pds) {
         Method getMethod = pd.getReadMethod();
         Object o = getMethod.invoke(list.get(i), new Object[0]);
         isSell = StringUtil.isEqualString("*", String.valueOf(o));
         if (isSell) {
           list.remove(i);
           break;
         }
       }
     }
 
     return list;
   }
 
   public static String convertToUncode(String gbString) {
     char[] utfBytes = gbString.toCharArray();
     String unicodeBytes = "";
     for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
       String hexB = Integer.toHexString(utfBytes[byteIndex]);
       if (hexB.length() <= 2) {
         hexB = "00" + hexB;
       }
       unicodeBytes = unicodeBytes + "\\\\u" + hexB;
     }
     System.out.println("unicodeBytes is: " + unicodeBytes);
     return unicodeBytes;
   }
 
   public static void main(String[] arg0)
   {
     convertToUncode("肖琪琪");
   }
 }

