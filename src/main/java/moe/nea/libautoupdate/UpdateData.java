package moe.nea.libautoupdate;

import com.google.gson.JsonElement;
import moe.nea.libautoupdate.util.FileUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UpdateData {
	private final String versionName;
	private final JsonElement versionNumber;
	private final String sha256;
	private final String download;

	public UpdateData(
			String versionName,
			JsonElement versionNumber,
			String sha256,
			String download
	) {
		this.versionName = versionName;
		this.versionNumber = versionNumber;
		this.sha256 = sha256;
		this.download = download;
	}

	public String getVersionName() { return versionName; }
	public JsonElement getVersionNumber() { return versionNumber; }
	public String getSha256() { return sha256; }
	public String getDownload() { return download; }

	public URI getDownloadAsURI() throws URISyntaxException {
		return new URI(download);
	}

	public URL getDownloadAsURL() throws MalformedURLException {
		return URI.create(download).toURL();
	}

	public String getRawFileName() throws URISyntaxException {
		String path = getDownloadAsURI().getRawPath();
		String file = FileUtils.lastSegment(path, "/");
		if (file == null || file.isEmpty()) {
			throw new IllegalStateException(
					"Download URL has no valid file name: " + download
			);
		}
		return file;
	}
}