package com.politecnico.inventario.controller;

import com.politecnico.inventario.model.entity.Producto;
import com.politecnico.inventario.service.ProductoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductoGraphQLController {

    private final ProductoService productoService;

    public ProductoGraphQLController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @QueryMapping
    public List<Producto> listarTodos() {
        return productoService.obtenerTodos();
    }

    @QueryMapping
    public Producto obtenerPorId(@Argument Long id) {
        return productoService.obtenerPorId(id);
    }

    @MutationMapping
    public Producto crearProducto(@Argument ProductoInput producto) {
        Producto nuevo = new Producto();
        nuevo.setNombre(producto.nombre());
        nuevo.setDescripcion(producto.descripcion());
        nuevo.setPrecio(producto.precio());
        return productoService.guardar(nuevo);
    }

    @MutationMapping
    public Producto actualizarProducto(@Argument Long id, @Argument ProductoInput producto) {
        Producto prodActualizar = new Producto();
        prodActualizar.setNombre(producto.nombre());
        prodActualizar.setDescripcion(producto.descripcion());
        prodActualizar.setPrecio(producto.precio());
        return productoService.actualizar(id, prodActualizar);
    }

    @MutationMapping
    public Boolean eliminarProducto(@Argument Long id) {
        productoService.eliminar(id);
        return true;
    }

    // Record auxiliar para mapear el DTO de entrada GraphQL
    public record ProductoInput(String nombre, String descripcion, Double precio) {}
}