package com.mrprez.gencross.impl.cops;

import java.util.HashMap;
import java.util.Map;

import com.mrprez.gencross.Property;
import com.mrprez.gencross.listener.PassToNextPhaseListener;

public class SetMaxListener extends PassToNextPhaseListener {

	private String pattern;

	@Override
	public void callPassToNextPhase() throws Exception {
		for(Property property : getPersonnage().getProperties()){
			treatProperty(property);
		}
	}

	private void treatProperty(Property property){
		if(property.getAbsoluteName().matches(pattern)){
			property.setMax();
		}
		if(property.getSubProperties()!=null){
			for(Property subProperty : property.getSubProperties()){
				treatProperty(subProperty);
			}
			if(property.getSubProperties().getDefaultProperty()!=null){
				treatProperty(property.getSubProperties().getDefaultProperty());
			}
			if(property.getSubProperties().getOptions()!=null){
				for(Property option : property.getSubProperties().getOptions().values()){
					treatProperty(option);
				}
			}
		}
	}

	@Override
	public Map<String, String> getArgs() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("pattern", pattern);
		return result;
	}

	@Override
	public void setArgs(Map<String, String> args) throws Exception {
		pattern = args.get("pattern");
	}

}
