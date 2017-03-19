package com.sirap.common.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexedTimer;
import com.sirap.basic.domain.EmailCommandRecord;
import com.sirap.basic.email.EmailCenter;
import com.sirap.basic.search.CriteriaFilter;
import com.sirap.basic.thirdparty.email.EmailFetcher;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.Janitor;
import com.sirap.common.framework.SimpleKonfig;

public class RemoteCommandManager extends MexedTimer {

	private static RemoteCommandManager instance;
	private List<EmailCommandRecord> allRecords;
	private String location;
	private boolean selfCensorship = true;

	private int mins2live;
	private int seconds2refresh;
	
	private RemoteCommandManager() {
		
	}
	
	private static class Holder {
		private static RemoteCommandManager instance = new RemoteCommandManager();
	}
	
	public static RemoteCommandManager g() {
		if(instance == null) {
			instance = Holder.instance;
			instance.init();
		}
		
		return instance;
	}
	
	private void init() {
		mins2live = SimpleKonfig.g().getUserNumberValueOf("remote.command.mins2live", 10);
		seconds2refresh = SimpleKonfig.g().getUserNumberValueOf("remote.command.seconds2refresh", 30);
				
		setDelaySeconds(0);
		setPeriodSeconds(seconds2refresh);
		
		if(SimpleKonfig.g().isRemoteEnabled()) {
			startTimer();
		}
	}
	
	public void switchEnability(boolean toEnable) {
		if(isTimerActive()) {
			if(toEnable) {
				updateTimerTask();
			} else {
				cancelTimerTask();
			}
		} else {
			if(toEnable) {
				startTimer();
			} else {
			}
		}
	}
	
	private List<EmailCommandRecord> getAllRecords() {
		if(allRecords == null) {
			if(location == null) {
				location = SimpleKonfig.g().pathWithSeparator("storage.remote", Konstants.FOLDER_REMOTE);
		    	FileUtil.makeDirectoriesIfNonExist(location);
			}
			String filePath = location + getFilename();
			allRecords = MexUtil.readMexItemsViaExplicitClass(filePath, EmailCommandRecord.class);
		}
		
		return allRecords;		
	}
	
	@Override
	protected void timerAction() {
		if(!SimpleKonfig.g().isEmailEnabled()) {
			C.pl("Email should be enabled to perform remote control.");
			return;
		}
		
		List<EmailCommandRecord> todoList = updateCommandsFromRemote(EmailCenter.g().getUsername(), EmailCenter.g().getPassword());
		if(EmptyUtil.isNullOrEmpty(todoList)) {
			return;
		}
		
		processCommands(todoList);
		saveAllRecords();
	}
	
	private void processCommands(List<EmailCommandRecord> todoList) {
		String username = EmailCenter.g().getUsername();
		for(EmailCommandRecord record:todoList) {
			String subject = record.getSubject();
			String info = record.getCommandBasicInfo();
			C.pl(info);
			
			String command = null;
			if(StrUtil.contains(subject, "@")) {
				if(selfCensorship) {
					CriteriaFilter pete = new CriteriaFilter(username, subject, ";");
					command = pete.getFixedCommand();
				}
			} else {
				String[] params = StrUtil.parseParams("(\\$|)(.*?)", subject);
				boolean toReply = params[0].isEmpty();
				command = params[1];
				if(toReply) {
					String replyTo = record.getReplyToString(username);
					command += ">" + replyTo;
				}
			}
			
			CommandHistoryManager.g().collect(info);
			Janitor.g().process(command);
			record.setStatus(EmailCommandRecord.DONE);
		}
	}
	
	public String getKonfigInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("account:" + EmailCenter.g().getUsername());
		sb.append(", minutes to live:" + mins2live);
		sb.append(", seconds to refresh:" + seconds2refresh);
		
		return sb.toString();
	}
	
	public List<EmailCommandRecord> getAllRemoteCommands() {
		List<EmailCommandRecord> files = getAllRecords();
		
		return files;
	}
	
	private List<EmailCommandRecord> updateCommandsFromRemote(String username, String password) {		
		EmailFetcher mike = new EmailFetcher(username, password);
		List<EmailCommandRecord> records = mike.fetch();
		List<EmailCommandRecord> todoList = new ArrayList<EmailCommandRecord>();
		
		boolean hasChange = false;
		for(EmailCommandRecord record:records) {
			if(getAllRecords().indexOf(record) >= 0) {
				continue;
			}
			
			hasChange = true;
			getAllRecords().add(record);
			
			if(record.isExpired(mins2live)) {
				record.setStatus(EmailCommandRecord.EXPIRED);
				continue;
			}
			
			if(record.isIgnorable()) {
				record.setStatus(EmailCommandRecord.IGNORED);
				continue;
			}
			
			record.setStatus(EmailCommandRecord.DOING);
			
			todoList.add(record);
		}
		
		if(hasChange) {
			saveAllRecords();
		}
		
		return todoList;
	}
	
	public String getFilename() {
		return StrUtil.occupy("COMMANDS_{0}.txt", EmailCenter.g().getUsername());
	}
	
	private void saveAllRecords() {
		String filePath = location + getFilename();
		List<EmailCommandRecord> list = getAllRecords();
		Collections.sort(list);
		
		MexUtil.saveAsNew(getAllRecords(), filePath);
	}
}
