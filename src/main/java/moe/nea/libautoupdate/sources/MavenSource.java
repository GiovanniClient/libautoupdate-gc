package moe.nea.libautoupdate.sources;

import com.google.gson.JsonPrimitive;
import moe.nea.libautoupdate.UpdateData;
import moe.nea.libautoupdate.UpdateSource;
import moe.nea.libautoupdate.UpdateUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MavenSource implements UpdateSource {
	String repoUrl;
	String module;
	String artifact;

	public MavenSource(String extension, String classifier, String artifact, String module, String repoUrl) {
		this.extension = extension;
		this.classifier = classifier;
		this.artifact = artifact;
		this.module = module;
		this.repoUrl = repoUrl;
	}

	String classifier;
	String extension;

	protected URI getMavenBaseUrl() {
		var uri = URI.create(repoUrl);
		for (String modulePart : UpdateUtils.stringSplitIterator(module, ".")) {
			uri = uri.resolve(UpdateUtils.urlEncode(modulePart) + "/");
		}
		uri = uri.resolve(UpdateUtils.urlEncode(artifact) + "/");
		return uri;
	}

	protected URI getMavenMetadataUrl() {
		return getMavenBaseUrl().resolve("maven-metadata.xml");
	}

	protected String getFileName(String version) {
		return String.format("%s-%s%s%s.%s", artifact, version, classifier.isEmpty() ? "" : "-", classifier, extension);
	}

	protected URI getMavenArtifactUrl(String version) {
		return getMavenBaseUrl().resolve(UpdateUtils.urlEncode(version) + "/")
				.resolve(UpdateUtils.urlEncode(getFileName(version)));
	}

	@Override
	public CompletableFuture<UpdateData> checkUpdate(String updateStream) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				var dbf = DocumentBuilderFactory.newInstance();
				dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				var db = dbf.newDocumentBuilder();

				try (var is = UpdateUtils.openUrlConnection(getMavenMetadataUrl().toURL())) {
					var document = db.parse(new InputSource(is));

					var metadata = document.getDocumentElement();
					var versioning = (Element) metadata.getElementsByTagName("versioning").item(0);
					var latest = (Element) versioning.getElementsByTagName("latest").item(0);
					var release = (Element) versioning.getElementsByTagName("release").item(0);

					if (release != null && ("full".equals(updateStream) || latest == null)) {
						latest = release;
					}

					var latestVersion = latest.getTextContent();

					return new UpdateData(
							latestVersion,
							new JsonPrimitive(latestVersion),
							null,
							getMavenArtifactUrl(latestVersion).toString()
					);

				}
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}
}