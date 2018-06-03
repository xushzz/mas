package com.sirap.orm;

import java.io.File;
import java.util.List;

import com.sirap.basic.domain.MexZipEntry;
import com.sirap.basic.util.ArisUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.SatoUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.orm.mybatis.BatisManager;
import com.sirap.orm.mybatis.BatisUtil;

public class CommandBatis extends CommandBase {

	private static final String KEY_MYBATIS = "bat";
	private static final String KEY_ALIAS = "a";
	private static final String KEY_DTD = "d";

	@Override
	public boolean handle() throws Exception {
		if(is(KEY_MYBATIS + " " + KEY_ALIAS)) {
			boolean showAll = OptionUtil.readBooleanPRI(options, "a", true);
			List<List> items;
			if(showAll) {
				items = BatisUtil.getSessionAliases(BatisManager.g().getSession());
				BatisManager.g().closeSession();
			} else {
				items = BatisUtil.getBuiltInAliases();
			}
			
			options = OptionUtil.mergeOptions(options, "c=#s3");

			exportMatrix(items);
			return true;
		}
		
		if(is(KEY_MYBATIS + " " + KEY_DTD)) {
			String jarRegex = "rx:mybatis-\\d\\.\\d\\.\\d\\.jar";
			String batisJar = SatoUtil.kidOfJavaClassPath(jarRegex);
			List<MexZipEntry> items;
			if(FileUtil.exists(batisJar)) {
				items = ArisUtil.parseZipEntries(batisJar);
			} else {
				String source = g().getUserValueOf("mybatis.source");
				XXXUtil.nullCheck(source, "mybatis.source not exists");
				File file = parseFile(source);
				XXXUtil.nullCheck(file, ":Not exists " + source);
				items = ArisUtil.parseZipEntries(file.getAbsolutePath());
			}
			
			export2(items, "rx:\\.dtd$");

			return true;
		}
		
		return false;
	}

}
