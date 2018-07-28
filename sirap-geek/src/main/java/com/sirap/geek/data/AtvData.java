package com.sirap.geek.data;

import java.util.List;

import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.util.Amaps;

public class AtvData {

	public static final AlinkMap<String, String> EGGS = Amaps.newLinkHashMap();
	static {
		EGGS.put("fargo", "https://en.wikipedia.org/wiki/List_of_Fargo_episodes");
		EGGS.put("arrow", "https://en.wikipedia.org/wiki/List_of_Arrow_episodes");
		EGGS.put("empire", "https://en.wikipedia.org/wiki/List_of_Boardwalk_Empire_episodes");
		EGGS.put("bad", "https://en.wikipedia.org/wiki/List_of_Breaking_Bad_episodes");
		EGGS.put("friends", "https://en.wikipedia.org/wiki/List_of_Friends_episodes");
		EGGS.put("got", "https://en.wikipedia.org/wiki/List_of_Game_of_Thrones_episodes");
		EGGS.put("homeland", "https://en.wikipedia.org/wiki/List_of_Homeland_episodes");
		EGGS.put("sop", "https://en.wikipedia.org/wiki/List_of_The_Sopranos_episodes");
		EGGS.put("24", "https://en.wikipedia.org/wiki/List_of_24_episodes");
		EGGS.put("ww", "https://en.wikipedia.org/wiki/Westworld_(TV_series)");
		EGGS.put("flash", "https://en.wikipedia.org/wiki/List_of_The_Flash_episodes");
		EGGS.put("grimm", "https://en.wikipedia.org/wiki/List_of_Grimm_episodes");
		EGGS.put("shield", "https://en.wikipedia.org/wiki/List_of_Agents_of_S.H.I.E.L.D._episodes");
		EGGS.put("ship", "https://en.wikipedia.org/wiki/List_of_The_Last_Ship_episodes"); //bug: no tv name
		EGGS.put("lucifer", "https://en.wikipedia.org/wiki/List_of_Lucifer_episodes"); //bug
		EGGS.put("100", "https://en.wikipedia.org/wiki/List_of_The_100_episodes"); //bug
		EGGS.put("vampire", "https://en.wikipedia.org/wiki/List_of_The_Vampire_Diaries_episodes");
		EGGS.put("sha", "https://en.wikipedia.org/wiki/The_Shannara_Chronicles");
		EGGS.put("supergirl", "https://en.wikipedia.org/wiki/List_of_Supergirl_episodes");
		EGGS.put("fall", "https://en.wikipedia.org/wiki/List_of_Falling_Skies_episodes");
		EGGS.put("walking", "https://en.wikipedia.org/wiki/List_of_The_Walking_Dead_episodes");
		EGGS.put("strain", "https://en.wikipedia.org/wiki/List_of_The_Strain_episodes"); //bug
		EGGS.put("supernatural", "https://en.wikipedia.org/wiki/List_of_Supernatural_episodes");
		EGGS.put("znation", "https://en.wikipedia.org/wiki/List_of_Z_Nation_episodes"); //bug
		EGGS.put("containment", "https://en.wikipedia.org/wiki/Containment_(TV_series)"); //one season
		EGGS.put("teenwolf", "https://en.wikipedia.org/wiki/List_of_Teen_Wolf_episodes");
		EGGS.put("horror", "https://en.wikipedia.org/wiki/List_of_American_Horror_Story_episodes");
		EGGS.put("bigbang", "https://en.wikipedia.org/wiki/List_of_The_Big_Bang_Theory_episodes");
		EGGS.put("submission", "https://en.wikipedia.org/wiki/Submission_(TV_series)"); //one season
		EGGS.put("shameless", "https://en.wikipedia.org/wiki/List_of_Shameless_(U.S._TV_series)_episodes");
		EGGS.put("2", "https://en.wikipedia.org/wiki/List_of_2_Broke_Girls_episodes");
		EGGS.put("charlie", "https://en.wikipedia.org/wiki/List_of_Good_Luck_Charlie_episoes"); //no name
		EGGS.put("bauer", "https://en.wikipedia.org/wiki/List_of_Designated_Survivor_episodes");
		EGGS.put("femme", "https://en.wikipedia.org/wiki/Femme_Fatales_(TV_series)"); //nothing
		EGGS.put("cards", "https://en.wikipedia.org/wiki/List_of_House_of_Cards_episodes");
		EGGS.put("saul", "https://en.wikipedia.org/wiki/List_of_Better_Call_Saul_episodes");
		EGGS.put("modern", "https://en.wikipedia.org/wiki/List_of_Modern_Family_episodes");
		EGGS.put("lost", "https://en.wikipedia.org/wiki/List_of_Lost_episodes");
	}
	
	public static List<String> eggs() {
		return Amaps.listOf(EGGS);
	}
}
