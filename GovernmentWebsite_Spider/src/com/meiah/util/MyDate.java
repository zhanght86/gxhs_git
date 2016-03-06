package com.meiah.util;

import java.util.Calendar;

/** OK
 * 获取各种日期
 */
public class MyDate {
    	java.util.Calendar Date_time;
    	String Tyear,Tmonth,Tday,Thour,Tminute,Tsecond,TAM_PM;


	public MyDate(int diff) {
	      	Date_time=Calendar.getInstance();
      		Date_time.add(Calendar.DATE, diff);      		      	
	      	Tyear=String.valueOf(Date_time.get(Calendar.YEAR));
	      	Tmonth=String.valueOf(Date_time.get(Calendar.MONTH)+1);
	      	if (Tmonth.length()<2) Tmonth="0"+Tmonth;
	      	Tday=String.valueOf(Date_time.get(Calendar.DAY_OF_MONTH));
	      	if (Tday.length()<2) Tday="0"+Tday;
	      	Thour=String.valueOf(Date_time.get(Calendar.HOUR_OF_DAY));
	      	if (Thour.length()<2) Thour="0"+Thour;
	      	Tminute=String.valueOf(Date_time.get(Calendar.MINUTE));	
	      	if (Tminute.length()<2) Tminute="0"+Tminute;
	      	Tsecond=String.valueOf(Date_time.get(Calendar.SECOND));	
	      	if (Tsecond.length()<2) Tsecond="0"+Tsecond;	      	
   	 
	      	TAM_PM=String.valueOf(Date_time.get(Calendar.AM_PM));
	      	//System.out.println(get_yyyymmddhh24miss());
		//System.out.println(String.valueOf(get_week())+"--"+String.valueOf(getTime()));
	}

	public void addDay(int diff) {
	      	
      		Date_time.add(Calendar.DATE, diff);      		      	
	      	Tyear=String.valueOf(Date_time.get(Calendar.YEAR));
	      	Tmonth=String.valueOf(Date_time.get(Calendar.MONTH)+1);
	      	if (Tmonth.length()<2) Tmonth="0"+Tmonth;
	      	Tday=String.valueOf(Date_time.get(Calendar.DAY_OF_MONTH));
	      	if (Tday.length()<2) Tday="0"+Tday;
	      	Thour=String.valueOf(Date_time.get(Calendar.HOUR_OF_DAY));
	      	if (Thour.length()<2) Thour="0"+Thour;
	      	Tminute=String.valueOf(Date_time.get(Calendar.MINUTE));	
	      	if (Tminute.length()<2) Tminute="0"+Tminute;
	      	Tsecond=String.valueOf(Date_time.get(Calendar.SECOND));	
	      	if (Tsecond.length()<2) Tsecond="0"+Tsecond;	      	
   	 
	      	TAM_PM=String.valueOf(Date_time.get(Calendar.AM_PM));
	      	//System.out.println(get_yyyymmddhh24miss());
		//System.out.println(String.valueOf(get_week())+"--"+String.valueOf(getTime()));
	}


	public MyDate() {
	      	Date_time=Calendar.getInstance();
      		      		      	
	      	Tyear=String.valueOf(Date_time.get(Calendar.YEAR));
	      	Tmonth=String.valueOf(Date_time.get(Calendar.MONTH)+1);
	      	if (Tmonth.length()<2) Tmonth="0"+Tmonth;
	      	Tday=String.valueOf(Date_time.get(Calendar.DAY_OF_MONTH));
	      	if (Tday.length()<2) Tday="0"+Tday;
	      	Thour=String.valueOf(Date_time.get(Calendar.HOUR_OF_DAY));
	      	if (Thour.length()<2) Thour="0"+Thour;
	      	Tminute=String.valueOf(Date_time.get(Calendar.MINUTE));	
	      	if (Tminute.length()<2) Tminute="0"+Tminute;
	      	Tsecond=String.valueOf(Date_time.get(Calendar.SECOND));	
	      	if (Tsecond.length()<2) Tsecond="0"+Tsecond;	      	
   	 
	      	TAM_PM=String.valueOf(Date_time.get(Calendar.AM_PM));
	      	//System.out.println(get_yyyymmddhh24miss());
		//System.out.println(String.valueOf(get_week())+"--"+String.valueOf(getTime()));
	}
	public MyDate(long lTime) {
	      	Date_time=Calendar.getInstance();
	      	Date_time.setTimeInMillis(lTime);
	      	Date_time.add(Calendar.MONTH,1);
	}

