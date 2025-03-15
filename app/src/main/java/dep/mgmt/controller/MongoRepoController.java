package dep.mgmt.controller;

import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.Dependency;
import dep.mgmt.model.DependencyResponse;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.server.Endpoints;
import dep.mgmt.service.GradleDependencyVersionService;
import dep.mgmt.service.GradlePluginVersionService;
import dep.mgmt.service.LatestVersionService;
import dep.mgmt.service.NpmDependencyVersionService;
import dep.mgmt.service.ProcessSummaryService;
import dep.mgmt.service.PythonPackageVersionService;
import dep.mgmt.util.ConvertUtils;
import dep.mgmt.util.ServerUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoRepoController {

  private final GradlePluginVersionService gradlePluginVersionService;
  private final GradleDependencyVersionService gradleDependencyVersionService;
  private final NpmDependencyVersionService npmDependencyVersionService;
  private final PythonPackageVersionService pythonPackageVersionService;
  private final LatestVersionService latestVersionService;
  private final ProcessSummaryService processSummaryService;

  public MongoRepoController() {
    this.gradlePluginVersionService = new GradlePluginVersionService();
    this.gradleDependencyVersionService = new GradleDependencyVersionService();
    this.npmDependencyVersionService = new NpmDependencyVersionService();
    this.pythonPackageVersionService = new PythonPackageVersionService();
    this.latestVersionService = new LatestVersionService();
    this.processSummaryService = new ProcessSummaryService();
  }

  public void handleRequest(
      final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {
    final String requestUri = ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());
    final HttpMethod requestMethod = fullHttpRequest.method();

    if (requestMethod.equals(HttpMethod.GET)) {
      switch (requestUri) {
        case Endpoints.MONGO_GRADLE_PLUGIN:
          ServerUtils.sendResponse(ctx, getGradlePlugins(), HttpResponseStatus.OK);
          break;
        case Endpoints.MONGO_GRADLE_DEPENDENCY:
          ServerUtils.sendResponse(ctx, getGradleDependencies(), HttpResponseStatus.OK);
          break;
        case Endpoints.MONGO_NPM_DEPENDENCY:
          ServerUtils.sendResponse(ctx, getNpmDependencies(), HttpResponseStatus.OK);
          break;
        case Endpoints.MONGO_PYTHON_PACKAGE:
          ServerUtils.sendResponse(ctx, getPythonPackages(), HttpResponseStatus.OK);
          break;
        case Endpoints.MONGO_LATEST_VERSION:
          ServerUtils.sendResponse(ctx, getLatestVersion(), HttpResponseStatus.OK);
          break;
        case Endpoints.MONGO_PROCESS_SUMMARY:
          final String updateType = ServerUtils.getQueryParam(requestUri, "updateType", "");
          final String updateDate = ServerUtils.getQueryParam(requestUri, "updateDate", "");
          final String pageNumber = ServerUtils.getQueryParam(requestUri, "pageNumber", "1");
          final String pageSize = ServerUtils.getQueryParam(requestUri, "pageSize", "100");

          if (validateProcessSummaryUpdateType(updateType)) {
            ServerUtils.sendResponse(
                ctx,
                getProcessSummary(updateType, updateDate, pageNumber, pageSize),
                HttpResponseStatus.OK);
          } else {
            ServerUtils.sendErrorResponse(ctx, "Invalid Update Type...", HttpResponseStatus.BAD_REQUEST);
          }
          break;
        case Endpoints.MONGO_REPO_UPDATE:
          updateDependenciesInMongo();
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.ACCEPTED);
          break;
        default:
          ServerUtils.sendErrorResponse(ctx, "MongoRepoController Get Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
          break;
      }
    } else if (requestMethod.equals(HttpMethod.POST)) {
      final Dependency dependencyRequest = ServerUtils.getRequestBody(fullHttpRequest, Dependency.class);
      if (dependencyRequest == null || CommonUtilities.isEmpty(dependencyRequest.getName()) || CommonUtilities.isEmpty(dependencyRequest.getVersion())) {
        ServerUtils.sendErrorResponse(ctx, "Missing Input...", HttpResponseStatus.BAD_REQUEST);
        return;
      }

      switch (requestUri) {
        case Endpoints.MONGO_GRADLE_PLUGIN:
          saveGradlePlugin(dependencyRequest);
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
          break;
        case Endpoints.MONGO_GRADLE_DEPENDENCY:
          saveGradleDependency(dependencyRequest);
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
          break;
        case Endpoints.MONGO_NPM_DEPENDENCY:
          saveNpmDependency(dependencyRequest);
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
          break;
        case Endpoints.MONGO_PYTHON_PACKAGE:
          savePythonPackage(dependencyRequest);
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
          break;
        default:
          ServerUtils.sendErrorResponse(ctx, "MongoRepoController Post Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
          break;
      }
    } else {
      ServerUtils.sendErrorResponse(
          ctx,
          "MongoRepoController Method Mapping Not Found...",
          HttpResponseStatus.METHOD_NOT_ALLOWED);
    }
  }

  private DependencyResponse getGradlePlugins() {
    final List<DependencyEntity> gradlePluginEntities =
        gradlePluginVersionService.getGradlePluginsMap().values().stream().toList();
    final List<Dependency> gradlePlugins =
        ConvertUtils.convertDependencyEntities(gradlePluginEntities);
    return new DependencyResponse(gradlePlugins);
  }

  private void saveGradlePlugin(final Dependency dependency) {
    DependencyEntity dependencyEntity = gradlePluginVersionService.getGradlePluginsMap().get(dependency.getName());
    if (dependencyEntity == null) {
      gradlePluginVersionService.insertGradlePlugin(dependency.getName(), dependency.getVersion());
    } else {
      dependencyEntity.setVersion(dependency.getVersion());
      dependencyEntity.setSkipVersion(dependency.getSkipVersion());
      gradlePluginVersionService.updateGradlePlugin(dependencyEntity);
    }
  }

  private DependencyResponse getGradleDependencies() {
    final List<DependencyEntity> gradleDependencyEntities =
        gradleDependencyVersionService.getGradleDependenciesMap().values().stream().toList();
    final List<Dependency> gradleDependencies =
        ConvertUtils.convertDependencyEntities(gradleDependencyEntities);
    return new DependencyResponse(gradleDependencies);
  }

  private void saveGradleDependency(final Dependency dependency) {
    DependencyEntity dependencyEntity = gradleDependencyVersionService.getGradleDependenciesMap().get(dependency.getName());
    if (dependencyEntity == null) {
      gradleDependencyVersionService.insertGradleDependency(dependency.getName(), dependency.getVersion());
    } else {
      dependencyEntity.setVersion(dependency.getVersion());
      dependencyEntity.setSkipVersion(dependency.getSkipVersion());
      gradleDependencyVersionService.updateGradleDependency(dependencyEntity);
    }
  }

  private DependencyResponse getNpmDependencies() {
    final List<DependencyEntity> npmDependencyEntities =
        npmDependencyVersionService.getNpmDependenciesMap().values().stream().toList();
    final List<Dependency> npmDependencies =
        ConvertUtils.convertDependencyEntities(npmDependencyEntities);
    return new DependencyResponse(npmDependencies);
  }

  private void saveNpmDependency(final Dependency dependency) {
    DependencyEntity dependencyEntity = npmDependencyVersionService.getNpmDependenciesMap().get(dependency.getName());
    if (dependencyEntity == null) {
      npmDependencyVersionService.insertNpmDependency(dependency.getName(), dependency.getVersion());
    } else {
      dependencyEntity.setVersion(dependency.getVersion());
      dependencyEntity.setSkipVersion(dependency.getSkipVersion());
      npmDependencyVersionService.updateNpmDependency(dependencyEntity);
    }
  }

  private DependencyResponse getPythonPackages() {
    final List<DependencyEntity> pythonPackageEntities =
        pythonPackageVersionService.getPythonPackagesMap().values().stream().toList();
    final List<Dependency> pythonPackages =
        ConvertUtils.convertDependencyEntities(pythonPackageEntities);
    return new DependencyResponse(pythonPackages);
  }

  private void savePythonPackage(final Dependency dependency) {
    DependencyEntity dependencyEntity = pythonPackageVersionService.getPythonPackagesMap().get(dependency.getName());
    if (dependencyEntity == null) {
      pythonPackageVersionService.insertPythonPackage(dependency.getName(), dependency.getVersion());
    } else {
      dependencyEntity.setVersion(dependency.getVersion());
      dependencyEntity.setSkipVersion(dependency.getSkipVersion());
      pythonPackageVersionService.updatePythonPackage(dependencyEntity);
    }
  }

  private AppDataLatestVersions getLatestVersion() {
    return latestVersionService.getLatestVersion();
  }

  private ProcessSummaries getProcessSummary(
      final String updateType,
      final String updateDate,
      final String pageNumber,
      final String pageSize) {
    return processSummaryService.getProcessSummaries(
        updateType,
        getLocalDateNoEx(updateDate),
        CommonUtilities.parseIntNoEx(pageNumber),
        CommonUtilities.parseIntNoEx(pageSize));
  }

  private boolean validateProcessSummaryUpdateType(final String updateType) {
    if (CommonUtilities.isEmpty(updateType)) {
      return true;
    }

    return List.of(
            RequestParams.UpdateType.ALL.name(),
            RequestParams.UpdateType.GRADLE.name(),
            RequestParams.UpdateType.NPM.name(),
            RequestParams.UpdateType.PYTHON.name())
        .contains(updateType);
  }

  private LocalDate getLocalDateNoEx(final String localDate) {
    try {
      return LocalDate.parse(localDate);
    } catch (DateTimeParseException ex) {
      return null;
    }
  }

  private void updateDependenciesInMongo() {
    CompletableFuture.runAsync(gradleDependencyVersionService::updateGradleDependencies);
    CompletableFuture.runAsync(gradlePluginVersionService::updateGradlePlugins);
    CompletableFuture.runAsync(npmDependencyVersionService::updateNpmDependencies);
    CompletableFuture.runAsync(pythonPackageVersionService::updatePythonPackages);
  }
}
