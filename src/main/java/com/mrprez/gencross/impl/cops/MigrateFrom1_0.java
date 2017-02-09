package com.mrprez.gencross.impl.cops;

import com.mrprez.gencross.Version;
import com.mrprez.gencross.migration.MigrationPersonnage;
import com.mrprez.gencross.migration.Migrator;
import com.mrprez.gencross.value.IntValue;

public class MigrateFrom1_0 implements Migrator {

	@Override
	public MigrationPersonnage migrate(MigrationPersonnage migrationPersonnage) throws Exception {
		if (!migrationPersonnage.getPhase().equals("Caractéristiques")
				&& !migrationPersonnage.getPhase().equals("Compétences de bases")) {
			migrationPersonnage.getProperty("Compétences#Conduite#Voiture").setEditable(true);
			if (migrationPersonnage.getPhase().equals("Compétences")) {
				migrationPersonnage.getProperty("Compétences#Conduite#Voiture").setMax(
						new IntValue(migrationPersonnage.getProperty("Compétences#Conduite#Voiture").getValue().getInt() - 2));
			}
		}

		migrationPersonnage.getPluginDescriptor().setVersion(new Version(1, 1));
		return migrationPersonnage;
	}


}
