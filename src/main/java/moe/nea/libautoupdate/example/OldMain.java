package moe.nea.libautoupdate.example;

import moe.nea.libautoupdate.UpdateContext;
import moe.nea.libautoupdate.api.UpdateSource;
import moe.nea.libautoupdate.api.UpdateTarget;
import moe.nea.libautoupdate.updaters.ExitHookInvoker;
import moe.nea.libautoupdate.version.CurrentVersion;

public class OldMain {
	public static void main(String[] args) {

		UpdateContext updater = new UpdateContext(
			UpdateSource.githubUpdateSource("hannibal002", "SkyHanni"),
			UpdateTarget.deleteAndSaveInTheSameFolder(OldMain.class),
			CurrentVersion.ofTag("1.1.0"),
			"test"
		);
		updater.cleanup();
		System.out.println("Update cleaned");
		System.out.println("Created update context: " + updater);
		String stream = "pre";
		updater.checkUpdate(stream).thenCompose(it -> {
			System.out.println("Checked for update on " + stream + ": " + it);
			System.out.println("Can update: " + it.isUpdateAvailable());
			System.out.println("Executing update.");
			return it.launchUpdate();
		}).join();
		//System.out.println(ExitHookInvoker.getActionsForTesting());
	}
}
