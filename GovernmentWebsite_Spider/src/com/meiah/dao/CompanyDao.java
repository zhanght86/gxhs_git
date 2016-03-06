package com.meiah.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.meiah.po.CompanyInfo;
import com.meiah.util.Converter;

/**
 * @author huhb
 * 
 */
public class CompanyDao {

	private Logger logger = Logger.getLogger(CompanyDao.class);
	private BaseDao baseDao = new BaseDao();

	private static CompanyDao dao;

	private CompanyDao() {
	}

	public static CompanyDao getInstance() {
		if (dao == null)
			dao = new CompanyDao();

		return dao;
	}

	public void save(Collection<CompanyInfo> companyinfo) {
		String sqlStr = "insert n_companyInfo(TASK_ID,CHINESE_NAME,ENGLISH_NAME,SEASON_CODE,SEASON_MARKET,SEASON_DATE,SEASON_NAME,SEASON_PRICE,LEAD_BIZ,BUILD_DATE,REGISTE_CAPITAL,"
				+ "MECHANISM_TYPE,ORGANIZE_TYPE,COMPANY_INFO,OPERATE_RANGE,REGISTE_ADDR,COMPANY_ADDR,COMPANY_PHONE,COMPANY_WEB,COMPANY_MAIL,COMPANY_FAX,COMPANY_POSTCODE,DISCLOSURE_WEB,"
				+ "SECRETARY_NAME,SECRETARY_PHONE,SECRETARY_MAIL,SECRETARY_FAX,PAGE_URL,SAVE_DATE ) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		List<Object[]> parasValues = new ArrayList<Object[]>();
		for (Iterator<CompanyInfo> i = companyinfo.iterator(); i.hasNext();) {
			CompanyInfo ne = i.next();
			Object[] parasValue = new Object[] { 
					ne.getTaskId(),
					ne.getChineseName(),
					ne.getEnglishName(),
					ne.getSeasonCode(),
					ne.getSeasonMarket(),
					Converter.getSqlDateFromUtil(ne.getSeasonDate()),
					ne.getSeasonName(),
					ne.getSeasonPrice(),
					ne.getLeadBiz(),
					Converter.getSqlDateFromUtil(ne.getBuildDate()),
					ne.getRegisteCapital(),
					ne.getMechanismType(),
					ne.getOrganizeType(),
					ne.getCompanyInfo(),
					ne.getOperateRange(),
					ne.getRegisteAddr(),
					ne.getCompanyAddr(),
					ne.getCompanyPhone(),
					ne.getCompanyWeb(),
					ne.getCompanyMail(),
					ne.getCompanyFax(),
					ne.getCompanyPostcode(),
					ne.getDisclosureWeb(),
					ne.getSecretaryName(),
					ne.getSecretaryPhone(),
					ne.getSecretaryMail(),
					ne.getSecretaryFax(),
					ne.getPageUrl(),
					Converter.getSqlDateFromUtil(ne.getSaveDate())
			};
			parasValues.add(parasValue);
		}

		try {

			baseDao.save(sqlStr, parasValues);
		} catch (SQLException e) {
			logger.error("保存公司信息时出错！", e);
		} catch (Exception e) {
			logger.error("保存公司信息时出错！", e);
		}
	}

	
	public static void main(String[] args) {
//		List<CompanyInfo> list = new ArrayList<CompanyInfo>();
//		CompanyInfo ne =new CompanyInfo();
//				ne.setTaskId("1");
//				ne.setChineseName("2");
//				ne.setEnglishName("3");
//				ne.setSeasonCode("4");
//				ne.setSeasonMarket("5");
//				ne.setSeasonDate(new Date());
//				ne.setSeasonName("6");
//				ne.setSeasonPrice("7");
//				ne.setLeadBiz("8");
//				ne.setBuildDate(new Date());
//				ne.setRegisteCapital("9");
//				ne.setMechanismType("10");
//				ne.setOrganizeType("11");
//				ne.setCompanyInfo("12");
//				ne.setOperateRange("13");
//				ne.setRegisteAddr("14");
//				ne.setCompanyAddr("15");
//				ne.setCompanyPhone("16");
//				ne.setCompanyWeb("17");
//				ne.setCompanyMail("18");
//				ne.setCompanyFax("19");
//				ne.setCompanyPostcode("20");
//				ne.setDisclosureWeb("21");
//				ne.setSecretaryName("22");
//				ne.setSecretaryPhone("23");
//				ne.setSecretaryMail("24");
//				ne.setSecretaryFax("25");
//				ne.setPageUrl("26");
//				ne.setSaveDate(new Date());
//				list.add(ne);
//				CompanyDao.getInstance().save(list);
	}
}
