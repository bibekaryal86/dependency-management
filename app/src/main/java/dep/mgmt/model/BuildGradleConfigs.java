package dep.mgmt.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

public class BuildGradleConfigs implements Serializable {
  private final Path buildGradlePath;
  private final List<String> originals;
  private final GradleConfigBlock plugins;
  private final List<GradleConfigBlock> dependencies;

  public BuildGradleConfigs(
      final Path buildGradlePath,
      final List<String> originals,
      final GradleConfigBlock plugins,
      final List<GradleConfigBlock> dependencies) {
    this.buildGradlePath = buildGradlePath;
    this.originals = originals;
    this.plugins = plugins;
    this.dependencies = dependencies;
  }

  public Path getBuildGradlePath() {
    return buildGradlePath;
  }

  public List<String> getOriginals() {
    return originals;
  }

  public GradleConfigBlock getPlugins() {
    return plugins;
  }

  public List<GradleConfigBlock> getDependencies() {
    return dependencies;
  }

  @Override
  public String toString() {
    return "BuildGradleConfigs{"
        + "buildGradlePath="
        + buildGradlePath
        + ", originals="
        + originals
        + ", plugins="
        + plugins
        + ", dependencies="
        + dependencies
        + '}';
  }

  public static class GradleConfigBlock implements Serializable {
    private final List<GradleDefinition> definitions;
    private final List<GradleDependencyPlugin> dependencies;
    private final List<GradleDependencyPlugin> plugins;

    public GradleConfigBlock(
        final List<GradleDefinition> definitions,
        final List<GradleDependencyPlugin> dependencies,
        final List<GradleDependencyPlugin> plugins) {
      this.definitions = definitions;
      this.dependencies = dependencies;
      this.plugins = plugins;
    }

    public List<GradleDefinition> getDefinitions() {
      return definitions;
    }

    public List<GradleDependencyPlugin> getDependencies() {
      return dependencies;
    }

    public List<GradleDependencyPlugin> getPlugins() {
      return plugins;
    }

    @Override
    public String toString() {
      return "GradleConfigBlock{"
          + "definitions="
          + definitions
          + ", dependencies="
          + dependencies
          + ", plugins="
          + plugins
          + '}';
    }

    public static class GradleDefinition implements Serializable {
      private final String original;
      private final String name;
      private final String value;

      public GradleDefinition(final String original, final String name, final String value) {
        this.original = original;
        this.name = name;
        this.value = value;
      }

      public String getOriginal() {
        return original;
      }

      public String getName() {
        return name;
      }

      public String getValue() {
        return value;
      }

      @Override
      public String toString() {
        return "GradleDefinition{"
            + "original='"
            + original
            + '\''
            + ", name='"
            + name
            + '\''
            + ", value='"
            + value
            + '\''
            + '}';
      }
    }

    public static class GradleDependencyPlugin implements Serializable {
      private final String original;
      private final String group;
      private final String artifact;
      private final String version;
      private final Boolean skipVersion;

      public GradleDependencyPlugin(
          final String original,
          final String group,
          final String artifact,
          final String version,
          final Boolean skipVersion) {
        this.original = original;
        this.group = group;
        this.artifact = artifact;
        this.version = version;
        this.skipVersion = skipVersion;
      }

      public GradleDependencyPlugin(
          final String original,
          final String group,
          final String version,
          final Boolean skipVersion) {
        this.original = original;
        this.group = group;
        this.artifact = null;
        this.version = version;
        this.skipVersion = skipVersion;
      }

      public String getOriginal() {
        return original;
      }

      public String getGroup() {
        return group;
      }

      public String getArtifact() {
        return artifact;
      }

      public String getVersion() {
        return version;
      }

      public Boolean getSkipVersion() {
        return skipVersion;
      }

      @Override
      public String toString() {
        return "GradleDependencyPlugin{"
            + "original='"
            + original
            + '\''
            + ", group='"
            + group
            + '\''
            + ", artifact='"
            + artifact
            + '\''
            + ", version='"
            + version
            + '\''
            + ", skipVersion="
            + skipVersion
            + '}';
      }
    }
  }
}
