package moe.nea.libautoupdate.source;

import com.google.gson.JsonPrimitive;
import moe.nea.libautoupdate.UpdateData;
import moe.nea.libautoupdate.api.UpdateSource;
import moe.nea.libautoupdate.util.FileUtils;
import moe.nea.libautoupdate.util.HttpClient;
import moe.nea.libautoupdate.util.StringUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MavenSource implements UpdateSource {
	private final String repoUrl;
	private final String module;
	private final String artifact;
	private final String classifier;
	private final String extension;

	private MavenSource(Builder builder) {
		this.repoUrl = builder.repoUrl;
		this.module = builder.module;
		this.artifact = builder.artifact;
		this.classifier = builder.classifier;
		this.extension = builder.extension;
	}

	public static Builder builder(
			String repoUrl, String module, String artifact
	) {
		return new Builder(repoUrl, module, artifact);
	}

	public static class Builder {
		private final String repoUrl;
		private final String module;
		private final String artifact;
		private String classifier = "";
		private String extension = "jar";

		private Builder(
				String repoUrl, String module, String artifact
		) {
			this.repoUrl = repoUrl;
			this.module = module;
			this.artifact = artifact;
		}

		public Builder classifier(String classifier) {
			this.classifier = classifier;
			return this;
		}

		public Builder extension(String extension) {
			this.extension = extension;
			return this;
		}

		public MavenSource build() {
			return new MavenSource(this);
		}
	}

	URI getMavenBaseUrl() {
		URI uri = URI.create(repoUrl);
		for (String part : StringUtils.splitIterator(module, ".")) {
			uri = uri.resolve(FileUtils.urlEncode(part) + "/");
		}
		return uri.resolve(FileUtils.urlEncode(artifact) + "/");
	}

	URI getMavenMetadataUrl() {
		return getMavenBaseUrl().resolve("maven-metadata.xml");
	}

	String getFileName(String version) {
		String cls = classifier.isEmpty() ? "" : "-" + classifier;
		return String.format(
				"%s-%s%s.%s", artifact, version, cls, extension
		);
	}

	URI getArtifactUrl(String version) {
		return getMavenBaseUrl()
				.resolve(FileUtils.urlEncode(version) + "/")
				.resolve(FileUtils.urlEncode(getFileName(version)));
	}

	@Override
	public CompletableFuture<UpdateData> checkUpdate(UpdateStream stream) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				var dbf = DocumentBuilderFactory.newInstance();
				dbf.setFeature(
						XMLConstants.FEATURE_SECURE_PROCESSING, true
				);
				var db = dbf.newDocumentBuilder();

				try (var is = HttpClient.open(
						getMavenMetadataUrl().toURL())) {
					var doc = db.parse(new InputSource(is));
					var root = doc.getDocumentElement();
					var versioning = (Element) root
							.getElementsByTagName("versioning").item(0);
					var latest = (Element) versioning
							.getElementsByTagName("latest").item(0);
					var release = (Element) versioning
							.getElementsByTagName("release").item(0);

					if (release != null
							&& ("full".equals(stream)
							|| latest == null)) {
						latest = release;
					}

					String version = latest.getTextContent();
					return new UpdateData(
							version,
							new JsonPrimitive(version),
							null,
							getArtifactUrl(version).toString()
					);
				}
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}
}