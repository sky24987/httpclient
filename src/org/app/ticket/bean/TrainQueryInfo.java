 package org.app.ticket.bean;
 
 public class TrainQueryInfo
 {
   private String trainCode;
   private String trainNo;
   private String trainDate;
   private String fromStation;
   private String fromStationName;
   private String fromStationCode;
   private String startTime;
   private String toStation;
   private String toStationName;
   private String toStationCode;
   private String endTime;
   private String locationCode;
   private String takeTime;
   private String formStationNo;
   private String toStationNo;
   private String buss_seat;
   private String best_seat;
   private String one_seat;
   private String two_seat;
   private String vag_sleeper;
   private String soft_sleeper;
   private String hard_sleeper;
   private String soft_seat;
   private String hard_seat;
   private String none_seat;
   private String other_seat;
   private String mmStr;
   private String trainno4;
   private String ypInfoDetail;
   private String single_round_type = "1";
 
   public String getTrainCode() {
     return this.trainCode;
   }
 
   public void setTrainCode(String trainCode) {
     this.trainCode = trainCode;
   }
 
   public String getTrainNo() {
     return this.trainNo;
   }
 
   public void setTrainNo(String trainNo) {
     this.trainNo = trainNo;
   }
 
   public String getTrainDate() {
     return this.trainDate;
   }
 
   public void setTrainDate(String trainDate) {
     this.trainDate = trainDate;
   }
 
   public String getFromStation() {
     return this.fromStation;
   }
 
   public void setFromStation(String fromStation) {
     this.fromStation = fromStation;
   }
 
   public String getFromStationCode() {
     return this.fromStationCode;
   }
 
   public void setFromStationCode(String fromStationCode) {
     this.fromStationCode = fromStationCode;
   }
 
   public String getStartTime() {
     return this.startTime;
   }
 
   public void setStartTime(String startTime) {
     this.startTime = startTime;
   }
 
   public String getToStation() {
     return this.toStation;
   }
 
   public void setToStation(String toStation) {
     this.toStation = toStation;
   }
 
   public String getToStationCode() {
     return this.toStationCode;
   }
 
   public void setToStationCode(String toStationCode) {
     this.toStationCode = toStationCode;
   }
 
   public String getEndTime() {
     return this.endTime;
   }
 
   public void setEndTime(String endTime) {
     this.endTime = endTime;
   }
 
   public String getLocationCode() {
     return this.locationCode;
   }
 
   public void setLocationCode(String locationCode) {
     this.locationCode = locationCode;
   }
 
   public String getTakeTime() {
     return this.takeTime;
   }
 
   public void setTakeTime(String takeTime) {
     this.takeTime = takeTime;
   }
 
   public String getFormStationNo() {
     return this.formStationNo;
   }
 
   public void setFormStationNo(String formStationNo) {
     this.formStationNo = formStationNo;
   }
 
   public String getToStationNo() {
     return this.toStationNo;
   }
 
   public void setToStationNo(String toStationNo) {
     this.toStationNo = toStationNo;
   }
 
   public String getBuss_seat() {
     return this.buss_seat;
   }
 
   public void setBuss_seat(String buss_seat) {
     this.buss_seat = buss_seat;
   }
 
   public String getBest_seat() {
     return this.best_seat;
   }
 
   public void setBest_seat(String best_seat) {
     this.best_seat = best_seat;
   }
 
   public String getOne_seat() {
     return this.one_seat;
   }
 
   public void setOne_seat(String one_seat) {
     this.one_seat = one_seat;
   }
 
   public String getTwo_seat() {
     return this.two_seat;
   }
 
   public void setTwo_seat(String two_seat) {
     this.two_seat = two_seat;
   }
 
   public String getVag_sleeper() {
     return this.vag_sleeper;
   }
 
   public void setVag_sleeper(String vag_sleeper) {
     this.vag_sleeper = vag_sleeper;
   }
 
   public String getSoft_sleeper() {
     return this.soft_sleeper;
   }
 
   public void setSoft_sleeper(String soft_sleeper) {
     this.soft_sleeper = soft_sleeper;
   }
 
   public String getHard_sleeper() {
     return this.hard_sleeper;
   }
 
   public void setHard_sleeper(String hard_sleeper) {
     this.hard_sleeper = hard_sleeper;
   }
 
   public String getSoft_seat() {
     return this.soft_seat;
   }
 
   public void setSoft_seat(String soft_seat) {
     this.soft_seat = soft_seat;
   }
 
   public String getHard_seat() {
     return this.hard_seat;
   }
 
   public void setHard_seat(String hard_seat) {
     this.hard_seat = hard_seat;
   }
 
   public String getNone_seat() {
     return this.none_seat;
   }
 
   public void setNone_seat(String none_seat) {
     this.none_seat = none_seat;
   }
 
   public String getOther_seat() {
     return this.other_seat;
   }
 
   public void setOther_seat(String other_seat) {
     this.other_seat = other_seat;
   }
 
   public String getMmStr() {
     return this.mmStr;
   }
 
   public void setMmStr(String mmStr) {
     this.mmStr = mmStr;
   }
 
   public String getTrainno4() {
     return this.trainno4;
   }
 
   public void setTrainno4(String trainno4) {
     this.trainno4 = trainno4;
   }
 
   public String getYpInfoDetail() {
     return this.ypInfoDetail;
   }
 
   public void setYpInfoDetail(String ypInfoDetail) {
     this.ypInfoDetail = ypInfoDetail;
   }
 
   public String getSingle_round_type() {
     return this.single_round_type;
   }
 
   public void setSingle_round_type(String single_round_type) {
     this.single_round_type = single_round_type;
   }
 
   public String getFromStationName() {
     return this.fromStationName;
   }
 
   public void setFromStationName(String fromStationName) {
     this.fromStationName = fromStationName;
   }
 
   public String getToStationName() {
     return this.toStationName;
   }
 
   public void setToStationName(String toStationName) {
     this.toStationName = toStationName;
   }
 
   public String toString()
   {
     StringBuilder builder = new StringBuilder();
     builder.append("TrainQueryInfo [trainCode=");
     builder.append(this.trainCode);
     builder.append(", trainNo=");
     builder.append(this.trainNo);
     builder.append(", trainDate=");
     builder.append(this.trainDate);
     builder.append(", fromStation=");
     builder.append(this.fromStation);
     builder.append(", fromStationCode=");
     builder.append(this.fromStationCode);
     builder.append(", startTime=");
     builder.append(this.startTime);
     builder.append(", toStation=");
     builder.append(this.toStation);
     builder.append(", toStationCode=");
     builder.append(this.toStationCode);
     builder.append(", endTime=");
     builder.append(this.endTime);
     builder.append(", takeTime=");
     builder.append(this.takeTime);
     builder.append(", formStationNo=");
     builder.append(this.formStationNo);
     builder.append(", toStationNo=");
     builder.append(this.toStationNo);
     builder.append(", buss_seat=");
     builder.append(this.buss_seat);
     builder.append(", best_seat=");
     builder.append(this.best_seat);
     builder.append(", one_seat=");
     builder.append(this.one_seat);
     builder.append(", two_seat=");
     builder.append(this.two_seat);
     builder.append(", vag_sleeper=");
     builder.append(this.vag_sleeper);
     builder.append(", soft_sleeper=");
     builder.append(this.soft_sleeper);
     builder.append(", hard_sleeper=");
     builder.append(this.hard_sleeper);
     builder.append(", soft_seat=");
     builder.append(this.soft_seat);
     builder.append(", hard_seat=");
     builder.append(this.hard_seat);
     builder.append(", none_seat=");
     builder.append(this.none_seat);
     builder.append(", mmStr=");
     builder.append(this.mmStr);
     builder.append(", trainno4=");
     builder.append(this.trainno4);
     builder.append(", ypInfoDetail=");
     builder.append(this.ypInfoDetail);
     builder.append(", single_round_type=");
     builder.append(this.single_round_type);
     builder.append("]");
     return builder.toString();
   }
 }

