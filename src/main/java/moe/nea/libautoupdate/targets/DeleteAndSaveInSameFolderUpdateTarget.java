package moe.nea.libautoupdate.targets;

import moe.nea.libautoupdate.PotentialUpdate;
import moe.nea.libautoupdate.actions.UpdateAction;
import moe.nea.libautoupdate.UpdateTarget;
import moe.nea.libautoupdate.UpdateUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class DeleteAndSaveInSameFolderUpdateTarget implements UpdateTarget {
    File file;

    public DeleteAndSaveInSameFolderUpdateTarget(File file) {
        this.file = file;
    }

    @Override
    public List<UpdateAction> generateUpdateActions(PotentialUpdate update) {
        try {
            File parentFolder = file.getParentFile();

            String fileName = update.getFileName();
            File targetFile = UpdateUtils.getContainedFile(parentFolder, fileName);

            return Arrays.asList(
                    new UpdateAction.DeleteFile(file),
                    new UpdateAction.MoveDownloadedFile(update.getUpdateJarStorage(), targetFile)
            );
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to resolve update target path", e);
        }
    }
}