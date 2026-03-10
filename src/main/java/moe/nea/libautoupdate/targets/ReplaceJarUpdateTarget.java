package moe.nea.libautoupdate.targets;

import moe.nea.libautoupdate.PotentialUpdate;
import moe.nea.libautoupdate.actions.UpdateAction;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ReplaceJarUpdateTarget implements UpdateTarget {
    File file;

    public ReplaceJarUpdateTarget(File file) {
        this.file = file;
    }

    @Override
    public List<UpdateAction> generateUpdateActions(PotentialUpdate update) {
        return Arrays.asList(
                new UpdateAction.DeleteFile(file),
                new UpdateAction.MoveDownloadedFile(update.getUpdateJarStorage(), file)
        );
    }
}