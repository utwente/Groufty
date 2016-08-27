package nl.javalon.groufty.build;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This file runs at compile time (see pom.xml), and requires a functioning git binary on PATH
 * Generates a file with build-time version info.
 * @author Lukas Miedema
 */
public class BuildInfoGenerator {

	public static final String BUILD_FILE = "build.properties";

	private static final String COMMIT_COUNT_CMD = "git rev-list --count HEAD";
	private static final String BRANCH_NAME_CMD = "git describe --all";
	private static final String SHORT_COMMIT_CMD = "git rev-parse --short HEAD";
	private static final String COMMIT_TAG_CMD = "git tag -l --contains HEAD";

	public static void main(String[] args) throws IOException, InterruptedException {

		// Get commit count
		Process commitCountCmd = Runtime.getRuntime().exec(COMMIT_COUNT_CMD);
		commitCountCmd.waitFor();
		int commitCount = new Scanner(commitCountCmd.getInputStream()).nextInt();

		// Get the branch name
		Process branchNameCmd = Runtime.getRuntime().exec(BRANCH_NAME_CMD);
		branchNameCmd.waitFor();
		String branchName = new Scanner(branchNameCmd.getInputStream()).nextLine();
		branchName = branchName.substring(branchName.lastIndexOf('/') < 0 ? 0 : branchName.lastIndexOf('/') + 1);

		// Get short commit
		Process shortCommitCmd = Runtime.getRuntime().exec(SHORT_COMMIT_CMD);
		shortCommitCmd.waitFor();
		String shortCommitHash = new Scanner(shortCommitCmd.getInputStream()).nextLine();

		// Get commit tag
		Process commitTagCmd = Runtime.getRuntime().exec(COMMIT_TAG_CMD);
		commitTagCmd.waitFor();
		List<String> commitTags = new LinkedList<>();
		try (Scanner s = new Scanner(commitTagCmd.getInputStream())) {
			while (s.hasNextLine()) {
				commitTags.add(s.nextLine());
			}
		}

		// Append to application.yml
		File file = new File("target/classes/" + BUILD_FILE);
		System.out.println(file.getAbsoluteFile());
		try (PrintWriter writer = new PrintWriter(file)) {

			writer.println("groufty.build.commit-count: " + commitCount);
			writer.println("groufty.build.branch-name: " + branchName);
			writer.println("groufty.build.commit-hash: " + shortCommitHash);
			writer.println("groufty.build.timestamp: " + System.currentTimeMillis());
			writer.println("groufty.build.commit-tags: " + commitTags.stream().collect(Collectors.joining(",")));
		}
	}
}
