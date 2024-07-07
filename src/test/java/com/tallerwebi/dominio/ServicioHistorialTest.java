package com.tallerwebi.dominio;

import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
public class ServicioHistorialTest {
}
