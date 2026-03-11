package moe.nea.libautoupdate.api;

import moe.nea.libautoupdate.PotentialUpdate;
import moe.nea.libautoupdate.targets.UpdateAction;
import moe.nea.libautoupdate.targets.DeleteAndSaveInSameFolderUpdateTarget;
import moe.nea.libautoupdate.targets.ReplaceJarUpdateTarget;
import moe.nea.libautoupdate.util.FileUtils;

import java.io.File;
import java.util.List;

public interface UpdateTarget {
    List<UpdateAction> generateUpdateActions(PotentialUpdate update);

    static UpdateTarget replaceJar(Class<?> containedClass) {
        File file = FileUtils.getJarFile(containedClass);
        return new ReplaceJarUpdateTarget(file);
    }

    static UpdateTarget deleteAndSaveInTheSameFolder(
            Class<?> containedClass
    ) {
        File file = FileUtils.getJarFile(containedClass);
        return new DeleteAndSaveInSameFolderUpdateTarget(file);
    }
}