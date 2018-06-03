package com.sirap.orm.mybatis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.sirap.orm.mybatis.entity.Actor;
import com.sirap.orm.mybatis.entity.Lang;
import com.sirap.orm.mybatis.vo.ActorQueryVO;

public interface ActorDao {
	public int insertActor(Actor actor);  
	public int updateActor(Actor actor);  
	public int deleteActorById(int actorId);
    public Actor findActorById(int actorid);
    public List<Actor> findActorsByName(@Param("name") String name);
    public List<Actor> findActorsByOrName(String first, String last);
    public List<Actor> findActorsByBothName(String first, String last);
    public Lang findLangById(int id);  
    //findActorByIdList
    public List<Actor> findActorsByIntegerArr(@Param("listA") Integer[] ids);
    public List<Actor> findActorsByIdList(@Param("listA") List<Integer> ids);
    public List<Actor> findActorsByTwoIdLists(@Param("listA") List<Integer> ids1, @Param("listB") List<Integer> ids2);
    public List<Actor> findActorsByNameMap(Map<String, String> names);
    public List<Actor> findActors(Lang name);
    public List<Actor> findActors(Actor vo); 
    public Map<String, Object> findActorByIdWithMap(int actorid);  
    public int countOfActors(ActorQueryVO vo);  
    
    @Select("select * from actor where datediff(last_update, now()) = 0")
	public List<Actor> findActorsByToday();
}
