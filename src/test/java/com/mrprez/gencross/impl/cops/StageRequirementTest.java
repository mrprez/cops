package com.mrprez.gencross.impl.cops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mrprez.gencross.Personnage;
import com.mrprez.gencross.Property;
import com.mrprez.gencross.test.util.PersonnageBuilder;

@RunWith(Parameterized.class)
public class StageRequirementTest {
	private String stageName;
	private Set<String> additionalErrors;

	@Parameters(name = "{0}")
	public static Collection<Object[]> params() throws Exception {
		List<Object[]> params = new ArrayList<Object[]>();
		for (String stageName : PersonnageBuilder.newPersonnage("COPS").get().getProperty("Stages").getSubProperties().getOptions().keySet()) {
			Set<String> additionalErrors = new HashSet<String>();
			if (stageName.startsWith("Communication médiatique Niv. 3")) {
				additionalErrors.add("Communication médiatique Niv. 3: Relation au sein de l’OCOB de niveau 2");
			} else if (stageName.startsWith("Contrôle des biens et des contrefaçons Niv. 2")) {
				additionalErrors.add("Connaissance/MySpecification: 6+");
			} else if (stageName.startsWith("Manœuvres et mouvements Niv. 2")) {
				additionalErrors.add("Manœuvres et mouvements Niv. 1: Stage niveau 1 (x2: minimum)");
			} else if (stageName.startsWith("Sensibilisation culturelle Niv. 2")) {
				additionalErrors.add("Sensibilisation culturelle Niv. 2: Connaissance appropriée d'une culture particulière dans un quartier spécifique: 5+");
			} else if (stageName.startsWith("Pilotage et intervention en milieu urbain Niv. 2 - hélicoptère")) {
				additionalErrors.add("Pilotage/hélicoptère 6+ nécessaire");
			} else if (stageName.startsWith("Pilotage et intervention en milieu urbain Niv. 2 - VTOL")) {
				additionalErrors.add("Pilotage/VTOL 6+ nécessaire");
			} else if (stageName.startsWith("Législation et droit du citoyen Niv. 3")) {
				additionalErrors.add("Législation et droit du citoyen Niv. 3: Relation de niveau 2 dans une organisation criminelle");
			} else if (stageName.startsWith("Lutte anti-drogue (NADIV) Niv. 2")) {
				additionalErrors.add("Lutte anti-drogue (NADIV) Niv. 2: Relation de niveau 2 au sein du parti majoritaire à Los: Angeles ou du bureau du procureur.");
			} else if (stageName.startsWith("Technique d`interrogatoire Niv. 1")) {
				additionalErrors.add("Eloquence: 6+ nécessaire");
				additionalErrors.add("Charme 3 nécessaire");
			} else if (stageName.startsWith("Technique de l`informatique Niv. 2 - Scanner")) {
				additionalErrors.add("Technique de l`informatique Niv. 2 - Scanner: Informatique[piratage]: 5+ nécessaire");
				additionalErrors.add("Technique de l`informatique Niv. 2 - Scanner: LAPD Software nécessaire");
			} else if (stageName.startsWith("Technique de l`informatique Niv. 2 - Scan FaceBook Master")) {
				additionalErrors.add("Technique de l`informatique Niv. 2 - Scanner: Informatique[programmation]: 5+ nécessaire");
				additionalErrors.add("Technique de l`informatique Niv. 2 - Scanner: Google Geek nécessaire");
			} else if (stageName.startsWith("Technique de l`informatique Niv. 3 - Trojan")) {
				additionalErrors.add("Technique de l`informatique Niv. 2 - Scanner: Informatique[piratage]: 4+ nécessaire");
				additionalErrors.add("Technique de l`informatique Niv. 3 - Trojan: Scanner nécessaire");
			} else if (stageName.startsWith("Technique de l`informatique Niv. 3 - Big Brother")) {
				additionalErrors.add("Technique de l`informatique Niv. 2 - Scanner: Informatique[programmation]: 4+ nécessaire");
				additionalErrors.add("Technique de l`informatique Niv. 2 - Scanner: Scan FaceBook Master");
			} else if (stageName.startsWith("Tir Niv. 1")) {
				additionalErrors.add("Compétences#Arme de Poing: 6+");
			} else if (stageName.startsWith("Tir Niv. 2")) {
				additionalErrors.add("Compétences#Arme de Poing: 5+");
			} else if (stageName.startsWith("Tir Niv. 3")) {
				additionalErrors.add("Compétences#Arme de Poing: 4+");
			}

			params.add(new Object[] { stageName, additionalErrors });
		}

		return params;
	}

	public StageRequirementTest(String stageName, Set<String> additionalErrors) {
		super();
		this.stageName = stageName;
		this.additionalErrors = additionalErrors;
	}

	@Test
	public void testStageRequirement() throws Exception {
		// GIVEN
		Personnage personnage = PersonnageBuilder.newPersonnage("COPS").get();
		Property stage = personnage.getProperty("Stages").getSubProperties().getOptions().get(stageName);
		if (stage.getSpecification() != null) {
			stage.setSpecification("MySpecification");
		}

		// WHEN
		personnage.addPropertyToMotherProperty(stage);

		// THEN
		if (!stage.getName().contains(" Niv. 1")) {
			Assert.assertTrue(personnage.getErrors().contains("A la création, vous ne pouvez pas avoir de stage de niveau 2 ou 3"));
		}

		for (String requirement : personnage.getAppendix().getSubMap("requirement." + stage.getName()).values()) {
			String expectedError = "Prérequis du stage: " + stage.getName() + ": " + buildExpectedErrorMessage(requirement);
			Assert.assertTrue("Errors does not contains " + expectedError, personnage.getErrors().contains(expectedError));
		}

		int errorNb = 0;
		for (String error : personnage.getErrors()) {
			if (error.startsWith("Prérequis du stage: ")) {
				errorNb++;
			}
		}
		int expectedErrorNb = personnage.getAppendix().getSubMap("requirement." + stage.getName()).size() + additionalErrors.size();
		Assert.assertEquals("Requirement error nb for " + stageName, expectedErrorNb, errorNb);
	}

	private String buildExpectedErrorMessage(String requirement) {
		if (requirement.contains("|")) {
			return buildExpectedErrorMessage(requirement.substring(0, requirement.indexOf("|")))
					+ " ou "
					+ buildExpectedErrorMessage(requirement.substring(requirement.indexOf("|") + 1));
		}
		if (requirement.startsWith("Stages#")) {
			return requirement.substring(requirement.indexOf("#") + 1);
		}
		if (requirement.startsWith("Compétences#")) {
			return requirement.substring(0, requirement.lastIndexOf(':')) + ": " + requirement.substring(requirement.lastIndexOf(':') + 1) + "+";
		}
		if (requirement.contains(":")) {
			return requirement.substring(0, requirement.lastIndexOf(':')) + ": " + requirement.substring(requirement.lastIndexOf(':') + 1);
		}
		return requirement;
	}
}
