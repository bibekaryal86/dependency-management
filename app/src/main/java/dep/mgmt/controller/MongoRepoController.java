package dep.mgmt.controller;

import dep.mgmt.model.AppDataLatestVersions;
import dep.mgmt.model.Dependencies;
import dep.mgmt.model.ExcludedRepos;
import dep.mgmt.model.ProcessSummaries;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.model.entity.ExcludedRepoEntity;
import dep.mgmt.model.enums.RequestParams;
import dep.mgmt.server.Endpoints;
import dep.mgmt.service.ExcludedRepoService;
import dep.mgmt.service.GradleDependencyVersionService;
import dep.mgmt.service.GradlePluginVersionService;
import dep.mgmt.service.LatestVersionService;
import dep.mgmt.service.NodeDependencyVersionService;
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
  private final NodeDependencyVersionService nodeDependencyVersionService;
  private final PythonPackageVersionService pythonPackageVersionService;
  private final LatestVersionService latestVersionService;
  private final ProcessSummaryService processSummaryService;
  private final ExcludedRepoService excludedRepoService;

  public MongoRepoController() {
    this.gradlePluginVersionService = new GradlePluginVersionService();
    this.gradleDependencyVersionService = new GradleDependencyVersionService();
    this.nodeDependencyVersionService = new NodeDependencyVersionService();
    this.pythonPackageVersionService = new PythonPackageVersionService();
    this.latestVersionService = new LatestVersionService();
    this.processSummaryService = new ProcessSummaryService();
    this.excludedRepoService = new ExcludedRepoService();
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
        case Endpoints.MONGO_NODE_DEPENDENCY:
          ServerUtils.sendResponse(ctx, getNodeDependencies(), HttpResponseStatus.OK);
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
            ServerUtils.sendErrorResponse(
                ctx, "Invalid Update Type...", HttpResponseStatus.BAD_REQUEST);
          }
          break;
        case Endpoints.MONGO_REPO_UPDATE:
          updateDependenciesInMongo();
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.ACCEPTED);
          break;
        case Endpoints.MONGO_EXCLUDED_REPO:
          ServerUtils.sendResponse(ctx, getExcludedRepos(), HttpResponseStatus.OK);
          break;
        default:
          ServerUtils.sendErrorResponse(
              ctx, "MongoRepoController Get Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
          break;
      }
    } else if (requestMethod.equals(HttpMethod.POST)) {
      final Dependencies.Dependency dependencyRequest =
          ServerUtils.getRequestBody(fullHttpRequest, Dependencies.Dependency.class);
      if (dependencyRequest == null
          || CommonUtilities.isEmpty(dependencyRequest.getName())
          || (CommonUtilities.isEmpty(dependencyRequest.getVersion())
              && !requestUri.equals(Endpoints.MONGO_EXCLUDED_REPO))) {
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
        case Endpoints.MONGO_NODE_DEPENDENCY:
          saveNpmDependency(dependencyRequest);
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
          break;
        case Endpoints.MONGO_PYTHON_PACKAGE:
          savePythonPackage(dependencyRequest);
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
          break;
        case Endpoints.MONGO_EXCLUDED_REPO:
          saveExcludedRepo(dependencyRequest.getName());
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
          break;
        default:
          ServerUtils.sendErrorResponse(
              ctx, "MongoRepoController Post Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
          break;
      }
    } else if (requestMethod.equals(HttpMethod.DELETE)) {
      switch (requestUri) {
        case Endpoints.MONGO_EXCLUDED_REPO:
          final String repoName = ServerUtils.getQueryParam(fullHttpRequest.uri(), "repoName", "");
          final boolean isDeleteAll =
              Boolean.parseBoolean(ServerUtils.getQueryParam(fullHttpRequest.uri(), "deleteAll", ""));
          deletedExcludedRepo(repoName, isDeleteAll);
          ServerUtils.sendErrorResponse(ctx, "", HttpResponseStatus.NO_CONTENT);
          break;
        default:
          ServerUtils.sendErrorResponse(
              ctx, "MongoRepoController Delete Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
          break;
      }
    } else {
      ServerUtils.sendErrorResponse(
          ctx,
          "MongoRepoController Method Mapping Not Found...",
          HttpResponseStatus.METHOD_NOT_ALLOWED);
    }
  }

  private Dependencies getGradlePlugins() {
    final List<DependencyEntity> gradlePluginEntities =
        gradlePluginVersionService.getGradlePluginsMap().values().stream().toList();
    final List<Dependencies.Dependency> gradlePlugins =
        ConvertUtils.convertDependencyEntities(gradlePluginEntities);
    return new Dependencies(gradlePlugins);
  }

  private void saveGradlePlugin(final Dependencies.Dependency dependency) {
    DependencyEntity dependencyEntity =
        gradlePluginVersionService.getGradlePluginsMap().get(dependency.getName());
    if (dependencyEntity == null) {
      gradlePluginVersionService.insertGradlePlugin(dependency.getName(), dependency.getVersion());
    } else {
      dependencyEntity.setVersion(dependency.getVersion());
      dependencyEntity.setSkipVersion(dependency.getSkipVersion());
      gradlePluginVersionService.updateGradlePlugin(dependencyEntity);
    }
  }

  private Dependencies getGradleDependencies() {
    final List<DependencyEntity> gradleDependencyEntities =
        gradleDependencyVersionService.getGradleDependenciesMap().values().stream().toList();
    final List<Dependencies.Dependency> gradleDependencies =
        ConvertUtils.convertDependencyEntities(gradleDependencyEntities);
    return new Dependencies(gradleDependencies);
  }

  private void saveGradleDependency(final Dependencies.Dependency dependency) {
    DependencyEntity dependencyEntity =
        gradleDependencyVersionService.getGradleDependenciesMap().get(dependency.getName());
    if (dependencyEntity == null) {
      gradleDependencyVersionService.insertGradleDependency(
          dependency.getName(), dependency.getVersion());
    } else {
      dependencyEntity.setVersion(dependency.getVersion());
      dependencyEntity.setSkipVersion(dependency.getSkipVersion());
      gradleDependencyVersionService.updateGradleDependency(dependencyEntity);
    }
  }

  private Dependencies getNodeDependencies() {
    final List<DependencyEntity> nodeDependencyEntities =
        nodeDependencyVersionService.getNodeDependenciesMap().values().stream().toList();
    final List<Dependencies.Dependency> nodeDependencies =
        ConvertUtils.convertDependencyEntities(nodeDependencyEntities);
    return new Dependencies(nodeDependencies);
  }

  private void saveNpmDependency(final Dependencies.Dependency dependency) {
    DependencyEntity dependencyEntity =
        nodeDependencyVersionService.getNodeDependenciesMap().get(dependency.getName());
    if (dependencyEntity == null) {
      nodeDependencyVersionService.insertNodeDependency(
          dependency.getName(), dependency.getVersion());
    } else {
      dependencyEntity.setVersion(dependency.getVersion());
      dependencyEntity.setSkipVersion(dependency.getSkipVersion());
      nodeDependencyVersionService.updateNodeDependency(dependencyEntity);
    }
  }

  private Dependencies getPythonPackages() {
    final List<DependencyEntity> pythonPackageEntities =
        pythonPackageVersionService.getPythonPackagesMap().values().stream().toList();
    final List<Dependencies.Dependency> pythonPackages =
        ConvertUtils.convertDependencyEntities(pythonPackageEntities);
    return new Dependencies(pythonPackages);
  }

  private void savePythonPackage(final Dependencies.Dependency dependency) {
    DependencyEntity dependencyEntity =
        pythonPackageVersionService.getPythonPackagesMap().get(dependency.getName());
    if (dependencyEntity == null) {
      pythonPackageVersionService.insertPythonPackage(
          dependency.getName(), dependency.getVersion());
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
            RequestParams.UpdateType.NODE.name(),
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
    CompletableFuture.runAsync(nodeDependencyVersionService::updateNodeDependencies);
    CompletableFuture.runAsync(pythonPackageVersionService::updatePythonPackages);
  }

  private ExcludedRepos getExcludedRepos() {
    final List<ExcludedRepoEntity> excludedRepoEntities =
        excludedRepoService.getExcludedReposMap().values().stream().toList();
    final List<ExcludedRepos.ExcludedRepo> excludedRepos =
        ConvertUtils.convertExcludedRepoEntities(excludedRepoEntities);
    return new ExcludedRepos(excludedRepos);
  }

  private void saveExcludedRepo(final String name) {
    excludedRepoService.insertExcludedRepo(name);
  }

  private void deletedExcludedRepo(final String name, final boolean isDeleteAll) {
    if (CommonUtilities.isEmpty(name) && isDeleteAll) {
      excludedRepoService.deleteAllExcludedRepos();
    } else {
      excludedRepoService.deleteExcludedRepo(name);
    }
  }
}
