 package org.app.ticket.bean;
 
 public class OrderRequest
 {
   private String includeStudent = "00";
   private String from_station_telecode;
   private String start_time_str;
   private String to_station_telecode;
   private String train_date;
   private String query_date;
   private String train_no="";
   private String trainClass = "QB#D#Z#T#K#QT#";
 
   private String trainPassType = "QB";
 
   private String seatTypeAndNum = "";
   private String from;
   private String to;
 
   public String getIncludeStudent()
   {
     return this.includeStudent;
   }
 
   public void setIncludeStudent(String includeStudent) {
     this.includeStudent = includeStudent;
   }
 
   public String getFrom_station_telecode() {
     return this.from_station_telecode;
   }
 
   public void setFrom_station_telecode(String from_station_telecode) {
     this.from_station_telecode = from_station_telecode;
   }
 
   public String getStart_time_str() {
     return this.start_time_str;
   }
 
   public void setStart_time_str(String start_time_str) {
     this.start_time_str = start_time_str;
   }
 
   public String getTo_station_telecode() {
     return this.to_station_telecode;
   }
 
   public void setTo_station_telecode(String to_station_telecode) {
     this.to_station_telecode = to_station_telecode;
   }
 
   public String getTrain_date() {
     return this.train_date;
   }
 
   public void setTrain_date(String train_date) {
     this.train_date = train_date;
   }
 
   public String getQuery_date() {
     return this.query_date;
   }
 
   public void setQuery_date(String query_date) {
     this.query_date = query_date;
   }
 
   public String getTrain_no() {
     return this.train_no;
   }
 
   public void setTrain_no(String train_no) {
     this.train_no = train_no;
   }
 
   public String getTrainClass() {
     return this.trainClass;
   }
 
   public void setTrainClass(String trainClass) {
     this.trainClass = trainClass;
   }
 
   public String getTrainPassType() {
     return this.trainPassType;
   }
 
   public void setTrainPassType(String trainPassType) {
     this.trainPassType = trainPassType;
   }
 
   public String getSeatTypeAndNum() {
     return this.seatTypeAndNum;
   }
 
   public void setSeatTypeAndNum(String seatTypeAndNum) {
     this.seatTypeAndNum = seatTypeAndNum;
   }
 
   public String getFrom() {
     return this.from;
   }
 
   public void setFrom(String from) {
     this.from = from;
   }
 
   public String getTo() {
     return this.to;
   }
 
   public void setTo(String to) {
     this.to = to;
   }
 }

