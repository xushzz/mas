package com.sirap.orm;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.orm.mybatis.BatisUtil;
import com.sirap.orm.mybatis.dao.ActorDao;
import com.sirap.orm.mybatis.dao.LangDao;
import com.sirap.orm.mybatis.entity.Actor;
import com.sirap.orm.mybatis.entity.Lang;
import com.sirap.orm.mybatis.vo.ActorQueryVO;

public class BatisTest {
	
	public SqlSession kim;
	
	@Before
	public void init() {
		 kim = BatisUtil.getSessionFactory("batis-config.xml").openSession(true);
	}
	
	@After
	public void release() {
		kim.close();
	}
	
//	@Test
	public void testAlias() {
		D.list(BatisUtil.getSessionAliases(kim));
	}
	
//	@Test  
    public void testLangDao() {
		 LangDao dao = kim.getMapper(LangDao.class);
		 Lang king = new Lang();
		 king.setName(RandomUtil.LETTERS(2));
		 int id = RandomUtil.number(20);
//		 D.list(dao.findAllLangs());
		 //D.pl(dao.insertLang(king));
//		 D.pl(dao.insertLang(king));
		 D.pl(dao.findLangById(id));
//		 D.list(dao.findLangByToday());
	}
	
    @Test
    public void testActorDao() {  
        try {
//        	D.sleep5();
//        	D.pl(232);
//            D.pla(sqlSession, sqlSession.getConnection());
            ActorDao dao = kim.getMapper(ActorDao.class);
            ActorQueryVO vo = new ActorQueryVO();
            vo.setFirstName("K");
            Actor find = new Actor();
            String r1 = StrUtil.occupy("%{0}%", RandomUtil.letters(1));
            String r2 = StrUtil.occupy("%{0}%", RandomUtil.letters(1));
            find.setFirstName(StrUtil.occupy("%{0}%", RandomUtil.letters(1)));
            find.setLastName(StrUtil.occupy("%{0}%", RandomUtil.letters(1)));
            List<Integer> ids = Lists.newArrayList();
            for(int k = 1; k < 7; k++) {
            	ids.add(k + 100);
            }
            int id = RandomUtil.number(200);
//            ids = null;
//            ids.clear();
            List<Integer> ids2 = Lists.newArrayList();
            for(int k = 1; k < 100; k++) {
            	ids2.add(k + 160);
            }
            Integer[] idarr = {12, 32, 45, 56};
            Map names = Maps.newConcurrentMap();
            names.put("fake", r1);
            names.put("lake", r2);
            names.put("ids", ids);
//            kim.selectlist
//            D.pl(dao.findActorsByToday());
//            D.pl(dao.findActorsByNameMap(names).size());
            D.pl(dao.findActorsByNameMap(names));
//            D.pl(dao.findActorsByIntegerArr(idarr));
//            D.list(dao.findActorsByIdList(ids));
//            D.pl(dao.findActorsByTwoIdLists(ids, ids2).size());
//	          D.pl(dao.findActorsByBothName(r1, r2).size());
//            D.pl(dao.findActorsByOrName(r1, r2).size());
//            find.setLastName("%a%");
//            D.pl(dao.findActors(find).size());
//            D.pl(dao.findActors("AS"));
//            Actor king = new Actor();
//            king.setActorId(208);
//            king.setFirstName("Kido");
//            king.setLastName("Xilin");
//            D.pl(dao.updateActor(king));
//            String statement = "com.sirap.orm.mybatis.dao.ActorDao.insertActor";
//            D.pl(kim.insert(statement, king));
//            D.pl(dao.deleteActorById(208));
//            kim.commit();
//            D.pl(dao.insertActor(king));
//            D.pl(sqlSession.selectOne("select count(*) from actor"));
//            D.pjsp(dao.findActorByIdX(7), Actor.class);
//            D.pjsp(dao.findActorByIdWithMap(17), Actor.class);
//            D.pl(dao.findActorByIdWithMap(17));
//            D.pjsp(dao.findActors("K"));
//            D.pl(dao.findActors(vo));
//            D.list(dao.findActors("K"));
//            int id = RandomUtil.number(10, 199);
//            D.pl(dao.findActorById(id));
//            Actor user = userMapper.findActorById(id);    
//            D.pls(user);
//            Lang lang = userMapper.findLangById(RandomUtil.number(0, 3));
//            Lang lang = sqlSession.selectOne("com.sirap.orm.mybatis.dao.ActorDao.findLangById", 3);
//            D.pls(lang);
//            sqlSession.close();
        } finally {
            kim.close();
        }
    }
}
