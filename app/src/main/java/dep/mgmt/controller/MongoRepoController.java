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

  private static final String SAVE_SUCCESSFUL = "{\"save\": \"successful\"}";

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
          System.out.println("process summary");
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
        default:
          ServerUtils.sendErrorResponse(
              ctx, "MongoRepoController Get Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
          break;
      }
    } else if (requestMethod.equals(HttpMethod.POST)) {
      switch (requestUri) {
        case Endpoints.MONGO_GRADLE_PLUGIN:
          System.out.println("gradle plugin");
          break;
        case Endpoints.MONGO_GRADLE_DEPENDENCY:
          System.out.println("gradle dependency");
          break;
        case Endpoints.MONGO_NPM_DEPENDENCY:
          System.out.println("npm dependency");
          break;
        case Endpoints.MONGO_PYTHON_PACKAGE:
          System.out.println("python package");
          break;
        case Endpoints.MONGO_REPO_UPDATE:
          System.out.println("mongo repo update");
          break;
        default:
          ServerUtils.sendErrorResponse(
              ctx, "MongoRepoController Post Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
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
        this.gradlePluginVersionService.getGradlePluginsMap().values().stream().toList();
    final List<Dependency> gradlePlugins =
        ConvertUtils.convertDependencyEntities(gradlePluginEntities);
    return new DependencyResponse(gradlePlugins);
  }

  private DependencyResponse getGradleDependencies() {
    final List<DependencyEntity> gradleDependencyEntities =
        this.gradleDependencyVersionService.getGradleDependenciesMap().values().stream().toList();
    final List<Dependency> gradleDependencies =
        ConvertUtils.convertDependencyEntities(gradleDependencyEntities);
    return new DependencyResponse(gradleDependencies);
  }

  private DependencyResponse getNpmDependencies() {
    final List<DependencyEntity> npmDependencyEntities =
        this.npmDependencyVersionService.getNpmDependenciesMap().values().stream().toList();
    final List<Dependency> npmDependencies =
        ConvertUtils.convertDependencyEntities(npmDependencyEntities);
    return new DependencyResponse(npmDependencies);
  }

  private DependencyResponse getPythonPackages() {
    final List<DependencyEntity> pythonPackageEntities =
        this.pythonPackageVersionService.getPythonPackagesMap().values().stream().toList();
    final List<Dependency> pythonPackages =
        ConvertUtils.convertDependencyEntities(pythonPackageEntities);
    return new DependencyResponse(pythonPackages);
  }

  private AppDataLatestVersions getLatestVersion() {
    return this.latestVersionService.getLatestVersion();
  }

  private ProcessSummaries getProcessSummary(
      final String updateType,
      final String updateDate,
      final String pageNumber,
      final String pageSize) {
    return this.processSummaryService.getProcessSummaries(
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
}
