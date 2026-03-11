package moe.nea.libautoupdate;

import moe.nea.libautoupdate.targets.UpdateAction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ExitHookInvoker {
	private static final Object LOCK = new Object();
	private static boolean hookRegistered = false;
	private static volatile ExitHookState state = null;

	private record ExitHookState(
			String identifier,
			UUID uuid,
			File updaterJar,
			List<UpdateAction> actions
	) {
		String[] buildInvocation() {
			boolean isWindows = System.getProperty("os.name", "")
					.startsWith("Windows");
			String javaBin = System.getProperty("java.home")
					+ "/bin/java" + (isWindows ? ".exe" : "");

			List<String> args = new ArrayList<>();
			args.add(javaBin);
			args.add("-jar");
			args.add(updaterJar.getAbsolutePath());
			args.add(identifier);
			args.add(uuid.toString());
			for (UpdateAction action : actions) {
				action.encode(args);
			}
			return args.toArray(String[]::new);
		}
	}

	private ExitHookInvoker() {}

	public static void setExitHook(
			String identifier,
			UUID uuid,
			File updaterJar,
			List<UpdateAction> actions
	) {
		synchronized (LOCK) {
			if (!hookRegistered) {
				Runtime.getRuntime().addShutdownHook(
						new Thread(
								ExitHookInvoker::runExitHook,
								"libautoupdate-exit"
						)
				);
				hookRegistered = true;
			}
			state = new ExitHookState(
					identifier, uuid, updaterJar, List.copyOf(actions)
			);
		}
	}

	public static void cancelExitHook() {
		state = null;
	}

	static List<UpdateAction> getActionsForTesting() {
		ExitHookState s = state;
		return s != null ? s.actions() : null;
	}

	private static void runExitHook() {
		ExitHookState current = state;
		if (current == null) {
			System.out.println("Skipping cancelled update.");
			return;
		}
		try {
			String[] invocation = current.buildInvocation();
			System.out.println(
					"Running post-updater: " + String.join(" ", invocation)
			);
			Runtime.getRuntime().exec(invocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}