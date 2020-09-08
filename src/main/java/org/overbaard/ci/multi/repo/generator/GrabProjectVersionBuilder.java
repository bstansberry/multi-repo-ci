package org.overbaard.ci.multi.repo.generator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class GrabProjectVersionBuilder {
    private String envVarName;


    public GrabProjectVersionBuilder setEnvVarName(String envVarName) {
        this.envVarName = envVarName;
        return this;
    }

    public Map<String, Object> build() {
        StringBuilder bash = new StringBuilder();
        // Do an initial run where we download everything from maven, which pollutes the output
        bash.append("mvn -B help:evaluate -Dexpression=project.version -pl .\n");
        bash.append("TMP=\"$(mvn -B help:evaluate -Dexpression=project.version -pl . | grep -v '^\\[')\"\n");
        bash.append("echo \"version: ${TMP}\"\n");
        if (envVarName != null) {
            bash.append("echo \"Saving version to env var: \\$" + envVarName + "\"\n");
            bash.append(String.format("echo \"::set-output name=%s::${TMP}\"\n", envVarName));
        }

        Map<String, Object> cmd = new LinkedHashMap<>();
        cmd.put("name", "Grab project version");
        cmd.put("id", "grab-version");
        cmd.put("run", bash.toString());
        return cmd;
    }
}
