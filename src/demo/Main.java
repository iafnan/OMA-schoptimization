package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import timetabling.InstanceReader;
import timetabling.InstanceSolver;
import timetabling.ettp.EttpSolution;

public class Main {

	@Option(name = "-t", usage = "timelimit in seconds", required = false, metaVar = "timelimit")
	private int timelimit;

	// receives other command line parameters than options
	@Argument(usage = "instance name")
	private List<String> arguments = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		new Main().doMain(args);
	}

	public void doMain(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		CmdLineParser parser = new CmdLineParser(this);

		// if you have a wider console, you could increase the value;
		// here 80 is also the default
		parser.setUsageWidth(80);

		try {
			// parse the arguments.
			parser.parseArgument(args);

			// you can parse additional arguments if you want.
			// parser.parseArgument("more","args");

			// after parsing arguments, you should check
			// if enough arguments are given.
			if (arguments.isEmpty())
				throw new CmdLineException(parser, "No argument is given");

		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err.println("ETPsolver_OMAAL_group03.exe [options...] instancename");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			return;
		}

		String instance_name = arguments.get(0);

		InstanceReader ir = new InstanceReader(instance_name);
		long estimatedTime = System.currentTimeMillis() - startTime;
		InstanceSolver is = new InstanceSolver();
		EttpSolution sol = is.solve(ir.getData(), timelimit * 1000 - estimatedTime);
		sol.printToFile(instance_name);
	}
}
