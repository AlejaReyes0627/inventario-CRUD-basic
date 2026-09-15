package com.politecnico.inventario.service.impl;

import com.politecnico.inventario.exception.ResourceNotFoundException;
import com.politecnico.inventario.model.entity.Producto;
import com.politecnico.inventario.repository.ProductoRepository;
import com.politecnico.inventario.service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
    }

    @Override
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizar(Long id, Producto productoDetalles) {
        Producto productoExistente = obtenerPorId(id);
        
        productoExistente.setNombre(productoDetalles.getNombre());
        productoExistente.setDescripcion(productoDetalles.getDescripcion());
        productoExistente.setPrecio(productoDetalles.getPrecio());

        return productoRepository.save(productoExistente);
    }

    @Override
    public void eliminar(Long id) {
        Producto productoExistente = obtenerPorId(id);
        productoRepository.delete(productoExistente);
    }
}