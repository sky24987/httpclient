 package org.app.ticket.msg;
 
 import java.io.BufferedInputStream;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.PrintStream;
 import java.net.URL;
 import java.util.MissingResourceException;
 import java.util.PropertyResourceBundle;
 import java.util.ResourceBundle;
 import javax.swing.ImageIcon;
 
 public class ResManager
 {
   private static final String BUNDLE_NAME = "resources.ticketrob";
   private static final String IMAGES_PATH = "/resources/images/";
   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("resources.ticketrob");
 
   private static ResourceBundle EXIT_RESOURCE_BUNDLE = null;
 
   public static String getString(String key)
   {
     try
     {
       return RESOURCE_BUNDLE.getString(key); } catch (MissingResourceException e) {
     }
     return '!' + key + '!';
   }
 
   public static ImageIcon createImageIcon(String filename)
   {
     return new ImageIcon(getFileURL(filename));
   }
 
   public static URL getFileURL(String filename) {
     String path = "/resources/images/" + filename;
     return ClassLoader.class.getResource(path);
   }
 
   public static void initProperties(String filePath) throws IOException {
     InputStream in = new BufferedInputStream(new FileInputStream(filePath));
     EXIT_RESOURCE_BUNDLE = new PropertyResourceBundle(in);
   }
 
   public static String getByKey(String key) {
     try {
       if (EXIT_RESOURCE_BUNDLE == null) {
         return "";
       }
       return EXIT_RESOURCE_BUNDLE.getString(key); } catch (Exception e) {
     }
     return "";
   }
 
   public static void main(String[] arg0) throws Exception
   {
     initProperties("E:\\config.properties");
     String[] keys = getByKey("traincode").split(",");
     for (int i = 0; i < keys.length; i++) {
       System.out.println("key" + i + " = " + keys[i]);
     }
     System.out.println(getByKey("traincode"));
     System.out.println(System.getProperty("user.dir"));
   }
 }

