package com.tallerwebi.dominio.servicio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("MercadoPagoService")
@Transactional
public class MercadoPagoServiceImpl implements MercadoPagoServicio{
    @Override
    public String comprar(Double precio) {
        return null;
    }
}
