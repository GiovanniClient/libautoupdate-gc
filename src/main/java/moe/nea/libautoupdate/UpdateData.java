package moe.nea.libautoupdate;

import com.google.gson.JsonElement;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UpdateData {
	protected String versionName;
	protected JsonElement versionNumber;

	public String getVersionName() {
		return versionName;
	}

	public JsonElement getVersionNumber() {
		return versionNumber;
	}

	public String getSha256() {
		return sha256;
	}

	public String getDownload() {
		return download;
	}

	protected String sha256;
	protected String download;

	// Manually defined constructor so the IDE never loses it
	public UpdateData(String versionName, JsonElement versionNumber, String sha256, String download) {
		this.versionName = versionName;
		this.versionNumber = versionNumber;
		this.sha256 = sha256;
		this.download = download;
	}

	public URI getDownloadAsURI() throws URISyntaxException {
		return new URI(download);
	}

	public String getRawFileName() throws URISyntaxException {
		var path = getDownloadAsURI().getRawPath();
		var file = UpdateUtils.lastStringSegment(path, "/");
		if (file == null || file.isEmpty())
			throw new RuntimeException("Download URL " + download + " does not contain a valid file name.");
		return file;
	}

	public URL getDownloadAsURL() throws MalformedURLException {
		return URI.create(download).toURL();
	}
}