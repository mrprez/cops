package com.mrprez.gencross.impl.cops;

import com.mrprez.gencross.Personnage;
import com.mrprez.gencross.Property;
import com.mrprez.gencross.value.IntValue;
import com.mrprez.gencross.value.Value;

public class Cops extends Personnage {
	private final static String[] BASE_SOCIAL_COMPETENCES = new String[]{"Éloquence", "Intimidation", "Rhétorique"};
	private final static String[] BASE_CAC_SPE = new String[]{"coups", "projections", "immobilisations"};
	
	
	
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

	@Override
	public void calculate() {
		super.calculate();
		if(getPhase().equals("Compétences de bases")){
			calculateBaseCompetences();
		}
	}
	
	
	private void calculateBaseCompetences(){
		int socialCompCount = 0;
		for(String competence : BASE_SOCIAL_COMPETENCES){
			if(getProperty("Compétences").getSubProperty(competence).getValue().getInt()==7){
				socialCompCount++;
			}
		}
		if(socialCompCount!=1){
			getErrors().add("Vous devez positionner une des compétences suivantes à 7: Éloquence, Intimidation ou Rhétorique");
		}
		
		int cacCount = 0;
		for(String specialite : BASE_CAC_SPE){
			if(getProperty("Compétences#Corps à Corps").getSubProperty(specialite)!=null && getProperty("Compétences#Corps à Corps").getSubProperty(specialite).getValue().getInt()==7){
				cacCount++;
			}
		}
		if(cacCount!=1){
			getErrors().add("Vous devez positionner une des spécialités de Corps à Corps suivantes à 7: coups, projections ou immobilisations");
		}
	}
	
	public void goToPhaseCompetence(){
		for(Property competence : getProperty("Compétences").getSubProperties()){
			if(competence.getValue()!=null){
				if(competence.getSubProperties()==null){
					competence.setMin(new IntValue(competence.getValue().getInt() - 2));
				} else {
					int min = Math.max(
							competence.getSubProperties().getDefaultProperty().getValue().getInt(),
							competence.getValue().getInt() - 2);
					competence.setMin(new IntValue(min));
					if(competence.getValue().equals(competence.getSubProperties().getDefaultProperty().getValue())){
						competence.getSubProperties().setFixe(false);
					}
				}
			}else{
				competence.getSubProperties().setFixe(false);
			}
		}
	}
	
}
