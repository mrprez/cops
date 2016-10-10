package com.mrprez.gencross.impl.cops;

import com.mrprez.gencross.Personnage;
import com.mrprez.gencross.Property;
import com.mrprez.gencross.value.Value;

public class Cops extends Personnage {
	
	public void changeCompetences(Property competence, Value oldValue){
		if(competence.getSubProperties()!=null){
			competence.getSubProperties().setFixe(competence.getSubProperties().getDefaultProperty().getValue().getInt() != competence.getValue().getInt());
		}
	}
	
	public boolean checkCompetences(Property competence, Value newValue){
		if(competence.getSubProperties()!=null){
			if(competence.getSubProperties().size()>0){
				setActionMessage("Vous ne pouvez modifier une compétence avec une ou plusieurs spécialités");
				return false;
			}
		}
		return true;
	}
	
}
