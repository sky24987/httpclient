 package org.app.ticket.bean;
 
 import java.io.Serializable;
 
 public class UserInfo
   implements Serializable
 {
   private static final long serialVersionUID = 1L;
   private String cardID;
   private String name;
   private String phone;
   private String seatType = "1";
   private String tickType = "1";
   private String cardType = "1";
   private String idMode = "Y";
 
   public UserInfo() {
   }
 
   public UserInfo(String cardID, String name) {
     this.cardID = cardID;
     this.name = name;
     this.phone = "";
   }
 
   public UserInfo(String cardID, String name, String phone) {
     this.cardID = cardID;
     this.name = name;
     this.phone = phone;
   }
 
   public String getCardID() {
     return this.cardID;
   }
 
   public void setCardID(String cardID) {
     this.cardID = cardID;
   }
 
   public String getName() {
     return this.name;
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   public String getPhone() {
     return this.phone;
   }
 
   public void setPhone(String phone) {
     this.phone = phone;
   }
 
   public String getSeatType() {
     return this.seatType;
   }
 
   public void setSeatType(String seatType) {
     this.seatType = seatType;
   }
 
   public String getTickType() {
     return this.tickType;
   }
 
   public void setTickType(String tickType) {
     this.tickType = tickType;
   }
 
   public String getCardType() {
     return this.cardType;
   }
 
   public void setCardType(String cardType) {
     this.cardType = cardType;
   }
 
   public String getIdMode() {
     return this.idMode;
   }
 
   public void setIdMode(String idMode) {
     this.idMode = idMode;
   }
 
   public static long getSerialversionuid() {
     return 1L;
   }
 
   public String getText() {
     StringBuilder builder = new StringBuilder();
     builder.append(this.seatType).append(",").append("0,").append(this.tickType).append(",").append(getSimpleText()).append(",").append(this.phone).append(",").append(this.idMode);
     return builder.toString();
   }
 
   public String getSimpleText() {
     StringBuilder builder = new StringBuilder();
     builder.append(this.name).append(",").append(this.cardType).append(",").append(this.cardID);
     return builder.toString();
   }
 
   public String toString()
   {
     StringBuilder builder = new StringBuilder();
     builder.append("UserInfo [ID=").append(this.cardID).append(", name=").append(this.name).append(", phone=").append(this.phone).append(", rangDate=").append(", startDate=").append(", seatType=").append(this.seatType).append(", tickType=").append(this.tickType).append(", cardType=").append(this.cardType).append(", idMode=").append(this.idMode).append("]");
 
     return builder.toString();
   }
 }

