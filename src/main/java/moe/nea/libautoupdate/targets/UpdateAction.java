package moe.nea.libautoupdate.actions;

import java.io.File;
import java.util.List;
import java.util.Objects;

public sealed interface UpdateAction
        permits UpdateAction.DeleteFile, UpdateAction.MoveFile {

    void encode(List<String> arguments);

    record DeleteFile(File target) implements UpdateAction {
        public DeleteFile {
            Objects.requireNonNull(target, "target");
        }

        @Override
        public void encode(List<String> arguments) {
            arguments.add("delete");
            arguments.add(target.getAbsolutePath());
        }
    }

    record MoveFile(File source, File destination) implements UpdateAction {
        public MoveFile {
            Objects.requireNonNull(source, "source");
            Objects.requireNonNull(destination, "destination");
        }

        @Override
        public void encode(List<String> arguments) {
            arguments.add("move");
            arguments.add(source.getAbsolutePath());
            arguments.add(destination.getAbsolutePath());
        }
    }
}