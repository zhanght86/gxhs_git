package com.meiah.test;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;

import com.meiah.dao.BaseDao;
import com.meiah.dao.NewsDao;
import com.meiah.po.News;

public class TestDB {
	private static Logger logger = Logger.getLogger(NewsDao.class);
	private BaseDao baseDao = new BaseDao();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestDB t = new TestDB();
		// String sql_1 = "select top 10000 * from n_realtime where
		// datediff(dd,datepublished,getdate())<3";
		// String sql_2 = "select top 10000 * from n_realtime where
		// datepublished>dateadd(dd,-3,getdate())";
		// String sql_3 = "select top 10000 * from n_realtime where
		// datepublished>'2010-10-8'";
		// logger.debug(sql_3 + ":" + t.testSqlTime(sql_3));
		// logger.debug(sql_2 + ":" + t.testSqlTime(sql_2));
		//
		// logger.debug(sql_1 + ":" + t.testSqlTime(sql_1));
		// t.testTrsTime();
		String sqlStr = "select page_url from n_realtime where taskid=3697215402 and islist=0 and conform='2387357020'";
		t.testSqlTime(sqlStr);
	long timeT = 0, timeI = 0;
//		for (int i = 0; i < 10; i++) {
//			timeT += t.testTrsTime();
//		}
		for (int i = 0; i < 1000; i++) {
			timeI += t.testInserTime();
		}
		logger.info("平均查询时间：" + (double) timeT / 1000 + " ms");
		logger.info("平均插入时间：" + (double) timeI / 1000 + " ms");

	}

	public long testSqlTime(String sqlStr) {
		long t1 = System.currentTimeMillis();
		try {
			logger.debug(baseDao.query(sqlStr).size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeE = System.currentTimeMillis() - t1;
		logger.debug("耗费时间："+timeE+" ms");
		return timeE;
	}

	public long testTrsTime() {
		Random r = new Random();
		int hour = Math.abs(r.nextInt() % 23);
		int minute = Math.abs(r.nextInt() % 58);

		String sqlStr = "select taskid,isnull(TopDomain,'')TopDomain,isnull(WebDomain,'')WebDomain,page_url,isnull(title,'')title,isnull(pageSize,0)pageSize,datePublished,Savetime,isnull(Filename,'')Filename,isnull(islist,'')islist,isnull(Conform,'')Conform,isnull(IP,'')IP,isnull(content,'')content,isnull(username,'')username,isnull(page_type,'')page_type,isnull(spot_code,'')spot_code,isnull(source_type,'')source_type,id,isnull(refererurl,'')refererurl from n_realtime"
				+ "  where trstime>'2010-11-11 "
				+ hour
				+ ":"
				+ minute
				+ ":00' and trstime<='2010-11-11 "
				+ hour
				+ ":"
				+ (minute + 1)
				+ ":00' and islist<>9 ";
		logger.debug(sqlStr);
		long t1 = System.currentTimeMillis();
		try {
			logger.debug(baseDao.query(sqlStr).size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeE = System.currentTimeMillis() - t1;
		logger.info("语句耗费时间：" + timeE + " ms");
		return timeE;
	}

	public long testInserTime() {
		News ne = new News();
		ne.setTaskid("9999");
		String page_url = "http://www.test.com.cn/" + Math.random() * 1000;
		ne.setUrl(page_url);
		ne
				.setContent("http://www.zhengjian.org/zj/CultureNet/123,136,409,1.html',N'正见',36825,'2010-11-12 17:52:16:000','2010-11-12 17:52:16:000',NULL,1,N'',N'',N'',N'正见 　　 首页| 正体| 简体| 返回正见 编辑信箱 网站地图 首页 > 历史 > 远古 夏 商 西周 春秋战国/东周 秦 西汉 东汉 魏晋南北朝 隋 唐 五代十国 北宋 南宋 元 明 清 近代 民俗节日 其它 中国古代先王管理社会策略：唐刑法 清雅　01/28/10 中华典故：永州之鼠 云开　12/26/09 悠悠古风：裴度还带 附录音 紫微　09/18/09 民间传说：测字擒盗 紫悦　05/16/09 历史故事：韦澳执法不避权贵 刚正不阿 程实　05/05/09 以史为鉴：不听道人示警 宰相惨遭灭门 陆文　04/14/09 民间传说：洞庭龙女 紫悦　04/11/09 因果报应实录：忘恩负义变成牛 附录音 大陆学员　03/29/09 历史故事：宰相之母教子谨慎 严谨　03/21/09 以史为鉴：为官不可趋炎附势 秦学善　03/19/09 古风悠悠：正义正直 不屈权势 故事二则 陆真　03/18/09 因果报应实录：减税行善得福报 华翰　03/09/09 历史故事：官员善待僧人 后得福报 陆文　03/09/09 洪山菜薹的传说 小阳春　整理　02/19/09 佛家故事：一行预测天气 陆文　01/30/09 因果报应实录：亵渎佛像险入地狱 幸得菩萨慈悲方还阳 附录音 大陆学员　01/23/09 历史故事：恰巧就是第八名 秦自省　01/14/09 佛家故事：大唐玄奘收弟子 程实　01/13/09 因果报应实录：盗窃寺院财物 遭护法神惩戒 大法学员　01/13/09 佛家故事：病愈献宅修庙 赢得佛光普照 陆真　12/30/08 因果报应实录：谤佛之人罪孽深 死变乌鸦捎口信 程实　12/20/08 佛家故事：妻儿敬佛 僧救亲人 郑念行　12/19/08 佛家故事：妻子虔诚敬佛 丈夫死途生返 陆文　12/17/08 历史故事：盲修十年志愈坚 双目复明谢神仙 秦自省　12/01/08 汉服设计：唐朝服饰 张咏　11/30/08 因果报应实录：王琼未升职 原来是报应 陆真　11/29/08 历史故事：李翱认错 开始敬信神明 陆文　11/28/08 历史故事：飞天神保佑了一方百姓 郑念行　11/14/08 请看神怎样救庇好人：敬佛法护佛经 神灵救其上岸 大法学员　11/09/08 因果报应实录：裸身不敬经 遭报被神打 附录音 大法学员　11/06/08 因果报应实录：意欲玷污修炼人 遭神严惩悔已迟 大法学员　11/05/08 因果报应实录：保全佛寺 死而生还 伊莲　整理　11/02/08 因果报应实录：救无辜 大胆用权得善报 陆文　09/22/08 冥冥之中有定数：韦氏女梦中知晓一生命运 大陆学员　09/22/08 佛家故事：佛法威力助天人 附录音 大法学员　09/19/08 古风悠悠：古人美德故事拾珍 三则 陆文　09/13/08 首页　|　前页　| 后页 | 末页 | 1 页/ 8 页　|　共 286 篇 1 2 3 4 ... : 优昙婆罗花 医山夜话 神仙故事 悠游字在 古代音乐 传统武术 中国古典舞 汉服 唐朝建筑 新唐人大赛 神韵晚会 道德礼仪 神州史纲 治者修行 古代名医 杏林漫步 中医教材 民间传说 成语故事 历史故事 中华典故 名山古寺 山岳仙迹探微 佛家故事 道家修炼故事 诗人修炼故事 神州五千年纵观 因果报应实录 冥冥之中有定数 西方艺术 黑暗传 《西游记》探秘 著名古塔 著名古桥 中国城市发展史 古代术数 中国历法 古代四大发明 中国文化简介 古代文化漫谈 书法漫谈 神传汉字之谜 古籍中龙的记载 &copy;　正见网版权所有◎1999-2009　ZHENGJIAN.ORG　转载请注明出处 ");
		ne.setIslist(0);
		ne.setDateline(new Date());
		ne.setSavetime(new Date());
		long t1 = System.currentTimeMillis();
		try {
		//	NewsDao.getInstance().save(ne);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeE = System.currentTimeMillis() - t1;
		logger.info("语句耗费时间：" + timeE + " ms");
		return timeE;
	}
}
