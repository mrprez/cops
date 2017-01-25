package com.mrprez.gencross.impl.cops;

import java.util.Collection;
import java.util.HashSet;
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

	@Parameters
	public static Collection<Object[]> params() throws Exception {
		Set<Object[]> params = new HashSet<Object[]>();
		for (String stageName : PersonnageBuilder.newPersonnage("COPS").get().getProperty("Stages").getSubProperties().getOptions().keySet()) {
			params.add(new Object[] { stageName });
		}
		return params;
	}

	public StageRequirementTest(String stageName) {
		super();
		this.stageName = stageName;
	}

	@Test
	public void testStageRequirement() throws Exception {
		// GIVEN
		Personnage personnage = PersonnageBuilder.newPersonnage("COPS").get();
		Property stage = personnage.getProperty("Stages").getSubProperties().getOptions().get(stageName);

		// WHEN
		personnage.addPropertyToMotherProperty(stage);

		// THEN
		if (!stage.getName().contains(" Niv. 1")) {
			Assert.assertTrue(personnage.getErrors().contains("A la création, vous ne pouvez pas avoir de stage de niveau 2 ou 3"));
		}
		int stageRequirementErrorNb = 0;
		for(String error : personnage.getErrors()){
			if(error.startsWith("Prérequis du stage: ")){
				stageRequirementErrorNb++;
			}
		}
		Assert.assertEquals(personnage.getAppendix().getSubMap("requirement." + stage.getName()).size(), stageRequirementErrorNb);
		Assert.assertFalse(stage.getFullName() + " has no errors", personnage.getErrors().isEmpty());

	}
}
