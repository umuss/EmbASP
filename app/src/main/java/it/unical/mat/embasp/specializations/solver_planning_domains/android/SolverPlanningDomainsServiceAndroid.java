package it.unical.mat.embasp.specializations.solver_planning_domains.android;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import it.unical.mat.embasp.base.Callback;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.OptionDescriptor;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.platforms.android.AndroidService;
import it.unical.mat.embasp.platforms.android.AndroidUtility;
import it.unical.mat.embasp.specializations.solver_planning_domains.SolverPlanningDomainPlan;
import it.unical.mat.embasp.specializations.solver_planning_domains.SolverPlanningDomainsUtility;

public class SolverPlanningDomainsServiceAndroid extends AndroidService {

	
	
	private final SolverPlanningDomainsUtility spdu;

	public SolverPlanningDomainsServiceAndroid(final Context c) {
		super(c);
		spdu = new SolverPlanningDomainsUtility() {
			@Override
			protected String readFile(String s) throws IOException {
				return AndroidUtility.getRawTextFileFromName(c,s);
			}
		};
	}

	protected Output getOutput(final String output, final String error) {
		return new SolverPlanningDomainPlan(output, error);
	}

	@Override
	public void startAsync(final Callback callback, final List<InputProgram> programs,final List<OptionDescriptor> options) {

		new Thread() {
			@Override
			public void run() {
				if (programs.isEmpty())
					callback.callback(getOutput("", "PDDLInputProgram not defined"));
				try {
					callback.callback(getOutput(spdu.postJsonToURL(spdu.createJson(programs).toString()), ""));
				} catch (final Exception e) {
					callback.callback(getOutput("", "Error : " + e.getMessage()));
				}
			}
		}.start();

	}

}
