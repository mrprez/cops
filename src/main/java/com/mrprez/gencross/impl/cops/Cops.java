package com.mrprez.gencross.impl.cops;

import java.util.HashMap;
import java.util.Map;

import com.mrprez.gencross.Personnage;
import com.mrprez.gencross.Property;
import com.mrprez.gencross.history.MapHistoryFactory;
import com.mrprez.gencross.history.ProportionalHistoryFactory;
import com.mrprez.gencross.value.IntValue;
import com.mrprez.gencross.value.Value;

public class Cops extends Personnage {
	private final static String[] BASE_SOCIAL_COMPETENCES = new String[]{"Éloquence", "Intimidation", "Rhétorique"};
	private final static String[] BASE_CAC_SPE = new String[]{"coups", "projections", "immobilisations"};
	private final static String STAGE_REQUIREMENT_PREFIX = "Prérequis du stage: ";
	
	
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
	public boolean phaseFinished(){
		calculate();
		for(String error : errors){
			if (!error.startsWith(STAGE_REQUIREMENT_PREFIX)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void calculate() {
		super.calculate();
		calculateLangues();
		calculateTirRafale();
		calculateStageRequirement();
		if(!getPhase().equals("En service")){
			calculateCaracteristiques();
			limiteStageLevel();
		}
		if(getPhase().equals("Compétences de bases")){
			calculateBaseCompetences();
		}
		if(getPhase().equals("Compétences")){
			calculateCompetences();
		}
		if(getPhase().equals("Origine sociale")){
			if(getProperty("Equipement").getSubProperties().size()!=1){
				errors.add("Vous avez le droit à un et un seul équipement supplémentaire");
			}
		}
		if(getPhase().equals("Relations supplémentaires")){
			calculateRelation();
		}
	}
	
	private void calculateStageRequirement() {
		for(Property stage : getProperty("Stages").getSubProperties()){
			for(String requirement : appendix.getSubMap("requirement."+stage.getName()).values()){
				StringBuilder completeMsg = new StringBuilder();
				for (String requirementClause : requirement.split("[|]")) {
					String errorMsg = calculateOneRequirement(requirementClause);
					if (errorMsg != null) {
						if (completeMsg.length() > 0) {
							completeMsg.append(" ou ");
						}
						completeMsg.append(errorMsg);
					} else {
						completeMsg = new StringBuilder();
						break;
					}
				}
				if (completeMsg.length() > 0) {
					errors.add(STAGE_REQUIREMENT_PREFIX + stage.getName() + ": " + completeMsg);
				}
			}
			if (stage.getName().startsWith("Communication médiatique Niv. 3")) {
				calculateMassGuruStageRequirement();
			}
			if (stage.getName().startsWith("Contrôle des biens et des contrefaçons Niv. 2")) {
				calculateReceleurStageRequirement(stage);
			}
			if (stage.getName().startsWith("Manœuvres et mouvements Niv. 2")) {
				calculateBulldozerStageRequirement();
			}
			if (stage.getName().startsWith("Sensibilisation culturelle Niv. 2")) {
				calculateEtCaresserLesChiensStageRequirement(stage);
			}
			if (stage.getName().startsWith("Pilotage et intervention en milieu urbain Niv. 2")) {
				calculateSupercopterStageRequirement(stage);
			}
			if (stage.getName().startsWith("Législation et droit du citoyen Niv. 3")) {
				calculateMonsieurLeProcureurStageRequirement();
			}
			if (stage.getName().startsWith("Lutte anti-drogue (NADIV) Niv. 2")) {
				calculateDopeMasterStageRequirement();
			}
			if (stage.getName().startsWith("Technique d`interrogatoire Niv. 1")) {
				calculateLeBonLaBruteEtLeTruand(stage);
			}
			if (stage.getName().startsWith("Technique de l`informatique Niv. 2")) {
				calculateDosMaster1_0(stage);
			}
			if (stage.getName().startsWith("Technique de l`informatique Niv. 3")) {
				calculateKeyboardMaestro2_0(stage);
			}
			if (stage.getName().startsWith("Tir Niv. 1")) {
				calculatePatGarrett(stage);
			}
			if (stage.getName().startsWith("Tir Niv. 2")) {
				calculateWyattEarp(stage);
			}
			if (stage.getName().startsWith("Tir Niv. 3")) {
				calculateLeeHarveyOswald(stage);
			}
		}
	}

	private void calculatePatGarrett(Property stage) {
		String error = calculateOneRequirement("Compétences#" + stage.getSpecification() + ":6");
		if (error != null) {
			errors.add(STAGE_REQUIREMENT_PREFIX + error);
		}
	}

	private void calculateWyattEarp(Property stage) {
		String error = calculateOneRequirement("Compétences#" + stage.getSpecification() + ":5");
		if (error != null) {
			errors.add(STAGE_REQUIREMENT_PREFIX + error);
		}
	}

	private void calculateLeeHarveyOswald(Property stage) {
		String error = calculateOneRequirement("Compétences#" + stage.getName().split(" - ")[1] + ":4");
		if (error != null) {
			errors.add(STAGE_REQUIREMENT_PREFIX + error);
		}
	}

	private void calculateKeyboardMaestro2_0(Property stage) {
		if (stage.getSpecification().equals("Trojan")) {
			if (getProperty("Compétences#Informatique#piratage") == null || getProperty("Compétences#Informatique#piratage").getValue().getInt() > 4) {
				errors.add(STAGE_REQUIREMENT_PREFIX + "Technique de l`informatique Niv. 3 - Trojan: Informatique[piratage]: 4+ nécessaire");
			}
			if (getProperty("Stages#Technique de l`informatique Niv. 2 - Scanner") == null) {
				errors.add(STAGE_REQUIREMENT_PREFIX + "Technique de l`informatique Niv. 3 - Trojan: Scanner nécessaire");
			}
		}
		if (stage.getSpecification().equals("Big Brother")) {
			if (getProperty("Compétences#Informatique#programmation") == null || getProperty("Compétences#Informatique#programmation").getValue().getInt() > 4) {
				errors.add(STAGE_REQUIREMENT_PREFIX + "Technique de l`informatique Niv. 3 - Big Brother: Informatique[programmation]: 4+ nécessaire");
			}
			if (getProperty("Stages#Technique de l`informatique Niv. 2 - Scan FaceBook Master") == null) {
				errors.add(STAGE_REQUIREMENT_PREFIX + "Technique de l`informatique Niv. 3 - Big Brother: Scan FaceBook Master nécessaire");
			}
		}
	}

	private void calculateDosMaster1_0(Property stage) {
		if(stage.getSpecification().equals("Scanner")){
			if (getProperty("Compétences#Informatique#piratage") == null || getProperty("Compétences#Informatique#piratage").getValue().getInt() > 5) {
				errors.add(STAGE_REQUIREMENT_PREFIX + "Technique de l`informatique Niv. 2 - Scanner: Informatique[piratage]: 5+ nécessaire");
			}
			if (getProperty("Stages#Technique de l`informatique Niv. 1 - LAPD Software") == null) {
				errors.add(STAGE_REQUIREMENT_PREFIX + "Technique de l`informatique Niv. 2 - Scanner: LAPD Software nécessaire");
			}
		}
		if (stage.getSpecification().equals("Scan FaceBook Master")) {
			if (getProperty("Compétences#Informatique#programmation") == null || getProperty("Compétences#Informatique#programmation").getValue().getInt() > 5) {
				errors.add(STAGE_REQUIREMENT_PREFIX + "Technique de l`informatique Niv. 2 - Scanner: Informatique[programmation]: 5+ nécessaire");
			}
			if (getProperty("Stages#Technique de l`informatique Niv. 1 - Google Geek") == null) {
				errors.add(STAGE_REQUIREMENT_PREFIX + "Technique de l`informatique Niv. 2 - Scanner: Google Geek nécessaire");
			}
		}
	}

	private void calculateLeBonLaBruteEtLeTruand(Property stage) {
		Property competence = getProperty("Compétences").getSubProperty(stage.getSpecification());
		if(competence.getValue().getInt()>6){
			errors.add(STAGE_REQUIREMENT_PREFIX + stage.getFullName() + ": " + competence.getName() + ": 6+ nécessaire");
		}
		if (competence.getName().equals("Éloquence") && getProperty("Caracteristiques#Charme").getValue().getInt() < 3) {
			errors.add(STAGE_REQUIREMENT_PREFIX + stage.getFullName() + ": Charme 3 nécessaire");
		} else if (competence.getName().equals("Intimidation") && getProperty("Caracteristiques#Sang froid").getValue().getInt() < 3) {
			errors.add(STAGE_REQUIREMENT_PREFIX + stage.getFullName() + ": Sang froid 3 nécessaire");
		} else if (competence.getName().equals("Rhétorique") && getProperty("Caracteristiques#Education").getValue().getInt() < 3) {
			errors.add(STAGE_REQUIREMENT_PREFIX + stage.getFullName() + ": Education 3 nécessaire");
		}
	}

	private void calculateMonsieurLeProcureurStageRequirement() {
		Property relationLevel2 = null;
		for (Property relation : getProperty("Relations").getSubProperties()) {
			if (relation.getValue().getInt() >= 2) {
				relationLevel2 = relation;
			}
		}
		if (relationLevel2 == null) {
			errors.add(STAGE_REQUIREMENT_PREFIX + "Législation et droit du citoyen Niv. 3: Relation de niveau 2 dans une organisation criminelle");
		}
	}

	private void calculateDopeMasterStageRequirement() {
		Property relationLevel2 = null;
		for (Property relation : getProperty("Relations").getSubProperties()) {
			if (relation.getValue().getInt() >= 2) {
				relationLevel2 = relation;
			}
		}
		if (relationLevel2 == null) {
			errors.add(STAGE_REQUIREMENT_PREFIX + "Lutte anti-drogue (NADIV) Niv. 2: Relation de niveau 2 au sein du parti majoritaire à Los: Angeles ou du bureau du procureur.");
		}
	}

	private void calculateSupercopterStageRequirement(Property stage) {
		Property competence = getProperty("Compétences#Pilotage").getSubProperty(stage.getSpecification());
		if(competence==null || competence.getValue().getInt()>6){
			errors.add(STAGE_REQUIREMENT_PREFIX + stage.getName() + ": Pilotage/" + stage.getSpecification() + " 6+ nécessaire");
		}
	}

	private void calculateEtCaresserLesChiensStageRequirement(Property stage) {
		Property connaissance = getProperty("Compétences#Connaissance").getSubProperty(stage.getSpecification());
		if(connaissance==null || connaissance.getValue().getInt() > 5) {
			errors.add(STAGE_REQUIREMENT_PREFIX + "Sensibilisation culturelle Niv. 2: Connaissance appropriée d'une culture particulière dans un quartier spécifique: 5+");
		}
	}

	private void calculateMassGuruStageRequirement() {
		Property ocobRelation = null;
		for (Property relation : getProperty("Relations").getSubProperties()) {
			if (relation.getName().toUpperCase().contains("OCOB")) {
				if (ocobRelation == null || ocobRelation.getValue().getInt() < relation.getValue().getInt()) {
					ocobRelation = relation;
				}
			}
		}
		if (ocobRelation == null || ocobRelation.getValue().getInt() < 2) {
			errors.add(STAGE_REQUIREMENT_PREFIX + "Communication médiatique Niv. 3: Relation au sein de l’OCOB de niveau 2");
		}
	}

	private void calculateReceleurStageRequirement(Property stage) {
		Property connaissance = getProperty("Compétences#Connaissance#" + stage.getSpecification());
		if (connaissance == null || connaissance.getValue().getInt() > 6) {
			errors.add(STAGE_REQUIREMENT_PREFIX + stage.getFullName() + ": Connaissance/" + stage.getSpecification() + ": 6+");
		}
	}

	private void calculateBulldozerStageRequirement() {
		int level1StageNb = 0;
		for (Property stage : getProperty("Stages").getSubProperties()) {
			if (stage.getName().startsWith("Manœuvres et mouvements Niv. 1")) {
				level1StageNb++;
			}
		}
		if (level1StageNb < 2) {
			errors.add(STAGE_REQUIREMENT_PREFIX + "Manœuvres et mouvements Niv. 1: Stage niveau 1 (x2: minimum)");
		}
	}

	private String calculateOneRequirement(String requirement) {
		if (requirement.contains(":")) {
			String propertyName = requirement.split(":")[0];
			int limit = Integer.parseInt(requirement.split(":")[1]);
			Property property = getProperty(propertyName);
			if (propertyName.startsWith("Compétences#")) {
				if (property == null || property.getValue().getInt() > limit) {
					return propertyName + ": " + limit + "+";
				}
			} else {
				if (property == null || property.getValue().getInt() < limit) {
					return propertyName + ": " + limit;
				}
			}
		} else {
			if (requirement.startsWith("Stages#")) {
				String stageName = requirement.split("#")[1];
				for (Property stage : getProperty("Stages").getSubProperties()) {
					if (stage.getName().equals(stageName)) {
						return null;
					}
				}
				return stageName;
			}
			if (getProperty(requirement) == null) {
				return requirement;
			}
		}
		return null;
	}


	private void limiteStageLevel() {
		for(Property stage : getProperty("Stages").getSubProperties()){
			if(stage.getName().charAt(stage.getName().length()-1)!='1'){
				errors.add("A la création, vous ne pouvez pas avoir de stage de niveau 2 ou 3");
			}
		}
	}

	private void calculateTirRafale() {
		for(Property specialite : getProperty("Compétences#Tir en Rafale").getSubProperties()){
			Property tirComp = getProperty("Compétences").getSubProperty(specialite.getName());
			int tirLevel = tirComp.getValue().getInt();
			if(tirComp.getSubProperties()!=null){
				for(Property tirSpe : tirComp.getSubProperties()){
					if(tirSpe.getValue().getInt()<tirLevel){
						tirLevel = tirSpe.getValue().getInt();
					}
				}
			}
			if(specialite.getValue().getInt()<tirLevel){
				errors.add("Votre score de Tir en Rafale / "+specialite.getName()+" ne peut être inférieur à votre score en "+specialite.getName());
			}
		}
		
	}

	private void calculateLangues(){
		if(getProperty("Caracteristiques#Education").getValue().getInt() != getProperty("Langues").getSubProperties().size()){
			errors.add("Vous devez avoir autant de Langues que votre Caractéristique d'Education");
		}
	}
	
	private void calculateCaracteristiques(){
		int caracAt5Nb = 0;
		for(Property carac : getProperty("Caracteristiques").getSubProperties()){
			if(carac.getValue().getInt()>=5){
				caracAt5Nb++;
			}
		}
		if(caracAt5Nb>1){
			errors.add("Vous ne pouvez avoir plus d'une Caractéristique à 5 à la création");
		}
	}
	
	private void calculateRelation(){
		int relationOver4Nb = 0;
		for(Property relation : getProperty("Relations").getSubProperties()){
			if(relation.getValue().getInt()>=4){
				relationOver4Nb++;
			}
		}
		if(relationOver4Nb>2){
			errors.add("Vous ne pouvez avoir plus de 2 relations au niveau 4");
		}
	}
	
	private void calculateCompetences(){
		int baseCompetenceCount = 0;
		for(Property competence : getProperty("Compétences").getSubProperties()){
			if(competence.getValue()!=null){
				if(competence.getMax().getInt()<10){
					baseCompetenceCount = baseCompetenceCount + competence.getMax().getInt() - competence.getValue().getInt();
				}
			}
			if(competence.getSubProperties()!=null){
				for(Property specialite : competence.getSubProperties()){
					if(specialite.getMax().getInt()<10){
						baseCompetenceCount = baseCompetenceCount + specialite.getMax().getInt() - specialite.getValue().getInt();
					}
				}
			}
		}
		if(baseCompetenceCount>5){
			errors.add("Vous ne pouvez dépenser plus de 5 points dans les compétences de bases");
		}
	}
	
	private void calculateBaseCompetences(){
		int socialCompCount = 0;
		int social7CompCount = 0;
		for(String competenceName : BASE_SOCIAL_COMPETENCES){
			Property competence = getProperty("Compétences").getSubProperty(competenceName);
			if(competence.getValue().getInt()<10){
				socialCompCount++;
			}
			if(competence.getValue().getInt()==7){
				social7CompCount++;
			}
		}
		if(socialCompCount!=1 || social7CompCount!=1){
			getErrors().add("Vous devez positionner une et une seule des compétences suivantes à 7: Éloquence, Intimidation ou Rhétorique");
		}
		
		int cac7Count = 0;
		for(String specialite : BASE_CAC_SPE){
			if(getProperty("Compétences#Corps à Corps").getSubProperty(specialite)!=null && getProperty("Compétences#Corps à Corps").getSubProperty(specialite).getValue().getInt()==7){
				cac7Count++;
			}
		}
		if(cac7Count!=1 || getProperty("Compétences#Corps à Corps").getSubProperties().size()!=1){
			getErrors().add("Vous devez positionner une et une seule des spécialités de Corps à Corps suivantes à 7: coups, projections ou immobilisations");
		}
	}
	
	public void goToPhaseCompetence(){
		// Compétences sociales
		for(String competenceName : BASE_SOCIAL_COMPETENCES){
			Property competence = getProperty("Compétences").getSubProperty(competenceName);
			competence.setMax();
			competence.setMin(new IntValue(competence.getValue().getInt()-2));
		}
		
		// Spécialités de corps à corps
		Property cacCompetence = getProperty("Compétences#Corps à Corps");
		for(Property cacSpe : cacCompetence.getSubProperties()){
			cacSpe.setMax();
			cacSpe.setMin(new IntValue(cacSpe.getValue().getInt()-2));
		}
		for(Property cacSpe : cacCompetence.getSubProperties().getOptions().values()){
			cacSpe.setMax();
			cacSpe.setMin(new IntValue(cacSpe.getValue().getInt()-2));
		}
		Property cacSpe = cacCompetence.getSubProperties().getDefaultProperty();
		cacSpe.setMin(new IntValue(cacSpe.getValue().getInt()-2));
		
		// Set competences editable
		for(Property competence : getProperty("Compétences").getSubProperties()){
			if(competence.getValue()!=null){
				competence.setEditable(true);
				if(competence.getSubProperties()!=null && competence.getValue()!=null && competence.getValue().equals(competence.getSubProperties().getDefaultProperty().getValue())){
					competence.setEditable(false);
					competence.getSubProperties().setFixe(false);
					for (Property spe : competence.getSubProperties()) {
						spe.setEditable(true);
						spe.setMin(new IntValue(spe.getValue().getInt() - 2));
					}
				}
			}else{
				competence.getSubProperties().setFixe(false);
			}
		}
		
		// Competence history factory
		getProperty("Compétences").setHistoryFactory(new ProportionalHistoryFactory("Compétences", -1));
		for(Property competence : getProperty("Compétences").getSubProperties()){
			if(competence.getSubProperties()!=null){
				int startValue = competence.getSubProperties().getDefaultProperty().getValue().getInt();
				competence.getSubProperties().getDefaultProperty().setHistoryFactory(new ProportionalHistoryFactory("Compétences", -1, startValue));
				for(Property spe : competence.getSubProperties()){
					spe.setHistoryFactory(new ProportionalHistoryFactory("Compétences", -1, startValue));
				}
				for(Property spe : competence.getSubProperties().getOptions().values()){
					spe.setHistoryFactory(new ProportionalHistoryFactory("Compétences", -1, startValue));
				}
			}
		}
		getPointPools().get("Compétences").setToEmpty(true);
	}
	
	public void goToPhaseOrigineSociale(){
		getPointPools().get("Relations").add(2);
	}
	
	public void goToPhaseEtude(){
		for(Property competence : getProperty("Compétences").getSubProperties()){
			if(competence.getValue()!=null){
				competence.setMax();
				competence.setMin(new IntValue(2));
			}
			if(competence.getSubProperties()!=null){
				for(Property specialite : competence.getSubProperties()){
					specialite.setMax();
					specialite.setMin(new IntValue(2));
				}
				competence.getSubProperties().getDefaultProperty().setMin(new IntValue(2));
				for(Property specialite : competence.getSubProperties().getOptions().values()){
					specialite.setMin(new IntValue(2));
				}
			}
		}
		getPointPools().get("Compétences").add(2);
		getPointPools().get("Relations").add(1);
	}
	
	public void goToPhaseDevenirCops(){
		getPointPools().get("Relations").add(2);
		getPointPools().get("Adrénaline/Ancienneté").setToEmpty(true);
	}
	
	public void goToPhaseRelationSupplementaire(){
		getPointPools().get("Relations").add(2);
	}
	
	public void goToPhaseStage(){
		getPointPools().get("Stages").setToEmpty(true);
	}
	
	public void goToPhaseEnService() {
		// Caracteristiques history factory
		Map<Value, Integer> caracCostMap = new HashMap<Value, Integer>();
		caracCostMap.put(new IntValue(2), 0);
		caracCostMap.put(new IntValue(3), 14);
		caracCostMap.put(new IntValue(4), 35);
		caracCostMap.put(new IntValue(5), 63);
		getProperty("Caracteristiques").setHistoryFactory(new MapHistoryFactory(caracCostMap, "Expérience"));

		// Compétences history factory
		Map<Value, Integer> compCostMap = new HashMap<Value, Integer>();
		compCostMap.put(new IntValue(10), 0);
		compCostMap.put(new IntValue(9), 6);
		compCostMap.put(new IntValue(8), 8);
		compCostMap.put(new IntValue(7), 12);
		compCostMap.put(new IntValue(6), 18);
		compCostMap.put(new IntValue(5), 26);
		compCostMap.put(new IntValue(4), 36);
		compCostMap.put(new IntValue(3), 48);
		compCostMap.put(new IntValue(2), 62);
		getProperty("Compétences").setHistoryFactory(new MapHistoryFactory(compCostMap, "Expérience"));
		for (Property competence : getProperty("Compétences").getSubProperties()) {
			if (competence.getSubProperties() != null) {
				competence.getSubProperties().getDefaultProperty().setHistoryFactory(new MapHistoryFactory(compCostMap, "Expérience"));
				for (Property spe : competence.getSubProperties()) {
					spe.setHistoryFactory(new MapHistoryFactory(compCostMap, "Expérience"));
				}
				for (Property spe : competence.getSubProperties().getOptions().values()) {
					spe.setHistoryFactory(new MapHistoryFactory(compCostMap, "Expérience"));
				}
			}
		}

		// Stages history factory
		for (Property stage : getProperty("Stages").getSubProperties()) {
			stage.getHistoryFactory().setPointPool("Expérience");
		}
		for (Property stage : getProperty("Stages").getSubProperties().getOptions().values()) {
			stage.getHistoryFactory().setPointPool("Expérience");
		}

		// Points d'ancienneté
		getProperty("Points d'ancienneté").setHistoryFactory(new ProportionalHistoryFactory("Expérience", 15));
		getProperty("Points d'adrénaline").setHistoryFactory(new ProportionalHistoryFactory("Expérience", 15));

		// Relations
		Map<Value, Integer> relationCostMap = new HashMap<Value, Integer>();
		relationCostMap.put(new IntValue(1), 4);
		relationCostMap.put(new IntValue(2), 7);
		relationCostMap.put(new IntValue(3), 13);
		relationCostMap.put(new IntValue(4), 22);
		getProperty("Relations").setHistoryFactory(new MapHistoryFactory(relationCostMap, "Expérience"));

	}

	public void addStage(Property stage) {
		if (stage.getSpecification() == null && stage.getName().contains(Property.SPECIFICATION_SEPARATOR)) {
			stage.setSpecification(stage.getName().substring(stage.getName().lastIndexOf(Property.SPECIFICATION_SEPARATOR) + Property.SPECIFICATION_SEPARATOR.length()));
			stage.setName(stage.getName().substring(0, stage.getName().lastIndexOf(Property.SPECIFICATION_SEPARATOR)));
		}
	}

}
