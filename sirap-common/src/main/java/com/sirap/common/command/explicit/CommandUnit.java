package com.sirap.common.command.explicit;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.util.MathUtil;
import com.sirap.common.command.CommandBase;

public class CommandUnit extends CommandBase {
	
	@Override
	public boolean handle() {
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*(kg|lb|oz)");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.weight(value, params[1]));
			
			return true;
		}
		
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*(Fa|Ce)");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.temperature(value, params[1]));
			
			return true;
		}
		
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*(Yard|Chi|Feet|Cun|Inch|Cm)");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.distance(value, params[1]));
			
			return true;
		}
		
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*(Mile|Km|naut|nmi)");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.longDistance(value, params[1]));
			
			return true;
		}
				
		return false;
	}
}