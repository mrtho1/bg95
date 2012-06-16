package com.thompson234.bg95.cli;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.thompson234.bg95.Bg95Configuration;
import com.thompson234.bg95.model.Airman;
import com.thompson234.bg95.model.CrewAssignment;
import com.thompson234.bg95.model.Mission;
import com.thompson234.bg95.model.Sortie;
import com.yammer.dropwizard.AbstractService;
import com.yammer.dropwizard.cli.ConfiguredCommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DisplayAirmanMissionsCommand extends ConfiguredCommand<Bg95Configuration> {

    public DisplayAirmanMissionsCommand() {
        super("airman-missions", "Displays mission data for a given airman.");
    }

    @Override
    public Options getOptions() {
        final Option option = OptionBuilder.isRequired()
                .hasArg(true)
                .withLongOpt("airman")
                .withDescription("Full name of airman")
                .withArgName("airman")
                .create('a');
        final Options options = new Options();
        options.addOption(option);
        return options;
    }

    @Override
    protected void run(AbstractService<Bg95Configuration> service,
                       Bg95Configuration configuration,
                       CommandLine params) throws Exception {

        final Airman airman = configuration.getAirmanDao().findByFullName(params.getOptionValue("airman"));
        final List<Mission> missions = configuration.getMissionDao().findAllByAirman(airman);

        for (Mission mission: missions) {
            System.out.println(String.format("%1$d. %2$tm/%2$td/%2$tY - %3$s", mission.getNumber(), mission.getDate(), mission.getDestination()));
            System.out.println(String.format("%d Total Sorties %d Completed %d Damaged %d Lost %d Salvaged", mission.getTookOff(), mission.getCompleted(), mission.getDamaged(), mission.getLost(), mission.getSalvaged()));
            System.out.println("Sorties:");

            int sortieIndex = 1;
            for (Sortie sortie: mission.getSorties()) {
                final ImmutableList<String> names = sortie.getAircraft().getNames();
                final StringBuilder nameBuilder = new StringBuilder();
                for (String name: sortie.getAircraft().getNames()) {

                    if (!Strings.isNullOrEmpty(name)) {

                        if (nameBuilder.length() > 0) {
                            nameBuilder.append(" / ");
                        }

                        nameBuilder.append(name);
                    }
                }

                System.out.println(String.format("\t%d. %s - %s",
                        sortieIndex,
                        sortie.getAircraft().getNumber(),
                        nameBuilder.toString()));

                List<CrewAssignment> sortedAssignments = Lists.newArrayList(sortie.getCrewAssignments());
                Collections.sort(sortedAssignments, new Comparator<CrewAssignment>() {
                    @Override
                    public int compare(CrewAssignment lhs, CrewAssignment rhs) {
                        return lhs.getAirman().compareTo(rhs.getAirman());
                    }
                });

                int assignmentIndex = 1;
                for (CrewAssignment assignment: sortedAssignments) {
                    System.out.println(String.format("\t\t%d. %s [%s] - %s\t%s",
                            assignmentIndex,
                            assignment.getAirman().getFullName(),
                            assignment.getAirman().getRank(),
                            assignment.getRole(),
                            assignment.getStatus()));
                    ++assignmentIndex;
                }

                ++sortieIndex;
            }
        }
    }
}