	public MyDate(String df){
		try{
			java.text.SimpleDateFormat myFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date= myFormatter.parse(df); 
			Date_time=Calendar.getInstance();
			Date_time.setTime(date);
		}catch(Exception e){
			Date_time=Calendar.getInstance();
		}

	      	Tyear=String.valueOf(Date_time.get(Calendar.YEAR));
	      	Tmonth=String.valueOf(Date_time.get(Calendar.MONTH)+1);
	      	if (Tmonth.length()<2) Tmonth="0"+Tmonth;
	      	Tday=String.valueOf(Date_time.get(Calendar.DAY_OF_MONTH));
	      	if (Tday.length()<2) Tday="0"+Tday;
	      	Thour=String.valueOf(Date_time.get(Calendar.HOUR_OF_DAY));
	      	if (Thour.length()<2) Thour="0"+Thour;
	      	Tminute=String.valueOf(Date_time.get(Calendar.MINUTE));	
	      	if (Tminute.length()<2) Tminute="0"+Tminute;
	      	Tsecond=String.valueOf(Date_time.get(Calendar.SECOND));	
	      	if (Tsecond.length()<2) Tsecond="0"+Tsecond;	      	
   	 
	      	TAM_PM=String.valueOf(Date_time.get(Calendar.AM_PM));

	}

	public int get_week() {
		int this_week=Date_time.get(Calendar.DAY_OF_WEEK);
		if (this_week==1) this_week=7;
		else this_week=this_week-1;
		return this_week;
	}
	public String get_hh24miss() {
		return Thour+Tminute+Tsecond;
	}		
	public String get_yyyymmddhh24miss() {
		return Tyear+Tmonth+Tday+Thour+Tminute+Tsecond;
	}	
	public String getDateTime() {
    		return Tday+"/"+Tmonth+"/"+Tyear+" "+Thour+":"+Tminute;		
	}
	public String get_china_DateTime() {
		return Tyear+"-"+Tmonth+"-"+Tday+" "+Thour+":"+Tminute;
	}
	public String get_yyyy_mm_dd() {
		return Tyear+"-"+Tmonth+"-"+Tday;
	}		
	public String getDate() {
    		return Tday+"/"+Tmonth+"/"+Tyear;
	}
	public String  getYear() {
		return Tyear;
	}	
	public String get_mmdd() {
    		return Tmonth+Tday;
	}		
	public String get_yyyymmdd() {
    		return Tyear+Tmonth+Tday;
	}	
	public String getTime() {
    		return Thour+":"+Tminute;		
	}
	public String get_yyyymmdd_hh() {
		String mi="5";
		try {
			if (Tminute.compareTo("00")>=0 && Tminute.compareTo("09")<=0) mi="0";
			else if (Tminute.compareTo("10")>=0 && Tminute.compareTo("19")<=0) mi="1";
			else if (Tminute.compareTo("20")>=0 && Tminute.compareTo("29")<=0) mi="2";
			else if (Tminute.compareTo("30")>=0 && Tminute.compareTo("39")<=0) mi="3";
			else if (Tminute.compareTo("40")>=0 && Tminute.compareTo("49")<=0) mi="4";
			else mi="5";
		} catch (Throwable e) {}	
    		return Tyear+Tmonth+Tday+"\\"+Thour+"\\"+mi+"\\";
	}	
	public int get_day() {
		return Integer.parseInt(Tday);
	}
	public long get_long() {
		return Date_time.getTime().getTime();
	}	
	public Long get_Long() {
		return new Long(Date_time.getTime().getTime());
	}	
	public String addDate(int Ayear) {
		String year_tmp,month_tmp,day_tmp;
		year_tmp=String.valueOf(Date_time.get(Calendar.YEAR)+Ayear);
		if (Tmonth.length()<2) month_tmp="0"+Tmonth;
		else month_tmp=Tmonth;
		if (Tday.length()<2) day_tmp="0"+Tday;
		else day_tmp=Tday;
		return year_tmp+"/"+month_tmp+"/"+day_tmp;
	}

	public static void main(String[] args){
//		MyDate md = new MyDate(-31);
//		System.out.println(md.get_yyyymmdd());
//		System.out.println(md.get_yyyymmddhh24miss());
//		System.out.println(md.get_week());
//		System.out.println(md.getTime().substring(0,2));
//		System.out.println(md.get_day());
//
//		md = new MyDate(1);
//		System.out.println(md.get_yyyymmddhh24miss());
//		System.out.println(md.get_week());
//		System.out.println(md.getTime().substring(0,2));
//		System.out.println(md.get_day());
//
//		md = new MyDate("1977-4-5");
//		System.out.println(md.get_week());
//
//		md.addDay(365);
//		System.out.println(md.get_yyyymmddhh24miss());
//		System.out.println(md.get_yyyymmdd());
		
		/*System.out.println(new MyDate().get_yyyymmddhh24miss());
		System.out.println(new MyDate().get_hh24miss());
		System.out.println(new Date());*/
	}
}