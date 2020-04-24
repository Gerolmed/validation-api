package net.endrealm.validate.factories;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationSettings {

    private final List<String> paths;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<String> paths = new ArrayList<>();

        private Builder() { }

        public Builder addPaths(String... paths) {
            if(paths == null)
                return this;

            Collections.addAll(this.paths, paths);
            return this;
        }

        public ValidationSettings build() {
            return new ValidationSettings(paths);
        }
    }
}
