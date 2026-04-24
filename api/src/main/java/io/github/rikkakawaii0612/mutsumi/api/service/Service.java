package io.github.rikkakawaii0612.mutsumi.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.rikkakawaii0612.mutsumi.api.ServiceModule;
import io.github.rikkakawaii0612.mutsumi.api.service.data.ObjectData;
import org.pf4j.ExtensionPoint;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Service implements ExtensionPoint {
    private ServiceModule module;

    public final void initialize(ServiceModule module) {
        this.module = module;
        this.load();
    }

    protected ServiceModule getModule() {
        return this.module;
    }

    /**
     * <p>服务的调用方法, 传入请求参数以返回服务提供的数据.
     * 单服务只能实现一个方法, 但是可以通过传入不同的参数来指定不同的方法.
     *
     * <p>返回的数据结构类型可能由服务本身提供, 这些数据结构的类<b>不应直接引用</b>,
     * 而是应当再提供一个处理这些数据的服务.
     *
     * @param request 请求参数
     * @return 服务提供的数据
     */
    public abstract ObjectData call(ServiceRequest request);

    public ObjectData call() {
        return this.call(ServiceRequest.builder().build());
    }

    public ObjectData call(String header) {
        return this.call(ServiceRequest.builder().header("value", header).build());
    }

    public ObjectData call(ObjectData data) {
        return this.call(ServiceRequest.builder().data("value", data).build());
    }

    public ObjectData call(String header, ObjectData data) {
        return this.call(ServiceRequest.builder().header("value", header).data("value", data).build());
    }

    public ObjectData call(ObjectData data, String header) {
        return this.call(header, data);
    }

    /**
     * 异步调用多次服务, 在全部完成后返回所有结果.
     *
     * @param requests 请求参数的集合
     * @return 服务提供的数据集合
     */
    public List<ObjectData> callAsync(Collection<ServiceRequest> requests) {
        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            List<CompletableFuture<ObjectData>> futures = requests.stream()
                    .map(request -> CompletableFuture.supplyAsync(() -> this.call(request), executor))
                    .toList();

            return futures.stream().map(CompletableFuture::join).toList();
        }
    }

    public abstract void load();

    public abstract void unload();

    public abstract String getId();
}
