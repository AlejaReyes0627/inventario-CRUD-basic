package com.politecnico.inventario.grpc;

import com.politecnico.inventario.exception.ResourceNotFoundException;
import com.politecnico.inventario.model.entity.Producto;
import com.politecnico.inventario.service.ProductoService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class ProductoGrpcService extends ProductoServiceGrpc.ProductoServiceImplBase {

    private final ProductoService productoService;

    public ProductoGrpcService(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Override
    public void listarTodos(Empty request, StreamObserver<ListaProductos> responseObserver) {
        try {
            List<Producto> productos = productoService.obtenerTodos();

            ListaProductos.Builder builder = ListaProductos.newBuilder();
            for (Producto p : productos) {
                builder.addProductos(convertirAProto(p));
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error al listar productos: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void obtenerPorId(IdRequest request, StreamObserver<com.politecnico.inventario.grpc.Producto> responseObserver) {
        try {
            Producto producto = productoService.obtenerPorId(request.getId());
            responseObserver.onNext(convertirAProto(producto));
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void crearProducto(CrearProductoRequest request, StreamObserver<com.politecnico.inventario.grpc.Producto> responseObserver) {
        try {
            Producto nuevo = new Producto();
            nuevo.setNombre(request.getNombre());
            nuevo.setDescripcion(request.getDescripcion());
            nuevo.setPrecio(request.getPrecio());

            Producto guardado = productoService.guardar(nuevo);
            responseObserver.onNext(convertirAProto(guardado));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error al crear producto: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void actualizarProducto(ActualizarProductoRequest request, StreamObserver<com.politecnico.inventario.grpc.Producto> responseObserver) {
        try {
            Producto detalles = new Producto();
            detalles.setNombre(request.getNombre());
            detalles.setDescripcion(request.getDescripcion());
            detalles.setPrecio(request.getPrecio());

            Producto actualizado = productoService.actualizar(request.getId(), detalles);
            responseObserver.onNext(convertirAProto(actualizado));
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error al actualizar: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void eliminarProducto(IdRequest request, StreamObserver<EliminarResponse> responseObserver) {
        try {
            productoService.eliminar(request.getId());

            EliminarResponse response = EliminarResponse.newBuilder()
                    .setEliminado(true)
                    .setMensaje("Producto eliminado correctamente")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error al eliminar: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private com.politecnico.inventario.grpc.Producto convertirAProto(Producto entity) {
        return com.politecnico.inventario.grpc.Producto.newBuilder()
                .setId(entity.getId())
                .setNombre(entity.getNombre() != null ? entity.getNombre() : "")
                .setDescripcion(entity.getDescripcion() != null ? entity.getDescripcion() : "")
                .setPrecio(entity.getPrecio() != null ? entity.getPrecio() : 0.0)
                .build();
    }
}