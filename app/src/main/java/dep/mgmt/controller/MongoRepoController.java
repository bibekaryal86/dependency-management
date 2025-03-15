package dep.mgmt.controller;

import dep.mgmt.model.Dependency;
import dep.mgmt.model.DependencyResponse;
import dep.mgmt.model.entity.DependencyEntity;
import dep.mgmt.server.Endpoints;
import dep.mgmt.service.GradleDependencyVersionService;
import dep.mgmt.service.GradlePluginVersionService;
import dep.mgmt.service.NpmDependencyVersionService;
import dep.mgmt.service.PythonPackageVersionService;
import dep.mgmt.util.ServerUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;

public class MongoRepoController {

    private final GradlePluginVersionService gradlePluginVersionService;
    private final GradleDependencyVersionService gradleDependencyVersionService;
    private final NpmDependencyVersionService npmDependencyVersionService;
    private final PythonPackageVersionService pythonPackageVersionService;

    public MongoRepoController() {
        this.gradlePluginVersionService = new GradlePluginVersionService();
        this.gradleDependencyVersionService = new GradleDependencyVersionService();
        this.npmDependencyVersionService = new NpmDependencyVersionService();
        this.pythonPackageVersionService = new PythonPackageVersionService();
    }

    private static final String SAVE_SUCCESSFUL = "{\"save\": \"successful\"}";

    public void handleRequest(final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {
        final String requestUri = ServerUtils.getRequestUriLessParams(fullHttpRequest.uri());
        final HttpMethod requestMethod = fullHttpRequest.method();

        switch (requestUri) {
            case Endpoints.MONGO_GRADLE_PLUGIN:
                System.out.println("gradle plugin");
                if (requestMethod.equals(HttpMethod.GET)) {
                    final DependencyResponse dependencyResponse = getGradlePlugins();
                    ServerUtils.sendResponse(ctx, dependencyResponse, HttpResponseStatus.OK);
                } else if (requestMethod.equals(HttpMethod.POST)) {

                } else {
                    ServerUtils.sendErrorResponse(ctx, "MongoRepoController Method Mapping Not Found.", HttpResponseStatus.METHOD_NOT_ALLOWED);
                }
            case Endpoints.MONGO_GRADLE_DEPENDENCY:
                System.out.println("gradle dependency");
                if (requestMethod.equals(HttpMethod.GET)) {
                    final DependencyResponse dependencyResponse = getGradleDependencies();
                    ServerUtils.sendResponse(ctx, dependencyResponse, HttpResponseStatus.OK);
                } else if (requestMethod.equals(HttpMethod.POST)) {

                } else {
                    ServerUtils.sendErrorResponse(ctx, "MongoRepoController Method Mapping Not Found..", HttpResponseStatus.METHOD_NOT_ALLOWED);
                }
            case Endpoints.MONGO_NPM_DEPENDENCY:
                System.out.println("npm dependency");
                if (requestMethod.equals(HttpMethod.GET)) {
                    final DependencyResponse dependencyResponse = getNpmDependencies();
                    ServerUtils.sendResponse(ctx, dependencyResponse, HttpResponseStatus.OK);
                } else if (requestMethod.equals(HttpMethod.POST)) {

                } else {
                    ServerUtils.sendErrorResponse(ctx, "MongoRepoController Method Mapping Not Found...", HttpResponseStatus.METHOD_NOT_ALLOWED);
                }
            case Endpoints.MONGO_PYTHON_PACKAGE:
                System.out.println("python package");
                if (requestMethod.equals(HttpMethod.GET)) {
                    final DependencyResponse dependencyResponse = getPythonPackages();
                    ServerUtils.sendResponse(ctx, dependencyResponse, HttpResponseStatus.OK);
                } else if (requestMethod.equals(HttpMethod.POST)) {

                } else {
                    ServerUtils.sendErrorResponse(ctx, "MongoRepoController Method Mapping Not Found....", HttpResponseStatus.METHOD_NOT_ALLOWED);
                }
            case Endpoints.MONGO_REPO_UPDATE:
                System.out.println("mongo repo update");
            case Endpoints.MONGO_LATEST_VERSION:
                System.out.println("latest version");
            case Endpoints.MONGO_PROCESS_SUMMARY:
                System.out.println("process summary");
            default:
                ServerUtils.sendErrorResponse(ctx, "MongoRepoController Mapping Not Found...", HttpResponseStatus.NOT_FOUND);
        }
    }

    private DependencyResponse getGradlePlugins() {
        final List<DependencyEntity> gradlePluginEntities = this.gradlePluginVersionService.getGradlePluginsMap().values().stream().toList();
        final List<Dependency> gradlePlugins = ServerUtils.convertDependencyEntities(gradlePluginEntities);
        return new DependencyResponse(gradlePlugins);
    }

    private DependencyResponse getGradleDependencies() {
        final List<DependencyEntity> gradleDependencyEntities = this.gradleDependencyVersionService.getGradleDependenciesMap().values().stream().toList();
        final List<Dependency> gradleDependencies = ServerUtils.convertDependencyEntities(gradleDependencyEntities);
        return new DependencyResponse(gradleDependencies);
    }

    private DependencyResponse getNpmDependencies() {
        final List<DependencyEntity> npmDependencyEntities = this.npmDependencyVersionService.getNpmDependenciesMap().values().stream().toList();
        final List<Dependency> npmDependencies = ServerUtils.convertDependencyEntities(npmDependencyEntities);
        return new DependencyResponse(npmDependencies);
    }

    private DependencyResponse getPythonPackages() {
        final List<DependencyEntity> pythonPackageEntities = this.pythonPackageVersionService.getPythonPackagesMap().values().stream().toList();
        final List<Dependency> pythonPackages = ServerUtils.convertDependencyEntities(pythonPackageEntities);
        return new DependencyResponse(pythonPackages);
    }
}

