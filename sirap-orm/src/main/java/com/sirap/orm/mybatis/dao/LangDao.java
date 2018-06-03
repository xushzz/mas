package com.sirap.orm.mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.sirap.orm.mybatis.entity.Lang;

public interface LangDao {
	public int insertLang(Lang Lang);
	public int updateLang(Lang Lang);  
	public int deleteLangById(int LangId);
	
	@Select("select * from language where language_id = #{id}")
    public Lang findLangById(int langid);
	
	@Select("select * from language where datediff(last_update, now()) = 0")
	public List<Lang> findLangByToday();
	
	public List<Lang> findAllLangs();
}
