package com.sirap.extractor;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.thread.MasterItemsOriented;
import com.sirap.basic.thread.WorkerItemsOriented;
import com.sirap.common.command.CommandBase;
import com.sirap.extractor.avron.AvronExtractors;

public class CommandAvron extends CommandBase {
	@Override
	public boolean handle() throws Exception {
		if(is("xkai")) {
			String maxPage = AvronExtractors.fetchMaxPageOfStagedCompanies();
			List<Integer> pages = Lists.newArrayList();
			for(int k = 1; k <= Integer.parseInt(maxPage); k++) {
				pages.add(k);
			}
			List<ValuesItem> allItems = fetchAllStagedCompanies(pages);
			export(allItems);
			
			return true;
		}
		
		if(is("gkai")) {
			String maxPage = AvronExtractors.fetchMaxPageOfPublishedCompanies();
			List<Integer> pages = Lists.newArrayList();
			for(int k = 1; k <= Integer.parseInt(maxPage); k++) {
				pages.add(k);
			}
			List<ValuesItem> allItems = fetchAllPublishedCompanies(pages);
			export(allItems);
			
			return true;
		}
		return false;
	}
	
	private List<ValuesItem> fetchAllPublishedCompanies(List<Integer> pageNumbers) {
		MasterItemsOriented<Integer, ValuesItem> george = new MasterItemsOriented<Integer, ValuesItem>(pageNumbers, new WorkerItemsOriented<Integer, ValuesItem>() {
			@Override
			public List<ValuesItem> process(Integer page) {
				int count = queue.size() + 1;
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "async dealing...", page);
				List<ValuesItem> items = AvronExtractors.fetchPublishedCompanies(page);
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "async done", page);
				return items;
			}
		});
		
		return george.getAllMexItems();
	}
	
	private List<ValuesItem> fetchAllStagedCompanies(List<Integer> pageNumbers) {
		MasterItemsOriented<Integer, ValuesItem> george = new MasterItemsOriented<Integer, ValuesItem>(pageNumbers, new WorkerItemsOriented<Integer, ValuesItem>() {
			@Override
			public List<ValuesItem> process(Integer page) {
				int count = queue.size() + 1;
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "async dealing...", page);
				List<ValuesItem> items = AvronExtractors.fetchStagedCompanies(page);
				status(STATUS_TEMPLATE_SIMPLE, count, countOfTasks, "async done", page);
				return items;
			}
		});
		
		return george.getAllMexItems();
	}
}
